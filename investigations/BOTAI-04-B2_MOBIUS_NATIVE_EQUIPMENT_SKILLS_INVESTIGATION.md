# BOTAI-04-B2 — Mobius Native Equipment & Skills Investigation

## 1. Metadata
- SPRINT: BOTAI-04-B2
- FASE: Equipment / Skills / Character Progression
- MODO: PLAN — INVESTIGACIÓN
- TARGET: L2J_Mobius_CT_2.6_HighFive (repo indexado: metaleiser/L2J)
- Autoridad de evidencia: SOURCE actual > config/XML > referencias internas > externo
- Nota de alcance: los IDs concretos de items y los XML del datapack (data/stats/items,
  data/stats/armorsets, data/EnchantSkillGroups.xml, skill trees XML) NO están indexados;
  se citan las clases Java que los cargan, pero los valores concretos quedan PENDING de grep local.

## 2. Objetivo
Determinar qué sistemas NATIVOS de Mobius pueden reutilizarse para equipment, skills,
skill enchant y progresión de bots basados en clase + nivel + grado + rol, y qué debe
programarse como capa propia.

## 3. Alcance
Grade/expertise, restricciones de equipamiento, automatización de equipamiento, selección
de arma/armadura/joyería/cloak, skill learning, skill trees, skill enchant, FakePlayer,
AI de combate. NO se implementa nada.

## 4. Resumen ejecutivo
- **Grade/Expertise: VERIFIED.** Mobius modela grado con `CrystalType` (NONE..S84) e
  `ItemGrade`. La relación "qué grado PUEDE usar el personaje" existe vía
  `getExpertiseLevel()` + `refreshExpertisePenalty()` + `ConditionPlayerGrade`. Es
  **restricción/penalización**, NO recomendación.
- **Restricción de equipamiento: VERIFIED.** `ItemTemplate.checkCondition()` +
  `Inventory.equipItem()` aplican todas las condiciones (clase, grado, hero, etc.).
- **Auto-equipment por clase/nivel/grado: NOT_FOUND.** No existe un motor que decida
  y equipe automáticamente el loadout apropiado por nivel. Lo único nativo es
  `InitialEquipmentData` (equipo de creación de personaje, nivel 1) — no progresivo.
- **Selección de arma/armadura/joyería/cloak: NOT_FOUND (como decisión automática).**
  Existen los datos (WeaponType, ArmorSet, CrystalType) pero no una función
  "recomiéndame el arma de esta clase a este nivel".
- **Skill learning por clase+nivel: VERIFIED.** `SkillTreeData.getAllAvailableSkills(...)`
  + `Player.giveAvailableSkills(...)` + `rewardSkills()` dan exactamente las skills que un
  personaje de esa clase/nivel debería tener. Reutilizable directamente.
- **Skill enchant: VERIFIED (datos y rutas).** `EnchantSkillGroupsData` + `EnchantSkillLearn`
  + `EnchantSkillGroup.EnchantSkillHolder`. Pero NO hay helper "auto-enchant a +X"; el flujo
  nativo es interactivo por packet.
- **FakePlayer: VERIFIED como NPC decorativo.** Es un `Npc`, no un `Player`; equipamiento
  estático vía `FakePlayerHolder`/`FakePlayerInfo`; combate mínimo vía `AttackableAI`.
  NO sirve como base para bots-jugador (ya descartado en fases previas, se reconfirma).
- **Combat AI / skill usage reutilizable como referencia: PARTIAL.** `AttackableAI` tiene
  heal/res/buff/debuff/target logic, pero está acoplado a `Attackable`, no a `Player`.

## 5. Grade / Level / Expertise

### 5.1 CrystalType (VERIFIED / CURRENT)
- Archivo: `java/org/l2jmobius/gameserver/entity/item/type/CrystalType.java`
- Enum: `NONE(0)`, `D(1)`, `C(2)`, `B(3)`, `A(4)`, `S(5)`, `S80(6)`, `S84(7)`.
- Cada item tiene un crystal type; `getLevel()` da el nivel de grado.
- Helpers: `isGreater(CrystalType)`, `isLesser(CrystalType)`.
- Reutilizable: SÍ, para razonar sobre grado de cualquier item.

### 5.2 ItemGrade (VERIFIED)
- Archivo: `java/org/l2jmobius/gameserver/entity/item/enums/ItemGrade.java`
- Enum: `NONE, D, C, B, A, S`. `valueOf(CrystalType)` mapea S/S80/S84 → S.

### 5.3 Expertise del personaje (VERIFIED)
- `Player.getExpertiseLevel()` existe (1 referencia en Player.java; usado por
  `ConditionPlayerGrade` y `refreshExpertisePenalty`). Representa el máximo grado que el
  personaje puede portar sin penalización (depende del nivel/skills de expertise).
- `Player.refreshExpertisePenalty()` (Player.java ~2175-2247): recorre items equipados,
  compara `item.getTemplate().getCrystalType().getLevel()` contra `getExpertiseLevel()`,
  y si el item es de grado superior aplica `WEAPON_GRADE_PENALTY` / `ARMOR_GRADE_PENALTY`
  (skills de penalización). Solo activo si `PlayerConfig.EXPERTISE_PENALTY`.
- `ConditionPlayerGrade` (mechanics/conditions/ConditionPlayerGrade.java):
  `testImpl` → `_value == (byte) player.getExpertiseLevel()`.
- Conclusión clave: **expertise responde "¿PUEDE usar este grado?" (restricción),
  no "¿QUÉ grado le corresponde comprar/equipar?" (recomendación).**

## 6. Equipment Restrictions (VERIFIED)
- `ItemTemplate.checkCondition(Creature, WorldObject, boolean)` (ItemTemplate.java 816-884):
  aplica GM restriction, oly restriction, hero items, y todas las `_preConditions`
  (incluye condiciones de clase/grado/nivel). Devuelve false y envía SystemMessage si falla.
- `Inventory.equipItem(Item)` (Inventory.java 1263+): valida store mode, hero item, formal
  wear, body part. `equipItemAndRecord(Item)` envuelve y registra cambios.
- `Player.useEquippableItem(Item, boolean)` (Player.java 2249-2356): flujo completo de
  equipar/desequipar con mensajes, `refreshExpertisePenalty()`, `broadcastUserInfo()`,
  InventoryUpdate. Si no cumple condición → `YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM`.
- Reutilizable: SÍ. Es exactamente lo que BotProvisioning ya usa para equipar de forma segura.

## 7. Equipment Automation

### 7.1 Lo único nativo (VERIFIED, pero limitado)
- `InitialEquipmentData` (data/xml/InitialEquipmentData.java): equipo inicial POR CLASE.
- `CharacterCreate.java` (300-317): al crear personaje hace
  `getClassEquipment(newChar.getPlayerClass())` → por cada `InitialEquipment`:
  `inventory.addItem(...)` y si `isEquipable() && isEquipped()` → `inventory.equipItem(item)`.
- Limitación: es equipamiento de **nivel 1 de creación**, NO progresivo por nivel/grado.

### 7.2 Motor de progresión por nivel/grado (NOT_FOUND)
- No existe ninguna clase que, dado (clase, nivel), devuelva el loadout apropiado (arma D/C/B/A/S,
  set correspondiente, joyería). No hay "EquipmentProgression", "GearTable", "LoadoutByLevel".
- Conclusión: **debe construirse como capa propia** (nuestro preset), apoyándose en las APIs
  nativas de validar (checkCondition) y equipar (useEquippableItem / equipItemAndRecord).

## 8. Weapon Selection
- `WeaponType` incluye `DUAL`, `DUALFIST` (dual swords del Gladiator) con helper `isDual()`.
- No existe función nativa "¿qué arma para Gladiator lvl80/84?". La elección Icarus/Vesper/etc.
  es **decisión de diseño**, no dato derivable del source.
- Estado: datos VERIFIED; selección automática NOT_FOUND.

## 9. Armor Selection
- `ArmorSet` (data/holders/ArmorSet.java) + `ArmorSetData` (data/xml/ArmorSetData.java,
  carga `data/stats/armorsets`). `ArmorSet` está **indexado por chestId** y tiene
  `containAll(...)`, `getChestId()`, pero **NO expone getters públicos de cada pieza**
  (legs/head/gloves/feet individuales). Confirmado en fase B1.
- Consecuencia: no se puede "derivar el set completo desde el chest" en runtime con la API
  pública; las 5+ piezas deben declararse explícitamente en el preset.
- Tipo de armadura por clase (heavy/light/robe): lo determinan las condiciones del item
  (`checkCondition`), no hay una tabla "clase→tipo de armadura" reutilizable directamente.
- Estado: estructura VERIFIED; resolución automática de set NOT_FOUND.

## 10. Jewelry / Cloak / Accessories
- Slots de paperdoll (Inventory.java 84-94): `PAPERDOLL_NECK=4`, `PAPERDOLL_REAR=8`,
  `PAPERDOLL_LEAR=9`, `PAPERDOLL_RFINGER=13`, `PAPERDOLL_LFINGER=14`. Esto explica por qué
  faltaban un segundo earring y un segundo ring: son **slots distintos** (2 earrings, 2 rings).
- Cloak/belt/bracelet/deco: existen slots en el paperdoll y en `FakePlayerInfo` se ve
  `equipCloak`, belt, bracelets, deco. No hay lógica automática de "qué joyería/cloak por nivel".
- Estado: slots VERIFIED; selección automática NOT_FOUND.

## 11. Skill Learning (VERIFIED — el hallazgo más importante de este sprint)
- `Player.rewardSkills()` (Player.java 2688-2730): si `PlayerConfig.AUTO_LEARN_SKILLS`
  → `giveAvailableSkills(AUTO_LEARN_FS_SKILLS, true, AUTO_LEARN_SKILLS_WITHOUT_ITEMS)`;
  si no → `giveAvailableAutoGetSkills()`.
- `Player.giveAvailableSkills(includeByFs, includeAutoGet, includeRequiredItems)`
  (Player.java 2799-2842): obtiene
  `SkillTreeData.getInstance().getAllAvailableSkills(this, getPlayerClass(), ...)` y hace
  `addSkill(skill, false)` + `storeSkills(...)`. **Esto entrega exactamente las skills que
  un personaje de esa clase y nivel debería tener.**
- `rewardSkills()` se dispara nativamente en:
  - `PlayerStat.addLevel(...)` (PlayerStat.java 293) — al subir de nivel.
  - `Player.setPlayerClass(id)` (Player.java 2503) — al cambiar de clase.
- Config: `AUTO_LEARN_SKILLS`, `AUTO_LEARN_FS_SKILLS`, `AUTO_LEARN_SKILLS_WITHOUT_ITEMS`
  en `PlayerConfig.java` (valores concretos del TARGET = PENDING: leer .ini del dist).
- **Reutilización para bots:** llamar `bot.giveAvailableSkills(true, true, true)` (o
  `rewardSkills()`) tras fijar clase y nivel entrega el kit de skills completo sin importar
  el valor de config. Firma exacta VERIFIED.

## 12. Skill Trees (VERIFIED)
- `SkillTreeData` (data/xml/SkillTreeData.java): fuente de skills por clase.
  - `getAllAvailableSkills(Player, PlayerClass, includeByFs, includeAutoGet, includeRequiredItems)`
    → `Collection<Skill>` (usado por `giveAvailableSkills`).
  - `getAvailableSkills(player, class, false, false)` → `Collection<SkillLearn>` (usado por
    `Folk.showSkillList` — pantalla de aprendizaje del NPC).
  - `getAvailableAutoGetSkills(player)`, `getCompleteClassSkillTree(class)`,
    `getTransferSkillTree(class)`, `getMinLevelForNewSkill(...)`, fishing/collect trees.
- Ejemplos de uso: `Folk.showSkillList` (Folk.java 79-175), `Fisherman.showFishSkillList`.
- Reutilizable: SÍ, directamente.

## 13. Skill Enchant (VERIFIED datos; sin helper de auto-enchant)
- `EnchantSkillGroupsData` (data/xml/EnchantSkillGroupsData.java): carga
  `data/EnchantSkillGroups.xml`; mantiene `_enchantSkillGroups` y `_enchantSkillTrees`.
  Constantes: `NORMAL_ENCHANT_BOOK=6622`, `SAFE_ENCHANT_BOOK=9627`, `CHANGE_ENCHANT_BOOK=9626`,
  `UNTRAIN_ENCHANT_BOOK=9625`. `getSkillEnchantmentBySkillId(id)`.
- `EnchantSkillLearn` (mechanics/skill/holders/EnchantSkillLearn.java): rutas de enchant.
  `getEnchantRoute(level)=level/100`, `getEnchantIndex(level)=(level%100)-1`,
  `isMaxEnchant(level)`, `getEnchantSkillHolder(level)`. **Skill enchantada = nivel >100**
  (route*100 + subnivel).
- `EnchantSkillGroup.EnchantSkillHolder`: `getLevel`, `getSpCost`, `getExpCost`, `getAdenaCost`,
  `getRate(player)` (0 si `player.getLevel()<76`).
- Requisitos de uso nativo (RequestExEnchantSkillRouteChange.java): 3ª clase completada,
  nivel ≥ 76, off-battle. Flujo nativo es **interactivo por packet**, no server-side batch.
- **No hay** helper "enchantSkill(skill, +X)" server-side. Para bots habría que fijar el nivel
  de skill enchantado directamente (`getSkill(id, enchantedLevel)` + `addSkill`) apoyándose en
  `EnchantSkillLearn` para calcular niveles válidos → **capa propia mínima**.

## 14. FakePlayer / Bot Systems (VERIFIED)
- FakePlayer en Mobius es un **NPC**, no un Player:
  - `FakePlayerHolder` (entity/actor/holders/npc/FakePlayerHolder.java): datos estáticos
    (classId, equipHead/RHand/LHand/Gloves/Chest/Legs/Feet/Cloak, weaponEnchantLevel,
    armorEnchantLevel, etc.) leídos de StatSet de NPC.
  - `FakePlayerInfo` (network/serverpackets/FakePlayerInfo.java): packet que "pinta" el NPC
    como si fuera un jugador con ese equipo. **El equipo NO está en un inventario real; son
    IDs decorativos del holder.**
  - `isFakePlayer()` vive en `Npc`/`WorldObject`, `FakePlayerData`, `FakePlayersConfig`.
- Combate del FakePlayer: `AttackableAI.thinkActive()` (AttackableAI.java 494-543) — aggro,
  pickup de drops, running. Es AI de NPC atacable, no de jugador.
- Conclusión: **FakePlayer = personaje equipado visualmente, NO personaje con IA de jugador
  ni inventario/skills reales.** Reconfirma la decisión previa (FASE 4) de NO usarlo como base.

## 15. Combat / AI Systems Found (PARTIAL, referencia)
- `AttackableAI` contiene lógica rica reutilizable como REFERENCIA (no copiable tal cual
  porque opera sobre `Attackable`):
  - Heal a líder/party (AttackableAI.java 1068-1107, condición por % HP).
  - Resurrección de clan/party (1204-1238).
  - Buffs continuos / debuffs / sleep / ataques mágicos (cast(Skill), 1401+, 1616+, 1809+).
  - Selección short/long range skill (1243-1272), `checkSkillCastConditions`, `tryCast`.
  - `notifyActionThink()` (2268-2326): switch ACTIVE/ATTACK/CAST.
- Para bots-Player, el motor de ejecución ya decidido es **AutoPlay/AutoUse** (FASE 4), no
  AttackableAI. Este bloque solo sirve como patrón de reglas de rol.

## 16. Reusable Native Components
| Componente | Uso para bots | Estado |
|---|---|---|
| `CrystalType` / `ItemGrade` | razonar grado de items | VERIFIED |
| `Player.getExpertiseLevel()` | grado máximo usable sin penalización | VERIFIED |
| `refreshExpertisePenalty()` | validar penalización tras equipar | VERIFIED |
| `ItemTemplate.checkCondition()` | ¿puede equipar este item? | VERIFIED |
| `useEquippableItem` / `equipItemAndRecord` | equipar de forma segura | VERIFIED |
| `Player.addItem(...enchantLevel...)` + `setEnchantLevel` | crear item con enchant | VERIFIED |
| `SkillTreeData.getAllAvailableSkills` | skills por clase+nivel | VERIFIED |
| `Player.giveAvailableSkills / rewardSkills` | aprender kit completo | VERIFIED |
| `EnchantSkillGroupsData` / `EnchantSkillLearn` | datos de rutas de enchant | VERIFIED |
| `InitialEquipmentData` | patrón addItem→equip (solo lvl1) | VERIFIED (limitado) |
| `AttackableAI` | referencia de reglas de rol | PARTIAL |

## 17. Evidence Matrix
| Capacidad | Existe en Mobius | Archivo | Clase/Método | Reutilizable | Observaciones |
|---|---|---|---|---|---|
| Grade detection | VERIFIED | entity/item/type/CrystalType.java | CrystalType.getLevel | SÍ | grado del item |
| Expertise | VERIFIED | entity/actor/Player.java | getExpertiseLevel / refreshExpertisePenalty | SÍ | grado usable = restricción |
| Equipment restriction | VERIFIED | entity/item/ItemTemplate.java | checkCondition | SÍ | condiciones clase/grado |
| Auto equipment | PARTIAL | data/xml/InitialEquipmentData.java | getClassEquipment | Parcial | solo creación lvl1 |
| Weapon selection | NOT_FOUND | entity/item/type/WeaponType.java | isDual (solo datos) | NO | decisión de diseño |
| Armor selection | NOT_FOUND | data/holders/ArmorSet.java | (sin getters de piezas) | NO | preset explícito |
| Jewelry selection | NOT_FOUND | itemcontainer/Inventory.java | PAPERDOLL_* (solo slots) | NO | 2 ear + 2 ring |
| Cloak selection | NOT_FOUND | — | — | NO | sin lógica por nivel |
| Skill learning | VERIFIED | entity/actor/Player.java | giveAvailableSkills / rewardSkills | SÍ | por clase+nivel |
| Skill tree | VERIFIED | data/xml/SkillTreeData.java | getAllAvailableSkills | SÍ | fuente de skills |
| Skill enchant | PARTIAL | data/xml/EnchantSkillGroupsData.java | EnchantSkillLearn | Parcial | datos sí, auto-enchant no |
| FakePlayer equipment | VERIFIED | actor/holders/npc/FakePlayerHolder.java | FakePlayerHolder | NO (base) | decorativo NPC |
| FakePlayer skills | NOT_FOUND | — | — | NO | NPC sin skills de jugador |
| Combat skills | PARTIAL | ai/AttackableAI.java | cast/tryCast/thinkAttack | Referencia | acoplado a Attackable |
| Targeting | PARTIAL | ai/AttackableAI.java | targetReconsider | Referencia | NPC-based |
| Follow | VERIFIED (fase prev) | ai/AbstractAI.java | startFollow | SÍ | vía AutoPlay/AI |
| Party | VERIFIED (fase prev) | entity/groups/Party.java | addPartyMember/joinParty | SÍ | ya confirmado |

## 18. Contradictions / Unexpected Findings
- Contradicción con la intuición "expertise recomienda grado": FALSO. `ConditionPlayerGrade`
  y `refreshExpertisePenalty` demuestran que expertise es **restricción/penalización**, no
  recomendación. No existe "gradoRecomendado(nivel)".
- `ArmorSet` NO expone las piezas del set individualmente (reconfirmado): impide derivar el
  loadout desde el set en runtime → el preset debe listar cada pieza.
- FakePlayer parecía candidato a "personaje equipado" pero es NPC decorativo: no tiene
  inventario ni skills reales.

## 19. What Mobius Already Provides
1. Validación de si un personaje puede usar/equipar un item (grado, clase, condiciones).
2. Sistema de penalización por grado (expertise).
3. Aprendizaje automático de skills por clase+nivel (`getAllAvailableSkills`/`giveAvailableSkills`).
4. Datos completos de rutas de skill enchant.
5. Creación de items con enchant + equip seguro (addItem/setEnchantLevel/useEquippableItem).
6. Patrón de equip por clase (InitialEquipmentData) — como plantilla de código, no de progresión.

## 20. What Mobius Does NOT Provide
1. Motor que decida el loadout apropiado por clase+nivel+grado (arma/armor/joyería/cloak).
2. Selección automática de arma por clase (Icarus/Vesper/etc.).
3. Resolución de set de armadura completo desde el chest.
4. Selección automática de joyería/cloak por nivel.
5. Helper server-side de auto-enchant de skills a +X (el flujo nativo es interactivo).
6. Un "FakePlayer" que sea un Player real con IA/skills (es NPC decorativo).

## 21. Recommended Architecture — Opción D (HÍBRIDO)
```
Mobius native rules  (checkCondition, expertise, SkillTreeData, EnchantSkillLearn)
        +
Nuestra configuración de bot  (BotPreset: qué items/grado/enchant por clase+nivel+rol)
        ↓
Equipment/Skill Profile aplicado por BotProvisioning
```
- Equipment: preset declarativo por (clase, nivel/grado, rol) con IDs verificados; aplicar con
  `addItem(...enchantLevel...)` + `useEquippableItem`. Validar con `checkCondition` y revisar
  `getExpertiseWeaponPenalty/ArmorPenalty` tras equipar (no debe haber penalización).
- Skills: NO hardcodear. Tras fijar clase y nivel, llamar `bot.giveAvailableSkills(true,true,true)`
  (o `rewardSkills()`). Mobius entrega el kit exacto de la clase/nivel.
- Skill enchant: capa mínima propia usando `EnchantSkillLearn` para calcular niveles válidos y
  fijar el skill enchantado (nivel >100) con `SkillData.getSkill(id, lvl)` + `addSkill`.
- Grade "recomendado": tabla propia nivel→grado (D/C/B/A/S/S80/S84) porque no existe nativa;
  usar `CrystalType`/`getExpertiseLevel` solo para VALIDAR, no para decidir.

Por qué D y no A/B/C:
- A (presets manuales puros): funciona pero no escala a 8 clases/roles ni a progresión.
- B (sistema propio total): reinventa validación y skills que ya existen → desperdicio y riesgo.
- C (solo nativo): imposible, no hay motor de loadout ni de recomendación de grado.
- D: reutiliza validación + skill trees + enchant data nativos; solo añade la tabla de
  loadout/grado por clase+nivel+rol, que es lo único que Mobius no provee.

## 22. Recommendation for BOTAI-04-B3
1. Definir `BotPreset` como perfil por (clase, nivel/grado objetivo, rol) con listas explícitas:
   arma (+enchant), 5 piezas de set (+enchant), 5 joyerías (+enchant), cloak opcional.
2. En `BotProvisioning`: crear items con enchant, equipar con `useEquippableItem`, verificar
   ausencia de penalización por expertise, y **derivar skills vía `giveAvailableSkills`** en
   lugar de listas de skills manuales.
3. Añadir tabla propia nivel→grado (única pieza nueva de "reglas") y, si se desea, capa mínima
   de skill-enchant apoyada en `EnchantSkillLearn`.
4. Grep local previo (fuera del index): IDs en data/stats/items y data/stats/armorsets;
   valores de AUTO_LEARN_SKILLS en el .ini del dist.

## 23. Risks
- IDs de items/sets no verificables desde el index → riesgo de ID incorrecto (mitigar con grep local).
- `useEquippableItem`/`broadcastUserInfo` con `_client == null` (bot clientless): la visibilidad
  dinámica sigue UNVERIFIED en runtime (heredado de FASE 4); no bloquea equip, sí puede afectar
  propagación de cambios visuales.
- Skill enchant server-side no tiene helper nativo → capa propia con riesgo de niveles inválidos
  si no se usa `EnchantSkillLearn` para validar.
- `setPlayerClass` agenda tareas a 100ms (applyItemSkills/sendSkillList) → no encadenar store
  inmediatamente tras cambio de clase.

## 24. Open Questions
- Valor real de `AUTO_LEARN_SKILLS` en el TARGET (irrelevante si se llama giveAvailableSkills
  explícitamente, pero conviene documentarlo). PENDING.
- IDs concretos de dual swords/sets/joyería por grado. PENDING (grep datapack local).
- ¿Queremos skill enchant en bots en B3 o diferirlo? Decisión de diseño.

## 25. Final Status
- Grade/Expertise/restricción: VERIFIED.
- Skill learning/trees: VERIFIED (reutilización directa).
- Skill enchant: PARTIAL (datos sí, auto-enchant no).
- Auto-equipment/loadout/selección por nivel: NOT_FOUND (requiere capa propia).
- FakePlayer como base de bot: REFUTED (es NPC decorativo).
- Arquitectura recomendada: Opción D (híbrido).

Respuesta a la pregunta final:
> ¿Podemos aprovechar reglas/sistemas nativos en lugar de duplicar equipment y skills?
- SKILLS: SÍ (VERIFIED) — reutilizar `giveAvailableSkills`/`SkillTreeData` casi elimina trabajo.
- EQUIPMENT: PARCIALMENTE — reutilizar validación/equip nativos, pero la DECISIÓN de qué
  equipar (arma/armor/joyería/cloak/grado por nivel) NO existe en Mobius y es capa propia.
- SKILL ENCHANT: PARCIALMENTE — datos nativos reutilizables, ejecución server-side propia.