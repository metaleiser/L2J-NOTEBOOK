/*
 * D-0001 RUNTIME SPIKE - Native AutoPlay on a real Player with GameClient == null.
 *
 * TRANSIENT disposable harness (LIVE RUNTIME only). Follows the C2/C3 spike pattern:
 *  - config gate: data/scripts/custom/BotSpikeD01/BotSpikeD01.ini (BotSpikeD01Enabled = True)
 *  - no core modification, no permanent gameplay code, no .play interaction
 *  - disposable char: 268483035 (ADMIN, level 1) - same C2 validated object id
 *  - isolated area: Talking Island territory gludio32_1725_02 (passive lvl 1-3 mobs)
 *
 * Sequence (evidence printed to the server log with tag [BotSpikeD01]):
 *  START  (+60s after script load):
 *    1. verify global gate AutoPlayConfig.ENABLE_AUTO_PLAY == true
 *    2. verify World has no human players (isolation)
 *    3. find a spawned passive Npc (20432/20481/20544) near the anchor
 *    4. Player.load(268483035); assert getClient() == null
 *    5. setOnlineStatus(true, false)  -> NO database write
 *    6. setOfflinePlay(true)          -> memory flag required by AutoPlayTaskManager
 *                                        (client==null => isInOfflineMode()==true)
 *    7. spawnMe(anchor.x-200, anchor.y, anchor.z); assert World registration
 *    8. AutoPlaySettings: targetMode=1 (Monster), pickup=false, shortRange=false
 *       AutoUseSettings: autoActions += 2 (AUTO_ATTACK_ACTION -> melee engine path,
 *       isMageCaster()==false)
 *    9. AutoPlayTaskManager.startAutoPlay(bot)  -> P1 evidence
 *   10. poll every 1000ms for 45s: position, target identity/HP, AI intention,
 *       attacking/casting/moving, bot HP  -> P2/P3 (B3a..B3d) evidence
 *  TEARDOWN (+55s): setOfflinePlay(false) BEFORE stopAutoPlay (avoids
 *    OfflinePlayTable.removeOfflinePlay DELETE), stopAutoPlay, autosave.remove,
 *    stopVitalityTask, setOnlineStatus(false,false), decayMe, verify removal.
 *  REPORT (+58s): P1-P4 verdict lines + clean server shutdown (restores config state).
 *  WATCHDOG (+240s): forced teardown + shutdown if the cycle never completed.
 *
 * DB SAFETY: no setOnlineStatus(true,true), no storeOfflinePlay, no item/party writes.
 */
package custom.BotSpikeD01;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.Properties;
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
import org.l2jmobius.gameserver.mechanics.script.Script;
import org.l2jmobius.gameserver.taskmanagers.AutoPlayTaskManager;
import org.l2jmobius.gameserver.taskmanagers.PlayerAutoSaveTaskManager;

public class BotSpikeD01 extends Script
{
	private static final Logger LOGGER = Logger.getLogger(BotSpikeD01.class.getName());
	private static final String TAG = "[BotSpikeD01]";

	// Disposable C2 char (ADMIN, level 1, online=0 in DB).
	private static final int CHAR_ID = 268483035;

	// Passive low-level monsters of the isolated area (Elpy 1, Bearded Keltir 1, Elder Keltir 3).
	private static final int[] TARGET_NPC_IDS =
	{
		20432,
		20481,
		20544
	};

	// Territory gludio32_1725_02 centroid (Talking Island fields, no peace zone).
	private static final int ANCHOR_X = -81411;
	private static final int ANCHOR_Y = 246563;

	private static final long START_DELAY_MS = 60_000;
	private static final long OBSERVE_MS = 45_000;
	private static final long POLL_MS = 1_000;
	private static final long TEARDOWN_AT_MS = 55_000;
	private static final long REPORT_AT_MS = 58_000;
	private static final long WATCHDOG_AT_MS = 240_000;
	private static final long SHUTDOWN_SECONDS = 15;

	private volatile Player _bot;
	private volatile boolean _teardownDone;
	private volatile boolean _reported;

	// Evidence flags.
	private boolean _p1EngineActive;
	private boolean _p2TargetAcquired;
	private boolean _p2Moved;
	private boolean _p3AttackInitiated;
	private boolean _p3HpChanged;
	private boolean _p3CycleContinued;
	private boolean _mobDied;
	private boolean _exceptions;
	private Creature _lastTarget;
	private int _lastTargetHp = -1;
	private int _lastX = Integer.MIN_VALUE;
	private int _lastY = Integer.MIN_VALUE;
	private int _attackTicks;
	private int _pollCount;

	public BotSpikeD01()
	{
		boolean enabled = false;
		try (FileInputStream fis = new FileInputStream("data/scripts/custom/BotSpikeD01/BotSpikeD01.ini"))
		{
			final Properties props = new Properties();
			props.load(fis);
			enabled = Boolean.parseBoolean(props.getProperty("BotSpikeD01Enabled", "False").trim());
		}
		catch (Exception e)
		{
			LOGGER.info(TAG + " gate unreadable (" + e.getMessage() + ") - spike idle.");
			return;
		}

		if (!enabled)
		{
			LOGGER.info(TAG + " gate disabled (BotSpikeD01.ini) - spike idle.");
			return;
		}

		LOGGER.info(TAG + " gate enabled - scheduling spike: start in " + START_DELAY_MS + " ms, observe " + OBSERVE_MS + " ms, watchdog " + WATCHDOG_AT_MS + " ms.");
		ThreadPool.schedule(this::startExperiment, START_DELAY_MS);
		ThreadPool.schedule(this::runTeardown, START_DELAY_MS + TEARDOWN_AT_MS);
		ThreadPool.schedule(this::runReport, START_DELAY_MS + REPORT_AT_MS);
		ThreadPool.schedule(this::runWatchdog, WATCHDOG_AT_MS);
	}

	private void startExperiment()
	{
		try
		{
			LOGGER.info(TAG + " ===== D-0001 SPIKE START =====");
			LOGGER.info(TAG + " runtime gate AutoPlayConfig.ENABLE_AUTO_PLAY=" + AutoPlayConfig.ENABLE_AUTO_PLAY);
			if (!AutoPlayConfig.ENABLE_AUTO_PLAY)
			{
				LOGGER.warning(TAG + " ABORT-BLOCKED: EnableAutoPlay is false in runtime config.");
				return;
			}

			// Safety: no human players may be affected.
			final Collection<Player> players = World.getPlayers();
			if (!players.isEmpty())
			{
				LOGGER.warning(TAG + " ABORT-BLOCKED: players present in World=" + players.size());
				return;
			}
			LOGGER.info(TAG + " isolation ok: World players=0 | visible objects=" + World.getVisibleObjects().size());

			// Find the closest spawned passive npc to the anchor.
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

			// Load the real player (clientless).
			final Player bot = Player.load(CHAR_ID);
			if (bot == null)
			{
				LOGGER.warning(TAG + " ABORT: Player.load returned null for " + CHAR_ID);
				return;
			}
			_bot = bot;
			LOGGER.info(TAG + " loaded Player objectId=" + bot.getObjectId() + " name=" + bot.getName() + " level=" + bot.getLevel() + " client=" + (bot.getClient() == null ? "NULL" : bot.getClient().toString()));
			if (bot.getClient() != null)
			{
				LOGGER.warning(TAG + " ABORT: client is not null (D-0001 precondition violated).");
				return;
			}

			// Clientless online state (no DB write) + offline play flag required by the native engine.
			bot.setOnlineStatus(true, false);
			bot.setOfflinePlay(true);
			LOGGER.info(TAG + " state: online=" + bot.isOnline() + " inOfflineMode=" + bot.isInOfflineMode() + " offlinePlay=" + bot.isOfflinePlay());

			// Spawn into World near the anchor (same terrain height).
			bot.spawnMe(anchor.getX() - 200, anchor.getY(), anchor.getZ());
			final boolean inWorld = World.getPlayer(CHAR_ID) == bot;
			LOGGER.info(TAG + " world.getPlayer(id)==bot -> " + inWorld + " | spawned=" + bot.isSpawned() + " | pos=" + bot.getX() + "," + bot.getY() + "," + bot.getZ());
			if (!inWorld || !bot.isSpawned())
			{
				LOGGER.warning(TAG + " ABORT: World registration failed.");
				return;
			}
// Movement baseline: seed last coordinates to the spawn position so the
			// first poll cannot falsely flag a position change.
			_lastX = bot.getX();
			_lastY = bot.getY();

			// Minimum AutoPlay/AutoUse configuration for a melee test.
			bot.getAutoPlaySettings().setNextTargetMode(1);
			bot.getAutoPlaySettings().setPickup(false);
			bot.getAutoPlaySettings().setShortRange(false);
			bot.getAutoUseSettings().getAutoActions().add(2);
			LOGGER.info(TAG + " autoplay settings: targetMode=1 pickup=false shortRange=false autoActions=[2]");

			// P1: start the native AutoPlay engine.
			AutoPlayTaskManager.getInstance().startAutoPlay(bot);
			_p1EngineActive = bot.isAutoPlaying();
			LOGGER.info(TAG + " startAutoPlay -> isAutoPlaying=" + bot.isAutoPlaying() + " | P1 engine active=" + _p1EngineActive);

			ThreadPool.scheduleAtFixedRate(this::poll, POLL_MS, POLL_MS);
			LOGGER.info(TAG + " polling every " + POLL_MS + " ms for " + OBSERVE_MS + " ms.");
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

	private void poll()
	{
		try
		{
			_pollCount++;
			final Player bot = _bot;
			if ((bot == null) || (_pollCount * POLL_MS > OBSERVE_MS))
			{
				return;
			}

			final WorldObject targetObj = bot.getTarget();
			final Creature target = (targetObj != null) && targetObj.isCreature() ? targetObj.asCreature() : null;
			final String tgt = target == null ? "null" : target.getName() + "(" + target.getObjectId() + ") hp=" + (int) target.getCurrentHp() + "/" + (int) target.getMaxHp() + (target.isAlikeDead() ? " DEAD" : "");
			LOGGER.info(TAG + " [poll " + _pollCount + "] pos=" + bot.getX() + "," + bot.getY() + "," + bot.getZ() + " | tgt=" + tgt + " | ai=" + (bot.hasAI() ? bot.getAI().getIntention().name() : "NOAI") + " | attacking=" + bot.isAttackingNow() + " casting=" + bot.isCastingNow() + " moving=" + bot.isMoving() + " | ap=" + bot.isAutoPlaying() + " botHP=" + (int) bot.getCurrentHp() + "/" + (int) bot.getMaxHp());

			// Progression capture.
			if ((bot.getX() != _lastX) || (bot.getY() != _lastY))
			{
				_p2Moved = true;
				_lastX = bot.getX();
				_lastY = bot.getY();
			}

			if (target != null)
			{
				if (!_p2TargetAcquired)
				{
					_p2TargetAcquired = true;
					LOGGER.info(TAG + " B3a: target acquired -> " + tgt);
				}

				if (target == _lastTarget)
				{
					final int hp = (int) target.getCurrentHp();
					if ((hp < _lastTargetHp) && !_p3HpChanged)
					{
						_p3HpChanged = true;
						LOGGER.info(TAG + " B3b: real HP change observed on target (" + _lastTargetHp + " -> " + hp + ")");
					}
					if (target.isAlikeDead() && !_mobDied)
					{
						_mobDied = true;
						LOGGER.info(TAG + " B3d: mob death observed.");
					}
				}
				_lastTarget = target;
				_lastTargetHp = (int) target.getCurrentHp();

				final boolean attacking = bot.isAttackingNow() || bot.isCastingNow() || (bot.hasAI() && (bot.getAI().getIntention() == Intention.ATTACK));
				if (attacking)
				{
					if (!_p3AttackInitiated)
					{
						_p3AttackInitiated = true;
						LOGGER.info(TAG + " B3a: attack initiated -> " + tgt);
					}
					_attackTicks++;
					if (_attackTicks >= 2)
					{
						_p3CycleContinued = true;
					}
				}
			}
			else if ((_lastTarget != null) && _lastTarget.isAlikeDead())
			{
				// Previous target died; engine should pick a new one by itself.
				LOGGER.info(TAG + " B3c: previous target dead, engine re-targeting (autonomous cycle).");
			}
		}
		catch (Throwable t)
		{
			_exceptions = true;
			LOGGER.severe(TAG + " POLL EXCEPTION " + _pollCount + ": " + t);
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
		try
		{
			final Player bot = _bot;
			LOGGER.info(TAG + " ===== TEARDOWN BEGIN =====");
			if (bot == null)
			{
				LOGGER.info(TAG + " teardown: bot never started - nothing to clean.");
				_teardownDone = true;
				return;
			}
			LOGGER.info(TAG + " teardown pre-state: spawned=" + bot.isSpawned() + " ap=" + bot.isAutoPlaying() + " offlinePlay=" + bot.isOfflinePlay() + " online=" + bot.isOnline() + " dead=" + bot.isDead());
			// Order matters: clear offlinePlay flag before stopAutoPlay so setAutoPlaying(false)
			// does not trigger OfflinePlayTable.removeOfflinePlay (DELETE) - zero DB writes.
			bot.setOfflinePlay(false);
			AutoPlayTaskManager.getInstance().stopAutoPlay(bot);
			PlayerAutoSaveTaskManager.getInstance().remove(bot);
			bot.stopVitalityTask();
			bot.setOnlineStatus(false, false);
			bot.decayMe();
			_teardownDone = true;
			LOGGER.info(TAG + " teardown done: World.getPlayer(id)=" + World.getPlayer(CHAR_ID) + " | spawned=" + bot.isSpawned() + " | ap=" + bot.isAutoPlaying() + " | offlinePlay=" + bot.isOfflinePlay() + " | online=" + bot.isOnline() + " | client=" + (bot.getClient() == null ? "NULL" : bot.getClient().toString()));
		}
		catch (Throwable t)
		{
			_exceptions = true;
			LOGGER.severe(TAG + " TEARDOWN EXCEPTION: " + t);
			for (StackTraceElement ste : t.getStackTrace())
			{
				LOGGER.severe(TAG + "   at " + ste);
			}
		}
	}

	private void runReport()
	{
		if (_reported)
		{
			return;
		}
		_reported = true;
		try
		{
			if (!_teardownDone)
			{
				runTeardown();
			}
			LOGGER.info(TAG + " ===== D-0001 SPIKE VERDICT =====");
			LOGGER.info(TAG + " P1 engine activation (client==null + autoplay active + no shutdown): " + _p1EngineActive);
			LOGGER.info(TAG + " P2 autonomous targeting: " + _p2TargetAcquired + " | movement: " + _p2Moved);
			LOGGER.info(TAG + " B3a attack initiated: " + _p3AttackInitiated + " | B3b real HP change: " + _p3HpChanged + " | B3c cycle continued: " + _p3CycleContinued + " | B3d mob death: " + _mobDied);
			LOGGER.info(TAG + " P4 exceptions during spike: " + _exceptions + " | polls=" + _pollCount + " | attackTicks=" + _attackTicks);
			LOGGER.info(TAG + " spike finished - scheduling clean server shutdown in " + SHUTDOWN_SECONDS + " s (restores config state).");
		}
		finally
		{
			try
			{
				Shutdown.getInstance().startShutdown(null, (int) SHUTDOWN_SECONDS, false);
			}
			catch (Throwable t)
			{
				LOGGER.severe(TAG + " SHUTDOWN SCHEDULE EXCEPTION: " + t);
			}
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
			Shutdown.getInstance().startShutdown(null, 5, false);
		}
		catch (Throwable t)
		{
			LOGGER.severe(TAG + " WATCHDOG SHUTDOWN EXCEPTION: " + t);
		}
	}

	public static void main(String[] args)
	{
		new BotSpikeD01();
	}
}
