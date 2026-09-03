# CLAIMS — Atomic Claims Ledger

> Append‑only. Status: VERIFIED unless marked otherwise.
> Baseline: L2J Mobius CT 2.6 HighFive @ e2518ab108 (unless noted).

| ID | Claim | Status | Baseline | Evidence | Supersedes |
|----|-------|--------|----------|----------|------------|
| CL-0001 | `Player.load(int)` is public, delegates to `restore(objectId)` | VERIFIED-SOURCE | e2518ab108 | SOURCE: Player.java:1211-1213 | — |
| CL-0002 | `Player.sendPacket(ServerPacket)` is null‑safe over `_client` | VERIFIED-SOURCE | e2518ab108 | SOURCE: Player.java:4429-4435 | — |
| CL-0003 | Party stores members as `List<Player>` (CopyOnWriteArrayList) | VERIFIED-SOURCE | e2518ab108 | SOURCE: Party.java:88 | — |
| CL-0004 | Mobius FakePlayer = NPC representation, not Player | VERIFIED-SOURCE | e2518ab108 | SOURCE: FakePlayerInfo.java:38 | — |
| CL-0005 | `Player.joinParty(Party)` sets `_party`; `Party.addPartyMember(Player)` does NOT | VERIFIED-SOURCE | e2518ab108 | SOURCE: Player.java:6862, Party.java:280 | — |
| CL-0006 | `CommandChannel(Player leader)` NPE if leader has no party | VERIFIED-SOURCE | e2518ab108 | SOURCE: CommandChannel.java:48-57 | — |
| CL-0007 | CommandChannel autodisbands when `_parties.size() < 2` | VERIFIED-SOURCE | e2518ab108 | SOURCE: CommandChannel.java:40-42, 87-116 | — |
| CL-0008 | Player with `GameClient == null` is safe (sendPacket null‑safe; OfflinePlayTable precedent) | VERIFIED-SOURCE | e2518ab108 | SOURCE: Player.java:4429-4435; RUNTIME: C2 spike | — |
| CL-0009 | C2 spike PASS: Player.load → spawn → 30 s → teardown | VERIFIED-RUNTIME | e2518ab108 | RUNTIME: knowledge/15 C2; CLASS hash in evidence/MANIFEST | — |
| CL-0010 | C3 spike PASS: party 1 human + 8 bots = 9 members; disband < 2 | VERIFIED-RUNTIME | e2518ab108 | RUNTIME: knowledge/15 C3 | — |
| CL-0011 | Mobius H5 has native AutoPlay/AutoUse systems (AutoPlayTaskManager, holders, config) | VERIFIED-SOURCE | e2518ab108 | SOURCE: taskmanagers/AutoPlayTaskManager.java + holders + AutoPlayConfig | — |
| CL-0012 | PlayerVariables exist for custom persistence without DB schema | VERIFIED-SOURCE | e2518ab108 | SOURCE: mechanics/variables/PlayerVariables.java | — |
| CL-0013 | D-0001 PASS: AutoPlayTaskManager operates combat on Player with GameClient==null | VERIFIED-RUNTIME | e2518ab108 | RUNTIME: evidence/spikes/D0001; knowledge/19 | — |
| CL-0014 | Party + AutoPlay + AssistLeader: party 9, follow/assist/combat, teardown clean | VERIFIED-RUNTIME | e2518ab108 | RUNTIME: evidence/spikes/PartyAutoPlay; knowledge/20 | — |
| CL-0015 | Server start procedure: Login → Game, READY signals as documented in REF-BOOT | VERIFIED-RUNTIME | e2518ab108 | RUNTIME: java0.log boots; GM validation 2026-09-02 | — |
| CL-0016 | Server shutdown: `//shutdown` or `Stop-Process`; verify process/port cleanup | VERIFIED-RUNTIME | e2518ab108 | RUNTIME: GM validated 2026-09-02; REF-BOOT | — |
| CL-0017 | `//reload handler` does NOT substitute handler bytecode (ScriptClassLoader static final) | VERIFIED-SOURCE | e2518ab108 | SOURCE: ScriptExecutor.java:53; RUNTIME: //bs status 9669 B no effect until restart | — |
| CL-0018 | `ScriptExecutor.java:53` static final SCRIPT_CLASS_LOADER prevents class redefinition | VERIFIED-SOURCE | e2518ab108 | SOURCE: ScriptExecutor.java:53, ScriptClassLoader | — |
| CL-0019 | `//bs` validated by GM 2026-09-02: summon/dismiss, party, follow/assist/combat, stability | GM-VALIDATED | e2518ab108 | RUNTIME: java0.log 13:31-13:40; audit0.log 13:31:13; GM confirmation | — |

## Legend

- **VERIFIED-SOURCE**: confirmed by reading Mobius source code (file:line anchor provided)
- **VERIFIED-RUNTIME**: confirmed by observing live server behaviour (log, spike, test)
- **GM-VALIDATED**: confirmed in‑game by the GM (product authority)
- **INFERRED**: logical conclusion from verified facts — not directly tested
- **HYPOTHESIS**: suspected but not verified
- **REFUTED**: disproven by evidence
- **STALE**: baseline changed, requires re‑verification
- **SUPERSEDED**: replaced by a later claim
- **PARKED**: interesting but not actionable