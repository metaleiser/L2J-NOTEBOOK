# SOURCE EVIDENCE — Temporary Player / Clientless Archer (Mobius CT 2.6 HighFive)

> **Estado:** VERIFICADO contra fuente local
> **Ruta base upstream:** `UPSTREAM\L2J_Mobius\L2J_Mobius_CT_2.6_HighFive\java\org\l2jmobius\gameserver\`
> **Fecha verificación:** Marzo 2026
> **Confianza general:** ALTA (todas las referencias son líneas exactas del source real)

---

## 1. Player — Creación y constructores

### Fuente: `entity/actor/Player.java`

#### Constructor privado primario (líneas 895-917)
```
private Player(int objectId, PlayerTemplate template, String accountName, PlayerAppearance app)
```
- Llama a `super(objectId, template)` → `Creature` → `WorldObject`
- `setInstanceType(InstanceType.Player)`
- `initCharStatusUpdateValues()`, `initPcStatusUpdateValues()`
- Inicializa `_htmlActionCaches`
- `_accountName = accountName`
- `app.setOwner(this)`; `_appearance = app`
- **Llama a `getAI()` (línea 912) → crea `PlayerAI` automáticamente**
- Crea `_radar = new Radar(this)` (línea 915)
- `startVitalityTask()` (línea 916)
- **NO requiere `GameClient`**

#### Constructor privado simplificado (líneas 925-928)
```
private Player(PlayerTemplate template, String accountName, PlayerAppearance app)
```
- Delega al primario con `IdManager.getInstance().getNextId()`
- **Ambos son `private` → requieren factory en el mismo paquete (`entity.actor`)**

#### Factory pública `Player.create()` (líneas 1020-1042)
```
public static Player create(PlayerTemplate template, String accountName, String name, PlayerAppearance app)
```
- Crea vía constructor simplificado
- `setName(name)`, `setCreateDate(Calendar)`, `setBaseClass(...)`, `setNewbie(1)`, `setRecomLeft(20)`
- **Llama a `createDb()` (línea 1041) → INSERT en `characters`**
- **NO USAR para temporary players**

#### `createDb()` (línea 7062)
- Ejecuta `INSERT_CHARACTER` (`"INSERT INTO characters (account_name,charId,char_name,level,...)"`, línea 397)
- **PERSISTENTE — evitar**

#### `storeMe()` (líneas 7637-7640)
- Delega a `store(true)` que ejecuta `UPDATE characters SET ...` completo (líneas 7648-7700)
- **PERSISTENTE — no usar en temporary players**

#### `autoSave()` (líneas 8627-8638)
- Llama a `storeMe()`, `storeRecommendations(false)`
- Si `UPDATE_ITEMS_ON_CHAR_STORE`: `getInventory().updateDatabase()`, `getWarehouse().updateDatabase()`, `getFreight().updateDatabase()`
- **PERSISTENTE — no usar**

---

## 2. Player — GameClient dependency

### Fuente: `entity/actor/Player.java`

#### `sendPacket(ServerPacket)` (líneas 4429-4435)
```java
public void sendPacket(ServerPacket packet)
{
    if (_client != null)
    {
        _client.sendPacket(packet);
    }
}
```
- **YA TIENE GUARD `_client != null`** — seguro clientless
- No lanza excepción ni crea problema

#### `sendPacket(SystemMessageId)` (líneas 4442-4445)
- Delega a `sendPacket(new SystemMessage(id))` — mismo guard

#### `getAccountName()` (líneas 1044-1047)
```java
public String getAccountName()
{
    return _client == null ? _accountName : _client.getAccountName();
}
```
- **YA TIENE FALLBACK** cuando `_client == null`

### Hallazgo: VERDICT
- `Player.sendPacket` ya es clientless-safe
- No se necesita parche global `if (getClient() != null)`
- **No hay blocker**

---

## 3. Player — Nivel, clase, skills

### Fuente: `entity/actor/Player.java`

#### `getPlayerClass()` (líneas 2415-2418)
```java
public PlayerClass getPlayerClass()
{
    return getTemplate().getPlayerClass();
}
```
- Deriva del template — no requiere DB

#### `setPlayerClass(int id)` (líneas 2424-2475)
- Involucra clan academy (`_clan`), broadcast `MagicSkillUse`, `sendPacket`
- **NO USAR para temporary players** (innecesario, clase viene del template)

#### `setBaseClass(PlayerClass)` (línea 1330)
- Solo asigna campo `_baseClass`
- **RUNTIME — seguro**

#### `addSkill(Skill newSkill)` (líneas 7998-8002)
```java
public Skill addSkill(Skill newSkill)
{
    addCustomSkill(newSkill);
    return super.addSkill(newSkill);
}
```
- Solo runtime — **seguro**

#### `addSkill(Skill newSkill, boolean store)` (líneas 8013-8025)
- Si `store=false`: solo runtime (como `addSkill(skill)`)
- Si `store=true`: llama a `storeSkill(newSkill, oldSkill, -1)` → INSERT/UPDATE en `character_skills`
- **PERSISTENTE SI `store=true` — usar con `false`**

#### `removeSkill(Skill skill)` (líneas 8048-8060)
- Llama a `super.removeSkill(skill, true)` + `DELETE_SKILL_FROM_CHAR` (línea 8056)
- **PERSISTENTE SIEMPRE en esta forma**

### Hallazgo: VERDICT
- Skills runtime: `addSkill(skill, false)` o `addSkill(skill)`
- Removal runtime: `removeSkill(skill, false, cancelEffect)` — usar la variante con 3 params
- **No hay blocker**

---

## 4. Player — deleteMe

### Fuente: `entity/actor/Player.java` (líneas 11734-12147)

#### `deleteMe()` (resumen de operaciones):
1. `EventDispatcher` → `OnPlayerLogout` event (línea 11736)
2. `ZoneManager.getZones(this)`, `zone.onPlayerLogoutInside(this)` (línea 11743)
3. `setOnlineStatus(false, true)` → `updateOnlineStatus()` → **UPDATE characters SET online=?** (líneas 11761, 7042-7055)
4. HandysBlockChecker, Kratei arena cleanup (líneas 11768-11790)
5. `abortAttack()`, `abortCast()`, `stopMove(null)` (líneas 11796-11798)
6. Olympiad, Observation, Siege cleanup...
7. Duel, party, summon cleanup...
8. `PlayerAutoSaveTaskManager.getInstance().remove(this)` (línea 12144)
9. `return super.deleteMe()` → `Creature.deleteMe()` (línea 12146)
10. `notifyFriends()`, `_blockList.playerLogout()` (línea 12136)

#### `Creature.deleteMe()` (líneas 2805-2829)
```java
public boolean deleteMe()
{
    if (hasAI()) getAI().stopAITask();
    _effectList.stopAllEffectsWithoutExclusions(false, false);
    if (_seenCreatures != null) { ... CreatureSeeTaskManager.getInstance().remove(this); ... }
    _buffFinishTask.stop();
    setWorldRegion(null);
    return true;
}
```
- **NO es persistente** — solo runtime cleanup

### Hallazgo: VERDICT
- `Player.deleteMe()` es **PERSISTENTE** (UPDATE characters)
- `Creature.deleteMe()` es **RUNTIME** — reutilizable en despawn
- Para despawn temporal: extraer solo abortAttack/abortCast/stopMove/AI stop/effects stop + Creature.deleteMe

---

## 5. Player — stopAllTasks

### Fuente: `entity/actor/Player.java` (líneas 15185-15230)

#### `stopAllTasks()`
Cancela y limpia:
- `_mountFeedTask`, `_dismountTask`, `_fameTask`
- `_vitalityTask`, `_teleportWatchdog`
- `_taskForFish`, `_chargeTask`, `_soulTask`
- **RUNTIME — seguro usar en despawn**
- Patrón usado por `Disconnection` constructor (Disconnection.java:58)

---

## 6. InstanceType y subclases

### Fuente: `entity/actor/Player.java` (línea 898)
```java
setInstanceType(InstanceType.Player);
```

### Fuente: `entity/WorldObject.java` (InstanceType enum)
- `isPlayer()` / `asPlayer()` dependen de `InstanceType.Player`
- Si se crea `TemporaryPlayer extends Player`, el InstanceType podría heredarse

### Hallazgo: VERDICT
- **Opción A**: Usar Player directamente (sin subclase) — InstanceType = Player, todo funciona
- **Opción B**: Subclase de Player — InstanceType debe ser Player para casts funcionen
- **Recomendación**: sin subclase, usar flag en manager o Player field opcional
- Un Temporary Player debe ser indistinguible de Player para todo el engine

---

## 7. Inventory y equipment — persistencia

### Fuente: `entity/actor/Player.java` (línea 607)
```java
private final PlayerInventory _inventory = new PlayerInventory(this);
```
- Se crea automáticamente en el constructor de Player (vía inicializador de campo)

### Fuente: `entity/itemcontainer/ItemContainer.java`
- `addItem(ItemProcessType, Item, Player, Object)` (líneas 221-235): llama a `item.updateDatabase()` en línea 235
- `protected void addItem(Item)` (líneas 524-555): llama a `item.updateDatabase(true)` en línea 555

### Fuente: `entity/itemcontainer/Inventory.java`
- `setPaperdollItem(int slot, Item item)` (líneas 1072-1129): llama a `old.updateDatabase()` (línea 1109) y `item.updateDatabase()` (línea 1129)
- `equipItem(Item item)` (líneas 1263+): delega a `setPaperdollItem`

### Hallazgo: VERDICT
- **TODO equipamiento llama a `updateDatabase()`** — cada setPaperdollItem persiste
- Solución: **flag `_temporary` en `Item`** que haga no-op en `updateDatabase()`
- Sin flag → cualquier item del bot se INSERTA en `items` en el primer equipamiento
- **Este es el punto de adaptación mínimo requerido**

---

## 8. Item — updateDatabase y persistencia

### Fuente: `entity/item/instance/Item.java`

#### Constructores (líneas 184-239)
- `Item(int objectId, int itemId)` (línea 184): `_itemId`, `_itemTemplate` desde `ItemData`, count=1, loc=VOID
- `Item(int objectId, ItemTemplate)` (línea 212): variante con template
- `Item(int itemId)` (línea 236): obtiene objectId de `IdManager.getNextId()`
- Ninguno inicia `_existsInDb` (queda `false` por defecto)

#### `updateDatabase(boolean force)` (líneas 1481-1512)
```text
SI _existsInDb == true:
    SI ownerId==0 o loc==VOID > removeFromDb()
    SINO > updateInDb()
SINO (_existsInDb == false):
    SI ownerId==0 o loc==VOID > return (no-op)
    SINO > insertIntoDb()  ← INSERT en items
```

#### `updateInDb()` (líneas 1656-1684)
```java
if (!_existsInDb || _wear || _storedInDb) return;  // guard
```
- `_wear` flag evita persistencia (usado en try-on NPC)

#### `insertIntoDb()` (líneas 1689-1725)
```java
if (_existsInDb || (getObjectId() == 0) || _wear) return;  // guard
```
- `_wear` también protege INSERT

#### Flag `_wear` (línea 135, usado en líneas 1658, 1691)
```java
private boolean _wear; // DEBUG: weapon wearing
```
- Solo usado para NPC try-on
- **Sin setter público** — no reutilizable sin modificación

### Hallazgo: VERDICT
- Mecanismo `_wear` prueba que Mobius ya tiene flag anti-persistencia para items
- Pero `_wear` no tiene setter público y su semántica no es "temporal"
- **Adaptación núcleo**: añadir campo `_temporary` + setter + guard de 1 línea en `updateDatabase()`

---

## 9. Item — lifecycle de flechas

### Fuente: `entity/actor/Player.java`

#### `reduceArrowCount(boolean bolts)` (líneas 6391-6420)
- Obtiene flechas de `_inventory.getPaperdollItem(Inventory.PAPERDOLL_LHAND)`
- Si count>1: `arrows.changeCount(null, -1, this, null)` — modifica runtime + updateDatabase()
- Si count==1: `_inventory.destroyItem(ItemProcessType.NONE, arrows, this, null)` — destruye + updateDatabase()
- Llama a `sendItemList(false)` al final — protegido por guard

### Fuente: `entity/itemcontainer/Inventory.java`

#### Asignación de flechas al equipar bow (líneas 188-233)
- `findArrowForBow(item.getTemplate())` (línea 222)
- `setPaperdollItem(PAPERDOLL_LHAND, arrow)` (línea 225) — **persiste**

### Hallazgo: VERDICT
- Consumo de flechas persiste por defecto
- **Con flag `_temporary`** en las flechas: toda la cadena (changeCount/destroyItem/setPaperdollItem) llama a updateDatabase → no-op
- Cuando count llega a 0 y loc=VOID: updateDatabase es no-op
- **Riesgo menor**: si se destruye el Item, el objectId se pierde (releaseId necesario)

---

## 10. IdManager

### Fuente: `managers/IdManager.java`

#### Estructura (líneas 23, 41, 53-56)
```java
import java.util.BitSet;
private BitSet _freeIds;
```
- **100% BitSet en memoria** — sin consultas DB
- Sin tablas, sin SELECT/INSERT/DELETE

#### `getNextId()` (líneas 108-152)
```java
final int newId = _nextFreeId;
_freeIds.set(newId);  // marca como usado
_freeIdCount--;
// resize si threshold superado
return newId + IdManagerConfig.FIRST_OBJECT_ID;
```
- `FIRST_OBJECT_ID` offset fijo

#### `releaseId(int objectId)` (líneas 158-177)
```java
_freeIds.clear(objectId - IdManagerConfig.FIRST_OBJECT_ID);
_freeIdCount++;
```

### Hallazgo: VERDICT
- **IdManager es puramente runtime**
- `releaseId()` debe llamarse en despawn para evitar fuga de IDs
- **No hay blocker**

---

## 11. World

### Fuente: `entity/World.java`

#### `addObject(WorldObject)` (líneas 175-202)
- `_allObjects.putIfAbsent(object.getObjectId(), object)` — runtime
- Si `isPlayer()`: `_allPlayers.putIfAbsent(objectId, newPlayer)` — runtime
- No llama a DB ni a persistencia
- Caso duplicado (líneas 193-194): llama a `Disconnection.storeAndDeleteWith` solo si mismo objectId existe — no aplica con IdManager correcto

#### `getPlayers()` (línea 269)
- Retorna `_allPlayers.values()` — **runtime puro**

#### `broadcastToAllOnlinePlayers(ServerPacket)` (línea 2461)
- Itera `_allPlayers`, llama `player.sendPacket(packet)` — protegido por guard de sendPacket

### Hallazgo: VERDICT
- World no requiere GameClient para add/remove/broadcast
- **No hay blocker**

---

## 12. PlayerAI

### Fuente: `ai/PlayerAI.java`

#### `thinkAttack()` (líneas 341-363)
```java
private void thinkAttack()
{
    final Creature target = getAttackTarget();
    if (target == null) return;
    if (checkTargetLostOrDead(target)) { setAttackTarget(null); return; }
    if (maybeMoveToPawn(target, _actor.getPhysicalAttackRange())) return;
    clientStopMoving(null);
    _actor.doAttack(target);
}
```
- **No referencia a GameClient**
- `maybeMoveToPawn` → MovementTaskManager (server-side con geodata)
- `clientStopMoving` → broadcast MoveToLocation

#### `thinkCast()` (líneas 365-413)
- Similar: no referencia a GameClient
- `_actor.doCast(_skill)` — ruta de cast completa

#### `clientNotifyDead()` (líneas 335-339)
- Solo resetea `_clientMovingToPawnOffset` y llama a super — no requiere cliente

### Fuente: `taskmanagers/AutoPlayTaskManager.java`
- `player.getAI().setIntentionAttack(creature)` (líneas 173, 179, 327)
- `player.getAI().setIntentionMoveTo(target)` (líneas 115, 153, 209, 321)
- `player.getAI().setIntentionFollow(leader)` (línea 279)
- **Prueba de producción** de que PlayerAI funciona sin cliente

### Hallazgo: VERDICT
- **PlayerAI no requiere GameClient** para operaciones de PvE
- **AutoPlayTaskManager prueba que el patrón funciona**
- **No hay blocker** — reutilización directa

---

## 13. Party

### Fuente: `entity/groups/Party.java`

#### `addPartyMember(Player)` (líneas 280-345)
- `_members.add(player)` — runtime
- Todos los `sendPacket`/`broadcastPacket` protegidos por guard de `sendPacket`
- Ajuste `_partyLvl` — runtime
- **Seguro clientless**

#### `removePartyMember(Player, PartyMessageType)` (líneas 401-475+)
- `player.sendPacket(SystemMessageId...)` — protegido
- `broadcastPacket(...)` — protegido
- **Seguro clientless**

#### `broadcastToPartyMembers(Player, ServerPacket)` (líneas 265-271)
```java
for (Player member : _members)
    if (member != null) member.sendPacket(packet);
```
- protegido

#### `distributeItem(Player, Item)` (líneas 618-640)
- `target.addItem(ItemProcessType.LOOT, item, player, true)` — **persiste items**
- **Riesgo: bot no debe ser looter en MVP**

### Hallazgo: VERDICT
- Party game logic agnóstica de cliente
- UI sync protegida por guard de sendPacket
- **Único riesgo**: loot hacia el bot
- **No hay blocker**

---

## 14. SkillTreeData

### Fuente: `data/xml/SkillTreeData.java`

#### `load()` (líneas 119-137)
- `parseDatapackDirectory("data/stats/players/skillTrees/", true)` — carga XML
- **100% en memoria desde startup**

#### `getAllAvailableSkills(Player, PlayerClass, boolean, boolean, boolean)` (líneas 602-642)
- Itera skills existentes del player, luego añade learnable skills del árbol de clase
- Usa `SkillData.getInstance().getSkill(id, level)` para instanciar Skills
- **100% en memoria — no toca DB**

#### `getAvailableSkills(Player, PlayerClass, boolean, boolean)` (línea 532)
- Basado en level, class, skills ya aprendidas
- **Runtime puro**

### Datos de skillTrees (verificados en deploy)
- Ruta base: `L2J MOBIUS H5 SERVER\game\data\stats\players\skillTrees\`
- Subcarpetas: `StartingClass/`, `1stClass/`, `2ndClass/`, `3rdClass/`
- Skills por clase en archivos XML individuales (ej: `2ndClass/Hawkeye.xml`)

### Hallazgo: VERDICT
- Skills para el bot se calculan desde datos XML ya cargados
- `addSkill(skill, false)` añade cada skill a runtime sin persistir
- **No hay blocker**

---

## 15. PlayerTemplateData

### Fuente: `data/xml/PlayerTemplateData.java`

#### `getTemplate(PlayerClass)` (línea 189)
- Retorna `PlayerTemplate` desde mapa cargado en memoria
- **Runtime puro**

#### `getTemplate(int classId)` (línea 199)
- Ídem por classId numérico

#### `Player.getPlayerClass()` (línea 2415)
```java
return getTemplate().getPlayerClass();
```
- Deriva del template — no requiere DB

### Hallazgo: VERDICT
- Template disponible runtime sin DB

---

## 16. PlayerClass — Archer hierarchy

### Fuente: `enums/player/PlayerClass.java`

#### Jerarquía HUMANA (líneas 42-52)
```java
FIGHTER(0, false, Race.HUMAN, null),
WARRIOR(1, false, Race.HUMAN, FIGHTER),
ROGUE(7, false, Race.HUMAN, FIGHTER),
HAWKEYE(9, false, Race.HUMAN, ROGUE),
SAGITTARIUS(92, false, Race.HUMAN, HAWKEYE),
```

### Hallazgo: VERDICT
- `PlayerClass.HAWKEYE` id=9, parent=ROGUE, human
- **Para MVP**: HAWKEYE (2ª clase) o ROGUE para level bajo
- Sagittarius (id=92) es 3ª clase — evitar en MVP por skills requeridos

---

## 17. Disconnection

### Fuente: `network/Disconnection.java`

#### Constructor (líneas 49-73)
```java
private Disconnection(GameClient client, Player player)
{
    _client = client != null ? client : player != null ? player.getClient() : null;
    _player = player != null ? player : client != null ? client.getPlayer() : null;
    if (_player != null) _player.stopAllTasks();  // ← limpia tasks runtime
    AntiFeedManager.getInstance().onDisconnect(_client);
    if (_client != null) _client.setPlayer(null);
    if (_player != null) _player.setClient(null);
}
```

#### `storeAndDelete()` (líneas 110-134)
```java
_player.storeMe();     // PERSISTENTE
_player.deleteMe();    // PERSISTENTE
```

#### `storeAndDeleteWith(ServerPacket)` (líneas 140-148)
- Llama `storeAndDelete()` + `if (_client != null) _client.close(packet)`

#### `onDisconnection()` (líneas 155-176)
- Si `canLogout()`: `storeAndDelete()` inmediato
- Si no: schedule diferido (`AttackStanceTaskManager.COMBAT_TIME`)

#### `of(Player)` (líneas 90-93)
```java
return new Disconnection(null, player);
```

### Hallazgo: VERDICT
- `Disconnection` es el mecanismo normal de logout → **no usar para bots**
- Constructor hace `stopAllTasks()` + `setClient(null)` — reutilizable
- Owner-disconnect hook: `GameClient.onDisconnection()` (líneas 91-97) → `Disconnection.of(this).onDisconnection()`
- **Punto de integración**: antes de `Disconnection.of(owner)`, ejecutar `TemporaryPlayerManager.despawnOwned(owner)`

---

## 18. Shutdown

### Fuente: `Shutdown.java`

#### `disconnectAllCharacters()` (líneas 560-565)
```java
for (Player player : World.getPlayers())
{
    Disconnection.of(player).storeAndDeleteWith(ServerClose.STATIC_PACKET);
}
```
- **PERSISTE todos los players, incluidos bots** si no se filtran
- Itera `World.getPlayers()` → bots serían incluidos automáticamente

#### `saveData()` (líneas 448-555)
- NO itera players — no hay autoSave de players aquí

### Hallazgo: VERDICT
- **Integración requerida**: `TemporaryPlayerManager.despawnAll()` ejecutar **antes** de `disconnectAllCharacters()`
- Punto de enganche: `Shutdown.runShutdown` antes de `saveData()` o al inicio de `disconnectAllCharacters()`

---

## 19. PlayerAutoSaveTaskManager

### Fuente: `taskmanagers/PlayerAutoSaveTaskManager.java`

#### Mecanismo (líneas 37-84)
```java
private static final Map<Player, Long> PLAYER_TIMES = new ConcurrentHashMap<>();
// Thread cada 1s: si player.isOnline() → player.autoSave()
```

#### `add(Player)` (líneas 86-89)
- Solo llamado desde: `Player.java:7410`, `LoginServerThread.java:565`, `OfflinePlayTable.java:184`, `OfflineTraderTable.java:325`
- **Bot nunca pasa por estas rutas → nunca en autosave**

#### `remove(Player)` (líneas 91-94)
- Llamado en `Player.deleteMe()` (12144)
- Seguro llamar defensivamente en despawn

### Hallazgo: VERDICT
- Bot no registrado en autosave automáticamente
- Llamada `remove(bot)` en despawn es segura (defensiva)
- **No hay blocker**

---

## 20. Creature.onDecay

### Fuente: `entity/actor/Creature.java`

#### `onDecay()` (líneas 527-540)
```java
if (PlayerConfig.DISCONNECT_AFTER_DEATH && isPlayer())
{
    asPlayer().storeAndDeleteWith(...);  // ← PERSISTE
}
else decayMe();
```

### Fuente: `config/PlayerConfig.java` (línea 538)
```java
DISCONNECT_AFTER_DEATH = config.getBoolean("DisconnectAfterDeath", true);
```
- **Default = true** → al morir, onDecay persiste al jugador

### Hallazgo: VERDICT
- **Si bot muere y llega onDecay → persistencia accidental**
- Soluciones: cancelar decay task en despawn, o despawn directo tras muerte
- **No blocker, requiere cuidado en despawn**

---

## 21. OfflinePlayTable (no aplicable)

### Fuente: search — `OfflinePlayTable.java:184`
- Registra en `PlayerAutoSaveTaskManager`
- **No relevante**: OfflinePlay persiste offline; Temporary Player desaparece sin rastro
- Diferencia fundamental confirmada

---

## 22. ItemData — Items del Archer MVP

### Fuente: `L2J MOBIUS H5 SERVER\game\data\stats\items\00000-00099.xml`

#### Bow (item 14, línea 298)
```xml
<item id="14" type="Weapon" name="Bow">
```

#### Wooden Arrow (item 17, línea 372)
```xml
<item id="17" type="EtcItem" name="Wooden Arrow">
```
- EtcItemType.ARROW

#### Wooden Breastplate (item 23, línea 461)
```xml
<item id="23" type="Armor" name="Wooden Breastplate">
    <set name="armor_type" val="LIGHT" />
    <set name="bodypart" val="chest" />
```

### Hallazgo: VERDICT
- IDs verificados: Bow=14, Arrows=17, Chest=23 (Light armor_type)
- Craft vía `ItemData.getInstance().getTemplate(id)`

---

## 23. PlayerAppearance

### Fuente: `entity/actor/appearance/PlayerAppearance.java`

#### Constructor (línea 51)
```java
public PlayerAppearance(byte face, byte hColor, byte hStyle, boolean isFemale)
```
- Público
- Valores MVP: `new PlayerAppearance((byte)0, (byte)0, (byte)0, false)`

#### `setOwner(Player)` (Player.java línea 908)
```java
app.setOwner(this);
```
- Llamado por Player constructor internamente

### Hallazgo: VERDICT
- Constructor público, sin dependencias adicionales

---

## 24. Creature.broadcastPacket

### Fuente: `entity/actor/Creature.java` (líneas 615-626)

```java
public void broadcastPacket(ServerPacket packet)
{
    packet.sendInBroadcast();
    World.forEachVisibleObject(this, Player.class, player -> {
        if (isVisibleFor(player)) player.sendPacket(packet);
    });
}
```
- `player.sendPacket` protegido por guard de sendPacket
- El bot también recibe broadcasts de otros players visible hacia él

### Hallazgo: VERDICT
- **Seguro clientless** — no diferencia players con/sin cliente

---

## 25. PlayerStat.setLevel

### Fuente: `entity/actor/stat/PlayerStat.java` (líneas 506-523)

```java
public void setLevel(byte value)
{
    byte level = value;
    if (level > (ExperienceData.getInstance().getMaxLevel() - 1))
        level = (byte) (ExperienceData.getInstance().getMaxLevel() - 1);
    // subclass index handling
    super.setLevel(level);
}
```
- **Runtime puro**
- Clampea al maxLevel-1
- Recalcula maxHp/maxCp/maxMp automáticamente (vía getters)
- **No persiste nada**

---

## 26. Callers de autoSave (verificación de seguridad)

### Fuente: search de `PlayerAutoSaveTaskManager.getInstance().add()`
1. `Player.java:7410` — login path
2. `LoginServerThread.java:565` — login
3. `OfflinePlayTable.java:184` — offline play
4. `OfflineTraderTable.java:325` — offline trade
5. `World.java:193` — solo en caso de duplicado
6. `Creature.java:534` — solo en onDecay con DisconnectAfterDeath

### Hallazgo: VERDICT
- **Ninguno de estos paths es alcanzable por un Temporary Player**
- El bot nace sin login, sin sesión, sin offline play
- El único path que podría alcanzar al bot (onDecay) debe cancelarse

---

## 27. SUMMARY EVIDENCE TABLE

| # | Finding | Source | Lines | Confianza |
|---|---|---|---|---|
| 1 | Player ctor privado funciona sin client | Player.java | 895-917 | ALTA |
| 2 | Player.create() persiste → evitar | Player.java | 1020-1042 | ALTA |
| 3 | sendPacket ya es clientless-safe | Player.java | 4429-4435 | ALTA |
| 4 | addSkill(skill,false) runtime-only | Player.java | 7998-8025 | ALTA |
| 5 | removeSkill 3 params runtime-only | Player.java | 8034-8038 | ALTA |
| 6 | storeMe() persiste → evitar | Player.java | 7637-7700 | ALTA |
| 7 | deleteMe() persiste → evitar | Player.java | 11734-12147 | ALTA |
| 8 | Creature.deleteMe() es runtime | Creature.java | 2805-2829 | ALTA |
| 9 | PlayerAI no requiere client | PlayerAI.java | 341-470 | ALTA |
| 10 | World.addObject no persiste | World.java | 175-202 | ALTA |
| 11 | IdManager es BitSet en memoria | IdManager.java | 108-177 | ALTA |
| 12 | Item.updateDatabase INSERT si !existsInDb | Item.java | 1481-1512 | ALTA |
| 13 | _wear flag es concepto no-persist existente | Item.java | 1656-1725 | ALTA |
| 14 | Party.sendPacket protegido por guard | Party.java | 253-325,615-640 | ALTA |
| 15 | SkillTrees 100% en memoria | SkillTreeData.java | 119-642 | ALTA |
| 16 | Shutdown itera World.getPlayers() | Shutdown.java | 560-565 | ALTA |
| 17 | Bot no registrado en autosave | AutoSaveTM | 86-89,71-78 | ALTA |
| 18 | onDecay persiste si DisconnectAfterDeath | Creature.java | 527-540 | ALTA |
| 19 | HAWKEYE classId=9, human, parent ROGUE | PlayerClass.java | 52 | ALTA |
| 20 | Bow=14, Arrow=17, Chest=23 (Light) | items XML | 298,372,461 | ALTA |
| 21 | setLevel es runtime | PlayerStat.java | 506-523 | ALTA |
| 22 | stopAllTasks runtime seguro | Player.java | 15185-15230 | ALTA |

