# .clinerules — L2J Project

## 1. PROJECT IDENTITY

This workspace contains two independent products:

**L2J-RECIPE** — public, portable, validated L2J Mobius capabilities.
**L2J Notebook** — private research laboratory and durable knowledge memory.

Plus supporting directories:
- `UPSTREAM` — Mobius source (read‑only)
- `L2J MOBIUS H5 SERVER` — runtime server
- `Lineage2-TCT-273-client` — game client
- `others` — historical archive (read‑only, harvest selectively)

---

## 2. REPOSITORY ROLES

| Product | Git | Content | Public? |
|---------|-----|---------|---------|
| L2J-RECIPE | `E:\L2J MOBIUS IA\L2J-RECIPE` | validated recipes, references, patches | YES |
| L2J Notebook | `E:\L2J MOBIUS IA\L2J Notebook` | claims, knowledge, decisions, evidence, investigations, inbox | NO |

---

## 3. RETRIEVAL ORDER

For a GM question or task:

1. **Public recipes first** — if it is about a known capability, check L2J‑RECIPE.
2. **Private knowledge** — if more depth is needed, check L2J Notebook (INDEX → domain artifact).
3. **Trust assessment** — classify what you find into:

| Class | Meaning | Action |
|-------|---------|--------|
| SUFFICIENT | status ≥ VERIFIED-SOURCE on current baseline | Answer from it. |
| PARTIAL | mechanism known but specific value/config missing | Investigate only that piece. |
| STALE | baseline changed / invalidated | Re‑verify before using. |
| CONTRADICTORY | two sources disagree | Source/runtime is the arbiter. |
| LOW CONFIDENCE | only INFERRED/HYPOTHESIS available | Answer with caveat. |
| ABSENT | nothing in either repo | Full investigation needed. |

4. **Source investigation** — only the relevant source files, not a full re‑archaeology.
5. **Web/external** — only when it adds material value; treat as HYPOTHESIS until locally verified.

---

## 4. TRUST / PROVENANCE

- Source evidence (file:line) outranks inference.
- Runtime evidence outranks source inspection.
- GM validation outranks everything.
- External research (ChatGPT, web, forks) is NOT truth — it goes to `inbox/`, not into trusted knowledge.
- Never silently promote external claims.

---

## 5. WRITE‑BY‑CHANGE

Do NOT document merely because an investigation happened.

Write only when there is durable value:
- new knowledge
- corrected knowledge
- important evidence
- meaningful decision
- validated capability
- invalidation/staleness
- useful recipe

Avoid: daily diaries, mandatory post‑task reports, duplicate summaries, derived state files.

---

## 6. VALIDATION

Every implementation must include validation. For runtime features:
- Checklist with PASS/FAIL criteria.
- Evidence captured **at the moment** (log extracts, screenshots, GM confirmation).
- Evidence imported into L2J Notebook/evidence/ (never left only in rotation logs).
- Teardown/rollback tested.

---

## 7. PROMOTION TO PUBLIC

A capability becomes a public recipe only when the GM orders it after passing:

[ ] implementation is reproducible
[ ] deployment/use is reproducible
[ ] rollback/removal is understood
[ ] runtime validation passed
[ ] current baseline is known
[ ] useful to an external GM

Promotion is NOT automatic.

---

## 8. STOP CONDITIONS

Stop and report when:
- an architectural blocker is found
- requirements conflict
- source behaviour contradicts current design
- persistence or clientless safety cannot be established
- the requested change requires a substantially different architecture
- unrelated problems are discovered
- the `others/` directory is about to be modified (DON'T)

Do NOT silently improvise around these conditions.

---

## 9. FINAL PRINCIPLE

```
UNDERSTAND MOBIUS
     ↓
REUSE MOBIUS
     ↓
ADAPT MOBIUS MINIMALLY
     ↓
ADD ONLY WHAT IS MISSING
     ↓
VALIDATE
     ↓
PRESERVE KNOWLEDGE IN THE CORRECT REPOSITORY
```

---

## 10. `others/` RULE

The `others/` directory is a READ‑ONLY historical archive.
Do NOT modify it during normal work.
Harvest useful material selectively into L2J‑RECIPE or L2J Notebook,
preserving provenance and distinguishing verified from historical/inferred.