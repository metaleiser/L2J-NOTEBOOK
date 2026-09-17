# FASE 6B — EXECUTION PLAN

> **Modo:** ACT (materialización del plan aprobado en FASE 6B-PLAN).
> **Objetivo:** archivar reversible y no destructivamente los históricos `_FROM_ROOT_*` sin romper enlaces de conocimiento. **Nada de MOVE/RENAME/DELETE/MERGE de contenido.**
> **Repo:** `C:\L2J MOBIUS IA\L2J Notebook`; HEAD `48387b428bb818390a41967e1ed9569462474d35`; `master` ≡ `origin/master`; WT limpio excepto `?? investigations/FASE6A_CANONICALIZATION_REPORT.md`.
> **Regla de oro:** los únicos archivos que mencionan `_FROM_ROOT_*` (`git grep -l "_FROM_ROOT_"`) son `ARCHITECTURE/README.md`, `BOOTSTRAP/00_FASE1_MAP.md`, `INDEX.md`, `investigations/FASE1_MIGRATION_REPORT.md` (+ no-rastreado FASE6A_REPORT). **Ningún doc de conocimiento/decisión enlaza un `_FROM_ROOT_*`.** Archivar en sitio (header-only) rompe cero enlaces.

---

## A. Checkpoint
- Repo: `C:\L2J MOBIUS IA\L2J Notebook` (raíz `C:\L2J MOBIUS IA` NO es repo git).
- Branch `master`; HEAD `48387b428bb818390a41967e1ed9569462474d35`; `origin/master` idéntico.
- `git status --porcelain`: limpio salvo `?? investigations/FASE6A_CANONICALIZATION_REPORT.md`.
- Relación FASE 6A: reporte recuperado completo (163 líneas, A-J). Esta plan valida/corrige su matriz §F (ver §H).

## B. Canónicos (todos VERIFIED físicamente; headers leídos)

| # | Canónico | Estado/Vigencia | Evidencia |
|---|---|---|---|
| 1 | `knowledge/CONSTRAINTS.md` | VERIFIED · EVIDENCE · CURRENT | INDEX.md:34; header l.5 ✅ |
| 2 | `knowledge/TEMP-PLAYER-TECHNICAL-MAP.md` | INFERRED · HISTORICAL | INDEX.md:35; header l.3 ✅ |
| 3 | `knowledge/TEMPORARY-PLAYER-TECHNICAL-CONTRACT.md` | PROPOSED · HISTORICAL | INDEX.md:36; header l.3 ✅ |
| 4 | `knowledge/SOURCE_EVIDENCE.md` | VERIFIED · EVIDENCE | INDEX.md:33; SHA `AFCCAE09…` |
| 5 | `decisions/ARCHITECTURE_DECISIONS.md` (ADR-001..013) | VERIFIED · CURRENT | INDEX.md:47; header ✅ |
| 6 | `OPEN_QUESTIONS.md` (raíz) | INFERRED · CURRENT | INDEX.md:41; header ✅ |
| 7 | `investigations/TEMPORARY-ARCHER-MVP.md` | PROPOSED · HISTORICAL | INDEX.md:74 ✅ (existe) |
| — | `BOOTSTRAP/_FROM_ROOT_cline_bat.md` | operacional (.bat launcher) | origen root `cline.bat` (ausente) |
| — | `BOOTSTRAP/_FROM_ROOT_clinerules.md` | operacional (.clinerules) | origen root `.clinerules.md` (ausente) |
| — | `decisions/_FROM_ROOT_ahora_GIT-02D_AUTH.md` | procedural (auth GIT-02D) | origen root `ahora.txt` (ausente) |

## C. Historícos — los 10 `_FROM_ROOT_*` (mapa 1:1 vía `BOOTSTRAP/00_FASE1_MAP.md:9-18`)

| # | Histórico | Root original (manifiesto) | Canónico/Authority | Vigencia histórico | Header actual |
|---|---|---|---|---|---|
| H1 | `ARCHITECTURE/_FROM_ROOT_CONSTRAINTS.md` | `CONSTRAINTS.md` | `knowledge/CONSTRAINTS.md` | DUPLICATE (candidato) | ✅ `Authority`+`DUPLICATE (candidato)` |
| H2 | `ARCHITECTURE/_FROM_ROOT_MAPA_PLAYER_CLIENTLESS.md` | `MOBIUS — MAPA…md` | `knowledge/TEMP-PLAYER-TECHNICAL-MAP.md` | DUPLICATE (candidato) | ✅ `Authority`+`DUPLICATE (candidato)` |
| H3 | `ARCHITECTURE/_FROM_ROOT_TEMPORARY_PLAYER_CONTRACT.md` | `TEMPORARY PLAYER—CONTRACT.md` | `knowledge/TEMPORARY-PLAYER-TECHNICAL-CONTRACT.md` | DUPLICATE (candidato) | ✅ `Authority`+`DUPLICATE (candidato)` |
| H4 | `SOURCE_MAP/_FROM_ROOT_SOURCE_EVIDENCE.md` | `SOURCE_EVIDENCE.md` | `knowledge/SOURCE_EVIDENCE.md` | HISTORICAL | ⚠️ header original; sin marcador `Authority` |
| H5 | `decisions/_FROM_ROOT_ARCHITECTURE_DECISIONS.md` | `ARCHITECTURE_DECISIONS.md` | `decisions/ARCHITECTURE_DECISIONS.md` | DUPLICATE (candidato) | ✅ `Authority`+`DUPLICATE (candidato)` |
| H6 | `decisions/_FROM_ROOT_ahora_GIT-02D_AUTH.md` | `ahora.txt` | (procedim.) | PROCEDIMENTAL | NO canónico |
| H7 | `investigations/_FROM_ROOT_OPEN_QUESTIONS_SUPERSEDED.md` | `OPEN_QUESTIONS.md` | `OPEN_QUESTIONS.md` (raíz) | SUPERSEDED | ⚠️ header `Post-investigación`; sin banner `Authority` |
| H8 | `investigations/_FROM_ROOT_TEMPORARY_ARCHER_MVP.md` | `TEMPORARY ARCHER…MVP.md` | `investigations/TEMPORARY-ARCHER-MVP.md` | HISTORICAL | ⚠️ sin marcador `Authority/DUPLICATE` |
| H9 | `BOOTSTRAP/_FROM_ROOT_cline_bat.md` | `cline.bat` | (launcher) | OPERACIONAL | NO canónico |
| H10 | `BOOTSTRAP/_FROM_ROOT_clinerules.md` | `.clinerules.md` | (identidad) | OPERACIONAL | NO canónico |

## D. Archive candidates (7 — archivado HEADER-ONLY en sitio)

Operación: APPEND banner `> **FASE 6B ARCHIVAL:** Historical snapshot — DUPLICATE/SUPERSEDED of <canónico>. Content unchanged. Read-only.` Solo armonizar los que LO QUEDEN SIN marcador (H4, H7, H8); H1/H2/H3/H5 ya marcados (solo verificar). **Cero cambio de contenido; cero MOVE.**

| Archivo | Acción | Refs entrantes (rastreados) | Riesgo | Rollback |
|---|---|---|---|---|
| H1 | verificar header (ya marcado) | README:8, `00_FASE1_MAP`:11, INDEX:102, FASE1_MIG:14, FASE6A:16,31,104,126 | Muy bajo | `git restore` |
| H2 | verificar header (ya marcado) | README:7, `00_FASE1_MAP`:16, INDEX:103, FASE1_MIG:19, FASE6A:32,107 | Muy bajo | `git restore` |
| H3 | verificar header (ya marcado) | README:6, `00_FASE1_MAP`:14, INDEX:104, FASE1_MIG:17, FASE6A:108,131 | Alto* (solo borrar) | `git restore` |
| H4 | ADD header HISTORICAL + `Authority: knowledge/SOURCE_EVIDENCE.md` | `00_FASE1_MAP`:12, FASE1_MIG:15, FASE6A:17,76,82,96,105,127 | Muy bajo | `git restore` |
| H5 | verificar header (ya marcado) | `00_FASE1_MAP`:10, INDEX:48, FASE1_MIG:13, FASE6A:109,129 | Muy bajo | `git restore` |
| H7 | ADD banner SUPERSEDED | `00_FASE1_MAP`:13, FASE1_MIG:16, FASE6A:106,128 | Ninguno | `git restore` |
| H8 | ADD header HISTORICAL + `Authority: investigations/TEMPORARY-ARCHER-MVP.md` | `00_FASE1_MAP`:15, FASE1_MIG:18, FASE6A:116 | Muy bajo | `git restore` |

\* H3: "Alto" solo si se **borra** (el reporte §G:131 ya lo advierte). Archivado header-only = Muy bajo.

### Especificación SOURCE_EVIDENCE (caso énfasis)
- Canónico: `knowledge/SOURCE_EVIDENCE.md` SHA `AFCCAE09CB62EB0F0CCB66DAF212885A7481BEBA533FC03B45F2C5600A096FC9`.
- Histórico: `SOURCE_MAP/_FROM_ROOT_SOURCE_EVIDENCE.md` SHA `06132400840270F2786A922AEEFC5196A445A858490F1247C7A7122816043CE4`.
- Diferencia: únicamente el epígrafe header (`@@ -1,7 +1,6 @@`; 3 líneas: 1 ins / 2 del). Canónico enriqueció header a convención INDEX (`VERIFIED · Evidence: SOURCE · Vigencia: EVIDENCE` + puntero `Claims consolidados`). Secciones §1–§27 idénticas (título, 27 secciones, file:line, §27 SUMMARY 22 findings). → No es byte-idéntico (hashes difieren) pero **el contenido de evidencia sí es idéntico**. Histórico preserva header original PRE-canonicalización → trazabilidad. **Preservar.** Archivo = ADD marcador HISTORICAL a H4 (NO tocar §1–§27).

## E. NO ACTION documents (3 — preservar, no tocar)

| Archivo | Razón |
|---|---|
| H6 `decisions/_FROM_ROOT_ahora_GIT-02D_AUTH.md` | Procedimiento de autorización sync GIT-02D (`git pull --ff-only`). Consumido; no es conocimiento domain. |
| H9 `BOOTSTRAP/_FROM_ROOT_cline_bat.md` | Launcher runtime `.bat`; el root `cline.bat` está ausente → posible única copia. NO archivar/borrar. |
| H10 `BOOTSTRAP/_FROM_ROOT_clinerules.md` | Identidad/procedimientos del workspace (`.clinerules`); root ausente → posible única copia. NO tocar. |

## F. INDEX changes (solo EXECUTION, con autorización — INDEX.md es sensible)
- **NO CHANGE (falso positivo):** el reporte §G:130 propuso "agregar OPEN_QUESTIONS.md a tabla Knowledge". `INDEX.md:41` YA lo lista dentro de la tabla Knowledge. No hay inconsistencia.
- **PROPUESTO (opcional):** agregar filas históricas `SOURCE_MAP/_FROM_ROOT_SOURCE_EVIDENCE.md` (HISTORICAL) e `investigations/_FROM_ROOT_TEMPORARY_ARCHER_MVP.md` (DUPLICATE) para documentar los pares omitidos por FASE 6A.
- Los 3 `ARCHITECTURE/_FROM_ROOT_*` + `decisions/_FROM_ROOT_ARCHITECTURE_DECISIONS.md` YA figuran en INDEX (102-104, 48) → nada que cambiar.

## G. Cross-link changes — **NINGUNA (verificado)**
`git grep -l "_FROM_ROOT_"` → {`ARCHITECTURE/README.md`, `BOOTSTRAP/00_FASE1_MAP.md`, `INDEX.md`, `investigations/FASE1_MIGRATION_REPORT.md`}. Ningún doc de knowledge/decisions/evidence enlaza un histórico. `CLAIMS.md:6` apunta al canónico `knowledge/SOURCE_EVIDENCE.md`. **Cero cross-links de contenido a actualizar.**

## H. Report correction candidates (NO aplicar sobre FASE6A_REPORT — registrar aquí)
- **H1 — §E "contenido idéntico / copia exacta":** corregir a "§1–§27 idénticas; headers difieren en 3 líneas; SHA-256 distintos". La conclusión §D (canónico correcto) sigue válida.
- **H2 — §F:116 MVP "(no existe canónico)":** FALSO. Canónico = `investigations/TEMPORARY-ARCHER-MVP.md` (INDEX.md:74). El `_FROM_ROOT_` es su snapshot HISTORICAL.
- **H3 — §G:130 "INDEX UPDATE: agregar OPEN_QUESTIONS.md":** NO-ACTION (ya está en INDEX.md:41).
- **H4 — §F omitió 3 artefactos operacionales:** H6, H9, H10 (ver §E).
- **Política:** NO modificar `FASE6A_CANONICALIZATION_REPORT.md` (artefacto de auditoría con sello temporal). Si se desea corregir, crear `investigations/FASE6A_REPORT_ADDENDUM.md` en EXECUTION.

## I. DELETE policy — **NO DELETE**
Ningún `_FROM_ROOT_*` se borra. El reporte §G:131 marcó H3 como DELETE-CANDIDATE pero lo anuló por su propio "NO borrar — solo archivar". Los 3 operacionales (H6/H9/H10) pueden ser copias únicas (root ausente) → preservar. Base: regla Notebook "Git is history: never rewrite" + project rule 11 (preservar backups).

## J. MERGE policy — **NO MERGE**
No fusionar históricos en canónicos: son snapshots con header original (pre-canonicalización) y valor de trazabilidad. `SOURCE_EVIDENCE` canónico tiene header ENRIQUECIDO vs. histórico ORIGINAL → preservar ambos.

## K. Execution order (futuro, requiere autorización explícita)
1. **Checkpoint:** `git rev-parse HEAD` + `git status --porcelain` → confirmar `48387b4` y WT limpio excepto plan.
2. **Backup:** copiar los 7 candidatos (§D) a `.bak` (git history es el backup primario).
3. **Verificar headers H1..H8** (leer l.1-8).
4. **Armonizar:** APPEND banner a H4, H7, H8 (H1/H2/H3/H5 ya marcados → confirmar). CERO cambio de contenido.
5. **`git diff --stat`:** SOLO banners de header en ≤3 archivos + plan. Nada en `knowledge/`, canónicos, raíz.
6. **INDEX.md** (solo si se autoriza): aplicar §F opcional.
7. **Commit** (solo si se autoriza): `git add -A && git commit -m "FASE 6B: archive _FROM_ROOT_ historicals (header-only, content unchanged)"`. **Push: NO.**
8. **Final audit:** `git grep -l "_FROM_ROOT_"` (estable); `git status`; hashes canónicos inalterados.

## L. Verification gates (obligatorias tras K.4 y K.7)
- **G1:** `git status --porcelain` = exactamente ≤3 históricos editados + plan (nada más).
- **G2:** `git diff <histórico>` muestra SOLO banner de header; cero cambios en §1–§27 / ADRs / facts.
- **G3:** `git grep -l "_FROM_ROOT_"` = {README, 00_FASE1_MAP, INDEX, FASE1_MIGRATION_REPORT} (estable).
- **G4:** hashes canónicos inalterados (CONSTRAINTS, SOURCE_EVIDENCE, DECISION_RULES, ARCHITECTURE_DECISIONS, OPEN_QUESTIONS, TEMPORARY-ARCHER-MVP).
- **G5:** HEAD sigue en `48387b4` (pre-commit) o el commit FASE6B es child directo de `48387b4`.

## M. Rollback
- **Primary:** `git restore -- '<ruta>'` (pre-commit) / `git restore .` (todo) ; `git revert <commitFASE6B>` (post-commit).
- **Secondary:** backup del paso K.2.
- **INDEX.md:** `git checkout -- INDEX.md` (pre-commit) / `git revert` (post-commit). Nunca merge/rebase.

## N. Freeze recovery checkpoint
- **Último paso completado:** creación de este plan. FASE 6A intacto.
- **Operaciones realizadas por FASE 6B:** NINGUNA aún (solo creación de este archivo).
- **Pendientes:** §K.1–K.8 (requieren autorización explícita).
- **Único archivo nuevo:** `investigations/FASE6B_EXECUTION_PLAN.md`.
- **Archivos garantizados intachos:** todo `knowledge/`, `decisions/ARCHITECTURE_DECISIONS.md`, `OPEN_QUESTIONS.md`, raíz, `UPSTREAM`, runtime, canónicos.
- **Próximo paso seguro al reanudar:** §K.1 (checkpoint) → §K.2 (backup) → §K.3 (verificar headers) → §K.4 (armonizar H4/H7/H8).

---

**Nota de proceso:** documento creado en ACT mode como única mutación del working tree aprobada (además del `FASE6A_REPORT` preexistente e intocado). Tras su creación re-ejecutar verificación read-only (git status / git grep / Test-Path) para confirmar baseline.