# BOTAI-05.1 — REAL PLAYER ARCHITECTURE VERIFICATION

> **SPRINT:** BOTAI-05.1
> **FASE:** Verificación de arquitectura Real Player (PLAYER clientless)
> **MODO:** PLAN — SOLO INVESTIGACIÓN (no se modificó ningún archivo de código)
> **EJECUTOR:** Devin (informe original) + Cline (verificación local)
> **ESTADO:** INVESTIGACIÓN COMPLETADA — GO CONDICIONAL
> **Fecha:** 2026-09-14
> **Source autoritativo:** L2J Mobius CT 2.6 HighFive @ `e2518ab108` (UPSTREAM local) / repo indexado `metaleiser/L2J`

---

## CONTEXTO / RELACIÓN CON SPRINTS PREVIOS

- `BOTAI-04-B1.1_EQUIPMENT_PENDING_CLOSURE.md` cerró el equipment/provisioning con la instrucción
  **"NO iniciar BOTAI-05"**. Este informe es la investigación que abre formalmente BOTAI-05: verificar
  que un `Player` persistente real sin `GameClient` puede ejecutar el ciclo de vida completo (party,
  AutoPlay, AutoUse, combate, death/revive, loot, persistencia) usando solo mecánicas nativas de Mobius.
- El informe NO ejecuta código ni implementa nada: es una verificación de arquitectura por lectura de source.
- La auditoría de los custom files TARGET (`AdminBotManager`, `BotSession`, `BotGradeLoadout*`,
  `BotProvisioning`, `BotEquipmentProgression`) se marcó en el informe original como `NO VERIFICADO`
  porque no estaban indexados en el repo remoto. **Esta copia del Notebook agrega un ANEXO de verificación
  local (Cline, 2026-09-14)** que confirma los hallazgos contra UPSTREAM y audita el hook nativo de
  `BotEquipmentProgression` — cerrando el riesgo #3 del informe.

---

## INFORME ORIGINAL (Devin) — transcripción íntegra

> Source autoritativo: `L2J_Mobius_CT_2.6_HighFive` (repo indexado `metaleiser/L2J`).
> Modo: SOLO INVESTIGACIÓN. No se modificó ningún archivo.
> Nota de alcance: los archivos custom (`AdminBotManager`, `BotProvisioning`, `BotGradeLoadout*`, `BotEquipmentProgression`) viven en tu workspace local TARGET y **no están indexados** en este repo; su auditoría (INV-11) se marca en base a evidencia de comportamiento nativo, no lectura directa. Se marca `NO VERIFICADO` donde corresponde.

---

## 1. Veredicto

**GO CONDICIONAL**

Un `Player` persistente normal con `getClient() == null` puede existir, entrar a Party, ejecutar AutoPlay/AutoUse, combatir, moverse, seguir/asistir, morir, revivir, lootear y persistir usando **exclusivamente sistemas nativos de Mobius**, sin fabricar `GameClient` y sin implementar una IA de combate paralela.

Es **CONDICIONAL** (no GO puro) solo por dos motivos acotados y ya conocidos, ninguno estructural:
1. **Visibilidad dinámica**: `broadcastCharInfo()` hace early-return si `isOnlineInt() == 0` (cliente null). Movimiento/combate se propagan por `broadcastPacket` (no chequea el flag), pero cambios de aspecto (equipo/título) pueden no propagarse en caliente. `[VERIFICADO]`
2. **Barrido `getClient()` server-side fuera de `Player.java`**: dentro de `Player.java` el riesgo es **nulo** (0 desreferencias), pero no audité exhaustivamente cada call site externo. Riesgo bajo por el precedente de offline players. `[NO VERIFICADO exhaustivamente]`

---

## 2. Resumen ejecutivo

Mobius ya contiene **casi toda** la arquitectura que necesitas. El precedente de producción es `OfflinePlayTable.restoreOfflinePlayers()`, que arranca Players clientless con AutoPlay/AutoUse. La cadena `startAutoPlay → pool task → target → movement → attack → skill → loot` está implementada **enteramente server-side** sobre el objeto `Player`, sin enviar packets de input ni leer `getClient()`.

Lo único que tu código realmente debe aportar es **configuración y ciclo de vida**: quién es el bot, su clase/nivel/skills/equipo/dyes, y el perfil de AutoPlay/AutoUse. Todo el gameplay lo ejecuta el core.
---

## 3. Player sin GameClient `[VERIFICADO]`

**Archivo:** `gameserver/entity/actor/Player.java`

- `sendPacket(ServerPacket)` (líneas 4429-4435): `if (_client != null) { _client.sendPacket(packet); }`. **Null-guard nativo.** Todo envío de packets del Player pasa por aquí.
- `getClient()` (4117-4120): simple getter, puede devolver `null`.
- **Hallazgo decisivo:** grep de `getClient().` en `Player.java` → **0 coincidencias**. `Player` **nunca** desreferencia su propio cliente; siempre usa `sendPacket()` (guardado). Un bot con `_client == null` no puede provocar NPE por rutas internas de `Player`.
- `isInOfflineMode()` (7987-7990): `return (_client == null) || _client.isDetached();`
- `isOnlineInt()` (7910-7918): devuelve `0` si `_client == null` → relevante para visibilidad (§10).
- `isOnline()` (7905-7908): devuelve el booleano `_isOnline` puro, **independiente del cliente**. Esto es lo que consultan AutoPlay/AutoUse y el autosave.

Respuestas puntuales:
1. ¿Player con `getClient()==null`? **SÍ.**
2. ¿`Player.load(...)` sin cliente? **SÍ** — `load` restaura inventario/skills/hennas/shortcuts/etc. y termina en `setOnlineStatus(true,false)`; **no asigna `_client`** (líneas ~7341-7413). El cliente solo se asocia en el flujo de login real vía `setClient`.
3. ¿Qué hace `load` con client? **Nada** — lo deja como esté (null si se cargó fuera de login).
4. ¿Métodos que asumen `getClient()!=null`? Dentro de `Player.java`: **ninguno** desreferencia directo. Fuera: algunos packet-handlers y `EnterWorld` usan `getClient()` pero son flujos client-driven que **no** se disparan sobre un bot server-side.
5-11. `spawnMe()`, `setOnlineStatus()`, `setOfflinePlay()`, `setRunning()`, `decayMe()`, `PlayerAI`: **ninguno requiere `GameClient`** — son operaciones sobre estado del mundo/objeto. `[VERIFICADO]` por precedente de `OfflinePlayTable`.

---

## 4. AutoPlay `[VERIFICADO]`

**Archivo:** `gameserver/taskmanagers/AutoPlayTaskManager.java`

- **Ejecución:** 100% server-side. Pools de `Set<Player>` (`POOL_SIZE=200`), tarea `AutoPlay implements Runnable` reprogramada por `ThreadPool.schedulePriorityTaskAtFixedRate(..., TASK_DELAY=700, 700)` (líneas 388-392).
- **Guard de entrada del loop (78-84):** `if (!player.isOnline() || (player.isInOfflineMode() && !player.isOfflinePlay()) || !ENABLE_AUTO_PLAY) { stopAutoPlay(player); continue; }`. → un bot con `isOnline()==true` + `isOfflinePlay()==true` **pasa el guard**. Esto es exactamente el estado que fija `OfflinePlayTable`.
- **Targeting:** `World.getNearestVisibleObjectInRange(...)` + `GeoEngine.canSeeTarget/canMoveToTarget` (285-307). Sin cliente.
- **Movimiento:** `player.getAI().setIntentionMoveTo(...)` (115, 209, 252, 321). Sin cliente.
- **Ataque:** `player.getAI().setIntentionAttack(creature)` (173, 179, 327). Sin cliente.
- **Loot:** `player.doPickupItem(droppedItem)` (259) tras `World.getFirstVisibleObjectInRange(... Item.class ...)`. Sin cliente.
- **Assist/Follow nativos (266-282):** si hay Party y `ENABLE_AUTO_ASSIST`, toma `leader.getTarget()` y si no hay target válido hace `setIntentionFollow(leader)`. **El follow y el assist ya existen en AutoPlay — no hay que programarlos.**
- **Ninguna** llamada a `sendPacket`/`getClient` en todo el loop.

Flujo real confirmado:
```
startAutoPlay(player)
  → pool + schedulePriorityTaskAtFixedRate (700ms)
    → guard isOnline/isOfflinePlay
      → assist leader.getTarget() | follow leader | getNearestVisibleObjectInRange
        → setIntentionMoveTo / setIntentionAttack
          → doPickupItem
```
Dependencia de `GameClient`: **ninguna.** `[VERIFICADO]`
---

## 5. AutoUse `[VERIFICADO]`

**Archivo:** `gameserver/taskmanagers/AutoUseTaskManager.java`

- Mismo patrón de pools + `ThreadPool.schedulePriorityTaskAtFixedRate(..., 300, 300)` (452).
- **Mismo guard clientless (79-83):** `isOnline() || (isInOfflineMode() && !isOfflinePlay())`.
- **Items/potions:** `ItemHandler.getHandler(...).onItemUse(player, item, false)` (141-146, 165-171). Server-side.
- **Buffs/skills:** `caster.doCast(skill)` / `caster.useMagic(skill, true, false)` (244-262, 362). Server-side.
- Soulshots/spiritshots se manejan como items/skills por el mismo mecanismo.
- **Ninguna** dependencia de `getClient()`.

AutoUse es plenamente utilizable por un Player sin cliente. `[VERIFICADO]`

---

## 6. PlayerAI / Combat `[VERIFICADO]`

- `PlayerAI` extiende la jerarquía `PlayableAI → CreatureAI → AbstractAI`. Los "think" (`thinkAttack`, `thinkCast`, `thinkPickUp`, `thinkInteract`) operan sobre `_actor` (el Player) y llaman `_actor.doAttack(target)` / `_actor.doCast(skill)` — sin cliente.
- `setIntentionAttack/MoveTo/Follow` (usados por AutoPlay) están implementados en `AbstractAI`.

**Respuesta a la pregunta clave:** **NO necesitas una nueva `BotAI`.** El comportamiento (target, move, attack, cast, follow, assist, pickup) ya existe entre `PlayerAI` + AutoPlay + AutoUse. Solo se configura y se arranca. `[VERIFICADO]`

---

## 7. Party `[VERIFICADO]`

**Archivo:** `gameserver/entity/groups/Party.java`

- `addPartyMember(Player)` (280-384): usa `player.sendPacket(...)` (null-guarded) y `broadcastPacket(...)`. Ajusta `_partyLvl`, `updateEffectIcons`, `broadcastUserInfo`. **No desreferencia cliente.** Un miembro clientless no rompe el método.
- `broadcastToPartyMembers` (265-274) y `broadcastToPartyMembersNewLeader` (247-258): iteran y llaman `member.sendPacket(...)` → guardado. Enviar a un bot clientless simplemente no hace nada, sin error.
- `removePartyMember` (401-524): server-side, `sendPacket` guardado.
- Loot de party (`getActualLooter`/`getCheckedNextLooter`, 172-242): reparte por `member.getInventory()` — funciona para bots.
- **Precedente:** `OfflinePlayTable` (líneas 227-232) crea `new Party(...)` y hace `member.joinParty(party)` con players clientless en restore. Confirma coexistencia nativa.
- Límite: `RequestJoinParty` valida `party.getMemberCount() >= 9` → 1 GM + 8 bots = 9 encaja exacto.

`GM Player + bot sin cliente` coexisten nativamente en Party. `[VERIFICADO]`

---

## 8. Skills / AutoLearn `[VERIFICADO parcial]`

**Archivo:** `Player.java` + `SkillTreeData`

- `giveAvailableSkills(boolean includeByFs, boolean includeAutoGet, boolean includeRequiredItems)` (2799-2804) → `SkillTreeData.getAllAvailableSkills(this, getPlayerClass(), ...)`. **Este es el método nativo exacto** para dar a un Player todos los skills que le corresponden por clase/nivel. No hay que implementar un sistema paralelo.
- Flujo viable: `load → setClassId/clase → setLevel/exp → giveAvailableSkills(...) → store(true)`.
- `[NO VERIFICADO]` el valor runtime de `AUTO_LEARN_SKILLS` en el `.ini` del dist (no indexado). Es irrelevante para el mecanismo: llamar `giveAvailableSkills` explícitamente resuelve el aprendizaje sin depender del config.

---

## 9. Equipment / Expertise `[VERIFICADO]`

- Equipar: `Player.useEquippableItem(...)` / `Inventory.equipItemAndRecord(item)` (Player 2303; Inventory 1244-1257). Crear con enchant: `ItemContainer.createItem` + `item.setEnchantLevel(...)`.
- **Expertise/grade penalty es nativo:** `ConditionPlayerGrade.testImpl` compara `getExpertiseLevel()`; `CrystalType`/`ItemGrade` definen los grados. Si equipas por encima del expertise del bot, el core aplica la penalización automáticamente — **debes respetarlo eligiendo el loadout correcto por nivel/grado.**
- **Confirmado estructural (afecta tu diseño):** `ArmorSet` **no expone getters de piezas** (solo `getChestId`, 237-240) → el loadout debe declararse pieza por pieza en tu config, no puede derivarse del set en runtime. Los **5 slots de joyería son independientes** (`Inventory` 84-94: `PAPERDOLL_REAR/LEAR/RFINGER/LFINGER/NECK`) → esto explica el earring/ring faltante observado.

Conclusión: tu sistema puede limitarse a **seleccionar un loadout** (IDs + enchant); `Player + Inventory + Equipment + expertise` es el mecanismo real. `[VERIFICADO]`

---

## 10. Dyes / Henna `[HIPÓTESIS — mecanismo nativo probable, no leído en esta sesión]`

Mobius maneja dyes vía sistema de **Henna** (`restoreHenna()` se invoca en `Player.load`, confirmado en el bloque de carga). La API de agregar henna sobre el Player existe nativamente (add/remove henna con modificadores STR/DEX/CON/INT/WIT/MEN). No leí la clase `Henna`/`HennaList` completa en esta sesión → marcar `[HIPÓTESIS]` hasta confirmar la firma exacta del método de aplicación. No es un bloqueo.
---

## 11. Death / Revive / Respawn `[VERIFICADO]`

**Archivo:** `Player.java`

- `doDie(Creature killer)` (5241-5488): totalmente server-side. Usa `broadcastStatusUpdate()`, `sendPacket` (guardado), penalización de exp `calculateDeathExpPenalty` (5856-5923). Un bot clientless **puede morir sin error.**
- `_canRevive`, `_reviveRequested`, `_revivePower` (840-843) → soporte de revive presente. `doRevive` server-side.
- `DecayTaskManager.getInstance().add(this)` solo si `DISCONNECT_AFTER_DEATH` (5482-5485).
- **AutoPlay tras muerte:** la tarea de pool **NO se elimina al morir** (solo se remueve si falla el guard `isOnline`/offline). Mientras el bot esté vivo=false pero online=true, el loop simplemente no encuentra acción válida; al revivir, **AutoPlay continúa automáticamente sin reiniciar.** `[VERIFICADO]`

Respuestas: morir SÍ; revivir SÍ; auto-respawn depende de config/mecanismo de revive de party; AutoPlay **no** necesita reinicio manual salvo que el guard lo haya expulsado del pool (p. ej. si se marca offline).

---

## 12. Client dependencies — clasificación `[VERIFICADO para los caminos listados]`

Clave: A=funciona sin client · B=necesita guard/null-check · C=requiere client · D=no relevante.

| Camino | Método/archivo | Clase |
|---|---|---|
| Envío de packets del Player | `Player.sendPacket` (4429) | **A** (guard nativo `_client != null`) |
| Desref. directa de cliente en Player | grep `getClient().` en `Player.java` = 0 | **A** |
| Carga | `Player.load` | **A** |
| Spawn | `spawnMe` | **A** |
| AutoPlay loop | `AutoPlayTaskManager.AutoPlay.run` | **A** |
| AutoUse loop | `AutoUseTaskManager.AutoUse.run` | **A** |
| Party add/remove/broadcast | `Party.*` (usa sendPacket) | **A** |
| Combate/skills/loot | `doAttack/doCast/doPickupItem` | **A** |
| Muerte | `Player.doDie` | **A** |
| Visibilidad de aspecto en caliente | `broadcastCharInfo` (early-return si `isOnlineInt()==0`) | **B** (localizado; movimiento/combate van por `broadcastPacket`, sin flag) |
| Anti-dualbox HWID | `EnterWorld` `getClient().getHardwareInfo()` | **D** (flujo de login, no se dispara sobre bot server-side) |

No se encontró ningún camino **C** (requiere cliente obligatorio) dentro de los flujos de gameplay del bot. `[VERIFICADO]`

---

## 13. Auditoría de AdminBotManager actual `[NO VERIFICADO por lectura — archivos no indexados]`

No puedo leer `AdminBotManager.java`, `BotProvisioning.java`, `BotGradeLoadout*`, `BotEquipmentProgression.java` (viven en tu TARGET local, fuera del repo indexado). Evaluación en base a la lista de operaciones que declaras + evidencia nativa:

### A — Lógica realmente necesaria
- **Lifecycle/orquestación**: `Player.load` → `setOnlineStatus(true,false)` → `spawnMe` → `setOfflinePlay(true)` → `setOnlineStatus(true,true)` → `restoreEffects/setRunning` → `joinParty` → `startAutoPlay`/`startAutoUseTask`. (Réplica de `OfflinePlayTable`.)
- **BotProfile/configuración**: clase, nivel, loadout (IDs+enchant), dyes, perfil AutoPlay/AutoUse.
- **Registro de bots** (mapa objectId↔bot) y comandos GM.

### B — Lógica que duplica mecanismos nativos
- Cualquier ThinkLoop/scheduler propio de combate → **duplica** AutoPlay/AutoUse. `[SOSPECHA — confirmar en TARGET]`
- Cualquier lógica de follow/assist propia → **duplica** el bloque assist/follow de AutoPlay (líneas 266-282).
- Cualquier motor de skills propio → duplica `giveAvailableSkills` + AutoUse.

### C — Lógica que podría desaparecer si Real Player funciona
- `BotEquipmentProgression` como "listener" de level-up: **sospecha crítica que planteas es válida** — no está confirmado que se registre como listener. El hook nativo real es `EventType.ON_PLAYER_LEVEL_CHANGED` (disparado en `PlayerStat.addLevel`). Si tu clase no se registra ahí, la "progresión automática" no se ejecuta. `[NO VERIFICADO — requiere leer el archivo y su registro en TARGET]`

### D — Lógica que sí hay que conservar
- El `getClient()` null-guard que ya añadiste (defensivo, correcto).
- El BotProfile/loadout declarativo (obligatorio: `ArmorSet` no da piezas, joyería en 5 slots).
- El registro/versionado por `PlayerVariables` para idempotencia.

Transformación conceptual recomendada (coherente con tu intuición):
```
AdminBotManager  = lifecycle/orchestrator
BotProfile       = configuración (clase/nivel/loadout/dyes/perfil)
Player           = personaje real (ejecuta todo vía core)
```

---

## 14. Qué código custom realmente necesitamos

1. **Orquestador de ciclo de vida** (summon/dismiss/registro) — réplica del bloque `OfflinePlayTable`.
2. **BotProfile declarativo**: clase, nivel objetivo, loadout por grade (IDs+enchant, pieza por pieza), dyes, perfil AutoPlay (`AutoPlaySettings`: targetMode, pickup, potionPercent) y AutoUse (`autoBuffs`, `autoSkills`, `autoSupplyItems`, `autoActions`).
3. **Provisioning idempotente**: `addItem`+`setEnchantLevel`+`equipItemAndRecord`+`giveAvailableSkills`+`store(true)`, con marca en `PlayerVariables`.
4. **Entrada opcional en Community Board** como panel de control (handler existente `CommunityBoardHandler.handleParseCommand`, guardado contra `player==null`).
---

## 15. Qué código custom probablemente sobra

- Cualquier **BotAI/ThinkLoop/scheduler de combate paralelo** → lo hace AutoPlay/AutoUse.
- Cualquier **follow/assist manual** → nativo en AutoPlay.
- Cualquier **motor de skills** → `giveAvailableSkills` + AutoUse.
- Cualquier intento de **fabricar GameClient/detached** → innecesario; `_client == null` es suficiente (precedente `OfflinePlayTable`).
- `BotEquipmentProgression` como clase standalone que **no** esté registrada como listener → o se registra en `ON_PLAYER_LEVEL_CHANGED`, o se ejecuta on-demand en provisioning; tal como está descrita, es candidata a eliminación/refactor. `[confirmar en TARGET]`

---

## 16. Arquitectura propuesta

```
CommunityBoard (panel de control, handler nativo)
      ↓
BotProfile (config: clase/nivel/loadout/dyes/perfil AutoPlay-AutoUse)
      ↓
AdminBotManager (lifecycle: load/spawn/offlinePlay/party/start)
      ↓
Player persistente (_client == null, isOnline=true, isOfflinePlay=true)
      ↓
Native Mobius (PlayerAI + AutoPlay + AutoUse + Party + Inventory +
               expertise + doDie/doRevive + PlayerVariables persistence)
```

---

## 17. Test mínimo de validación (diseño, NO ejecutar)

Objetivo: validar la cadena completa sobre BSBOT01 (charId 268483130).

Métodos que toca: `Player.load`, `getClient()` (esperar null), `setOnlineStatus(true,false)`, `spawnMe`, `setOfflinePlay(true)`, `setOnlineStatus(true,true)`, `restoreEffects`, `setRunning`, `joinParty`, `startAutoPlay`, `startAutoUseTask`, luego observar `doAttack`/`doPickupItem`/`doDie`/revive.

Condiciones a verificar y logs útiles:
- `getClient()==null` tras load → log confirmación.
- Bot **visible** para el GM tras `spawnMe` (test primario, RUNTIME REQUIRED — riesgo B de §10).
- `isOnline()==true` y `isOfflinePlay()==true` → AutoPlay pasa el guard.
- Bot selecciona target, se mueve, ataca (log `setIntentionAttack`).
- Bot con Party+leader: hace follow/assist (log branch 266-282).
- Party recibe XP; bot lootea (`doPickupItem`).
- Bot muere (`doDie` sin excepción) y revive; AutoPlay **continúa sin reinicio**.
- `store(true)` persiste; reload conserva estado.

- **GO** si: visible + AutoPlay activo + combate + party XP + loot + muerte/revive sin NPE.
- **NO-GO** si: NPE por `getClient()` en algún flujo externo, o el bot es invisible y no hay forma de forzar `sendInfo`/knownlist.

---

## 18. Riesgos

1. **Visibilidad dinámica clientless** (`broadcastCharInfo` early-return si `isOnlineInt()==0`). `[VERIFICADO el mecanismo; RUNTIME REQUIRED el impacto]` — riesgo B, localizado.
2. **`getClient()` server-side fuera de `Player.java`** disparado sobre un bot. Riesgo bajo (precedente offline players), `[NO VERIFICADO exhaustivamente]`.
3. **`BotEquipmentProgression` no registrado como listener** → progresión por nivel no se ejecuta. `[NO VERIFICADO — auditar en TARGET, hook nativo = ON_PLAYER_LEVEL_CHANGED]`.
4. **Escalabilidad** 8-18 bots: broadcast + GeoEngine por bot. Inferencia, no benchmark.
5. **Timing de provisioning**: `setPlayerClass` agenda `applyItemSkills()/sendSkillList()` a 100ms → no encadenar `store(true)` inmediatamente tras cambio de clase.
6. **IDs de datapack** (loadouts) no verificables aquí (XML no indexado) → grep local antes de fijar loadout.

---

## 19. GO / NO-GO final

**GO CONDICIONAL.**
- **GO** en: existir sin cliente, Party, AutoPlay, AutoUse, combate, movimiento, skills, XP/SP, loot, muerte, revive, persistencia — todo por sistemas nativos, sin GameClient falso, sin IA paralela. `[VERIFICADO]`
- **Condiciones** (guards pequeños/localizados, no estructurales): (a) confirmar visibilidad runtime del bot clientless; (b) cerrar el barrido de `getClient()` externo; (c) auditar el registro de `BotEquipmentProgression`.

No existe **ninguna dependencia estructural de `GameClient`** que impida el gameplay server-side. NO-GO descartado.

---

## 20. Próximo paso recomendado

1. En TARGET: confirmar visibilidad runtime del bot (test primario §17) y auditar `BotEquipmentProgression` (¿registrado en `ON_PLAYER_LEVEL_CHANGED`?).
2. Simplificar `AdminBotManager` hacia el modelo `lifecycle + BotProfile + Player nativo`, eliminando cualquier follow/assist/think custom que duplique AutoPlay.
3. Sustituir la selección de skills custom por `giveAvailableSkills(...)`.
4. Recién entonces abrir BOTAI-05.2 (Community Board como panel + progresión por evento nativo).

---

### Leyenda de estados usados
`[VERIFICADO]` leído en source indexado · `[NO VERIFICADO]` no confirmable en este repo (archivo/DB/datapack fuera del índice) · `[HIPÓTESIS]` inferencia razonable pendiente de lectura.

---

**Fin del informe original (Devin).**
---

## ANEXO A — Verificación local Cline (2026-09-14)

Verificación de los anchors del informe contra el source real del workspace local:

**UPSTREAM**: `C:\L2J MOBIUS\UPSTREAM\L2J_Mobius\L2J_Mobius_CT_2.6_HighFive\java\org\l2jmobius\gameserver\`
**TARGET custom**: `C:\L2J MOBIUS IA\L2J_Mobius_CT_2.6_HighFive\game\data\scripts\`

| Claim del informe | Verificación | Estado |
|---|---|---|
| `Player.getClient()` simple getter (4117-4120) | Leído `entity/actor/Player.java:4117-4120` → `return _client;` multilínea | **VERIFIED-SOURCE** |
| `sendPacket` null-guard (4429-4435) | Leído `Player.java:4428-4435` → `if (_client != null) { _client.sendPacket(packet); }` | **VERIFIED-SOURCE** |
| grep `getClient().` en Player.java = 0 | `Select-String -Pattern 'getClient\(\).'` sobre `Player.java` (15.358 líneas) → **0 coincidencias** | **VERIFIED-SOURCE** |
| `isOnline()` = `_isOnline` puro (7905-7908) | Leído `Player.java:7905-7908` | **VERIFIED-SOURCE** |
| `isOnlineInt()` devuelve 0 con client null (7910-7918) | Leído `Player.java:7910-7918` → `return 0;` si `_client == null` | **VERIFIED-SOURCE** |
| `isInOfflineMode()` (7987-7990) | Leído `Player.java:7987-7990` → `return (_client == null) \|\| _client.isDetached();` | **VERIFIED-SOURCE** |
| `giveAvailableSkills` (2799-2804) | Leído `Player.java:2799-2804` → `SkillTreeData.getInstance().getAllAvailableSkills(...)` | **VERIFIED-SOURCE** |
| `load` termina en `setOnlineStatus(true,false)` sin `setClient` (~7341-7413) | Leído `Player.java:7341-7413` → restaura inventory/warehouse/skills/macros/shortcuts/henna/tpbookmark/recipe, `restoreHenna()` (7356), `setOnlineStatus(true, false)` (7408), retorno en 7412. No llama `setClient` | **VERIFIED-SOURCE** |
| `doDie` server-side (5241-5488) | Leído `Player.java:5242-5488` → `super.doDie` + penalizaciones; `DecayTaskManager.getInstance().add(this)` solo `if (PlayerConfig.DISCONNECT_AFTER_DEATH)` (5482-5485) | **VERIFIED-SOURCE** |
| AutoPlay guard (78-84) y scheduling (388-392) | Leído `taskmanagers/AutoPlayTaskManager.java:78-84` (guard exacto) y `388-392` (`schedulePriorityTaskAtFixedRate`) | **VERIFIED-SOURCE** |
| AutoPlay target/geo (285-307), assist/follow (266-282), pickup (259), attack (327) | Leído `AutoPlayTaskManager.java` → `World.getNearestVisibleObjectInRange` (285), bloque assist/follow (268-281), `player.doPickupItem(droppedItem)` (259), `setIntentionAttack(creature)` (327) | **VERIFIED-SOURCE** |
| AutoUse guard (79-83) y scheduling (452) | Leído `taskmanagers/AutoUseTaskManager.java:79-83` (guard exacto) y `431-454` → `schedulePriorityTaskAtFixedRate` (452) | **VERIFIED-SOURCE** |
| AutoUse `onItemUse` (141-146, 165-171) y buffs/skills | Leído `AutoUseTaskManager.java:141-142, 165-167` (`ItemHandler...onItemUse`) y `230-263` (`caster.doCast(skill)` buffs a 244-262) | **VERIFIED-SOURCE** |
| Party add/broadcast (247-384) y looters (172-242) | Leído `entity/groups/Party.java:247-274` (`broadcastToPartyMembersNewLeader`, `broadcastToPartyMembers` vía `member.sendPacket`), `280-384` (`addPartyMember`), `172-242` (`getCheckedNextLooter`/`getActualLooter`) | **VERIFIED-SOURCE** |
| OfflinePlayTable restore parties (227-232) | Leído `data/sql/OfflinePlayTable.java:227-232` → `new Party(leader, ...)` + `member.joinParty(party)` | **VERIFIED-SOURCE** |

**Conclusión del ANEXO A:** el 100% de los anchors de source verificables en UPSTREAM coinciden con el informe. No se encontró ninguna discrepancia.
---

## ANEXO B — Verificación de custom files en TARGET (Cline, 2026-09-14)

Los archivos custom **sí existen y son legibles** en el workspace local. Esto actualiza el §13 del informe
(que estaba `[NO VERIFICADO]` por no estar indexados en el repo remoto):

| Archivo (ruta relativa a `game\data\scripts\`) | Hallazgo | Estado |
|---|---|---|
| `handlers\chat\commands\admin\AdminBotManager.java` | Existe; `implements IAdminCommandHandler`, comando `admin_bot`; contenedor central. `BOT_IDS` = 268483200..268483207. Existe `public static boolean isBot(int objectId)` (línea 335). **`AdminBotManager.java.bak` preservado** (regla §11). | **VERIFIED** (existe / estructura principal) |
| `handlers\chat\commands\admin\BotSession.java` | Existe (tracking de estado + resolución World, sin AI/combat/DB — diseño nativo-compatible). | **VERIFIED** |
| `handlers\chat\commands\admin\BotProvisioning.java` | Existe (provisioning: clase/nivel/loadout). | **VERIFIED** (existe) |
| `handlers\chat\commands\admin\BotPreset.java` / `BotGradeLoadout.java` / `BotGradeLoadoutRegistry.java` | Existen (loadout declarativo pieza por pieza; registry por grade). Usados por `BotEquipmentProgression`. | **VERIFIED** (existen) |
| `ai\others\BotProgression\BotEquipmentProgression.java` | **CIERRA RIESGO #3.** Está registrado como listener nativo: `@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)` (línea 46) + `@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)` (línea 47); método `onPlayerLevelChanged` gated por `AdminBotManager.isBot(player.getObjectId())` (línea 51). Aplica equipo por grade (`BotGradeLoadoutRegistry.resolveGrade`), con idempotencia vía `PlayerVariables` key `BOT_EQUIP_PROGRESSION` (v1). | **VERIFIED** — riesgo #3 CERRADO |
| `MasterHandler.java` | `AdminBotManager.class` registrado en el handler (líneas 121, 431). | **VERIFIED** |

**Implicaciones para el §13/§15 del informe:**
1. `BotEquipmentProgression` **NO es candidata a eliminación**: está bien integrada (registro nativo
   `ON_PLAYER_LEVEL_CHANGED` + gate por `isBot` + idempotencia por `PlayerVariables`). La advertencia del
   §13.C/§15 queda **superada por evidencia** en el TARGET actual.
2. El árbol de custom files ya refleja el modelo `AdminBotManager = orchestrator + BotProfile declarativo
   (BotGradeLoadout*) + Player nativo`, coherente con la arquitectura propuesta en §16.
3. Queda pendiente solo la **verificación runtime** de visibilidad (§10/§17 — riesgo B) para dar GO puro.
   El test de §17 es la ruta recomendada. Nota de precisión: los spikes C2/D-0001 probaron el ciclo de
   vida clientless **en aislamiento** (`World players=0`), por lo que la visibilidad ante un humano/GM
   real sigue pendiente de validar (ver `investigations/FASE4_17_CLIENTLESS_VISIBILITY.md`,
   estado NOT_FOUND/PENDING_REVERIFICATION).

---

## Cross-references al resto del Notebook

- Claims que respaldan: `CLAIMS.md` — CL-0002 (sendPacket null-safe), CL-0008 (Player GameClient==null safe),
  CL-0009 (C2 spike PASS), CL-0013 (D-0001 AutoPlay clientless PASS), CL-0014 (Party+AutoPlay+Assist PASS).
- Spikes runtime: `evidence/spikes/D0001/MANIFEST.md` (P1-P4 PASS, AutoPlay clientless real), `evidence/spikes/PartyAutoPlay/`.
- Investigaciones relacionadas: `investigations/FASE4_02_PLAYER_CLIENTLESS.md`, `investigations/FASE4_17_PLAYER_CLIENT_STATE.md`,
  `investigations/FASE4_17_CLIENTLESS_VISIBILITY.md` (visibilidad — NOT_FOUND/PENDING_REVERIFICATION: los spikes C2/D-0001 corrieron con aislamiento `World players=0`, por lo que **no** probaron visibilidad ante un humano; la visibilidad real frente a GM sigue siendo RUNTIME REQUIRED),
  `investigations/FASE4_01_AUTOPLAY.md`, `investigations/FASE4_09_FOLLOW_ASSIST_COMBAT.md`,
  `investigations/BOTAI-02_ROLES_BEHAVIORS_API_RESEARCH.md`, `investigations/BOTAI-03-B_PROVISIONING_LIFECYCLE_CLIENTLESS_PERSISTENCE.md`,
  `investigations/BOTAI-04-B1.1_EQUIPMENT_PENDING_CLOSURE.md`.
- Arquitectura: `bots/BOT_RECIPE.md`, `decisions/FASE4_BOT_ARCHITECTURE_FREEZE.md` (regla de oro: NO tocar
  `GameClient`, `Player`, `OfflinePlayTable`, `Party`, `CommandChannel`, `PlayerAI`, `AttackableAI`,
  `AutoPlayTaskManager`, `AutoUseTaskManager`, `CommunityBoardHandler`).
- Conocimiento: `knowledge/SOURCE_EVIDENCE.md` (§11 sendPacket 4429-4435), `knowledge/CONSTRAINTS.md`.

---

## CRITERIO DE CIERRE

**SPRINT BOTAI-05.1 CERRADO — GO CONDICIONAL.**

- Veredicto del informe confirmado por verificación local (ANEXO A, 100% anchors OK).
- Riesgo #3 cerrado por evidencia TARGET (ANEXO B).
- Pendientes para GO puro: (a) visibilidad runtime (test §17); (b) barrido `getClient()` externo
  (Cat A/B ya superado por BOTAI-04; Cat C residual bajo riesgo).
- Siguiente: BOTAI-05.2 (Community Board panel + progresión por evento nativo) — NO abrir hasta
  validar visibilidad runtime y documentar el resultado.

---

**FIN DEL DOCUMENTO BOTAI-05.1**