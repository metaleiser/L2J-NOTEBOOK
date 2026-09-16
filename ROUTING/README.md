# ROUTING — Necesito X. Donde miro?

> Regla: leer SOLO esta pagina + 1 ficha. No abrir 100 archivos.
> **Gobernanza:** Ver NOTEBOOK_GOVERNANCE.md para reglas de creación/actualización de conocimiento.
﻿# ROUTING — Necesito X. Donde miro?

> Regla: leer SOLO esta pagina + 1 ficha. No abrir 100 archivos.

| Necesito... | Ficha | Evidencia base |
|---|---|---|
| Convocar party de bots con 1 comando | `bots.md#bs` | patches/0004, R-BOT/R-BS001 |
| Que un bot cure al party (healer) | `bots.md#healer` | AutoPlayTaskManager, PlayerAI, Party |
| Saber si FakePlayer sirve para party | `bots.md#fakeplayer` | FakePlayerInfo.java:38 -> DESCARTADO |
| Disenar un TemporaryPlayer | `bots.md#temporary` | HIPOTESIS: leer 13 ADRs + CONSTRAINTS primero |
| Anadir pagina/buff/teleport al CB | `cb.md` | knowledge/COMMUNITY_BOARD.md |
| Nuevo NPC de servicio | L2J-RECIPE patches/0001 | PrepMaster |
| Spawn persistente de NPC | L2J-RECIPE patches/0003 | SpawnData.java:110 |
| Arrancar/parar el servidor | REF-BOOT (L2J-RECIPE) | CL-0020..CL-0023 |
| Que cambio entre baselines | `../SOURCE_MAP/BASELINES.md` | CL-0024 |
| Metodologia de investigacion | `../PATTERNS/OBJECTIVE_DRIVEN_METHOD.md` | 24 pasos + 11 reglas |
