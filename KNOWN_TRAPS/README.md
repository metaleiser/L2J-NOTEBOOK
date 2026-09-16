# KNOWN TRAPS (leer antes de implementar)

> **Estado:** VERIFIED · **Evidence:** SOURCE + RUNTIME · **Vigencia:** CURRENT
﻿# KNOWN TRAPS (leer antes de implementar)

| Trampa | Evidencia | Regla |
|---|---|---|
| `//reload handler` NO sustituye bytecode (ScriptClassLoader static final) | ScriptExecutor.java:53; CL-0017 | Restart para handlers |
| Buylist 423 fija en HomeBoard:192 | CB canon | Cambiar = Java |
| `_bbstop` sin sanitizar; `_bbs*` sin validateHtmlAction; flood no aplica a write | CB canon | No exponer sin gates |
| Shutdown con bots activos DESCONOCIDO | 0004 sec.5; UQ-3 | `//bs off` antes de apagar |
| Bot looter persiste items | Party 618-640; ADR-006 | Bot no-looter |
| Muerte+decay persiste | Creature 527-540; ADR-012 | Cancelar decay |
| storeMe/deleteMe/autoSave/load/create persisten | CONSTRAINTS sec.1 | NO usar en bots |
| Qwen 1.5B alucina APIs L2J (22-33%) | `_FROM_BENCHMARK_RESULTS`, `_FROM_QWEN_TEST_INFORME` | Dar imports/firmas exactas; no agente autonomo |
| Baseline: e2518ab108 != 43ac8878f5 | CL-0024; SOURCE_MAP/BASELINES.md | Re-anclar antes de reusar |
| Assumir TemporaryPlayer = solucion | ROUTING/bots.md | HIPOTESIS hasta FASE 4 |
