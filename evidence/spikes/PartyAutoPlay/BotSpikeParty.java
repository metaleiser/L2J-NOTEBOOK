/*
 * PARTY + AUTOPLAY SPIKE (BotSpikeParty) — native Party + native AutoPlay + AssistLeader
 * on REAL server-side Players with GameClient == null.
 *
 * TRANSIENT disposable harness (LIVE RUNTIME only). Disposable topology:
 *   leader = charId 268483035 (ADMIN, clientless)
 *   bots   = charId 268483100..268483107 (SQL clones of ADMIN row, clientless)
 *   Party A = 1 leader + 8 bots = 9 native members
 *
 * Native gates (game/config/Custom/AutoPlay.ini):
 *   EnableAutoPlay = True  -> AutoPlayConfig.ENABLE_AUTO_PLAY
 *   AssistLeader = True    -> AutoPlayConfig.ENABLE_AUTO_ASSIST
 *
 * No core modification, no .play, no custom combat AI, no CommandChannel.
 * Lifecycle mirrors D-0001 (clientless Player.load + setOnlineStatus(true,false)
 * + setOfflinePlay(true) + spawnMe + AutoPlayTaskManager.startAutoPlay) composed
 * with the C3 proven native Party join (bot.joinParty(party); leader.setParty(party)).
 *
 * DB SAFETY: players are removed from PlayerAutoSaveTaskManager right after load
 * (CHAR_DATA_STORE_INTERVAL default 15 min; window ~90s -> no autosave writes);
 * offlinePlay cleared before stopAutoPlay (avoids OfflinePlayTable DELETE);
 * Party is in-memory only (no party DB tables). Provisioned bot rows are deleted
 * by the operator in the post-flight rollback.
 *
 * Timeline captured to log with tag [BotSpikeParty]:
 *   T0 party formed | T1 bots clientless | T2 autoplay active
 *   T3 leader target acquired | T4 first assist | T5 first bot attack
 *   T6 first HP change | T7 continued combat | T8 target transition | T9 teardown
 *
 * Schedule: +60s start | observe 75s | teardown +77s | report +82s | shutdown +20s
 *           watchdog +300s (forced teardown + shutdown)
 */
package custom.BotSpikeParty;

import java.io.FileInputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.Shutdown;
import org.l2jmobius.gameserver.ai.Intention;
import org.l2jmobius.gameserver.config.custom.AutoPlayConfig;
import org.l2jmobius.gameserver.entity.World;
import org.l2jmobius.gameserver.entity.WorldObject;
import org.l2jmobius.gameserver.entity.actor.Creature;
import org.l2jmobius.gameserver.entity.actor.Npc;
import org.l2jmobius.gameserver.entity.actor.Player;
import org.l2jmobius.gameserver.entity.groups.Party;
import org.l2jmobius.gameserver.entity.groups.PartyDistributionType;
import org.l2jmobius.gameserver.entity.groups.PartyMessageType;
import org.l2jmobius.gameserver.mechanics.script.Script;
import org.l2jmobius.gameserver.taskmanagers.AutoPlayTaskManager;
import org.l2jmobius.gameserver.taskmanagers.PlayerAutoSaveTaskManager;
public class BotSpikeParty extends Script
{
	private static final Logger LOGGER = Logger.getLogger(BotSpikeParty.class.getName());
	private static final String TAG = "[BotSpikeParty]";

	private static final int LEADER_ID = 268483035;
	private static final int[] BOT_IDS =
	{
		268483100, 268483101, 268483102, 268483103,
		268483104, 268483105, 268483106, 268483107
	};
	private static final String[] BOT_NAMES =
	{
		"SPIKEBOT01", "SPIKEBOT02", "SPIKEBOT03", "SPIKEBOT04",
		"SPIKEBOT05", "SPIKEBOT06", "SPIKEBOT07", "SPIKEBOT08"
	};
	private static final int[] TARGET_NPC_IDS =
	{
		20432,
		20481,
		20544
	};
	private static final int ANCHOR_X = -81411;
	private static final int ANCHOR_Y = 246563;

	private static final long START_DELAY_MS = 60_000L;
	private static final long OBSERVE_MS = 75_000L;
	private static final long POLL_MS = 1000L;
	private static final long TEARDOWN_AT_MS = 77_000L;
	private static final long REPORT_AT_MS = 82_000L;
	private static final long WATCHDOG_AT_MS = 300_000L;
	private static final long SHUTDOWN_SECONDS = 20L;

	private volatile Party _party;
	private volatile Player _leader;
	private final List<Player> _bots = new ArrayList<>();
	private int _pollCount;
	private volatile boolean _teardownDone;
	private volatile boolean _reported;
	private boolean _exceptions;

	// B0..B6 verdict flags.
	private boolean _b0PartyOk;
	private boolean _b1AutoPlayOk = true;
	private boolean _b2AssistObserved;
	private boolean _b3RealCombat;
	private boolean _b4Cycles;
	private boolean _b5PartyStable = true;
	private boolean _b6Teardown;

	// Timeline markers (first-occurrence gates).
	private boolean _t0, _t1, _t2, _t3, _t4, _t5, _t6, _t7, _t8, _t9;
		private int _targetTransitions;
	private int _hpDecreaseEvents;
	private int _cyclesSeen;

	// Observation tracking.
	private int _lastLeaderTargetId = -1;
	private int _lastLeaderTargetHp = -1;
	private final Set<String> _attackersSeen = new HashSet<>();
	private boolean _assistMoveObserved;

	public BotSpikeParty()
	{
		boolean enabled = false;
		try (FileInputStream fis = new FileInputStream("data/scripts/custom/BotSpikeParty/BotSpikeParty.ini"))
		{
			final Properties props = new Properties();
			props.load(fis);
			enabled = Boolean.parseBoolean(props.getProperty("BotSpikePartyEnabled", "False").trim());
		}
		catch (Exception e)
		{
			LOGGER.info(TAG + " gate unreadable (" + e.getMessage() + ") - spike idle.");
			return;
		}
		if (!enabled)
		{
			LOGGER.info(TAG + " gate disabled (BotSpikeParty.ini) - spike idle.");
			return;
		}
		LOGGER.info(TAG + " gate enabled - scheduling spike: start in " + START_DELAY_MS + " ms, observe " + OBSERVE_MS + " ms, watchdog " + WATCHDOG_AT_MS + " ms.");
		ThreadPool.schedule(this::startExperiment, START_DELAY_MS);
		ThreadPool.schedule(this::runTeardown, START_DELAY_MS + TEARDOWN_AT_MS);
		ThreadPool.schedule(this::runReport, START_DELAY_MS + REPORT_AT_MS);
		ThreadPool.schedule(this::runWatchdog, WATCHDOG_AT_MS);
	}

	private boolean allClientless(List<Player> members)
	{
		for (Player p : members)
		{
			if (p.getClient() != null)
			{
				return false;
			}
		}
		return true;
	}

	private static String targetBrief(WorldObject t)
	{
		if (t == null)
		{
			return "null";
		}
		String name = t.toString();
		try
		{
			name = t.getName();
		}
		catch (Throwable ignored)
		{
		}
		if (t instanceof Creature)
		{
			final Creature c = (Creature) t;
			return name + "(" + c.getObjectId() + ") hp=" + (int) c.getCurrentHp() + "/" + (int) c.getMaxHp() + (c.isAlikeDead() ? " DEAD" : "");
		}
		return name + "(" + t.getObjectId() + ")";
	}

	private String snapshot()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("leader=").append(_leader.getName()).append("(").append(_leader.getObjectId()).append(")")
			.append(" tgt=").append(targetBrief(_leader.getTarget()))
			.append(" atk=").append(_leader.isAttackingNow()).append(" mov=").append(_leader.isMoving())
			.append(" ap=").append(_leader.isAutoPlaying());
		for (int i = 0; i < _bots.size(); i++)
		{
			final Player b = _bots.get(i);
			if (b == null)
			{
				continue;
			}
			final boolean assist = (_lastLeaderTargetId >= 0) && (b.getTarget() != null) && (b.getTarget().getObjectId() == _lastLeaderTargetId);
			sb.append(" | ").append(BOT_NAMES[i])
				.append("=").append(targetBrief(b.getTarget()))
				.append(" atk=").append(b.isAttackingNow() || b.isCastingNow())
				.append(" mov=").append(b.isMoving())
				.append(" ai=").append(b.hasAI() ? b.getAI().getIntention().name() : "NOAI")
				.append(" distL=").append((int) b.calculateDistance3D(_leader))
				.append(" assist=").append(assist);
		}
		return sb.toString();
	}
	private void startExperiment()
	{
		try
		{
			LOGGER.info(TAG + " ===== PARTY+AUTOPLAY SPIKE START =====");
			LOGGER.info(TAG + " gate AutoPlayConfig.ENABLE_AUTO_PLAY=" + AutoPlayConfig.ENABLE_AUTO_PLAY + " | ENABLE_AUTO_ASSIST(AssistLeader)=" + AutoPlayConfig.ENABLE_AUTO_ASSIST);
			if (!AutoPlayConfig.ENABLE_AUTO_PLAY || !AutoPlayConfig.ENABLE_AUTO_ASSIST)
			{
				LOGGER.warning(TAG + " ABORT-BLOCKED: EnableAutoPlay / AssistLeader global gate is false.");
				return;
			}

			// Safety: no human players may be affected.
			final Collection<Player> playersInWorld = World.getPlayers();
			if (!playersInWorld.isEmpty())
			{
				LOGGER.warning(TAG + " ABORT-BLOCKED: players present in World=" + playersInWorld.size());
				return;
			}
			LOGGER.info(TAG + " isolation ok: World players=0 | visible objects=" + World.getVisibleObjects().size());

			// Anchor npc (passive mobs of Talking Island gludio32_1725_02).
			Npc anchor = null;
			double best = Double.MAX_VALUE;
			for (WorldObject obj : World.getVisibleObjects())
			{
				if (!(obj instanceof Npc))
				{
					continue;
				}
				final Npc npc = (Npc) obj;
				boolean match = false;
				for (int id : TARGET_NPC_IDS)
				{
					if (npc.getId() == id)
					{
						match = true;
						break;
					}
				}
				if (!match || npc.isAlikeDead() || !npc.isSpawned())
				{
					continue;
				}
				final double d = Math.hypot(npc.getX() - ANCHOR_X, npc.getY() - ANCHOR_Y);
				if (d < best)
				{
					best = d;
					anchor = npc;
				}
			}
			if (anchor == null)
			{
				LOGGER.warning(TAG + " ABORT-BLOCKED: no passive target npc spawned near anchor.");
				return;
			}
			LOGGER.info(TAG + " anchor npc=" + anchor.getName() + "(" + anchor.getId() + ") pos=" + anchor.getX() + "," + anchor.getY() + "," + anchor.getZ() + " dist=" + (int) best);

			// B0 step 1-N: load leader + 8 bots (all clientless).
			final List<Player> members = new ArrayList<>();
			{
				final Player leader = Player.load(LEADER_ID);
				if (leader == null)
				{
					LOGGER.warning(TAG + " ABORT: Player.load returned null for leader " + LEADER_ID);
					return;
				}
				_leader = leader;
				LOGGER.info(TAG + " T0 leader loaded objectId=" + leader.getObjectId() + " name=" + leader.getName() + " level=" + leader.getLevel() + " client=" + (leader.getClient() == null ? "NULL" : leader.getClient().toString()));
				if (leader.getClient() != null)
				{
					LOGGER.warning(TAG + " ABORT: leader GameClient is not null (precondition violated).");
					return;
				}
				members.add(leader);
			}
			for (int i = 0; i < BOT_IDS.length; i++)
			{
				final int id = BOT_IDS[i];
				final Player bot = Player.load(id);
				if (bot == null)
				{
					throw new IllegalStateException("Player.load null for bot charId=" + id);
				}
				if (bot.getClient() != null)
				{
					throw new IllegalStateException("bot charId=" + id + " has GameClient (must be clientless).");
				}
				if (!BOT_NAMES[i].equals(bot.getName()))
				{
					LOGGER.warning(TAG + " NOTE: bot charId=" + id + " name=" + bot.getName() + " (expected " + BOT_NAMES[i] + ").");
				}
				_bots.add(bot);
				members.add(bot);
				LOGGER.info(TAG + " T1 bot loaded objectId=" + bot.getObjectId() + " name=" + bot.getName() + " client=NULL");
			}

			// DB-safety: never autosave the spike players.
			for (Player p : members)
			{
				PlayerAutoSaveTaskManager.getInstance().remove(p);
			}

			// Clientless online state (no DB write) + offline-play memory flag.
			for (Player p : members)
			{
				p.setOnlineStatus(true, false);
				p.setOfflinePlay(true);
			}
			_t1 = true;
			LOGGER.info(TAG + " T1 all 8 bots confirmed GameClient==null | online=" + _leader.isOnline() + " inOfflineMode=" + _leader.isInOfflineMode() + " offlinePlay=" + _leader.isOfflinePlay());

			// Spawn a 3x3-style cluster around the anchor (within ALT_PARTY_RANGE*2 = 3000 assist radius).
			final int cx = ANCHOR_X;
			final int cy = ANCHOR_Y;
			final int cz = anchor.getZ();
			_leader.spawnMe(cx, cy, cz);
			for (int i = 0; i < _bots.size(); i++)
			{
				final Player b = _bots.get(i);
				final int dx = ((i % 3) - 1) * 120;
				final int dy = (((i / 3) % 3) - 1) * 120;
				b.spawnMe(cx + dx, cy + dy, cz);
				if (World.getPlayer(b.getObjectId()) != b)
				{
					throw new IllegalStateException("World.getPlayer mismatch for bot " + b.getName());
				}
			}
			LOGGER.info(TAG + " all 9 members spawned near anchor; leader pos=" + _leader.getX() + "," + _leader.getY() + "," + _leader.getZ());
			// B0: native Party construction (correct join path: joinParty sets _party).
			_party = new Party(_leader, PartyDistributionType.FINDERS_KEEPERS);
			_leader.setParty(_party);
			for (Player bot : _bots)
			{
				bot.joinParty(_party);
			}
			final boolean b0 = verifyParty(_party, _bots);
			_b0PartyOk = b0;
			_t0 = true;
			LOGGER.info(TAG + " T0 B0 party constructed leader=" + _party.getLeader().getName() + " memberCount=" + _party.getMemberCount() + " ok=" + b0);

			// B1: configure native AutoPlay on all 9 (melee path: autoAction 2 -> isMageCaster false).
			for (Player p : members)
			{
				p.getAutoPlaySettings().setNextTargetMode(1); // Monster
				p.getAutoPlaySettings().setPickup(false);
				p.getAutoPlaySettings().setShortRange(false);
				p.getAutoPlaySettings().setRespectfulHunting(false);
				p.getAutoUseSettings().getAutoActions().add(2); // AUTO_ATTACK_ACTION
			}
			LOGGER.info(TAG + " autoplay settings applied to all 9 (targetMode=1 pickup=false shortRange=false respectful=false autoActions=[2]).");

			// B1: start native AutoPlay engine on all 9.
			for (Player p : members)
			{
				AutoPlayTaskManager.getInstance().startAutoPlay(p);
			}
			_t2 = true;
			boolean allAp = true;
			for (Player p : members)
			{
				allAp &= p.isAutoPlaying();
			}
			_b1AutoPlayOk = allAp && allClientless(members);
			for (Player bot : _bots)
			{
				LOGGER.info(TAG + " B1 bot=" + bot.getName() + " isAutoPlaying=" + bot.isAutoPlaying() + " client=" + (bot.getClient() == null ? "NULL" : "SET"));
			}
			LOGGER.info(TAG + " T2 B1 all-autoplay=" + allAp + " | leader ap=" + _leader.isAutoPlaying() + " | all clientless=" + allClientless(members));

			LOGGER.info(TAG + " polling every " + POLL_MS + " ms for " + OBSERVE_MS + " ms (B2 AssistLeader, B3 combat, B4 cycles, B5 party sanity).");
			ThreadPool.scheduleAtFixedRate(this::poll, POLL_MS, POLL_MS);
		}
		catch (Throwable t)
		{
			_exceptions = true;
			LOGGER.severe(TAG + " START EXCEPTION: " + t);
			for (StackTraceElement ste : t.getStackTrace())
			{
				LOGGER.severe(TAG + "   at " + ste);
			}
		}
	}

	private boolean verifyParty(Party party, List<Player> bots)
	{
		boolean ok = party.getMemberCount() == 9;
		ok &= party.getLeader() == _leader;
		ok &= party.isLeader(_leader);
		ok &= _leader.getParty() == party;
		ok &= _leader.isInParty();
		final List<Player> members = party.getMembers();
		ok &= members.contains(_leader);
		for (Player bot : bots)
		{
			ok &= members.contains(bot);
			ok &= bot.getParty() == party;
			ok &= bot.isInParty();
			ok &= bot.getClient() == null;
			ok &= World.getPlayer(bot.getObjectId()) == bot;
		}
		for (Player m : members)
		{
			LOGGER.info(TAG + " B0 member=" + m.getName() + "/" + m.getObjectId() + " client=" + (m.getClient() == null ? "NULL" : "SET") + " inParty=" + m.isInParty());
		}
		LOGGER.info(TAG + " B0 verify memberCount=" + party.getMemberCount() + " leader=" + party.getLeader().getName() + " ok=" + ok);
		return ok;
	}
		private void poll()
	{
		// Stop observation once teardown/report has run (guards against _leader/_party being nulled).
		if (_teardownDone || _reported || (_leader == null))
		{
			return;
		}
		try
		{
			_pollCount++;
			if (_pollCount * POLL_MS > OBSERVE_MS)
			{
				return;
			}

			final Party party = _party;
			if (party != null)
			{
				// B5: native Party sanity on every poll.
				final int mc = party.getMemberCount();
				final boolean leaderOk = (party.getLeader() == _leader) && (_leader.getParty() == party);
				boolean stable = leaderOk && (mc == 9);
				for (Player bot : _bots)
				{
					stable &= bot.getParty() == party;
					stable &= bot.isInParty();
					stable &= bot.getClient() == null;
				}
				_b5PartyStable &= stable;
			}

			// Leader target snapshot (source of the assist relation).
			final WorldObject lObj = _leader.getTarget();
			final Creature lTgt = (lObj != null) && lObj.isCreature() ? lObj.asCreature() : null;
			final int lTid = (lTgt != null) ? lTgt.getObjectId() : -1;
			final int lHp = (lTgt != null) ? (int) lTgt.getCurrentHp() : -1;

			// T3: leader acquired a target.
			if (!_t3 && (lTgt != null) && lTgt.isAttackable())
			{
				_t3 = true;
				LOGGER.info(TAG + " T3 leader target acquired -> " + targetBrief(lObj) + " | ai=" + (_leader.hasAI() ? _leader.getAI().getIntention().name() : "NOAI"));
			}

			// T8: target transition (leader target object id changed between polls).
			if (_t3 && (lTid >= 0) && (lTid != _lastLeaderTargetId) && (_lastLeaderTargetId != -1))
			{
				_targetTransitions++;
				_t8 = true;
				_cyclesSeen++;
				LOGGER.info(TAG + " T8 target transition: leader newtgt oid=" + lTid + " hp=" + lHp + " (prev oid=" + _lastLeaderTargetId + " hp=" + _lastLeaderTargetHp + ") cycle#" + _cyclesSeen);
			}

			// T6: real HP change on the leader's current target (decreases between polls).
			if (_t3 && (lTid >= 0) && (lTid == _lastLeaderTargetId) && (_lastLeaderTargetHp >= 0) && (lHp < _lastLeaderTargetHp))
			{
				_hpDecreaseEvents++;
				_t6 = true;
				LOGGER.info(TAG + " T6 leader target HP change: oid=" + lTid + " hp " + _lastLeaderTargetHp + " -> " + lHp + " (event#" + _hpDecreaseEvents + ")");
			}

			// B2 + T4 + T5 + B3: per-bot assist + combat evidence.
			int assistCount = 0;
			for (int i = 0; i < _bots.size(); i++)
			{
				final Player b = _bots.get(i);
				final WorldObject bObj = b.getTarget();
				final boolean assist = (lTid >= 0) && (bObj != null) && (bObj.getObjectId() == lTid);
				if (assist)
				{
					assistCount++;
					if (b.isMoving() || (b.hasAI() && b.getAI().getIntention() == Intention.ATTACK))
					{
						_assistMoveObserved = true;
					}
					if (!_t4)
					{
						_t4 = true;
						LOGGER.info(TAG + " T4 first assist: " + BOT_NAMES[i] + " target == leader target oid=" + lTid + " distL=" + (int) b.calculateDistance3D(_leader));
					}
				}
				final boolean atk = b.isAttackingNow() || b.isCastingNow() || (b.hasAI() && (b.getAI().getIntention() == Intention.ATTACK));
				if (atk && _attackersSeen.add(BOT_NAMES[i]))
				{
					LOGGER.info(TAG + " T5 B3 targetID=" + lTid + " targetHP=" + lHp + " attacking bot=" + BOT_NAMES[i] + " | ts=" + Instant.now());
					if (!_t5)
					{
						_t5 = true;
					}
				}
			}

			_b2AssistObserved = _b2AssistObserved || (assistCount > 0);
			_b3RealCombat = _t6 && !_attackersSeen.isEmpty();
			// T7 (continued combat): a mob killed (transition) + >=1 attacking bot, or >=2 hp-decrease events,
			// i.e. multiple combat cycles with native AssistLeader following.
			if ((_targetTransitions >= 1 && !_attackersSeen.isEmpty()) || (_hpDecreaseEvents >= 2) || (_attackersSeen.size() >= 2))
			{
				_t7 = true;
				_b4Cycles = true;
			}

			if ((_pollCount % 5) == 0)
			{
				LOGGER.info(TAG + " [poll " + _pollCount + "] " + snapshot());
			}
			else
			{
				LOGGER.info(TAG + " [poll " + _pollCount + "] L tgt=" + targetBrief(lObj) + " | assist=" + assistCount + " atk=" + _attackersSeen.size());
			}

			_lastLeaderTargetId = lTid;
			_lastLeaderTargetHp = lHp;
		}
		catch (Throwable t)
		{
			_exceptions = true;
			LOGGER.severe(TAG + " POLL EXCEPTION poll#" + _pollCount + ": " + t);
			for (StackTraceElement ste : t.getStackTrace())
			{
				LOGGER.severe(TAG + "   at " + ste);
			}
		}
	}
	private void runTeardown()
	{
		if (_teardownDone)
		{
			return;
		}
		final List<Player> members = new ArrayList<>();
		if (_leader != null)
		{
			members.add(_leader);
		}
		for (Player b : _bots)
		{
			if (b != null)
			{
				members.add(b);
			}
		}
		try
		{
			LOGGER.info(TAG + " ===== TEARDOWN BEGIN ===== members=" + members.size() + " leader.ap=" + (_leader != null && _leader.isAutoPlaying()) + " partyMemberCount=" + (_party != null ? _party.getMemberCount() : -1));
			if (_leader == null && _bots.isEmpty())
			{
				LOGGER.info(TAG + " teardown: experiment never ran - nothing to clean.");
				_teardownDone = true;
				return;
			}

			// B6 order: offlinePlay false BEFORE stopAutoPlay (avoids OfflinePlayTable DELETE -> DB write).
			for (Player p : members)
			{
				try
				{
					p.setOfflinePlay(false);
					AutoPlayTaskManager.getInstance().stopAutoPlay(p);
					PlayerAutoSaveTaskManager.getInstance().remove(p);
					p.stopVitalityTask();
					p.setOnlineStatus(false, false);
				}
				catch (Throwable t)
				{
					_exceptions = true;
					LOGGER.severe(TAG + " TEARDOWN member EXCEPTION " + p.getName() + ": " + t);
				}
			}

			// Remove all members from the native Party.
			final Party party = _party;
			if (party != null)
			{
				try
				{
					for (Player bot : _bots)
					{
						if (bot != null)
						{
							party.removePartyMember(bot, PartyMessageType.LEFT);
							LOGGER.info(TAG + " B6 bot removed from party=" + bot.getName() + " memberCount=" + party.getMemberCount());
						}
					}
					if (_leader != null)
					{
						party.removePartyMember(_leader, PartyMessageType.LEFT);
						LOGGER.info(TAG + " B6 leader removed from party leader=" + _leader.getName());
					}
				}
				catch (Throwable t)
				{
					_exceptions = true;
					LOGGER.severe(TAG + " TEARDOWN party remove EXCEPTION: " + t);
				}
			}

			// Decay / remove from World + verify Party==null for every member (dangling cleanup).
			for (Player p : members)
			{
				try
				{
					if (p.getParty() != null)
					{
						LOGGER.warning(TAG + " teardown: native removal left party reference for " + p.getName() + " - setParty(null).");
						p.setParty(null);
					}
					if (p.isSpawned())
					{
						p.decayMe();
					}
				}
				catch (Throwable t)
				{
					_exceptions = true;
					LOGGER.severe(TAG + " TEARDOWN decay EXCEPTION " + p.getName() + ": " + t);
				}
								LOGGER.info(TAG + " B6 final " + p.getName() + " world=" + (World.getPlayer(p.getObjectId()) == null) + " party=" + (p.getParty() == null) + " ap=" + p.isAutoPlaying() + " offlinePlay=" + p.isOfflinePlay() + " client=" + (p.getClient() == null ? "NULL" : "SET"));
			}

			// Verify B6: World.getPlayer(botId)==null and Party==null for every bot.
			boolean b6 = true;
			for (Player bot : _bots)
			{
				b6 &= (World.getPlayer(bot.getObjectId()) == null);
				b6 &= (bot.getParty() == null);
				b6 &= (!bot.isAutoPlaying());
				b6 &= (!bot.isOfflinePlay());
			}
			_b6Teardown = b6 && !_exceptions;
			_t9 = true;
			_teardownDone = true;
			LOGGER.info(TAG + " T9 B6 teardown done ok=" + _b6Teardown + " | leader world=" + (World.getPlayer(_leader.getObjectId()) == null) + " leader party=" + (_leader.getParty() == null));
		}
		finally
		{
			_party = null;
			_leader = null;
			_bots.clear();
		}
	}

	private void runReport()
	{
		if (_reported)
		{
			return;
		}
		_reported = true;
		if (!_teardownDone)
		{
			runTeardown();
		}
		LOGGER.info(TAG + " ===== PARTY+AUTOPLAY SPIKE VERDICT =====");
		LOGGER.info(TAG + " B0 party construction (9 members, leader, 8 clientless): " + _b0PartyOk);
		LOGGER.info(TAG + " B1 autoplay active per bot (clientless, no immediate shutdown): " + _b1AutoPlayOk);
		LOGGER.info(TAG + " B2 AssistLeader observed (bot target == leader target, move toward target): " + _b2AssistObserved + " | assistMove=" + _assistMoveObserved);
		LOGGER.info(TAG + " B3 real group combat (leader target HP change + bot attacks): " + _b3RealCombat + " | hpDecreaseEvents=" + _hpDecreaseEvents + " | distinctAttackers=" + _attackersSeen.size());
		LOGGER.info(TAG + " B4 continued behavior (targetTransitions=" + _targetTransitions + ", cycles=" + _cyclesSeen + ", observeMs=" + (_pollCount * POLL_MS) + "): " + _b4Cycles);
		LOGGER.info(TAG + " B5 party sanity during+after: " + _b5PartyStable);
		LOGGER.info(TAG + " B6 teardown (World.getPlayer==null, Party==null per bot): " + _b6Teardown);
		LOGGER.info(TAG + " timeline: T0=" + _t0 + " T1=" + _t1 + " T2=" + _t2 + " T3=" + _t3 + " T4=" + _t4 + " T5=" + _t5 + " T6=" + _t6 + " T7=" + _t7 + " T8=" + _t8 + " T9=" + _t9);
		final boolean overall = _b0PartyOk && _b1AutoPlayOk && _b2AssistObserved && _b3RealCombat && _b4Cycles && _b5PartyStable && _b6Teardown && !_exceptions;
		LOGGER.info(TAG + " OVERALL VERDICT: " + (overall ? "PASS" : "FAIL"));
		LOGGER.info(TAG + " exceptions=" + _exceptions + " | polls=" + _pollCount + " | attackers=" + _attackersSeen);
		LOGGER.info(TAG + " spike finished - scheduling controlled server shutdown in " + SHUTDOWN_SECONDS + " s (restores clean state).");
		try
		{
			Shutdown.getInstance().startShutdown(null, (int) SHUTDOWN_SECONDS, false);
		}
		catch (Throwable t)
		{
			LOGGER.severe(TAG + " SHUTDOWN SCHEDULE EXCEPTION: " + t);
		}
	}

	private void runWatchdog()
	{
		if (_reported)
		{
			return;
		}
		LOGGER.warning(TAG + " WATCHDOG: cycle did not complete in " + WATCHDOG_AT_MS + " ms - forcing teardown + shutdown.");
		try
		{
			runTeardown();
		}
		catch (Throwable t)
		{
			LOGGER.severe(TAG + " WATCHDOG TEARDOWN EXCEPTION: " + t);
		}
		try
		{
			Shutdown.getInstance().startShutdown(null, 10, false);
		}
		catch (Throwable t)
		{
			LOGGER.severe(TAG + " WATCHDOG SHUTDOWN EXCEPTION: " + t);
		}
	}

	public static void main(String[] args)
	{
		new BotSpikeParty();
	}
}
//__APPEND_POINT__