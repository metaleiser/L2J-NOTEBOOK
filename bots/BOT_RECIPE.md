# BOT_RECIPE.md — Receta Técnica de Bots Clientless

> **L2J Mobius CT 2.6 HighFive**
> Documento de referencia para implementaciones futuras con Cline.
> Distingue estrictamente: **VERIFICADO** / **PROPUESTA** / **EXPERIMENTO ELIMINADO**.
> **Estado:** VERIFIED · **Evidence:** RUNTIME · **Vigencia:** CURRENT · **Authority:** Arquitectura implementada de bots
> **Investigaciones relacionadas:** FASE4_01_AUTOPLAY.md, FASE4_03_PARTY.md, FASE4_17_PLAYER_CLIENT_STATE.md

---

## 1. Objetivo del sistema

Servidor privado/offline PvE single-player basado en L2J Mobius High Five, donde el jugador humano actúa como líder y utiliza personajes `Player` reales, persistidos en DB, como bots clientless.

**Objetivo actual:**

- humano + hasta 8 bots
- party máxima de 9 miembros
- bots sin `GameClient`
- AutoPlay nativo
- AutoUse nativo
- progresión sincronizable con el líder
- futura evolución hacia party PvE completa

---

## 2. Arquitectura VERIFICADA

> **VERIFICADO EN NUESTRO SERVIDOR**

Los bots son objetos `Player` reales cargados mediante `Player.load()`.

```text
DB
 ↓
Player.load()
 ↓
Player sin GameClient
 ↓
setOnlineStatus(...)
 ↓
setOfflinePlay(...)
 ↓
spawnMe(...)
 ↓
AutoPlay / AutoUse
 ↓
Party
```

El sistema funciona sin cliente L2 conectado para los bots.

---

## 3. Ciclo de vida VERIFICADO

> **VERIFICADO EN NUESTRO SERVIDOR**

Flujo de activación probado:

```text
Player.load
→ guard getClient()==null
→ setOnlineStatus(true,false)
→ setOfflinePlay(true)
→ spawnMe
→ configuración AutoPlay
→ startAutoPlay

---

## 7. Provisioning

> **VERIFICADO EN NUESTRO SERVIDOR**

```text
db\0005_botmanager_provisioning.sql
db\gen_0005_botmanager_provisioning.ps1
```

El provisioning crea personajes persistentes y su equipamiento inicial.

**Problema histórico documentado:**

```text
classid=2
base_class=0
```

era inconsistente para esta versión de Mobius. No recomendar copiar ese estado.

---

## 8. Problema classid/base_class

> **VERIFICADO EN NUESTRO SERVIDOR**

Problema observado durante `Player.load()`:

```text
Player:BSBOT01[...] reverted to base class...
```

En esta versión un personaje single-class normal debe mantener:

```text
classid == base_class
```

Ejemplo Gladiator:

```text
classid=2
base_class=2
```

---

## 9. Solución VERIFICADA

> **VERIFICADO EN NUESTRO SERVIDOR**

Solución nativa utilizada en FASE 4T:

```java
bot.setPlayerClass(2);
bot.setBaseClass(2);
bot.giveAvailableSkills(true, true, true);
bot.store(false);
```

Resultado probado:

---

## 13. AutoUse VERIFICADO

> **VERIFICADO EN NUESTRO SERVIDOR**

El executor nativo ya funciona para un `Player` clientless.

Componentes investigados:

```text
AutoUseTaskManager
AutoUseSettings
AutoPlay
PlayerVariables
```

Holders:

```text
_autoSkills
_autoBuffs
_autoSupplyItems
_autoPotionItem
_autoActions
```

`_autoActions` con acción `2` permite ataque básico.

---

## 14. AutoUse: causa del problema inicial

> **VERIFICADO EN NUESTRO SERVIDOR**

AutoUse no estaba fallando como executor.

El problema era que los holders estaban vacíos:

```text
_autoSkills
_autoBuffs
_autoSupplyItems
_autoPotionItem
```

Por eso:

- ataque básico funcionaba
- skills automáticas no
- pociones no
- items automáticos no

---

## 15. Criterio NATIVO para skills ofensivas

> **VERIFICADO EN NUESTRO SERVIDOR — DATO CRÍTICO**

En esta versión de Mobius:

```java
skill.hasNegativeEffect()

---

## 19. AutoUse PERMANENTE — PROPUESTA, NO IMPLEMENTADA

> ⚠️ **PROPUESTA / PENDIENTE — NO IMPLEMENTADO**

Plan de FASE 4V-F:

- inicializar AutoUse después de sincronizar nivel
- recorrer skills disponibles
- excluir passive/toggle
- respetar `AutoPlayConfig.DISABLED_AUTO_SKILLS`
- clasificar mediante `hasNegativeEffect()`
- registrar ofensivas en `getAutoSkills()`
- registrar buffs en `getAutoBuffs()`
- mantener inicialmente items/pocations fuera
- no persistir AutoUse por ahora
- volver a inicializar después de nuevas skills

**No presentar este código como código existente.**

---

## 20. Riesgo pendiente de verificar

> **PROPUESTA / PENDIENTE**

`getAllSkills()` puede contener skills que `.playskills` filtra adicionalmente.

> No copiar ciegamente `.playskills` ni asumir que todas las skills devueltas por `getAllSkills()` deben entrar en AutoUse.

La implementación permanente debe verificarse contra el código actual de:

```text
AutoPlay.java
AutoUseTaskManager.java
AutoUseSettings.java
Skill.java
AutoPlayConfig
```

---

## 21. Estado actual del servidor

> **VERIFICADO EN NUESTRO SERVIDOR**

- servidor objetivo no debe modificarse durante esta fase
- BSBOT01 reparado
- classid/base_class = 2/2
- level sync implementado para BSBOT01
- AutoPlay funcionando
- Power Strike probado end-to-end temporalmente
- experimento temporal eliminado del código
- `AdminBotManager.java` en SHA:

```text
910C28378145CBD1598948977D91D84BC2DD06FB1F85BA14770B769388B4F3DD
```

- GameServer actualmente puede tener en memoria una versión anterior al último cleanup debido al `ScriptClassLoader`; **no reiniciar durante este sprint**.
- La versión limpia está en disco.

---

## 22. Próximos pasos

### COMPLETADO

- FASE 4F — corrección de BOT_IDS
- FASE 4G / 4G.2 — restart controlado y eliminación de BotSpikeTest1
- FASE 4H — sintaxis del comando
- FASE 4I — primer runtime test
- FASE 4J — validación de comportamiento
- FASE 4K — activación de AutoPlay
- FASE 4M — investigación de macros y movimiento
- FASE 4N — prueba de macros
- FASE 4O — walk → run
- FASE 4P — limpieza
- FASE 4S — investigación de progresión
- FASE 4T — reparación de BSBOT01
- FASE 4U — sincronización de nivel
- FASE 4V-A — localización de fuentes estáticas
- FASE 4V-B — skill tree nivel 1/5
- FASE 4V-C — prueba de nivel 5
- FASE 4V-D / 4V-D.1 — prueba temporal de AutoUse + Power Strike
- FASE 4V-E — eliminación del experimento temporal
- FASE 4V-F — PLAN de implementación permanente de AutoUse

### PENDIENTE

1. AutoUse permanente.
2. Buffs automáticos.
3. Potions.
4. AutoSupplyItems.
5. sincronización de los 8 bots.
6. progresión de clases.
7. equipamiento proporcional al nivel.
8. roles de party.
9. healer/tank/DPS.
10. Community Board.

> No implementar ninguno durante este sprint.

---

*Documento generado para referencia técnica. Distingue VERIFICADO / PROPUESTA / EXPERIMENTO ELIMINADO.*
```

es el criterio utilizado por `.playskills` para clasificar una skill como ofensiva.

Implementación verificada:

```java
if (knownSkill.hasNegativeEffect())
{
    player.getAutoUseSettings().getAutoSkills().add(skillId);
}
else
{
    player.getAutoUseSettings().getAutoBuffs().add(skillId);
}
```

**No usar:**

```java
skill.getOperateType().isActive()
```

como sustituto de este criterio.

**Tampoco afirmar que existe:**

```java
skill.isOffensive()
```

porque no existe en esta versión investigada.

---

## 16. Power Strike — PRUEBA END-TO-END

> **EXPERIMENTO TEMPORAL — ELIMINADO**

Prueba temporal de FASE 4V-D / 4V-D.1.

Se registró temporalmente:

```java
bot.getAutoUseSettings().getAutoSkills().add(3);
```

después de verificar:

```java
bot.getKnownSkill(3)
```

y:

```java
skill.hasNegativeEffect()
```

Resultado:

BSBOT01 ejecutó realmente:

```text
Power Strike
ID 3
```

contra un objetivo válido.

La cadena verificada fue:

```text
AutoUseTaskManager
→ getAutoSkills()
→ target válido
→ canUseMagic()
→ caster.useMagic(...)
→ Power Strike
```

> **Este experimento fue eliminado posteriormente.**

---

## 17. Experimentos TEMPORALES ELIMINADOS

> **EXPERIMENTO TEMPORAL / DESCARTADO — NO FORMAN PARTE DEL SISTEMA ACTUAL**

- `BotSpikeTest1.java`
- `BotMacros.java`
- `RepairBSBOT01.java`
- registro temporal de Power Strike de FASE 4V-D

Fueron pruebas temporales y fueron eliminados. No proponer reutilizarlos.

---

## 18. Macros

> **VERIFICADO EN NUESTRO SERVIDOR**

Se probaron macros mediante `MacroList` y `character_macroses`.

Macros probadas:

```text
BOT 1      → //bot 1
BOT STATUS → //bot status
BOT OFF    → //bot off
BOT 8      → //bot 8
```

Funcionaron y los datos permanecen en DB, pero el script temporal utilizado para crearlos fue eliminado.

```text
ANTES:
class=0
base=0
skills=4

DESPUÉS:
class=2
base=2
skills=48
```

> Nota: el script de reparación fue temporal y posteriormente eliminado. No dejar ningún script temporal como parte de la solución.

---

## 10. Skill progression VERIFICADA

> **VERIFICADO EN NUESTRO SERVIDOR**

Componentes:

- `Player.giveAvailableSkills(...)`
- `SkillTreeData`
- `ClassMaster`
- `PlayerStat`
- `ExperienceData`

Cambiar solamente el nivel no basta para garantizar que las skills necesarias estén disponibles.

Patrón probado:

```text
set level
→ set EXP
→ giveAvailableSkills(...)
→ store
```

---

## 11. Level Sync VERIFICADO

> **VERIFICADO EN NUESTRO SERVIDOR**

Método actualmente incorporado en:

`game\data\scripts\handlers\chat\commands\admin\AdminBotManager.java`

Conceptualmente:

```text
nivel del líder
→ ExperienceData.getExpForLevel(...)
→ bot.getStat().setLevel(...)
→ bot.getStat().setExp(...)
→ bot.getStat().setSp(0)
→ giveAvailableSkills(...)
→ store(false)
```

En FASE 4U fue probado con BSBOT01.

**Importante:** actualmente la sincronización fue aplicada deliberadamente solo a:

```text
BSBOT01 / 268483130
```

No afirmar que los 8 bots ya tengan sincronización permanente.

---

## 12. AutoPlay VERIFICADO

> **VERIFICADO EN NUESTRO SERVIDOR**

`game\config\Custom\AutoPlay.ini`

Actualmente:

```text
EnableAutoPlay = True
AssistLeader = True
```

Se verificó:

- AutoPlay activo
- follow
- assist
- attack
- movimiento
- party funcionando

Fue necesario:

```java
bot.setRunning();
```

después de:

```java
bot.restoreEffects();
```

para corregir el comportamiento walk → run en el contexto probado.
→ Party
```

Flujo de desmontaje histórico/probado:

```text
leaveParty()
→ setOfflinePlay(false)
→ stopAutoPlay()
→ PlayerAutoSaveTaskManager.remove()
→ stopVitalityTask()
→ setOnlineStatus(false,false)
→ decayMe()
```

> Nota: patrón verificado/histórico utilizado. No presentado como código recomendado nuevo.

---

## 4. Party

> **VERIFICADO EN NUESTRO SERVIDOR**

- líder humano = `ADMIN`
- hasta 8 bots
- máximo 9 miembros
- party real de L2J
- follow/assist/attack fueron probados
- no se requiere core modification para este sistema base

---

## 5. Comando actual

> **VERIFICADO EN NUESTRO SERVIDOR**

```text
//bot 1
//bot 2
...
//bot 8

//bot status
//bot off
//bot resetcd
```

Traducción:

```text
//bot 1  →  admin_bot 1
```

**Importante:**

```text
//admin_bot 1
```

es incorrecto porque terminaría enviando:

```text
admin_admin_bot 1
```

---

## 6. BOT_IDS VERIFICADOS

> **VERIFICADO EN NUESTRO SERVIDOR**

```text
268483130  BSBOT01
268483131  BSBOT02
268483132  BSBOT03
268483133  BSBOT04
268483134  BSBOT05
268483135  BSBOT06
268483136  BSBOT07
268483137  BSBOT08
```

Existen 8 personajes persistentes en DB.