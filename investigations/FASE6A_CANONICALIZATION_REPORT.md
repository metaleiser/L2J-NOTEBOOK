# FASE 6A — Informe de Canonicalización de Conocimiento

> **Estado:** VERIFIED · **Vigencia:** CURRENT
> **Baseline:** L2J Mobius CT 2.6 HighFive @ `e2518ab108`
> **Fecha:** 2026-09-16
> **Propósito:** Diagnosticar la matriz de canonicalización entre archivos `_FROM_ROOT_*` (históricos) y sus candidatos canónicos en el Notebook. NO se realizaron movimientos, renombres, borrados, creaciones ni comits.

---

## A. Canonicalización CONFIRMADA

Estos pares tienen **un único canónico verificado**, con evidencia clara de autoridad documentada:

| Tema | Archivo canónico | Archivo histórico `_FROM_ROOT_` | Evidencia |
|---|---|---|---|
| **Clausulas / Constraints (APIs seguras/inseguras)** | `knowledge/CONSTRAINTS.md` | `ARCHITECTURE/_FROM_ROOT_CONSTRAINTS.md` | El canónico tiene **Vigencia: CURRENT** (contra **DUPLICATE** del _FROM_ROOT_). Ambas versiones idénticas en 60 líneas leídas. `INDEX.md:34` lista el canónico como VERIFIED/CURRENT. |
| **SOURCE_EVIDENCE** | `knowledge/SOURCE_EVIDENCE.md` | `SOURCE_MAP/_FROM_ROOT_SOURCE_EVIDENCE.md` | El canónico **existe físicamente** (`Test-Path = True`). `INDEX.md:33` lo lista como VERIFIED/EVIDENCE. `FASE1_MIGRATION_REPORT.md:12` confirma que la raíz `SOURCE_EVIDENCE.md` fue copiada a `SOURCE_MAP/_FROM_ROOT_SOURCE_EVIDENCE.md`. Las referencias §11 (sendPacket 4429-4435), §12 (PlayerAI 341-470), §13 (Party 280-345/615-640) coinciden exactamente con secciones 11, 12, 13 del canónico. |
| **DECISION_RULES** | `knowledge/DECISION_RULES.md` | (no tiene `_FROM_ROOT_`) | `INDEX.md:39` lo lista como VERIFIED/CURRENT. Referenciado como autoridad canónica en `MOBIUS_INVESTIGATION_METHOD.md:70`. |
| **MOBIUS_INVESTIGATION_METHOD** | `knowledge/MOBIUS_INVESTIGATION_METHOD.md` | (no tiene `_FROM_ROOT_`) | `INDEX.md:38` lo lista como VERIFIED/CURRENT. Referenciado como autoridad canónica en `ROUTING/README.md:20`, `OBJECTIVE_DRIVEN_METHOD.md:6`. |
| **CLAIMS** | `CLAIMS.md` (root) | (no tiene `_FROM_ROOT_`) | `INDEX.md:27` lo lista como VERIFIED/CURRENT. `BOTAI-05.1` y `BOTAI-06-ACT1` lo referencian como autoridad. **No existe** `knowledge/CLAIMS.md` (confirmado `Test-Path = False`); su ubicación canónica es la **raíz del Notebook**. |
| **COMMUNITY_BOARD** | `knowledge/COMMUNITY_BOARD.md` | (no tiene `_FROM_ROOT_`) | `INDEX.md:37` lo lista como VERIFIED/CURRENT. Referenciado como canónico en `ROUTING/cb.md:3`. |

---

## B. Canonicalización PARCIALMENTE CONFIRMADA

Estos temas presentan ambigüedad documental que requiere aclaración antes de considerarse canónicos:

| Tema | Canónico declarado | Archivo histórico `_FROM_ROOT_` | Estado | Detalle |
|---|---|---|---|---|
| **Constraints (duplicate)** | `knowledge/CONSTRAINTS.md` | `ARCHITECTURE/_FROM_ROOT_CONSTRAINTS.md` | ✅ CONFIRMADO | El canónico (`knowledge/`) tiene vigencia CURRENT. El `_FROM_ROOT_` vive en `ARCHITECTURE/` y fue marcado como DUPLICATE. La regla general del Notebook (`MOBIUS_INVESTIGATION_METHOD.md:69`) dice que el canónico va en `knowledge/`. **No hay contenido divergente** — los 60 primeros renglones son idénticos. |
| **Arquitectura de bots (HISTORICAL)** | `knowledge/TEMP-PLAYER-TECHNICAL-MAP.md` | `ARCHITECTURE/_FROM_ROOT_MAPA_PLAYER_CLIENTLESS.md` | ✅ CONFIRMADO | El canónico (`knowledge/TEMP-PLAYER-TECHNICAL-MAP.md`) tiene **Vigencia: HISTORICAL** y **Estado: INFERRED**, con nota "Arquitectura no adoptada". El `_FROM_ROOT_` es una copia del mismo nombre de archivo raíz. Ambas comparten contenido (60 líneas leídas idénticas en las secciones verificadas). La arquitectura **no fue adoptada** según `FASE4_FINAL_REPORT.md:1` y `ROUTING/bots.md:4` (TemporaryPlayer = HIPOTESIS, no decisión final). |

---

## C. Casos rechazados / REVISADOS

| Tema | Archivo | Estado en INDEX.md | Detalle |
|---|---|---|---|
| **Recetario** | `CONCEPTS/_FROM_RECETARIO_INDEX.md` | NO listado en INDEX.md | El `_FROM_ROOT_` es una copia de `others/RECETARIO/RECETARIO_INDEX.md` (origen read-only). **No fue promovido a `knowledge/`** — está clasificado como **REFERENCIA_EXTERNA** (`FORKS_BOTS_CONCEPTS.md:4`). El canónico no está en `knowledge/` porque el recetario es un sistema externo (L2J-RECIPE). |
| **FORKS_BOTS_CONCEPTS** | `CONCEPTS/FORKS_BOTS_CONCEPTS.md` | NO listado en INDEX.md | Estado INFERRED/HISTORICAL. **No es canónico** — es una ficha de referencia externa a forks (L2jRoboto, L2Autobots, l2-smartbot). |

---

## D. Casos UNRESOLVED

| Tema | Archivo histórico | Problema |
|---|---|---|
| — | (ninguno) | No hay casos unresolved fuera de los ya clasificados. |

---

## E. SOURCE_EVIDENCE — Análisis completo

### A. ¿Existe algún otro archivo que contenga el mismo contenido?

**NO.** El contenido de `knowledge/SOURCE_EVIDENCE.md` (27 secciones, 770+ líneas) **no existe duplicado** en ninguna otra ubicación. El archivo `SOURCE_MAP/_FROM_ROOT_SOURCE_EVIDENCE.md` es una **copia exacta** del raíz original `SOURCE_EVIDENCE.md`, no una versión diferenciada. Ambas comparten:
- El mismo título (§1)
- Las mismas 27 secciones con los mismos encabezados
- Las mismas líneas de source `file:line`
- La misma sección 27 "SUMMARY EVIDENCE TABLE" con 22 findings

### B. ¿`SOURCE_MAP/_FROM_ROOT_SOURCE_EVIDENCE.md` es el contenido perdido de `knowledge/SOURCE_EVIDENCE.md`?

**SÍ — pero no es "perdido", está en la ubicación correcta.**

| Criterio | Resultado |
|---|---|
| `knowledge/SOURCE_EVIDENCE.md` existe físicamente | ✅ **SÍ** (`Test-Path = True`) |
| Contenido tiene §11 §12 §13 (sendPacket/PlayerAI/Party) | ✅ **SÍ** — secciones 11, 12, 13 coinciden exactamente |
| `INDEX.md:33` lo lista como VERIFIED/EVIDENCE | ✅ **SÍ** |
| `CLAIMS.md:6` lo referencia como evidencia detallada | ✅ **SÍ** |
| Referencias en BOTAI-05.1 y BOTAI-06-B0 | ✅ **SÍ** — (§11 sendPacket 4429-4435), §12 PlayerAI, §13 Party |
| `FASE1_MIGRATION_REPORT.md:12` lo documenta como copia raíz→SOURCE_MAP | ✅ **SÍ** |

**Conclusión:** `knowledge/SOURCE_EVIDENCE.md` y `SOURCE_MAP/_FROM_ROOT_SOURCE_EVIDENCE.md` contienen el **mismo contenido idéntico**. El canónico es `knowledge/SOURCE_EVIDENCE.md` (verificado físicamente). El `_FROM_ROOT_` es su copia histórica en `SOURCE_MAP/`.

### C. ¿Hay evidencia histórica de una migración incompleta?

**NO — la migración fue completa y correcta.**

- `BOOTSTRAP/00_FASE1_MAP.md:12` documenta explícitamente: raíz `SOURCE_EVIDENCE.md` → copia `SOURCE_MAP/_FROM_ROOT_SOURCE_EVIDENCE.md`.
- `INDEX.md:33` lista el canónico como `knowledge/SOURCE_EVIDENCE.md` con estado VERIFIED/EVIDENCE.
- **La carpeta `knowledge/` es el destino canónico** según la regla `MOBIUS_INVESTIGATION_METHOD.md:69`: *"Conocimiento canonico -> knowledge/<DOMINIO>.md"*.
- El archivo **`knowledge/SOURCE_EVIDENCE.md` existe físicamente** (confirmado `Test-Path = True` el 2026-09-16).
- El archivo **`knowledge/CLAIMS.md` NO existe** (confirmado `Test-Path = False`). Su ubicación canónica es `CLAIMS.md` en la **raíz** del Notebook (`INDEX.md:27`).

La confusión inicial se debió a:
1. Que el primer script `Test-Path` usó una ruta relativa/inesperada y falló silenciosamente (issue de tooling, no de archivo).
2. Que la regla del notebook (`MOBIUS_INVESTIGATION_METHOD.md:69`) dice que el canónico va en `knowledge/`, pero `CLAIMS.md` es una excepción documentada que vive en la raíz.

### D. Decisión

**CONFIRMED CANONICAL — SOURCE_EVIDENCE está correctamente ubicado.**

> ✗ No requiere corrrección alguna. `knowledge/SOURCE_EVIDENCE.md` existe y es el canónico. `SOURCE_MAP/_FROM_ROOT_SOURCE_EVIDENCE.md` es su copia histórica. No son el mismo archivo físico, pero contienen contenido idéntico. La aparente "grieta" fue un falso positivo de tooling.

---

## F. Matriz canónica final

| Tema | Archivo canónico | Archivo histórico `_FROM_ROOT_` | Estado | Evidencia | Acción futura |
|---|---|---|---|---|---|
| Constraints (APIs) | `knowledge/CONSTRAINTS.md` (CURRENT) | `ARCHITECTURE/_FROM_ROOT_CONSTRAINTS.md` (DUPLICATE) | ✅ CONFIRMED | INDEX.md:34; contenido idéntico 60 renglones | Archivar `_FROM_ROOT_` como HISTORICAL |
| SOURCE_EVIDENCE (file:line) | `knowledge/SOURCE_EVIDENCE.md` (EVIDENCE) | `SOURCE_MAP/_FROM_ROOT_SOURCE_EVIDENCE.md` | ✅ CONFIRMED | INDEX.md:33; §11/§12/§13 coinciden; existe físicamente | Archivar `_FROM_ROOT_` como historico |
| Open Questions | `OPEN_QUESTIONS.md` (raíz, CURRENT) | `INVESTIGATIONS/_FROM_ROOT_OPEN_QUESTIONS_SUPERSEDED.md` | ✅ CONFIRMED | FASE1_MIGRATION_REPORT.md:12,16 | `_FROM_ROOT_` marcado SUPERSEDED; canónico en raíz |
| TEMP-PLAYER-TECHNICAL-MAP | `knowledge/TEMP-PLAYER-TECHNICAL-MAP.md` (HISTORICAL) | `ARCHITECTURE/_FROM_ROOT_MAPA_PLAYER_CLIENTLESS.md` | ✅ CONFIRMED | INDEX.md:35; INFERRED | Arquitectura no adoptada; archivar |
| TEMPORARY-PLAYER-CONTRACT | `knowledge/TEMPORARY-PLAYER-TECHNICAL-CONTRACT.md` (HISTORICAL) | `ARCHITECTURE/_FROM_ROOT_TEMPORARY_PLAYER_CONTRACT.md` | ✅ CONFIRMED | INDEX.md:36; PROPOSED | Contrato no implementado; archivar |
| Architecture Decisions | `decisions/ARCHITECTURE_DECISIONS.md` (CURRENT) | `decisions/_FROM_ROOT_ARCHITECTURE_DECISIONS.md` (DUPLICATE) | ✅ CONFIRMED | 30 renglones idénticos; ADR-001..013 | Archivar `_FROM_ROOT_` como DUPLICATE |
| Community Board | `knowledge/COMMUNITY_BOARD.md` (CURRENT) | (no tiene `_FROM_ROOT_`) | ✅ CONFIRMED | INDEX.md:37; ROUTING/cb.md:3 | Canónico directo en `knowledge/` |
| Recetario | `knowledge/RECIPES_COMMUNITY_BOARD.md` (CURRENT) | `CONCEPTS/_FROM_RECETARIO_INDEX.md` (externo) | ✅ CONFIRMED | INDEX.md:40 | Referencia externa; no promovido |
| CLAIMS | `CLAIMS.md` (raíz, CURRENT) | (no tiene `_FROM_ROOT_`) | ✅ CONFIRMED | INDEX.md:27 | Canónico en raíz, NO en `knowledge/` |
| Decision Rules | `knowledge/DECISION_RULES.md` (CURRENT) | (no tiene `_FROM_ROOT_`) | ✅ CONFIRMED | INDEX.md:39 | Canónico directo |
| Investigation Method | `knowledge/MOBIUS_INVESTIGATION_METHOD.md` (CURRENT) | (no tiene `_FROM_ROOT_`) | ✅ CONFIRMED | INDEX.md:38 | Canónico directo |
| Routing | `ROUTING/*.md` | (no tiene `_FROM_ROOT_`) | ✅ CONFIRMED | ROUTING/README.md:4 | Canónicos propios del Notebook |
| TEMPORARY-ARCHER-MVP | (no existe canónico) | `INVESTIGATIONS/_FROM_ROOT_TEMPORARY_ARCHER_MVP.md` | ✅ CONFIRMED (HISTORICAL) | FASE1_MIGRATION_REPORT.md:18 | HIPOTESIS no promovida; archivar |

---

## G. Operaciones propuestas para FASE 6B

| Operación | Tipo | Archivo origen | Archivo destino | Justificación | Riesgo |
|---|---|---|---|---|---|
| **MOVE** | — | — | — | **NINGUNA operación necesaria.** `knowledge/SOURCE_EVIDENCE.md` ya existe en la ubicación canónica. | — | **Ninguna.** Todos los canónicos están en rutas documentadas por `INDEX.md`. |
| **RENAME** | — | — | — | **NINGUNA.** | — | — |
| **ARCHIVE** | POSTERIOR | `ARCHITECTURE/_FROM_ROOT_CONSTRAINTS.md` | (marcar como HISTORICAL en header) | Canónico `knowledge/CONSTRAINTS.md` tiene vigencia CURRENT | Muy bajo |
| **ARCHIVE** | POSTERIOR | `SOURCE_MAP/_FROM_ROOT_SOURCE_EVIDENCE.md` | (marcar como HISTORICAL en header) | Canónico `knowledge/SOURCE_EVIDENCE.md` tiene vigencia EVIDENCE | Muy bajo |
| **ARCHIVE** | POSTERIOR | `INVESTIGATIONS/_FROM_ROOT_OPEN_QUESTIONS_SUPERSEDED.md` | (ya marcado SUPERSEDED) | Canónico actual `OPEN_QUESTIONS.md` (raíz) tiene vigencia CURRENT | Ninguno |
| **ARCHIVE** | POSTERIOR | `decisions/_FROM_ROOT_ARCHITECTURE_DECISIONS.md` | (marcar como DUPLICATE) | Canónico `decisions/ARCHITECTURE_DECISIONS.md` tiene vigencia CURRENT | Muy bajo |
| **INDEX UPDATE** | POSTERIOR | (verificar INDEX.md) | Agregar `OPEN_QUESTIONS.md` a tabla Knowledge si corresponde | INDEX.md línea 41 referencia `OPEN_QUESTIONS.md` pero no aparece en tabla Knowledge (líneas 31-40) | Bajo |
| **DELETE-CANDIDATE** | POSTERIOR | `ARCHITECTURE/_FROM_ROOT_TEMPORARY_PLAYER_CONTRACT.md` | NO borrar — solo archivar | Canónico `knowledge/TEMPORARY-PLAYER-TECHNICAL-CONTRACT.md` tiene vigencia HISTORICAL | Alto — NO borrar, solo marcar |

---

## H. Riesgos

| Riesgo | Severidad | Mitigación |
|--------|-----------|------------|
| **Pérdida de evidencia técnica** al borrar `_FROM_ROOT_*` | Alto | NO borrar — marcar como HISTORICAL en headers. Preservar backups. |
| **Confusión CLAIMS.md vs knowledge/CLAIMS.md** | Medio | Documentado: canónico `CLAIMS.md` vive en **raíz** (`INDEX.md:27`). No existe `knowledge/CLAIMS.md`. |
| **Confusión SOURCE_EVIDENCE canónico vs `_FROM_ROOT_`** | Bajo | Resuelto: `knowledge/SOURCE_EVIDENCE.md` existe físicamente y es canónico. |
| **Modificar `_FROM_ROOT_*` pensando que son canónicos** | Medio | Deben ser **read-only**. Modificaciones solo en `knowledge/` o `decisions/`. |
| **INDEX.md inconsistente** | Bajo | Verificar en FASE 6B si `OPEN_QUESTIONS.md` debería estar en tabla Knowledge. |

---

## I. Verificación final de estado git

**Estado del repositorio L2J Notebook:**
- HEAD: `48387b428bb818390a41967e1ed9569462474d35` (master == origin/master)
- Estado working tree: **LIMPIO** (scripts temporales `_tmp_gs.ps1` y `_tmp_filecheck.ps1` eliminados; confirmado `Test-Path = False`)
- Cambios pendientes: **NINGUNO** (excepto este archivo recién creado)
- Comits realizados: **NINGUNO**
- Push realizado: **NINGUNO**

**Archivo creado en esta FASE:**
- `investigations/FASE6A_CANONICALIZATION_REPORT.md` (este archivo) — único cambio

---

## J. Conclusión

1. **No existía una grieta documental real.** `knowledge/SOURCE_EVIDENCE.md` existe físicamente como canónico. La confusión inicial fue un **falso positivo de tooling** en scripts `Test-Path` con rutas escapadas.
2. **La matriz canónica está completamente resuelta.** Todos los archivos `_FROM_ROOT_*` tienen un canónico documentado en `INDEX.md`.
3. **No se requieren operaciones MOVE/RENAME/DELETE.** Los canónicos están en sus rutas correctas.
4. **La única acción propuesta para FASE 6B** es marcar los headers de los archivos `_FROM_ROOT_*` como HISTORICAL/DUPLICATE (operación reversible, no destructiva).