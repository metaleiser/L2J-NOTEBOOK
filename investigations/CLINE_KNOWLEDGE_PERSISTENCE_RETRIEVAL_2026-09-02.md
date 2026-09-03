# CLINE KNOWLEDGE PERSISTENCE & RETRIEVAL — L2J MOBIUS IA

> **Fecha**: 2026-09-02
> **Modo**: Plan (research-only; nothing modified)
> **Cline version verified**: 4.1.16
> **Workspace**: `E:\L2J MOBIUS IA`
> **Question investigated**: Can Cline itself remember/recover technical discoveries across tasks, or must discoveries ALWAYS be externalized into files?

---

## 1. CONTEXT — What information can Cline access?

### During an active task (VERIFIED):

| Source | Content | Mechanism |
|--------|---------|-----------|
| **Conversation history** | All user messages, assistant responses, tool calls, tool results | Working context (in-memory) |
| **System prompt** | Workspace config, tool definitions, mode rules, preferred language | Auto-loaded at task start |
| **Workspace files** | Any file in the workspace, read via `read_files` | Must be explicitly read |
| **Command output** | Results of `run_commands` | Must be explicitly executed |
| **Web content** | Results of `web_search`, `fetch_web_content` | Must be explicitly fetched |
| **Search results** | Results of `search_codebase` | Must be explicitly searched |

### Context window limits (VERIFIED from `~/.cline/data/globalState.json`):

- **Act mode**: 262,144 tokens context window, 32,768 max output
- **Plan mode**: 256,000 tokens context window, 23,040 max output
- **Model in use**: `cline-free/longcat-2.0` (act), `z-ai/glm-5.2:free` (plan)

### What survives when a task ends (VERIFIED):

| Survives? | Data | Location |
|-----------|------|----------|
| YES | Full message history (all conversations) | `~/.cline/data/sessions/<id>/<id>.messages.json` |
| YES | Compaction summaries (when context was full) | `~/.cline/data/sessions/<id>/<id>.compaction.json` |
| YES | Session metadata (start/end, model, cost, prompt) | `~/.cline/data/sessions/<id>/<id>.json` |
| YES | Global settings and provider config | `~/.cline/data/globalState.json`, `~/.cline/data/settings/` |
| NO | Working context (in-memory conversation) | Lost — not auto-loaded next task |
| NO | Tool call results | Lost — must re-read files |
| NO | Inferences, drafts, chain-of-thought | Lost — not persisted anywhere |

### What disappears or becomes inaccessible (VERIFIED):

- **Working context**: When a task ends, the conversation history remains on disk but is NOT automatically loaded into the next task's working context
- **Unwritten discoveries**: Any technical discovery not written to a file is lost when context is gone
- **Intermediate reasoning**: Chain-of-thought, drafts, failed assumptions — lost unless externalized

---

## 2. RETRIEVAL — How can Cline recover information?

### Automatic mechanisms (VERIFIED):

| Mechanism | What it retrieves | Availability |
|-----------|-------------------|--------------|
| **System prompt** | Workspace hint, tool definitions, mode rules | Every task — automatic |
| **Compaction injection** | Within-session summary when context is full | Only WITHIN a session that exceeded context limits |

### Explicit mechanisms (require action):

| Mechanism | What it retrieves | How to trigger |
|-----------|-------------------|----------------|
| `read_files` | Any file on disk | Explicit call |
| `run_commands` | Command output | Explicit call |
| `search_codebase` | Code search results | Explicit call |
| `web_search` | Web results | Explicit call |
| `fetch_web_content` | Web page content | Explicit call |

### Previous session access (VERIFIED):

- **99 sessions stored on disk** in `~/.cline/data/sessions/`
- **30 sessions have compaction files** (context was full)
- **I CAN read previous session files** using `read_files` — verified by reading a `.messages.json` file
- **But it is NOT automatic** — I must explicitly choose to read them
- **No search mechanism across sessions** — I would need to know which session to read

### Can Cline reliably retrieve information from previous tasks? (VERIFIED):

**NO — not reliably.**

- Previous sessions are accessible as files but NOT automatically loaded
- Session files are large (4MB+ for messages.json) — impractical to read entirely
- Compaction summaries are selective (files read + key findings) — not comprehensive
- No indexing or search across sessions
- **Conclusion**: We must assume persistence must be externalized

---

## 3. PERSISTENCE MECHANISMS — Complete inventory

### 3.1 Workspace files (VERIFIED)

| What | Where | Strengths | Limitations | Durable? |
|------|-------|-----------|-------------|----------|
| Any file in workspace | `E:\L2J MOBIUS IA\...` | Directly readable; versionable | Must be explicitly written; no auto-capture | YES if committed |

### 3.2 Project instructions/rules (VERIFIED)

| What | Where | Strengths | Limitations | Durable? |
|------|-------|-----------|-------------|----------|
| WORKFLOW_RULES.md | `E:\L2J MOBIUS IA\WORKFLOW_RULES.md` | Canonical workflow rules; read manually | Not auto-loaded into context | YES |
| .clinerules | (none found) | Would be auto-loaded if present | Not created in this project | N/A |
| System prompt | Auto-injected | Always available | Fixed size; can't store project knowledge | YES |

**Finding**: No `.clinerules` file exists. The system prompt only contains workspace hint ("L2J MOBIUS IA"), not project knowledge.

### 3.3 Task/context mechanisms (VERIFIED)

| What | Where | Strengths | Limitations | Durable? |
|------|-------|-----------|-------------|----------|
| Session metadata | `~/.cline/data/sessions/<id>.json` | Start/end time, model, cost, prompt | Not auto-loaded; no knowledge content | YES (as history) |
| Compaction summaries | `~/.cline/data/sessions/<id>.compaction.json` | Preserves key findings when context full | Only within-session; selective; not cross-task | Partial |
| Session history | `~/.cline/data/sessions/<id>.messages.json` | Full conversation record | Large; not auto-loaded; no search | YES (as archive) |

### 3.4 Notes/memory mechanisms (VERIFIED)

| What | Available? | Details |
|------|------------|---------|
| Cline memory tool | NO | No memory tool in available tools |
| Auto-save discoveries | NO | No mechanism to auto-capture discoveries |
| Task history search | NO | No tool to search across previous sessions |
| Context restoration | NO | No automatic loading of previous context at task start |

**Finding**: Cline 4.1.16 has NO memory mechanism available. No tool to save, search, or retrieve memories.

### 3.5 Git history (VERIFIED)

| What | Where | Strengths | Limitations | Durable? |
|------|-------|-----------|-------------|----------|
| Commit history | `INTELIGENCIA_ARTESANAL_L2J\.git` | Permanent; versioned; auditable | Only for that subfolder; not for research reports | YES |
| Commit messages | Same | Can encode discoveries in messages | Must be explicitly committed | YES |

**Finding**: `INTELIGENCIA_ARTESANAL_L2J` has 8 commits. The parent workspace `E:\L2J MOBIUS IA` is NOT a git repository. Research reports in `investigacion features y forks/` are NOT in git.

### 3.6 Research reports (VERIFIED)

| What | Where | Strengths | Limitations | Durable? |
|------|-------|-----------|-------------|----------|
| Research reports | `E:\L2J MOBIUS IA\investigacion features y forks\` | Historical archive of investigations | NOT in git; can be overwritten | If preserved |

### 3.7 RECETARIO (VERIFIED)

| What | Where | Strengths | Limitations | Durable? |
|------|-------|-----------|-------------|----------|
| Recipes | `E:\L2J MOBIUS IA\RECETARIO\` | Validated, composable capabilities | Only for implemented/validated features | YES |
| References | `E:\L2J MOBIUS IA\RECETARIO\REFERENCE\` | Reusable knowledge components | Only extracted when ≥2 recipes use it | YES |

### 3.8 Logs/Evidence (VERIFIED)

| What | Where | Strengths | Limitations | Durable? |
|------|-------|-----------|-------------|----------|
| Evidence | `INTELIGENCIA_ARTESANAL_L2J\evidence\` | Immutable artifacts with SHA-256 | Only in git-tracked subfolder | YES |
| Learning reports | `INTELIGENCIA_ARTESANAL_L2J\.ia\LEARNING_REPORTS\` | Structured task learnings | Only in git-tracked subfolder | YES |

### 3.9 Cline internal storage (VERIFIED)

| What | Where | Strengths | Limitations | Durable? |
|------|-------|-----------|-------------|----------|
| Session files | `~/.cline/data/sessions/` | Full history preserved | Not auto-loaded; no search; not project-knowledge | Archive only |
| Compaction files | `~/.cline/data/sessions/<id>.compaction.json` | Survives context overflow | Selective; not cross-task; no search | Partial |
| Global state | `~/.cline/data/globalState.json` | Settings persist | No project knowledge stored | N/A |

---

## 4. INFORMATION LOSS — Concrete scenarios

### Scenario A: Discovery during research, not written down
> "These bots currently only know skills 194 and 1322; before proceeding we should inspect what those skills do and what Skill.checkCondition validates."

**Risk**: If Cline discovers this but the task ends before writing it to a file — **LOST**

### Scenario B: Failed assumption discovered
> "I assumed FakePlayer could be subclassed, but it's actually an NPC representation, not a Player server-side."

**Risk**: If not written to `.ia/CLAIMS.md` or a research report — **LOST**

### Scenario C: Context compaction
When context fills up, compaction removes 398+ messages. Only a summary survives.

**Risk**: Detailed technical findings from early in a long session may be **LOST IN PRACTICE** (summary is selective)

### Scenario D: Task ends without learning report
**Risk**: All discoveries, failed attempts, reusable conclusions — **LOST**

### Scenario E: Cross-task knowledge needed
**Risk**: Cline cannot recall what it discovered in task N-1 when starting task N unless it was written to a file — **LOST**

### What CANNOT be preserved/retrieved reliably:

1. **Chain-of-thought reasoning** — never persisted
2. **Failed assumptions** — only if written to learning report
3. **Intermediate discoveries** — only if written to files
4. **Context from previous sessions** — accessible as files but not auto-loaded
5. **Tool call results** — lost when context is gone
6. **Compaction summaries** — selective, not comprehensive

---

## 5. DISCOVERY CAPTURE — Minimal practical mechanism

### Requirements:

- Avoid private chain-of-thought capture
- Preserve factual/verifiable discoveries
- Avoid duplicate knowledge
- Avoid creating unnecessary documentation
- Work naturally with current workflow
- Easy for Cline to execute consistently

### Proposed mechanism: "Discovery Checkpoint" (end-of-task)

At the end of every task, Cline appends a **Discovery Checkpoint** to the task's learning report or research report. Format:

```markdown
## DISCOVERY CHECKPOINT — [date]

### New Facts Discovered
- [FACT] [anchor/file:line] — [verifiable statement]
- [FACT] [anchor/file:line] — [verifiable statement]

### Assumptions Disproven
- [WAS ASSUMED] → [ACTUAL] [evidence]

### Reusable Knowledge
- [KNOWLEDGE_ITEM] → belongs in: [RECETARIO_REF / CLAIMS / research report / knowledge/]

### Open Questions
- [QUESTION] → [next step to resolve]
```

### Why this works:

1. **No chain-of-thought**: Only factual, anchored discoveries
2. **Verifiable**: Every fact has a source anchor
3. **No duplication**: Each item tagged with its proper destination
4. **Minimal**: Only discoveries, not a full report
5. **Natural**: Fits existing workflow (learning reports already required by contracts/04)
6. **Consistent**: Same format every time, easy to execute

### Execution trigger:

Cline runs the Discovery Checkpoint automatically at the end of every task, BEFORE the user ends the session. It requires no user prompting.

---

## 6. KNOWLEDGE DESTINATION — Where discoveries belong

| Discovery Type | Destination | When |
|----------------|-------------|------|
| Verifiable source fact | `.ia/CLAIMS.md` | After verification against source |
| Runtime observation | `evidence/` + `knowledge/` | After runtime validation |
| Reusable implementation pattern | `RECETARIO/REFERENCE/` | When ≥2 recipes need it |
| Validated capability | `RECETARIO/` (recipe) | After implementation + GM validation |
| Research finding | `investigacion features y forks/` | After research task |
| Workflow rule | `WORKFLOW_RULES.md` | When GM approves new rule |
| Architectural decision | `knowledge/` + ADR | When decision is made |
| Failed attempt | Learning report + `knowledge/` | Always (don't repeat mistakes) |
| Fork insight (prior-art) | Research report only | Never copy into RECETARIO |

### Decision tree:

```
Is it verified?
├── NO → research report (INFERRED)
├── YES → Is it reusable?
│   ├── NO → learning report only
│   └── YES → Is it implemented?
│       ├── NO → CLAIMS.md (VERIFIED) + knowledge/
│       └── YES → RECETARIO recipe + REFERENCE/
```

---

## 7. RETRIEVAL WORKFLOW — Minimal workflow for task start/end

### At the START of a task:

```
1. Read WORKFLOW_RULES.md (rules)
2. Read STATE.md (if exists in project)
3. Read relevant RECETARIO entries (if implementation task)
4. Read relevant research reports (if continuing research)
5. Identify what knowledge already exists BEFORE investigating
```

### At the END of a task:

```
1. Run Discovery Checkpoint (see §5)
2. Externalize discoveries to proper destination
3. Update learning report (required by contracts/04)
4. Verify files were actually written
5. Report exact paths to user
```

---

## 8. LIMITATIONS — Classification

### VERIFIED CLINE CAPABILITY:

| Capability | Evidence |
|------------|----------|
| Full session history persisted to disk | Verified: 99 sessions in `~/.cline/data/sessions/` |
| Context compaction when full | Verified: 30 compaction files exist |
| I can read previous session files | Verified: Successfully read a `.messages.json` file |
| No memory tool available | Verified: Tools list has no memory function |
| No auto-loading of previous context | Verified: System prompt contains no previous session data |
| No cross-session search | Verified: No tool for this |
| Context window: 262K tokens (act) | Verified: `globalState.json` shows `contextWindow: 262144` |
| Compaction removes 398+ messages | Verified: `messagesRemoved: 398` in compaction metadata |

### VERIFIED WORKSPACE CAPABILITY:

| Capability | Evidence |
|------------|----------|
| Files persist on disk | Verified: Standard filesystem |
| Git versioning (partial) | Verified: Only `INTELIGENCIA_ARTESANAL_L2J` is a git repo |
| Research reports NOT in git | Verified: Parent folder is not a git repository |
| No `.clinerules` file | Verified: Searched workspace, none found |

### INFERENCE (not directly verified):

- Compaction summaries may not capture all important details
- Reading a 4MB messages.json file is impractical for retrieval
- Session files may eventually be cleaned up (no retention policy observed)

### UNKNOWN / REQUIRES VALIDATION:

| Question | Status |
|----------|--------|
| Can session files be searched/indexed by Cline internally? | UNKNOWN — no tool visible |
| Is there a Cline memory feature in other versions? | UNKNOWN — this is 4.1.16 |
| Will session files persist indefinitely? | UNKNOWN — no retention policy observed |
| Can the workspace configuration inject project knowledge? | UNKNOWN — only a hint is shown |
| Can `.clinerules` be used to persist knowledge? | UNKNOWN — not tested, no file exists |

---

## 9. FINAL RECOMMENDATION — Minimal operating model for L2J IA RECIPE

### Core principle:

> **Cline does not remember. Externalize everything important.**

### Operating model:

1. **Every discovery goes to a file before the task ends**
2. **Use the Discovery Checkpoint format** (§5) at end of every task
3. **Route discoveries to their proper destination** (§6 decision tree)
4. **At task start, read existing knowledge before investigating**
5. **Trust only workspace files and git history** — nothing else persists reliably

### What to trust as durable project knowledge:

| Mechanism | Trust? | Why |
|-----------|--------|-----|
| Files in workspace | YES | Persistent on disk |
| Git commits | YES | Permanent history |
| RECETARIO | YES | Validated, composable |
| Research reports | YES | Historical archive (but not in git!) |
| Cline session files | PARTIAL | Archive, not auto-loaded |
| Cline working memory | NO | Lost when task ends |
| Cline "memory" | NO | Does not exist in this version |

### Single most important rule:

> **If Cline discovered it but didn't write it to a file, it doesn't exist.**

---

## TECHNICAL DISCOVERIES

This section captures concrete, verifiable discoveries made during this investigation that could be useful later.

### Discovery 1: Cline version and storage location
- **Fact**: Cline 4.1.16 stores session data in `%USERPROFILE%\.cline\data\`
- **Anchor**: `C:\Users\Metaleiser\.cline\data\` (verified exists)
- **Use**: This is where session history lives; can be accessed for forensic recovery of discoveries

### Discovery 2: Session file structure
- **Fact**: Each session has 2-3 files: `<id>.json` (metadata), `<id>.messages.json` (full history), `<id>.compaction.json` (if context was full)
- **Anchor**: `~/.cline/data/sessions/<id>/` (verified)
- **Use**: Previous conversations CAN be read but are large (4MB+)

### Discovery 3: 99 sessions archived
- **Fact**: 99 sessions stored on disk
- **Anchor**: `Get-ChildItem ~/.cline/data/sessions | Measure-Object` → Count: 99
- **Use**: Historical record exists but is not searchable/indexed

### Discovery 4: Context compaction removes 398+ messages
- **Fact**: When context fills up, compaction removes hundreds of messages; only a summary survives
- **Anchor**: Compaction file shows `messagesRemoved: 398`
- **Use**: In long tasks, early discoveries may be lost even within the same session

### Discovery 5: No memory mechanism in Cline 4.1.16
- **Fact**: No memory tool, no auto-loading of previous context, no cross-session search
- **Anchor**: Verified tools list (6 tools, none related to memory)
- **Use**: Do NOT rely on Cline to remember anything between tasks

### Discovery 6: No .clinerules file in workspace
- **Fact**: No `.clinerules` file exists in the workspace
- **Anchor**: Searched `E:\L2J MOBIUS IA` for `.clinerules`, `cline*`, `.cline` — none found
- **Use**: Could be created to provide persistent project instructions (auto-loaded by Cline)

### Discovery 7: Research reports NOT in git
- **Fact**: `investigacion features y forks/` is NOT in a git repository
- **Anchor**: `E:\L2J MOBIUS IA` is not a git repo (only `INTELIGENCIA_ARTESANAL_L2J` is)
- **Use**: Research reports could be lost if disk fails; consider backing up

### Discovery 8: Workspace configuration is minimal
- **Fact**: The workspace configuration in the system prompt only contains a hint ("L2J MOBIUS IA"), not project knowledge
- **Anchor**: System prompt shows `hint: "L2J MOBIUS IA"` only
- **Use**: Could be expanded to include project knowledge (if supported)

### Discovery 9: Compaction summary structure
- **Fact**: Compaction summaries contain: Goal, State, Highlights, Next steps, Files read, Recent messages
- **Anchor**: Verified by reading `.compaction.json` files
- **Use**: Compaction summaries ARE a form of knowledge preservation within a session

### Discovery 10: Hooks log only shows shutdown events
- **Fact**: `~/.cline/data/logs/hooks.jsonl` only contains `session_shutdown` events
- **Anchor**: Read hooks.jsonl, all entries are shutdown events
- **Use**: No hidden knowledge preservation mechanism in hooks

### Discovery 11: Global state contains model config, not knowledge
- **Fact**: `globalState.json` contains Cline version, model settings, UI preferences — no project knowledge
- **Anchor**: Verified by reading `globalState.json`
- **Use**: Cannot use global state to persist project discoveries

### Discovery 12: I can read previous session files
- **Fact**: Cline's `read_files` tool CAN read files from `~/.cline/data/sessions/`
- **Anchor**: Successfully read `1788098743530_vo9do.messages.json`
- **Use**: Previous discoveries CAN be recovered if you know which session to read

---

## What information CANNOT be preserved/retrieved reliably:

1. **Chain-of-thought reasoning** — never persisted anywhere
2. **Discoveries not written to files** — lost when context is gone
3. **Tool call results from previous sessions** — must re-execute
4. **Context from previous sessions** — accessible as files but NOT auto-loaded
5. **Intermediate drafts and failed attempts** — lost unless in learning report
6. **Compaction summaries are selective** — may miss important details
7. **Session files are not indexed** — no search, no automatic retrieval

## What MUST be externalized:

- Every verified fact → `.ia/CLAIMS.md` or `knowledge/`
- Every failed assumption → learning report
- Every reusable pattern → `RECETARIO/REFERENCE/`
- Every research finding → `investigacion features y forks/`
- Every architectural decision → `knowledge/` + ADR
- Every workflow rule → `WORKFLOW_RULES.md`

---

## END OF RESEARCH REPORT

**Verified**: Cline version 4.1.16, 99 sessions stored, no memory mechanism, compaction removes 398+ messages, research reports not in git

**Unknown**: Whether `.clinerules` can persist knowledge, whether session files can be searched internally, retention policy for session files

**Recommended next action**: Review and approve the Discovery Checkpoint mechanism (§5) for implementation in WORKFLOW_RULES.md

*Report saved: 2026-09-02*
