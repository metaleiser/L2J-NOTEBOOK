# TEMPORARY PLAYER — TECHNICAL CONTRACT

## Estado: PROPOSED · **Vigencia:** DUPLICATE (candidato) · **Authority:** knowledge/TEMPORARY-PLAYER-TECHNICAL-CONTRACT.md

## 1. Definición

Un `TemporaryPlayer` es un `Player` real de L2J Mobius utilizado exclusivamente como entidad runtime temporal.

No es:

- un `FakePlayer` paralelo;
- un NPC;
- un summon;
- un personaje persistente;
- una cuenta;
- un cliente simulado.

Es:

```text
TemporaryPlayer
    =
Mobius Player
+
Temporary lifecycle
+
Owner
+
Role configuration
+
Clientless-safe execution
```

---

# 2. Identidad

Cada Temporary Player debe tener:

```text
objectId único
name único mientras exista
PlayerTemplate válido
PlayerAppearance válida
owner identificado
role identificado
```

El `objectId` debe proceder del mecanismo normal de Mobius.

No se deben fabricar IDs manualmente.

---

# 3. Estado de conexión

Condición fundamental:

```text
temporaryPlayer.getClient() == null
```

El bot no necesita:

- LoginServer
- GameClient
- socket
- packet input
- packet output
- cliente Lineage 2 ejecutándose.

El bot recibe órdenes exclusivamente desde lógica server-side.

---

# 4. Estado de existencia

El Temporary Player tendrá conceptualmente estos estados:

```text
CREATING
    ↓
CREATED
    ↓
SPAWNED
    ↓
ACTIVE
    ↓
DESPAWNING
    ↓
DESPAWNED
```

No debe existir una situación ambigua donde el manager crea que el bot está activo mientras `World` ya lo eliminó.

---

# 5. Ownership

Cada bot pertenece a un Player real.

```text
owner
   │
   ├── Temporary Archer
   ├── Temporary Healer
   └── Temporary Tank
```

El ownership permitirá posteriormente:

- limitar cantidad de bots;
- despawnear todos los bots del usuario;
- controlar permisos;
- asociar órdenes;
- identificar quién creó el bot;
- impedir que otro usuario controle el bot.

---

# 6. Manager

Debe existir un componente responsable de los Temporary Players.

Conceptualmente:

```text
TemporaryPlayerManager
```

Responsabilidades:

```text
create()
spawn()
despawn()
despawnAll(owner)
get(owner)
getAll(owner)
contains(bot)
```

El manager debe mantener las referencias de runtime.

No debe convertirse en una segunda implementación de `World`.

`World` continúa siendo la autoridad sobre objetos existentes en el mundo.

---

# 7. Factory

La creación debe estar separada del manager.

```text
TemporaryPlayerFactory
```

Responsabilidad:

```text
configuración
      ↓
Player válido
```

La factory debe resolver:

- ObjectId;
- template;
- appearance;
- account/identity runtime;
- nivel;
- skills;
- stats;
- inventory inicial;
- configuración de rol.

Pero NO debe encargarse del lifecycle completo del bot.

---

# 8. Role Definition

El rol debe ser datos/configuración, no una nueva jerarquía de clases gigantesca.

Conceptualmente:

```text
TemporaryRole
```

Ejemplo:

```text
ARCHER
 ├── class
 ├── weapon
 ├── armor
 ├── skills
 ├── attack range
 ├── combat behavior
 └── target behavior
```

En el futuro:

```text
TANK
HEALER
BUFFER
WARRIOR
ARCHER
MAGE
```

pueden utilizar la misma infraestructura.

---

# 9. Nivel

El nivel es una propiedad del bot.

El sistema debe separar:

```text
Owner Level
```

de:

```text
Bot Level
```

aunque inicialmente puedan ser iguales.

Ejemplo:

```text
Owner = Level 20

Archer:
Level 20
```

posteriormente:

```text
Owner Level 20
Archer Level 18
```

o:

```text
Owner Level 20
Archer Level 20 máximo
```

La política debe poder evolucionar sin reescribir el manager.

---

# 10. Equipamiento

El Temporary Player debe tener equipamiento real de Mobius.

Para Archer:

```text
Weapon
    Bow

Armor
    Light

Ammunition
    Arrows

Accessories
    opcionales
```

Debe utilizar el sistema normal de:

```text
PlayerInventory
Paperdoll
Item
Stats
Equipment listeners
```

No crear un sistema paralelo de estadísticas.

---

# 11. Skills

Las skills deben ser skills reales de Mobius.

El bot debe utilizar:

```text
Skill
SkillData
Player skill state
PlayerAI
```

cuando corresponda.

No crear:

```text
BotSkill
FakeSkill
BotDamageCalculator
```

sin evidencia de que Mobius no pueda realizarlo.

---

# 12. Combate

El combate debe pasar por las mismas capas utilizadas por un Player normal.

Objetivo:

```text
TemporaryPlayer
      ↓
PlayerAI
      ↓
Combat system
      ↓
Creature
```

El bot no debe tener:

```text
if bot:
    calculateDamageManually()
```

como primera solución.

La prioridad es reutilizar Mobius.

---

# 13. Movimiento

El bot debe utilizar:

```text
PlayerAI
MovementTaskManager
GeoEngine
```

cuando corresponda.

Para Archer:

```text
buscar target
     ↓
comprobar rango
     ↓
si fuera de rango:
    MOVE_TO
     ↓
si dentro de rango:
    ATTACK
```

El comportamiento específico del Archer puede vivir en una capa de control, pero el movimiento físico debe continuar utilizando la infraestructura Mobius.

---

# 14. AI Controller

Aquí debemos separar dos conceptos:

### Motor de Mobius

```text
PlayerAI
```

maneja las intenciones y ejecución base.

### Controlador del bot

```text
TemporaryPlayerController
```

decide qué quiere hacer el bot.

Ejemplo:

```text
TemporaryPlayerController
          │
          ├── findTarget()
          ├── evaluateDistance()
          ├── followOwner()
          ├── chooseSkill()
          └── setMobiusIntention()
```

Por tanto:

```text
Controller
    ↓
PlayerAI
    ↓
Movement / Combat
```

No debemos reemplazar `PlayerAI`.

---

# 15. Clientless Safety Layer

Debe existir una frontera clara entre:

```text
GAME LOGIC
```

y:

```text
CLIENT NOTIFICATION
```

El Temporary Player puede ejecutar:

```text
attack
move
cast
die
revive
equip
join party
```

sin cliente.

Pero algunas APIs de Mobius pueden intentar enviar:

```text
ServerPacket
SystemMessage
UserInfo
EffectIcons
ActionFailed
StopAutoAttack
```

Por eso Cline debe identificar exactamente qué llamadas requieren:

```text
if (getClient() != null)
```

o una abstracción equivalente.

No realizar un parche masivo de todos los `sendPacket()`.

---

# 16. Party Contract

El Temporary Player debe poder ser miembro de una `Party`.

```text
Party
 ├── real Player
 ├── temporary Archer
 ├── temporary Healer
 └── temporary Tank
```

El core de Party continuará trabajando con:

```text
Player
```

Las operaciones de UI/client notification deberán ser client-safe.

Primera versión:

```text
join
leave
follow
assist
loot
```

solamente en la medida necesaria para validar el prototipo.

---

# 17. Inventory Contract

Todo Item creado específicamente para un Temporary Player debe tener lifecycle temporal.

```text
CREATE ITEM
    ↓
ASSIGN TO BOT
    ↓
EQUIP
    ↓
USE
    ↓
DESPAWN
    ↓
CLEANUP
```

No debe convertirse en:

```text
Persistent Character Item
```

por accidente.

Especial atención a operaciones que llaman:

```text
updateDatabase()
```

---

# 18. Database Contract

Regla absoluta:

> El Temporary Player no representa un personaje persistente.

Por tanto, durante el lifecycle normal:

```text
SUMMON
ACTIVE
DESPAWN
```

no debe ocurrir:

```text
INSERT character
INSERT inventory
UPDATE character
UPDATE equipment
INSERT skills
INSERT variables
```

salvo que una futura feature explícitamente lo requiera.

---

# 19. Persistence Firewall

Antes de implementar equipamiento, skills o inventario temporal debe existir una respuesta clara a:

```text
¿Qué código puede tocar la DB?
```

Cualquier camino que pueda persistir información debe:

1. evitarse;
2. aislarse;
3. o recibir una adaptación específica para Temporary Players.

No confiar simplemente en:

```text
"después borramos el personaje"
```

porque eso convertiría el lifecycle temporal en un lifecycle persistente innecesariamente complejo.

---

# 20. Despawn Contract

El método conceptual:

```text
despawn(bot)
```

debe ser idempotente.

Es decir:

```text
despawn(bot)
despawn(bot)
```

no debe provocar errores ni corrupción.

Proceso esperado:

```text
DESPAWNING
    │
    ├── stop controller
    ├── stop movement
    ├── abort attack
    ├── abort cast
    ├── leave party
    ├── remove summons
    ├── stop temporary tasks
    ├── stop relevant timers
    ├── clean temporary inventory
    ├── decayMe()
    ├── verify World removal
    └── manager.remove(bot)
          │
          ▼
      DESPAWNED
```

---

# 21. `deleteMe()` prohibition

No utilizar:

```text
Player.deleteMe()
```

como mecanismo estándar de Temporary Player despawn.

Razón:

`deleteMe()` pertenece al lifecycle persistente de Player y realiza operaciones de logout, DB, inventory, warehouse, party, clan, instances, etc.

El Temporary Player necesita un lifecycle más pequeño y controlado.

---

# 22. World Contract

Cuando está activo:

```text
World.containsPlayer(bot)
```

debe ser verdadero.

Cuando termina el despawn:

```text
World.containsPlayer(bot)
```

debe ser falso.

También deben desaparecer sus referencias visibles correspondientes.

---

# 23. Task Contract

Todo task iniciado específicamente por el Temporary Player debe poder detenerse.

No debe existir:

```text
bot despawned
    ↓
task continúa ejecutándose
    ↓
task referencia bot muerto
```

El manager debe poder garantizar:

```text
ACTIVE
    → tasks permitted

DESPAWNING
    → tasks stopped

DESPAWNED
    → zero bot-owned tasks
```

---

# 24. Summon Contract

Si el Temporary Player posee summons propios:

```text
TemporaryPlayer
      ↓
Summon
```

el summon debe ser destruido antes de completar el despawn del bot.

Nunca dejar:

```text
bot = gone
summon = alive
```

sin ownership válido.

---

# 25. Error Contract

Si falla cualquier etapa de creación:

```text
create
template
inventory
equipment
skills
spawn
```

la factory/manager debe realizar rollback.

Ejemplo:

```text
CREATE
  ↓
EQUIPMENT ERROR
  ↓
cleanup partial Player
  ↓
NO WORLD OBJECT
  ↓
NO DB DATA
  ↓
NO leaked task
```

Un Temporary Player parcialmente creado nunca debe quedar vivo.

---

# 26. Concurrency Contract

El manager debe ser consciente de operaciones simultáneas:

```text
summon
despawn
death
disconnect owner
server shutdown
```

Ejemplo:

```text
Owner disconnects
       │
       ▼
TemporaryPlayerManager
       │
       ├── Archer
       ├── Healer
       └── Tank
             ↓
         despawnAll()
```

La implementación concreta de concurrencia deberá determinarse después de inspeccionar los patrones utilizados por Mobius.

---

# 27. Server shutdown

El sistema debe permitir:

```text
TemporaryPlayerManager.despawnAll()
```

durante shutdown.

Los Temporary Players no deben intentar persistirse como personajes normales.

---

# 28. Security Contract

El bot pertenece al owner.

Nunca debe aceptarse una orden como:

```text
despawn bot
```

si el bot pertenece a otro jugador, salvo una autoridad administrativa explícita.

Posteriormente esto permitirá:

```text
GM control
Community Board
owner commands
```

sin rediseñar ownership.

---

# 29. First implementation boundary

El primer prototipo NO implementará todo este contrato.

La primera implementación solamente debe demostrar:

```text
TemporaryPlayer
    ↓
create
    ↓
clientless
    ↓
spawn
    ↓
World
    ↓
equip Archer
    ↓
AI
    ↓
PvE attack
    ↓
despawn
    ↓
cleanup
```

Party, múltiples roles, Community Board y macros quedan fuera del primer milestone.

---

# 30. Architecture rule

La regla central del proyecto queda establecida:

> **Reutilizar Mobius antes de crear infraestructura paralela.**

Orden de preferencia:

```text
1. Player existente
2. PlayerAI existente
3. MovementTaskManager
4. AutoPlayTaskManager
5. Party existente
6. Inventory existente
7. Item/Skill existentes
8. World existente
9. Adaptación clientless mínima
10. Nueva infraestructura solamente cuando Mobius no pueda resolverlo
```

Esto evita repetir funcionalidades que el source ya posee.

---

# 31. Resultado esperado

El sistema final deberá permitir eventualmente:

```text
Jugador nivel 20
      │
      │ summon archer
      ▼
Archer temporal nivel 20
      │
      ├── Player real
      ├── sin GameClient
      ├── arco
      ├── light armor
      ├── skills
      ├── AI
      ├── movimiento
      ├── PvE
      └── Party
```

y:

```text
/despawn archer
      ↓
Player eliminado del World
      ↓
Party limpia
      ↓
Tasks limpias
      ↓
Items temporales limpiados
      ↓
Manager limpio
      ↓
DB intacta
```

Ese es el contrato que debe respetar cualquier futura implementación de bots.