# BOTAI-P10-A — INFORME DE RECUPERACIÓN TRAS BLOQUEO DEL PC

> **SPRINT:** BOTAI-P10-A (recovery) · **MODO:** AUDITORÍA — sin cambios funcionales
> **Fecha auditoría:** 2026-09-15, 10:00–10:40 (hora del sistema)
> **TARGET:** `C:\L2J MOBIUS IA\L2J_Mobius_CT_2.6_HighFive`
> **UPSTREAM:** NO tocado · **Notebook:** `C:\L2J MOBIUS IA\L2J Notebook`

Leyenda: **VERIFIED** | **INFERRED** | **PROPOSED** | **NOT FOUND**

## 1. Método (no destructivo)

Lecturas por `read_files`; listados vía `cmd/dir/forfiles/findstr` con salida a
`C:\Windows\Temp\` (fuera del proyecto); hashes con `certutil`; inspección
read-only de procesos y puertos. **Cero** commit/reset/clean/checkout/delete/
build/deploy/runtime. No se inició ni reinició ningún servidor; no se tocó la DB.

## 2. Notebook — estado (VERIFIED)

- `investigations/BOTAI-P10_RUNTIME_GROUP_HEAL_PARTY_VALIDATION.md`
  (7969 B, mtime 15/09/2026 02:17): es el documento **pre-P10-A** —
  "Estado: PARCIAL — pre-validación estática VERIFIED / runtime PENDING".
  **No contiene sección P10-A ni resultados runtime.** Resultados parciales en
  Notebook: NOT FOUND. Evidencia de ejecución en Notebook: NOT FOUND.
- `INDEX.md` (mtime 02:18) lista el doc P10 y los catálogos 06-B/06-C/06-D/06-ACT1;
  **no referencia ningún artefacto P10-A.**
- La task perdida NO escribió en el Notebook (ningún `BOTAI-P10-A*`, ningún
  apéndice, nada posterior a las 02:18 en `investigations`).

## 3. Git del TARGET (VERIFIED)

- El TARGET **NO es un repositorio git** (`.git` ausente en raíz y en todo el
  subárbol; único `.git` del workspace: el del Notebook). No hay branch/HEAD/
  status/diff que auditar → auditoría git del TARGET = **NOT APPLICABLE**.
- `git` CLI **no disponible** en PATH (`where git` vacío; ausente en rutas
  habituales). Aplica regla §12 del proyecto: la verificación se sustituye por
  inventario de archivos por fecha (§4) + contenido + hashes (§5).
- `.git` del Notebook NO auditado (sin CLI; no requerido por esta task).

## 4. Inventario de cambios — TARGET (VERIFIED)

`forfiles /S /D +15/09/2026` sobre TODO el TARGET (lista completa, 22 entradas).
Únicos cambios del 15/09:

| Elemento | mtime | Tamaño |
|---|---|---|
| `game/data/scripts/custom/BotSpikeP10A/BotSpikeP10A.ini` (NUEVO) | 02:38:26 | 28 B |
| `game/data/scripts/custom/BotSpikeP10A/BotSpikeP10A.java` (NUEVO) | 09:38:05 | 10613 B |
| `game/log/java2.log` (run 02:56–03:08) | 09:26:46→rotado | 40669 B |
| `game/log/java1.log` (run ~09:26–09:35) | 09:35:47 | 35599 B |
| `game/log/java0.log` (run 09:39–09:44) | 09:44:46 | 35970 B |
| `game/config/Interface.ini`, `login/config/Interface.ini` (artefacto de arranque) | 02:54 | 426 B c/u |
| `.lck` / `errorN.log` / logs de login de arranque | 02:54–09:44 | — |

- Sin `.class` de BotSpikeP10A en todo `game\` (VERIFIED por
  `dir /s *BotSpikeP10A*`: solo carpeta + `.ini` + `.java`) → el GameServer lo
  compiló en memoria al arrancar; no queda artefacto compilado.
- **Ningún** otro archivo del TARGET cambió el 15/09: cero XML de skills, cero
  skill trees, cero BotProfile/BotPreset/BotProvisioning/BotEquipment, cero
  Player/Party/AutoPlay/AutoUse/PlayerAI/GameClient, cero `AdminBotManager*`
  (sus `.bak` / `.bak.20260912-BOTAI01` / `_p1..p3.txt` preservados e intactos).
- Mtimes de directorios coherentes con creación 02:37–02:48 (`custom/`,
  `BotSpikeP10A/`).

## 5. Spike encontrado — `custom/BotSpikeP10A/` (A + D)

- **Archivo:** `game/data/scripts/custom/BotSpikeP10A/BotSpikeP10A.java`
  (327 líneas, 10613 B, mtime 09:38:05) — **SHA-256:
  `895390163ba4705408ae23648fb85cbd6b25987c43e1148dd56c9482325c9beb`**
  · Gate: `BotSpikeP10A.ini` = `BotSpikeP10AEnabled = True` (**ARMADO**).
- **Qué hace:** extiende `Script` de Mobius; lee el `.ini`; si está habilitado
  programa con `ThreadPool`: setup (+90 s) → cast1 (+195 s) → cast2 (+235 s) →
  teardown (+270 s) → report (+275 s) → watchdog (+300 s). Carga `Player.load`
  de OBSERVER `268483132` (líder GM), CASTER `268483130` (BSBOT01), MEMBER
  `268483131` (BSBOT02); exige `getClient()==null`; `setOnlineStatus(true,false)`
  + `setOfflinePlay(true)`; `spawnMe` en (-96221,243497,-3544) ±60; Party de 3
  con `FINDERS_KEEPERS`; verifica `getKnownSkill(1027)==null`; obtiene
  `SkillData(1027, lvl=15)`; **`_caster.addSkill(_skill, false)`** (runtime-only,
  sin store) y **`removeSkill(_skill,false)`** en teardown; NO usa `storeSkill`;
  NO toca DB directamente; cast con `_caster.doCast(_skill)`; mide HP antes/
  después (CTRL sin cast + CAST real) y deja la interpretación como manual.
- **Cómo debía eliminarse:** el propio javadoc ordena borrar la carpeta
  `game/data/scripts/custom/BotSpikeP10A` + clases compiladas (no existen) —
  eliminación pendiente, riesgo §8.
- Corresponde al P10-A perdido: SÍ. **NO eliminarlo todavía.**

## 6. Evidencia runtime — TRES ejecuciones reales (logs VERIFIED)

Fuentes: `game/log/java{2,1,0}.log` (`[BotSpikeP10A-LAB]`; `revoked
knownSkill(1027)=null (OK)` en los dos runs completos).

**Run 1 — 02:56→03:08 (`java2.log`, versión inicial): ABORT.**
`Player.load` devolvió null (DB/LoginServer no listo). `CAST1/CAST2 skipped`;
`RESULT=D (none) VERDICT=FAIL`.

**Run 2 — 09:31→09:35 (`java1.log`, 2 casts, lvl=1): PASS.**
`clientless ok`; party n=3 líder BSBOT03; temp-grant lvl=1; `doCast issued
casting=true` en ambos; CAST1 1903→1960 / 1832→1915; CAST2 2417→2474 / 782→865;
`RESULT=C (both) VERDICT=PASS`.

**Run 3 — 09:39→09:44 (`java0.log`, CTRL+CAST, lvl=15): COMPLETO, veredicto
manual pendiente.** Party n=3; temp-grant lvl=15 (PARTY/1000/7000/6000);
CTRL9s sin cast Δ=+57/+82; CAST12s Δ=+333/+367 (RAW 628→961, 447→814);
`exceptions=false`; interpretación manual — el Δ-cast neto (×12/9 del control)
sigue positivo en ambos (~+257 caster, ~+258 member), coherente con el PASS del
Run 2 — pero **la task NO dejó escrita ninguna interpretación**, y el PC se
bloqueó tras las 09:44:46 (última escritura de log del día).

**DB (VERIFIED por mtimes + código):** `character_skills.ibd` 14/09/2026 13:49
(sin cambios el 15/09); `characters.ibd` 14/09 14:13; resto de tablas ±09:26
(ruido de arranque). → Cero evidencia de cambio permanente. `_client==null` +
Party real + HP antes/después: VERIFIED. Caso B (AutoUse): NOT FOUND.

## 7. Cambios permanentes — NINGUNO (VERIFIED)

`character_skills`, XML de skills, skill trees, BotProfile/BotPreset/
BotProvisioning/BotEquipment, Player, Party, AutoPlay, AutoUse, PlayerAI,
GameClient: **sin modificaciones** (§4 + §6). `Interface.ini ×2`: artefacto de
arranque, irrelevante.

## 8. Riesgos ante un (re)arranque (INFERRED del código VERIFIED)

1. **Gate ARMADO (`= True`):** cualquier arranque del GameServer re-ejecutará
   el LAB a los +90 s: `Player.load` ×3, spawn, party, `addSkill(1027)`, **HP
   forzados al 40%/20%** + `doCast` real (con MP/posiciones actuales de esos
   personajes). Cleanup previsto: revoca skill, deshace party, decay.
2. **Nada vivo ahora:** ningún `java.exe` de GameServer/LoginServer, ninguna
   escucha en 2106/7777/9014/3306 (el `java.exe` visto a las ~10:0x = language
   server de VS Code, NO el juego). El riesgo es solo ante un futuro arranque.
3. El directorio `BotSpikeP10A` contamina el datapack hasta su limpieza.

## 9. Clasificación

- **A — RECUPERADO:** spike final (§5, hash) + logs de 3 runs (Run 2 PASS,
  Run 3 completo con deltas).
- **B — PARCIAL:** interpretación escrita del Run 3 + volcado a Notebook/B0/06-B.
- **C — PREEXISTENTE:** doc P10 (02:17), catálogos 06-*, `.bak`s de
  `AdminBotManager`, `Tempcompile.bat`/`otai_build`/`TempjarTemp001`/
  `...botai_build02` (12–13/09), resto del datapack.
- **D — RESTO TEMPORAL:** `custom/BotSpikeP10A/` (gate armado) — borrar SOLO
  tras la decisión de §10.
- **E — DESCONOCIDO:** nada.

## 10. Recomendación — CLEAN THEN CONTINUE

Antes de cualquier arranque del GameServer: (1) `BotSpikeP10A.ini → False` (o
borrar `custom/BotSpikeP10A/` tras archivar el hash de §5); (2) arranque limpio
sin líneas `[BotSpikeP10A-LAB]`; (3) redactar el veredicto P10-A desde los logs
existentes (Runs 2–3). **No ejecutar todavía** (solo diagnóstico).

## 11. Resultado final

- **RECOVERED:** spike final + 3 runs (Run 2 PASS; Run 3: CTRL +57/+82,
  CAST +333/+367; skill revocada OK ×2).
- **PARTIAL:** interpretación escrita del Run 3 + volcado a Notebook/B0/06-B.
- **NOT FOUND:** sección P10-A en Notebook; Caso B; artefactos `.class`.
- **TEMPORARY REMAINS:** `custom/BotSpikeP10A/` (gate `True`).
- **RISKS:** re-ejecución automática del LAB ante un arranque; contaminación
  del datapack.
- **RECOMMENDATION:** CLEAN THEN CONTINUE (§10). Sin ejecutar.

**ESTADO: COMPLETADO** · MODO: AUDITORÍA (cero cambios funcionales; único
archivo creado: este informe). Notebook GATE: este informe es el documento
autoritativo de recuperación; la interpretación P10-A deberá volcarse a
`BOTAI-P10_RUNTIME_GROUP_HEAL_PARTY_VALIDATION.md` en la task de continuación.
