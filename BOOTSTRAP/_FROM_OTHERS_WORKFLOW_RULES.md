# WORKFLOW RULES — L2J Mobius IA

Reglas persistentes de workflow del proyecto. Este archivo es la fuente canónica de reglas de proceso para el agente. Añadir aquí toda regla aprobada por el GM.

---

## R1 — Almacenamiento de informes de investigación (obligatorio)

Whenever you generate a substantive **research report**, **feature investigation report**, **fork/prior-art analysis**, **source discovery report**, **research plan**, or **research-result report**, you must save the final report as a Markdown (`.md`) file under:

`E:\L2J MOBIUS IA\investigacion features y forks`

### Rules

* The research folder is the historical archive of investigations.
* Save the report there automatically; do not merely print it in chat.
* Use a descriptive filename with the date, for example:

  * `INFORME_BOT_RESEARCH_2026-09-02.md`
  * `PLAN_B1_BOT_ROLE_PRESETS_2026-09-02.md`
  * `INFORME_B1_BOT_ROLE_PRESETS_RESULT_2026-09-02.md`

* Do not overwrite an existing report unless it is explicitly an update/version of that same report.
* Preserve the complete research report, including:
  * source findings
  * evidence/source anchors
  * prior-art findings
  * conclusions
  * assumptions or unknowns
  * implementation plan
  * test design
  * runtime results
  * failures and fixes
  * recipe decision
  * legacy-material decision
  * next research opportunities
* Keep research reports separate from the `RECETARIO`.
* Do NOT automatically convert research into a recipe.
* A recipe belongs in `RECETARIO` only after the corresponding Mobius-native capability has been implemented and validated according to the project workflow.
* If a research report is later superseded, keep the original and create a new dated report rather than silently deleting history.
* Before finishing any research task, verify that the `.md` report was actually written to that folder and report its exact path.

### Referencias de ejemplo en el archivo histórico

- `INFORME_BOT_RESEARCH_2026-09-02.md` — investigación global de forks vs Mobius (prior-art)
- `PLAN_B1_BOT_ROLE_PRESETS_2026-09-02.md` — plan de la misión B1 (perfiles de rol)