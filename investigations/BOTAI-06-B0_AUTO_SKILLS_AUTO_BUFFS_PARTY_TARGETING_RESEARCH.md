# BOTAI-06-B0 — AUTO SKILLS / AUTO BUFFS + PARTY HEAL/BUFF TARGETING RESEARCH

> **SPRINT:** BOTAI-06-B0
> **FASE:** Investigación (sin implementación)
> **MODO:** INVESTIGACIÓN + DOCUMENTACIÓN (sin cambios funcionales)
> **Estado:** COMPLETADO (investigación). Ninguna implementación.
> **Fecha:** 2026-09-15
> **TARGET:** `C:\L2J MOBIUS IA\L2J_Mobius_CT_2.6_HighFive` (runtime `libs\GameServer.jar` + datapack `game\data\...`)
> **UPSTREAM:** `C:\L2J MOBIUS\UPSTREAM\L2J_Mobius\L2J_Mobius_CT_2.6_HighFive` (SOLO contraste, lectura)
> **Baseline:** L2J Mobius CT 2.6 HighFive @ e2518ab108 (runtime)
> **Predecesores:** `bots/BOT_RECIPE.md`, `investigations/BOTAI-06-D_SKILL_BUFF_COMBAT_MASTER_CATALOG.md`, `investigations/BOTAI-06-ACT1_CONSOLIDATION.md`, `evidence/spikes/D0001/`

Leyenda de estados: **VERIFIED** | **INFERRED** | **PROPOSED** | **NOT FOUND** | **PENDING**

Convención de evidencia:

* **[TARGET/JAR]** = verificado por desensamblado (`javap -c -p`) o inspección del `libs\GameServer.jar` real del TARGET.
* **[TARGET/DATAPACK]** = leído directamente del código fuente/XML del datapack dentro del TARGET (`game\data\...`).
* **[TARGET/db]** = esquema SQL presente en `db_installer\sql\game\` del TARGET.
* **[UPSTREAM]** = leído del source de UPSTREAM como CONTRASTE. El JAR del TARGET es un build limpio de esa misma base (mismas firmas/paquetes), pero el comportamiento fino se marca UPSTREAM salvo verificación de bytecode.
* **[RUNTIME]** = validado en ejecución por spikes previos (D0001, BOT_RECIPE).

---

## 1. Objetivo

Descubrir cómo maneja Mobius `_autoSkills` y `_autoBuffs`, y cómo funciona el targeting de party para heals y buffs: son el punto de unión entre los futuros perfiles de rol y el motor nativo AutoPlay/AutoUse.

Preguntas centrales: (1) dónde viven, quién los crea/rellena/modifica/limpia, orden y persistencia; (2) ¿AutoPlay/AutoUse puede seleccionar automáticamente otro miembro de party como objetivo de un heal?; (3) ¿cómo se ejecutan buffs de party y qué resuelve Mobius automáticamente?; (4) ¿qué significa realmente "poner un skill en AutoPlay"?; (5) ¿dónde debería vivir la configuración TANK/HEALER/MAGE/SUPPORT/DPS?

## 2. Alcance

* SOLO investigación y documentación.
* NO se implementa BotAI, RoleStrategy ni ThinkLoop.
* NO se modifica Player.java ni ningún core congelado.
* NO se toca UPSTREAM, ni build/deploy/restart, ni DB.

## 3. Método de investigación

| Método | Fuente | Uso |
|---|---|---|
| `javap -p` / `javap -c -p` | TARGET `libs\GameServer.jar` | firmas y bytecode de `AutoUseSettingsHolder`, `AutoPlaySettingsHolder`, `Player`, `PlayerVariables`, `TargetType`, `AutoUseTaskManager(+$AutoUse)`, `AutoPlayTaskManager` |
| Lectura source Java | TARGET datapack | `voiced\AutoPlay.java`, `handlers\skill\targets\*.java`, `MasterHandler.java` |
| Lectura source Java | UPSTREAM (contraste) | `AutoUseTaskManager`, `AutoPlayTaskManager`, `Player`, `OfflinePlayTable`, `PlayerVariables`, `AutoPlayConfig`, `AttackableAI`, `Skill` |
| Parseo XML | TARGET datapack (`skills` + `skillTrees`) | tabla de skills con efectos de heal (274) y cruce con IDs aprendibles (1173) |
| Inspección config / DB | TARGET `AutoPlay.ini`, `db_installer\sql\game\` | baseline real y tablas de persistencia |

Notas: la terminal PowerShell de VS Code trunca/crashea salidas largas → se trabajó con scripts en `%TEMP%\botai06b0\` con salida a archivo (mismo método que BOTAI-06-D). Los scripts NO forman parte del Notebook. Los números de línea citados corresponden al source de UPSTREAM; en el bytecode del TARGET las estructuras coinciden (verificado por `javap`) pero no hay mapeo 1:1 de líneas.

---


## 4. `_autoSkills`

**Declaración y tipo — VERIFIED [TARGET/JAR + UPSTREAM]**

* Clase: `org.l2jmobius.gameserver.entity.actor.holders.player.AutoUseSettingsHolder`.
* Campo: `private final List<Integer> _autoSkills = new CopyOnWriteArrayList<>();` (UPSTREAM `AutoUseSettingsHolder.java:37`).
* `javap -p` confirma en el JAR del TARGET los campos `_autoSupplyItems`, `_autoActions`, `_autoBuffs` (todos `Collection<Integer>`), `_autoSkills` (`List<Integer>`), `_autoPotionItem` (`AtomicInteger`) y `_skillIndex` (int).
* El bytecode de `isAutoSkill`, `getNextSkillId`, `incrementSkillOrder`, `resetSkillOrder` e `isEmpty` coincide con el source de UPSTREAM (verificado por `javap -c -p` sobre el JAR).

**Titularidad — VERIFIED [TARGET/JAR + UPSTREAM]**

* `Player` tiene `private final AutoUseSettingsHolder _autoUseSettings = new AutoUseSettingsHolder();` (UPSTREAM `Player.java:975`) y `private final AutoPlaySettingsHolder _autoPlaySettings = new AutoPlaySettingsHolder();` (`Player.java:974`).
* `javap -p Player` en el JAR del TARGET confirma ambos campos más `AtomicBoolean _autoPlaying`, y los métodos `getVariables()`, `getAutoPlaySettings()`, `getAutoUseSettings()`, `setAutoPlaying(boolean)`, `isAutoPlaying()`.

**Quién lo crea — VERIFIED**

* El constructor de `Player` (inicialización de campo, UPSTREAM `Player.java:975`). No existe un "creador" externo.

**Quién lo rellena — VERIFIED**

Solo dos lugares en todo el producto (búsqueda exhaustiva en UPSTREAM `java\` y en el datapack del TARGET):

1. **Comando voiced del datapack del TARGET** `game\data\scripts\handlers\chat\commands\voiced\AutoPlay.java`:
   * `.playskills` → si `knownSkill.hasNegativeEffect()` → `getAutoSkills().add(id)`; si NO → `getAutoBuffs().add(id)` (AutoPlay.java:389-435).
   * `.playitems` → `getAutoSupplyItems().add(id)` (AutoPlay.java:500-538).
   * `.playpotion` → `setAutoPotionItem(id)` (AutoPlay.java:599+).
   * `.play attack|loot|respect|range|mode|percent` → toggles de `AutoPlaySettingsHolder` + `getAutoActions().add(2)` para ataque melee (AutoPlay.java:181-242).
   * `.play start|stop` → `AutoPlayTaskManager.startAutoPlay` + `AutoUseTaskManager.startAutoUseTask` / `stopAutoPlay` + `stopAutoUseTask` (AutoPlay.java:243-254).
2. **`OfflinePlayTable`** (restore al arrancar el server, UPSTREAM `OfflinePlayTable.java:104-131`): `TYPE_AUTO_SKILL = 3` → `getAutoSkills().add(id)`.

**NOT FOUND [buscado explícitamente]:** no existe ningún packet de cliente que rellene `_autoSkills`/`_autoBuffs` en esta versión. El registro es 100% server-side vía el voiced command.

**Quién lo modifica/limpia — VERIFIED**

* `AutoUseTaskManager` (SKILLS loop, UPSTREAM:310-314): si el skill ya no es conocido por player/summon → `getAutoSkills().remove(skillId)` + `resetSkillOrder()`.
* Cambio de clase (`Player.java:10839-10840`): `getAutoSkills().clear()` y `getAutoBuffs().clear()`.
* `stopAutoUseTask` llama `resetSkillOrder()` (UPSTREAM:456-466).
* `Player.setAutoPlaying(false)` + `OfflinePlayConfig.RESTORE_AUTO_PLAY_OFFLINERS` → `OfflinePlayTable.removeOfflinePlay(this)` (borra filas DB) (UPSTREAM `Player.java:15344-15352`).

**Orden — VERIFIED**

* `_autoSkills` es `CopyOnWriteArrayList` → **orden de inserción**. No hay prioridad ni sort.
* Consumo **round-robin** con `_skillIndex`: `getNextSkillId()` (UPSTREAM:80-98) + `incrementSkillOrder()` (:100-103) + `resetSkillOrder()` (:105-108). Verificado también en bytecode del JAR del TARGET.
* `_autoBuffs` es `ConcurrentHashMap.newKeySet()` → **sin orden garantizado**; se itera entera en cada tick.

**Cómo lo consume AutoPlay/AutoUse — VERIFIED**

* `AutoPlayTaskManager` consulta `_autoSkills` solo para el caso especial del skill 254 (Spoil) contra monstruos muertos (UPSTREAM `AutoPlayTaskManager.java:102`) y `_autoActions` para decidir si es melee (`isMageCaster`, :332-335).
* El consumo real de `_autoSkills` lo hace `AutoUseTaskManager` (ver §6).

---


## 5. `_autoBuffs`

**Declaración y tipo — VERIFIED [TARGET/JAR + UPSTREAM]**

* `private final Collection<Integer> _autoBuffs = ConcurrentHashMap.newKeySet();` (UPSTREAM `AutoUseSettingsHolder.java:36`).
* Misma titularidad y ciclo de vida que `_autoSkills` (mismo holder, mismo `Player._autoUseSettings`).

**Quién lo rellena — VERIFIED**

1. Voiced command del TARGET: `.playskills` → skills SIN efecto negativo → `getAutoBuffs().add(id)` (AutoPlay.java:389-435).
2. `OfflinePlayTable`: `TYPE_AUTO_BUFF = 2` → `getAutoBuffs().add(id)` (UPSTREAM `OfflinePlayTable.java:123-127`).

**Quién lo modifica/limpia — VERIFIED**

* `AutoUseTaskManager` BUFFS loop (UPSTREAM:204-228): si el skill no es conocido por player/summon/pet → `getAutoBuffs().remove(skillId)`.
* Cambio de clase: `getAutoBuffs().clear()` (UPSTREAM `Player.java:10840`).

**Orden — VERIFIED**

* Set sin orden; se recorre completo en cada tick de `AutoUse` (300 ms por pool).

**Consumo — VERIFIED [UPSTREAM + bytecode TARGET/JAR]**

Bloque BUFFS de `AutoUseTaskManager$AutoUse.run()`:

* Puerta de entrada (UPSTREAM:176-202): `AutoPlayConfig.ENABLE_AUTO_SKILL`; `isInPeaceZone` → break; `isCastingNow` → break; `isAttackingNow` → break; `isTeleporting` → break.
* Por skill (UPSTREAM:204-263):
  1. Resolución: `player.getKnownSkill(skillId)` → si null, `summon.getKnownSkill` → si null, `PetSkillData.getKnownSkill`; si sigue null → `remove(skillId)`.
  2. `target = player.getTarget()`; `canCastBuff(player, target, skill)`.
  3. `caster = pet != null ? pet : player`.
  4. **Si `target != null && target.isPlayable()`** (UPSTREAM:239-256):
     * si `((targetPlayer.getPvpFlag() == 0) && (targetPlayer.getKarma() <= 0)) || (targetPlayer.getParty() == caster.getParty())` → `caster.doCast(skill)` (**castea sobre el target actual**);
     * si no → si el caster no está ya afectado → truco `setTarget(self) → doCast → setTarget(savedTarget)`.
  5. **Si el target no es playable** (UPSTREAM:257-263): truco `setTarget(self) → doCast → setTarget(savedTarget)` (self-buff).

**`canCastBuff(player, target, skill)` (UPSTREAM:370-401; bytecode coincidente en el JAR del TARGET) — VERIFIED**

* Target criatura `isAlikeDead()` y `targetType` NO en {SELF, CORPSE, PC_BODY} → `false` (no se buffea/cura a un muerto salvo resurrección).
* `playableTarget = target.isPlayable() ? target.asPlayable() : player` → el check de presencia de buff se hace **sobre el target (o sobre self si no hay target playable)**.
* Distancia: si `player != playableTarget` y `calculateDistance3D > skill.getCastRange()` → `false`.
* `canUseMagic(player, playableTarget, skill)` (UPSTREAM:403-428): item consume, cargas, MP (`getMpInitialConsume + getMpConsume` contra el MP del caster), caso especial skill 254 (spoiled), `isSkillDisabled` (reuse) y `skill.checkCondition(...)`.
* Presencia de buff: `getBuffInfoBySkillId` y `getBuffInfoByAbnormalType` sobre `playableTarget`; compara nivel/abnormalLevel/tiempo con `REUSE_MARGIN_TIME = 3` para decidir recast.

**Consecuencia crítica (INFERRED, coherente con bytecode):** una heal en `_autoBuffs` NO tiene umbral de HP del objetivo. Se casteará siempre que no esté en reuse, haya MP, no se esté casteando/atacando/teleportando y no se esté en zona de paz. La cadencia real la marca `reuseDelay` + el tick de 300 ms.

**Nota [TARGET/DATAPACK]:** `hasNegativeEffect()` = `(effectPoint < 0) && (targetType != SELF)` (UPSTREAM `Skill.java:941-944`). Por eso las HEALS (effectPoint positivo, targetType `ONE`/`PARTY`/etc.) caen en `_autoBuffs`, no en `_autoSkills`.

---


## 6. AutoUse (motor de skills/items/pociones)

**Identidad — VERIFIED [TARGET/JAR]**

* Clase `org.l2jmobius.gameserver.taskmanagers.AutoUseTaskManager` (+ inner `AutoUse implements Runnable`).
* Constantes (UPSTREAM:51-54): `POOL_SIZE = 200` jugadores por pool, `TASK_DELAY = 300` ms, `REUSE_MARGIN_TIME = 3` s.
* API (javap del JAR): `startAutoUseTask(Player)` (synchronized), `stopAutoUseTask(Player)`, `getInstance()`.

**Ciclo por tick (UPSTREAM:70-368; estructura verificada por bytecode del JAR del TARGET) — VERIFIED**

1. Gate de vida (UPSTREAM:77-83): si `!isOnline()` o (`isInOfflineMode()` && `!isOfflinePlay()`) → `stopAutoUseTask`.
2. Guards (UPSTREAM:85-88): sitting/stunned/sleeping/paralyzed/controlBlocked/alikeDead/mounted/riding-transform → `continue`.
3. `isInPeaceZone` (UPSTREAM:90).
4. **ITEMS** (UPSTREAM:92-147): si `AutoPlayConfig.ENABLE_AUTO_ITEM && !isInPeaceZone` → recorre `getAutoSupplyItems()`, usa `ItemHandler.onItemUse`, respeta reuse de item; si el item ya no existe → `remove(itemId)`.
5. **Auto potion** (UPSTREAM:150-174): si `ENABLE_AUTO_POTION && !isInPeaceZone && currentHpPercent < autoPotionPercent` → usa `getAutoPotionItem()` (si el item ya no existe → `setAutoPotionItem(0)`).
6. **BUFFS** (UPSTREAM:176-264): ver §5. **Se ejecuta aunque AutoPlay esté apagado.**
7. Gate (UPSTREAM:266-270): `if (!player.isAutoPlaying()) continue;` → **el bloque SKILLS solo corre con AutoPlay activo.**
8. **SKILLS** (UPSTREAM:272-365): recorre `_autoSkills` en round-robin:
   * `isCastingNow`/`isTeleporting` → `break SKILLS`.
   * `target = player.getTarget()`; `skillId = getNextSkillId()`.
   * Skill conocido por player/summon/pet; si no → `remove(skillId)` + `resetSkillOrder()` + `break`.
   * `target == player` → `break` (no se castea a sí mismo aquí).
   * `target == null || target.isDead()` → limpia `queuedSkill` y `break`.
   * `target.isInsideZone(PEACE) || !target.isAutoAttackable(player)` → `break` (UPSTREAM:336-339). **Un miembro de party nunca es `isAutoAttackable` → el bloque SKILLS nunca casteará sobre un aliado.**
   * `Guard` + targetMode no 3/0 → `break`.
   * `incrementSkillOrder()`; `canUseMagic(caster, target, skill)` → `caster.useMagic(skill, true, false)`; `break`.

**Lectura clave para BOTAI-06-B (INFERRED, coherente con bytecode):** AutoUse NO hace ninguna selección de objetivo por HP/CP/MP de terceros. El bytecode de `run()/canCastBuff()/canUseMagic()` del JAR del TARGET no contiene llamadas a `getParty`, `getCurrentHp`, `getCurrentCp` ni iteración sobre `getParty().getMembers()`. La única lógica de HP en AutoUse es la propia del bot (`AutoPotionPercent`, UPSTREAM:150).

---


## 7. AutoPlay (motor de movimiento/targeting/ataque)

**Identidad — VERIFIED [TARGET/JAR]**

* Clase `org.l2jmobius.gameserver.taskmanagers.AutoPlayTaskManager` (+ inner `AutoPlay implements Runnable`).
* Constantes (UPSTREAM:51-55): `POOL_SIZE = 200`, `TASK_DELAY = 700` ms, `AUTO_ATTACK_ACTION = 2`.
* API (javap): `startAutoPlay(Player)` (synchronized), `stopAutoPlay(Player)`, `getInstance()`.
* Estado extra: `IDLE_COUNT` (Map<Player,Integer>) para detección de idle (UPSTREAM:52).

**Ciclo por tick (UPSTREAM:70-363) — VERIFIED [UPSTREAM]**

1. Gate (UPSTREAM:78-84): `!isOnline()` o (`isInOfflineMode()` && `!isOfflinePlay()`) o `!AutoPlayConfig.ENABLE_AUTO_PLAY` → `stopAutoPlay`.
2. Guards (UPSTREAM:86-89): `isSitting()`, `isCastingNow()`, `getQueuedSkill() != null` → `continue`.
3. `targetMode = player.getAutoPlaySettings().getNextTargetMode()` (UPSTREAM:92). Valores: 1 = Monster, 2 = Characters, 3 = NPC, 0/default = Any (UPSTREAM `isTargetModeValid`, :337-363).
4. Pickup de items (UPSTREAM:228-259): si `doPickup()` y inventario < 90% → recoge drops en rango.
5. Búsqueda de target (UPSTREAM:285-307): `World.getNearestVisibleObjectInRange(player, Creature.class, short/longRange, predicate)` con:
   * `isTargetModeValid(targetMode, player, nearby)`;
   * `isRespectfulHunting()` → no robar targets de otros;
   * `|dz| < 800` + `canSeeTarget` + `canMoveToTarget`.
6. Resultado (UPSTREAM:310-328): `player.setTarget(creature)`; si `isMageCaster(player)` y distancia > 900 y no se mueve → `getAI().setIntentionMoveTo(creature)`; si no → `getAI().setIntentionAttack(creature)`.
7. `isMageCaster(player)` = `!player.getAutoUseSettings().getAutoActions().contains(2)` (UPSTREAM:332-335). **El flag de "ataque melee" (acción 2 en `_autoActions`) es lo que convierte al bot en attacker.**
8. Caso especial (UPSTREAM:102): si el target muerto es monstruo y `_autoSkills` contiene 254 (Spoil) → no re-target (para swepear).

**Veredicto de targeting de AutoPlay — VERIFIED [UPSTREAM + bytecode estructural]:**

* `isTargetModeValid` (UPSTREAM:337-363) solo acepta objetivos **atacables**:
  * modo 1 (default): `creature.isMonster() && !isRaid() && isAutoAttackable(player)`;
  * modo 2: `creature.isPlayable() && isAutoAttackable(player)`;
  * modo 3: `creature.isNpc() && !isMonster() && !isInsideZone(PEACE)`;
  * default: NPC fuera de zona de paz O playable `isAutoAttackable`.
* **Un miembro de party nunca pasa ese filtro** (no es monstruo, no es NPC, y no es `isAutoAttackable`). Por tanto **AutoPlay jamás seleccionará un aliado como target.**

---


## 8. PlayerAI

* `PlayerAI` NO participa en AutoUse: `AutoUse` llama directamente `caster.useMagic(...)` / `caster.doCast(...)` (UPSTREAM AutoUseTaskManager:362, 244, 252, 261), sin pasar por `PlayerAI`.
* `AutoPlay` sí usa `PlayerAI` vía `setIntentionAttack` / `setIntentionMoveTo` (UPSTREAM AutoPlayTaskManager:115, 153, 173, 179, 209, 321, 327).
* `PlayerAI` (UPSTREAM:368, 407, 412) maneja la intención CAST (incl. `TargetType.GROUND`) y el pickup, pero **no contiene lógica de targeting de party para heals** (buscado explícitamente: NOT FOUND).
* Consecuencia para bots clientless: el control del bot en combate viene de `AutoPlay` (movimiento/ataque) + `AutoUse` (skills/items), ambos server-side, sin necesidad de cliente.

## 9. Party Heal

**Pregunta:** ¿AutoPlay/AutoUse puede seleccionar automáticamente otro miembro de party como objetivo de un heal?

**Respuesta: NO existe selección automática de miembro de party para heals.** (VERIFIED por bytecode del JAR del TARGET + source UPSTREAM; ver §6.)

Desglose exacto:

| Mecanismo | ¿Selecciona aliados? | Evidencia |
|---|---|---|
| Búsqueda de target de `AutoPlay` | **NO** — solo objetivos atacables (`isTargetModeValid`, modo 1/2/3/0) | UPSTREAM `AutoPlayTaskManager.java:337-363` |
| Bloque SKILLS de `AutoUse` (ofensivas) | **NO** — exige `target.isAutoAttackable(player)` | UPSTREAM `AutoUseTaskManager.java:336-339` |
| Bloque BUFFS de `AutoUse` (buffs/heals) | **Parcial** — castea sobre `player.getTarget()` **si** ese target es un playable inocente o de la misma party; si no, castea en self | UPSTREAM `AutoUseTaskManager.java:239-263` |
| Selección por HP/CP/MP del aliado | **NOT FOUND** — no existe en AutoUse/AutoPlay/PlayerAI | bytecode del JAR sin llamadas a `getCurrentHp/getCurrentCp/getParty().getMembers()` |

**Implicación operativa (INFERRED, muy relevante):**

* Una heal (p. ej. `Heal 1011`, targetType `ONE`) en `_autoBuffs` **solo curará a un miembro de party si el target actual del bot ES ese miembro**. Como AutoPlay nunca pone a un aliado como target, en la práctica la heal irá a **self**.
* Para que un bot healer cure a un aliado concreto, **alguien tiene que fijar el target** (`player.setTarget(partyMember)`) antes de que el bloque BUFFS lo castee. Ese "alguien" no existe en Mobius: tendría que ser la futura RoleStrategy/provisioning.
* Patrón nativo equivalente para NPC (UPSTREAM `AttackableAI.java:1067-1160`): el AI de monstruos healer hace `setTarget(leader) → doCast(healSkill) → setTarget(saved)`, y usa `World.getFirstVisibleObjectInRange(...)` para elegir objetivo de skills `TargetType.ONE`, y `isParty(sk)` para skills de party. **Es el único lugar del producto con "party heal targeting" real, y es NPC-side, no Player-side.** Marcar como **[UPSTREAM]** (contraste) y como patrón de referencia, no como comportamiento de Player.

**Heals de party que SÍ funcionan nativamente:** las heals con `targetType PARTY`/`PARTY_CLAN`/`AURA_FRIENDLY`/`AREA_FRIENDLY` resuelven su propia lista de objetivos (ver §10), así que basta con ponerlas en `_autoBuffs` para que el bot las caste "sobre la party" sin seleccionar a nadie. Ejemplos aprendibles verificados en el datapack del TARGET (§19): `Group Heal 1027`, `Greater Group Heal 1219`, `Mass Vitalize 1552`, `Mass Recharge 1428`, `Chant of Life 1229`, `Benediction 1271`, `The Heart/Honor of Pa'agrio 1256/1305`, `Pa'agrio's Fist 1416`, `Sublime Self-Sacrifice 1505`.

**Condiciones nativas que siempre aplican al castear una heal (VERIFIED):**

* no estar en zona de paz; no estar casteando; no estar atacando; no estar teleportando (UPSTREAM:176-202);
* el target no debe estar muerto salvo que el `targetType` sea `SELF`/`CORPSE`/`PC_BODY` (UPSTREAM `canCastBuff`, :370-375);
* rango: `calculateDistance3D <= skill.getCastRange()` (UPSTREAM:378-381);
* MP/cargas/items/reuse/checkCondition del caster (UPSTREAM `canUseMagic`, :403-428).

---


## 10. Party Buff

**Pregunta:** ¿cómo se ejecutan buffs de party, qué targetTypes usan y quién determina el target?

**Cadena nativa — VERIFIED:**

1. El skill declara `targetType` (datapack, TARGET). BOTAI-06-D censó los targetTypes del datapack (PARTY 124, PARTY_MEMBER 17, TARGET_PARTY 3, PARTY_CLAN 21, CLAN 42, CLAN_MEMBER 12, AURA 343, AURA_FRIENDLY 1, etc.).
2. Al castear, `Skill.getTargetList(...)` delega en `TargetHandler.getInstance().getHandler(getTargetType())` (UPSTREAM `Skill.java:1035-1056`).
3. **Los handlers de targetType viven en el datapack del TARGET como código fuente legible:** `game\data\scripts\handlers\skill\targets\*.java` (VERIFIED [TARGET/DATAPACK]). `MasterHandler.java` los registra (líneas 274-297, 571-604).

**Resolución por targetType (VERIFIED [TARGET/DATAPACK]):**

| targetType | Handler | Comportamiento |
|---|---|---|
| `PARTY` | `Party.java:35-84` | caster + miembros de party (y sus summons) dentro de `affectRange`. **Resuelve Mobius automáticamente.** |
| `PARTY_CLAN` | `PartyClan.java` | party + clan (skills de clan) |
| `PARTY_MEMBER` | `PartyMember.java:37-79` | exige un `target` explícito que sea: el propio caster, miembro de la misma party (mismo leader), o su summon |
| `TARGET_PARTY` | `TargetParty.java:36-75` | usa la party del **target** (no del caster); si el target no tiene party → solo el target |
| `AURA` / `AURA_FRIENDLY` | `Aura.java` / `AuraFriendly.java` | área alrededor del caster según `AffectObject`/`AffectScope` |
| `ONE` | `One.java:35-46` | exige target explícito válido; si no → mensaje `THAT_IS_AN_INCORRECT_TARGET` |
| `SELF` | `Self.java` | solo el caster |

**Consecuencia (INFERRED, operativa):**

* Buffs/heals de tipo `PARTY`/`AURA_*`/`AREA_FRIENDLY` se castean "en self" y Mobius resuelve los miembros dentro del radio → **no hace falta targeting por miembro**. El truco `setTarget(self) → doCast → setTarget(saved)` del bloque BUFFS (UPSTREAM:257-263) es suficiente.
* Buffs/heals de tipo `ONE`/`PARTY_MEMBER`/`TARGET_PARTY` **requieren target explícito**; sin un mecanismo que fije `player.setTarget(aliado)`, un bot no puede usarlos sobre terceros.
* La RoleStrategy futura NO debe reimplementar la resolución de targets: debe apoyarse en los handlers existentes y decidir solo **qué skills** poner en `_autoBuffs`/`_autoSkills` y, si hace falta targeting por miembro, **a quién** poner como target.

---


## 11. Targeting (resumen del modelo nativo)

**Modelo de targeting de Mobius para un Player (VERIFIED):**

```text
target = player.getTarget()                  ← lo fija el cliente (humano) o el código server-side
AutoPlay  → setTarget(monstruo atacable) + setIntentionAttack/MoveTo   (movimiento + ataque)
AutoUse BUFFS → usa target actual (si es playable inocente o misma party) o self
AutoUse SKILLS → usa target actual SOLO si isAutoAttackable
TargetType handlers (datapack) → resuelven la LISTA de objetivos según targetType
```

* El "target" es un estado del Player (`player.getTarget()/setTarget()`); los target handlers lo reciben como parámetro (`Skill.getTargetList(creature, onlyFirst, target)`).
* No existe ningún "targeting AI" para aliados en el lado Player.
* El único patrón de targeting por miembro que existe en el producto es el de `AttackableAI` (NPC): `setTarget(X) → doCast(skill) → setTarget(saved)` [UPSTREAM].
* `ConditionTarget*` del datapack (`myPartyExceptMe`, `race`, `npcType`, `npcId`, `mindistance`, `abnormal`, `active_effect_id`, `active_skill_id`) actúan como **validadores del target**, no como selectores. Censo en el datapack del TARGET: 132 usos de `<target ...>`; **no existe ninguna condición de HP del target** (`<target hp=...>` = NOT FOUND). Por tanto **no se puede hacer "heal solo si el target está por debajo de X% HP" con condiciones de skill nativas**.

## 12. Persistencia

**Modelo real (VERIFIED):** combinación de runtime + dos caminos de persistencia explícitos.

1. **Runtime:** las listas viven en `AutoUseSettingsHolder` / `AutoPlaySettingsHolder` dentro de `Player`. No se persisten en el save normal del personaje (`storeMe`/`autoSave` no las tocan).
2. **PlayerVariables → tabla `character_variables`** (VERIFIED):
   * Claves (UPSTREAM `PlayerVariables.java:55-62`): `AUTO_USE_SETTINGS`, `AUTO_USE_ACTIONS`, `AUTO_USE_BUFFS`, `AUTO_USE_SKILLS`, `AUTO_USE_ITEMS`, `AUTO_USE_POTION` (+ `RESTORE_LOCATION`, `UI_KEY_MAPPING`).
   * El SQL está en el constant pool del JAR del TARGET (VERIFIED [TARGET/JAR], `javap -c -p PlayerVariables`): `SELECT * FROM character_variables WHERE charId = ?`, `DELETE ...`, `INSERT ... ON DUPLICATE KEY UPDATE`.
   * Guardado asíncrono (UPSTREAM:51-52, 60 s) y `restoreMe()` en el constructor.
   * **Quién escribe/lee:** los listeners `ON_PLAYER_LOGIN` / `ON_PLAYER_LOGOUT` del voiced command `AutoPlay.java` del TARGET (líneas 77-161): al login restauran settings y listas; al logout guardan. **Si nadie ejecuta el voiced command (o un equivalente server-side), estas variables no se escriben.**
   * Esquema presente en el TARGET: `db_installer\sql\game\character_variables.sql` [TARGET/db].
3. **OfflinePlayTable → tablas `character_offline_play` y `character_offline_play_group`** (VERIFIED):
   * Tipos (UPSTREAM:63-69): 0 soulshot activo, 1 supply item, 2 auto buff, 3 auto skill, 4 auto action, 5 auto potion, 100 sistema custom de autopotion.
   * Al arrancar el server: `restoreOfflinePlayers()` hace `Player.load`, `spawnMe`, restaura las listas por tipo y llama **`startAutoPlay(player)` y `startAutoUseTask(player)`** (UPSTREAM:174-175).
   * Al logout con offline play: guarda las listas (UPSTREAM:270-340) y restaura parties si `AutoPlayConfig.ENABLE_AUTO_ASSIST` (UPSTREAM:196-200).
   * Esquema presente en el TARGET: `db_installer\sql\game\character_offline_play.sql` y `character_offline_play_group.sql` [TARGET/db].

**Aclaración para bots (INFERRED/PROPOSED):** el flujo de bots (`AdminBotManager` → `BotProvisioning`) NO pasa por `Player.load` ni por el voiced command; si BOTAI-06-B quiere persistencia de AutoUse para bots, tendrá que escribir/leer esas variables explícitamente (o decidir no persistirlas).

---


## 13. Relación con BotProvisioning

**Estado actual — VERIFIED [TARGET/DATAPACK + BOT_RECIPE]:**

* `BotProvisioning` (TARGET datapack, `handlers\chat\commands\admin\BotProvisioning.java`) declara explícitamente lo que NO hace: *load, spawn, OfflinePlay, Party, AutoPlay, AutoUse, lifecycle, roles, combat behavior* (comentario de cabecera, línea 7).
* El spike `evidence/spikes/D0001/BotSpikeD01.java` (RUNTIME VERIFIED) ya demostró el patrón de configuración server-side:
  * `bot.getAutoPlaySettings().setNextTargetMode(1)`, `setPickup(false)`, `setShortRange(false)`;
  * `bot.getAutoUseSettings().getAutoActions().add(2)`;
  * `AutoPlayTaskManager.getInstance().startAutoPlay(bot)`.
* `BOT_RECIPE.md` §19 ya proponía (sin implementar) inicializar AutoUse recorriendo los skills y clasificando con `hasNegativeEffect()`.

**Dónde debe vivir la configuración por rol (PROPOSED — diseño conceptual, sin APIs inventadas):**

```text
AdminBotManager (orquestador, lifecycle intacto)
   ↓
BotProfile (declarativo: role/classId/targetLevel/grade ladder)   [ya existe, BOTAI-06-ACT1]
   ↓
BotProvisioning.apply(Player, BotProfile)                        [ya existe]
   |-- clase/equipo/skills (giveAvailableSkills)                 [ya existe]
   ↓
RoleStrategy (BOTAI-06-B, FUTURO)  →  traduce el rol a:
   |-- AutoPlaySettingsHolder: targetMode / pickup / shortRange / respectfulHunting / potionPercent
   |-- AutoUseSettingsHolder:  _autoSkills (rotación ofensiva, orden de inserción)
   |                           _autoBuffs  (buffs + heals, incl. heals de party)
   |                           _autoSupplyItems / _autoPotionItem
   |                           _autoActions = [2] solo si el rol es melee attacker
   ↓
AutoPlayTaskManager + AutoUseTaskManager (motores NATIVOS, sin modificación de core)
```

* No se necesita ninguna API nueva del core: todo lo anterior es API pública ya existente y verificada en el JAR del TARGET.
* La clasificación skill → lista debe seguir la regla nativa `hasNegativeEffect()` (misma que `.playskills`) y respetar `AutoPlayConfig.DISABLED_AUTO_SKILLS` (TARGET: 42) y `DISABLED_AUTO_ITEMS`.

---


## 14. Implicaciones para BOTAI-06-B

| Rol | Configuración nativa que tendría que fijar una RoleStrategy |
|---|---|
| **TANK** | `targetMode=1` (Monster); `_autoActions=[2]` (melee attacker, `isMageCaster=false` → `setIntentionAttack`); `_autoSkills` con rotación de aggro/golpe; `_autoBuffs` con self-buffs |
| **DPS** | Igual que TANK pero con `_autoSkills` de daño (round-robin por orden de inserción); la "prioridad" de skill = orden de inserción |
| **MAGE** | NO añadir acción 2 (`isMageCaster=true` → AutoPlay mueve y no auto-hitea); `_autoSkills` con nukes |
| **HEALER** | `_autoBuffs` con heals/buffs **de tipo PARTY/PARTY_CLAN/AURA_FRIENDLY** (se resuelven solos sobre la party) + self-buffs. Las heals `ONE` solo funcionan si el target del bot es el aliado. **Restricción dura:** el bloque BUFFS no corre si el bot está atacando o casteando (UPSTREAM:176-202) |
| **SUPPORT** | Igual que HEALER para buffs de party (`PARTY`/`AURA`); sin `_autoSkills` ofensivos o mínimos |

**Restricciones y huecos que la RoleStrategy debe asumir (VERIFIED/INFERRED):**

1. **No hay selector de miembro herido.** Para curar a un aliado concreto con una heal `ONE`, hay que fijar `player.setTarget(aliado)`; Mobius no lo hace solo. (VERIFIED la ausencia; PROPOSED la implementación.)
2. **No hay umbral de HP en AutoUse** (solo en el autopotion del propio bot, UPSTREAM:150). Las heals en `_autoBuffs` se castean por cadencia de reuse, no por necesidad.
3. **BUFFS se detiene si el bot ataca o castear** (UPSTREAM:176-202): un healer bot que auto-ataca no curará. Implicación: el healer probablemente no debe llevar `_autoActions=[2]`, o debe aceptarse que curar y atacar son mutuamente excluyentes por tick.
4. **SKILLS (ofensivas) requieren `isAutoPlaying()`** (UPSTREAM:266-270): sin `startAutoPlay`, la rotación ofensiva no corre aunque `_autoSkills` esté llena.
5. **Las buffs/heals NO requieren AutoPlay activo**, solo `startAutoUseTask` (el bloque BUFFS está antes del gate de `isAutoPlaying`).
6. El registro de skills debe seguir la regla nativa `hasNegativeEffect()` para no desincronizarse con el comportamiento del voiced command.
7. Los target handlers del datapack ya resuelven PARTY/AURA; la RoleStrategy no debe reimplementarlos.

---


## 15. Límites / NOT FOUND

| # | Elemento buscado | Resultado | Evidencia |
|---|---|---|---|
| NF1 | Selector automático de miembro de party herido (heal targeting) | **NOT FOUND** en AutoUse/AutoPlay/PlayerAI (Player-side) | bytecode JAR sin `getParty`/`getCurrentHp`/`getCurrentCp`; source UPSTREAM revisado completo |
| NF2 | Packet de cliente que registra `_autoSkills`/`_autoBuffs` | **NOT FOUND** en esta versión | búsqueda exhaustiva UPSTREAM + datapack TARGET |
| NF3 | Condición de skill sobre HP del target (`<target hp=...>`) | **NOT FOUND** en el datapack (132 usos de `<target ...>`, ninguno con HP) | parseo TARGET datapack |
| NF4 | Persistencia de AutoUse en el save normal del player (`storeMe`/autoSave) | **NOT FOUND** — solo logout (PlayerVariables) y OfflinePlayTable | UPSTREAM + TARGET/JAR |
| NF5 | Prioridad/pesos por skill en `_autoSkills` | **NOT FOUND** — solo orden de inserción + round-robin | UPSTREAM `AutoUseSettingsHolder.java:80-108` |
| NF6 | Lógica de "party heal" en PlayerAI | **NOT FOUND** — PlayerAI no participa | UPSTREAM `PlayerAI.java` |
| NF7 | Umbral de HP para heals en AutoUse | **NOT FOUND** (solo `AutoPotionPercent` del propio bot) | UPSTREAM `AutoUseTaskManager.java:150` |
| NF8 | Confirmación bytecode línea-a-línea de `AutoPlay.run()` | **PENDING** (estructura y firmas verificadas; flujo leído en UPSTREAM) | `javap -p AutoPlayTaskManager` |

## 16. Anomalías

| # | Anomalía | Impacto | Estado |
|---|---|---|---|
| A1 | El bloque BUFFS (incl. heals) **no corre si el bot ataca o castear** (UPSTREAM:176-202). Un healer que auto-ataca no cura. | ALTO para diseño del HEALER | VERIFIED (source) |
| A2 | El bloque SKILLS requiere `isAutoPlaying()`; el bloque BUFFS no (UPSTREAM:266-270 vs 176). | MEDIO — explica comportamientos asimétricos observados en bots | VERIFIED (source) |
| A3 | AutoUse casteará sobre `player.getTarget()` aunque sea un jugador inocente NO de la party (UPSTREAM:242: `pvpFlag==0 && karma<=0`). Un bot podría curar/buffear a un desconocido si ese es su target. | BAJO — relevante en PvP/PvE mixto | VERIFIED (source) |
| A4 | Heals en `_autoBuffs` se castean por cadencia de reuse, sin umbral de HP → riesgo de spam de heal y consumo de MP innecesario. | MEDIO | INFERRED (coherente con `canCastBuff` + bytecode) |
| A5 | AutoPlay nunca fija target a un aliado → las heals `ONE` de un bot caen en self salvo que algo externo fije el target. | ALTO para el diseño HEALER | VERIFIED (source) |
| A6 | `AttackableAI` (NPC healer, UPSTREAM:1139) usa `obj.isDead()` dentro del predicado de selección de objetivo de heal, lo que parece invertido (debería ser `!isDead()`). | BAJO (NPC-side, no Player-side) | UPSTREAM only — **no verificado en bytecode del TARGET**; no afirmar como comportamiento del TARGET |
| A7 | La persistencia de AutoUse depende del voiced command (logout) o de OfflinePlayTable (boot). Un bot provisionado sin esos caminos no persiste sus listas. | MEDIO | VERIFIED (source) |
| A8 | `DISABLED_AUTO_SKILLS` del TARGET solo contiene 42. Cualquier profile debe respetar esa lista. | BAJO | VERIFIED (config TARGET) |

## 17. Preguntas abiertas

1. ¿Validar en runtime (spike) que una heal `PARTY` (p. ej. `Group Heal 1027`) en `_autoBuffs` de un bot en party cura efectivamente a todos los miembros en radio? (PENDING — requeriría spike; fuera del alcance de esta tarea.)
2. ¿Validar en runtime el comportamiento del bloque BUFFS cuando el bot tiene target a un aliado y está siendo atacado (¿el gate `isAttackingNow` lo bloquea)? (PENDING.)
3. ¿Se quiere persistencia de AutoUse para bots (PlayerVariables) o runtime-only con re-aplicación por provisioning? (Decisión de diseño pendiente.)
4. ¿`AttackableAI.java:1139` (`obj.isDead()`) está igual en el bytecode del TARGET? (PENDING — requiere desensamblado del método.)
5. ¿Existe algún mecanismo de assist que fije el target del bot a un aliado (no a un monstruo)? Buscado en `ENABLE_AUTO_ASSIST`/`AssistLeader`: solo sigue el target del líder = monstruo. **NOT FOUND** para aliados.

---


## 18. Conclusión

1. **`_autoSkills` y `_autoBuffs` NO viven en PlayerAI ni en AutoPlay:** viven en `AutoUseSettingsHolder`, dentro de `Player`, y son el **contrato de entrada** al motor nativo `AutoUseTaskManager`. AutoPlay es el motor de movimiento/targeting/ataque; AutoUse es el motor de skills/items/pociones. Ambos son independientes y tienen pools y cadencias distintas (700 ms vs 300 ms).
2. **La única puerta de registro nativa es el voiced command `.play*`** (datapack del TARGET) y el restore de `OfflinePlayTable`. No hay packets de cliente ni hooks en el core: **una RoleStrategy puede rellenar las mismas listas server-side sin tocar el core**, usando exactamente la misma API.
3. **Clasificación nativa:** `hasNegativeEffect()` decide ofensiva (`_autoSkills`) vs buff/heal (`_autoBuffs`). Es la regla que debe copiar cualquier profile.
4. **Party Heal:** Mobius **no selecciona automáticamente** a un miembro de party herido. Solo existe una condición de party en el bloque BUFFS (UPSTREAM:242) que permite castear sobre el target actual si es aliado de la misma party. Para heals `ONE`, el bot solo curará a un aliado si su target es ese aliado; en caso contrario casteará en self. Las heals de tipo `PARTY`/`AURA_FRIENDLY` sí cubren a la party entera de forma automática (los target handlers del datapack resuelven la lista).
5. **Party Buff:** los buffs de `targetType PARTY`/`AURA` se resuelven solos (self-cast + resolución de lista). Los de `PARTY_MEMBER`/`TARGET_PARTY`/`ONE` exigen target explícito.
6. **Persistencia:** runtime + `character_variables` (logout/login vía voiced command) + `character_offline_play` (boot, offline play). No se persiste en el save normal.
7. **Para BOTAI-06-B:** el trabajo se reduce a **mapear rol → (AutoPlaySettings + listas AutoUse)** y a resolver, si se quiere heal por miembro, **el seteo de target** (`setTarget(aliado)`), que no existe nativamente. Nada de esto requiere modificar el core.

## 19. Tabla de referencia: heals/buffs de party aprendibles (TARGET datapack)

Skills con efectos de heal/cure detectados en `game\data\stats\skills\**` (274 en total), filtrados por `targetType` de party/friendly y presentes en `skillTrees` (1173 IDs aprendibles). Extracto relevante:

| id | Skill | targetType | operateType | castRange | effectRange | reuseDelay |
|---|---|---|---|---|---|---|
| 787 | Touch of Eva | PARTY | A2 | 40 | 400 | 150000 |
| 1027 | Group Heal | PARTY | A1 | — | — | 6000 |
| 1219 | Greater Group Heal | PARTY | A2 | — | — | 6000 |
| 1229 | Chant of Life | PARTY | A2 | — | — | 5000 |
| 1271 | Benediction | PARTY | A1 | — | — | 900000 |
| 1305 | The Honor of Pa'agrio | PARTY_CLAN | A1 | — | — | 5000 |
| 1256 | The Heart of Pa'agrio | PARTY_CLAN | A2 | — | — | 5000 |
| 1416 | Pa'agrio's Fist | PARTY_CLAN | A2 | — | — | 300000 |
| 1428 | Mass Recharge | PARTY | A1 | — | — | 3600000 |
| 1505 | Sublime Self-Sacrifice | AURA_FRIENDLY | A2 | — | — | 3600000 |
| 1552 | Mass Vitalize | PARTY | A1 | — | — | 9000 |
| 1553 | Chain Heal | AREA_FRIENDLY | A1 | 900 | 1400 | 5000 |

Heals de target único (`ONE`) relevantes para el gap de targeting: `1011 Heal`, `1015 Battle Heal`, `1020 Vitalize`, `1217 Greater Heal`, `1218 Greater Battle Heal`, `1258 Restore Life`, `1401 Major Heal`, `1487 Restoration` (todas `castRange 600 / effectRange 1100`, salvo indicación).

> Método del censo: parseo de `game\data\stats\skills\*.xml` con cruce de `skillId` contra `game\data\stats\players\skillTrees\**\*.xml`. El listado completo (274 filas) NO se copia al Notebook; regenerable con el método descrito.

---


## 20. Evidencia / referencias

**TARGET/JAR (bytecode y firmas verificadas con `javap` sobre `libs\GameServer.jar`):**

* `org.l2jmobius.gameserver.entity.actor.holders.player.AutoUseSettingsHolder` (campos `_autoSupplyItems/_autoActions/_autoBuffs/_autoSkills/_autoPotionItem/_skillIndex`; métodos `getAutoSupplyItems/getAutoActions/getAutoBuffs/getAutoSkills/getAutoPotionItem/setAutoPotionItem/isAutoSkill/getNextSkillId/incrementSkillOrder/resetSkillOrder/isEmpty`).
* `org.l2jmobius.gameserver.entity.actor.holders.player.AutoPlaySettingsHolder` (campos `_options/_pickup/_nextTargetMode/_shortRange/_respectfulHunting/_autoPotionPercent` + getters/setters).
* `org.l2jmobius.gameserver.entity.actor.Player` (campos `_autoPlaySettings/_autoUseSettings/_autoPlaying`; métodos `getVariables/getAutoPlaySettings/getAutoUseSettings/setAutoPlaying/isAutoPlaying`).
* `org.l2jmobius.gameserver.mechanics.variables.PlayerVariables` (constantes `AUTO_USE_*` + SQL de `character_variables` en constant pool).
* `org.l2jmobius.gameserver.mechanics.skill.targets.TargetType` (38 constantes: AREA, AREA_CORPSE_MOB, AREA_FRIENDLY, AREA_SUMMON, AREA_UNDEAD, AURA, AURA_CORPSE_MOB, AURA_FRIENDLY, BEHIND_AREA, BEHIND_AURA, CLAN, CLAN_MEMBER, COMMAND_CHANNEL, CORPSE, CORPSE_CLAN, CORPSE_MOB, CORPSE_PET, ENEMY_SUMMON, FLAGPOLE, GROUND, HOLY, NPC, ONE, OWNER, OWNER_PET, PARTY, PARTY_CLAN, PARTY_MEMBER, PARTY_NOTME, PARTY_OTHER, PC_BODY, PET, SELF, SERVITOR, SUMMON, TARGET_PARTY, TARGETABLE_AREA, UNLOCKABLE).
* `org.l2jmobius.gameserver.taskmanagers.AutoUseTaskManager` (+`$AutoUse`: flujo ITEMS/BUFFS/SKILLS, `canCastBuff`, `canUseMagic` verificados por bytecode).
* `org.l2jmobius.gameserver.taskmanagers.AutoPlayTaskManager` (firmas; inner `$AutoPlay` con `isMageCaster`/`isTargetModeValid`).
* `org.l2jmobius.gameserver.handler.TargetHandler` / `ITargetTypeHandler` (core).

**TARGET/DATAPACK (source legible dentro del TARGET):**

* `game\data\scripts\handlers\chat\commands\voiced\AutoPlay.java` (registro, toggles, listeners login/logout).
* `game\data\scripts\handlers\skill\targets\*.java` (`One`, `Party`, `PartyMember`, `TargetParty`, `Aura`, `AuraFriendly`, `PartyClan`, ...).
* `game\data\scripts\handlers\chat\commands\admin\BotProvisioning.java` (cabecera: qué NO hace).
* `game\data\scripts\handlers\MasterHandler.java` (registro de target handlers, líneas 274-297 / 571-604).
* `game\data\stats\skills\*.xml` + `game\data\stats\players\skillTrees\**\*.xml` (censo 274 heals / 1173 aprendibles).
* `game\config\Custom\AutoPlay.ini` (baseline real: `EnableAutoPlay=True`, `EnableAutoPotion=True`, `EnableAutoSkill=True`, `EnableAutoItem=True`, `ResumeAutoPlay=False`, `AssistLeader=True`, `ShortRange=600`, `LongRange=1400`, `AutoPlayPremium=False`, `DisabledSkillIds=42`).

**TARGET/db:**

* `db_installer\sql\game\character_variables.sql`
* `db_installer\sql\game\character_offline_play.sql`
* `db_installer\sql\game\character_offline_play_group.sql`


**UPSTREAM (contraste, source):**

* `java\org\l2jmobius\gameserver\entity\actor\holders\player\AutoUseSettingsHolder.java` (:34-39, :45-113)
* `java\org\l2jmobius\gameserver\entity\actor\holders\player\AutoPlaySettingsHolder.java` (:29-101)
* `java\org\l2jmobius\gameserver\entity\actor\Player.java` (:974-975, :10839-10840, :11385-11390, :15334-15357)
* `java\org\l2jmobius\gameserver\taskmanagers\AutoUseTaskManager.java` (:51-54, :70-368, :370-401, :403-428, :431-466)
* `java\org\l2jmobius\gameserver\taskmanagers\AutoPlayTaskManager.java` (:51-55, :70-363, :366-413)
* `java\org\l2jmobius\gameserver\data\sql\OfflinePlayTable.java` (:54-69, :78-200, :270-340)
* `java\org\l2jmobius\gameserver\mechanics\variables\PlayerVariables.java` (:45-62, :51-52)
* `java\org\l2jmobius\gameserver\config\custom\AutoPlayConfig.java` (:38-96)
* `java\org\l2jmobius\gameserver\mechanics\skill\Skill.java` (:941-944, :996-1064, :1092-1200)
* `java\org\l2jmobius\gameserver\ai\AttackableAI.java` (:1067-1160)
* `java\org\l2jmobius\gameserver\ai\PlayerAI.java` (:368, :407, :412)

**Notebook relacionado (autoridad cruzada):**

* `bots/BOT_RECIPE.md` §12/§13/§14/§15/§19 (AutoPlay/AutoUse verificado, criterio `hasNegativeEffect`)
* `investigations/BOTAI-06-D_SKILL_BUFF_COMBAT_MASTER_CATALOG.md` §4.3 (censo de targetTypes), §9 (condiciones nativas)
* `investigations/BOTAI-06-ACT1_CONSOLIDATION.md` (BotProfile → BotProvisioning, qué NO hace provisioning)
* `evidence/spikes/D0001/BotSpikeD01.java` (patrón server-side de configuración AutoPlay/AutoUse, RUNTIME VERIFIED)
* `knowledge/SOURCE_EVIDENCE.md` §12 (PlayerAI) / §13 (Party)
* `ROUTING/bots.md`

**Fin del documento BOTAI-06-B0.**

