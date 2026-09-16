# FASE 4.15 — Especificación del MVP

## Objetivo mínimo
1 humano + 1 bot-Player persistente + Party + Follow + Assist + Attack + AutoUse +
Death + Revive + Persistencia. SIN: 18 bots, Command Channel, Community Board completo,
roles avanzados, raids.

## Precondición
El bot debe existir como personaje en la tabla `characters` (creado por cualquier
medio). El MVP NO crea personajes desde cero.

## Componente nuevo: BotManager
Replica el bloque de `OfflinePlayTable.restoreOfflinePlayers()`:
- `Player.load(charId)` (deja `_client == null`)
- `setOnlineStatus(true, false)` → `spawnMe(x,y,z)`
- `setOfflinePlay(true)` → `setOnlineStatus(true, true)`
- `restoreEffects()` → `setRunning()`
- `joinParty(human.getParty())`
- `AutoPlayTaskManager.getInstance().startAutoPlay(bot)`
- `AutoUseTaskManager.getInstance().startAutoUseTask(bot)`

## Lo que viene GRATIS por reutilización
Follow/assist/attack (AutoPlay), buffs/pociones/skills (AutoUse), XP/SP/loot (Party),
death/revive (lógica Player), persistencia (PlayerAutoSaveTaskManager).

## Prohibiciones
- NO crear GameClient. NO llamar startOfflinePlay().
- NO modificar GameClient.java / Player.java / OfflinePlayTable.java.
- NO clonar AutoPlay ni crear ThinkLoop nuevo.
- NO usar FakePlayer como base. NO crear clase BotPlayer paralela.

## Criterio de éxito
El bot se une a la party del humano, lo sigue, asiste en combate, usa buffs/pociones,
muere y puede revivir, y persiste su estado tras reinicio (recarga por charId).

## Pendiente previo a implementar
- Tener un charId de bot en DB.
- (Riesgo bajo) verificar visibilidad dinámica y call sites getClient() en runtime.
