# BOTAI-04-B3 — Level → Equipment → Skills → Party Progression

**SPRINT:** BOTAI-04-B3
**FASE:** Investigación — Progresión automática de bots
**MODO:** PLAN — INVESTIGACIÓN
**TARGET:** L2J_Mobius_CT_2.6_HighFive
**ESTADO:** COMPLETED — NO IMPLEMENTADO

> Autoridad: source real (repo indexado `metaleiser/L2J`, carpeta `L2J_Mobius_CT_2.6_HighFive`).
> Los IDs concretos de items del datapack (`data/stats/items`, `data/stats/armorsets`)
> NO están indexados aquí → se marcan PENDING (grep local previo al ACT).

---

## Executive Summary

**¿Es posible?** SÍ, con arquitectura híbrida (Mobius nativo + capa declarativa propia).

**Lo que Mobius YA da (VERIFIED):**
- Hook de subida de nivel: `OnPlayerLevelChanged` disparado en `PlayerStat.addLevel()`.
- Recompensa de skills por clase/nivel: `rewardSkills()` → `giveAvailableSkills()` /
  `giveAvailableAutoGetSkills()`, ya llamado automáticamente dentro de `addLevel()`.
- Sistema de grade/crystal: `CrystalType` (NONE,D,C,B,A,S,S80,S84), `ItemGrade`,
  `ConditionPlayerGrade`, `getExpertiseLevel()`, `refreshExpertisePenalty()`.
- APIs de equipar/crear con enchant: `useEquippableItem`, `equipItemAndRecord`,
  `addItem` + `setEnchantLevel`.
- Party completa (addPartyMember, máx 9, PartyDistributionType, XP compartida).

**Lo que Mobius NO da (VERIFIED / NOT_FOUND):**
- NO existe `EquipmentProgression`, `GearTable`, `LoadoutByLevel`, `RecommendedEquipment`.
- NO existe "level → grade" nativo (solo lo hay implícito vía expertise penalty).
- NO existe "class + grade → item" ni "armor type + grade → set".
- `ArmorSet` NO expone getters de piezas individuales (ya verificado en B1/B2).
- `InitialEquipmentData` es SOLO equipo de creación (nivel 1), no progresivo.

**Conclusión:** la DECISIÓN de qué equipar por nivel/clase/grado es capa propia.
La EJECUCIÓN (equipar, enchant, skills, grade penalty) es 100% nativa reutilizable.
Recomendación: **ARQUITECTURA 3 (híbrida)** — tabla declarativa + APIs nativas +
listener `OnPlayerLevelChanged` filtrado solo a bots.

---

## 1. Current Mobius Capabilities

| Capacidad | Existe nativo | Evidencia | Estado |
|-----------|---------------|-----------|--------|
| Level-up hook (evento) | SÍ | `PlayerStat.addLevel` → `OnPlayerLevelChanged` | VERIFIED |
| Skills por clase/nivel | SÍ | `rewardSkills`/`giveAvailableSkills` | VERIFIED |
| Grade / CrystalType | SÍ | `CrystalType`, `ItemGrade` | VERIFIED |
| Expertise (penalty por grado) | SÍ | `refreshExpertisePenalty`, `getExpertiseLevel` | VERIFIED |
| Equipar item + enchant | SÍ | `useEquippableItem`, `setEnchantLevel` | VERIFIED |
| Party | SÍ | `Party.addPartyMember` | VERIFIED |
| level → grade automático | NO | (búsqueda sin resultado) | NOT_FOUND |
| class/grade → item | NO | (búsqueda sin resultado) | NOT_FOUND |
| equipment progression engine | NO | (búsqueda sin resultado) | NOT_FOUND |

---

## 2. Level → Grade

### 2.1 Cómo maneja Mobius el grade (VERIFIED)
El grade NO se guarda "por personaje". Se deriva del equipo equipado vía
`CrystalType` de cada item + el nivel de expertise del jugador.

`CrystalType` — niveles enteros por grado:
`NONE=0, D=1, C=2, B=3, A=4, S=5, S80=6, S84=7`.
```
L2J_Mobius_CT_2.6_HighFive/.../entity/item/type/CrystalType.java
```

`getExpertiseLevel()` determina qué grado puede usar el jugador SIN penalización;
si el `crystaltype` del item equipado supera el expertise, aplica penalty skills
(`WEAPON_GRADE_PENALTY` / `ARMOR_GRADE_PENALTY`).
```
L2J_Mobius_CT_2.6_HighFive/.../entity/actor/Player.java :2175-2247  refreshExpertisePenalty()
```

### 2.2 ¿Hay función nativa "nivel → grade"? NO (NOT_FOUND)
No existe un método que devuelva "el grade máximo apropiado para el nivel X".
La relación nivel→expertise existe indirectamente vía skills de Expertise
(`CommonSkill.EXPERTISE`, aprendidas por skill tree a ciertos niveles), pero no hay
API pública tipo `getGradeForLevel(int)`.

### 2.3 Conclusión
La tabla nivel→grade (1-19 NONE, 20-39 D, 40-51 C, 52-60 B, 61-75 A, 76-79 S,
80-83 S80, 84+ S84) es **conocimiento externo de L2 High Five (INFERRED)**, no
verificable en source, y debe ser una **tabla propia**. Se puede validar en runtime
comparando contra el expertise real que otorga el skill tree (PENDING).

- Estado: nivel→grade como tabla propia → **PROPOSED**
- CrystalType usable directamente → **VERIFIED**

---

## 3. Level-Up Hook  (LO MÁS IMPORTANTE — VERIFIED)

### 3.1 Punto exacto
Todo cambio de nivel pasa por `PlayerStat.addLevel(byte value)`. Ahí, ANTES de
aplicar el nivel, se dispara el evento asíncrono si hay listeners:
```
L2J_Mobius_CT_2.6_HighFive/.../entity/actor/stat/PlayerStat.java :260-273
    if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYER_LEVEL_CHANGED, player))
        EventDispatcher.getInstance().notifyEventAsync(
            new OnPlayerLevelChanged(player, getLevel(), getLevel()+value), player);
```
Evento y tipo:
```
.../mechanics/events/holders/actor/player/OnPlayerLevelChanged.java   (getPlayer/getOldLevel/getNewLevel)
.../mechanics/events/EventType.java :217   ON_PLAYER_LEVEL_CHANGED(OnPlayerLevelChanged.class, void.class)
```
Cadena real de `addLevel()` tras subir: `super.addLevel` → `rewardSkills()` →
`refreshExpertisePenalty()` → `updateUserInfo()`.
```
.../entity/actor/stat/PlayerStat.java :292-342
```

### 3.2 Cuál es el mejor hook y cómo limitarlo a bots
El patrón nativo de scripts es registrar un listener anotado
(`AnnotationEventListener`) sobre `ON_PLAYER_LEVEL_CHANGED`. Dentro del callback,
filtrar por bot (p.ej. `PlayerVariables` marca de bot o pertenencia al `BotManager`)
y salir temprano para jugadores humanos. Esto NO modifica `PlayerStat`.

- Riesgo: el listener es global a todos los players → **mitigación: guard `isBot(player)`
  como primera línea** (INFERRED, patrón estándar del EventDispatcher).
- Nota: el evento se dispara con `getLevel()` (viejo) y `getLevel()+value` (nuevo),
  útil para detectar cruce de threshold. (VERIFIED)

- Estado: hook nativo disponible → **VERIFIED / CURRENT**
- Estado: filtrado a bots vía guard en listener → **PROPOSED**

---

## 4. Equipment Selection

### 4.1 ¿Existe class+level+grade+type+role → item nativo? NO (NOT_FOUND)
No hay API ni tabla. Único "equipment by class" nativo = `InitialEquipmentData`
(equipo de creación, nivel 1) usado en `CharacterCreate` (addItem→equipItem).
No es progresivo.

### 4.2 Comparación de estrategias
- **A) lista manual por bot/preset** — es lo actual (`BotPreset`). No escala a 8+ clases.
- **B) tabla class/role + grade → itemIds** — mantenible; el núcleo de la solución.
- **C) tabla armor type + grade → set** — reutilizable entre clases del mismo type.
- **D) tabla weapon type + grade → weapon** — reutilizable por type.
- **E) combinación B+C+D** — **RECOMENDADA**: máxima reutilización, mínimo hardcode.
- **F) mecanismo nativo** — **NO existe (NOT_FOUND)**.

**Recomendación:** E (B+C+D declarativo). El "role/class" mapea a (armorType, weaponType);
grade se deriva del nivel (tabla §2); las tablas type+grade resuelven IDs concretos.

- Estado: E como diseño → **PROPOSED**

---

## 5. Armor

`ArmorSetData` carga sets desde `data/stats/armorsets` pero su holder `ArmorSet`
**no expone getters de piezas individuales** (solo `getChestId()` y validación
`containAll`), ya verificado en B1/B2. Por tanto NO se puede "pedir el set completo
por grade" en runtime.
```
.../data/xml/ArmorSetData.java  (load parseDatapackDirectory "data/stats/armorsets")
.../data/holders/ArmorSet.java :237-240  getChestId()  (sin getters de piezas)
```
Slots de paperdoll a cubrir (5 piezas de armor + head): head, chest, legs, gloves, feet.
```
.../entity/itemcontainer/Inventory.java :80-105  PAPERDOLL_HEAD/CHEST/LEGS/GLOVES/FEET
```

- Estado: piezas del set deben declararse en tabla propia → **PROPOSED**
- Estado: `ArmorSet` no da piezas → **VERIFIED**

---

## 6. Weapons

`WeaponType` (con `isDual()`, DUAL/DUALFIST) permite clasificar armas, verificado en B2.
No hay tabla nativa "grade + weaponType → weaponId" (NOT_FOUND).
Gladiator=DUAL, Warlord=POLE, Tyrant=FIST, Tank=SWORD+SHIELD, Mage=STAFF/BLUNT,
Healer=BLUNT es **conocimiento de diseño (INFERRED)**, debe ser tabla propia.

- Estado: WeaponType consultable → **VERIFIED**
- Estado: tabla grade+type→weapon → **PROPOSED**

---

## 7. Jewelry

5 slots distintos (necklace, 2 earrings, 2 rings) — confirma por qué en B1 faltaban
earring/ring: son slots separados del paperdoll.
```
.../entity/itemcontainer/Inventory.java :84-94
    PAPERDOLL_NECK=4, PAPERDOLL_REAR=8, PAPERDOLL_LEAR=9,
    PAPERDOLL_RFINGER=13, PAPERDOLL_LFINGER=14
```
No hay "grade → jewelry set" nativo (NOT_FOUND). Tabla propia grade→{neck,2xearring,2xring}.
Evitar duplicados: para 2 earrings y 2 rings del mismo id hay que crear DOS items
distintos y equiparlos en R y L; `equipItem`/`useEquippableItem` colocan por BodyPart.

- Estado: slots verificados → **VERIFIED**
- Estado: tabla jewelry por grade → **PROPOSED**

---

## 8. Cloak / Accessories (slots reales en High Five — VERIFIED)

Slots existentes en esta versión (de `Inventory` + `BodyPart`):
```
.../entity/itemcontainer/Inventory.java :95-105
    PAPERDOLL_LBRACELET=15, PAPERDOLL_RBRACELET=16,
    PAPERDOLL_DECO1..DECO6=17..22, PAPERDOLL_CLOAK=23, PAPERDOLL_BELT=24
.../entity/item/enums/BodyPart.java :53,62-65  BACK(cloak), R/L_BRACELET, DECO(talisman), BELT
```
- Cloak (BACK), Belt, Bracelet (R/L), Talismans (DECO1-6) EXISTEN.
- `getTalismanSlots()` controla cuántos talismanes admite; cloak requiere `canEquipCloak()`.
- Brooch/agathion de versiones posteriores NO aplican aquí (no asumir).

**Recomendación:** dejar cloak/belt/bracelet/talisman FUERA de la primera versión
(tienen requisitos/condiciones adicionales); incorporarlos en fase posterior.

- Estado: slots existen → **VERIFIED**
- Estado: excluir de V1 → **PROPOSED**

---

## 9. Skills (VERIFIED — reutilización directa)

`addLevel()` ya llama `rewardSkills()` automáticamente en cada subida.
```
.../entity/actor/Player.java :2688-2730  rewardSkills()
    if (AUTO_LEARN_SKILLS) giveAvailableSkills(...); else giveAvailableAutoGetSkills();
.../entity/actor/Player.java :2799-2804  giveAvailableSkills(...) via SkillTreeData.getAllAvailableSkills
```
Comportamiento por caso:
- **Cambia nivel:** `rewardSkills()` corre solo → skills al día. (VERIFIED)
- **Cambia clase:** `setPlayerClass()` también llama `rewardSkills()`. (VERIFIED)
  ```
  .../entity/actor/Player.java :2424-2519  setPlayerClass → setClassTemplate → rewardSkills
  ```
- **Cambia subclase:** `setActiveClass` maneja skills; flujo VillageMaster. (VERIFIED)
- **Bot creado directo en nivel alto:** basta llamar `giveAvailableSkills(...)` /
  `rewardSkills()` una vez tras fijar nivel+clase. (INFERRED, misma API)
- **Bot subido artificialmente:** al usar `addLevel/addExpAndSp`, `rewardSkills()` corre. (VERIFIED)

Mecanismo más seguro: fijar clase → fijar nivel/exp → `rewardSkills()`. Con
`AUTO_LEARN_SKILLS=true` no hace falta enumerar skills.

- Estado: skills por clase/nivel sin hardcode → **VERIFIED / CURRENT**

---

## 10. Skill Enchant

No se halló mecanismo nativo simple de auto-enchant de skills (confirmado B2, NOT_FOUND
de un helper). Existen datos de enchant de skill pero la ejecución server-side sería propia.
**Recomendación V1: opción A (no usar skill enchant)**; posponer a
`SkillProgressionProfile` (opción C) en fase posterior.

- Estado: sin auto-enchant nativo → **NOT_FOUND**
- Estado: excluir de V1 → **PROPOSED**

---

## 11. Bot From Level 1

Flujo (qué es nativo / qué es propio):
```
crear/summon bot (Player.load, patrón OfflinePlay) ....... NATIVO (B1)
nivel 1 + clase inicial ................................. NATIVO
No Grade (equipo inicial) ............................... NATIVO InitialEquipmentData O tabla propia grade=NONE
skills iniciales ........................................ NATIVO rewardSkills
sube a 20 → listener OnPlayerLevelChanged ............... NATIVO evento / PROPIO handler
  → cruza threshold D → aplicar loadout D ............... PROPIO (tabla + useEquippableItem)
cambio de clase a 20/40 ................................. NATIVO setPlayerClass (manual o quest)
```
Partes propias: detección de threshold en el listener + selección de loadout + equipar.
Partes nativas: nivel, exp, skills, expertise, equip API.

- Estado: viable desde nivel 1 → **INFERRED (componentes VERIFIED)**

---

## 12. Bot Spawned Directly at High Level

```
summon bot → setLevel/addExpAndSp a 80 → setPlayerClass(GLADIATOR/DUELIST)
→ rewardSkills → aplicar loadout S80/S84 (tabla) → equip → store(true) → joinParty
```
La MISMA capa (tabla grade + aplicador de loadout) sirve para ambos casos: en vez de
reaccionar a un evento, se invoca `applyCurrentProgression(bot)` una vez tras fijar
nivel/clase. → **UNA sola arquitectura** para "subida progresiva" y "spawn directo".