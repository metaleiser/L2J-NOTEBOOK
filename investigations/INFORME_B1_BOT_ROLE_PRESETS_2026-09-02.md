# INFORME B1 — Data-Driven Bot Role Presets

**Fecha**: 2026-09-02
**Misión**: B1 — Perfiles de rol data-driven para bots Mobius
**Estado**: IMPLEMENTADO / PENDIENTE DE VALIDACIÓN RUNTIME

---

## 1. SOURCE FINDINGS

### 1.1 Arquitectura nativa relevante

| Componente | Ancla (fuente) | Función |
|---|---|---|
| `AutoPlaySettingsHolder` | `Player.java:974`, getter `15334` | targetMode, pickup, shortRange, respectfulHunting, autoPotionPercent |
| `AutoUseSettingsHolder` | `Player.java:975`, getter `15339` | autoActions, autoSkills, autoBuffs, autoSupplyItems, autoPotionItem |
| `AutoPlayTaskManager` | `taskmanagers/AutoPlayTaskManager.java` | Motor PvE 700ms, gate L80, targeting L190-245, follow/assist L264-282 |
| `AutoUseTaskManager` | `taskmanagers/AutoUseTaskManager.java` | Auto-use de skills/items, validación de skills L290-364 |
| `Player.load(int)` | `Player.java:1211-1213` | Carga de Player desde DB sin cliente |
| `setOfflinePlay(boolean)` | `Player.java:7960-7967` | Flag para gate de AutoPlay clientless |

### 1.2 Descubrimiento clave

El motor nativo ya distingue roles por configuración:
- `AutoPlayTaskManager.java:332-335`: `isMageCaster(player) = !autoActions.contains(2)`
  - **FIGHTER/melee**: `autoActions` contiene `2` → `setIntentionAttack` (L327)
  - **MAGE/caster**: `autoActions` SIN `2` → solo se acerca, no auto-ataca
- `AutoPlayTaskManager.java:285`: `shortRange=true → 600`, `false → 1400` (rango de adquisición)

**Conclusión**: Un "perfil de rol" en Mobius ES el conjunto de valores de los dos holders nativos. No requiere nueva arquitectura.

### 1.3 Estado de la DB (verificado en vivo)

| charId | nombre | classid | level | skills |
|---|---|---|---|---|
| 268483120 | BSBOT01 | 0 (Human Fighter) | 3 | 194, 1322 |
| 268483121 | BSBOT02 | 0 | 3 | 194, 1322 |
| 268483122 | BSBOT03 | 0 | 3 | 194, 1322, **1177 (Wind Strike)**, **1235 (Hydro Blast)** |

BSBOT03 provisionado con skills de mage (nivel 1, class_index=0, castRange 600).

---

## 2. MOBIUS-NATIVE EXTENSION PATH

**Ningún cambio de core**. Solo datapack + SQL idempotente:

| Capa | Implementación |
|---|---|
| Configuración | `data/scripts/custom/BotRoles/BotRoles.ini` (Properties file) |
| Aplicador | `BotRoleProfiles.java` (utilidad sin `main`, compilada en boot) |
| Ejecución | Holders nativos + `AutoPlayTaskManager.startAutoPlay` |
| Provisión | INSERT idempotente en `character_skills` |
| Test | `BotSpikeRoles.java` (Script con `main`, se ejecuta en boot) |

**Extensión points existentes reutilizados**:
- `data/scripts/custom/` (datapack scripts)
- `Player.getAutoPlaySettings()` / `getAutoUseSettings()`
- `AutoPlayTaskManager.getInstance().startAutoPlay(bot)`

---

## 3. IMPLEMENTACIÓN

### 3.1 Archivos creados

| Archivo | Tamaño | Descripción |
|---|---|---|
| `game/data/scripts/custom/BotRoles/BotRoleProfiles.java` | 9035 bytes | Parser INI + aplicador de perfiles a holders nativos |
| `game/data/scripts/custom/BotRoles/BotRoles.ini` | 1998 bytes | Definición de 3 perfiles (fighter, archer, mage) |
| `game/data/scripts/custom/BotSpikeRoles/BotSpikeRoles.java` | 20802 bytes | Harness de prueba (4 bots, party, observación) |

### 3.2 BotRoles.ini

```ini
fighter.autoAttack=true
fighter.targetMode=1
fighter.shortRange=true
fighter.respectfulHunting=false
fighter.pickup=false

archer.autoAttack=true
archer.targetMode=1
archer.shortRange=false
archer.respectfulHunting=false
archer.pickup=false

mage.autoAttack=false
mage.targetMode=1
mage.shortRange=false
mage.respectfulHunting=false
mage.pickup=false
mage.skills=1177,1235
```

### 3.3 BotRoleProfiles.java

- Clase utilidad sin estado
- `load()`: parsea INI una vez (lazy, thread-safe)
- `apply(Player, String)`: mapea campos a holders nativos
- Validación: `getKnownSkill(skillId)` antes de añadir a `autoSkills`
- Bot sin rol explícito → no se toca (default seguro)

### 3.4 BotSpikeRoles.java

- `extends Script`, `public static void main(String[])`
- Topología: party 4 miembros (líder + fighter + archer + mage)
- Fases: setup → observación (poll 1s × 75s) → teardown
- Veredictos: V0-V6 (carga, holders, spawn, combate, comportamiento por rol, teardown)
- Watchdog 300s, sin Shutdown

### 3.5 SQL idempotente

```sql
INSERT INTO l2jmobiush5.character_skills (charId, skill_id, skill_level, class_index)
SELECT 268483122, s.skill_id, s.skill_level, 0 FROM (
  SELECT 1177 AS skill_id, 1 AS skill_level UNION SELECT 1235, 1
) s
WHERE NOT EXISTS (SELECT 1 FROM l2jmobiush5.character_skills cs
                  WHERE cs.charId=268483122 AND cs.skill_id=s.skill_id AND cs.class_index=0);
```

Rollback: `DELETE FROM character_skills WHERE charId=268483122 AND skill_id IN (1177,1235);`

---

## 4. TEST DESIGN

### 4.1 Harness: BotSpikeRoles.java

**Zona**: Talking Island (mobs 20432/20481/20544, melee low-level)
**Timeline**: +60s setup | observe 75s | teardown +77s | report +82s | watchdog +300s

### 4.2 Veredictos

| ID | Criterio | Bloqueante? |
|---|---|---|
| V0 | 3 perfiles cargados y distintos | Sí |
| V1 | Holders de cada bot == perfil aplicado | Sí |
| V2 | 4 clientless, spawn, party=4, autoplay activo | Sí |
| V3 | Combate real (mob HP decrece + atacantes) | Sí |
| V4a | FIGHTER auto-ataca (melee hits) | Sí |
| V4b | MAGE no ataca melee (solo castea) | Sí |
| V4c | ARCHER adquisición a larga distancia | No (blando) |
| V5 | Party estable durante observación | Sí |
| V6 | Teardown limpio (World.getPlayer==null) | Sí |

---

## 5. RUNTIME RESULT

**PENDIENTE DE VALIDACIÓN**.

Estado actual:
- ✅ Archivos implementados en datapack
- ✅ SQL provisioning ejecutado (BSBOT03 tiene skills 1177+1235)
- ⏳ GameServer NO está corriendo
- ⏳ Harness NO ejecutado aún
- ⏳ Veredictos NO verificados

**Próximo paso requerido**: arrancar el GameServer (REF-BOOT) y observar `[BotSpikeRoles]` en `game/log/java0.log`.

---

## 6. LIFECYCLE / RESTART REQUIREMENT

| Cambio | Requiere | Evidencia |
|---|---|---|
| Datapack scripts nuevos | **Full GS restart** | REF-BOOT: scripts se compilan y ejecutan `main` en boot |
| SQL provisioning | **No restart** | Solo mysqld, leído en `Player.restore` |
| AutoPlay.ini gates | **Ninguno** | Ya activos (EnableAutoPlay=True, AssistLeader=True) |

**Checkpoint GM**: arrancar GameServer → observar `[BotSpikeRoles]` en java0.log.
Cline NO inicia/apaga el servidor.

---

## 7. FAILURES AND FIXES

**Ningún fallo detectado aún** (implementación completa, runtime pendiente).

Clasificación predefinida para fallos futuros:
1. Malentendido de fuente → re-leer UPSTREAM, corregir plan
2. Supuesto de prior-art incompatible → prohibido, revertir a nativo
3. Defecto de implementation → corregir dentro del alcance
4. Problema de lifecycle → guards `_running`, verificar `setOfflinePlay(true)`
5. Problema de configuración → registrar parcial, no maquillar
6. Contaminación del harness → guardias de idempotencia

---

## 8. RECIPE DECISIÓN

### Estado actual: **KNOWLEDGE/SKILL ONLY** (pendiente de validación)

### Razón:
- La capacidad está **implementada** pero **NO runtime-validated**
- No se puede crear receta sin evidencia de que la adaptación Mobius funciona
- El harness está listo pero no ejecutado

### Criterio para upgrade a RECIPE:
Tras PASS runtime limpio de BotSpikeRoles → crear **R-BS002 — "Perfiles de rol por config (FIGHTER/ARCHER/MAGE) sobre holders nativos"**

### Composición futura de R-BS002:
- REF-PLAYER-CLIENTLESS (lifecycle)
- REF-PARTY-AUTOPLAY (party + assist)
- REF-SQL-PROVISION (provisión de skills)
- BotRoles.ini (configuración data-driven)
- BotRoleProfiles.java (aplicador)

---

## 9. LEGACY KNOWLEDGE RESCUE DECISION

### Material revisado de INTELIGENCIA_ARTESANAL_L2J:

| Archivo | Clasificación | Razón |
|---|---|---|
| `knowledge/14_PVE_BOTS_LIFECYCLE_AI_ARCHITECTURE.md` | **REFERENCE ONLY** | Contiene análisis de lifecycle + AI architecture que informó el diseño de BotRoleProfiles, pero los REF-* ya consolidaron ese conocimiento |
| `knowledge/15_PVE_BOTS_C2_C3_VALIDATION_EVIDENCE.md` | **REFERENCE ONLY** | Evidencia de validación de spikes previos (C2/C3) útil como referencia de metodología BotSpike |
| `knowledge/19_D0001_AUTO_PLAY_SPIKE.md` | **REFERENCE ONLY** | Spike D-0001 previo, ya sintetizado en REF-PLAYER-CLIENTLESS |
| `knowledge/20_PARTY_AUTOPLAY_SPIKE.md` | **REFERENCE ONLY** | Spike PartyAutoPlay, ya sintetizado en REF-PARTY-AUTOPLAY |
| `.ia/CLAIMS.md` | **REFERENCE ONLY** | Claims históricos, ya verificados en REF-* |
| `.ia/REGISTRY_CAPABILITY.md` | **REFERENCE ONLY** | Registro de capacidades previas, útil para contexto |
| `patches/0004-bot-squad/PATCH.md` | **DISCARD** | Parche de exp00, reemplazado por R-BS001 (AdminBotSquad) |

### Decisión final: **REFERENCE ONLY**

**Nada necesita ser rescatado activamente**. Los REF-* del RECETARIO ya consolidan el conocimiento relevante de INTELIGENCIA_ARTESANAL_L2J. Los archivos de IA pueden permanecer como registro histórico pero no requieren integración en el proyecto actual.

---

## 10. NEXT POSSIBLE IMPROVEMENTS

Tras validación exitosa de B1:

| Prioridad | Mejora | Dependencia |
|---|---|---|
| 1 | **R-BS002** (recipe de perfiles de rol) | PASS runtime de BotSpikeRoles |
| 2 | **B3** — Soporte/healing al party | B1 validado + autoBuffs/autoSkills extendidos |
| 3 | **B2** — Skill priority queue | B1 validado + necesidad detectada |
| 4 | **B6** — Bot analytics/reporting | B1 validado |
| 5 | **//bs <rol>`** en AdminBotSquad | R-BS002 creada |

---

## FINAL MISSION STATUS

| Checkpoint | Estado |
|---|---|
| Implementación completa | ✅ 3 archivos datapack + SQL |
| Runtime validation | ⏳ Pendiente (GS apagado) |
| INTELIGENCIA_ARTESANAL_L2J review | ✅ Reference only (nada rescatable) |
| Recipe decision | ⏳ KNOWLEDGE/SKILL ONLY (pendiente validación) |
| Informe guardado | ✅ Este archivo |

**SIGUIENTE PASO**: GM arranca GameServer (REF-BOOT) y observa `[BotSpikeRoles]` en `game/log/java0.log` para validación runtime. Cline NO inicia/apaga el servidor.

---

**Fin del informe B1**
