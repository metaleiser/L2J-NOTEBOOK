# FASE 4 — Investigación de Bots PvE server-side
## L2J Mobius CT 2.6 High Five

Estado global: **ARQUITECTURA APROBADA Y VERIFICADA (FASE 4.18)**
Fecha de cierre: 2026-09-10
Source autoritativo: `L2J_Mobius_CT_2.6_HighFive` (repo `metaleiser/L2J`, monorepo MobiusDevelopment)

## Objetivo del proyecto
Convertir High Five en un MMORPG PvE single-player: 1 jugador humano + personajes
bot persistentes controlados por el servidor, integrados en Party y (después)
Command Channel, controlados desde Community Board (Alt+B), con roles reales
(Tank/Healer/Buffer/DPS/Support) y progresión hasta Raid/Grand/Epic bosses.

## Índice de documentos
- FASE4_01_AUTOPLAY.md
- FASE4_02_PLAYER_CLIENTLESS.md
- FASE4_03_PARTY.md
- FASE4_04_COMMAND_CHANNEL.md
- FASE4_05_FAKEPLAYER.md
- FASE4_06_TEMPORARYPLAYER.md
- FASE4_07_BOT_AI.md
- FASE4_09_FOLLOW_ASSIST_COMBAT.md
- FASE4_11_COMMUNITY_BOARD.md
- FASE4_12_RAID_ENDGAME.md
- FASE4_13_SCALABILITY.md
- FASE4_17_PLAYER_CLIENT_STATE.md
- FASE4_17_CLIENTLESS_VISIBILITY.md
- FASE4_17_CLAIMS_MATRIX.md
- FASE4_18_GAMECLIENT_DETACHED.md
- FASE4_15_MVP_SPEC.md
- FASE4_16_ROADMAP.md
- FASE4_FINAL_REPORT.md
- DECISIONS/FASE4_BOT_ARCHITECTURE_FREEZE.md

## Regla de autoridad
1. Source real actual de Mobius CT 2.6 High Five.
2. Código existente de Offline Play.
3. Código existente de Party / AutoPlay / AutoUse.
4. Documentación de esta investigación.
5. Hipótesis anteriores.
La documentación NUNCA prevalece sobre el source real.

## Convención de confianza
- VERIFIED: leído directamente en el source de esta versión.
- PROBABLE: inferencia fuerte respaldada por código adyacente.
- UNVERIFIED: sin evidencia directa suficiente / requiere runtime.
- FALSE/CORRECTED: conclusión previa refutada por el source.
