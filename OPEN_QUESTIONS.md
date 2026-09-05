# OPEN QUESTIONS AND VERIFICATION GAPS — Temporary Player / Clientless Archer

> **Propósito:** Registrar todo hallazgo no resuelto, parcialmente verificado, o que requiere verificación adicional.
> **Estado:** Post-investigación source (plan mode)
> **Target:** Mobius CT 2.6 HighFive

---

## 1. VERIFIED FACTS (confirmados por source evidence)

| # | Hecho | Source | Confianza |
|---|-------|--------|-----------|
| V1 | Player ctor privado funciona sin GameClient | Player.java:895-917 | ALTA |
| V2 | create() persiste (NO usar) | Player.java:1020-1042 | ALTA |
| V3 | sendPacket tiene guard _client!=null | Player.java:4429-4435 | ALTA |
| V4 | addSkill(skill,false) es runtime-only | Player.java:7998-8025 | ALTA |
| V5 | removeSkill(skill,false,cancelEffect) runtime | Player.java:8034-8038 | ALTA |
| V6 | storeMe() persiste (NO usar) | Player.java:7637-7700 | ALTA |
| V7 | deleteMe() persiste (NO usar) | Player.java:11734-12147 | ALTA |
| V8 | Creature.deleteMe() es runtime | Creature.java:2805-2829 | ALTA |
| V9 | PlayerAI no referencia GameClient | PlayerAI.java:341-413 | ALTA |
| V10 | World.addObject runtime (no DB) | World.java:175-202 | ALTA |
| V11 | IdManager BitSet puro, sin DB | IdManager.java:108-177 | ALTA |
| V12 | Item.updateDatabase INSERT si !existsInDb | Item.java:1481-1512 | ALTA |
| V13 | _wear flag existe como no-persist | Item.java:1658,1691 | ALTA |
| V14 | Party sendPacket protegido | Party.java:280-345,615-640 | ALTA |
| V15 | SkillTreeData 100% en memoria | SkillTreeData.java:119-642 | ALTA |
| V16 | Shutdown itera World.getPlayers() | Shutdown.java:560-565 | ALTA |
| V17 | Bot no registrado en autosave | AutoSaveTM.java:86-89 | ALTA |
| V18 | onDecay persiste con DisconnectAfterDeath=true | Creature.java:527-540 | ALTA |
| V19 | HAWKEYE classId=9, human, parent ROGUE | PlayerClass.java:52 | ALTA |
| V20 | Bow=14, Arrows=17, Chest=23 (Light) | items XML:298,372,461 | ALTA |
| V21 | setLevel runtime | PlayerStat.java:506-523 | ALTA |
| V22 | stopAllTasks runtime seguro | Player.java:15185-15230 | ALTA |

---

## 2. PARTIALLY VERIFIED FACTS

| # | Hecho | Evidencia | Gap | Prioridad |
|---|-------|-----------|-----|-----------|
| PV1 | `rewardSkills()` seguro para bots | Player.java:2688 | No verificado si llama a storeSkills | MEDIA |
| PV2 | `PlayerAI.clientNotifyDead()` seguro | PlayerAI.java:335-339 | Cadena completa en Creature.doDie | BAJA |
| PV3 | AttackStanceTaskManager seguro clientless | search | Asumido, no verificado directamente | BAJA |
| PV4 | Items _temporary + count=0 → DB no-op | Item.java:1500-1503 | Confirmado por source lógica | ALTA |

---

## 3. HYPOTHESES (requieren verificación runtime)

| # | Hipótesis | Base | Riesgo |
|---|-----------|------|--------|
| H1 | Script listener itera World.getPlayers() y hace sendPacket | Script system usa mismo patrón | BAJO |
| H2 | Bot lootea accidental en Party | Party.java:618-640 | MEDIO — excluir por política |
| H3 | Skills pasivas se aplican automáticamente al addSkill | SkillData | BAJO |
| H4 | isOnline() retorna false sin setOnlineStatus | Player.java:7020 | MEDIO |
| H5 | Bot muerto podría llegar a onDecay y persistir | Creature.java:527-540 | MEDIO |
| H6 | Nombre del bot debe ser único | Name constraints | BAJO |

---

## 4. ITEMS REQUIRING FUTURE SOURCE INSPECTION

| # | Item | Razón | Prioridad |
|---|------|-------|-----------|
| FI1 | AttackStanceTaskManager — cómo detecta ataque | Para combat flag en despawn | MEDIA |
| FI2 | DecayTaskManager — cómo registra decay de player | Para cancelar decay del bot | ALTA |
| FI3 | Creature.doDie() — tasks/lifecycle al morir | Para decidir despawn inmediato | ALTA |
| FI4 | Item.changeCount() — si llama updateDatabase | Confirmado en search que sí | ALTA |
| FI5 | ItemContainer.destroyItem() — ruta persistencia | Para cleanup de items | ALTA |
| FI6 | Player.sendItemList(boolean) — client null | Llamado en reduceArrowCount | MEDIA |
| FI7 | Player.recalcStats(boolean) — ¿updateDatabase? | Si stats afectan DB | BAJA |
| FI8 | Player.setClassTemplate(int) — inicialización | Llamado en setPlayerClass | BAJA |

---

---

## 5. TESTS REQUERIDOS (runtime testing)

| # | Test | Qué verificar | Prioridad |
|---|------|---------------|-----------|
| T1 | Crear Player vía constructor sin client | No excepción | ALTA |
| T2 | sendPacket() con client null | No-op | ALTA |
| T3 | setLevel() → recálculo stats | maxHp/maxCp correctos | ALTA |
| T4 | Item _temporary → updateDatabase no-op | 0 INSERT items | ALTA |
| T5 | reduceArrowCount con flechas _temporary | Count baja, DB no cambia | ALTA |
| T6 | PlayerAI.setIntentionAttack(monster) | Bot se mueve y ataca | ALTA |
| T7 | Party.addPartyMember con bot | Owner ve ventana | ALTA |
| T8 | Creature.deleteMe() en bot | World.removeObject funciona | ALTA |
| T9 | IdManager.releaseId(botId) | ID reusable | ALTA |
| T10 | Summon/despawn × 10 sin fuga | No memory leak | MEDIA |
| T11 | Owner disconnect → bot despawned | No queda en World | ALTA |
| T12 | Shutdown → bot despawned | No storeAndDelete | ALTA |
| T13 | Bot muerte → decay cancelado | No persistencia post-mortem | ALTA |

## 6. UNKNOWN #17 (del reporte final)

**Origen:** "Scripts/quest listeners globales que iteren World.getPlayers() o reaccionen a eventos del bot"

**Estado actual:** No verificado directamente en source de scripts. Sin embargo:
- Todos los `sendPacket` conocidos pasan por `Player.sendPacket()` → guard presente
- EventDispatcher (`OnPlayerLogout`, etc.) solo se dispara en deleteMe/acciones específicas
- El bot nunca pasa por logout normal → eventos de logout no se disparan
- Eventos de ataque/muerte sí podrían disparar listeners de scripts

**Riesgo estimado:** BAJO
**Acción:** Verificar en Act Mode con logs de SQL y script callbacks durante combate PvE.

---

## 7. DECISIONES PENDIENTES (requieren resolución antes de Act Mode)

| # | Decisión | Opciones | Recomendación |
|---|----------|----------|---------------|
| D1 | Nombre del bot | `IA_Archer`, random | Prefijo IA_ + contador atómico |
| D2 | Account name del bot | "IA" fijo, o dinámico | "IA" fijo (Player.java _accountName) |
| D3 | Política si bot muere sin despawn | Auto-despawn, decay con flag | Auto-despawn (cancelar decay, cleanup directo) |
| D4 | Paquete de TemporaryPlayerManager | manager o temporary nuevo | `org.l2jmobius.gameserver.temporary` |
| D5 | Integration en Shutdown | Antes de saveData o en disconnectAll | Hook en disconnectAllCharacters (1 línea) |
| D6 | Integration en Disconnection | En storeAndDelete o GameClient | GameClient.onDisconnection (1 línea) |
---

## 8. SERVER LIFECYCLE (REF-BOOT / R-SRV001) — PREGUNTAS PENDIENTES

> Estas preguntas NO son hechos. Son validaciones PARKED/UNKNOWN que quedan fuera de lo verificado.
> **Separación de baseline:** el runtime verificado es L2J Mobius CT 2.6 HighFive @ `e2518ab108` (2026-09-03). La claim **CL-0024** se basa en evidencia de **upstream** `43ac8878f5` y NO está validada en el runtime `e2518ab108` (ver CLAIMS.md).

| # | Pregunta / gap | Evidencia actual | Estado | Prioridad |
|---|----------------|------------------|--------|-----------|
| UQ-1 | Validación real de `//shutdown N` (Method A) con cliente de juego + sesión GM; confirmar que ejecuta shutdown hooks y persiste personajes/traders/eventos correctamente | Start/Stop(Method C)/Restart verificados por runtime 2026-09-03; Method A NO probado (sin cliente) | PARKED/UNKNOWN | ALTA |
| UQ-2 | accessLevel requerido para el comando `//shutdown N` en este baseline (¿≥ 100?) | GM char de prueba tenía accesslevel 70; regla ≥ 100 no confirmada | PARKED/UNKNOWN | MEDIA |
| UQ-3 | Comportamiento de shutdown forzado (Method C) mientras existen bots (`//bs` squad) o jugadores conectados | DB limpia solo con 0 conexiones; no se forzó con bots/jugadores activos | PARKED/UNKNOWN | MEDIA |
| UQ-4 | Cierre por ventana: cerrar la ventana Java / WM_CLOSE NO detuvo el runtime en la prueba; determinar si existe algún cierre de ventana limpio | VERIFIED-RUNTIME 2026-09-03 (cierre por ventana ineficaz en el runtime probado) | VERIFICADO (comportamiento); sin alternativa limpia encontrada | BAJA |

Notas:
- Start / Stop / Restart fueron validados mediante el procedimiento disponible (lanzadores VBS y arranque directo `java -jar`, shutdown Method C). NO presentar `//shutdown N` como runtime-validated.
- Estos items PARKED también residen en `R-SRV001` §KNOWN LIMITS y en `investigations/INFORME_SERVER_LIFECYCLE_2026-09-03.md` §Pendientes; aquí se centraliza su seguimiento.