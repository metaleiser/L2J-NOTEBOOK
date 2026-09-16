# FASE 4 — Plan de investigacion Bots/Party/Clientless (TemporaryPlayer = HIPOTESIS)

> Objetivo: PLAYER REAL + TANK + HEALER + BUFFER + DPS + DPS en Party PvE (luego instances/raids/bosses/epics).
> Orden GM: 1.AutoPlay/AI -> 2.Player -> 3.Party -> 4.FakePlayer -> 5.TemporaryPlayer.
> Externos: CONCEPTS/FORKS_BOTS_CONCEPTS.md (C1 Roboto, C2 Autobots, C3 smartbot). NO copiar codigo.

## F4.1 AutoPlay/AI nativo

- Que sistema/clases: AutoPlayTaskManager (+holders/config AutoPlay.ini). Re-anclar file:line en 43ac8878f5.
- Que necesita un Player: settings 0004 (targetMode/pickup/shortRange/respectfulHunting/autoActions).
- GameClient-dependencia: AUDIT (PV3). Targeting/movimiento/skills/follow/assist/attack/healing/buffs/death-revive.
- Salida: ficha + claims + experimento minimo.

## F4.2 Player sin cliente

- Auditar TODOS los puntos criticos: GameClient/packets/session/connection/sendPacket/inventory/variables/autosave/offline/AI/movement/targeting/party/death/teleport/instance/raid.
- Base: CONSTRAINTS (peligrosas vs seguras vs condicionales H1-H9) + V1-V22 + FI1-FI8 + T1-T13.
- NO basta getClient()==null. Salida: matriz Player-clientless en 43ac8878f5.

## F4.3 Party

- joinParty/leader/member-list/XP/loot/death/revive/follow/assist/teleport/instances/raids/CommandChannel.
- Anclas: Party.java:88, Player.java:6862 vs Party.java:280, CommandChannel 40-57/48-57, Quest.java:1995/2067 (CL-0024: drop a miembro aleatorio!).
- Salida: matriz exigencias Party + politica bots (no-looter ADR-006).

## F4.4 FakePlayer

- Confirmar en 43ac8878f5: FakePlayerInfo.java:38 (NPC, no Player). Si se confirma: DESCARTADO PARA PARTY PVE (conservar conocimiento).

## F4.5 TemporaryPlayer (solo tras F4.1-4.4)

- Evaluar si hace falta o basta nativo (Player.load+OfflinePlay / PlayerVariables CL-0012 / SQL REF-SQL-PROVISION).
- Si hace falta: partir del contrato 30 caps + 13 ADRs + MVP como HIPOTESIS.
- Metodo por objetivo: `healer cura -> AI? -> target? -> skill? -> Player? -> Party? -> GameClient? -> spike minimo` (PATTERNS/OBJECTIVE_DRIVEN_METHOD.md).
- Cadena: REFERENCIA_EXTERNA -> HIPOTESIS -> VERIFICACION 43ac8878f5 -> EXPERIMENTO -> RESULTADO -> CLAIM.

## Parada

Tras FASE 1+2 entregar informe y DETENERSE antes de tocar 0001-0004 o bots (orden GM).
