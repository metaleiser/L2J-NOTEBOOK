# FASE 4.02 — Player sin GameClient

## Estado: VERIFIED · **Evidence:** SOURCE · **Vigencia:** CURRENT

## Hechos del source
- `getClient()` devuelve `_client` (puede ser null).
- `setClient(GameClient)` asigna `_client` y copia IP si no es null.
- `sendPacket(ServerPacket)` es null-safe: `if (_client != null) _client.sendPacket(...)`.
  → Un bot con _client==null simplemente no envía packets a sí mismo (no NPE).
- `isOnlineInt()`: devuelve 1/2 SOLO si `_isOnline && _client != null`; si no, 0.
- `isInOfflineMode()`: `(_client == null) || _client.isDetached()`.
- `broadcastCharInfo()`: early-return si `isOnlineInt()==0`.
  → Con _client==null, el bot NO emite su propio CharInfo.
- `broadcastPacket(...)`: NO chequea flag online; envía a observadores visibles.
  → Movimiento/ataque/skill/HP del bot SÍ se propagan a los humanos cercanos.

## Consecuencia
La lógica de juego (combate, stats, inventario, skills) funciona con _client==null.
Lo único condicionado es lo que el propio bot "emite" por broadcastCharInfo.

## Confianza: ALTA (código); visibilidad dinámica = UNVERIFIED en runtime
