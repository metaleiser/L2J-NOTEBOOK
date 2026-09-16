# FASE 4.05 — FakePlayer (por qué NO sirve)

## Estado: VERIFIED · **Evidence:** SOURCE · **Vigencia:** CURRENT · **Tipo:** Referencia negativa (demuestra qué NO usar)

## Hallazgo
El sistema "FakePlayer" de este source es un NPC (Attackable) con apariencia de
jugador, NO un Player. Se gobierna por `AttackableAI` y datos de `FakePlayerData`
(`_fakePlayerNames`, `_fakePlayerIds`, `_talkableFakePlayerNames`), controlado por
`FakePlayersConfig.FAKE_PLAYERS_ENABLED`.

## Consecuencia
- NO puede ser miembro de Party (Party espera Player).
- NO tiene inventario/skills/persistencia de Player.
- Su IA (AttackableAI) está acoplada a Attackable.

## Veredicto
FakePlayer NO es base para nuestros bots. Solo referencia conceptual.

## Confianza: ALTA
