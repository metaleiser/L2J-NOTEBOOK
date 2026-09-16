# FASE 4.17 — Visibilidad sin cliente

> **Estado:** NOT_FOUND · **Vigencia:** PENDING_REVERIFICATION · **Nota:** No usar como autoridad de implementación hasta nueva verificación

## Estado: VERIFIED (mecanismo) / UNVERIFIED (runtime)

## Hechos
- Con `_client == null`, `isOnlineInt()==0` → `broadcastCharInfo()` hace early-return.
  El bot NO emite su propio CharInfo.
- `OfflinePlayTable.restoreOfflinePlayers` deja `// player.broadcastUserInfo();`
  COMENTADO deliberadamente → el equipo Mobius ya asume ese comportamiento y no lo
  considera bloqueante para offline players con null client.
- `broadcastPacket(...)` (movimiento/ataque/skill/HP) NO chequea el flag online:
  esos packets SÍ llegan a los humanos cercanos.

## Inferencia (PROBABLE)
La visibilidad INICIAL del bot la resuelve el knownlist del observador humano
(su cliente renderiza al bot al entrar en rango), igual que los offline shops
con null client, que son visibles.

## UNVERIFIED (runtime)
- Aparición del bot al acercarse el humano.
- Actualizaciones dinámicas (cambio de equipo/título vía broadcastCharInfo) NO se
  propagarán mientras `isOnlineInt()==0`.
- Barrido completo de call sites `getClient()` server-side.

## Impacto en MVP
NO bloquea el MVP: movimiento/combate/skill/HP van por broadcastPacket.
Si la visibilidad dinámica resultara necesaria, sería un ajuste MÍNIMO en
`isOnlineInt`/`broadcastCharInfo` (última opción, no para el MVP).

## Confianza: ALTA (código) / MEDIA (runtime)
