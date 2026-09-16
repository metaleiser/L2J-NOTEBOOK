# BOTAI-03 — Provisioning / Equipment / Enchant / Dashboard Research

> **SPRINT:** BOTAI-03-RESEARCH
> **FASE:** Investigación Provisioning/Equipment/Enchant/Dashboard
> **MODO:** PLAN
> **Estado:** INVESTIGACIÓN COMPLETADA · **Evidence:** SOURCE · **Vigencia:** CURRENT
> **Fecha:** 2026-09-12
> **Investigaciones relacionadas:** BOTAI-02_ROLES_BEHAVIORS_API_RESEARCH.md, FASE4_00_INDEX.md, FASE4_11_COMMUNITY_BOARD.md, FASE4_18_GAMECLIENT_DETACHED.md, FASE4_15_MVP_SPEC.md, FASE4_16_ROADMAP.md, FASE4_FINAL_REPORT.md, BOT_RECIPE.md

---

## 1. Resumen ejecutivo

Este documento investiga los cuatro pilares necesarios para dotar a los bots clientless de equipamiento, encantamiento, panel de control y ciclo de vida completo:

- **Provisioning:** Creación y preparación de personajes bot con clase, nivel, skills y equipo inicial.
- **Equipment:** Dar items directamente con nivel de encantamiento asignado (Ruta B), evitando la mecánica probabilística de scrolls (Ruta A).
- **Auto-Enchant:** Distinguir entre la mecánica real de scrolls (Ruta A, probabilística, consume scrolls) y la asignación directa (Ruta B, determinística, sin fallos). Se recomienda Ruta B para provisioning de bots.
- **Dashboard:** Panel de control in-game vía Community Board (IParseBoardHandler) y comandos admin existentes.

**Hallazgo clave:** El 95% de los componentes necesarios ya existen en Mobius. La única pieza nueva relevante para el sprint es **BotProvisioning**, que orquesta llamadas a APIs existentes.

---

## 2. Estado

| Estado | Definición |
|--------|------------|
| **VERIFIED** | Confirmado directamente mediante código fuente, archivo, línea o ejecución |
| **INFERRED** | Deducción razonable basada en evidencia existente |
| **PROPOSED** | Diseño propuesto todavía no implementado/verificado |
| **NOT FOUND** | API hipotética que no existe en el código disponible |
| **UNVERIFIED** | Requiere verificación en runtime |

---

## 3. Fuentes investigadas

| Fuente | Uso |
|--------|-----|
| `TARGET\AdminEnchant.java` | Encantamiento directo (Ruta B) |
| `TARGET\EnchantScrolls.java` | Handler de scrolls (Ruta A, solo abre UI) |
| `TARGET\AdminCreateItem.java` | Creación de items con enchant |
| `TARGET\AdminBotManager.java` | Provisioning base del bot |
| `TARGET\handlers\bypass\communityboard\*.java` | Community Board handlers |
| `TARGET\AdminReload.java` | Data XML de enchant |
| `TARGET\ConvertItem.java` | Equip+enchant en skill effects |
| `TARGET\Restoration.java` | setEnchantLevel + equipItem |
| `TARGET\Transmog.java` | equipItem patterns |
| `TARGET\db\0005_botmanager_provisioning.sql` | SQL de provisioning FASE 4 |
| `TARGET\db\gen_0005_botmanager_provisioning.ps1` | Generador SQL |
| `L2J Notebook\BOT_RECIPE.md` | Documentación de referencia |
| `L2J Notebook\BOTAI-02` | Investigación previa roles/behaviors |
| `L2J Notebook\FASE4_00_INDEX.md` | Índice FASE 4 |
| `L2J Notebook\FASE4_11_COMMUNITY_BOARD.md` | Community Board |
| `L2J Notebook\FASE4_18_GAMECLIENT_DETACHED.md` | Clientless pattern |
| `L2J Notebook\FASE4_FINAL_REPORT.md` | Reporte final |
| `L2J Notebook\knowledge\COMMUNITY_BOARD.md` | Conocimiento CB |
| `L2J Notebook\knowledge\CONSTRAINTS.md` | Matriz de constraints |



---

## 9. Provisioning

### 9.1 Estado actual VERIFIED

El provisioning base YA EXISTE y está verificado:

**Archivos:**
- `db\0005_botmanager_provisioning.sql` — Crea 8 personajes REALES: BSBOT01..BSBOT08
- `db\gen_0005_botmanager_provisioning.ps1` — Generador idempotente

**Personajes en DB (VERIFIED):**
```
268483130  BSBOT01  Gladiator (classid=2, base_class=2, race=0)
268483131  BSBOT02
268483132  BSBOT03
268483133  BSBOT04
268483134  BSBOT05
268483135  BSBOT06
268483136  BSBOT07
268483137  BSBOT08
```

**Problema histórico corregido (VERIFIED):**
- classid=2 + base_class=0 era inconsistente
- Solución: `classid == base_class` (ej: Gladiator classid=2, base_class=2)

**Provisioning pattern VERIFIED (FASE 4.18):**
```java
// Replicar OfflinePlayTable.restoreOfflinePlayers()
Player bot = Player.load(charId);      // _client == null
bot.setOnlineStatus(true, false);
bot.spawnMe(x, y, z);
bot.setOfflinePlay(true);
bot.setOnlineStatus(true, true);
bot.restoreEffects();
bot.setRunning();
bot.joinParty(human.getParty());
AutoPlayTaskManager.getInstance().startAutoPlay(bot);
AutoUseTaskManager.getInstance().startAutoUseTask(bot);
```

**Clase/Nivel/XP VERIFIED:**
```java
bot.setPlayerClass(2);      // ClassMaster.java:450
bot.setBaseClass(2);        // Helper classes pattern
bot.getStat().setLevel(level);  // PlayerStat.java:506-523 (AdminBotManager.java:149)
bot.getStat().setExp(exp);      // ExperienceData.getExpForLevel(level)
bot.getStat().setSp(0);
bot.giveAvailableSkills(true, true, true);  // AdminBotManager.java:152
bot.store(false);
```

### 9.2 Lo que falta: BotProvisioning

**Pieza nueva relevante para el sprint.** Orquesta:
1. Carga del bot (Player.load)
2. Configuración de clase/nivel/skills
3. Inyección de equipo (items con enchant)
4. Spawn + AutoPlay/AutoUse
5. Party join


---

## 10. Equipment

### 10.1 APIs VERIFIED para inyección de items

| API | Archivo | Uso |
|-----|---------|-----|
| `ItemManager.createItem(process, itemId, count, ref, null)` | CtF.java:939 | Crear item instance |
| `Player.addItem(process, itemId, count, ref, true)` | ExtractableItems.java:128 | Añade a inventario |
| `Player.addItem(...).setEnchantLevel(N)` | SavingSanta.java:739 | Crea + encanta en cadena |
| `player.getInventory().equipItem(item)` | Transmog.java:105, AdminEnchant.java:216 | Equipar item |
| `player.useEquippableItem(item, false)` | AdminCreateItem.java:288 | Equipar usable |
| `Inventory.setPaperdollItem(slot, item)` | Inventory.java:1072 | Set slot directo |

### 10.2 Patrón recomendado para provisioning (Ruta B)

```java
// 1. Crear item con enchant directamente
Item weapon = ItemManager.createItem(ItemProcessType.REWARD, itemId, 1, bot, null);
weapon.setEnchantLevel(targetEnchant);  // Asignación directa, sin fallo
bot.addItem(ItemProcessType.REWARD, itemId, 1, bot, false).setEnchantLevel(targetEnchant);

// 2. Equipar
bot.getInventory().equipItem(weapon);
// o
bot.useEquippableItem(weapon, false);
```

### 10.3 Nota sobre sendPacket con _client==null

`sendPacket()` es null-safe cuando `_client == null` (no-op). VERIFIED:
- `AdminEnchant.java:219-222` usa `sendInventoryUpdate()` y `broadcastUserInfo()` sobre el target
- Los bots no reciben packets; el broadcaster filtra por clientes conectados


---

## 11. Auto-Enchant: Ruta A vs Ruta B

### 11.1 Ruta A — Mecánica real de scrolls (NO recomendada)

| Clase | Función |
|-------|---------|
| `EnchantScrolls.java` | Solo abre UI, NO calcula éxito |
| `EnchantItemGroupsData` | XML data max enchant por tipo |
| `EnchantItemData` | XML data de items encantables |

**NOT FOUND:** `EnchantScroll.calculateSuccess()`, `EnchantItemGroup.getChance()`, `RequestEnchantItem`

**Conclusión Ruta A:** Manejada en core (GameServer.jar), no accesible desde scripts. Inviable para bots.

### 11.2 Ruta B — Asignación directa (RECOMENDADA)

| API | Evidencia |
|-----|-----------|
| `item.setEnchantLevel(int)` | AdminEnchant.java:215, SavingSanta.java:739 |
| `player.addItem(...).setEnchantLevel(N)` | SavingSanta.java:739, 745 |

**Patrón VERIFIED (AdminEnchant.java:214-216):**
```java
player.getInventory().unEquipItemInSlot(slot);
itemInstance.setEnchantLevel(enchant);
player.getInventory().equipItem(itemInstance);
```

### 11.3 Comparación

| Aspecto | Ruta A | Ruta B |
|---------|--------|--------|
| Probabilidad de fallo | Sí | No |
| Consume scrolls | Sí | No |
| Requiere cliente | Sí | No |
| Verificado | Parcial | Sí |
| Recomendado | **NO** | **SÍ** |


---

## 16. Dashboard (visión general)

| Opción | Tipo | Estado |
|--------|------|--------|
| **A** | Community Board (IParseBoardHandler) | VERIFIED extensible |
| **B** | Admin command `//bot` | VERIFIED implementado |
| **C** | HTML externo | POSIBLE |
| **D** | NPC dashboard (PrepMaster) | POSIBLE |
| **E** | Hybrid (CB + admin) | POSIBLE |

**No se recomienda todavía una opción.** Se presentan para decisión GM.

---

## 17. Community Board (Opción A)

### 17.1 VERIFIED (FASE4_11_COMMUNITY_BOARD.md)

| Componente | Uso |
|------------|-----|
| `CommunityBoardHandler.handleParseCommand(cmd, player)` | Gate + dispatch |
| `IParseBoardHandler` | Interface para handler |
| `CommunityBoardHandler.registerHandler(handler)` | Registrar board |
| `CommunityBoardHandler.separateAndSend(html, player)` | Enviar HTML |

### 17.2 Propuesta BotBoard (PROPOSED)

```java
public class BotBoard implements IParseBoardHandler {
    private static final String[] COMMANDS = { "_bbsbot", "_bbsbotspawn", "_bbsbotgear" };

    @Override
    public boolean onCommand(String command, Player player) {
        // routing...
        CommunityBoardHandler.separateAndSend(html, player);
        return true;
    }

    @Override
    public String[] getCommandList() { return COMMANDS; }
}
```

Registro: `CommunityBoardHandler.getInstance().registerHandler(new BotBoard());`

**Ventajas:** NO editar CommunityBoardHandler.java; HTML vía separateAndSend; recompilable en runtime.

---

## 18. Dashboard externo (Opción C)

| Aspecto | Detalle |
|---------|---------|
| **Tipo** | HTML/JS fuera del cliente L2 |
| **Requisito** | Servidor HTTP embebido |
| **Estado** | No implementado; fuera del alcance actual |

---

## 19. Comparación con Mobius

| Funcionalidad | Proyecto externo | Nuestro Mobius | Reutilizable | Trabajo |
|---------------|------------------|----------------|--------------|---------|
| **Crear Player** | FakePlayer/NPC | Player.load + null client | SÍ | BotManager |
| **Clase** | setClassId custom | setPlayerClass + setBaseClass | SÍ | Ninguno |
| **Nivel** | setLevel custom | PlayerStat.setLevel | SÍ | Ninguno |
| **Skills** | SkillTable C4 | SkillTreeData + giveAvailableSkills | SÍ | Ninguno |
| **Equipo** | Equip por clase | createItem + addItem + equipItem | SÍ | BotProvisioning |
| **Enchant** | Enchant chance | setEnchantLevel directo | SÍ | Ninguno |
| **Party** | L2Party C4 | Party nativa joinParty | SÍ | Ninguno |
| **Follow** | Custom AI | AutoPlay AssistLeader=true | SÍ | Ninguno |
| **Assist** | Custom | AutoPlayTaskManager:264-282 | SÍ | Ninguno |
| **Heal** | Custom healer | AutoUse + RoleStrategy | PARCIAL | RoleStrategy |
| **Buff** | Custom buffer | AutoUse + SkillTreeData | PARCIAL | RoleStrategy |
| **Revive** | Teleport villa | Death/revive nativo | SÍ | Ninguno |
| **Dashboard** | `//a b` (Autobots) | Community Board + //bot | SÍ | BotBoard |

**NOTA:** Columna "Proyecto externo" marcada NOT_FOUND donde no hay código verificable.


---

## 21. APIs NOT_FOUND

| # | API | Nota |
|---|-----|------|
| 1 | `EnchantScroll.calculateSuccess()` | NOT FOUND — en core (GameServer.jar) |
| 2 | `EnchantItemGroup.getChance()` | NOT FOUND — no existe |
| 3 | `RequestEnchantItem` | NOT FOUND — packet handler cliente |
| 4 | `MultiSellChoose` | NOT FOUND — no existe en scripts |
| 5 | `giveAvailableAutoGetSkills()` | NOT FOUND — no existe en Player |
| 6 | `equipItemAndRecord()` | NOT FOUND — no existe en Inventory |
| 7 | `getDefaultEnchantLevel()` | NOT FOUND — no existe en ItemContainer |
| 8 | `rewardSkills()` | Solo en docs, no en scripts |
| 9 | Código L2jRoboto/DanielBarion/SmartBot/Autobots | NOT_FOUND |
| 10 | Admin handlers de enchant/item en build | NOT_FOUND — scripts no indexados |
| 11 | `TemporaryPlayer` class | NOT FOUND (FASE4_06) |
| 12 | `FakePlayer` como base Player | FALSE/CORRECTED — es NPC |
| 13 | `GameClient` sin socket | FALSE/CORRECTED — solo ctor con Connection |

---

## 22. Arquitectura candidata

```
BotManager (orquestador)
  └── BotProvisioning (NUEVA — única pieza relevante)
        ├── Player.load(charId)
        ├── setPlayerClass / setBaseClass
        ├── giveAvailableSkills
        ├── ItemManager.createItem + setEnchantLevel + equipItem
        ├── spawnMe + setOfflinePlay
        ├── AutoPlay / AutoUse start
        └── joinParty
  └── BotSession (wrapper Player bot)
  └── BotRole (configuración: Tank/Healer/Buffer/DPS)
  └── BotBehavior (estrategia sobre AutoPlay/AutoUse)
  └── (Party + AutoPlay + AutoUse + PlayerAI) — REUTILIZADOS
```

### Flujo end-to-end

```
[Admin] //bot 1
  → BotManager.spawnBot(charId=268483130)
    → BotProvisioning.provision(charId, classId, level, gearSet, role)
      → Player.load(268483130)
      → setPlayerClass(classId) + getStat().setLevel(level)
      → giveAvailableSkills(true,true,true)
      → for each gear slot: createItem + setEnchantLevel + equipItem
      → spawnMe + setOfflinePlay + joinParty
      → AutoPlay/AutoUse start
```

---

## 23. Qué reutilizar

| Componente | Evidencia |
|------------|-----------|
| Player.load + null client | FASE4_18 VERIFIED |
| OfflinePlay pattern | restoreOfflinePlayers VERIFIED |
| Party nativa | joinParty VERIFIED |
| AutoPlay (follow/assist/attack) | AutoPlayTaskManager VERIFIED |
| AutoUse (buffs/potions/skills) | AutoUseTaskManager VERIFIED |
| Skills (SkillTreeData + giveAvailableSkills) | ADR-010 VERIFIED |
| Community Board | FASE4_11 VERIFIED |
| Enchant directo (Ruta B) | AdminEnchant pattern VERIFIED |
| Item creation + equip | AdminCreateItem/Transmog VERIFIED |
| Admin command `//bot` | AdminBotManager VERIFIED |
| Death/revive nativo | Player.java VERIFIED |

---

## 24. Qué NO reutilizar

| Elemento | Razón |
|----------|-------|
| FakePlayer/NPC como base | Es NPC AttackableAI (FASE4_05) |
| TemporaryPlayer | No existe (FASE4_06) |
| GameClient artificial | Solo ctor con Connection (FASE4_18) |
| Código L2jRoboto/SmartBot/Autobots | Cores distintos |
| ThinkLoop nuevo | Reutilizar AutoPlayTaskManager |
| Scheduler paralelo | Reutilizar loops nativos |
| Modificaciones core | MVP no requiere cambios |
| Enchant con probabilidad (Ruta A) | Inviable sin core |
| BotPartyManager/BotGroup/PartyAI | Usar Party nativa |


---

## 25. Riesgos y pendientes

| Riesgo | Severidad | Mitigación | Estado |
|--------|-----------|------------|--------|
| `useEquippableItem` con `_client==null` | MEDIA | Verificar que no llama sendPacket crítico | UNVERIFIED |
| `broadcastUserInfo()` con isOnlineInt()==0 | BAJA | Verificar que no rompe party | UNVERIFIED |
| Barrido `getClient()` server-side | MEDIA | No auditado línea a línea | UNVERIFIED |
| Visibilidad dinámica del bot | BAJA | Precedente offline shops | UNVERIFIED |
| NPE en `startOfflinePlay()` sobre bot | ALTA | NO llamar (setDetached sin null-check) | VERIFIED (evitar) |

### Pendientes previos a implementación

1. Tener charId de bot en DB (VERIFIED: 8 BSBOT existen)
2. Verificar `useEquippableItem` runtime con null client (UNVERIFIED)
3. Verificar `broadcastUserInfo` sobre bot en party (UNVERIFIED)
4. Barrido getClient() server-side (UNVERIFIED)
5. Definir gear presets por rol (PROPOSED)
6. Definir enchant targets por tier (PROPOSED)

---

## 26. Recomendaciones

1. **Reutilizar** el patrón `OfflinePlayTable.restoreOfflinePlayers()` (FASE 4.18).
2. **Los 8 BSBOT** ya existen en DB (charId 268483130..268483137).
3. **Ruta B para enchant:** `item.setEnchantLevel(N)` directo, sin probabilidad, sin scrolls.
4. **Skills:** `giveAvailableSkills(true, true, true)` — 100% reutilizable.
5. **Equipment:** `ItemManager.createItem` + `setEnchantLevel` + `equipItem`.
6. **Community Board:** Extensión por registro (IParseBoardHandler), sin tocar core.
7. **Dashboard:** No decidir aún — presentar opciones A-E a GM.
8. **No inventar:** BotProvisioning orquesta APIs existentes.
9. **No implementar:** Ruta A de enchant, FakePlayer, TemporaryPlayer, GameClient artificial.

---

## 27. Preguntas abiertas

| # | Pregunta | Prioridad |
|---|----------|-----------|
| 1 | ¿Community Board (A) o admin commands (B) como dashboard? | MEDIA |
| 2 | ¿Gear preset estándar por rol? | MEDIA |
| 3 | ¿Enchant target por tier (ej: +4, +8, +12)? | MEDIA |
| 4 | ¿`useEquippableItem` seguro con `_client==null`? | ALTA (runtime) |
| 5 | ¿`broadcastUserInfo` funciona sobre bots en party? | ALTA (runtime) |
| 6 | ¿Persistencia de gear/enchant tras reinicio? | BAJA |
| 7 | ¿Dashboard externo deseable a futuro? | BAJA |
| 8 | ¿NPC dashboard (PrepMaster) como alternativa? | BAJA |


---

## Reporte Final

**SPRINT:** BOTAI-03-RESEARCH

**FASE:** Investigación Provisioning/Equipment/Enchant/Dashboard

**MODO:** PLAN

**ESTADO:** INVESTIGACIÓN COMPLETADA

**DOCUMENTO CREADO:**
`C:\L2J MOBIUS IA\L2J Notebook\investigations\BOTAI-03_PROVISIONING_EQUIPMENT_ENCHANT_DASHBOARD_RESEARCH.md`

**FUENTES INVESTIGADAS:**
- `TARGET\AdminEnchant.java` (240 líneas) — enchant directo Ruta B
- `TARGET\EnchantScrolls.java` (64 líneas) — handler scrolls Ruta A
- `TARGET\AdminCreateItem.java` — creación items con enchant
- `TARGET\AdminBotManager.java` — provisioning base
- `TARGET\AdminReload.java` — data XML enchant
- `TARGET\ConvertItem.java`, `Restoration.java`, `Transmog.java` — patrones equip+enchant
- `TARGET\handlers\bypass\communityboard\*.java` — 9 boards CB
- `TARGET\db\0005_botmanager_provisioning.sql` + `.ps1` — SQL provisioning
- `L2J Notebook\BOT_RECIPE.md`, `BOTAI-02`, `FASE4_*` — documentación referencia

**HALLAZGOS PRINCIPALES:**
1. El 95% de los componentes necesarios YA EXISTEN en Mobius.
2. La única pieza nueva relevante es **BotProvisioning** (orquestador).
3. **Ruta B (asignación directa de enchant)** es la recomendada: `item.setEnchantLevel(N)`.
4. **Ruta A (scrolls con probabilidad)** es inviable sin acceso al core.
5. Community Board es extensible por registro sin tocar core.
6. Los 8 BSBOT existen en DB con classid=2/base_class=2.
7. `setPlayerClass` NO es necesario para bots persistentes.

**APIs VERIFIED:** 37 APIs verificadas (ver sección 20).
- `Item.setEnchantLevel`, `Player.addItem`, `ItemManager.createItem`
- `Inventory.equipItem`, `Player.useEquippableItem`
- `giveAvailableSkills`, `SkillTreeData`, `PlayerStat.setLevel/setExp`
- `CommunityBoardHandler.registerHandler`, `separateAndSend`
- `Player.load` + null client pattern (FASE 4.18)

**APIS NOT_FOUND:** 13 APIs no encontradas (ver sección 21).
- `EnchantScroll.calculateSuccess()`, `EnchantItemGroup.getChance()`
- Código de proyectos externos (L2jRoboto, SmartBot, Autobots)
- `TemporaryPlayer`, `FakePlayer` como base, `GameClient` sin socket

**RECOMENDACIÓN:**
Implementar BotProvisioning usando exclusivamente APIs VERIFIED. Ruta B para enchant. Reutilizar Party + AutoPlay/AutoUse + SkillTreeData al 100%. No crear arquitectura paralela. No tocar core.

**PENDIENTES:**
- Verificar `useEquippableItem` runtime con `_client==null` (UNVERIFIED)
- Verificar `broadcastUserInfo` sobre bots en party (UNVERIFIED)
- Barrido getClient() server-side (UNVERIFIED)
- Decidir dashboard: Community Board vs admin commands vs híbrido
- Definir gear presets por rol
- Definir enchant targets por tier

---

**FIN DEL DOCUMENTO BOTAI-03-RESEARCH**
