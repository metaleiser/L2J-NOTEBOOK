# FASE 4.09 — Follow / Assist / Combat

## Estado: VERIFIED · **Evidence:** SOURCE · **Vigencia:** CURRENT

- Follow/Attack se realizan vía Intentions de la jerarquía AI
  (`setIntentionAttack`, `startFollow`) — ya implementados en AbstractAI/CreatureAI.
- En el modelo aprobado, AutoPlay ya cubre target+move+attack+assist+pickup, y
  AutoUse cubre buffs/pociones/skills. No se requiere lógica nueva de combate para el MVP.
- `PlayerAI.thinkAttack/thinkCast` ejecutan el ataque/cast una vez fijada la intención.

## Veredicto
Follow/Assist/Attack/AutoUse: reutilización pura para el MVP.

## Confianza: ALTA
