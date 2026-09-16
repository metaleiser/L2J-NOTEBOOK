# FASE 4.17 — Estado de _client tras Player.load

## Estado: VERIFIED · **Evidence:** SOURCE · **Vigencia:** CURRENT

## SOURCE
`Player.load(int objectId)`, `Player.setClient/getClient`, `setOnlineStatus`,
`isOnlineInt`, `isInOfflineMode`; `OfflinePlayTable.restoreOfflinePlayers`.

## CALL CHAIN
Player.load(charId) → restaura inventario/skills/macros/shortcuts/henna/HP-MP →
termina en `setOnlineStatus(true, false)` + `PlayerAutoSaveTaskManager.add(player)`.
NO llama a `setClient(...)` en ningún punto.

## EXACT STATE (VERIFIED)
- Tras `Player.load`: `_client == null` (valor por defecto; nunca asignado).
- `setOnlineStatus(isOnline, updateInDb)`: solo cambia `_isOnline` (+ update DB opcional).
  NO toca `_client`, NO registra en managers extra.
- Resultado: `isOnlineInt()==0` (porque exige `_client != null`);
  `isInOfflineMode()==true` (porque `_client == null`).

## CONCLUSION
Se puede crear un bot replicando el patrón de `restoreOfflinePlayers` SIN inventar un
nuevo estado de Player: Player.load + setOnlineStatus + spawnMe + setOfflinePlay(true)
+ AutoPlay/AutoUse. `_client` queda null por diseño.

## CONFIDENCE: ALTA (VERIFIED)
