# BOTAI-04 — Diseño de BotProvisioning MVP + BotPreset
## L2J Mobius CT 2.6 High Five — BSBOT01 / DPS_FIGHTER

Estado global: DISEÑO (PLAN). No implementar.
Autoridad: SOURCE ACTUAL > RUNTIME/GM-VALIDATED > INVESTIGATIONS > INFERENCIAS.

---

## 1. Resumen ejecutivo

Todas las APIs necesarias para "aplicar un preset a BSBOT01 y dejarlo preparado"
existen y están VERIFIED en el source actual. No se necesita ningún sistema de
configuración nuevo (no XML/JSON/YAML): un **preset como objeto Java inmutable**
(record/enum) es suficiente y es el mecanismo de menor complejidad.

BotProvisioning se puede implementar **sin modificar el core**, componiendo APIs
públicas existentes: `setPlayerClass` / `setBaseClass`, `PlayerStat.setLevel` +
`setExp`, `rewardSkills` / `giveAvailableSkills`, `addItem(...enchantLevel...)`,
`useEquippableItem`, y persistencia con `store(true)` + `PlayerVariables` como marca
de idempotencia.

Veredicto: **READY FOR ACT** (con 2 riesgos controlables, no bloqueantes).

---

## 2. Evidencia source

### Equipment
- `Player.addItem(ItemProcessType process, int itemId, long count, int enchantLevel, WorldObject reference, boolean sendMessage)` crea el item y, si `enchantLevel > -1`, ejecuta `createdItem.setEnchantLevel(enchantLevel)`. **VERIFIED · CURRENT · SOURCE.** (Player.java ~3476, enchant en ~3535-3537.)
- `Player.useEquippableItem(Item item, boolean abortAttack)` equipa vía `_inventory.equipItemAndRecord(item)`; llama `refreshExpertisePenalty()` + `broadcastUserInfo()` + `sendInventoryUpdate`. **VERIFIED · CURRENT.** (Player.java ~2249-2356.)
- `Inventory.equipItemAndRecord(Item)` → `equipItem(Item)` resuelve el paperdoll por BodyPart. **VERIFIED.** (Inventory.java ~1244-1488.)
- `Item.setEnchantLevel(int)` existe y se usa en múltiples flujos (multisell, quest reward). **VERIFIED.**

### Clase / Nivel / XP / SP
- `Player.setPlayerClass(int id)`: cambia clase activa (`setClassTemplate`), llama `rewardSkills()` y agenda a 100ms `applyItemSkills()` + `sendSkillList()`. **VERIFIED · CURRENT.** (Player.java ~2424-2520.)
- `Player.setBaseClass(int)` / `setBaseClass(PlayerClass)`. **VERIFIED.** (Player.java ~1325-1333.)
- `PlayerStat.setLevel(byte)`, `addLevel(byte)` (que llama `rewardSkills`), `addExpAndSp(double,double,boolean)`, `addExp/addSp`. **VERIFIED.** (PlayerStat.java ~120-352, ~506-523.)
- `Player.setExp(long)` → `getStat().setExp(...)`. **VERIFIED.** (Player.java ~2871.)

### Skills
- `Player.rewardSkills()`: si `PlayerConfig.AUTO_LEARN_SKILLS` → `giveAvailableSkills(FS, autoGet, withoutItems)`; si no → `giveAvailableAutoGetSkills()`. **VERIFIED.** (Player.java ~2688.)
- `Player.giveAvailableSkills(boolean includeByFs, boolean includeAutoGet, boolean includeRequiredItems)`: usa `SkillTreeData.getAllAvailableSkills(this, getPlayerClass(), ...)`, salta las ya conocidas (`getKnownSkill`), persiste con `storeSkills`. **Naturalmente idempotente.** **VERIFIED.** (Player.java ~2799-2842.)

### Persistencia
- `Player.store(boolean storeActiveEffects)` (synchronized): `storeCharBase` + `storeCharSub` + `storeEffect` + `storeItemReuseDelay` + `PlayerVariables.storeMe()` + inventory/warehouse/freight `updateDatabase()`. **VERIFIED.** (Player.java ~7605-7634.)
- `Player.storeMe()` = `store(true)`. **VERIFIED.** (~7637.)
- `Player.autoSave()` = `storeMe()` + recommendations. **VERIFIED.** (~8627.)
- `store()` lee las variables vía `getScript(PlayerVariables.class)`. **VERIFIED.** (~7619.)

### PlayerVariables (idempotencia)
- `PlayerVariables extends AbstractVariables`; `set(name,value)` marca cambios; `storeMe()` guarda **asíncrono a 60s**; `saveNow()`/`saveNowSync()` fuerza inmediato; `hasVariable(name)`; tabla `character_variables (charId, var, val)`. **VERIFIED.** (PlayerVariables.java 1-244; AbstractVariables.java 32-231.)


---

## 3. BotPreset

### Mecanismo elegido: **Java object (record/enum)** — PROPOSED
El source no tiene un "sistema de presets" reutilizable para esto. Los datos estáticos
(items, skills, clases) se referencian por **ID entero** en todo el core (itemId,
classId, skillId). Un preset es simplemente un agregado inmutable de esos IDs.

- Opción A (Java objects): **RECOMENDADA**. Sin parsing, sin IO, sin dependencias.
- Opción B (config existente `Config`): no aplica; los `*.Config` son flags globales.
- Opción C (XML): innecesario para 1 preset; añade `IXmlReader` + archivo + carga.
- Opción D (INI): no hay patrón para listas complejas.

Regla aplicada: no inventar sistema de configuración si no es necesario. Para el MVP,
un `enum BotPreset` o `record` basta.

### Esquema mínimo (conceptual, PROPOSED)
```
BotPreset (record inmutable)
├─ name            : String        // "DPS_FIGHTER"
├─ classId         : int           // PlayerClass id destino
├─ level           : int
├─ skillMode       : enum { AUTO_BY_CLASS, EXPLICIT, MIXED }
├─ explicitSkills  : List<int[]>   // opcional (skillId, level)
└─ equipment       : List<EquipEntry>
     EquipEntry ├─ itemId : int
                ├─ enchant: int
                └─ equip  : boolean
```
`weapon/armor/jewelry` NO necesitan tipos separados: todos son `EquipEntry` y el
paperdoll se resuelve solo por `BodyPart` en `equipItem`. **No crear
`BotEquipmentPreset` ni `BotSkillPreset`** — serían clases sin responsabilidad real.

---

## 4. Equipment provisioning — flujo mínimo (PROPOSED sobre APIs VERIFIED)

Por cada `EquipEntry`:
1. `Item item = bot.addItem(ItemProcessType.REWARD, itemId, 1, enchant, null, false);`
   (usar la sobrecarga con `enchantLevel`; `sendMessage=false` → no genera packets.)
2. Si `equip`: `bot.useEquippableItem(item, false);`

Notas VERIFIED:
- `sendPacket(...)` es null-safe con `_client == null` (no-op), por lo que los
  `sendInventoryUpdate` / SystemMessage dentro de `addItem`/`useEquippableItem` no
  lanzan NPE en un bot clientless.
- `useEquippableItem` llama `broadcastUserInfo()`; `broadcastCharInfo()` hace
  early-return si `isOnlineInt()==0` (clientless), pero `updateUserInfo()` es
  null-safe. No rompe; solo no propaga apariencia dinámicamente.

---

## 5. Skill provisioning

Recomendación: **B (automáticas por clase/nivel)** para el MVP DPS_FIGHTER. INFERRED.
- Tras fijar clase y nivel, llamar `bot.giveAvailableSkills(false, true, true)` o
  simplemente `bot.rewardSkills()` (respeta `PlayerConfig.AUTO_LEARN_SKILLS`).
- Es idempotente (salta skills ya conocidas) → seguro re-ejecutar.
- Reservar modo EXPLICIT (skills por ID) para roles futuros con builds específicas.

Dependencia: el resultado depende de `PlayerConfig.AUTO_LEARN_SKILLS` y de
`SkillTreeData`. Verificar el valor de config del server antes del ACT.

---

## 6. Class / Level provisioning

- **Clase:** `setPlayerClass(classId)` cambia la clase activa y ya dispara skills.
  Para un bot de base-class, además `setBaseClass(classId)` para que persista como
  clase base. **PENDING_REVERIFICATION:** si BSBOT01 ya está creado con la clase
  deseada, este paso puede omitirse (solo verificar en DB).
- **Nivel:** `getStat().setLevel((byte) level)`. Recomendado `setLevel` + ajustar exp
  con `setExp(getStat().getExpForLevel(level))` para consistencia. INFERRED.
- Tras cambio de clase/nivel: `setCurrentHpMp(getMaxHp(), getMaxMp())` +
  `setCurrentCp(getMaxCp())` para no dejar el bot con HP/MP viejos. INFERRED.


---

## 14. Riesgos

1. **Duplicación de items** si equipment se re-ejecuta sin guardia de versión.
   Mitigación: guardia `BOT_PROVISION_VERSION` + `store(true)` inmediato. (INFERRED, real.)
2. **Timing de `setPlayerClass`**: agenda `applyItemSkills()`+`sendSkillList()` a 100ms.
   No encadenar `store(true)` demasiado pegado al cambio de clase. (VERIFIED el schedule; impacto INFERRED.)
3. **Config-dependencia de skills**: `rewardSkills` depende de
   `PlayerConfig.AUTO_LEARN_SKILLS`. Verificar valor antes del ACT. (VERIFIED dependencia.)
4. **Visibilidad clientless**: `broadcastUserInfo` no propaga apariencia dinámica con
   `isOnlineInt()==0`. No bloquea provisioning (ya documentado en fases previas).
   (VERIFIED · PENDING_REVERIFICATION runtime.)
5. **BSBOT01 estado DB**: clase/nivel reales sin verificar. (PENDING_REVERIFICATION.)


---

## 18. Reporte final

SPRINT: BOTAI-04
FASE: Diseño BotProvisioning MVP + BotPreset
MODO: PLAN
ESTADO: INVESTIGACIÓN / DISEÑO COMPLETADO

PRODUCCIÓN MODIFICADA: NO
UPSTREAM MODIFICADO: NO
DB MODIFICADA: NO
CÓDIGO CREADO: NO

APIs VERIFIED:
addItem(...,enchantLevel,...), useEquippableItem, Inventory.equipItemAndRecord/equipItem,
Item.setEnchantLevel, setPlayerClass, setBaseClass, rewardSkills, giveAvailableSkills,
giveAvailableAutoGetSkills, PlayerStat.setLevel/addLevel/addExpAndSp/setExp,
store(boolean)/storeMe/autoSave, PlayerVariables.set/hasVariable/getInt/storeMe/saveNow,
getScript(PlayerVariables.class).

APIs NOT_FOUND:
Helper de auto-enchant masivo; sistema de presets reutilizable; firma real de BotSession
(workspace TARGET no indexado).

RIESGOS:
Duplicación de items sin guardia de versión; timing de setPlayerClass (100ms);
config AUTO_LEARN_SKILLS; visibilidad clientless (no bloqueante); estado DB de BSBOT01.

RECOMENDACIÓN:
Implementar (en ACT) BotProvisioning + BotPreset (record/enum) componiendo APIs
existentes, con guardia BOT_PROVISION_VERSION en PlayerVariables y store(true) explícito.
No crear BotEquipmentPreset ni BotSkillPreset. No modificar el core.

VEREDICTO:
READY FOR ACT.
