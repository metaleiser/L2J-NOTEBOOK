# FASE 3 — Compatibilidad patches 0001-0004 vs 43ac8878f5 (AUDITORIA, sin aplicar)

> Metodo por patch: archivos -> clases/metodos/anclas/imports/APIs/lineas/dependencias -> arquitectura -> jar/datapack -> veredicto.
> Veredicto: COMPATIBLE | COMPATIBLE CON CAMBIOS | INCOMPATIBLE | DESCONOCIDO.

## 0001-prepmaster (PrepMaster NPC 900105 Aria, datapack-only, GM VALIDATED e2518ab)

- Archivos: `game/data/scripts/custom/PrepMaster/PrepMaster.java + 900105.htm + game/data/stats/npcs/custom/PrepMaster.xml + game/data/teleporters/others/900105.xml`.
- Anclas a re-verificar en 43ac8878f5: `ScriptEngine (compila data/scripts al boot)`, `NpcData (template Folk)`, `TeleporterData (type OTHER)`, `AccessLevels (isGM 70/100)`.
- APIs: Script extends + main() auto-registro; HTML bypass `Script PrepMaster`; teleport lists.
- Drift CL-0024: Quest/Q00103 SIN solape. Riesgo: BAJO.
- Veredicto PREVIO (pendiente verificacion file:line en 43ac8878f5): COMPATIBLE (probable). NO aplicar hasta check.

## 0002-gm-cb-dashboard (_bbsgm IParseBoardHandler, IMPLEMENTADO pendiente GM C0-C10)

- Archivos: `game/data/scripts/custom/GmDashboard/GmDashboard.java + 6 html CommunityBoard/Custom/gmdashboard/*.html`.
- Anclas: `CommunityBoardHandler.getHandler/registerHandler (startsWith)`, `IParseBoardHandler.onCommand/getCommandList`, `HtmCache.getHtm`, `separateAndSend/ShowBoard 16250`, `MasterHandler` registro, CB config gates.
- APIs: buffs via SkillData, teleports, items (adena/shots/custom), player/server info, spawn NPC.
- Drift: SIN solape conocido. Riesgo: BAJO-MEDIO (toca CB, verificar que CommunityBoardHandler/HtmCache no cambiaron).
- Veredicto PREVIO: COMPATIBLE (probable). NO aplicar hasta check + validacion GM C0-C10.

## 0003-fase31-spawn-persistente (900105.xml en Giran, depende 0001)

- Archivos: `game/data/spawns/Others/900105.xml` (x=83485 y=147998 z=-3407 heading=23509, respawn 60).
- Anclas: `SpawnData.load() parseDatapackDirectory data/spawns (linea 110 en e2518ab)`, `checkTemplate() 157-184 (Folk OK, no SiegeGuard/RaidBoss/FakePlayer)`.
- Drift: SIN solape. Riesgo: BAJO.
- Veredicto PREVIO: COMPATIBLE (probable). NO aplicar hasta check.

## 0004-bot-squad (//bs [1-8] / //bs off, COMPILE OK, pendiente deploy)

- Archivos: NUEVO `game/data/scripts/handlers/chat/commands/admin/AdminBotSquad.java` + EDIT `MasterHandler.java (AdminBotSpike->AdminBotSquad)` + EDIT `game/config/AdminCommands.xml (admin_bs 100)` + NUEVO `db/0004_bot_squad_provisioning.sql (BSBOT01..08)` + residuo a retirar (`bs.java`, `AdminBotSpike.java`, `BotSpikeHuman.ini True->False`) + gates `AutoPlay.ini True/True`.
- Anclas criticas: `AdminCommandHandler.java:39-163 (onCommand 83-146)`, `registerHandler 50-57`, `AdminData.hasAccess 152-189`, `ScriptEngine.java:55-209 + ScriptExecutor 84-216 (+:53 static final loader)`, `Player.load 1211/restore`, `setOnlineStatus/setOfflinePlay/spawnMe`, `AutoPlayTaskManager.startAutoPlay (+264-282 follow/assist)`, `Party(gm)+joinParty (Party.java:88, Player.java:6862)`, `PlayerAutoSaveTaskManager.remove`, teardown `leaveParty/setOfflinePlay(false)/stopAutoPlay/stopVitalityTask/decayMe`, `World`, `Quest.java:1995,2067 (drop party!)`.
- APIs/dependencias: JDK25 compila scripts; MySQL l2jmobiush5; AutoPlay.ini; BSBOT rows online=0; GM sin party previa; N 1..8; //bs off antes de shutdown.
- Drift CL-0024: Quest party-drop ahora elige miembro elegible ALEATORIO (no killer) — IMPACTO EN BOTS CON PARTY: loot/quest drops pueden ir a un bot. Verificar politica no-looter (ADR-006) + Quest.java en 43ac8878f5.
- Veredicto PREVIO: COMPATIBLE CON CAMBIOS (probable: re-anclar + recompilar vs nuevo GameServer.jar + revisar Quest/Party). NO aplicar hasta FASE 3 completa.

## Checklist FASE 3 (por patch)

`1. diff anclas e2518ab vs 43ac8878f5 (file:line) · 2. imports/APIs existen · 3. javac vs nuevo libs/GameServer.jar (solo 0004 tiene Java compilable; 0001/0002 scripts se compilan al boot) · 4. datapack paths existen · 5. config gates · 6. riesgos arquitectura · 7. veredicto final`.
