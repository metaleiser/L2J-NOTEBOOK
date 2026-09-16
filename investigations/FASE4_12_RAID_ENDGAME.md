# FASE 4.12 — Raid / Grand / Epic bosses

## Estado: mezcla VERIFIED / UNVERIFIED

## Entrada a BossZone (VERIFIED)
`entity/zone/type/BossZone.java`:
- `onEnter(Creature)`: admite al player SOLO si su objectId está en
  `getSettings().getPlayersAllowed()`; si no, teletransporta fuera.
- `allowPlayerEntry(Player, durationInSec)`: registra objectId permitido + reentrada.
Como la admisión es por objectId (no por estado de cliente), un bot-Player puede ser
admitido igual que un humano si el script del boss lo permite.

## Privilegio de loot de raid por CC (VERIFIED)
`CommandChannel.meetRaidWarCondition`: exige memberCount >= LOOT_RAIDS_PRIVILEGE_CC_SIZE.

## Target de raid en AutoPlay (limitación VERIFIED)
`AutoPlayTaskManager.isTargetModeValid` no prioriza raids por defecto → atacar un raid
exige extender el targeting (capa nueva), no modificar el manager.

## UNVERIFIED
- Requisitos por-boss (quests, items de entrada, mínimos) viven en scripts NO leídos.
- Mecánicas scriptadas de Grand/Epic requieren comportamiento por-boss (capa posterior).

## Conclusión
Infra de entrada/loot: compatible con bots. IA de raid y requisitos por-boss:
trabajo posterior, no bloquean el MVP.

## Confianza: MEDIA
