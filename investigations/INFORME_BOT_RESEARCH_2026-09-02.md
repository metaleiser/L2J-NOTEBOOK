# INFORME DE INVESTIGACIÓN — Bots L2J Mobius IA

> **Fecha**: 2026-09-02
> **Modo**: Plan (research end-to-end) → act (solo guardado de informe)
> **Alcance**: Mejora del sistema de bots existente en L2J Mobius, sobre la arquitectura UPSTREAM
> **Regla arquitectónica**: Mobius source es la autoridad. Forks externos son prior-art/research únicamente.

---

## 1. ESTADO ACTUAL DEL SISTEMA (Validado)

### R-BS001 — `//bs` (VALIDADO Y FUNCIONANTE)

- **Estado**: PASS (2026-09-02, runtime L2J MOBIUS H5 SERVER, sesiones 00:13:11/00:17:23)
- **Componentes desplegados**:
  - `AdminBotSquad.java` → `data/scripts/handlers/chat/commands/admin/`
  - SQL provisioning: BSBOT01..08 (charId 268483120..268483127)
  - `AdminCommands.xml` entrada `admin_bs` (accessLevel=100)
  - `AutoPlay.ini` gates: EnableAutoPlay=True, AssistLeader=True

### Capacidates nativas verificadas en runtime

| Capacidad | Anchor Mobius | Evidencia |
|-----------|---------------|-----------|
| Player.load(charId) | Player.java:1211-1213 | REF-PLAYER-CLIENTLESS CL-0001 |
| setOnlineStatus(true,false) — sin DB write | Player.java:7020 | CL-0003 |
| setOfflinePlay(true) — gate AutoPlay clientless | Player.java:7960-7967 | CL-0004 |
| spawnMe(x,y,z) — World registration | WorldObject.java:160-215 | CL-0005 |
| AutoPlayTaskManager (700ms ciclo PvE) | AutoPlayTaskManager.java:366 | CL-0011 |
| Party join nativo (Player.joinParty) | Player.java:6862 | CL-0005 |
| AssistLeader follow+assist | AutoPlayTaskManager.java:264-282 | CL-0008 |
| Teardown limpio sin DB writes | REF-PLAYER-CLIENTLESS §3 | D-0001 P4 PASS |
| Party 9 miembros (1+8) reales | BotSpikeParty | CL-0014 |

### Spikes previos (harness desechables, no features)

| Spike | Fecha | Duración | Topología | Verdict |
|-------|-------|----------|-----------|---------|
| BotSpikeC02 | 2026-09-01 | 60s | 1 cliente clientless | PASS |
| BotSpikeParty | 2026-09-01 | 75s | 1 leader clientless + 8 bots | PASS |
| BotSpikeHuman | 2026-09-01 | 75s | 1 leader humano + 8 bots | PASS |

---

## 2. ARQUITECTURA UPSTREAM MOBIUS (Anclas críticas)

### AutoPlayTaskManager (motor principal)

- **Tipo**: Singleton `Runnable`, threadPool a 700ms
- **Gate** (L80): `isOnline() && (!isInOfflineMode() || isOfflinePlay()) && ENABLE_AUTO_PLAY`
- **Targeting** (L190-245): migración de bot a mob aggroable más cercano dentro de 3000 unidades
- **Follow/Assist** (L264-282): nativo cuando `AssistLeader=True`; guard `leader == null` evita NPE con líder clientless
- **Config**: `game/config/Custom/AutoPlay.ini` (no `config/AutoPlay.properties` en este fork — ancla frágil)

### AutoUseTaskManager

- Maneja auto-use actions: potions, items, skills por jugador
- Configurable per-player vía `AutoUseSettingsHolder` (autoActions Set, autoPotionItem, etc.)

### Party

- `MAX_MEMBERS = 9` hardcodeado (Party.java:71)
- `List<Player> _members` en Party.java:88
- Líder = primer miembro añadido
- **Flujo correcto**: `Player.joinParty(Party)` — `Party.addPartyMember(Player)` NO actualiza `_party` del jugador (CL-0005)

### Player clientless (lifecycle nativo probado)

`Player.load(int) → setOnlineStatus(true,false) → setOfflinePlay(true) → spawnMe(x,y,z) → PlayerAutoSaveTaskManager.remove(bot) → AutoPlayTaskManager.startAutoPlay(bot)`

Teardown (orden CRÍTICO): `leaveParty() → setOfflinePlay(false) ANTES de stopAutoPlay → stopAutoPlay → PlayerAutoSaveTaskManager.remove → stopVitalityTask → setOnlineStatus(false,false) → decayMe()`

### Config gates globales relevantes

| Flag | Ubicación | Efecto |
|------|-----------|--------|
| EnableAutoPlay | AutoPlay.ini | Motor global |
| AssistLeader | AutoPlay.ini | Follow/assist party |
| EnableAutoPotion/Skill/Item | AutoPlay.ini | AutoUse |
| ShortRange/LongRange | AutoPlay.ini | Rango targeting (600/1400) |

---

## 3. INVESTIGACIÓN DE FORKS (Prior Art)

### 3.1 L2Autobots (Kotlin, L2J Scions híbrido)
**Ubicación**: `investigacion features y forks/L2Autobots-master/`

| Componente | Descripción | Valor para Mobius |
|------------|-------------|-------------------|
| `AutobotScheduler.kt` | Scheduling con fases | MEDIO — concepto |
| `AutobotData.kt` | Config data-driven de bots | **ALTO** — configuración externa |
| `CombatBehavior.kt` | Combate + selección de skills | MEDIO |
| `BuffBehavior.kt` | Buff management automático | **ALTO** |
| `SupportBehavior.kt` | Soporte/healing al party | **ALTO** |
| `MovementBehavior.kt` | Movimiento | BAJO — ya nativo en Mobius |
| `AutobotClient.kt` | Network client custom | **INCOMPATIBLE** — viola clientless |
| `AdminAutobots.kt` | Admin UX + `.bot` comandos | MEDIO — referencia de UX |

**Ideas rescatables (concepto, no código)**:
- Config data-driven de comportamientos sin subclassing
- Buff automático antes de combate
- Soporte/healing automático a party members
- Admin UX de comandos por slug (`.bot spawn`, `.bot buff`, etc.)

### 3.2 L2jRoboto (Java, L2J datapack C4-ish)
**Ubicación**: `investigacion features y forks/L2jRoboto-master/`

| Componente | Descripción | Valor para Mobius |
|------------|-------------|-------------------|
| `FakePlayer extends Player` | Subclase Player | **INCOMPATIBLE** — viola arquitectura |
| `FakePlayerAI` con thinkAndAct | AI custom reemplazando nativa | **INCOMPATIBLE** |
| `CombatAI` | Prioridades de skills ordenadas | **ALTO** — modelo de decisión |
| `BotSkill` model (Healing/Offensive/Support) | Skills con prioridad | **ALTO** |
| `handleShots/getShotId` | Gestión automática de shots por level | **ALTO** — concepto |
| `FakePlayerManager` | Registro/spawn/cleanup | BAJO — ya cubierto por R-BS001 |

**Ideas rescatables (concepto)**:
- Selección de skills por prioridad + disponibilidad
- Configuración healer/support: `HealingSpell` con `priority` y `healPercent`
- Auto-shots por rango de level (idea, no ids C4 — HighFive tiene sus propios itemIds)

### 3.3 HighFive-master (fork L2JMobius CT 2.6 HighFive)
**Ubicación**: `investigacion features y forks/HighFive-master/`

| Componente | Descripción | Valor para Mobius |
|------------|-------------|-------------------|
| `BotReportTable.java` | Sistema de reportes de bots | **ALTO** — telemetría anti-bot |
| `FakePlayerHolder.java` | Holder para fake players | BAJO |
| `FakePlayerChatManager.java` | Chat management (frecuencia/spam) | MEDIO |

**Ideas rescatables**:
- Reportes/telemetría server-side de actividades sospechosas
- Chat management con límites de frecuencia

### 3.4 l2-smartbot-main (descubierto en este pase)

> **Nota de integridad**: descubierta durante un 2º pase al directorio de investigación. Código para L2J C4/SF — NO Mobius CT 2.6. Es la fuente de prior-art más completa para la siguiente fase.

| Componente | Rol | Valor para Mobius |
|------------|-----|-------------------|
| `SmartBotController.java` | Loop Runnable: follow/assist/attack/heal/res/buff/chat con presets por rol | **ALTO** — hoja de ruta de features |
| `SmartBotManager.java` | Singleton manager + persistencia + restore tras restart | **ALTO** — concepto manager/persistencia |
| `SmartBotDao.java` | DAO SQL (tabla smart_bots) | MEDIO — requiere DB schema propio |
| `SmartBotPreset.java` | Presets: ARCHER/MAGE/HEALER/BUFFER/DAGGER/TANK | **ALTO** — perfilado por rol |
| `BotLlmService.java` | LLM local (Ollama) para chat | BAJO — no esencial para PvE |
| `config/smartbot/knowledge/*.txt` | Knowledge base de hechos | BAJO — opcional |

**Ideas rescatables**: presets por rol con skills candidatas por clase; healer cura+res; buffer con sets fighter/mage; persistencia+restore; concepto de "controller" por bot que reemplaza la necesidad de steering manual.

### 3.5 Cross-analysis forks vs Mobius

| Capacidad | Mobius nativo | L2Autobots | L2jRoboto | SmartBot | HighFive |
|-----------|---------------|------------|-----------|----------|----------|
| Spawn bot clientless | ✅ (R-BS001) | ✅ (client wrapper) | ✅ (FakePlayer) | ✅ | ✅ |
| Party follow/assist | ✅ (AutoPlay L264-282) | ✅ (custom) | ✅ (custom) | ✅ (custom) | ✅ |
| Combat basic (auto-attack + target) | ✅ (AutoPlay 700ms) | ✅ | ✅ | ✅ | ✅ |
| Skill rotation / priority | ⚠️ (AutoUse actions limitado) | ✅ (CombatBehavior) | ✅ (CombatAI priority) | ✅ (presets) | ⚠️ |
| Buff gestión | ❌ (no nativo bots) | ✅ (BuffBehavior) | ⚠️ | ✅ (buffer preset) | ❌ |
| Heal/support de party | ❌ (no nativo) | ✅ (SupportBehavior) | ✅ (HealingSpell) | ✅ (healer preset) | ❌ |
| Auto-shots/ammo | ⚠️ (AutoUse simple) | ✅ | ✅ (handleShots) | ✅ (preset items) | ❌ |
| Persistencia/restore | ❌ (solo DB manual) | ❌ | ❌ | ✅ (SmartBotDao) | ❌ |
| LLM/chat | ❌ | ⚠️ | ❌ | ✅ (Ollama) | ❌ |
| Telemetría/reporting | ⚠️ (logs manuales) | ⚠️ | ❌ | ❌ | ✅ (BotReport) |

---

## 4. CANDIDATOS DE MEJORA (Clasificación)

### CATEGORÍA A — ALREADY AVAILABLE IN MOBIUS (no implementar; documentar)

| ID | Candidato | Evidencia |
|----|-----------|-----------|
| A1 | Spawn de bots clientless server-side | R-BS001 validado |
| A2 | Party follow/assist nativo (AssistLeader) | AutoPlay L264-282 |
| A3 | AutoPlay PvE (700ms) targeting + ataque | AutoPlayTaskManager |
| A4 | AutoUse actions (potions/items/skills) | AutoUseTaskManager |
| A5 | Teardown limpio sin DB writes | REF-PLAYER-CLIENTLESS |
| A6 | SQL provisioning idempotente | REF-SQL-PROVISION |

### CATEGORÍA B — COMPOSABLE WITH MOBIUS (sin core changes; scripts + config nativos)

| ID | Candidato | Origen | Adaptación |
|----|-----------|--------|------------|
| B1 | Perfiles por rol data-driven (JSON/XML preset) | SmartBotPreset, AutobotData | Script reader en datapack; usar `getSkill(id)` nativo |
| B2 | Skill priority queue | L2jRoboto CombatAI | Script helper; AutoUse actions nativas + timers |
| B3 | Buff automático pre-combat / party | BuffBehavior, SmartBotController | Script ticker + `useMagic` nativo |
| B4 | Healer support (heal + res party) | SupportBehavior, SmartBot healer preset | Script ticker + HP checks + useMagic |
| B5 | Shots/ammo gestión automática | L2jRoboto handleShots, SmartBot items | AutoUse nativo + config de items por clase |
| B6 | Telemetría/reporting de bots | HighFive BotReportTable | Logging estructurado + tabla opcional |
| B7 | Control de distancia/leash | AutoPlay ShortRange/LongRange | Config nativa per-player |
| B8 | Party management avanzado (invite/distribute) | SmartBot party | Party nativo + comando admin |

### CATEGORÍA C — CUSTOMIZABLE ON MOBIUS (requiere solo config/extension datapack)

| ID | Candidato | Requisito |
|----|-----------|-----------|
| C1 | Cambiar presets/timing sin recompile | Archivos config/JSON externos |
| C2 | Mapear skill IDs a clases HighFive | Tabla de mapeo en datapack |
| C3 | Soporte multi-clase (archer/mage/tank/etc.) | Presets por plantilla de clase |

### CATEGORÍA D — REQUIRES MINIMAL EXTENSION (pequeño script/core touch)

| ID | Candidato | Tipo de extensión |
|----|-----------|-------------------|
| D1 | Bot controller lifecycle autónomo (spawn→tick→dismiss) | Script datapack nuevo |
| D2 | Persistencia/restore de bots tras restart | SQL tabla + script; requiere salvaguardas |
| D3 | GM dialogo de control (lista de bots activos) | Admin command (ya `//bs`) + tab HTML |
| D4 | LLM chat (Ollama) opcional | Script service externo — NON-essential |

### CATEGORÍA E — INCOMPATIBLE / DISCARD

| ID | Candidato | Razón |
|----|-----------|-------|
| E1 | FakePlayer extends Player (Roboto) | Viola arquitectura Mobius (authority = source) |
| E2 | Custom AI reemplazando AutoPlayTaskManager | Reemplaza mobius nativo; prohibido |
| E3 | Network client wrapper (L2Autobots AutobotClient) | Bypassa clientless; innecesario |
| E4 | SmartBotDao con tabla propia + L2PcInstance.create desnudo | No sigue patrones Mobius (Party/join/AutoPlay); replica engine |
| E5 | LLM como dependencia crítica | No esencial; solo opcional |

---

## 5. ORDEN PROPUESTO DE IMPLEMENTACIÓN

### Fase 1 — Composición nativa (sin core, sin rebuild)
- B1: Perfiles data-driven (JSON) → primera base reusable
- B7: Config de rango/leash por bot
- B8: Party management avanzado (invite/distribute por GM)

### Fase 2 — Soporte avanzado (tickers por script)
- B3: Buff automático pre-combat / party
- B4: Healer support (heal + res)
- B2: Skill priority queue
- B5: Shots/ammo automático

### Fase 3 — Extensión mínima
- D1: Bot controller lifecycle autónomo (encapsula fases 1-2)
- B6: Telemetría/reporting
- D3: Panel GM de control de bots

### Fase 4 — Factores además / no urgente
- D2: Persistencia/restore tras restart (requiere revisión DB + salvaguardas)
- D4: LLM chat opcional (Ollama)

### Descartado formalmente
- E1–E5 (ver §4)

---
## 6. PRUEBAS SERVER-SIDE (matriz mínima, PvE)

> Metodología: usar reales server-side `Player` clientless (patrón BotSpike probado: load → online(false,true) → offlinePlay → spawnMe → PlayerAutoSaveTaskManager.remove → AutoPlay start) + Party nativo. Sin client.

### Pruebas por candidato (las mínimas no redundantes)

| Candidato | Prueba | Escenario | Pass criteria |
|-----------|--------|-----------|---------------|
| B1 | Cargar presets distintos (melee/archer/buffer) | 3 bots + 1 leader | Cada bot lee su preset correcto; skills disponibles acordes a su clase |
| B2 | Skill priority queue vs cooldown | 1 bot vs mob | Usa skill de mayor prioridad disponible; respeta cooldown; no castea skill no aprendida |
| B3 | Buff pre-combat | 1 bot + 1 leader party | Bot aplica buff antes de atacar; se observa efecto en target (getEffectList) |
| B4 | Heal + resé | 1 healer + 1 bot dañado | Bot con HP<umbral recibe heal; bot muerto recibe res |
| B5 | Shots automático | 1 bot melee/archer | Adequado shot del nivel; no overflow inventario indefinido |
| B6 | Reporting pasivo | Sesión 60-75s de party | Log resumido por sesión (spawn/teardown, combat events, errores) |
| B7 | Rango/leash | 1 bot a distancia | Bot ataca dentro de rango configurado; no persigue fuera de leash |
| B8 | Party distribute | 1 leader + bots | Modo FINDERS_KEEPERS/random funciona nativo; sin residuo al teardown |
| D1 | Lifecycle autónomo | `//bs off` + recreate | Dismiss deja World/DB limpios (patrón R-BS001) |
| D2 | Restore tras restart | 1 bot persistido + restart | Bot vuelve activo con online status correcto y sin duplicados |

### No re-probar (ya validado en R-BS001/REF-*)
- Player.load clientless, spawnMe, setOnlineStatus sin DB write
- Party join/follow/assist nativo
- Teardown orden crítico
- Provisioning SQL idempotente

---

## 7. RESTART/RELOAD REQUIRIMIENTOS

| Cambio | Requiere | Por qué |
|--------|----------|---------|
| Script datapack nuevo (custom/*.java) | **no reload/restart** (solo boot siguiente) | Scripts se compilan en boot; no toca core |
| Config JSON/INI de bots (B1/B8) | **no reload/restart** si se re-lee bajo demanda | Leer config al spawn; reconnect sin recompile |
| Script modificado | **script reload** (si el fork lo soporta) o **boot** | ScriptManager reload de handlers/skills; no afecta world object |
| Core Java modificado (no esperado) | **rebuild + full restart** | Requiere recompilar jar |
| Config core (AutoPlay.ini etc.) | **full server restart** | Se carga en boot (Si el server lee config en boot) |
| SQL tabla nueva (D2) | **DB migration/reload + server restart** | Nueva tabla requiere restaurar DB |
| AdminCommands.xml | **no restart** (si hot-load) o **boot** | Depende del fork; verificar |
| Solo ejecutar pruebas BotSpike | **no restart** (in-process scripts) | Harness corre en boot tras scripts |

---
## 8. KNOWLEDGE / SKILL / RECIPE OPPORTUNITIES

### KNOWLEDGE (reutilizable, documentado ya en REF-*)
- Clientless Player lifecycle (REF-PLAYER-CLIENTLESS)
- AutoPlayTaskManager internals + gate + asistencia (REF-PARTY-AUTOPLAY)
- Party mechanics nativas (max 9, join correcto, leader)
- Pattern BotSpike (test harness runtime reutilizable)
- Patrón SQL provisioning + rollback (REF-SQL-PROVISION)
- Cruce de features forks vs nativo (esta sección §3.5)

### SKILLS / CAPABILITIES (nuevas)
- Componer preset data-driven sin tocar core
- Ticker por script (ThreadPool) coexistente con AutoPlay sin conflicto
- Validación de bots reales en server-side (no fake, no client)
- Clean teardown + verificación de DB/World sin residuo
- Operar BotSpike con reloj/watchdog sin shutdown brusco

### RECIPE potenciales
- R-BS002: Bot presets data-driven (B1+C1+C2)
- R-BS003: Support bots — buff + heal + res (B3+B4)
- R-BS004: Skill priority engine (B2)
- R-BS005: Telemetría de bots (B6)
- R-BS006: Lifecycle autónomo + persistencia (D1+D2)

---

## 9. RESCATE DESDE INTELIGENCIA_ARTESANAL_L2J

### Material útil a rescatar tras research
- `knowledge/19_D0001_AUTO_PLAY_SPIKE.md` → base para REF-PARTY-AUTOPLAY (ya usado)
- `knowledge/20_PARTY_AUTOPLAY_SPIKE.md` → evidencia Party+AutoPlay+Assist (usado en R-BS001)
- `knowledge/HUNT_KIT_SLICE.md` → considerar para futura receta (hunting kit)
- `patches/0004-bot-squad/...` → administración de bot squad + comman directo (ya que AdminBotSquad.java viene de ahí)
- `.ia/REF-DATAPACK-SCRIPT-RELOAD.md` (si existe) → convenciones de script reload

### No rescatar
- Dependencias LLM/Ollama (SmartBot) como parte crítica
- Tabla smart_bots + DAO (E4)
- Custom AI (E1/E2)

---

## 10. RIESGOS Y GUARDRAILS (del research)

| Riesgo | Mitigación |
|--------|------------|
| Drift con upstream (baseline e2518ab108) | Re-anclar REFs antes de cada implementación contra baseline current |
| DB writes accidentales (online/offline flag) | Cumplir REF-PLAYER-CLIENTLESS teardown order |
| AutoPlay gates globales afectan jugadores reales | Mantener `EnableAutoPlay=True/AssistLeader=True` solo en entorno de pruebas |
| Skill/item IDs desalineados con datapack | Validar por SkillData/ItemData en runtime, no hardcodear |
| Conflictos con client en bots | Siempre verificar `getClient()==null` antes de operar |
| Shutdown con bots activos | Teardown siempre antes de apagado (patrón BotSpike cierra limpio sin shutdown brusco) |

---

## 11. RESUMEN / SIGUIENTE PASO

- **Descubrimientos**: Mobius ya cubre spawn/follow/assist/combat básico (R-BS001); los forks aportan: presets por rol (SmartBot), skill priority + healing (Roboto), buff/heal/support (L2Autobots), telemetría (HighFive).
- **Descartado / incompatible**: in-service FakePlayer subclassing, custom AI engine, DAO/tabla propia para bots, dependencia LLM.
- **Prioridad**: 1) B1 presets data-driven, 2) B3/B4 soporte (buff/heal/res), 3) B2 skill queue, 4) B6 telemetría, 5) D1 lifecycle reutilizable, 6) D2 persistencia (requiere DB).
- **Esperando**: aprobación GM para iniciar implementación (Act mode), respetando rule: Mobius source = authority; todo via scripts/config/REFs, sin tocar core.

---
*Fin del informe. Preparado tras revisión de: RECETARIO (R-BS001 + REFs), UPSTREAM Mobius (AutoPlay/AutoUse/Party/Player), Forks (L2Autobots, L2jRoboto, HighFive, l2-smartbot), IA knowledge (D0001/D0010 spikes).*