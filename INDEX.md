# L2J Notebook — Private Research

> ⚠️ **Private repository.** Not for public distribution.

This is the durable, self‑auditable memory of what we have learned about L2J Mobius High Five and Lineage 2 gameplay.

## Domain

- **Target**: L2J Mobius CT 2.6 HighFive @ `e2518ab108`
- **Workspace**: `E:\L2J MOBIUS IA`
- **Runtime**: `L2J_Mobius_CT_2.6_HighFive` (clean rebuild of e2518ab + datapack)

## Artifact Index

### Governance (reglas del Notebook)

| File | Estado | Vigencia | Baseline |
|------|--------|----------|----------|
| `NOTEBOOK_GOVERNANCE.md` (NOTEBOOK GATE: reglas de gobernanza) | VERIFIED | CURRENT | L2J_Mobius_CT_2.6_HighFive |
| `USER_MANUAL.md` (instrucciones pr�cticas de uso) | VERIFIED | CURRENT | L2J_Mobius_CT_2.6_HighFive |
| `INDEX.md` (este archivo, mapa de navegaci�n) | VERIFIED | CURRENT | L2J_Mobius_CT_2.6_HighFive |

### CLAIMS (granular atomic facts)

| File | Estado | Vigencia | Baseline |
|------|--------|----------|----------|
| `CLAIMS.md` (33 claims at�micos CL-0001..0028) | VERIFIED | CURRENT (0001..0028) / PENDING_REVERIFICATION (0024) | e2518ab108 � CL-0024 source @ 43ac8878f5 |

### Knowledge (domain artifacts)

| File | Estado | Vigencia | Baseline |
|------|--------|----------|----------|
| `knowledge/SOURCE_EVIDENCE.md` (evidencia primaria con file:line) | VERIFIED | EVIDENCE | e2518ab108 |
| `knowledge/CONSTRAINTS.md` (APIs seguras/inseguras) | VERIFIED | CURRENT | e2518ab108 |
| `knowledge/TEMP-PLAYER-TECHNICAL-MAP.md` (arquitectura no adoptada) | INFERRED | HISTORICAL | e2518ab108 |
| `knowledge/TEMPORARY-PLAYER-TECHNICAL-CONTRACT.md` (contrato no implementado) | PROPOSED | HISTORICAL | e2518ab108 |
| `knowledge/COMMUNITY_BOARD.md` (Community Board can�nico) | VERIFIED | CURRENT | L2J_Mobius_CT_2.6_HighFive |
| `knowledge/MOBIUS_INVESTIGATION_METHOD.md` (metodolog�a de investigaci�n) | VERIFIED | CURRENT | L2J_Mobius_CT_2.6_HighFive |
| `knowledge/DECISION_RULES.md` (reglas de decisi�n) | VERIFIED | CURRENT | L2J_Mobius_CT_2.6_HighFive |
| `knowledge/RECIPES_COMMUNITY_BOARD.md` (modelo Recipe) | VERIFIED | CURRENT | L2J_Mobius_CT_2.6_HighFive |
| `OPEN_QUESTIONS.md` (preguntas abiertas y pendientes) | INFERRED | CURRENT | L2J_Mobius_CT_2.6_HighFive |

### Decisions (ADRs)

| File | Estado | Vigencia |
|------|--------|----------|
| `decisions/ARCHITECTURE_DECISIONS.md` (13 ADRs Temporary Player) | VERIFIED | CURRENT |
| `decisions/_FROM_ROOT_ARCHITECTURE_DECISIONS.md` (duplicado) | VERIFIED | DUPLICATE → ARCHITECTURE_DECISIONS.md |
| `decisions/FASE4_BOT_ARCHITECTURE_FREEZE.md` (arquitectura bots FASE 4.18) | VERIFIED | CURRENT |

### Bots / Party / Clientless

| File | Estado | Vigencia | Baseline |
|------|--------|----------|----------|
| `bots/BOT_RECIPE.md` (arquitectura implementada de bots) | VERIFIED | CURRENT | e2518ab108 |
| `investigations/BOTAI-02_ROLES_BEHAVIORS_API_RESEARCH.md` (investigaci�n roles/behaviors) | VERIFIED + PROPOSED | CURRENT | e2518ab108 |
| `investigations/BOTAI-06-B0_AUTO_SKILLS_AUTO_BUFFS_PARTY_TARGETING_RESEARCH.md` (investigación `_autoSkills`/`_autoBuffs` + party heal/buff targeting) | VERIFIED + INFERRED | CURRENT | L2J_Mobius_CT_2.6_HighFive |
| `investigations/BOTAI-06-B_ROLE_PROFILES_MULTICLASS_RESEARCH.md` (especificación técnica de perfiles TANK/DPS_FIGHTER/MAGE/HEALER/SUPPORT: fichas por rol, matriz multiclass 5 roles × clases reales, Equipment/Skills/AutoSkills/AutoBuffs/AutoPlay/Targeting ↔ Role, limitaciones nativas (bloqueo BUFFS, DISABLED_AUTO_SKILLS=42=Sweeper solo voiced), huecos P1-P15 y propuestas PROPOSED; sin implementación) | VERIFIED + INFERRED + PROPOSED | CURRENT | L2J_Mobius_CT_2.6_HighFive |
| `investigations/BOTAI-P10_RUNTIME_GROUP_HEAL_PARTY_VALIDATION.md` (spike Group Heal 1027: pre-validacion estatica VERIFIED; P10-A PASS VERIFIED Run 2; P10-B PASS VERIFIED RUN 1 (AutoUse `_autoBuffs` ejecuta 1027 clientless en party; target = actual/self, sin seleccion por HP; PARTY cura a todos en radio; cierre 15/09: spikes P10A/P10B eliminados (hash en doc P10)) | VERIFIED (P10-A + P10-B) | CURRENT | L2J_Mobius_CT_2.6_HighFive |
| `ROUTING/bots.md` (routing bots/party/clientless) | VERIFIED | CURRENT | e2518ab108 |
| `ROUTING/cb.md` (routing Community Board) | VERIFIED | CURRENT | L2J_Mobius_CT_2.6_HighFive |
| `ROUTING/README.md` (�ndice de routing) | VERIFIED | CURRENT | L2J_Mobius_CT_2.6_HighFive |

### Investigations (dated reports)

| File | Estado | Vigencia |
|------|--------|----------|
| `investigations/MASTER_PROJECT_RETROSPECTIVE_2026-09-02.md` | VERIFIED | HISTORICAL |
| `investigations/INFORME_BOT_RESEARCH_2026-09-02.md` | VERIFIED | HISTORICAL |
| `investigations/INFORME_B1_BOT_ROLE_PRESETS_2026-09-02.md` | VERIFIED | HISTORICAL |
| `investigations/CLINE_KNOWLEDGE_PERSISTENCE_RETRIEVAL_2026-09-02.md` | VERIFIED | HISTORICAL |
| `investigations/INFORME_SERVER_LIFECYCLE_2026-09-03.md` | VERIFIED | CURRENT |
| `investigations/MICROINFORME_REF_BOOT_2026-09-03.md` | VERIFIED | HISTORICAL |
| `investigations/TEMPORARY-ARCHER-MVP.md` | PROPOSED | HISTORICAL |
| `investigations/FASE4_01_AUTOPLAY.md` (AutoPlay/AutoUse) | VERIFIED | CURRENT |
| `investigations/FASE4_02_PLAYER_CLIENTLESS.md` (Player sin cliente) | VERIFIED | CURRENT |
| `investigations/FASE4_03_PARTY.md` (Party system) | VERIFIED | CURRENT |
| `investigations/FASE4_04_COMMAND_CHANNEL.md` (Command Channel) | VERIFIED | CURRENT |
| `investigations/FASE4_05_FAKEPLAYER.md` (FakePlayer, referencia negativa) | VERIFIED | CURRENT |
| `investigations/FASE4_06_TEMPORARYPLAYER.md` (hip�tesis descartada) | REFUTED | HISTORICAL |
| `investigations/FASE4_07_BOT_AI.md` (estrategia BotAI) | VERIFIED + PROPOSED | CURRENT |
| `investigations/FASE4_09_FOLLOW_ASSIST_COMBAT.md` (Follow/Assist/Combat) | VERIFIED | CURRENT |
| `investigations/FASE4_17_CLAIMS_MATRIX.md` (claims matrix) | VERIFIED | CURRENT |
| `investigations/FASE4_17_CLIENTLESS_VISIBILITY.md` (visibilidad) | NOT_FOUND | PENDING_REVERIFICATION |
| `investigations/FASE4_17_PLAYER_CLIENT_STATE.md` (estado Player.load) | VERIFIED | CURRENT |
| `investigations/FASE4_18_GAMECLIENT_DETACHED.md` (decisi�n rechazada) | REFUTED | HISTORICAL |
| `investigations/FASE4_FINAL_REPORT.md` (informe final) | VERIFIED | CURRENT |
| `investigations/BOTAI-04-B2_MOBIUS_NATIVE_EQUIPMENT_SKILLS_INVESTIGATION.md` (equipment/skills nativos) | VERIFIED | CURRENT |
| `investigations/BOTAI-04-B3_LEVEL_EQUIPMENT_PROGRESSION_INVESTIGATION.md` (progresión por nivel) | VERIFIED | CURRENT |
| `investigations/BOTAI-05.1_REAL_PLAYER_ARCHITECTURE_VERIFICATION.md` (verificación arquitectura Real Player) | VERIFIED + CONDICIONAL | CURRENT |

| `investigations/BOTAI-06-ACT1_CONSOLIDATION.md` (consolidacion arquitectura equipamiento de bots: BotProfile/BotRole/BotEquipment/BotPresets; elimina heuristica enchant==4) | VERIFIED + BUILD | CURRENT |

| `investigations/BOTAI-06-C_EQUIPMENT_MASTER_CATALOG.md` (catalogo maestro de equipo del datapack TARGET: armor/weapons/jewelry/sets/grades/slots/tipos/IDs; 1067 armor equipables + 917 armas base + 261 jewelry + 217 sets; anomalias A1-A15) | VERIFIED | CURRENT |
| `investigations/BOTAI-06-D_SKILL_BUFF_COMBAT_MASTER_CATALOG.md` (catalogo maestro de skills/buffs/heals/combate/condiciones del datapack TARGET: 8144 skills en 95 XML; 17702 entradas de aprendizaje en 114 skill trees; 103 clases con classSkillTree; 162 efectos distintos; 932 skills aprendibles por clase + indice maestro; sistema nativo de condiciones (player hp<=, cp>=, using kind/slot, logicas and/or/not); Frenzy (id 176, hp=60) investigada sin implementar; SkillTreeData/rewardSkills/giveAvailableSkills; AutoPlay/AutoUse/PlayerAI; Scheme Buffer como precedente nativo; anomalias A1-A15) | VERIFIED | CURRENT |

### Architecture

| File | Estado | Vigencia |
|------|--------|----------|
| `ARCHITECTURE/README.md` (�ndice de arquitectura) | VERIFIED | CURRENT |
| `ARCHITECTURE/_FROM_ROOT_CONSTRAINTS.md` (duplicado candidato) | VERIFIED | DUPLICATE (candidato) |
| `ARCHITECTURE/_FROM_ROOT_MAPA_PLAYER_CLIENTLESS.md` (duplicado candidato) | INFERRED | DUPLICATE (candidato) |
| `ARCHITECTURE/_FROM_ROOT_TEMPORARY_PLAYER_CONTRACT.md` (duplicado candidato) | PROPOSED | DUPLICATE (candidato) |

### Known Traps

| File | Estado | Vigencia |
|------|--------|----------|
| `KNOWN_TRAPS/README.md` (trampas conocidas) | VERIFIED | CURRENT |
| `KNOWN_TRAPS/_FROM_BENCHMARK_RESULTS.md` (benchmark Qwen) | VERIFIED | EVIDENCE |

### Patterns

| File | Estado | Vigencia |
|------|--------|----------|
| `PATTERNS/OBJECTIVE_DRIVEN_METHOD.md` (m�todo objetivo-driven) | VERIFIED | CURRENT |

### Concepts (referencia externa)

| File | Estado | Vigencia | Evidence |
|------|--------|----------|----------|
| `CONCEPTS/FORKS_BOTS_CONCEPTS.md` (ideas de forks, no c�digo) | INFERRED | HISTORICAL | EXTERNAL |

### Evidence (immutable artifacts)

| File | SHA-256 |
|------|---------|
| `evidence/spikes/BotSpikeC02.class` | 22C7F44413F8... |
| `evidence/spikes/BotSpikeC03.class` | 86EAFBC3401A... |
| `evidence/spikes/D0001/` (5 files) | MANIFEST.md |
| `evidence/spikes/PartyAutoPlay/` | MANIFEST.md |
| `evidence/REF-BOOT-2026-09-03/` (REF-BOOT validation log extracts) | MANIFEST.md |

### Inbox (quarantine for external research)

Empty — no external research processed yet.

### Environment Binding

`env.local` (NOT committed) — see `env.local.template`

## Operating rules

1. **Write by change**: new or corrected knowledge only.
2. **One owner + pointers**: knowledge belongs in exactly one artifact.
3. **Status + baseline**: every artifact records its verification status and Mobius baseline.
4. **Evidence must survive**: imported at time of validation, never left in runtime only.
5. **External research ≠ truth**: not trusted until locally verified.
6. **Git is history**: never rewrite; append supersedes.

See [USER_MANUAL.md](./USER_MANUAL.md).

## Knowledge States (canonical)

| State | Definition |
|-------|------------|
| **VERIFIED** | Confirmed by source code, file, execution, or reliable evidence |
| **INFERRED** | Reasonable deduction from evidence, not directly verified |
| **PROPOSED** | Future design not yet implemented/verified |
| **EXPERIMENTAL** | Temporary test requiring validation |
| **NOT FOUND** | Explicitly searched but not found |

**Rules:**
- `PROPOSED != VERIFIED`
- `INFERRED != VERIFIED`
- `EXPERIMENTAL != VERIFIED`
- Never promote a hypothesis to VERIFIED by repetition.

## NOTEBOOK GATE

After every ACT task, evaluate if the result contains reusable knowledge:

1. Is there reusable technical knowledge?
2. Search INDEX.md and related documents.
3. Does the knowledge already exist?
4. If yes, update the authoritative document.
5. If no, create a new document only if necessary.
6. Update INDEX.md if applicable.

**Rule of doubt:** If there is reasonable doubt about whether something constitutes permanent knowledge, do NOT automatically create a new document. Record the doubt in the task report instead.

See [NOTEBOOK_GOVERNANCE.md](./NOTEBOOK_GOVERNANCE.md) for authoritative rules.
See [USER_MANUAL.md](./USER_MANUAL.md) for practical usage instructions.
