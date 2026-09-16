# BOTAI-03-B — Provisioning, Lifecycle, Clientless y Persistencia de Bots

> **SPRINT:** BOTAI-03-B
> **FASE:** Investigación de Provisioning, Lifecycle, Clientless y Persistencia
> **MODO:** ACT — DOCUMENTACIÓN ÚNICAMENTE
> **Estado:** DOCUMENTO CREADO Y VERIFICADO · **Evidence:** SOURCE · **Vigencia:** CURRENT
> **Fecha:** 2026-09-12
> **Investigaciones relacionadas:** BOTAI-03_PROVISIONING_EQUIPMENT_ENCHANT_DASHBOARD_RESEARCH.md, FASE4_02_PLAYER_CLIENTLESS.md, FASE4_17_CLIENTLESS_VISIBILITY.md, FASE4_18_GAMECLIENT_DETACHED.md

---

## 1. Resumen ejecutivo

El source de `L2J_Mobius_CT_2.6_HighFive` contiene las APIs necesarias para cargar, preparar, equipar, encantar, configurar clase/skills, persistir y recargar un `Player` existente (BSBOT01–08) como bot clientless, sin escribir `BotProvisioning` todavía y sin modificar el core.

La ruta correcta ya está probada por el motor de Offline Play:

`Player.load → restore → setOnlineStatus → spawnMe → AutoPlay/AutoUse`

y por el flujo real de cambio de clase:

`setPlayerClass → giveAvailableSkills → store`

**Conclusión fundamental:**

**SÍ**, existe evidencia suficiente para diseñar `BotProvisioning`.

Sin embargo, quedan experimentos runtime que deben cerrarse antes de implementarlo.

---

## 2. Estado de investigación

| Estado | Definición |
|--------|------------|
| **VERIFIED** | Confirmado directamente mediante código fuente, archivo, línea o ejecución |
| **INFERRED** | Deducción razonable basada en evidencia existente |
| **PROPOSED** | Diseño propuesto todavía no implementado/verificado |
| **EXPERIMENTAL** | Prueba temporal por validar |
| **NOT FOUND** | API hipotética que no existe en el código disponible |
| **REFUTED** | Conclusión previa refutada por el source |

Estados adicionales aplicables:


---

## 3. Fuentes revisadas

### Source interno (VERIFIED)

| Archivo | APIs/Mecanismos |
|---------|-----------------|
| `entity/actor/Player.java` | `load`, `restore`, `store`, `storeMe`, `storeCharBase`, `autoSave`, `addItem`, `useEquippableItem`, `setPlayerClass`, `rewardSkills`, `restoreSkills` |
| `entity/actor/stat/PlayerStat.java` | `addExpAndSp`, `setExp`, `setLevel`, `setSp` |
| `taskmanagers/PlayerAutoSaveTaskManager.java` | `add`, `run`, `remove` |
| `mechanics/variables/PlayerVariables.java` | `set`, `storeMe`, `saveNow` |
| `mechanics/variables/AbstractVariables.java` | Base de variables asíncronas |
| `data/sql/OfflinePlayTable.java` | `restoreOfflinePlayers()` |
| `item/enchant/EnchantScroll.java` | `calculateSuccess()` (Ruta A) |
| `managers/ItemManager.java` | `createItem()` |
| `itemcontainer/Inventory.java` | `equipItemAndRecord`, `equipItem` |

### Proyectos externos (NOT_FOUND / UNVERIFIED)

| Proyecto | Estado |
|----------|--------|
| L2jRoboto | NOT_FOUND en repositorio |
| DanielBarion | NOT_FOUND en repositorio |
| SmartBot | NOT_FOUND en repositorio |
| Autopilot | NOT_FOUND en repositorio |

**REGLA DE AUTORIDAD:** Los proyectos externos permanecen como NOT_FOUND / UNVERIFIED respecto del source disponible y NO deben presentarse como autoridad sobre nuestro Mobius.

### Documentación Notebook relacionada

| Documento | Relación |
|-----------|----------|
| `BOTAI-03_PROVISIONING_EQUIPMENT_ENCHANT_DASHBOARD_RESEARCH.md` | Enchant/Equipment/Dashboard |
| `FASE4_02_PLAYER_CLIENTLESS.md` | Player sin GameClient |
| `FASE4_17_CLIENTLESS_VISIBILITY.md` | Visibilidad sin cliente |
| `FASE4_18_GAMECLIENT_DETACHED.md` | GameClient detached REFUTED |
| `FASE4_17_PLAYER_CLIENT_STATE.md` | Estado de _client tras load |
| `BOT_RECIPE.md` | Documentación de referencia general |

| Estado | Definición |
|--------|------------|
| **CURRENT** | Vigente, sin refutar |
| **HISTORICAL** | Valido históricamente pero superado |
| **SUPERSEDED** | Reemplazado por investigación posterior |
| **PENDING_REVERIFICATION** | Requiere nueva verificación |

**Reglas de conservación:**
- NO convertir PROPOSED o INFERRED en VERIFIED.
- NO convertir UNVERIFIED en VERIFIED.
- `isOnline()` → UNVERIFIED / PROBABLE
- `isOfflinePlay()` → UNVERIFIED / PROBABLE

---

## 4. Player.load / restore — VERIFIED

`Player.load(int)` delega en `restore(int)`.

`restore(objectId)` construye el Player y restaura desde la tabla `characters`:

* classId/baseClass;
* experiencia;
* nivel;
* SP;
* karma;
* fama;
* PvP/PK;
* posición;
* clan;
* hero;
* noble;
* subclases;
* vitality;
* HP/MP/CP.

También restaura:

* inventario;
* warehouse;
* freight;
* skills;
* macros;
* shortcuts;
* henna;
* bookmarks;
* recipes;
* otros estados persistentes.

### Importante

`load/restore` NO:

* asigna `_client`;
* ejecuta `spawnMe`;
* mete al Player en party;
* arranca AutoPlay;
* arranca AutoUse;
* establece OfflinePlay.

Esos pasos pertenecen al llamador.

**Conclusión:** `Player.load()` por sí solo no deja al bot autónomo ni completamente presente en el mundo.

---

## 5. Player clientless — VERIFIED con matices

Después de `load`:

* `_client == null`;
* `isOnlineInt()` requiere `_client != null`;
* `isInOfflineMode()` considera `_client == null`;
* `sendPacket` es null-safe.

`broadcastCharInfo()` realiza early-return cuando `isOnlineInt()==0`.

La lógica server-side de equipamiento continúa ejecutándose aunque el cliente sea null.

### APIs VERIFIED (FASE4_02)

| API | Comportamiento con _client==null |
|-----|----------------------------------|
| `getClient()` | Retorna null |
| `sendPacket()` | No-op (null-safe) |
| `isOnlineInt()` | Retorna 0 |
| `isInOfflineMode()` | Retorna true |
| `broadcastCharInfo()` | Early-return |
| `broadcastPacket()` | SÍ envía a observadores |

### APIs NO completamente verificadas

| API | Estado |
|-----|--------|
| `isOnline()` | UNVERIFIED / PROBABLE |
| `isOfflinePlay()` | UNVERIFIED / PROBABLE |
| `setOfflinePlay()` | UNVERIFIED / PROBABLE |

**NO convertir estas tres APIs en VERIFIED.**

- `setOfflinePlay()` → UNVERIFIED / PROBABLE


---

## 14. Reload — PROPOSED / EXPERIMENTAL

Prueba pendiente:

```text
LOAD
→ PREPARE
→ SAVE
→ UNLOAD
→ LOAD AGAIN
→ VERIFY
```

Verificar:

* classId;
* level;
* XP/SP;
* skills;
* inventory;
* enchant;
* equipped items;
* variables;
* posición;
* HP/MP/CP.

El estado runtime (`_client`, `_isOnline`, OfflinePlay, AutoPlay, AutoUse) debe reconstruirse al iniciar. No se consideran datos persistentes equivalentes al equipamiento o nivel.

---

## 15. BSBOT01–08 — PROPOSED

Los ocho bots existentes pueden ser preparados individualmente mediante presets.

### Datos comunes

* nivel objetivo;
* tier de equipo;
* enchant base;
* configuración general de AutoUse.

### Datos individuales

* charId;
* classId;
* rol;
* equipo;
* skills específicas.

Evitar hardcodear esta información dentro de `AdminBotManager`.

---

## 16. BotSession vs BotProvisioning — PROPOSED

### BotProvisioning

Responsabilidad:

* clase;
* nivel;
* XP/SP;
* skills;
* inventario;
* equipo;
* enchant;
* marca de provisioning;
* persistencia.

No debe conocer:

* combate;
* targeting;
* party runtime.

### BotSession

Responsabilidad:

* lifecycle;
* load;
* spawn;
* OfflinePlay;
* Party;
* AutoPlay;
* AutoUse;
* shutdown.

No debe conocer:

* qué equipo entregar;
* qué clase asignar;
* qué rol desempeñar.

### BotRole

Responsabilidad: definir el rol.

### BotBehavior

Responsabilidad: decisiones de alto nivel.

---

## 17. Matriz de APIs

| Área | API/Mecanismo | Estado | Clientless | Persistente | Trabajo futuro |
|------|---------------|--------|------------|-------------|----------------|
| Load | `Player.load` → `restore` | VERIFIED | Sí | Lee DB | Ninguno |
| Class | `setPlayerClass` | VERIFIED | Sí | Sí | Wrapper provisioning |
| Level | `setLevel` | VERIFIED | Sí | Sí | Política |
| XP/SP | `addExpAndSp` / `setExp` / `setSp` | VERIFIED | Sí | Sí | Política |
| Skills | `rewardSkills` / `giveAvailableSkills` | VERIFIED | Sí | Sí | Selección por rol |
| Inventory | `addItem` | VERIFIED | Sí | Sí | Presets |
| Equipment | `useEquippableItem` | VERIFIED | Sí | Sí | Orden |
| Enchant | `addItem(...enchant...)` / `setEnchantLevel` | VERIFIED | Sí | Sí | Política |
| Variables | `PlayerVariables` | VERIFIED | Sí | Sí | Marca provisioning |
| Save | `store` / `autoSave` | VERIFIED | Sí | Sí | Forzar store |
| OfflinePlay | patrón `OfflinePlayTable` | VERIFIED | Sí | No runtime | Lifecycle |
| AutoPlay | `AutoPlayTaskManager` | VERIFIED | Sí | No | Rearmar |
| AutoUse | `AutoUseTaskManager` | VERIFIED | Sí | No | Rearmar |

---

## 18. APIs VERIFIED

* `Player.load`
* `Player.restore`
* `Player.store`
* `Player.storeMe`
* `Player.storeCharBase`
* `Player.autoSave`
* `Player.setPlayerClass`
* `Player.rewardSkills`
* `Player.giveAvailableSkills`
* `Player.restoreSkills`
* `Player.addItem(... enchantLevel ...)`
* `Player.useEquippableItem`
* `Inventory.equipItemAndRecord`
* `Item.setEnchantLevel`
* `EnchantScroll.calculateSuccess`
* `PlayerStat.addExpAndSp`
* `PlayerStat.setExp`
* `PlayerStat.setLevel`
* `PlayerStat.setSp`
* `PlayerVariables.set`
* `PlayerVariables.storeMe`
* `PlayerVariables.saveNow`
* `PlayerAutoSaveTaskManager.add`
* `PlayerAutoSaveTaskManager.run`

---

## 19. APIs NOT_FOUND o NO VERIFICADAS

### REFUTED / NOT_FOUND

* `TemporaryPlayer`
* constructor offline de `GameClient`
* helper nativo específico de "auto-enchant"
* código verificable de L2jRoboto
* código verificable de DanielBarion
* código verificable de SmartBot
* código verificable de Autopilot

### UNVERIFIED / PROBABLE

* cuerpo exacto de `isOnline()`
* cuerpo exacto de `isOfflinePlay()`
* cuerpo exacto de `setOfflinePlay()`

**No elevar estos tres elementos a VERIFIED.**


---

## 20. Riesgos

| # | Riesgo | Severidad |
|---|--------|-----------|
| 1 | Cambios no guardados antes del shutdown pueden perderse | ALTA |
| 2 | `PlayerVariables` tiene guardado asíncrono | MEDIA |
| 3 | Visibilidad dinámica de un Player clientless necesita prueba runtime | MEDIA |
| 4 | `setPlayerClass()` programa operaciones posteriores | MEDIA |
| 5 | Usos server-side de `getClient()` deben revisarse | MEDIA |
| 6 | Comportamiento exacto de `isOnline()` debe confirmarse antes de depender del autosave | ALTA |

---

## 21. Preguntas abiertas

| # | Pregunta |
|---|----------|
| 1 | ¿`isOnline()` devuelve `_isOnline` puro? |
| 2 | ¿El bot es visible dinámicamente al humano después de acercarse? |
| 3 | ¿Cómo deben definirse los presets? |
| 4 | ¿Preset externo o `PlayerVariables`? |
| 5 | ¿Nivelado mediante `setLevel + exp` o `addExpAndSp`? |
| 6 | ¿Qué operaciones exactas requieren esperar las tareas programadas por `setPlayerClass()`? |
| 7 | ¿Qué usos server-side de `getClient()` pueden afectar a bots clientless? |
| 8 | ¿El ciclo completo LOAD → PREPARE → SAVE → RELOAD funciona correctamente en runtime? |

---

## 22. Flujo recomendado — PROPOSED

### BotProvisioning.provision(charId, preset)

```text
Player.load(charId)
→ verificar estado de provisioning
→ setPlayerClass(preset.classId)
→ nivel / XP / SP
→ skills si corresponde
→ addItem(... enchantLevel ...)
→ useEquippableItem(...)
→ PlayerVariables (marca)
→ saveNow()
→ store(true)
→ PREPARED
```

### BotSession lifecycle (post-provisioning)

```text
load
→ setOnlineStatus
→ spawnMe
→ setOfflinePlay
→ setOnlineStatus
→ restoreEffects
→ setRunning
→ joinParty
→ AutoPlay
→ AutoUse
```

---

## 23. Qué reutilizar

* `Player.load/store/autoSave`
* `setPlayerClass`
* `rewardSkills`
* `giveAvailableSkills`
* `addItem(... enchant ...)`
* `useEquippableItem`
* `PlayerStat`
* `PlayerVariables`
* `PlayerAutoSaveTaskManager`
* patrón `OfflinePlayTable`
* `AutoPlay`
* `AutoUse`
* `Party`

---

## 24. Qué NO reutilizar

* FakePlayer/NPC como base
* TemporaryPlayer
* GameClient artificial
* ThinkLoop paralelo
* scheduler propio de combate
* nueva máquina de combate
* enchant probabilístico para provisioning
* modificaciones del core
* modificaciones de UPSTREAM

---

## 25. Arquitectura candidata

```text
BotManager
    ↓
BotProvisioning
    ↓
BotSession
    ↓
BotRole
    ↓
BotBehavior
    ↓
Party + AutoPlay + AutoUse + PlayerAI
    ↓
Mobius
```

**Principio:**

> LA IA DECIDE; MOBIUS EJECUTA.

---

## 26. Experimentos pendientes — EXPERIMENTAL

**NO ejecutar estos experimentos durante esta tarea:**

| # | Experimento |
|---|-------------|
| 1 | LOAD → PREPARE → SAVE → UNLOAD → LOAD → VERIFY sobre BSBOT01 |
| 2 | Confirmar visibilidad del bot al acercarse el humano |
| 3 | Confirmar cuerpo de `isOnline()` y comportamiento de autosave |
| 4 | Revisar usos server-side de `getClient()` |

---

## 27. Recomendación para siguiente sprint

La investigación indica que existe evidencia suficiente para especificar `BotProvisioning`.

Sin embargo, antes de implementarlo se recomienda cerrar los experimentos pendientes, especialmente:

1. ciclo real de reload de BSBOT01;
2. confirmación de `isOnline()` / autosave.

Después de esos experimentos podrá redactarse una especificación cerrada para `BotProvisioning`.


---

## 28. Reporte final

**SPRINT:** BOTAI-03-B

**FASE:** Investigación de Provisioning, Lifecycle, Clientless y Persistencia

**MODO:** ACT — DOCUMENTACIÓN ÚNICAMENTE

**ESTADO:** DOCUMENTO CREADO Y VERIFICADO

**DOCUMENTO:**
`C:\L2J MOBIUS IA\L2J Notebook\investigations\BOTAI-03-B_PROVISIONING_LIFECYCLE_CLIENTLESS_PERSISTENCE.md`

**PRODUCCIÓN MODIFICADA:** NO

**UPSTREAM MODIFICADO:** NO

**L2J-RECIPE MODIFICADO:** NO

**AdminBotManager.java MODIFICADO:** NO

**BotSession.java MODIFICADO:** NO

**ARCHIVOS CREADOS:**
- `C:\L2J MOBIUS IA\L2J Notebook\investigations\BOTAI-03-B_PROVISIONING_LIFECYCLE_CLIENTLESS_PERSISTENCE.md`

**ARCHIVOS MODIFICADOS:**
- NINGUNO

**ARCHIVOS ELIMINADOS:**
- NINGUNO

**IMPLEMENTACIÓN REALIZADA:**
- NO

**BOTAI-03-C INICIADO:**
- NO

**BOTAI-04 INICIADO:**
- NO

**VERIFICACIONES:**
- documento existe;
- documento contiene las secciones requeridas;
- clasificaciones VERIFIED/PROPOSED/UNVERIFIED/NOT_FOUND preservadas;
- no se modificó producción;
- no se modificó UPSTREAM;
- no se modificó L2J-RECIPE;
- no se implementó BotProvisioning;
- no se inició ningún sprint posterior.

**ESTADO FINAL:**
BOTAI-03-B DOCUMENTADO Y CERRADO.

---

**FIN DEL DOCUMENTO BOTAI-03-B**
