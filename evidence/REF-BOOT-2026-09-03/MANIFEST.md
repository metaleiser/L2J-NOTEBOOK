# MANIFEST — REF-BOOT Validation Evidence (2026-09-03)

**Test:** REF-BOOT server lifecycle re-validation (Start → Verify → Shutdown)
**Date:** 2026-09-03 (23:18–23:22)
**Baseline:** L2J Mobius CT 2.6 HighFive @ `e2518ab108`
**How:** Direct `java -jar` (no VBS), `EnableGUI=False` for headless
**Verdict:** PASS (with notes — Method A `//shutdown` untestable, no client)

## SHA-256 Manifest

| File | Size (bytes) | SHA-256 |
|------|-------------|---------|
| login_java0_2026-09-03.txt | 1581 | D88BC18B3FF5679FE4333C90410749F98ED7BBEF24E481D16FF5722EEAFF3E85 |
| game_java0_handlers_2026-09-03.txt | 1286 | AC92618C2F2B9957AD80275D326C4AEE8076BB99E453E93472B2AB2DAD2E5D5E |
| game_java0_spawns_2026-09-03.txt | 126 | 2188F6A3C36F1C4DC3684F247EE406D960BFB07931A49AA6A899873807C2B830 |
| game_java0_ready_2026-09-03.txt | 511 | 5D9D94483D10289609819B037B9309235B64B87D1BD9744F6C057AC1131444CF |

## Provenance

- Source logs: `${RUNTIME_DIR}\login\log\java0.log` and `${RUNTIME_DIR}\game\log\java0.log` (2026-09-03 runs)
- Extracts copied verbatim on 2026-09-03; hashes verify integrity
- Full narrative report: L2J-RECIPE `validation/REF_BOOT_VALIDATION_REPORT.md`
- Claims referencing this evidence: CL-0020, CL-0021, CL-0022 (L2J Notebook `CLAIMS.md`)

## Raw evidence summary (extract contents)

**login_java0_2026-09-03.txt** — LoginServer boot at 23:18:41–23:18:42:
- `LoginServer: Listening for GameServers on 127.0.0.1:9014`
- `LoginServer: is now listening on: 0.0.0.0:2106`
- HikariCP pool initialized; boot ≈ 1 second

**game_java0_handlers_2026-09-03.txt** — GameServer handler loading at 23:20:11–23:20:12:
- `MasterHandler: Handlers Loaded...`
- `AdminCommandHandler: Loaded 481 handlers`

**game_java0_spawns_2026-09-03.txt** — SpawnData at 23:21:58:
- `SpawnData: 42348 spawns have been initialized!`

**game_java0_ready_2026-09-03.txt** — READY signals at 23:22:13–23:22:14:
- `GameServer: Server loaded in 179 seconds.`
- `LoginServerThread: Registered on login as Server 2: Sieghardt`

---

*Manifest generado 2026-09-03 bajo el flujo de promoción REF-BOOT (evidence → claim → recipe). Hashes verificables con `Get-FileHash`.*