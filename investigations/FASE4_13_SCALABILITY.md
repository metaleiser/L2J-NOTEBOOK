# FASE 4.13 — Escalabilidad

## Estado: INFERENCIA basada en source (no benchmark)

## Datos del source
- AutoPlay: pools de `POOL_SIZE = 200`, `TASK_DELAY = 700` ms.
- Cada bot-Player: auto-save (PlayerAutoSaveTaskManager), efectos, tareas AI.
- `broadcastPacket` itera observadores visibles → coste escala con densidad.
- GeoEngine/pathfinding se invoca en el think de movimiento (maybeMoveToPawn).

## Cuellos de botella esperados (orden)
1. Broadcast de packets (movimiento/combate) con bots agrupados.
2. GeoEngine/pathfinding por bot en combate/movimiento.
3. Auto-save y tareas programadas por Player.
4. KnownList/targeting.

## Estimación cualitativa
- 6–18 bots: viable sin problemas esperables.
- 36–54 bots: vigilar broadcast/Geo en zonas densas.
- 100 bots: requiere pruebas y posible tuning; no confirmado.

## Confianza: MEDIA (sin benchmark real)
