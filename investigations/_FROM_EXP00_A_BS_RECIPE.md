# RECIPE A — Feature `//bs`: comando GM que convoca un escuadrón de party server-side

> Receta transferible que COMPONE RECIPE B (`../B_bots/recipe.md`). Un agente nuevo debe leer ambas:
> B explica el substrate nativo; A explica cómo exponerlo como intención de GM con el punto de
> extensión nativo de comandos admin.
> Procedencia: anclas UPSTREAM e2518ab108 verificadas en FASE 1 y re-verificadas por fresh-agent
> (ver `../B_bots/refresh.md` y `refresh.md` en este directorio).

- **problem_class**: GM_PRODUCTIVITY_COMMAND (intención: "que aparezca mi equipo y luche conmigo,
  con un comando, y que se quede hasta que yo lo despida")
- **resultado**: `//bs [N]` convoca N bots (default 8, máx 8) alrededor del GM, forma party nativa con
  el GM como líder, activa el motor nativo AutoPlay en los bots (follow + assist al líder). `//bs off`
  los despide limpiamente. Sin shutdown, sin desaparición programada, sin dependencia del harness.
- **clasificación global**: composición de NATIVO_PLATAFORMA + 1 clase de extensión RUNTIME_LOCAL
  (~200 líneas) + config datapack + provisión SQL. Core de Mobius: SIN TOCAR.

## PROBLEM CLASS

"Como GM quiero un comando único que materialice mi intención ('quiero mi party de bots aquí, ahora')
usando lo que la plataforma ya sabe hacer, sin instalar un sistema de bots."

Puntos que la plataforma ya resuelve (y cómo se llega a ellos):
1. ¿Dónde vive un comando `//`? → handlers admin como scripts de datapack + MasterHandler.
2. ¿Quién puede ejecutarlo? → gates nativos de `AdminCommandHandler.onCommand` + `AdminData.hasAccess`.
3. ¿Cómo se materializan los compañeros? → RECIPE B (substrate).

## NATIVE CAPABILITIES (anclas; capa de comando)

| # | Capacidad | Ancla (UPSTREAM) | Clase |
|---|---|---|---|
| A1 | Los comandos admin (`//x` → interno `admin_x`) se resuelven con `AdminCommandHandler`: `onCommand(player, cmd, useConfirm)` valida `isGM()`, busca handler, valida `hasAccess`, y ejecuta en ThreadPool | `handler/AdminCommandHandler.java:39-163` (onCommand L83-146) | NATIVO_PLATAFORMA |
| A2 | `registerHandler` es PÚBLICO y map-driven: cualquier clase `IAdminCommandHandler` puede registrar `admin_*` | `handler/AdminCommandHandler.java:50-57` | NATIVO_PLATAFORMA |
| A3 | Los handlers admin STOCK viven como SCRIPTS de datapack en `data/scripts/handlers/chat/commands/admin/*.java` (AdminKill, AdminHeal, …) implementando `IAdminCommandHandler` con `getCommandList()` | runtime `game/data/scripts/handlers/chat/commands/admin/` (stock Mobius CT 2.6) | NATIVO_PLATAFORMA |
| A4 | `MasterHandler.main()` instancia por reflexión cada clase listada en `HANDLERS` y la registra en el handler-map correspondiente | runtime `game/data/scripts/handlers/MasterHandler.java` (main + bucle de registro) | NATIVO_PLATAFORMA (mecanismo) / RUNTIME_LOCAL (la lista editada) |
| A5 | El motor de scripts compila TODOS los `.java` bajo `data/scripts` y ejecuta su `main(String[])` al boot (y con `executeScript(path)` bajo demanda) | `scripting/ScriptEngine.java:55-209` · `scripting/engine/ScriptExecutor.java:84-216` | NATIVO_PLATAFORMA |
| A6 | Permisos: `AdminData.hasAccess` — comando NO listado en AdminCommands.xml se auto-concede SOLO al accessLevel master; si se lista, se respeta su `accessLevel` | `data/xml/AdminData.java:152-189` · `game/config/AdminCommands.xml` · `game/config/AccessLevels.xml` (master=100) | NATIVO_PLATAFORMA (mecanismo) / RUNTIME_LOCAL (entrada XML) |
| A7 | Substrate de bots completo | RECIPE B (B1-B12) | NATIVO_PLATAFORMA |


## COMPOSITION (qué hace exactamente `//bs`)

```
GM escribe //bs            → cliente envía admin_bs
AdminCommandHandler        → isGM + hasAccess + ThreadPool (nativo, A1/A6)
AdminBotSquad (nuestro)    → 1 archivo datapack script registrado en MasterHandler (A3/A4)
  ├─ guards: GM sin party previa; squad no activo; N 1..8
  ├─ para cada bot: RECIPE B pasos 1-7 (load→online→offlinePlay→spawnMe→settings→autosave remove→startAutoPlay)
  ├─ RECIPE B paso 8: new Party(gm, FINDERS_KEEPERS) + gm.setParty + bots.joinParty
  └─ respuesta al GM (sendMessage) + registro de estado estático (party, bots[])
GM escribe //bs off        → teardown RECIPE B (leaveParty→offlinePlay false→stopAutoPlay→
                             autosave remove→stopVitalityTask→online(false,false)→decayMe)
```

Lo que la feature NO hace (y por qué no hace falta):
- NO IA de combate (B7 nativo), NO scheduler (el motor ya cicla a 700ms), NO shutdown (era del harness),
  NO persistencia custom (los Player.load son filas DB reales), NO core changes, NO rebuild.

## EXTENSION POINTS (usados)

1. `data/scripts/handlers/chat/commands/admin/` + `MasterHandler` HANDLERS (A3/A4) — punto de extensión
   oficial para comandos admin en este fork. La clase nueva se añade a la lista; el residuo
   `AdminBotSpike` se retira de la lista y del disco (se archiva copia en `residue-archive/`).
2. `game/config/AdminCommands.xml` (A6) — entrada explícita `admin_bs` (accessLevel=100) para no depender
   del auto-grant implícito de master.
3. `game/config/Custom/AutoPlay.ini` (B9) — gates del motor.
4. DB `characters` (B12) — provisión de los 8 bots (SQL one-shot).

## ORDER OF OPERATIONS (deploy completo, datapack-only, sin rebuild)

1. (Una vez) Ejecutar `db/0004_bot_squad_provisioning.sql` contra `l2jmobiush5` → BSBOT01..08
   (ids 268483120..268483127, clones de la fila del GM de pruebas, `online=0`).
2. Copiar `AdminBotSquad.java` a `game/data/scripts/handlers/chat/commands/admin/`.
3. Editar `game/data/scripts/handlers/MasterHandler.java`: retirar residuo `AdminBotSpike`
   (import + entrada en HANDLERS) y añadir `AdminBotSquad` en su lugar.
4. Retirar residuo del camino vivo: borrar `game/data/scripts/bs.java` y
   `game/data/scripts/handlers/chat/commands/admin/AdminBotSpike.java` (archivados antes en exp00).
5. Bajar el gate del harness: `custom/BotSpikeHuman/BotSpikeHuman.ini` → `BotSpikeHumanEnabled = False`
   (elimina el auto-shutdown del boot).
6. Verificar `game/config/Custom/AutoPlay.ini`: `EnableAutoPlay=True`, `AssistLeader=True`.
7. Añadir a `game/config/AdminCommands.xml`: `<admin command="admin_bs" description="Summon Bot Squad party members (RECIPE A)." accessLevel="100" />`
8. Arrancar GS y validar (protocolo interactivo: confirmación GM antes de cada paso runtime).
   La compilación de scripts ocurre al boot (A5) — si el script no compila, el GS lo loguea y sigue.

## CONSTRAINTS

- Todos los guards del comando son obligatorios: GM sin party previa (evita huérfana), squad único
  (re-invocación duplicaría spawns "Duplicate character!"), N∈[1,8].
- La clase de extensión mantiene el estado del escuadrón en campos estáticos (gap real mínimo,
  documentado en B). NO es un "bot manager": no hay polling, ni timers, ni teardown programado.
- No tocar UPSTREAM ni core JAR; todo lo anterior es datapack/config/DB.
- La prueba runtime se hace en zona con mobs melee low-level (Talking Island, como D-0001/knowledge/20).
- Comportamiento del Shutdown nativo con bots aún activos: DESCONOCIDO → usar `//bs off` antes de apagar.

## VALIDATION (cadena completa del Experimento 0)

```
//bs → bots creados → bots server-side válidos (client==null, spawned, World registrados)
    → party real (1+N, líder=GM) → follow (sin target) → assist (con target)
    → combate real (HP changes en mobs, varios atacantes) → estabilidad (sin shutdown/decay)
    → permanencia (siguen activos hasta //bs off) → //bs off → mundo y DB limpios
```
- También debe quedar explícito en el informe: qué comportamientos previos eran del harness
  (ventanas de tiempo, teardown forzado, shutdown programado, detección de humano) y qué pertenece
  a la solución (todo el ciclo de vida del substrate B).
- Evidencia: `game/log/java0.log` con timeline; veredicto sin `Shutdown.startShutdown` en el camino.

## RE-DERIVATION (agente nuevo, sin memoria de esta sesión)

1. Leer intención + esta receta + RECIPE B.
2. Re-verificar anclas A1-A7 y B1-B12 en source/runtime (no confiar: abrir los archivos).
3. Confirmar estado del runtime: gates, MasterHandler, AdminCommands.xml, filas BSBOT en DB.
4. Detectar drift (p.ej. residuo re-aparecido, config cambiada) y registrar hallazgos.
5. Adaptar receta si algo cambió; solo entonces implementar/validar.

## ADAPTATION

- Otro comando/nombre: cambiar la lista de `getCommandList()` y la entrada XML — nada más.
- `.bs` de voz (IVoicedCommandHandler) sería el mismo patrón con otro registro; NO requerido.
- Más escuadrones (varias parties/CC): fuera de alcance (DESCONOCIDO; knowledge/16 C4 bloqueado por GM).
- Otros comandos de producto (buffs, merchants, teleports): fuera del alcance de esta receta.

