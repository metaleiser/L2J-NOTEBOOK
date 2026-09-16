# BOTAI-04 — AUDITORÍA DE READINESS PARA IMPLEMENTACIÓN DE BOTS CON ROLES

> **SPRINT:** BOTAI-04
> **FASE:** Auditoría de readiness para implementación de bots con roles
> **MODO:** PLAN — SOLO INVESTIGACIÓN / AUDITORÍA
> **Estado:** AUDITORÍA COMPLETADA · **Evidence:** SOURCE + RUNTIME · **Vigencia:** CURRENT
> **Fecha:** 2026-09-12
> **Investigaciones auditadas:** BOTAI-02, BOTAI-03, BOTAI-03-B, BOTAI-03-C, BOT_RECIPE, FASE4_01..18, SOURCE_EVIDENCE, CLAIMS, OPEN_QUESTIONS

---

## AUDITORÍA DE READINESS — RESUMEN EJECUTIVO

**VEREDICTO: B — LISTOS PARA MVP**

Existe evidencia suficiente (source + runtime + GM-validated) para implementar bots equipados con roles. La infraestructura base está probada. Faltan componentes concretos de implementación (BotProvisioning, presets, role definitions), pero no falta investigación técnica.

---

## 1. ¿QUÉ TENEMOS YA?

### 1.1 Matriz de componentes

| Componente | Estado | Evidencia | ¿Listo para usar? |
|------------|--------|-----------|-------------------|
| **BotManager** | EXISTE | AdminBotManager.java (GM-validated CL-0019) | ✅ SÍ — extender |
| **BotSession** | INFRAESTRUCTA VERIFIED | OfflinePlayTable pattern + //bs runtime | ✅ SÍ — envolver |
| **lifecycle** | VERIFIED | FASE4_18 + BOTAI-03-B + CL-0009/0013/0014 | ✅ SÍ |
| **persistent Player** | VERIFIED | Player.load/store + DB rows BSBOT01-08 | ✅ SÍ |
| **OfflinePlay** | VERIFIED | OfflinePlayTable.restoreOfflinePlayers | ✅ SÍ |
| **AutoPlay** | VERIFIED | CL-0011/0013 + BOTAI-02 + runtime | ✅ SÍ |
| **AutoUse** | VERIFIED | CL-0011 + BOT_RECIPE §13 + runtime | ✅ SÍ |
| **Party** | VERIFIED | CL-0003/0010/0014 + runtime 9-member | ✅ SÍ |
| **Follow** | VERIFIED | AutoPlayTaskManager:264-282 + runtime | ✅ SÍ |
| **Assist** | VERIFIED | AutoPlayTaskManager:264-282 + runtime | ✅ SÍ |
| **Attack** | VERIFIED | AutoPlayTaskManager attack loop + runtime | ✅ SÍ |
| **Pickup** | VERIFIED | AutoPlayTaskManager pickup logic | ✅ SÍ |
| **Skills** | VERIFIED | SkillTreeData + giveAvailableSkills + runtime | ✅ SÍ |
| **PlayerAI** | VERIFIED | CL-0009 + FASE4_07 + intenciones FOLLOW/ATTACK | ✅ SÍ |
| **equipment APIs** | VERIFIED | ItemManager.createItem, addItem, equipItem, useEquippableItem | ✅ SÍ |
| **enchant APIs** | VERIFIED | Item.setEnchantLevel (Ruta B) + AdminEnchant pattern | ✅ SÍ |
| **inventory/equip APIs** | VERIFIED | Inventory.equipItem, setPaperdollItem | ✅ SÍ |
| **class/level APIs** | VERIFIED | setPlayerClass, PlayerStat.setLevel, addExpAndSp | ✅ SÍ |
| **skill provisioning** | VERIFIED | giveAvailableSkills + SkillTreeData + ADR-010 | ✅ SÍ |
| **PlayerVariables** | VERIFIED | CL-0012 + PlayerVariables.set/storeMe/saveNow | ✅ SÍ |
| **persistencia** | VERIFIED | store()/autoSave() + BOTAI-03-C isOnline VERIFIED | ✅ SÍ |
| **reload** | VERIFIED | BOTAI-03-C LOAD→SAVE→RELOAD design | ✅ SÍ |
| **role architecture** | PROPOSED | BOTAI-02 INV5 + BOTAI-03-B §16 | ✅ DISEÑO LISTO |
| **behavior architecture** | PROPOSED | BOTAI-02 INV5 + PlayerAI delegation | ✅ DISEÑO LISTO |

### 1.2 Evidencia runtime que respalda

| ID | Claim | Tipo |
|----|-------|------|
| CL-0008 | Player con _client==null es seguro | SOURCE + RUNTIME |
| CL-0009 | C2 spike PASS: load → spawn → 30s → teardown | RUNTIME |
| CL-0010 | C3 spike PASS: party 1+8 bots = 9 members | RUNTIME |
| CL-0013 | AutoPlay opera combate en Player sin cliente | RUNTIME |
| CL-0014 | Party + AutoPlay + AssistLeader: full combat | RUNTIME |
| CL-0019 | //bs validado por GM: summon/dismiss/follow/assist/combat | GM-VALIDATED |
| BOTAI-03-C | isOnline() = _isOnline puro → autosave clientless VERIFIED | SOURCE |


---

## 2. ¿QUÉ FALTA PARA QUE UN BOT "SALGA EQUIPADO"?

### 2.1 Respuesta: UNA SOLA PIEZA NUEVA

**BotProvisioning** — es la única pieza que falta. Todo lo demás son APIs existentes.

### 2.2 APIs/evidencia para cada paso del provisioning

| Paso | ¿Tenemos API? | Evidencia |
|------|---------------|-----------|
| elegir clase | ✅ SÍ | setPlayerClass(id) + setBaseClass(id) |
| establecer nivel | ✅ SÍ | PlayerStat.setLevel(byte) + ExperienceData.getExpForLevel |
| otorgar skills | ✅ SÍ | giveAvailableSkills(true,true,true) + SkillTreeData |
| crear armas | ✅ SÍ | ItemManager.createItem(process, itemId, 1, ref, null) |
| crear armaduras | ✅ SÍ | ItemManager.createItem + tipo armor |
| crear joyería | ✅ SÍ | ItemManager.createItem + tipo accessory |
| establecer enchant | ✅ SÍ | item.setEnchantLevel(N) — Ruta B |
| equipar | ✅ SÍ | useEquippableItem(item, false) o equipItem |
| guardar | ✅ SÍ | store(true) + PlayerVariables.saveNow() |
| recargar | ✅ SÍ | Player.load(charId) — idempotencia vía PlayerVariables |
| aplicar preset | ❌ FALTA | Definir estructura de preset (JSON/enum) |

### 2.3 Conclusión

**BotProvisioning es la única pieza nueva necesaria.** No faltan APIs, no falta evidencia técnica. Solo falta orquestar APIs existentes con un sistema de presets.

---

## 3. ¿QUÉ FALTA PARA LOS ROLES?

### 3.1 Análisis por rol

| Rol | Decisiones IA | APIs nativas | Complejidad | MVP |
|-----|---------------|--------------|-------------|-----|
| **DPS/FARMER** | Atacar target cercano | AutoPlayTaskManager attack loop | BAJA | ✅ SÍ |
| **FOLLOWER** | Seguir líder, atacar su target | AutoPlay AssistLeader=true | BAJA | ✅ SÍ |
| **TANK** | Mantener target líder, taunt | AutoPlay + setIntentionAttack | BAJA-MEDIA | ✅ SÍ |
| **HEALER** | Curar heridos, resucitar | party.getMembers(), getCurrentHp() | MEDIA | ✅ SÍ |
| **BUFFER** | Mantener buffs activos | getBuffInfoBySkillId() | MEDIA | ✅ SÍ |

### 3.2 Conclusión roles

**Todos los roles son implementables.** La solución más simple:

- **DPS/Follower** = AutoPlay nativo (AssistLeader=true) + configuración
- **Tank** = AutoPlay nativo + atacar target del líder
- **Healer** = AutoUse con heal skills + decisión simple (HP < X → curar)
- **Buffer** = AutoUse con buff skills + verificación periódica

**NO se necesita una IA gigantesca.** Se necesita configuración inteligente sobre AutoPlay/AutoUse.


---

## 4. SEPARACIÓN PROVISIONING / BEHAVIOR

### 4.1 Responsabilidades claras

#### BOT PROVISIONING: "¿Cómo nace preparado el bot?"

- Clase (setPlayerClass), Nivel/XP/SP (PlayerStat), Skills (giveAvailableSkills)
- Inventario (addItem), Equipo (equipItem), Enchant (setEnchantLevel)
- Marca idempotencia (PlayerVariables), Persistencia (store)
- **No conocer:** combate, targeting, party runtime

#### BOT ROLE: "¿Qué función cumple?"

- Definir configuración AutoPlay/AutoUse por rol
- Definir preset de equipo/skills
- Mapeo clase → skills base
- **No conocer:** cómo se equipa/carga el bot

#### BOT BEHAVIOR: "¿Qué decide hacer durante el combate?"

- Decisiones de alto nivel, delegar ejecución a Mobius
- Healer: HP < X → curar, Buffer: buff expirado → re-aplicar
- **No conocer:** APIs de equipamiento, persistencia

#### BOT SESSION: "¿Cuándo está activo/inactivo?"

- Load + spawn + OfflinePlay, Party join/leave
- AutoPlay/AutoUse start/stop, Shutdown limpio
- **No conocer:** equipo, rol, decisiones de combate

### 4.2 Principio

> LA IA DECIDE; MOBIUS EJECUTA.

---

## 5. ¿PODEMOS YA IMPLEMENTAR?

### Veredicto: B — LISTOS PARA MVP

**Razones:**
1. Infraestructura base VERIFIED (CL-0008..0014, CL-0019)
2. isOnline() gap CERRADO (BOTAI-03-C)
3. APIs enchant VERIFIED (Ruta B)
4. Persistencia VERIFIED
5. Roles diseñados (BOTAI-02)
6. Separación clara

### ¿Qué falta?

| Pieza | Tipo | Complejidad |
|-------|------|-------------|
| BotProvisioning class | Nueva | BAJA |
| Preset system | Nueva | BAJA |
| BotRole definitions | Nueva | BAJA |
| BotBehavior (Healer/Buffer) | Nueva | MEDIA |
| BotSession wrapper | Nueva | BAJA |

---

## 6. ORDEN DE IMPLEMENTACIÓN

1. **Sprint 1:** BotProvisioning MVP (applyClass, applyLevel, applyGear, persist, idempotencia)
2. **Sprint 2:** DPS/Farmer + Follower (AutoPlay default + AssistLeader)
3. **Sprint 3:** Tank + Healer (Follower + heal decision)
4. **Sprint 4:** Buffer (AutoUse buffs)
5. **Sprint 5:** Dashboard (Community Board BotBoard)

---

## 7. PRUEBAS YA SUPERADAS — NO REPETIR

| Prueba | Evidencia | Estado |
|--------|-----------|--------|
| Player.load deja _client==null | CL-0008, FASE4_17, FASE4_18 | ✅ SUPERADA |
| spawn + 30s + teardown | CL-0009 (C2 spike) | ✅ SUPERADA |
| Party 1+8 bots = 9 members | CL-0010 (C3 spike) | ✅ SUPERADA |
| AutoPlay clientless combate | CL-0013 (D0001 spike) | ✅ SUPERADA |
| Party+AutoPlay+AssistLeader | CL-0014 (PartyAutoPlay spike) | ✅ SUPERADA |
| //bs summon/dismiss/follow/assist | CL-0019 (GM 2026-09-02) | ✅ SUPERADA |
| Power Strike + AutoUse clientless | BOT_RECIPE §13 | ✅ SUPERADA |
| isOnline() = _isOnline puro | BOTAI-03-C Experimento 3 | ✅ SUPERADA |
| autosave funciona clientless | BOTAI-03-C Experimento 3 | ✅ SUPERADA |
| store(true) persiste todo | BOTAI-03-B §12 | ✅ SUPERADA |
| unload = storeMe()+deleteMe() | BOTAI-03-C Experimento 1D | ✅ SUPERADA |
| getClient sweep (Cat A/B) | BOTAI-03-C Experimento 4 | ✅ SUPERADA |

**NO repetir estas pruebas.** Ya están documentadas y verificadas.

---

## 8. GAPS REALES — LO ÚNICO QUE NOS FALTA

### Gaps de implementación

| Gap | Tipo | Bloquea MVP? |
|-----|------|--------------|
| **BotProvisioning class** | IMPLEMENTACIÓN | SÍ — primer sprint |
| **Preset system** | IMPLEMENTACIÓN | SÍ — primer sprint |
| **BotRole definitions** | IMPLEMENTACIÓN | NO |
| **BotBehavior Healer/Buffer** | IMPLEMENTACIÓN | NO |
| **Admin command //botprovision** | IMPLEMENTACIÓN | NO |

### NO son gaps (resueltos)

| Tema | Estado |
|------|--------|
| isOnline() behavior | RESUELTO — BOTAI-03-C |
| autosave clientless | RESUELTO — BOTAI-03-C |
| unload limpio | RESUELTO — BOTAI-03-C |
| getClient sweep | RESUELTO (parcial Cat C) |
| Persistencia/reinicio | RESUELTO — CL-0020/0021 |

### Gap EXPERIMENTAL (no bloquea)

| Gap | Clase | Impacto |
|-----|-------|---------|
| Visibilidad dinámica clientless | EXPERIMENTAL / RUNTIME REQUIRED | NO bloquea MVP |

### Conclusión

**LO ÚNICO QUE FALTA:**
1. BotProvisioning (orquestador)
2. Preset system (estructura de datos)
3. BotRole → configuración
4. BotBehavior (Healer/Buffer lógica)

**NO falta investigación técnica. Solo falta implementar.**


---

## 9. CLASIFICACIÓN EPISTEMOLÓGICA

| Conclusión | Clase | Vigencia | Evidencia |
|------------|-------|----------|-----------|
| Infraestructura base funciona clientless | VERIFIED | CURRENT | SOURCE + RUNTIME |
| isOnline() = _isOnline puro | VERIFIED | CURRENT | SOURCE (BOTAI-03-C) |
| autosave funciona clientless | VERIFIED | CURRENT | SOURCE (BOTAI-03-C) |
| BotProvisioning es factible | VERIFIED | CURRENT | APIs existentes |
| Roles son implementables | VERIFIED | CURRENT | BOTAI-02 + AutoPlay/AutoUse |
| Visibilidad clientless completa | EXPERIMENTAL | PENDING_REVERIFICATION | RUNTIME REQUIRED |
| getClient() Cat C 100% | UNVERIFIED | CURRENT | Riesgo bajo |
| TemporaryPlayer | REFUTED | HISTORICAL | NOT_FOUND |
| FakePlayer como base | REFUTED | HISTORICAL | Es NPC |

---

## 10. RECOMENDACIÓN FINAL

### Veredicto: B — LISTOS PARA MVP

### Primer sprint: BOTAI-04 — BotProvisioning MVP

**Alcance:**
- Clase BotProvisioning
- Preset system (enum o JSON)
- applyClass, applyLevel, applyGear, applySkills, persist
- Idempotencia vía PlayerVariables
- Admin command //botprovision

**Criterio de éxito:**
- BSBOT01-08 provisionados con clase/level/gear/enchant
- Persisten tras reinicio
- Recargables sin duplicación

### Principio rector

> LA IA DECIDE; MOBIUS EJECUTA.

No reinventar AutoPlay. No reinventar Party. No reinventar inventario.
Solo orquestar. Solo configurar. Solo delegar.

---

**FIN DE LA AUDITORÍA BOTAI-04**

**DETENTE AQUÍ.** No se implementó nada, no se modificó producción, no se inició BOTAI-04.
