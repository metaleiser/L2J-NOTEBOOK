# MOBIUS — MAPA TÉCNICO DE PLAYER CLIENTLESS TEMPORAL

## 1. Objetivo

Implementar personajes auxiliares temporales que sean **Players reales de L2J Mobius**, capaces de existir y actuar en el mundo sin `GameClient`.

El primer caso de uso es un **Archer temporal PvE**:

```text
Jugador real
    │
    │ summon
    ▼
Temporary Archer
    │
    ├── Player real de Mobius
    ├── ObjectId propio
    ├── GameClient = null
    ├── nivel configurable
    ├── skills
    ├── arco
    ├── armadura light
    ├── World
    ├── MovementTaskManager
    ├── PlayerAI
    └── combate PvE
```

Al hacer despawn:

```text
Temporary Archer
    │
    ├── detener AI
    ├── detener movimiento
    ├── abortar combate/cast
    ├── abandonar party
    ├── eliminar summons
    ├── detener timers/tasks
    ├── limpiar objetos temporales
    ├── salir de World
    └── liberar referencia del manager
```

Resultado esperado:

```text
TEMPORARY ARCHER
        ↓
     DESPAWN
        ↓
SIN RASTRO PERSISTENTE
```

---

# 2. Descubrimiento fundamental

Mobius ya posee la entidad que necesitamos:

```text
org.l2jmobius.gameserver.model.actor.Player
```

No es necesario crear inicialmente una entidad paralela llamada `FakePlayer`.

`Player` ya contiene:

- identidad
- nivel
- estadísticas
- inventario
- equipamiento
- skills
- AI
- party
- target
- movimiento
- combate
- summons
- teleport
- death/revive
- estados
- macros
- shortcuts
- interacción con World

La diferencia fundamental es:

```text
Player normal
    =
Player + GameClient + sesión del usuario
```

mientras que nuestro bot será:

```text
Temporary Player
    =
Player - GameClient
```

con un lifecycle especial.

---

# 3. GameClient NO es el Player

Arquitectónicamente:

```text
Player
   │
   └── GameClient _client
```

`Player` mantiene el estado del personaje.

`GameClient` representa la conexión/sesión de red.

Por tanto:

```text
Player
 ├── puede existir
 ├── puede estar en World
 ├── puede tener AI
 ├── puede moverse
 ├── puede combatir
 ├── puede pertenecer a Party
 └── puede tener inventario

GameClient
 └── es necesario principalmente para comunicación cliente ↔ servidor
```

Mobius incluso contempla estados donde:

```java
player.getClient() == null
```

Esto no es una hipótesis: el propio lifecycle de `Disconnection` trabaja con `Player` sin cliente.

---

# 4. World

`World` mantiene directamente:

```text
_allPlayers
_allObjects
_allGoodPlayers
_allEvilPlayers
```

Por tanto, un Temporary Player puede convertirse en una entidad mundial normal:

```text
TemporaryPlayer
       │
       ▼
spawnMe()
       │
       ▼
World.addObject(this)
       │
       ▼
World._allPlayers
```

Esto es importante.

No necesitamos:

```text
FakeWorld
FakePlayerWorld
BotWorld
```

El bot pertenece al mismo mundo que los jugadores normales.

---

# 5. Spawn

`WorldObject.spawnMe()` realiza el proceso esencial:

```text
spawnMe()
   │
   ├── marcar spawned
   ├── obtener WorldRegion
   ├── World.addObject(this)
   ├── addVisibleObject()
   ├── World.addVisibleObject()
   └── onSpawn()
```

No necesita `GameClient` para introducir el objeto en el mundo.

Por tanto:

```text
Temporary Player
      ↓
spawnMe()
      ↓
World
```

es arquitectónicamente válido.

---

# 6. Movimiento

Mobius posee:

```text
MovementTaskManager
```

que procesa movimiento de `Creature`.

Para Players existe un pool específico y una frecuencia de procesamiento.

El flujo conceptual es:

```text
PlayerAI
   │
   │ MOVE_TO
   ▼
MovementTaskManager
   │
   ▼
Player.updatePosition()
   │
   ▼
World position
```

No depende de un cliente para realizar el movimiento server-side.

Esto permite que el Archer:

```text
buscar objetivo
      ↓
calcular posición
      ↓
moverse
      ↓
mantener distancia
      ↓
atacar
```

sin ejecutar un cliente Lineage 2.

---

# 7. PlayerAI

`PlayerAI` ya representa la inteligencia server-side del Player.

Puede trabajar con intenciones como:

```text
IDLE
MOVE_TO
ATTACK
CAST
...
```

Por ejemplo:

```text
Temporary Archer
       │
       ▼
PlayerAI
       │
       ├── target
       ├── MOVE_TO
       ├── ATTACK
       └── CAST
```

Esto significa que nuestro proyecto no debería comenzar creando un nuevo motor de combate.

Primero debemos reutilizar el motor existente.

---

# 8. AutoPlayTaskManager

Este punto es especialmente importante.

Mobius ya contiene:

```text
AutoPlayTaskManager
```

que controla Players server-side.

Entre otras cosas puede:

- seleccionar objetivos
- atacar
- moverse
- lanzar skills
- recoger objetos
- seguir al líder
- trabajar con party
- consultar criaturas visibles
- utilizar GeoEngine

El scheduler trabaja sobre objetos `Player`.

Por tanto:

```text
Player
   ↓
AutoPlayTaskManager
   ↓
AI / Movement / Combat
```

ya existe.

Esto reduce enormemente la cantidad de código que necesitamos inventar.

---

# 9. OfflinePlay como evidencia arquitectónica

Mobius ya implementa otro caso muy parecido:

```text
Offline Player
```

El sistema puede:

```text
Player.load()
     ↓
Player
     ↓
spawnMe()
     ↓
setOfflinePlay()
     ↓
AutoPlay
```

sin una conexión cliente normal.

Esto demuestra algo fundamental:

> Mobius ya acepta operacionalmente Players que continúan ejecutando lógica de juego sin una sesión de cliente activa.

Nuestro Temporary Player puede aprovechar esta arquitectura, pero **no debemos copiar indiscriminadamente OfflinePlay**, porque nuestro personaje no representa un usuario persistente desconectado.

---

# 10. Party

`Party` utiliza:

```text
List<Player>
```

El líder también es un `Player`.

Por tanto:

```text
Jugador real
      │
      ▼
    Party
      │
      ├── Jugador real
      ├── Archer temporal
      ├── Healer temporal
      └── Tank temporal
```

es conceptualmente compatible.

Pero existe una frontera:

```text
PARTY CORE
    🟢 Player-based

PARTY UI / PACKETS
    🟡 necesita client-awareness
```

Muchas operaciones de party notifican mediante paquetes al miembro.

Para un bot:

```text
bot.getClient() == null
```

esas notificaciones deben ser seguras.

No debemos parchear todo Party de entrada.

Primero debemos identificar exactamente qué operaciones utilizadas por nuestro bot requieren protección.

---

# 11. Inventario

`Player` ya posee:

```text
PlayerInventory
```

y el sistema de equipamiento de Mobius entiende:

```text
BodyPart
Paperdoll
Weapon
Armor
Stats
Skills
Augmentation
Enchant
Elemental
Set effects
```

Esto permite construir un Archer real:

```text
Player
   │
   └── Inventory
         │
         ├── Bow
         ├── Arrows
         ├── Light Armor
         └── Accessories
```

Pero existe un peligro crítico.

Algunas operaciones de equipamiento pueden llamar:

```text
item.updateDatabase()
```

Por lo tanto:

```text
crear Item
      ↓
equiparlo
      ↓
¿se escribe DB?
```

debe considerarse una frontera explícita.

Los objetos del Temporary Player deben ser:

```text
TEMPORALES
```

y no terminar accidentalmente asociados a un personaje persistente.

---

# 12. Player.create() NO debe utilizarse directamente

Mobius tiene:

```text
Player.create(...)
```

pero esta API está orientada a la creación persistente de personajes.

El flujo incluye:

```text
createDb()
```

Eso es exactamente lo que NO queremos.

Nuestro objetivo:

```text
TemporaryPlayerFactory
```

debe crear:

```text
Player runtime
```

sin:

```text
INSERT character
INSERT inventory
INSERT skills
INSERT variables
...
```

La solución debe utilizar el constructor/lifecycle interno adecuado de Mobius, o introducir una factory controlada dentro del source.

---

# 13. Constructor actual

El constructor principal de `Player` es privado.

Conceptualmente:

```text
Player(...)
   │
   ├── ObjectId
   ├── PlayerTemplate
   ├── accountName
   ├── appearance
   ├── stats
   ├── AI
   └── runtime state
```

Esto significa que la creación de Temporary Players debe resolver explícitamente:

```text
¿cómo construir un Player legítimo
sin pasar por la creación persistente?
```

Esta es una de las primeras cuestiones que Cline deberá estudiar en Plan Mode.

No debemos adivinar la solución.

---

# 14. ObjectId

El constructor utiliza:

```text
IdManager.getInstance().getNextId()
```

para obtener identidad del objeto.

El Temporary Player necesita:

```text
ObjectId único
```

exactamente como cualquier entidad mundial.

Pero el lifecycle de ese ID debe investigarse antes de implementar destrucción masiva de bots.

---

# 15. Client synchronization

Aquí está una de las principales zonas amarillas.

Un Player puede ejecutar:

```text
sendPacket(...)
broadcastUserInfo()
updateEffectIcons()
clientStopAutoAttack()
clientActionFailed()
...
```

Un Player temporal no tiene cliente.

Por tanto:

```text
SERVER-SIDE LOGIC
        🟢

CLIENT-SYNC LOGIC
        🟡
```

La regla de arquitectura será:

> El bot debe poder ejecutar lógica de juego sin depender de que exista un GameClient.

Pero no significa que tengamos que eliminar toda llamada de packet.

Primero hay que identificar cuáles ya son seguras con `client == null` y cuáles necesitan guard.

---

# 16. Death

La muerte está principalmente implementada en lógica server-side:

```text
doDie()
   │
   ├── combat state
   ├── regeneration
   ├── buffs
   ├── zones
   ├── quests
   ├── killer
   ├── PvP/karma
   └── AI.DEATH
```

Esto es reutilizable.

Pero nuevamente:

```text
broadcast
packet
client notification
```

debe ser tratado con cuidado.

No necesitamos implementar una segunda mecánica de muerte para bots salvo que el análisis demuestre una incompatibilidad.

---

# 17. Despawn

Este es probablemente el punto más importante de toda la arquitectura.

NO usar:

```text
Player.deleteMe()
```

como mecanismo normal de despawn.

`deleteMe()` pertenece al lifecycle persistente del personaje y realiza muchas operaciones:

```text
DB status
save variables
party cleanup
inventory cleanup
warehouse
freight
recommendations
clan
trade
instances
pet
quests
...
```

Eso sería excesivo y peligroso para un objeto temporal.

---

# 18. Lifecycle temporal propuesto

Debe existir una capa propia:

```text
TemporaryPlayerManager
```

con responsabilidades como:

```text
spawn()
despawn()
despawnAll()
get()
getByOwner()
```

y una factory:

```text
TemporaryPlayerFactory
```

que construya el Player.

Arquitectura:

```text
                ┌──────────────────────┐
                │ TemporaryPlayerManager│
                └──────────┬───────────┘
                           │
                           ▼
                ┌──────────────────────┐
                │ TemporaryPlayerFactory│
                └──────────┬───────────┘
                           │
                           ▼
                      Player real
                           │
             ┌─────────────┼──────────────┐
             ▼             ▼              ▼
           World          AI          Inventory
             │             │              │
             └─────────────┼──────────────┘
                           ▼
                         PvE
```

---

# 19. Ownership

Cada Temporary Player debe saber quién lo controla.

Por ejemplo:

```text
ownerPlayerId
```

o una referencia controlada al Player propietario.

Conceptualmente:

```text
REAL PLAYER #100
      │
      ├── Archer #5001
      ├── Healer #5002
      └── Tank #5003
```

Esto permitirá posteriormente:

```text
/summon archer
```

y:

```text
/despawn archer
```

sin mezclar bots de distintos jugadores.

---

# 20. Roles

No debemos crear un sistema separado por cada clase.

El modelo correcto es:

```text
TemporaryPlayer
       │
       └── Role Definition
             │
             ├── ARCHER
             ├── TANK
             ├── HEALER
             ├── BUFFER
             └── ...
```

Una definición de rol puede determinar:

```text
Class
Level
Race
Weapon
Armor
Skills
Combat style
Target rules
Positioning
AI behavior
```

Por ejemplo:

```text
ARCHER
 ├── ranged
 ├── bow
 ├── light armor
 ├── ranged attack
 ├── maintain distance
 └── PvE target selection
```

Esto será mucho más escalable que hardcodear:

```text
if Archer ...
if Tank ...
if Healer ...
```

en decenas de lugares.

---

# 21. Nivel

El nivel del bot debe ser una decisión explícita.

No debemos asumir inicialmente:

```text
bot level = owner level
```

aunque puede ser una opción.

La arquitectura debería permitir:

```text
BotLevelPolicy
```

por ejemplo:

```text
OWNER_LEVEL
OWNER_LEVEL - X
FIXED_LEVEL
ROLE_LEVEL
CAPPED_LEVEL
```

Para el primer prototipo basta una política simple.

---

# 22. Equipamiento

El primer Archer necesita:

```text
Bow
Arrows
Light Armor
```

pero el equipamiento debe ser generado a partir del nivel/rol.

No debemos empezar creando cientos de reglas.

Primera versión:

```text
ArcherDefinition
   │
   ├── level
   ├── weapon template
   ├── armor templates
   └── required skills
```

Después podremos evolucionar a:

```text
EquipmentProfile
```

por rangos de nivel.

---

# 23. Persistencia: regla de oro

Todo recurso creado exclusivamente para un Temporary Player debe responder:

```text
¿puede terminar en la DB?
```

Si la respuesta es:

```text
sí
```

hay que detenerse y resolverlo.

Objetivo:

```text
SUMMON
   ↓
runtime state
   ↓
DESPAWN
   ↓
DB unchanged
```

La persistencia accidental sería considerada un bug arquitectónico.

---

# 24. Primera prueba de concepto

NO intentar todavía:

```text
Archer perfecto
Party completo
Macros
Community Board
10 roles
Level scaling
Equipamiento dinámico
Persistencia
```

El primer milestone debe ser extremadamente pequeño:

```text
SUMMON ARCHER
      ↓
crear Player temporal
      ↓
sin GameClient
      ↓
spawn
      ↓
World
      ↓
equipar
      ↓
AI
      ↓
atacar una criatura PvE
      ↓
DESPAWN
      ↓
limpieza completa
```

---

# 25. Criterios de éxito del prototipo

El prototipo será considerado exitoso únicamente si demuestra:

### Identidad

```text
bot instanceof Player == true
```

### Clientless

```text
bot.getClient() == null
```

### World

El bot aparece correctamente en `World`.

### Movimiento

Puede desplazarse mediante infraestructura server-side.

### Combate

Puede atacar una criatura PvE.

### Equipment

Tiene comportamiento de Archer y utiliza arco.

### Ownership

Está asociado al jugador que lo creó.

### Despawn

Desaparece correctamente del mundo.

### Party

Debe poder evaluarse su incorporación a Party sin romper el servidor.

### Persistence

No crea un personaje permanente en DB.

### Cleanup

Después del despawn no quedan:

```text
World references
Party references
Tasks
Timers
Temporary items
Manager references
```

---

# 26. Arquitectura final propuesta

```text
                     REAL PLAYER
                          │
                          │ summon
                          ▼
              ┌─────────────────────────┐
              │ TemporaryPlayerManager  │
              └────────────┬────────────┘
                           │
                           ▼
              ┌─────────────────────────┐
              │ TemporaryPlayerFactory  │
              └────────────┬────────────┘
                           │
                           ▼
                   MOBIUS Player
                           │
             ┌─────────────┼─────────────┐
             │             │             │
             ▼             ▼             ▼
           World        PlayerAI      Inventory
             │             │             │
             │             │             ├── Weapon
             │             │             ├── Armor
             │             │             └── Skills
             │             │
             │             ▼
             │      Movement / Combat
             │
             ▼
            PvE
             │
             ▼
          Party
             │
             ▼
          Despawn
             │
     ┌───────┼────────┐
     ▼       ▼        ▼
    AI     Party     World
    stop   leave     decay
     │       │        │
     └───────┼────────┘
             ▼
       Temporary cleanup
             │
             ▼
          NO TRACE
```

---

# 27. Decisión arquitectónica

## GO

La investigación del source actual de Mobius demuestra que:

**no necesitamos construir un FakePlayer engine desde cero.**

La estrategia correcta es:

```text
REAL MOBIUS PLAYER
+
TEMPORARY LIFECYCLE
+
ROLE CONFIGURATION
+
CLIENTLESS SAFETY
```

El mayor trabajo no está en crear un nuevo motor de IA.

Está en hacer que un `Player` pueda tener un lifecycle:

```text
CREATE → RUN → DESPAWN
```

sin pasar por:

```text
LOGIN
CHARACTER CREATION
DATABASE PERSISTENCE
CLIENT SESSION
NORMAL LOGOUT
```

---

# 28. Regla para la siguiente fase

Antes de modificar código, Cline debe investigar específicamente:

1. Cómo crear un `Player` legítimo sin `Player.create()`.
2. Qué constructor/factory interna puede reutilizarse.
3. Qué inicialización necesita obligatoriamente.
4. Qué APIs explotan cuando `GameClient == null`.
5. Qué operaciones de Inventory/equipment escriben DB.
6. Qué lifecycle mínimo permite sacar un Player de World limpiamente.
7. Cómo liberar/cerrar todos los timers/tasks.
8. Cómo añadir/quitar un Temporary Player de Party.
9. Cómo aprovechar `AutoPlayTaskManager` sin convertir al bot en un personaje persistente.
10. Cómo garantizar que el despawn deje **cero persistencia accidental**.

**No implementar todavía.**

El siguiente paso correcto es un **Plan Mode de Cline exclusivamente de diseño/inspección**, para convertir este mapa conceptual en un contrato técnico concreto contra el source real.