# L2J Notebook â€” Private Research

> âš ï¸ **Private repository.** Not for public distribution.

This is the durable, selfâ€‘auditable memory of what we have learned about L2J Mobius High Five and Lineage 2 gameplay.

## Domain

- **Target**: L2J Mobius CT 2.6 HighFive @ `e2518ab108`
- **Workspace**: `E:\L2J MOBIUS IA`
- **Runtime**: `L2J_Mobius_CT_2.6_HighFive` (clean rebuild of e2518ab + datapack)

## Artifact Index

### CLAIMS (granular atomic facts)

| File | Status | Baseline |
|------|--------|----------|
| `CLAIMS.md` | 23 claims (CL-0001..0023) | e2518ab108 |

### Knowledge (domain artifacts)

| File | Status | Baseline |
|------|--------|----------|
| `knowledge/SOURCE_EVIDENCE.md` (Temporary Player source investigation) | VERIFIED-SOURCE | e2518ab108 |
| `knowledge/CONSTRAINTS.md` (safe/unsafe API catalogue) | VERIFIED-SOURCE | e2518ab108 |
| `knowledge/TEMP-PLAYER-TECHNICAL-MAP.md` (Temporary Player technical map) | INFERRED | e2518ab108 |
| `knowledge/TEMPORARY-PLAYER-TECHNICAL-CONTRACT.md` (Temporary Player design contract) | DESIGN DECISION | e2518ab108 |
| `OPEN_QUESTIONS.md` (verification gaps and runtime test plan) | â€” | e2518ab108 |

### Decisions (ADRs)

| File | Status |
|------|--------|
| `decisions/ARCHITECTURE_DECISIONS.md` (13 ADRs + rejected alternatives) | ACCEPTED |

### Investigations (dated reports)

| File | Date | Status |
|------|------|--------|
| `investigations/MASTER_PROJECT_RETROSPECTIVE_2026-09-02.md` | 2026-09-02 | READ-ONLY |
| `investigations/INFORME_BOT_RESEARCH_2026-09-02.md` | 2026-09-02 | COMPLETE |
| `investigations/INFORME_B1_BOT_ROLE_PRESETS_2026-09-02.md` | 2026-09-02 | IMPLEMENTED / EXECUTION PENDING |
| `investigations/CLINE_KNOWLEDGE_PERSISTENCE_RETRIEVAL_2026-09-02.md` | 2026-09-02 | COMPLETE |
| `investigations/INFORME_SERVER_LIFECYCLE_2026-09-03.md` | 2026-09-03 | COMPLETE / RUNTIME VERIFIED |
| `investigations/TEMPORARY-ARCHER-MVP.md` | 2026-09-01 | NOT IMPLEMENTED |

### Evidence (immutable artifacts)

| File | SHA-256 |
|------|---------|
| `evidence/spikes/BotSpikeC02.class` | 22C7F44413F8... |
| `evidence/spikes/BotSpikeC03.class` | 86EAFBC3401A... |
| `evidence/spikes/D0001/` (5 files) | MANIFEST.md |
| `evidence/spikes/PartyAutoPlay/` | MANIFEST.md |
| `evidence/REF-BOOT-2026-09-03/` (REF-BOOT validation log extracts) | MANIFEST.md |

### Inbox (quarantine for external research)

Empty â€” no external research processed yet.

### Environment Binding

`env.local` (NOT committed) â€” see `env.local.template`

## Operating rules

1. **Write by change**: new or corrected knowledge only.
2. **One owner + pointers**: knowledge belongs in exactly one artifact.
3. **Status + baseline**: every artifact records its verification status and Mobius baseline.
4. **Evidence must survive**: imported at time of validation, never left in runtime only.
5. **External research â‰  truth**: not trusted until locally verified.
6. **Git is history**: never rewrite; append supersedes.

See [USER_MANUAL.md](./USER_MANUAL.md).
