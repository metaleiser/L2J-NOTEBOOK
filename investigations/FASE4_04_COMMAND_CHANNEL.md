# FASE 4.04 — Command Channel y bots

## Estado: VERIFIED (infra) / UNVERIFIED (requisitos por-boss) · **Evidence:** SOURCE · **Vigencia:** CURRENT (infra) / PENDING_REVERIFICATION (requisitos)

## Hechos
- `CommandChannel(Player leader)`: toma `leader.getParty()`, la añade a `_parties`,
  fija nivel y broadcast. No dereferencia GameClient.
- `addParty(Party)`: añade party a `_parties` (Collection<Party>), broadcast MPCC.
  → Soporta N parties; permite Party1=humano+bots, Party2=bots, Party3=bots.
- `meetRaidWarCondition(obj)`: exige memberCount >= LOOT_RAIDS_PRIVILEGE_CC_SIZE.

## Respuesta a la pregunta clave
Un humano PUEDE ser líder de un CC compuesto por su party (humano+bots) + parties
de solo bots. No hay punto de fallo relacionado con cliente en la construcción del CC.

## Veredicto
Command Channel acepta bots SIN modificar el núcleo.

## Confianza: ALTA (estructura de grupos)
