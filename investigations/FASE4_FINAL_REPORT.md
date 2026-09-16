# FASE 4 — Informe Final

## Estado: VERIFIED · **Evidence:** SOURCE + RUNTIME · **Vigencia:** CURRENT

## Hallazgo decisivo
El modelo "Player clientless + OfflinePlay + AutoPlay/AutoUse + Party" YA EXISTE y
corre en producción vía `OfflinePlayTable.restoreOfflinePlayers()`. Los bots se
implementan replicando ese patrón dentro de un `BotManager` nuevo.

## Corrección FASE 4.17 → 4.18
FASE 4.17 propuso "GameClient detached". RECHAZADO: GameClient solo tiene el ctor
`GameClient(Connection)`, imposible sin socket, y `startOfflinePlay()` haría NPE con
null client. Arquitectura definitiva: `_client == null` (patrón restoreOfflinePlayers).

## Tabla de preguntas
| Pregunta | Respuesta | Confianza |
|---|---|---|
| ¿Player puede funcionar server-side? | Sí | Alta |
| ¿Necesita GameClient? | No | Alta |
| ¿Detached client? | No (RECHAZADO); null client | Alta |
| ¿Party acepta bots? | Sí, sin modificar | Alta |
| ¿Command Channel acepta bots? | Sí, sin modificar núcleo | Alta |
| ¿FakePlayer sirve? | No (es NPC) | Alta |
| ¿AutoPlay sirve? | Sí, es la base | Alta |
| ¿BotAI nuevo necesario? | Parcial: reuse AutoPlay + RoleStrategy | Alta |
| ¿Community Board viable? | Sí, vía IParseBoardHandler | Alta |
| ¿Raid Boss viable? | Simples sí; extender targeting | Media |
| ¿Epic Raid requiere lógica especial? | Sí, por-boss | Media |
| ¿18 bots viables? | Sí; cuello broadcast/Geo | Media |
| ¿Arquitectura recomendada? | Player persistente + null client + OfflinePlay + AutoPlay/AutoUse + RoleStrategy + BotManager | Alta |

## Veredicto final
YES — arquitectura confirmada (sin GameClient, sin cambios de core para el MVP).
