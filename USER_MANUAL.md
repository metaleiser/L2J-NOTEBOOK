# USER MANUAL — L2J Notebook

*For the GM and any future Cline/agent who needs to understand what we know, why we know it, and what remains uncertain.*

---

## 1. What L2J Notebook is

L2J Notebook is the **private research laboratory and durable memory** for understanding L2J Mobius High Five. It preserves discoveries, source archaeology, evidence, decisions, and unresolved questions — regardless of whether they ever become a public L2J Recipe.

## 2. Why it exists

Cline (and any AI assistant) has no inherent memory between sessions. Without an external knowledge system, every discovery made in one session is lost for the next. This notebook prevents repeated archaeology and accumulated forgetting.

## 3. Directory structure

| Directory | What goes there |
|-----------|-----------------|
| `knowledge/` | Durable per‑domain knowledge (Mobius mechanisms, gameplay, config, lifecycle) |
| `decisions/` | Architecture Decision Records (why X was chosen over Y) |
| `investigations/` | Dated reports when a research effort produced durable findings |
| `inbox/` | Quarantine for external research (ChatGPT, web, forks) — not trusted yet |
| `evidence/` | Immutable log extracts, spike sources, MANIFEST with SHA‑256 |
| `INDEX.md` | Single‑line overview of every artifact |
| `CLAIMS.md` | Atomic claims table — the most granular knowledge |

## 4. How to search / retrieve knowledge

1. Start with **INDEX.md** — each domain has one entry with status and date.
2. Open the domain file in `knowledge/` or `investigations/`.
3. Check the header: **status** (VERIFIED-SOURCE / VERIFIED-RUNTIME / etc.) and **baseline**.
4. For atomic facts, query **CLAIMS.md** by claim ID.

## 5. How `inbox/` works

External research (ChatGPT, web search, GitHub, forums) arrives as Markdown in `inbox/`. Each file has status `EXTERNAL` and origin metadata.

**Workflow**:
1. Cline reads inbox content.
2. Cline extracts claims that are technologically load‑bearing.
3. Cline verifies each claim against local source or runtime.
4. Result: VERIFIED (becomes knowledge) / REFUTED (marked as such) / HYPOTHESIS (insufficient evidence) / PARKED (interesting but not actionable).
5. The inbox file is **never modified** — it preserves the original external research.

## 6. How claims / statuses work

`CLAIMS.md` is a single append‑only table. Each claim has:

- **ID**: `CL-NNNN` (sequential)
- **Claim**: what is asserted
- **Status**: VERIFIED-SOURCE / VERIFIED-RUNTIME / GM-VALIDATED / INFERRED / HYPOTHESIS / REFUTED / STALE / SUPERSEDED / PARKED
- **Baseline**: which Mobius commit this applies to
- **Evidence**: class of evidence (SOURCE file:line / RUNTIME log / GM session / DECISION)
- **Supersedes**: if this claim replaces an earlier one

## 7. How evidence is recorded

Evidence is **imported** at the moment of validation. Never left in runtime only (logs rotate, files get deleted).

An evidence file in `evidence/` always includes:
- The original artifact (log extract, Java source, SQL, config, etc.)
- A MANIFEST with SHA‑256 checksum
- Provenance metadata (date, baseline, who validated, how)

## 8. How investigations work

A dated report under `investigations/YYYY-MM-DD_<topic>.md` is created only when the investigation produced **durable knowledge** — not as a diary for every action.

Reports include:
- What was investigated and why
- What was discovered (with evidence anchors)
- What was refuted
- What remains unresolved
- Confidence assessment per finding
- Next step if further work is needed

## 9. How `PARKED` investigations resume

An investigation with status `PARKED` includes a "Next step" section in its header or last section. When resumed, Cline reads the parked file, checks the baseline, and continues from the next step.

## 10. How decisions are recorded

When a significant X‑vs‑Y decision is made, an ADR (Architecture Decision Record) is placed under `decisions/`. Format:

```
ADR-NNN: Title
Status: ACCEPTED / PROPOSED / SUPERSEDED
Context: what problem was being solved
Decision: what was chosen and why
Alternatives rejected: what was considered and why not
Consequences: what this enables or prevents
```

## 11. How baseline changes affect knowledge

When the authoritative Mobius baseline changes:

- Every artifact with that baseline becomes STALE
- Do NOT silently treat old knowledge as current
- Re‑verify affected anchors before reusing the knowledge
- This is a deliberate, not automated, process

## 12. How knowledge becomes a public recipe

Promotion follows a lightweight gate (see L2J‑RECIPE CONVENTION.md). The Notebook preserves the pre‑promotion evidence and decisions. The recipe itself lives in L2J‑RECIPE.

## 13. What Cline is responsible for

- Maintaining this repository (writing claims, updating knowledge, importing evidence)
- Keeping INDEX.md current
- Processing inbox
- Recording decisions in the moment
- Ensuring evidence is captured during validation
- Re‑anchoring knowledge when baseline changes (on GM order)

## 14. What the GM is responsible for

- Deciding what is promoted to the public recipe
- Setting the authoritative baseline
- Performing in‑game validation
- Ordering investigation priorities
- Final approval on architectural decisions

## 15. What L2J Notebook deliberately does NOT try to become

- A giant L2J encyclopaedia
- A Mobius source mirror
- A vector database or search service
- A dashboard platform
- A replacement for Git history
- A diary of every conversation
- A trivia collection

## 16. Git synchronization & release flow (RECIPE + NOTEBOOK)

Operational flow for promoting knowledge across the two repositories. Each repository has its own independent Git history.

```
WORK
→ KNOWLEDGE CANDIDATE      (write into the private notebook: claim / evidence / investigation)
→ HUMAN REVIEW             (GM decides: promote / park / discard — no push yet)
→ RECIPE / NOTEBOOK UPDATE (apply the approved change per CONVENTION / USER_MANUAL)
→ git diff review          (show `git status` + `git diff` per repo)
→ COMMIT                   (one commit per logical change, per repo)
→ PUSH                     (after explicit human review; NEVER automatic)
→ other PC                 (clone or pull, then verify)
```

Rules:
- One repo = one independent Git history (L2J-RECIPE public, L2J-NOTEBOOK private).
- Never `--force`; never overwrite remote work.
- On divergence: `pull` / `rebase` first, review, then push.
- `push` only after explicit human review and approval.
- Cline prepares changes and can stage/commit on approval, but must NOT make push an automatic action.
- Keep the public/private separation: only GM-approved content goes to L2J-RECIPE; no private paths, real IDs, or credentials there.

(The public repo does not duplicate this section; its promotion gate lives in L2J-RECIPE `USER_MANUAL.md §10` / `CONVENTION.md`.)