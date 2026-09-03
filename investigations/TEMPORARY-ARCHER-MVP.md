# TEMPORARY ARCHER — MINIMUM VIABLE PROTOTYPE

## 1. Objetivo del MVP

Crear un único personaje auxiliar:

```text
Temporary Archer
```

que pueda ser invocado por un Player real y que:

1. sea un `Player` real de Mobius;
2. no tenga `GameClient`;
3. tenga un nivel determinado;
4. tenga clase/configuración de Archer;
5. tenga arco, flechas y armadura adecuada;
6. aparezca en el World;
7. pueda seleccionar una criatura PvE;
8. pueda desplazarse;
9. pueda atacar a distancia;
10. pueda permanecer activo;
11. pueda ser despawneado;
12. desaparezca completamente;
13. no cree un personaje persistente;
14. no deje datos permanentes en la DB.

---

# 2. Qué NO forma parte del MVP

No implementar todavía:

- Healer;
- Tank;
- Mage;
- Buffer;
- múltiples bots;
- sistema de macros;
- Community Board;
- tienda de bots;
- economía;
- progresión independiente;
- equipamiento sofisticado;
- IA avanzada;
- formación de party;
- comportamiento social;
- navegación compleja;
- comandos definitivos para producción.

Todo eso depende de que el Archer básico funcione.

---

# 3. Flujo completo

El flujo conceptual será:

```text
PLAYER REAL
    │
    │ summon archer
    ▼
TemporaryPlayerManager
    │
    ▼
TemporaryPlayerFactory
    │
    ├── determinar nivel
    ├── seleccionar Archer
    ├── construir Player
    ├── inicializar runtime state
    ├── preparar skills
    └── preparar equipment
    │
    ▼
Temporary Archer
    │
    ▼
spawnMe()
    │
    ▼
World
    │
    ▼
TemporaryPlayerController
    │
    ├── buscar target
    ├── comprobar distancia
    ├── moverse
    └── atacar
    │
    ▼
PvE
    │
    │ despawn
    ▼
TemporaryPlayerManager
    │
    ▼
cleanup
    │
    ▼
NO TRACE
```

---

# 4. Identidad del Archer

El Archer debe tener:

```text
objectId
name
template
appearance
level
class
owner
role
```

El nombre debe permitir distinguirlo claramente durante el MVP.

Por ejemplo:

```text
[Bot] Archer
```

o una variante que respete las reglas de nombres de Mobius.

La forma exacta del nombre debe verificarse contra las restricciones existentes.

---

# 5. Nivel del Archer

Para la primera prueba:

```text
Bot Level = Owner Level
```

Ejemplo:

```text
Owner Level 20
        ↓
Archer Level 20
```

Esto simplifica la prueba y elimina inicialmente el problema de balance.

Pero la implementación debe evitar hardcodear:

```java
bot.setLevel(owner.getLevel());
```

en múltiples sitios.

Debe existir conceptualmente una política:

```text
BotLevelPolicy
```

aunque en MVP solamente tenga:

```text
OWNER_LEVEL
```

---

# 6. Clase

El Archer debe utilizar una clase real del sistema de clases de Mobius.

No inventar:

```text
BotArcherClass
```

La investigación de source debe determinar:

- qué `ClassId` utilizar;
- qué `PlayerTemplate` corresponde;
- qué stats iniciales necesita;
- qué skills recibe;
- qué restricciones de weapon/equipment existen.

La clase concreta debe ser determinada por el source de High Five, no por una suposición.

---

# 7. PlayerTemplate

La factory debe utilizar un `PlayerTemplate` válido de Mobius.

Conceptualmente:

```text
Role = ARCHER
       ↓
ClassId
       ↓
PlayerTemplate
       ↓
Player
```

El MVP no debe crear manualmente todos los stats del Archer si Mobius ya posee esa información.

Principio:

> Utilizar el template existente antes que reconstruir estadísticas manualmente.

---

# 8. Skills

El Archer debe recibir únicamente las skills necesarias para demostrar comportamiento válido.

Primera fase:

```text
Basic attack
+
mínimo conjunto de skills necesario
```

No intentar cargar toda la progresión de skills de un personaje real hasta que el lifecycle esté probado.

La investigación debe determinar si existe una API segura para cargar/aplicar skills sin tocar persistencia.

---

# 9. Weapon

El MVP necesita:

```text
Bow
```

y munición compatible.

El arma debe:

- existir como `Item` real;
- ocupar el slot correcto;
- producir las estadísticas correspondientes;
- permitir ataque a distancia.

No implementar un ataque artificial como:

```text
dealDamage(target, arbitraryDamage)
```

El objetivo es demostrar que el sistema normal de combate reconoce al bot como Archer.

---

# 10. Armor

El MVP necesita:

```text
Light Armor
```

suficiente para demostrar el perfil del Archer.

No necesitamos inicialmente construir un set completo perfecto.

La prioridad es:

```text
Player
+
weapon
+
armor
+
valid combat state
```

---

# 11. Items temporales

Los items creados para el Archer deben ser identificables como temporales.

Conceptualmente:

```text
TemporaryItemOwnership
```

o un mecanismo equivalente.

La implementación concreta debe investigarse.

El objetivo es que:

```text
Bot created
    ↓
items exist
    ↓
Bot despawned
    ↓
items no longer persist
```

---

# 12. Posición inicial

El Archer debe aparecer cerca de su owner.

No utilizar una posición arbitraria del mapa.

Conceptualmente:

```text
Owner
  │
  └── spawn offset
          ↓
      Archer
```

El offset debe respetar:

- distancia segura;
- GeoEngine;
- posición válida;
- no aparecer dentro del owner;
- no aparecer dentro de una pared.

Para MVP puede utilizarse un offset sencillo si la infraestructura existente lo permite.

---

# 13. Target inicial

El Archer debe seleccionar únicamente criaturas válidas para PvE.

Primera regla:

```text
target.isCreature()
AND
target.isAttackable()
AND
target != owner
```

No buscar jugadores.

No atacar:

- Players;
- summons del owner;
- otros Temporary Players;
- NPCs no atacables;
- objetos no combatibles.

El MVP debe ser estrictamente PvE.

---

# 14. Target selection

No crear todavía un sistema sofisticado.

Primera estrategia:

```text
buscar criaturas attackable
dentro de un radio razonable
```

y seleccionar una válida.

Posteriormente podremos evolucionar a:

```text
TargetPriority
    ├── owner target
    ├── owner attacked target
    ├── nearest hostile
    ├── lowest HP
    └── role-specific target
```

Pero no en MVP.

---

# 15. Comportamiento Archer

La máquina de comportamiento mínima será:

```text
IDLE
  │
  ▼
SEARCH TARGET
  │
  ▼
TARGET FOUND?
  │
  ├── NO → SEARCH
  │
  └── YES
        │
        ▼
    CHECK RANGE
        │
        ├── OUT OF RANGE
        │       ↓
        │     MOVE
        │       ↓
        │    CHECK RANGE
        │
        └── IN RANGE
                ↓
              ATTACK
                ↓
          TARGET DEAD?
             │
        ┌────┴────┐
        │         │
       YES        NO
        │          │
        ▼          ▼
     SEARCH    CONTINUE
```

---

# 16. Ranged behavior

El Archer debe intentar mantener una distancia de combate razonable.

No queremos inicialmente:

```text
Archer
   ↓
corre hasta pegar cuerpo a cuerpo
```

Queremos demostrar:

```text
Archer
   ↓
mantiene rango
   ↓
ataca con arco
```

La distancia exacta debe derivarse de las propiedades reales del arma/skill cuando sea posible.

No hardcodear arbitrariamente una distancia enorme.

---

# 17. AutoPlayTaskManager

Debe investigarse primero si el MVP puede apoyarse directamente en:

```text
AutoPlayTaskManager
```

para:

- target;
- movimiento;
- ataque;
- skills.

Si puede hacerlo de forma segura, esa debe ser la primera opción.

Solamente crear `TemporaryPlayerController` adicional donde AutoPlay no cubra el comportamiento necesario.

Esto evita duplicar la IA existente.

---

# 18. PlayerAI

La regla es:

```text
TemporaryPlayerController
             ↓
        PlayerAI
             ↓
      Mobius execution
```

El controller decide.

`PlayerAI` ejecuta las intenciones utilizando la infraestructura existente.

No reemplazar PlayerAI.

---

# 19. Combat

El ataque debe utilizar las APIs normales de Mobius.

La prueba ideal es:

```text
Archer
   ↓
PlayerAI
   ↓
ATTACK
   ↓
normal Mobius combat
   ↓
Monster receives damage
```

El daño debe provenir del sistema normal.

Esto validará simultáneamente:

- stats;
- weapon;
- attack range;
- combat;
- target;
- AI.

---

# 20. Death del Archer

Si el Archer muere durante el MVP:

```text
TemporaryPlayer
    ↓
normal Player death lifecycle
```

Debe investigarse qué ocurre exactamente con:

- AI;
- controller;
- timers;
- inventory;
- owner reference.

No implementar todavía resurrection automática.

Primero determinar el comportamiento natural de Mobius.

---

# 21. Despawn manual

Debe existir una acción equivalente a:

```text
despawn Archer
```

que pase por:

```text
TemporaryPlayerManager
```

Nunca directamente:

```text
bot.deleteMe()
```

El manager es la autoridad del lifecycle temporal.

---

# 22. Despawn después de la muerte

Para MVP se deben contemplar dos caminos:

```text
ACTIVE
   │
   └── manual despawn

DEAD
   │
   └── cleanup
```

No asumir que ambos pueden utilizar exactamente el mismo método.

Cline deberá comprobarlo contra el source.

---

# 23. Owner logout/disconnect

El owner puede desaparecer mientras el Archer está vivo.

Debe definirse:

```text
Owner disconnect
      ↓
TemporaryPlayerManager
      ↓
despawnAll(owner)
```

Esto evita bots huérfanos.

Para el MVP esto puede ser una regla sencilla:

> Si el owner deja de existir como Player válido, sus Temporary Players deben eliminarse.

---

# 24. Server shutdown

Todos los Temporary Players deben poder eliminarse:

```text
Server shutdown
      ↓
TemporaryPlayerManager.despawnAll()
```

sin persistencia.

---

# 25. Persistencia

El MVP se considera fallido si crea:

```text
Character DB row
```

para el Archer.

También se considera fallido si al equiparlo genera accidentalmente registros persistentes que sobreviven al despawn.

---

# 26. Prueba de "no trace"

Después de:

```text
summon
→ fight
→ despawn
```

debe comprobarse:

### World

```text
bot no longer present
```

### Party

```text
bot no longer present
```

### Tasks

```text
no bot-owned task active
```

### Inventory

```text
temporary items cleaned
```

### Manager

```text
bot reference removed
```

### Database

```text
no persistent character
no persistent inventory
no persistent equipment
```

---

# 27. Observabilidad del MVP

Durante el desarrollo debe existir suficiente logging para detectar:

```text
CREATE
SPAWN
TARGET
ATTACK
DESPAWN
CLEANUP
```

Ejemplo conceptual:

```text
[TemporaryPlayer] Created Archer objectId=...
[TemporaryPlayer] Spawned Archer objectId=...
[TemporaryPlayer] Target acquired objectId=...
[TemporaryPlayer] Despawn requested objectId=...
[TemporaryPlayer] Cleanup complete objectId=...
```

El logging puede ser temporal durante desarrollo.

---

# 28. Pruebas mínimas

### Test A — Create

```text
summon archer
```

Resultado:

```text
Player creado
client == null
```

### Test B — World

Archer visible en el mundo.

### Test C — Movement

Archer puede desplazarse.

### Test D — PvE

Archer ataca un monstruo.

### Test E — Equipment

Archer utiliza arco.

### Test F — Despawn

Archer desaparece.

### Test G — Repeat

```text
summon
despawn
summon
despawn
```

varias veces sin leaks.

### Test H — DB

Después de todas las pruebas:

```text
DB unchanged
```

respecto al bot.

---

# 29. Criterio de "GO"

El MVP pasa si:

```text
REAL PLAYER
      +
NO CLIENT
      +
WORLD
      +
MOVEMENT
      +
BOW
      +
PVE COMBAT
      +
DESPAWN
      +
NO PERSISTENCE
```

funcionan conjuntamente.

No necesitamos todavía una IA inteligente.

Necesitamos demostrar:

> **un Player real de Mobius puede comportarse como un personaje PvE temporal sin cliente y desaparecer sin dejar persistencia.**

---

# 30. Criterio de "STOP"

Se detiene el desarrollo y se vuelve a investigar si aparece cualquiera de estos problemas:

```text
Player creation requires persistent DB
        ↓
STOP — investigate factory/lifecycle
```

```text
Equipment necessarily persists
        ↓
STOP — design temporary item strategy
```

```text
PlayerAI requires GameClient
        ↓
STOP — isolate client dependency
```

```text
World requires GameClient
        ↓
STOP — investigate lifecycle
```

```text
Despawn leaves active tasks
        ↓
STOP — fix lifecycle before expanding
```

La regla es:

> No solucionar problemas arquitectónicos con hacks locales.

---

# 31. Evolución posterior

Una vez aprobado el Archer:

```text
MVP Archer
     ↓
Temporary Player Core
     ↓
Role System
     ↓
Level Scaling
     ↓
Party Integration
     ↓
Healer
     ↓
Tank
     ↓
Mage
     ↓
Community Board
     ↓
Macros
```

El Archer no debe contener código que impida esta evolución.

---

# 32. Arquitectura resultante

```text
                    PLAYER
                       │
                       │ summon
                       ▼
             TemporaryPlayerManager
                       │
                       ▼
             TemporaryPlayerFactory
                       │
                       ▼
              ┌──────────────────┐
              │   MOBIUS PLAYER   │
              └────────┬─────────┘
                       │
          ┌────────────┼────────────┐
          ▼            ▼            ▼
        World       PlayerAI     Inventory
          │            │            │
          │            ▼            ├── Bow
          │        Controller        ├── Arrows
          │            │             └── Light
          │            ▼
          │       AutoPlay / Combat
          │            │
          └────────────┼────────────
                       ▼
                       PvE
                       │
                       ▼
                    Despawn
                       │
                       ▼
                    Cleanup
                       │
                       ▼
                   NO TRACE
```

---

# 33. Decisión

El primer experimento técnico será:

> **Un único Archer temporal, basado en `Player` real de Mobius, clientless, PvE, con equipamiento real y lifecycle temporal completo.**

No se implementarán todavía múltiples roles ni un sistema general de bots.

El objetivo de esta prueba no es hacer un bot espectacular.

Es responder definitivamente esta pregunta:

> **¿Podemos convertir un `Player` real de Mobius en una entidad temporal clientless reutilizando World + PlayerAI + Movement + Combat, sin contaminar la persistencia?**

Si la respuesta es sí, acabamos de validar la base de todo el sistema futuro.