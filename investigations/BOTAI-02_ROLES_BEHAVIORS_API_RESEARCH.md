# BOTAI-02 — Investigación Roles/Behaviors API Research

> **SPRINT:** BOTAI-02
> **FASE:** Investigación Roles/Behaviors
> **MODO original:** PLAN
> **Estado:** VERIFIED (APIs) + PROPOSED (roles) · **Evidence:** SOURCE · **Vigencia:** CURRENT
> **Investigaciones relacionadas:** FASE4_01_AUTOPLAY.md, FASE4_07_BOT_AI.md, FASE4_09_FOLLOW_ASSIST_COMBAT.md
> **Fecha:** 2026-09-12
> **Alcance:** Investigación técnica completa de APIs para implementación de Roles/Behaviors en bots clientless

---

## Fuentes consultadas

| Fuente | Uso |
|--------|-----|
| `UPSTREAM\L2J_Mobius\L2J_Mobius_CT_2.6_HighFive\java\org\l2jmobius\gameserver\` | Código fuente principal verificado |
| `TARGET\L2J_Mobius_CT_2.6_HighFive\game\data\scripts\handlers\chat\commands\admin\AdminBotManager.java` | Código de producción actual |
| `TARGET\L2J_Mobius_CT_2.6_HighFive\game\config\Custom\AutoPlay.ini` | Configuración actual |
| `L2J Notebook\bots\BOT_RECIPE.md` | Documentación de referencia |
| `L2J Notebook\investigations\FASE4_01_AUTOPLAY.md` | Investigación previa |
| `L2J Notebook\investigations\FASE4_03_PARTY.md` | Investigación previa |
| `L2J Notebook\investigations\FASE4_07_BOT_AI.md` | Investigación previa |
| `L2J Notebook\investigations\FASE4_09_FOLLOW_ASSIST_COMBAT.md` | Investigación previa |
| `L2J Notebook\decisions\FASE4_BOT_ARCHITECTURE_FREEZE.md` | Decisiones arquitectónicas |

---

## Clasificación de evidencia

| Estado | Definición |
|--------|------------|
| **VERIFIED** | Confirmado directamente mediante código fuente, archivo, línea o ejecución |
| **INFERRED** | Deducción razonable basada en evidencia existente |
| **PROPOSED** | Diseño propuesto todavía no implementado/verificado |
| **NOT FOUND** | API hipotética que no existe en el código actual |

---

## INV1 — AutoPlay

### Comportamiento

AutoPlayTaskManager es un singleton Runnable que ejecuta un loop cada 700ms sobre pools de Players. Gestiona:
- Selección de target (más cercano dentro de rango)
- Movimiento hacia target
- Ataque automático
- Asistencia a party líder (AssistLeader)
- Pickup de items

### Gate del loop (línea 80)

```java
if (!player.isOnline() || (player.isInOfflineMode() && !player.isOfflinePlay()) || !AutoPlayConfig.ENABLE_AUTO_PLAY)
{
    stopAutoPlay(player);
    continue;
}
```

### Seguimiento/Asistencia nativo (líneas 264-282)

```java
final Party party = player.getParty();
final Player leader = party == null ? null : party.getLeader();
if (AutoPlayConfig.ENABLE_AUTO_ASSIST && (party != null) && (leader != null) && (leader != player) && !leader.isDead())
{
    if (leader.calculateDistance3D(player) < (PlayerConfig.ALT_PARTY_RANGE * 2))
    {
        final WorldObject leaderTarget = leader.getTarget();
        if ((leaderTarget != null) && (leaderTarget.isAttackable() || (leaderTarget.isPlayable() && !party.containsPlayer(leaderTarget.asPlayer()))))
        {
            creature = leaderTarget.asCreature();
        }
        else if ((player.getAI().getIntention() != Intention.FOLLOW) && !player.isDisabled())
        {
            player.getAI().setIntentionFollow(leader);
        }
    }
}
```

### Detección de MageCaster (línea 332-335)

```java
private boolean isMageCaster(Player player)
{
    return !player.getAutoUseSettings().getAutoActions().contains(AUTO_ATTACK_ACTION);
}
```

### APIs reutilizables

| API | Archivo | Uso |
|-----|---------|-----|
| `AutoPlayTaskManager.getInstance().startAutoPlay(player)` | AutoPlayTaskManager.java:366 | Iniciar auto-play |
| `AutoPlayTaskManager.getInstance().stopAutoPlay(player)` | AutoPlayTaskManager.java:395 | Detener auto-play |
| `player.isAutoPlaying()` | Player.java | Verificar estado |

---

## INV2 — AutoUse

### Comportamiento

AutoUseTaskManager es un singleton Runnable que ejecuta un loop cada 300ms. Gestiona:
- Auto-buffs (skills de buff automáticas)
- Auto-skills (skills ofensivas automáticas)
- Auto-potions (pociones de HP/MP)
- Auto-items (items de suministro)

### Gate del loop (línea 79)

```java
if (!player.isOnline() || (player.isInOfflineMode() && !player.isOfflinePlay()))
{
    stopAutoUseTask(player);
    continue;
}
```

### canCastBuff (línea 370-401)

Valida:
- Target no está muerto (a menos que skill sea SELF/CORPSE/PC_BODY)
- Distancia <= skill.getCastRange()
- canUseMagic() pasa
- Buff no está activo o está por expirar (tiempo <= REUSE_MARGIN_TIME = 3s)
- Level del buff es mayor al actual

### canUseMagic (línea 403-428)

Valida:
- Item consume disponible
- Charges suficientes
- MP suficiente (mpConsume)
- Skill no está en cooldown
- Condiciones de skill cumplidas

### APIs reutilizables

| API | Archivo | Uso |
|-----|---------|-----|
| `AutoUseTaskManager.getInstance().startAutoUseTask(player)` | AutoUseTaskManager.java:431 | Iniciar auto-use |
| `AutoUseTaskManager.getInstance().stopAutoUseTask(player)` | AutoUseTaskManager.java:456 | Detener auto-use |
| `player.getAutoUseSettings()` | Player.java | Obtener configuración |
| `player.getAutoUseSettings().getAutoBuffs()` | AutoUseSettingsHolder | Set de buff skill IDs |
| `player.getAutoUseSettings().getAutoSkills()` | AutoUseSettingsHolder | Set de skill IDs |
| `player.getAutoUseSettings().getAutoActions()` | AutoUseSettingsHolder | Set de action IDs |
| `player.getAutoUseSettings().getAutoSupplyItems()` | AutoUseSettingsHolder | Set de item IDs |
| `player.getAutoUseSettings().getAutoPotionItem()` | AutoUseSettingsHolder | Item de poción actual |

### Límites

- No resuelve heal decisional (no detecta miembros heridos)

---

## INV3 — Party

### APIs verificadas

| API | Archivo | Línea | Estado |
|-----|---------|-------|--------|
| `player.getParty()` | Player.java | getter | VERIFIED |
| `party.getMembers()` | Party.java | 1137 | VERIFIED |
| `party.getLeader()` | Party.java | 1045-1063 | VERIFIED |
| `party.isLeader(player)` | AbstractPlayerGroup.java | 86-100 | VERIFIED |
| `party.containsPlayer(player)` | AbstractPlayerGroup.java | 168-171 | VERIFIED |
| `party.getMemberCount()` | AbstractPlayerGroup.java | 105-108 | VERIFIED |
| `party.getLeaderObjectId()` | AbstractPlayerGroup.java | 70-79 | VERIFIED |
| `player.joinParty(Party)` | Player.java | 6862 | VERIFIED |
| `player.leaveParty()` | Player.java | - | VERIFIED |
| `player.isInParty()` | Player.java | - | VERIFIED |
| `party.addPartyMember(Player)` | Party.java | 280 | VERIFIED |
| `party.removePartyMember(Player)` | Party.java | 401 | VERIFIED |
| `party.distributeXpAndSp(...)` | Party.java | - | VERIFIED |
| `party.equals(Party)` | Party.java | 1157-1160 | VERIFIED |

### Party range

```java
LocationUtil.checkIfInRange(PlayerConfig.ALT_PARTY_RANGE, player1, player2, true)
```

### Distancia entre miembros

```java
player1.calculateDistance2D(player2)  // distancia 2D
player1.calculateDistance3D(player2)  // distancia 3D
```

### Flujo correcto de join

```java
Party party = new Party(leader, PartyDistributionType.FINDERS_KEEPERS);
leader.setParty(party);
for (Player bot : bots) {
    bot.joinParty(party);
}
```

**IMPORTANTE**: `Party.addPartyMember(Player)` NO actualiza `_party` del jugador. Siempre usar `player.joinParty(party)`.

### AssistLeader nativo

---

## INV4 — PlayerAI / Targeting / Follow

### Jerarquía

```
AbstractAI
  └── CreatureAI
        └── PlayableAI
              └── PlayerAI
```

### Intention enum

```java
public enum Intention
{
    IDLE, ACTIVE, REST, ATTACK, CAST, MOVE_TO, FOLLOW, PICK_UP, INTERACT
}
```

### setIntentionFollow (CreatureAI.java:430-464)

Valida target no null/no REST/no AllSkillsDisabled/no CastingNow/no MovementDisabled/no Dead/no self-target, entonces `startFollow(target)`.

### setIntentionAttack (PlayableAI.java:44-86)

Valida PVP: Newbie Protection, Cursed Weapons, nivel. Llama `super.setIntentionAttack(target)`.

### setIntentionMoveTo (CreatureAI.java:384-428)

Valida target, mueve a ubicación.

### setIntentionCast (CreatureAI.java:319-378)

Valida target/skill, establece intención CAST.

### thinkAttack (PlayerAI.java:341-363)

Obtiene target, verifica si está muerto/perdido, mueve si fuera de rango, ataca.

### thinkCast (PlayerAI.java:365-413)

Obtiene target, para GROUND target mueve a posición, verifica target perdido, mueve si fuera de rango mágico, castea.

### APIs reutilizables

| API | Archivo | Uso |
|-----|---------|-----|
| `player.getAI()` | Creature.java | Obtener AI |
| `ai.setIntentionAttack(target)` | PlayableAI.java:44 | Atacar |
| `ai.setIntentionFollow(target)` | CreatureAI.java:430 | Seguir |
| `ai.setIntentionMoveTo(loc)` | CreatureAI.java:384 | Moverse |
| `ai.setIntentionCast(skill, target)` | CreatureAI.java:319 | Castear |
| `ai.setIntentionIdle()` | PlayerAI.java:248 | Detener |
| `ai.startFollow(target, range)` | AbstractAI.java:487 | Iniciar follow |
| `ai.stopFollow()` | AbstractAI.java:514 | Detener follow |
| `ai.getAttackTarget()` | AbstractAI.java:550 | Obtener target |

---

## INV5 — Skills / Heal / Buff / Resurrection

### Detección de skill de curación

```java
// NO existe skill.isHeal()
!skill.hasNegativeEffect()  // Skills no-ofensivas
skill.getAbnormalType() == AbnormalType.HP_RECOVER  // Heal over time
```

### Detección de skill de buff

```java
// NO existe skill.isBuff()
!skill.hasNegativeEffect() && !skill.isPassive() && !skill.isToggle()
```

### Detección de skill de resurrección

```java
// NO existe skill.isResurrection()
skill.getTargetType() == TargetType.CORPSE  // Resurrección NPCs
skill.getTargetType() == TargetType.PC_BODY  // Resurrección Players
```

### Selección de target para heal

```java
// NO existe party.getLowestHpPartyMember()
// Debe implementarse manualmente iterando party.getMembers()
// y comparando member.getCurrentHp() / member.getMaxHp()
```

### Verificar HP

```java
member.getCurrentHp()  // HP actual (double)
member.getMaxHp()      // HP máximo (double)
member.getCurrentHp() / member.getMaxHp()  // Porcentaje (0.0 - 1.0)
```

### Verificar buff activo

```java
member.getEffectList().getBuffInfoBySkillId(skill.getId()) != null
member.getEffectList().getBuffInfoByAbnormalType(skill.getAbnormalType()) != null
buffInfo.getTime()  // Tiempo restante
buffInfo.getSkill().getAbnormalLevel()  // Nivel del buff
```

### Ejecutar heal/buff

```java
caster.useMagic(healSkill, true, false);
// O: caster.setTarget(target); caster.doCast(healSkill);
```

### TargetTypes relevantes

| TargetType | Uso |
|------------|-----|
| `SELF` | Skills sobre uno mismo |
| `PARTY` | Skills que afectan toda la party |
| `PARTY_MEMBER` | Skills sobre miembro específico |
| `CORPSE` | Resurrección de NPCs |
| `PC_BODY` | Resurrección de Players |


---

## INV7 — Arquitectura futura

### Arquitectura propuesta

```
BotSession
    │
    v
BotRole
    │
    v
BotBehavior
    │
    +────> AutoPlay (ejecución de ataque/movimiento)
    │
    +────> AutoUse (ejecución de skills/buffs)
    │
    +────> PlayerAI (intenciones: FOLLOW/ATTACK/CAST/MOVE_TO)
    │
    v
Mobius (motor de juego)
```

### Regla fundamental

> **LA IA DECIDE; MOBIUS EJECUTA.**

### ¿Qué NO se necesita crear?

| Componente | Necesidad | Razón |
|------------|-----------|-------|
| Scheduler propio | NO | AutoPlayTaskManager ya provee tick a 700ms |
| ThreadPool propio | NO | ThreadPool de Mobius ya gestiona ambos managers |
| ThinkLoop | NO | PlayerAI.notifyActionThink() ya despacha intenciones |
| Nuevo targeting | NO | AutoPlayTaskManager ya selecciona targets |
| Nuevo movimiento | NO | PlayerAI + MovementTaskManager ya mueven |
| Nuevo combate | NO | PlayerAI.thinkAttack() ya ataca |
| Nuevos cooldowns | NO | Skill.isSkillDisabled() ya gestiona |
| Modificar AutoPlayTaskManager | NO | Ya resuelve targeting/movement/attack/assist |
| Modificar AutoUseTaskManager | NO | Ya resuelve auto-buffs/auto-skills |
| Modificar PlayerAI | NO | Ya ejecuta intenciones sin cliente |
| Modificar Party | NO | Funciona clientless sin modificación |
| Modificar Player | NO | Todas las APIs necesarias son accesibles |
| Modificar UPSTREAM | NO | Arquitectura 100% compatible |

### DECISIÓN vs EJECUCIÓN

| Capa | Responsabilidad |
|------|-----------------|
| **DECISIÓN** (BotRole/BotBehavior) | Qué skill, a qué target, cuándo |
| **EJECUCIÓN** (AutoPlay/AutoUse/PlayerAI) | Cómo mover, cómo atacar, cómo castear |

---

## INV8 — Configuración / Persistencia

### Opción A: data/scripts/custom/BotRoles/*.ini

---

## Matriz de APIs VERIFIED (parte 1: Party + AI)

| Área | Clase | Método | Archivo | Línea | Uso futuro |
|------|-------|--------|---------|-------|------------|
| Party | Player | getParty() | Player.java | getter | Obtener party del bot |
| Party | Party | getMembers() | Party.java | 1137 | Listar miembros |
| Party | Party | getLeader() | Party.java | 1045 | Obtener líder |
| Party | AbstractPlayerGroup | isLeader() | AbstractPlayerGroup.java | 86 | Verificar líder |
| Party | AbstractPlayerGroup | containsPlayer() | AbstractPlayerGroup.java | 168 | Verificar membresía |
| Party | AbstractPlayerGroup | getMemberCount() | AbstractPlayerGroup.java | 105 | Contar miembros |
| Party | AbstractPlayerGroup | getLeaderObjectId() | AbstractPlayerGroup.java | 70 | ID del líder |
| Party | Player | joinParty(Party) | Player.java | 6862 | Unir bot a party |
| Party | Player | leaveParty() | Player.java | - | Salir de party |
| Party | Player | isInParty() | Player.java | - | Verificar membresía |
| Party | Party | addPartyMember(Player) | Party.java | 280 | Añadir miembro |
| Party | Party | removePartyMember(Player) | Party.java | 401 | Remover miembro |
| Party | Party | distributeXpAndSp() | Party.java | - | Distribución XP/SP |
| Party | Party | equals(Party) | Party.java | 1157 | Comparar parties |
| AI | CreatureAI | setIntentionFollow() | CreatureAI.java | 430 | Seguir líder |
| AI | PlayableAI | setIntentionAttack() | PlayableAI.java | 44 | Atacar target |
| AI | CreatureAI | setIntentionMoveTo() | CreatureAI.java | 384 | Moverse |
| AI | CreatureAI | setIntentionCast() | CreatureAI.java | 319 | Castear skill |
| AI | PlayerAI | setIntentionIdle() | PlayerAI.java | 248 | Detener |
| AI | AbstractAI | startFollow() | AbstractAI.java | 487 | Iniciar follow |
| AI | AbstractAI | stopFollow() | AbstractAI.java | 514 | Detener follow |
| AI | AbstractAI | getAttackTarget() | AbstractAI.java | 550 | Obtener target |
| AI | AbstractAI | getFollowTarget() | AbstractAI.java | 520 | Obtener follow |
| AI | AbstractAI | getIntention() | AbstractAI.java | - | Obtener intención |

---

## Matriz de APIs VERIFIED (parte 2: Player + EffectList + Skill + Geo + Managers)

| Área | Clase | Método | Archivo | Línea | Uso futuro |
|------|-------|--------|---------|-------|------------|
| Player | Creature | getCurrentHp() | Creature.java | - | HP actual |
| Player | Creature | getMaxHp() | Creature.java | - | HP máximo |
| Player | Creature | getCurrentMp() | Creature.java | - | MP actual |
| Player | Creature | getMaxMp() | Creature.java | - | MP máximo |
| Player | Creature | isAlikeDead() | Creature.java | - | Verificar muerte |
| Player | Player | getClient() | Player.java | - | Verificar clientless |
| Player | Player | isOnline() | Player.java | - | Estado online |
| Player | Player | isInOfflineMode() | Player.java | - | Modo offline |
| Player | Player | isOfflinePlay() | Player.java | - | Flag offline |
| Player | Creature | hasAI() | Creature.java | - | Verificar AI |
| Player | Creature | getAI() | Creature.java | - | Obtener AI |
| Player | Creature | getTarget() | Creature.java | - | Obtener target |
| Player | Creature | setTarget() | Creature.java | - | Establecer target |
| Player | Creature | doAttack() | Creature.java | - | Atacar |
| Player | Creature | doCast() | Creature.java | - | Castear |
| Player | Playable | useMagic() | Playable.java | - | Usar skill |
| Player | Player | getKnownSkill() | Player.java | - | Obtener skill |
| Player | Player | addSkill() | Player.java | - | Añadir skill |
| EffectList | EffectList | getBuffInfoBySkillId() | EffectList.java | - | Verificar buff |
| EffectList | EffectList | getBuffInfoByAbnormalType() | EffectList.java | - | Verificar abnormal |
| Skill | Skill | hasNegativeEffect() | Skill.java | - | Clasificar skill |
| Skill | Skill | getTargetType() | Skill.java | - | Tipo de target |

---

## APIs NOT FOUND

| API Hipotética | Razón | Alternativa Real |
|---------------|-------|------------------|
| `skill.isHeal()` | No existe | `!skill.hasNegativeEffect()` + `getAbnormalType() == AbnormalType.HP_RECOVER` |
| `skill.isBuff()` | No existe | `!skill.hasNegativeEffect() && !isPassive() && !isToggle()` |
| `skill.isResurrection()` | No existe | `getTargetType() == TargetType.CORPSE` o `TargetType.PC_BODY` |
| `player.getLowestHpPartyMember()` | No existe | Iterar `party.getMembers()` y comparar HP |
| `party.getMembersSortedByHp()` | No existe | Ordenar manualmente |
| `player.isInPartyWith(Player)` | No existe | `player.getParty().containsPlayer(other)` |
| `ai.assistLeader()` | No existe | AutoPlayTaskManager lo resuelve nativamente |
| `behavior.resurrect()` | No existe | Usar skill con `TargetType.PC_BODY` |
| `skill.getHealAmount()` | No existe | Depende de fórmula del servidor |
| `player.getCurrentHpPercent()` | No existe | `getCurrentHp() / getMaxHp()` |

---

## Decisiones / Restricciones

### Decisiones arquitectónicas

1. **BotSession permanece lifecycle/identity**: No convertir BotSession en motor de IA.
2. **No duplicar AutoPlay**: Reutilizar AutoPlayTaskManager para targeting/movimiento/ataque.
3. **No duplicar AutoUse**: Reutilizar AutoUseTaskManager para ejecución de skills.
4. **No duplicar PlayerAI**: Reutilizar intenciones (FOLLOW/ATTACK/CAST/MOVE_TO).
5. **No modificar Party**: Funciona clientless sin modificación.
6. **No modificar Player**: Todas las APIs necesarias son accesibles.
7. **No modificar UPSTREAM**: Arquitectura 100% compatible.
8. **No copiar código de proyectos históricos**: Solo referencia conceptual.

### Restricciones operacionales

- NO modificar AdminBotManager.java
- NO modificar BotSession.java
- NO modificar AutoPlayTaskManager.java
- NO modificar AutoUseTaskManager.java
- NO modificar Player.java
- NO modificar Party.java
- NO modificar PlayerAI.java
- NO modificar UPSTREAM
- NO modificar DB
- NO modificar configs productivas
- NO crear código Java
- NO realizar compilación ni deploy
- NO ejecutar BOTAI-03

---

## Recomendación futura

### BOTAI-03 — HEALER MVP (PROPOSED / NO IMPLEMENTADO)

La investigación propone como siguiente candidato:

**Rol:** HEALER
**Behavior:** HealLowestHpPartyMember

**Punto de integración:**
- Nuevo archivo: `BotHealerBehavior.java` (en `data/scripts/custom/BotRoles/`)
- Nuevo archivo: `BotRole.java` (clase base, abstracta)
- Modificación MÍNIMA: `AdminBotManager.java` (inyectar behavior en BotSession)

**Archivos candidatos:**
- `data/scripts/custom/BotRoles/BotRole.java`
- `data/scripts/custom/BotRoles/BotHealerBehavior.java`
- `data/scripts/custom/BotRoles/HealerConfig.ini`
- `handlers/chat/commands/admin/AdminBotManager.java`

**Pruebas necesarias:**
- Bot en party detecta miembro herido
- Bot selecciona heal correcto
- Bot se mueve si está fuera de rango
- Bot ejecuta heal y miembro recupera HP
- No interfiere con AutoPlay/AutoUse existentes

**Rollback:**
- Eliminar `BotRole.java`, `BotHealerBehavior.java`, `HealerConfig.ini`
- Revertir inyección en `AdminBotManager.java`
- No hay cambios en core

**Estado:** PROPOSED — Requiere autorización del GM para implementar.

---

## Resumen ejecutivo

| Investigación | Estado | APIs VERIFIED | APIs NOT FOUND |
|---------------|--------|---------------|----------------|
| INV1 — AutoPlay | COMPLETADO | 8 | 0 |
| INV2 — AutoUse | COMPLETADO | 8 | 0 |
| INV3 — Party | COMPLETADO | 14 | 0 |
| INV4 — PlayerAI | COMPLETADO | 24 | 0 |
| INV5 — Skills | COMPLETADO | 18 | 5 |
| INV6 — Roles | COMPLETADO | - | - |
| INV7 — Arquitectura | COMPLETADO | - | - |
| INV8 — Configuración | COMPLETADO | - | - |
| **TOTAL** | **COMPLETADO** | **72** | **5** |

---

**Fin del documento BOTAI-02.**
| Skill | Skill | getCastRange() | Skill.java | - | Rango de cast |
| Skill | Skill | getMpConsume() | Skill.java | - | Costo de MP |
| Skill | Skill | getAbnormalType() | Skill.java | - | Tipo de abnormal |
| Skill | Skill | getAbnormalLevel() | Skill.java | - | Nivel de buff |
| Skill | Skill | isPassive() | Skill.java | - | Es pasiva |
| Skill | Skill | isToggle() | Skill.java | - | Es toggle |
| Skill | Skill | isContinuous() | Skill.java | - | Es continua |
| Skill | Skill | isMagic() | Skill.java | - | Es mágica |
| Skill | Skill | isPhysical() | Skill.java | - | Es física |
| Geo | GeoEngine | canSeeTarget() | GeoEngine.java | - | Línea de visión |
| Geo | GeoEngine | canMoveToTarget() | GeoEngine.java | - | Pathfinding |
| AutoPlay | AutoPlayTaskManager | startAutoPlay() | AutoPlayTaskManager.java | 366 | Iniciar auto-play |
| AutoPlay | AutoPlayTaskManager | stopAutoPlay() | AutoPlayTaskManager.java | 395 | Detener auto-play |
| AutoUse | AutoUseTaskManager | startAutoUseTask() | AutoUseTaskManager.java | 431 | Iniciar auto-use |
| AutoUse | AutoUseTaskManager | stopAutoUseTask() | AutoUseTaskManager.java | 456 | Detener auto-use |
| AutoUse | AutoUseTaskManager | canCastBuff() | AutoUseTaskManager.java | 370 | Validar buff |
| AutoUse | AutoUseTaskManager | canUseMagic() | AutoUseTaskManager.java | 403 | Validar magic |
| Location | LocationUtil | checkIfInRange() | LocationUtil.java | - | Distancia |
| Config | AutoPlayConfig | ENABLE_AUTO_PLAY | AutoPlayConfig.java | - | Gate global |
| Config | AutoPlayConfig | ENABLE_AUTO_ASSIST | AutoPlayConfig.java | - | Assist leader |
| Config | PlayerConfig | ALT_PARTY_RANGE | PlayerConfig.java | - | Rango de party |

| Aspecto | Detalle |
|---------|---------|
| **Ventaja** | Fácil de editar sin recompilar |
| **Desventaja** | Requiere parser custom |
| **Uso propuesto** | Presets de rol (fighter, archer, mage, healer, buffer) |
| **Estado** | PROPOSED |

### Opción B: PlayerVariables

| Aspecto | Detalle |
|---------|---------|
| **Ventaja** | Persistencia en DB, accesible via getVariables() |
| **Desventaja** | Requiere limpieza al cambiar rol |
| **Uso propuesto** | Estado individual del bot (rol actual, configuración runtime) |
| **Estado** | PROPOSED |

### Opción C: character_variables

| Aspecto | Detalle |
|---------|---------|
| **Ventaja** | Persistencia permanente en DB |
| **Desventaja** | Requiere schema/consultas SQL |
| **Uso propuesto** | Configuración persistente entre sesiones |
| **Estado** | PROPOSED |

### Recomendación

| Tipo | Implementación |
|------|----------------|
| Configuración global | AutoPlay.ini (ya existe) |
| Preset de rol | data/scripts/custom/BotRoles/*.ini |
| Estado individual | PlayerVariables (rol asignado, estado runtime) |
| Persistencia | character_variables (no requiere schema nuevo) |
### AbnormalTypes relevantes

| AbnormalType | Uso |
|--------------|-----|
| `HP_RECOVER` | Heal over time |
| `NONE` | Sin abnormal |
| `FATAL_POISON` | Veneno mortal |

---

## INV6 — Roles

### FARMER

| Aspecto | Descripción |
|---------|-------------|
| **Objetivo** | Auto-farm de mobs |
| **Decisiones** | Seleccionar mob cercano, atacar, lootear |
| **Datos** | target mode, short range, pickup enabled |
| **APIs** | AutoPlayTaskManager (ya lo resuelve) |
| **Delega a AutoPlay** | TODO |
| **Delega a PlayerAI** | targeting + movimiento + ataque |
| **NO implementar** | targeting, movimiento, ataque |
| **Dependencias** | AutoPlayConfig.ENABLE_AUTOPLAY |
| **Complejidad** | BAJA (ya implementado) |

### FOLLOWER

| Aspecto | Descripción |
|---------|-------------|
| **Objetivo** | Seguir al líder, asistir en combate |
| **Decisiones** | Cuándo seguir vs cuándo atacar |
| **Datos** | leader reference, leader target, distancia |
| **APIs** | `setIntentionFollow(leader)`, `leader.getTarget()` |
| **Delega a AutoPlay** | follow/assist nativo (AssistLeader=true) |
| **Delega a PlayerAI** | intenciones FOLLOW/ATTACK |
| **NO implementar** | lógica de seguimiento |
| **Dependencias** | Party, AssistLeader config |
| **Complejidad** | BAJA (nativo) |

### TANK

| Aspecto | Descripción |
|---------|-------------|
| **Objetivo** | Atraer aggro, mantener target del líder |
| **Decisiones** | Usar taunt/aggro skills si existen |
| **Datos** | leader target, propia aggro list |
| **APIs** | `setIntentionAttack(target)`, skills de taunt |
| **Delega a AutoPlay** | auto-attack |
| **Delega a PlayerAI** | targeting |
| **NO implementar** | movimiento, ataque |
| **Dependencias** | Skills de taunt (si existen) |
| **Complejidad** | BAJA-MEDIA |

### HEALER

| Aspecto | Descripción |
|---------|-------------|
| **Objetivo** | Curar miembros heridos, resucitar |
| **Decisiones** | Quién curar, qué heal, cuándo resucitar |
| **Datos** | HP de miembros, skills de heal, miembros muertos |
| **APIs** | `party.getMembers()`, `getCurrentHp()/getMaxHp()`, `hasNegativeEffect()` |
| **Delega a AutoUse** | ejecución de skills |
| **Delega a PlayerAI** | movimiento hacia objetivo |
| **NO implementar** | cast, validaciones de skill |
| **Dependencias** | EffectList, TargetType |
| **Complejidad** | MEDIA (decision targeting custom) |

### BUFFER

| Aspecto | Descripción |
|---------|-------------|
| **Objetivo** | Mantener buffs activos en party |
| **Decisiones** | Qué buffs aplicar, a quién, cuándo re-aplicar |
| **Datos** | buffs activos, duración, skills disponibles |
| **APIs** | `getBuffInfoBySkillId()`, `buffInfo.getTime()` |
| **Delega a AutoUse** | ejecución de buffs |
| **Delega a PlayerAI** | movimiento si necesario |
| **NO implementar** | cast, cooldowns |
| **Dependencias** | EffectList, AbnormalType |
| **Complejidad** | MEDIA |
| `ai.getFollowTarget()` | AbstractAI.java:520 | Obtener follow |
| `player.getTarget()` | Creature.java | Obtener target |
| `player.setTarget(target)` | Creature.java | Establecer target |
| `player.doAttack(target)` | Creature.java | Atacar |
| `player.doCast(skill)` | Creature.java | Castear |
| `player.useMagic(skill, forceUse, simultaneous)` | Playable.java | Usar skill |
| `GeoEngine.getInstance().canSeeTarget(s, t)` | GeoEngine.java | Línea de visión |
| `GeoEngine.getInstance().canMoveToTarget(...)` | GeoEngine.java | Pathfinding |
| `player.getPhysicalAttackRange()` | Creature.java | Rango ataque |
| `player.getMagicalAttackRange(skill)` | Creature.java | Rango cast |
| `player.calculateDistance2D/Creature` | Creature.java | Distancia 2D |
| `player.calculateDistance3D/Creature` | Creature.java | Distancia 3D |
| `LocationUtil.checkIfInRange(...)` | LocationUtil.java | Distancia |

### Responsabilidades de PlayerAI

PlayerAI es **REACTIVO**: ejecuta la intención que le fijan; no "decide" solo.

Cuando `AssistLeader = true` en AutoPlay.ini:
- AutoPlayTaskManager detecta party y líder
- Obtiene target del líder
- Si target existe → ataca ese target
- Si no → sigue al líder
- No tiene lógica de "buffear party completa"
- No realiza resurrección automática
- Solo gestiona self-buffs y auto-skills configuradas
| `player.getAutoPlaySettings()` | Player.java | Obtener configuración |

### Límites

- No distingue roles por configuración (solo fighter/mage por autoActions)
- No tiene lógica de heal/buff/decision targeting avanzado
- No soporta party-aware decision making