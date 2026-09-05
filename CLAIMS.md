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
| CL-0020 | REF-BOOT start procedure re-validated 2026-09-03: LoginServer READY ≈1s (ports 9014+2106); GameServer 4 READY signals in order (Handlers Loaded → 42348 spawns → Server loaded 179s → Registered Server 2) | VERIFIED-RUNTIME | e2518ab108 | RUNTIME: login/log/java0.log 23:18:41-42; game/log/java0.log 23:20-23:22; evidence/REF-BOOT-2026-09-03 · promotes_to: R-SRV001 | — |
| CL-0021 | Method C shutdown (Stop-Process -Force) of GameServer+LoginServer verified 2026-09-03: processes terminated, ports 2106/9014/7777 closed, DB remained clean (0 online) with no players connected | VERIFIED-RUNTIME | e2518ab108 | RUNTIME: validation/REF_BOOT_VALIDATION_REPORT.md §Test 3 · promotes_to: R-SRV001 | — |
| CL-0022 | `EnableGUI = False` required for headless/agent operation of both LoginServer and GameServer (Interface.ini); default `True` blocks non-GUI start | VERIFIED-RUNTIME | e2518ab108 | RUNTIME: validation/REF_BOOT_VALIDATION_REPORT.md §Files Modified · promotes_to: R-SRV001 | — |
| CL-0023 | Direct `java -jar ../libs/LoginServer.jar` / `GameServer.jar` is equivalent to the VBS launchers (same JVM flags via java.cfg); both methods boot the same runtime | VERIFIED-RUNTIME | e2518ab108 | RUNTIME: validation/REF_BOOT_VALIDATION_REPORT.md §Test 1-2; CL-0020 · promotes_to: R-SRV001 | — |
| CL-0024 | Quest party-drop helper: `Quest.getRandomPartyMember(Player,String,String)` (Quest.java:1995) y `Quest.getRandomPartyMemberState(Player,byte)` (Quest.java:2067) seleccionan ALEATORIAMENTE (Rnd) un miembro ELEGIBLE de la party — mismo quest var/state, misma instancia, dentro de `ALT_PARTY_RANGE` del target/jugador (Quest.java:2043/2110) — como receptor del drop/recompensa de quest, por lo que el miembro que recibe/procesa el drop puede NO ser el `killer`; tras la selección, los pasos posteriores del script de quest deben operar sobre ese Player (`qs.getPlayer()`) y NO sobre `killer` — patrón fijado por el commit upstream `43ac8878f5` (Q00103_SpiritOfCraftsman ×8 crónicas: checks de items, giveItems/takeItems/giveItemRandomly y check de rango movidos de `killer` al helper-selected player). Conocimiento SOURCE/UPSTREAM — NO implica que el runtime actual (rebuild de e2518ab108) contenga este fix | VERIFIED-SOURCE | 43ac8878f5 (UPSTREAM master; runtime aún e2518ab108) | SOURCE: Quest.java:1995,2043,2056,2067,2110,2123 (UPSTREAM @ 43ac8878f5); UPSTREAM: commit 43ac8878f5 — Q00103_SpiritOfCraftsman.java ×8 (C1, C4, CT_0_Interlude, CT_2.4_Epilogue, CT_2.6_HighFive, Classic_1.0, Classic_1.5, Classic_2.0); antes: killer @ CT_2.6:300-312 vs después: qs.getPlayer() @ CT_2.6:294-313; RUNTIME: sin validar (runtime @ e2518ab108) | — |

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
- **`promotes_to: R-XXX`**: marks a claim that directly backs a public L2J-RECIPE recipe. Notebook keeps the evidence; the Recipe keeps the operational procedure.