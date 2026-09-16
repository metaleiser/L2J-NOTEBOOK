# ROUTING — Bots / Party / Clientless (HIPOTESIS TemporaryPlayer, NO decision final)

> Objetivo GM: PLAYER REAL + BOT TANK + BOT HEALER + BOT BUFFER + BOT DPS + BOT DPS en Party PvE (luego instances/raids/bosses/epics).
> TemporaryPlayer = HIPOTESIS. Primero NATIVO Mobius.

## 1. AutoPlay / AI nativo (investigar PRIMERO — FASE 4.1)

| Pregunta | Donde mirar (UPSTREAM 43ac8878f5) | Evidencia previa |
|---|---|---|
| Que sistema controla AutoPlay | `taskmanagers/AutoPlayTaskManager.java` (lineas 115-327 en e2518ab) | CL-0011, CL-0013, CL-0014 |
| Que clases intervienen | AutoPlay holders + config `AutoPlay.ini` (EnableAutoPlay/AssistLeader) | patches/0004 sec.4 |
| Que necesita un Player | targetMode/pickup/shortRange/respectfulHunting/autoActions (0004 sec.4) | exp00 B_bots |
| Que depende de GameClient | AUDIT PENDIENTE en 43ac8878f5 (FASE 4) | PV3 |
| Targeting/movimiento/skills/follow/assist/attack/heal/buff/death | AutoPlayTaskManager:264-282 + PlayerAI 341-413 | D-0001/PartyAutoPlay |

## 2. Player sin cliente (FASE 4.2)

| Pregunta | Donde mirar | Evidencia previa |
|---|---|---|
| Crear sin DB | ctor privado Player.java:895-917 (NO Player.create 1020-1042) | ADR-001/002, CL-0001 |
| sendPacket null-safe | Player.java:4429-4435 | CL-0002, CL-0008 |
| getAccountName fallback | Player.java:1044-1047 | CONSTRAINTS |
| NO usar: storeMe/autoSave/deleteMe/load/restoreSkills | Player.java:7637/8627/11734/7211/8171 | CONSTRAINTS sec.1 |
| Runtime OK: spawnMe/stopAllTasks/abort/Creature.deleteMe | Player.java:15185, Creature.java:2805 | V8, V22 |
| Peligros: Item.updateDatabase 1481-1512, onDecay 527-540, Shutdown 560-565 | CONSTRAINTS H1-H9 | ADR-003/004/011/012 |
| Auditar en 43ac8878f5: inventory/variables/autosave/offline/AI/movement/target/party/death/teleport/instance/raid | FASE 4 checklists | FI1-FI8, T1-T13 |

## 3. Party (FASE 4.3)

| Pregunta | Donde mirar | Evidencia previa |
|---|---|---|
| member list | Party.java:88 CopyOnWriteArrayList<Player> | CL-0003 |
| joinParty vs addPartyMember | Player.java:6862 set _party; Party.java:280 NO | CL-0005 |
| XP/loot/death/revive/follow/assist/teleport/instances/raids/CommandChannel | Party.java:280-345,615-640; CommandChannel.java:40-57,48-57; Quest.java:1995,2067 | CL-0006/0007, ADR-006, CL-0024 |
| Bot no-looter (politica) | ADR-006 | H2 |

## 4. FakePlayer (FASE 4.4)

- Mobius FakePlayer = representacion NPC, NO Player (FakePlayerInfo.java:38). CL-0004. ADR-013.
- Veredicto propuesto: DESCARTADO PARA PARTY PVE (mantener conocimiento, no borrar).
- Re-verificar en 43ac8878f5 antes de sellar.

## 5. TemporaryPlayer: evaluar DESPUES (FASE 4.5)

- Solo tras 1-4. Alternativas nativas primero: Player.load+OfflinePlay (0004), PlayerVariables (CL-0012), filas SQL (REF-SQL-PROVISION).
- Si se necesita: contrato 30 caps + 13 ADRs + MVP Archer como HIPOTESIS de partida.
- Experimento minimo por objetivo (ej. healer cura -> AI? target? skill? party? client? -> spike).

