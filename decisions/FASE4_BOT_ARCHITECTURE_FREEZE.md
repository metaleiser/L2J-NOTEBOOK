# DECISIÓN — Congelamiento de arquitectura de bots (FASE 4.18)

## Estado: APROBADA — VERIFIED contra source. Fecha: 2026-09-10

## Arquitectura congelada
```
Human (cliente real)
   └── Party
        ├── Bot1  Player  _client==null  OfflinePlay=true  AutoPlay  AutoUse
        ├── Bot2  Player  _client==null  OfflinePlay=true  AutoPlay  AutoUse
        └── Bot3  ...
   (después) └── CommandChannel (humano líder, parties de bots)
   (después) └── Community Board (BotBoard : IParseBoardHandler)
```

## Reutilizar (NO clonar, NO modificar)
Player, OfflinePlay (patrón restoreOfflinePlayers), AutoPlayTaskManager,
AutoUseTaskManager, Party, CommandChannel, PlayerAI, GeoEngine/Intentions.

## Crear nuevo
BotManager, RoleStrategy, BotBoard, comandos Hold/Regroup/Retreat, capa raid por-boss.

## NO TOCAR (regla de oro)
GameClient.java, Player.java, OfflinePlayTable.java, Party.java, CommandChannel.java,
PlayerAI.java, AttackableAI.java, AutoPlayTaskManager.java, AutoUseTaskManager.java,
CommunityBoardHandler.java. Solo modificar con evidencia concreta de que es indispensable.

## Prohibiciones explícitas
- NO GameClient artificial/detached. NO startOfflinePlay() para bots.
- NO TemporaryPlayer (no existe). NO FakePlayer como base. NO ThinkLoop nuevo. NO forks.

## Riesgos abiertos (no bloquean MVP)
- Visibilidad dinámica (UNVERIFIED runtime).
- getClient() server-side sobre bot (UNVERIFIED, riesgo bajo).
- Requisitos entry por-boss (UNVERIFIED, scripts).
- Escalabilidad >18 bots (broadcast/Geo).

## Instrucción cerrada para Cline
Crear un `BotManager` que replique `OfflinePlayTable.restoreOfflinePlayers()`:
Player.load(charId) → setOnlineStatus(true,false) → spawnMe → setOfflinePlay(true) →
setOnlineStatus(true,true) → restoreEffects/setRunning → joinParty(human.getParty()) →
startAutoPlay + startAutoUseTask. Sin crear GameClient, sin llamar startOfflinePlay(),
sin modificar el core. Requiere un charId de bot existente en la DB.
