# BASELINES — e2518ab108 vs 43ac8878f5

> **Estado:** VERIFIED · **Evidence:** SOURCE · **Vigencia:** CURRENT
﻿# BASELINES — e2518ab108 vs 43ac8878f5

| Item | e2518ab108 (runtime/recipe/notebook) | 43ac8878f5 (UPSTREAM master 2026-09-10) |
|---|---|---|
| UPSTREAM HEAD | anterior | `43ac8878f582ea792874eb74df16e2b0b64990e6` (verificado: rev-parse + log -3) |
| Mensaje HEAD | — | `Spirit of Craftsman quest step no longer lands on a different party member than the drop. Contributed by neovita.` |
| Drift conocido | base | Quest.java:1995,2043,2056,2067,2110,2123 + Q00103_SpiritOfCraftsman x8 cronicas (killer -> qs.getPlayer()) |
| Impacto CB/handlers/bots | — | NINGUNO conocido (CL-0024). Re-anclaje FASE 3 pendiente |
| L2J Notebook HEAD (antes FASE 1) | `d2f05953f6c4bbcd5ff5a1c12f5b6b2034cf9e9a` master==origin/master | branch master...origin/master |
| L2J Notebook estado (antes FASE 1) | ` M INDEX.md, M OPEN_QUESTIONS.md` + 4 knowledge untracked (CB/DECISION/METHOD/RECIPES_CB) | snapshot 2026-09-10 |
| L2J-RECIPE HEAD (antes FASE 1) | `f7e61884b21368d3c863eddd475c93173f3bb41d` master==origin/master | limpio |
| UPSTREAM estado (antes FASE 1) | master==origin/master, `?? build/` (solo build output, sin dirty en source) | limpio para pipeline |

Regla: todo artefacto con baseline e2518ab108 = STALE hasta FASE 3. No reusar sin re-anclar.
