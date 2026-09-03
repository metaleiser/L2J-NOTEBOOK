# MANIFEST — evidence/spikes/D0001/ (D-0001 AutoPlay clientless spike — 2026-09-01)

Preservación física del spike **D-0001** (motor nativo AutoPlay sobre un Player server-side real
con `GameClient == null`). Ejecutado en el LIVE RUNTIME el 2026-09-01.

| Archivo | Origen exacto | Tamaño | SHA-256 | Clase de procedencia |
|---|---|---|---|---|
| `BotSpikeD01.java` | LIVE `game\data\scripts\custom\BotSpikeD01\BotSpikeD01.java` (harness ejecutado en runtime) | 15.807 B | `F3F07ED2B1DB48E8935FDD8223988DF98C89D062B711328963E180FF8328E51D` | GENERATED (fuente del harness; copia fiel del archivo ejecutado) |
| `BotSpikeD01.ini` | LIVE `game\data\scripts\custom\BotSpikeD01\BotSpikeD01.ini` (gate `BotSpikeD01Enabled = True`, restaurado a False tras el spike) | 165 B | `71A823F732966485E181C7A6686692D3A5A06342629A52D56CC9DD1F7EC20E54` | GENERATED (config gate transitorio) |
| `BotSpikeD01.class` | Compilación javac 25 del mismo `BotSpikeD01.java` (el runtime compila in-memory; este artefacto es la compilación de verificación del source preservado) | 14.635 B | `DE0DFA447568D1DC0344BBCC4E86BAAE25D2FF5E5E788DE1DD55A7F112C261A9` | GENERATED (compilación offline de verificación; no es el class in-memory del runtime) |
| `spike_log_java0_2026-09-01.txt` | LIVE `game\log\java0.log` — extracto 2026-09-01 17:19:19 — 17:22:04 (líneas `[BotSpikeD01]`, boot, registro login, shutdown) | 14.283 B | `C465E645D4DFBFA9EFE5B84630F6242CF84179A8F67D325878D590B0C361BD92` | RUNTIME EXECUTION (importado: export, no es el log crudo completo) |

## Verdict

**P1 PASS · P2 PASS · P3 PASS (B3a+B3b+B3c+B3d) · P4 PASS** — ver `knowledge/19_D0001_AUTO_PLAY_SPIKE.md`.

- Líneas de veredicto emitidas por el harness en runtime (17:21:44):
  `P1 engine activation (client==null + autoplay active + no shutdown): true`
  `P2 autonomous targeting: true | movement: true`
  `B3a attack initiated: true | B3b real HP change: true | B3c cycle continued: true | B3d mob death: true`
  `P4 exceptions during spike: false | polls=57 | attackTicks=45`
- Teardown confirmado en runtime (17:21:41): `World.getPlayer(id)=null | spawned=false | ap=false | offlinePlay=false | online=false | client=NULL`.

## Contexto de baseline y entorno

- Baseline UPSTREAM: `e2518ab10872b28cd4c6860e102b493656ba8728` (runtime = clean rebuild LIVE).
- LIVE RUNTIME: `E:\L2J MOBIUS IA\L2J MOBIUS H5 SERVER` (LoginServer + GameServer bajo JDK 25).
- DB `l2jmobiush5`, char desechable `268483035` (ADMIN, level 1): estado DB post-spike verificado idéntico al pre-spike (`online=0`, pos `-119254,45846`, `character_offline_play` con 0 filas).
- `AutoPlay.ini`: pre-test SHA-256 `B128353128CBD9E7CBDC3D214A9342AA1865F17D3DB9B7AB38AFA9A6AF255E72` (`EnableAutoPlay = False`) → temporal `True` → restaurado post-spike con SHA-256 idéntico (`MATCH ORIGINAL: True`).
- UPSTREAM: sin escritura, sin compilación, sin ejecución durante esta tarea (solo lectura).

## Notas

- Este directorio NO modifica la evidencia C2/C3 previa (`../BotSpikeC0*.class`, `../MANIFEST.md`).
- La fuente `.java` aquí preservada es la evidencia reproductiva del harness; los `C2/C3` `.java` previos siguen clasificados LOST (ver `knowledge/15`, `knowledge/17`).
- Inmutabilidad: sin recompilar, sin reescribir, sin modificar sin autorización GM (contracts/02 sección 7).

---
*MANIFEST D-0001 — generado 2026-09-01.*