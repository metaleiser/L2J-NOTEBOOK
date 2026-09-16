# FASE 1 — Informe de migracion (2026-09-10)

## Que se copio (origenes INTACTOS, nada borrado)

- Raiz (10 docs) -> BOOTSTRAP/DECISIONS/ARCHITECTURE/SOURCE_MAP/INVESTIGATIONS (ver BOOTSTRAP/00_FASE1_MAP.md sec.A).
- others/ (6 ficheros) -> PATTERNS/INVESTIGATIONS/CONCEPTS/BOOTSTRAP/KNOWN_TRAPS (sec.B).

## Que conocimiento se extrajo y donde quedo

| Conocimiento | Desde | Donde quedo |
|---|---|---|
| Reglas proyecto (2 productos, retrieval, trust, write-by-change, validacion, promocion, stop, others read-only) | raiz .clinerules.md | BOOTSTRAP/_FROM_ROOT_clinerules.md |
| 13 ADRs TemporaryPlayer + alternativas rechazadas | raiz ARCHITECTURE_DECISIONS.md | DECISIONS/_FROM_ROOT_ARCHITECTURE_DECISIONS.md |
| APIs peligrosas/seguras/condicionales + H1-H9 + gates | raiz CONSTRAINTS.md | ARCHITECTURE/_FROM_ROOT_CONSTRAINTS.md |
| 27 evidencias file:line Player/Item/Party/AI/World | raiz SOURCE_EVIDENCE.md | SOURCE_MAP/_FROM_ROOT_SOURCE_EVIDENCE.md |
| V1-V22, PV1-4, H1-6, FI1-8, T1-13, D1-6, UQ-1-4, CB-1-3 | raiz OPEN_QUESTIONS.md | INVESTIGATIONS/_FROM_ROOT_OPEN_QUESTIONS_SUPERSEDED.md |
| Contrato 30 caps TemporaryPlayer | raiz TEMPORARY PLAYER...md | ARCHITECTURE/_FROM_ROOT_TEMPORARY_PLAYER_CONTRACT.md (HIPOTESIS) |
| MVP Archer 33 secciones | raiz TEMPORARY ARCHER...md | INVESTIGATIONS/_FROM_ROOT_TEMPORARY_ARCHER_MVP.md |
| Mapa World/PlayerAI/AutoPlay/Party/cleanup | raiz MOBIUS — MAPA...md | ARCHITECTURE/_FROM_ROOT_MAPA_PLAYER_CLIENTLESS.md |
| Autorizacion GIT-02D | raiz ahora.txt | DECISIONS/_FROM_ROOT_ahora_GIT-02D_AUTH.md |
| Launcher qwen local | raiz cline.bat | BOOTSTRAP/_FROM_ROOT_cline_bat.md |
| Metodo exp00 + clasificacion NATIVO/RUNTIME/RESIDUO/EXTERNO/DESCONOCIDO | others/exp00/method/BOOT.md | PATTERNS/_FROM_EXP00_BOOT_METHOD.md |
| Receta //bs (origen 0004) | others/exp00/slices/A_bs/recipe.md | INVESTIGATIONS/_FROM_EXP00_A_BS_RECIPE.md |
| R-BOT/R-BS001/R-PREPMASTER/R-GM-DASHBOARD/R-SRV001/R-SP001 + 4 REFs | others/RECETARIO/RECETARIO_INDEX.md | CONCEPTS/_FROM_RECETARIO_INDEX.md |
| R1 informes -> investigacion features y forks/ | others/WORKFLOW_RULES.md | BOOTSTRAP/_FROM_OTHERS_WORKFLOW_RULES.md |
| qwen1.5B 14.96 tok/s, alucina dominio | others/benchmark/RESULTS.md | KNOWN_TRAPS/_FROM_BENCHMARK_RESULTS.md |
| Score 22-33%, SimpleGreeter corregido | others/qwen_test/INFORME.md | INVESTIGATIONS/_FROM_QWEN_TEST_INFORME.md |
| Ideas Roboto/Autobots/smartbot (solo README, sin Java) | others/investigacion features y forks/*/README.md | CONCEPTS/FORKS_BOTS_CONCEPTS.md (REFERENCIA_EXTERNA) |

## Ficheros curados nuevos

`BOOTSTRAP/00_FASE1_MAP.md, ROUTING/README.md, ROUTING/bots.md, ROUTING/cb.md, ARCHITECTURE/README.md, PATTERNS/OBJECTIVE_DRIVEN_METHOD.md, CONCEPTS/FORKS_BOTS_CONCEPTS.md, KNOWN_TRAPS/README.md, SOURCE_MAP/BASELINES.md, INVESTIGATIONS/FASE3_COMPAT_43ac8878f5.md, INVESTIGATIONS/FASE4_BOTS_RESEARCH_PLAN.md` + este informe.

## Trazabilidad

Cada copia lleva prefijo `_FROM_ROOT_` o `_FROM_EXP00_/_FROM_RECETARIO_/_FROM_OTHERS_/_FROM_BENCHMARK_/_FROM_QWEN_`. Origenes con rutas absolutas en BOOTSTRAP/00_FASE1_MAP.md.

## Pendiente de eliminar (REQUIERE ORDEN GM)

1. Raiz 10 docs (tras commit). 2. others/exp00, benchmark, qwen_test, RECETARIO, WORKFLOW_RULES (tras commit). 3. game/ legacy: NO (comparar customs pendiente). 4. Forks: NO (ficha CONCEPTS solo de READMEs; Java no auditado). 5. UPSTREAM/tools logs + BIT33FE.tmp: tras FASE 2. 6. inbox/fase1_*.txt: al cerrar FASE 1.

## Estado git antes de FASE 1 (snapshot 2026-09-10)

- UPSTREAM: `43ac8878f582ea792874eb74df16e2b0b64990e6` master==origin/master, solo `?? build/`.
- L2J Notebook: `d2f05953f6c4bbcd5ff5a1c12f5b6b2034cf9e9a` master==origin/master, ` M INDEX.md, M OPEN_QUESTIONS.md` + 4 knowledge untracked.
- L2J-RECIPE: `f7e61884b21368d3c863eddd475c93173f3bb41d` master==origin/master, limpio.
