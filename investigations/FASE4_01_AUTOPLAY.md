# FASE 4.01 — AutoPlay / AutoUse

## Estado: VERIFIED · **Evidence:** SOURCE + RUNTIME · **Vigencia:** CURRENT

## Qué es
AutoPlay es un sistema autónomo YA presente en el source que opera sobre `Player`
(no sobre Npc). Gestiona selección de target, movimiento, ataque, asistencia a
party y pickup. AutoUse gestiona buffs, pociones y skills automáticas.

## Clases y evidencia
- `taskmanagers/AutoPlayTaskManager.java`
  - `Set<Set<Player>> POOLS`, `POOL_SIZE = 200`, `TASK_DELAY = 700` ms, `AUTO_ATTACK_ACTION = 2`.
  - `startAutoPlay(Player)` / `stopAutoPlay(Player)`.
  - Guard del loop: para cada player, si `!player.isOnline() || (player.isInOfflineMode() && !player.isOfflinePlay())`
    → `stopAutoPlay`. Es decir, un player con OfflinePlay=true SÍ sigue corriendo AutoPlay.
- `taskmanagers/AutoUseTaskManager.java` — `startAutoUseTask(Player)`.

## Prueba de que corre sin cliente
`OfflinePlayTable.restoreOfflinePlayers()` arranca AutoPlay/AutoUse sobre players
cargados desde DB sin asignar GameClient (ver FASE4_17_PLAYER_CLIENT_STATE).

## Veredicto
AutoPlay/AutoUse es LA BASE del motor de bots. NO clonar, NO crear un ThinkLoop nuevo.

## Confianza: ALTA
