# FASE 4.07 — Estrategia de BotAI

## Estado: VERIFIED (jerarquía AI) + PROPOSED (RoleStrategy) · **Evidence:** SOURCE · **Vigencia:** CURRENT

## Jerarquía AI existente
`AbstractAI` → `CreatureAI` → `PlayableAI` → `PlayerAI`.
- `PlayerAI.notifyActionThink()` despacha según Intention (ATTACK/CAST/PICK_UP/INTERACT).
- `PlayerAI.thinkAttack()`: valida target, `maybeMoveToPawn`, `doAttack`.
- `PlayerAI.thinkCast()`: valida target/rango, `doCast`.
- `PlayerAI` es REACTIVO: ejecuta la intención que le fijan; no "decide" solo.

## Quién decide
La capa de DECISIÓN autónoma es AutoPlay/AutoUse (no PlayerAI). Por eso NO se crea
un ThinkLoop nuevo: se reutiliza AutoPlay y se le añade una capa fina de rol.

## Propuesta (no implementar aún)
`RoleStrategy` (Tank/Healer/Buffer/DPS/Support) que ajuste prioridades de
target/skill por encima de AutoPlay, sin tocar PlayerAI ni AttackableAI.

## AttackableAI como referencia
`AttackableAI` tiene lógica de reconsideración de target para raids
(`RAID_CHAOS_TIME`, etc.) útil como REFERENCIA, pero está acoplada a Attackable:
NO heredar de ella; portar lógica como helpers si hiciera falta.

## Confianza: ALTA (estructura) / MEDIA (diseño RoleStrategy)
