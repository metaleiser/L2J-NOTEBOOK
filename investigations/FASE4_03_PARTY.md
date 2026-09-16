# FASE 4.03 — Party y bots

## Estado: VERIFIED · **Evidence:** SOURCE · **Vigencia:** CURRENT

## Hechos
- Party trabaja sobre `List<Player>`; un bot-Player es miembro válido sin clase especial.
- `member.joinParty(party)` es el mecanismo usado por el propio OfflinePlayTable para
  reconstruir parties de players autónomos al arrancar.
- XP/SP/loot se distribuyen por la lógica estándar de Party (distributeXpAndSp);
  no requiere cliente.

## Evidencia
`OfflinePlayTable.restoreOfflinePlayers()` crea `new Party(leader, PartyDistributionType...)`
y añade miembros con `member.joinParty(party)` sobre players sin cliente.

## Veredicto
Party acepta bots SIN modificar Party.java.

## Confianza: ALTA
