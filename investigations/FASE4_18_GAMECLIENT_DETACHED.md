# FASE 4.18 — GameClient Detached (CORRECCIÓN CRÍTICA)

> **Estado:** REFUTED · **Vigencia:** HISTORICAL · **Valor:** Documentación de decisión rechazada

## 1. Source inspected
`network/GameClient.java`, `Player.java` (_client, setClient, startOfflinePlay,
isOnlineInt, isInOfflineMode), `OfflinePlayTable.restoreOfflinePlayers`.

## 2. GameClient constructors
ÚNICO constructor: `GameClient(Connection<GameClient> connection)`, que hace
`super(connection)` y `_ip = connection.getRemoteAddress()`. Requiere conexión real.
NO existe constructor sin socket ni factory offline. `_isDetached` es un boolean
interno (`setDetached`), NO un modo de construcción sin conexión.

## 3. Player._client lifecycle
- `Player.load` NO asigna `_client` → queda null.
- `setClient(GameClient)` existe pero NADIE lo llama en el flujo offline.
- `setOnlineStatus` no toca `_client`.

## 4. OfflinePlay real call chain
- `startOfflinePlay()`: opera sobre un player CON cliente real; hace
  `_client.setDetached(true)` SIN null-check.
- `restoreOfflinePlayers()`: al boot, `Player.load` (sin cliente) + setOnlineStatus +
  spawnMe + setOfflinePlay(true) + AutoPlay/AutoUse. NUNCA crea/asigna GameClient.

## 5. How to create detached client
NO se crea. La ruta correcta es null client (patrón restoreOfflinePlayers).

## 6. Minimal code flow (modelo; Cline debe validar firmas)
```
Player bot = Player.load(charId);
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
NO crear GameClient. NO llamar startOfflinePlay().

## 7. Required source modifications
NINGUNA para el MVP.

## 8. Risks
- NPE si se usara startOfflinePlay() sobre bot (setDetached sin null-check).
- getClient() server-side sobre bot: riesgo bajo (precedente offline), UNVERIFIED.
- Visibilidad dinámica: UNVERIFIED runtime.

## 9. FINAL VERDICT
B — REUSE EXISTING OFFLINE MECHANISM
