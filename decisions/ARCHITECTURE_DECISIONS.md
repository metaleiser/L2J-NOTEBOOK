# ARCHITECTURE DECISION RECORD — Temporary Player / Clientless Archer

> **Proyecto:** INTELIGENCIA ARTESANAL L2J
> **Target:** L2J Mobius CT 2.6 HighFive
> **Contexto:** Investigación source para el diseño de un Player temporal clientless
> **Fecha:** Marzo 2026

---

## ADR-001: Usar Player real de Mobius, no FakePlayer

**Decisión:** Crear bots como instancias directas de `org.l2jmobius.gameserver.entity.actor.Player`, sin subclase ni entidad paralela.

**Razones:**
1. `Player` tiene constructor privado accesible desde factory del mismo paquete (Player.java:895-917)
2. `PlayerAI` se crea automáticamente en el constructor (Player.java:912)
3. InstanceType queda como `Player`, permitiendo `isPlayer()/asPlayer()` funcionar en todo el engine
4. World, MovementTaskManager, Party, Inventory, Skills, Combat — todo funciona sin modificación
5. FakePlayer requeriría duplicar toda la funcionalidad de Player y rompería `asPlayer()`

**Alternativas rechazadas:**
- FakePlayer entidad paralela → descartado por duplicación masiva
- Subclase TemporaryPlayer extends Player → descartado por InstanceType (Player.java:898)

**Evidencia:** Player.java:895-917, 898, 912

---

## ADR-002: Factory en mismo paquete para constructor privado

**Decisión:** Colocar `TemporaryPlayerFactory` en `org.l2jmobius.gameserver.entity.actor` (mismo paquete que Player).

**Razones:**
1. Constructor `Player(int, PlayerTemplate, String, PlayerAppearance)` es **private** (Player.java:895)
2. `Player.create()` es público pero persiste en DB (Player.java:1020-1042)
3. Factory en mismo paquete puede llamar al constructor privado sin reflection

**Alternativas rechazadas:**
- Reflection → frágil, lenta
- Modificar constructor a public → expone API interna
- Modificar Player.create() → cambiaría creación normal de personajes

**Evidencia:** Player.java:895, 1020

---

## ADR-003: Flag `_temporary` en Item como firewall de persistencia

**Decisión:** Añadir campo `boolean _temporary` + setter + guard en `Item.updateDatabase()`.

**Razones:**
1. `Item.updateDatabase()` (1481-1512) INSERTA en DB si !existsInDb y ownerId≠0 y loc≠VOID
2. Todas las rutas de equipamiento (`setPaperdollItem`, `addItem`, `equipItem`) llaman a updateDatabase()
3. Mobius ya tiene `_wear` flag (1658, 1691) que hace no-op — pero sin setter público

**Alternativas rechazadas:**
- Reutilizar `_wear` → sin setter público, semántica incorrecta
- OwnerId=0 → imposible (item necesita owner para estar en inventory)
- No usar items reales → requeriría engine paralelo de equipamiento

**Evidencia:** Item.java:1481-1512, 1656-1684, 1689-1725; Inventory.java:1072-1129

---

## ADR-004: No usar Player.deleteMe() para despawn

**Decisión:** Usar `Creature.deleteMe()` (runtime cleanup) en lugar de `Player.deleteMe()`.

**Razones:**
1. `Player.deleteMe()` (11734-12147) llama a `setOnlineStatus(false,true)` → UPDATE characters (7042-7055)
2. `Player.deleteMe()` ejecuta notifyFriends, zone logout, Olympiad cleanup — peligroso para bot
3. `Creature.deleteMe()` (2805-2829) solo limpia runtime: AI stop, effects stop

**Alternativas rechazadas:**
- Modificar Player.deleteMe() con guard → contaminaría logout normal
- Disconnection.storeAndDelete() → persiste

**Evidencia:** Player.java:11734-12147, 7042-7055; Creature.java:2805-2829

---

## ADR-005: Reutilizar PlayerAI, no crear controller AI

**Decisión:** Usar `PlayerAI.setIntentionAttack/MoveTo` directamente. Controller = task externo (TickTask) que fija intenciones.

**Razones:**
1. `PlayerAI.thinkAttack()` (341-363) no referencia GameClient
2. `PlayerAI.thinkCast()` (365-413) no referencia GameClient
3. AutoPlayTaskManager (líneas 115-327) prueba que PlayerAI funciona conducido externamente
4. `maybeMoveToPawn` usa MovementTaskManager → server-side con geodata

**Alternativas rechazadas:**
- Nueva clase BotAI → innecesario
- Controller con AI propia → sobreingeniería para MVP

**Evidencia:** PlayerAI.java:341-413; AutoPlayTaskManager.java:115-327

---

## ADR-006: Party sin loot para el bot

**Decisión:** Bot puede ser miembro de Party pero no debe ser looter.

**Razones:**
1. `Party.addPartyMember` (280-345) es seguro clientless
2. `Party.distributeItem()` (618-640) llama a `target.addItem()` → persiste items
3. Si bot recibe loot → items persistentes sin owner real

**Evidencia:** Party.java:280-345, 618-640; Item.java:1481-1512

---

## ADR-008: Owner disconnect → despawnOwned

**Decisión:** Hook en `GameClient.onDisconnection()` o `Disconnection.storeAndDelete()` para ejecutar `TemporaryPlayerManager.despawnOwned(owner)` antes del logout normal.

**Razones:**
1. `Shutdown.disconnectAllCharacters()` (560-565) itera `World.getPlayers()` → bots serían persistidos
2. `GameClient.onDisconnection()` (91-97) es el hook natural de desconexión
3. Bot debe ser despawned antes de que owner inicie persistence pipeline

**Evidencia:** Shutdown.java:560-565; GameClient.java:91-97; Disconnection.java:110-134

---

## ADR-009: Server shutdown → despawnAll antes de disconnectAllCharacters

**Decisión:** `TemporaryPlayerManager.despawnAll()` debe ejecutarse antes de `Shutdown.disconnectAllCharacters()`.

**Razones:**
1. disconnectAllCharacters() no discrimina entre bots y players reales
2. DespawnAll es runtime cleanup + releaseId
3. Después del despawn, World ya no contiene bots

**Evidencia:** Shutdown.java:560-565

---

## ADR-010: Skills vía SkillTreeData + addSkill(false)

**Decisión:** Calcular skills con `SkillTreeData.getAllAvailableSkills(player, HAWKEYE, false, true, false)` y añadirlas con `player.addSkill(skill, false)`.

**Razones:**
1. SkillTreeData es 100% en memoria (119-137)
2. getAllAvailableSkills calcula todas las skills aprendibles
3. addSkill(skill, false) es runtime-only (7998-8025)
4. Skills temporales desaparecen con el bot

**Alternativas rechazadas:**
- restoreSkills() desde DB → no existe character_skills
- storeSkills() → persistencia

**Evidencia:** SkillTreeData.java:119-137, 602-642; Player.java:7998-8025

---

## ADR-011: IdManager.releaseId() obligatorio en despawn

**Decisión:** Llamar `IdManager.releaseId(botId)` + `releaseId(itemId)` para cada item temporal en despawn.

**Razones:**
1. IdManager es BitSet en memoria (41-56) — IDs no se liberan automáticamente
2. Sin releaseId, cada summon consume un ID hasta agotar pool
3. releaseId() (158-177) es seguro y rápido

**Evidencia:** IdManager.java:108-152, 158-177

---

## ADR-012: Despawn debe abortar decay del bot

**Decisión:** En despawn, cancelar explícitamente la tarea de decay del bot si está muerto.

**Razones:**
1. `Creature.onDecay()` (527-540) con `DisconnectAfterDeath=true` → storeAndDeleteWith
2. Si bot muere y no se despawna antes del decay → persistencia accidental
3. DecayTaskManager debe ser notificado para cancelar la tarea

**Evidencia:** Creature.java:527-540; PlayerConfig.java:538

---

## ADR-013: Enfoques alternativos rechazados (documentados)

1. **FakePlayer (Npc con apariencia)**
   - Rechazado: no tiene AI de player, no entra en World._allPlayers
   - Ruptura: isPlayer()==false, no funciona con Party/MovementTaskManager

2. **Subclase TemporaryPlayer extends Player**
   - Rechazado: InstanceType se hereda, sigue siendo Player
   - No aporta beneficio, añade complejidad de herencia

3. **Cliente simulado (fake GameClient)**
   - Rechazado: innecesario — sendPacket() ya es null-safe
   - Crear socket falso para llamar APIs que ya funcionan sin él

4. **Modificación global de Player para guards de getClient()**
   - Rechazado: Player.sendPacket ya tiene guard
   - Riesgo: añadir ruido al código sin beneficio

5. **AutoPlayTaskManager como controller**
   - Rechazado: ligado a config de autoplay del cliente
   - Mejor: replicar patrón (setIntentionAttack) en controller propio

6. **Persistencia vía OfflinePlay**
   - Rechazado: OfflinePlay persiste personaje real con sesión
   - Diferencia fundamental: OfflinePlay ≠ Temporary