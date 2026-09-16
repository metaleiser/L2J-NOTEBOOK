# FASE 1 — Mapa de migracion (2026-09-10, reversible, sin borrados)

> Origenes INTACTOS. Todo lo de abajo son COPIAS. Nada se borro ni se movio fisicamente.

## A. Documentos raiz -> L2J Notebook

| Origen (raiz, NO tocado) | Copia en Notebook | Estado |
|---|---|---|
| `.clinerules.md` | `BOOTSTRAP/_FROM_ROOT_clinerules.md` | COPIADO |
| `ARCHITECTURE_DECISIONS.md` (13 ADRs Temporary Player) | `DECISIONS/_FROM_ROOT_ARCHITECTURE_DECISIONS.md` | COPIADO |
| `CONSTRAINTS.md` (APIs seguras/inseguras) | `ARCHITECTURE/_FROM_ROOT_CONSTRAINTS.md` | COPIADO |
| `SOURCE_EVIDENCE.md` (27 evidencias file:line) | `SOURCE_MAP/_FROM_ROOT_SOURCE_EVIDENCE.md` | COPIADO |
| `OPEN_QUESTIONS.md` (V/PV/H/FI/T/D + lifecycle + CB) | `INVESTIGATIONS/_FROM_ROOT_OPEN_QUESTIONS_SUPERSEDED.md` | COPIADO |
| `TEMPORARY PLAYER — TECHNICAL CONTRACT.md` | `ARCHITECTURE/_FROM_ROOT_TEMPORARY_PLAYER_CONTRACT.md` | COPIADO: tratar como HIPOTESIS |
| `TEMPORARY ARCHER — MINIMUM VIABLE PROTOTYPE.md` | `INVESTIGATIONS/_FROM_ROOT_TEMPORARY_ARCHER_MVP.md` | COPIADO |
| `MOBIUS — MAPA TECNICO DE PLAYER CLIENTLESS TEMPORAL.md` | `ARCHITECTURE/_FROM_ROOT_MAPA_PLAYER_CLIENTLESS.md` | COPIADO |
| `ahora.txt` (autorizacion GIT-02D) | `DECISIONS/_FROM_ROOT_ahora_GIT-02D_AUTH.md` | COPIADO |
| `cline.bat` (launcher qwen local) | `BOOTSTRAP/_FROM_ROOT_cline_bat.md` | COPIADO |

## B. Conocimiento others/ -> L2J Notebook (solo copia, others/ read-only)

| Origen (others/, NO tocado) | Copia | Valor rescatado |
|---|---|---|
| `others/exp00/method/BOOT.md` | `PATTERNS/_FROM_EXP00_BOOT_METHOD.md` | Metodo GM INTENT->RECIPE; clasificacion NATIVO/RUNTIME/RESIDUO/EXTERNO/DESCONOCIDO |
| `others/exp00/slices/A_bs/recipe.md` | `INVESTIGATIONS/_FROM_EXP00_A_BS_RECIPE.md` | Receta //bs (origen de patches/0004) |
| `others/RECETARIO/RECETARIO_INDEX.md` | `CONCEPTS/_FROM_RECETARIO_INDEX.md` | R-BOT/R-BS001/R-PREPMASTER/R-GM-DASHBOARD/R-SRV001/R-SP001 + 4 REFs |
| `others/WORKFLOW_RULES.md` | `BOOTSTRAP/_FROM_OTHERS_WORKFLOW_RULES.md` | Regla R1: informes a investigacion features y forks/ |
| `others/benchmark/RESULTS.md` | `KNOWN_TRAPS/_FROM_BENCHMARK_RESULTS.md` | qwen1.5B 14.96 tok/s; alucina APIs L2J |
| `others/qwen_test/INFORME.md` | `INVESTIGATIONS/_FROM_QWEN_TEST_INFORME.md` | Score 22-33%; correccion SimpleGreeter |

## C. Estructura creada

`BOOTSTRAP/ ROUTING/ CONCEPTS/ ARCHITECTURE/ PATTERNS/ SOURCE_MAP/ VERIFICATION/ KNOWN_TRAPS/ INVESTIGATIONS/ CLAIMS/ DECISIONS/`

Ficheros curados nuevos en esta FASE 1: `BOOTSTRAP/00_FASE1_MAP.md` (este), `ROUTING/` (3), `ARCHITECTURE/README.md`, `PATTERNS/OBJECTIVE_DRIVEN_METHOD.md`, `CONCEPTS/FORKS_BOTS_CONCEPTS.md`, `KNOWN_TRAPS/README.md`, `SOURCE_MAP/BASELINES.md`, `INVESTIGATIONS/FASE1_MIGRATION_REPORT.md`, `INVESTIGATIONS/FASE3_COMPAT_43ac8878f5.md`, `INVESTIGATIONS/FASE4_BOTS_RESEARCH_PLAN.md`.

## D. Pendiente de eliminar (REQUIERE ORDEN GM — nada borrado)

1. Raiz: 10 docs (tras commit Notebook).
2. `others/exp00, benchmark, qwen_test, RECETARIO, WORKFLOW_RULES` (tras commit).
3. `game/` legacy: NO hasta comparar customs vs runtime nuevo.
4. Forks en `others/investigacion features y forks/`: NO hasta ficha CONCEPTS completa.
5. `UPSTREAM/tools/*.log, BIT33FE.tmp`: tras FASE 2.
6. `L2J Notebook/inbox/fase1_*.txt`: marcadores temporales, borrar al cerrar FASE 1.
