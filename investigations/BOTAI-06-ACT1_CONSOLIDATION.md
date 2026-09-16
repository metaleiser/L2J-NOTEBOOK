# BOTAI-06 — ACT-1: Consolidación de la Arquitectura de Bots

> **SPRINT:** BOTAI-06
> **FASE:** ACT-1 — Consolidación de la arquitectura (equipamiento/provisioning)
> **MODO:** ACT
> **Estado:** IMPLEMENTADO · COMPILA (javac 25, 3/3 targets EXIT=0) · RUNTIME PENDIENTE (GM)
> **Evidence:** SOURCE (TARGET) + BUILD
> **Vigencia:** CURRENT
> **Fecha:** 2026-09-14
> **Baseline:** L2J Mobius CT 2.6 HighFive @ `e2518ab108` (runtime) · TARGET `C:\L2J MOBIUS IA\L2J_Mobius_CT_2.6_HighFive`
> **Predecesores:** BOTAI-04-ACT_IMPLEMENTATION.md, BOTAI-05.1_REAL_PLAYER_ARCHITECTURE_VERIFICATION.md

---

## 1. Objetivo del ACT

Convertir el equipamiento/provisioning de bots en **una única fuente declarativa
basada en `BotProfile`**, sin cambiar el lifecycle validado, sin tocar el core, sin
implementar Community Board, Dyes/Henna, ni la batería multiclass completa.

Resultado buscado: `AdminBotManager -> BotProfile -> BotProvisioning` como único camino,
con un helper de equipamiento compartido y sin la heurística destructiva
`enchantLevel == 4`.

---

## 2. Criterios de aceptación — estado

| # | Criterio | Estado | Evidencia |
|---|----------|--------|-----------|
| 1 | Existe `BotProfile` como modelo declarativo | ✅ | `BotProfile.java` (datos: id/role/classId/baseClassId/targetLevel/SkillMode/version/grade ladder) |
| 2 | Existe `BotRole` (enum, NO IA) | ✅ | `BotRole.java:23-30` (TANK, HEALER, MAGE, DPS, SUPPORT) |
| 3 | Única fuente declarativa de equipment | ✅ | `BotProfile -> grade ladder -> BotGradeLoadout -> EquipEntry` |
| 4 | `BotPreset`/`BotGradeLoadoutRegistry` absorbidos | ✅ | Ambos eliminados; lógica en `BotPresets` + `BotProfile` |
| 5 | Helper único de equipment | ✅ | `BotEquipment.java` (`equipFromEntries`, `removeEquipmentByItemIds`) |
| 6 | `BotProvisioning` y `BotEquipmentProgression` usan el helper | ✅ | `BotProvisioning.java:122,154`; `BotEquipmentProgression.java:150,153` |
| 7 | YA NO existe la heurística `enchant == 4` | ✅ | `removeBotaiEquipmentOnly()` eliminado; grep `getEnchantLevel() == 4` = 0 en código custom |
| 8 | `BOT_REGISTRY` thread-safe | ✅ | `AdminBotManager.java:67-68` (`ConcurrentHashMap.newKeySet()` / `new ConcurrentHashMap<>()`) |
| 9 | Política de skills unificada | ✅ | `BotProvisioning.java:180-191` + `AdminBotManager.java:154-157` (`false,true,false`) |
| 10 | Perfil DPS/Gladiator sigue funcionando | ✅ (compilación) / RUNTIME PENDIENTE | `BotPresets.DPS_FIGHTER` = mismos IDs BSBOT01 |
| 11 | No existe BotAI/ThinkLoop/combat-loop nuevo | ✅ | No se creó ninguna clase de motor; AutoPlay/AutoUse/PlayerAI nativos intactos |
| 12 | No se modifica el core | ✅ | Solo `data/scripts` (custom). `Player/PlayerAI/Party/AutoPlay/AutoUse` intactos |
| 13 | No se implementa Community Board | ✅ | No tocado |
| 14 | No se implementa Dyes/Henna | ✅ | No tocado (`BotProfile.dyes/henna` sigue FUTURO) |
| 15 | No se implementa la batería multiclass | ✅ | Solo DPS_FIGHTER; TANK/HEALER/MAGE/SUPPORT quedan para BOTAI-06-B |

---

## 3. Arquitectura resultante

```text
AdminBotManager (orquestador, BOT_REGISTRY thread-safe, lifecycle intacto)
    |
    v
BotPresets.DPS_FIGHTER  (BotProfile: classId, baseClassId, targetLevel,
    |                    SkillMode, profileVersion, grade ladder NONE..S)
    v
BotProvisioning.apply(Player, BotProfile)
    |-- BotEquipment.removeEquipmentByItemIds(ids del ladder)   <-- NO enchant==4
    |-- setPlayerClass / setBaseClass
    |-- setLevel / setExp / setSp / vitals
    |-- applySkills(SkillMode)  -> giveAvailableSkills(false,true,false)
    |-- BotEquipment.equipFromEntries(loadout del grade objetivo)
    '-- PlayerVariables: BOT_PROFILE_ID + BOT_PROVISION_VERSION -> saveNow -> store(true)

BotEquipmentProgression (listener nativo ON_PLAYER_LEVEL_CHANGED, sin cambios de registro)
    |-- AdminBotManager.isBot(oid)
    |-- resolveProfile(player)  (BOT_PROFILE_ID, fallback DPS_FIGHTER)
    |-- BotProfile.resolveGrade(old/new)
    |-- BotEquipment.removeEquipmentByItemIds(ids del grade ANTERIOR)
    |-- BotEquipment.equipFromEntries(loadout del grade NUEVO)
    '-- PlayerVariables: BOT_EQUIP_PROGRESSION = "profileId:GRADE:v2"
```

**Principio preservado:** LA IA DECIDE (rol/perfil); MOBIUS EJECUTA (AutoPlay/AutoUse/PlayerAI/Party).

---

## 4. Archivos CREADOS (todos en `game/data/scripts/handlers/chat/commands/admin/`)

| Archivo | Rol | Notas |
|---------|-----|-------|
| `BotRole.java` | Enum de roles (TANK/HEALER/MAGE/DPS/SUPPORT) | Datos de selección. **NO es una IA.** |
| `EquipEntry.java` | Entrada de equipo (itemId, enchant, equip) | Extraída de `BotPreset.EquipEntry` a clase top-level |
| `BotProfile.java` | Modelo declarativo central | id, role, classId, baseClassId, targetLevel, SkillMode, profileVersion, grade ladder; `resolveGrade`, `getLoadout`, `getAllItemIds`, `getItemIdsForGrade` |
| `BotEquipment.java` | Helper único de equipamiento | `equipFromEntries(List<EquipEntry>)` + `removeEquipmentByItemIds(Set<Integer>)` |
| `BotPresets.java` | Fuente declarativa única (factory) | `DPS_FIGHTER` con ladder completa NONE→S; `getById`, `all` |

## 5. Archivos MODIFICADOS

| Archivo | Cambio |
|---------|--------|
| `BotGradeLoadout.java` | Refactor: 13 campos individuales → `List<EquipEntry>` + `getItemIds()`. **Mismos IDs, sin cambios de ítems.** |
| `BotProvisioning.java` | Firma `apply(Player, BotPreset)` → `apply(Player, BotProfile)`. Limpieza por IDs del ladder (ya no "destroy ALL items"). Skill policy por `SkillMode`. Persiste `BOT_PROFILE_ID`. Gate de versión 2→3. |
| `BotEquipmentProgression.java` | Consume `BotProfile` (vía `BOT_PROFILE_ID` con fallback). Usa `BotEquipment` para remover/aplicar. **Eliminado `removeBotaiEquipmentOnly()` (heurística enchant==4).** Marker `v1` → `v2` e incluye `profileId`. |
| `AdminBotManager.java` | `BOT_REGISTRY`/`BOT_SESSIONS` thread-safe. `BotProvisions.apply(bot, BotPresets.DPS_FIGHTER)`. `syncBotLevel` alineado a la política unificada. Iteraciones defensivas con copia. |
| `game/data/scripts/compile.ps1` | Añadido target `BotEquipmentProgression.java` al chequeo de compilación |

## 6. Archivos ELIMINADOS (absorbidos)

| Archivo | Absorbido por |
|---------|---------------|
| `BotPreset.java` | `BotProfile` + `BotPresets` + `EquipEntry` |
| `BotGradeLoadoutRegistry.java` | `BotPresets` (ladder) + `BotProfile.Grade`/`resolveGrade` |

Verificación de ausencia de residuos: `dir` de `admin\*.class` **no** lista
`BotPreset.class` ni `BotGradeLoadoutRegistry.class` (no hay clases obsoletas que
puedan shadow-ear los fuentes nuevos).

## 7. Backups / evidencia PRESERVADA

- `AdminBotManager.java.bak` — intacto (no modificado).
- `AdminBotManager.java.bak.20260912-BOTAI01` — intacto (no modificado).
- `AdminBotManager_p1/p2/p3.txt`, `create_p*.ps1`, `fix.ps1`, `.class` — intactos (cleanup diferido).

---

## 8. DECISIONES TOMADAS

### D1 — Política de skills unificada (requisito §10 del ACT)
**Problema detectado:** `AdminBotManager.syncBotLevel` usaba
`giveAvailableSkills(true, true, true)` mientras `BotProvisioning` usaba
`giveAvailableSkills(false, true, false)`.

**Decisión:** política única = `SkillMode.AUTO_BY_CLASS` →
`giveAvailableSkills(false, true, false)`.

**Justificación:** coincide con el diseño ya documentado (BOTAI-04_PROVISIONING_DESIGN §5;
`BotPreset.SkillMode` ya lo describía), es **idempotente** (salta skills conocidas),
**determinista** e **independiente** del flag global `PlayerConfig.AUTO_LEARN_SKILLS`
(que no estaba verificada su configuración en runtime). `includeByFs=false` e
`includeRequiredItems=false` no afectan a Gladiator (sin skills FS ni de items).

**Aplicación:** `BotProvisioning.applySkills(...)` (fuente de la política) **y**
`AdminBotManager.syncBotLevel` (mismo call). En el flujo de summon, `syncBotLevel`
corre antes y `BotProvisioning` después, por lo que el estado final de skills ya lo
determinaba `BotProvisioning`; el alineamiento no altera el resultado final de BSBOT01.

### D2 — Extracción de equipamiento SIN heurística de enchant (requisito §4 del ACT)
**Problema:** la limpieza previa destruía **cualquier** ítem equipado con
`enchantLevel == 4` (riesgo real de destruir ítems ajenos al sistema de bots).

**Decisión:** la eliminación se basa **exclusivamente** en los **IDs del loadout**
(`BotProfile.getItemIdsForGrade` / `getAllItemIds` → `BotEquipment.removeEquipmentByItemIds`).
Ambas ramas (provisioning y progresión) usan el mismo helper. Idempotente
(conjunto vacío = no-op).

### D3 — Contrato de `getLoadout` (no devuelve null)
`EnumMap.put` prohíbe valores `null`, por lo que `Grade.NONE` y grades PENDING se
representan por **ausencia** en el mapa. `getLoadout(grade)` devuelve entonces un
`BotGradeLoadout` **vacío** ("EMPTY") en lugar de `null`. Los llamadores comprueban
`getEquipment().isEmpty()` (lista vacía = 0 iteraciones = no equipa nada), lo que
**preserva el comportamiento** previo de "grade sin loadout → skip".

### D4 — `profileVersion` 3 con gate de migración 2→3
`DPS_FIGHTER.profileVersion = 3`. El gate de idempotencia acepta el marker **2**
(versión BOTAI-04 ya persistida en BSBOT01) además del 3, para forzar **una** corrida de
re-provisioning que persiste `BOT_PROFILE_ID` (sin ella, `BotEquipmentProgression` no
podría resolver el perfil). El re-provisioning **re-aplica el mismo set validado**
(mismos IDs), por lo que es inocuo; luego el gate vuelve a ser estricto (`marker == version`).

### D5 — Thread-safety del registro (requisito §9 del ACT)
`BOT_REGISTRY` = `ConcurrentHashMap.newKeySet()`; `BOT_SESSIONS` = `ConcurrentHashMap`.
Motivo: `isBot()` es consultado desde hilos de listeners nativos
(`ON_PLAYER_LEVEL_CHANGED`) concurrentes con summon/dismiss. Semántica sin cambios
(identidad de bots activos por objectId). Se añadieron iteraciones sobre copia
(`new HashSet<>(...)`) en los bucles que mutan el set.

---

## 9. Comportamiento CONSERVADO

- Lifecycle de summon/dismiss **sin cambios** (`teardownBot`, `//bot off`, party, autosave).
- `//bot <1..8> | status | off | resetcd` sin cambios de semántica.
- Guard BSBOT01-only (`botId == 268483130`) **preservado**; no se convirtió a multiclass.
- Listener nativo `@RegisterEvent(ON_PLAYER_LEVEL_CHANGED)` + `@RegisterType(GLOBAL_PLAYERS)` sin cambios.
- `BotSession` sin cambios.
- Idempotencia por `PlayerVariables` mantenida (`BOT_PROVISION_VERSION` / `BOT_EQUIP_PROGRESSION`).
- **IDs de equipamiento idénticos** a lo ya validado (no se inventaron ítems ni sets).

**Ladder preservada (sin cambios de IDs):**

| Grade | Nivel | Ítems |
|-------|-------|-------|
| NONE | <20 | (sin loadout → skip) |
| D | 20-39 | 3 (Broadsword), 352, 2378, 2411 |
| C | 40-51 | 301 (Scorpion), 356, 2414 |
| B | 52-60 | 2600 (Raid Sword*Caliburs), 2376, 2379, 2415, 5714, 5730 |
| A | 61-75 | 5706 (Damascus*Damascus), 365, 388, 512, 5765, 5777, 852×2, 902×2 |
| S | 76+ | 6580 (Tallum Blade*Dark Legion's Edge), 365, 388, 512, 5765, 5777, 858×2, 889×2, 920 |

> Nota: el grade S usa el set **validado de BSBOT01** (Dark Crystal + Tallum + Tateossian),
> no el Imperial Crusader del antiguo `BotGradeLoadoutRegistry`, tal como exige el ACT
> ("conservarse el equipamiento actualmente validado de BSBOT01"). Los grades D/C/B/A
> se trasladaron **fielmente** del registry (sin cambios).

## 10. Comportamiento NUEVO

1. **Limpieza por IDs** en lugar de wipe total y en lugar de `enchant==4`.
2. **`BOT_PROFILE_ID`** persistido en `PlayerVariables` → la progresión resuelve el perfil.
3. **Marker de progresión v2** con formato `profileId:GRADE:v2` (v1 legacy se re-evalúa).
4. **Registro thread-safe**.
5. **Fallback legacy**: un bot sin `BOT_PROFILE_ID` resuelve `DPS_FIGHTER` (no rompe bots
   provisionados antes de este ACT; el siguiente `apply` escribe el ID real).

---

## 11. Pruebas ejecutadas

| # | Prueba | Comando / método | Resultado |
|---|--------|------------------|-----------|
| T1 | Compilación de `AdminBotManager` (+ BotRole, EquipEntry, BotGradeLoadout, BotProfile, BotEquipment, BotPresets, BotProvisioning, BotSession) | `javac 25 -encoding UTF-8 -cp libs/GameServer.jar -sourcepath scripts` | **EXIT=0** |
| T2 | Compilación del script `BotEquipmentProgression` | idem | **EXIT=0** |
| T3 | Compilación de `MasterHandler` (registro de handlers) | idem | **EXIT=0** |
| T4 | Ausencia de clases obsoletas | `dir /b ...\admin\*.class` | **No** aparecen `BotPreset.class` ni `BotGradeLoadoutRegistry.class` |
| T5 | Ausencia de la heurística destructiva | grep `getEnchantLevel() == 4` en custom | 0 coincidencias |
| T6 | Ausencia de referencias residuales | grep `BotPreset`/`BotGradeLoadoutRegistry` en `.java` del TARGET | 0 en código (solo docs históricos) |
| T7 | Ausencia de motor paralelo | grep `class BotAI`/`ThinkLoop`/`new GameClient` en custom | 0 |

**Log:** `game/data/scripts/compile_botai06.log`.

**NO ejecutado (fuera de alcance / no relacionado con los cambios):** arranque/reinicio
de GameServer, batería BOTAI-05, pruebas de visibilidad, `//bot` en runtime, cambios de
nivel en vivo, DB.

---

## 12. Riesgos restantes

| # | Riesgo | Severidad | Mitigación / Nota |
|---|--------|-----------|-------------------|
| R1 | Corrida de re-provisioning 2→3 en el primer `//bot 1` | BAJA | Re-aplica el **mismo** set validado; idempotente después |
| R2 | Ítems no-ladder (129 Sword of Revolution, 1061 pociones) **no** se eliminan | BAJA | Decisión explícita: no wipe. El 129 equipado se reemplaza nativamente al equipar 6580 (lrhand); las pociones quedan para AutoUse |
| R3 | Grade PENDING → loadout EMPTY → skip | BAJA | Comportamiento idéntico al `null` previo |
| R4 | Runtime sin validar (compilación ≠ ejecución) | MEDIA | Requiere `//bot 1` del GM + verificación de equipo/nivel/skills |
| R5 | Clases `.class` de verificación dejadas en `data/scripts` | BAJA | Cleanup diferido (ACT §12). No shadowean: no existen clases de fuentes borradas |
| R6 | `getClient()` externo server-side (Cat C de BOTAI-05.1) | BAJA | Sin cambios de lifecycle; ya aceptado por BOTAI-05.1 |
---

## 13. Pendiente para BOTAI-06-B (NO hecho aquí)

1. Perfiles TANK / HEALER / MAGE / SUPPORT (nuevas constantes en `BotPresets`).
2. Asignación **por slot** en lugar del guard BSBOT01-only (`BOT_IDS[] -> BotProfile[]`).
3. `AdminBotManager.hire(slot, profile)` (generalización del lifecycle) — solo si sigue siendo necesaria.
4. Usar efectivamente `BotRole` para seleccionar el perfil desde el slot.
5. (Futuro, investigación específica) `BotProfile.dyes/henna` — **NO** confundir con
   `face/hairStyle/hairColor` (apariencia). Los dyes/henna objetivo son los que afectan **stats**.

## 14. Artefactos temporales creados por esta ACT

| Archivo | Motivo | Acción pendiente |
|---------|--------|------------------|
| `game/data/scripts/compile_botai06.bat` | Helper de compilación | Remover en el cleanup diferido (ACT §12) |
| `game/data/scripts/cb.bat` | Helper de compilación (nombre corto por límite de terminal) | Remover en el cleanup diferido |
| `game/data/scripts/compile_botai06.log` | Evidencia del build (T1-T4) | Conservar como evidencia o remover en cleanup |

> **Nota de fricción (entorno):** la terminal integrada de VS Code sufrió crashes de
> PSReadLine (`ArgumentOutOfRangeException`) con comandos largos; se mitigó ejecutando
> `javac` desde batch con salida redirigida a archivo. No afecta al código ni al TARGET.

---

## 15. NOTEBOOK GATE (evaluación §18)

- **Conocimiento reutilizable:** SÍ → decisiones arquitectónicas (D1-D5) y claims con anclas.
- **Duplicación:** no existía documento de BOTAI-06; se crea este informe.
- **Actualizaciones:** `CLAIMS.md` (CL-0029..CL-0033) e `INDEX.md` (entrada de navegación).
- **Duda registrada:** si el fallback legacy de `BotEquipmentProgression` deba eliminarse
  cuando todos los bots tengan `BOT_PROFILE_ID` (candidato a limpieza futura).

## 16. Reporte final

**SPRINT:** BOTAI-06 · **FASE:** ACT-1 · **MODO:** ACT
**ESTADO:** COMPLETADO (código + compilación) — RUNTIME PENDIENTE (GM)

- Core modificado: **NO**
- UPSTREAM modificado: **NO**
- DB modificada: **NO**
- Backups alterados: **NO**
- Community Board / Dyes / Henna / multiclass: **NO implementados** (fuera de alcance)
- Motor nuevo (BotAI/ThinkLoop/GameClient): **NO creado**

**FIN DEL DOCUMENTO BOTAI-06-ACT1**

