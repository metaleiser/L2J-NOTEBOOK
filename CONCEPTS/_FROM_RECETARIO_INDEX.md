# RECETARIO INDEX - L2J MOBIUS IA

> Indice canonico de recetas y referencias reutilizables. Actualizado 2026-09-02 (refactor R-BOT).

---

## RECETAS (recipes)

### Capacidad compartida (high-level, GM-facing)

| id | title | status | composes | evidence |
|----|-------|--------|----------|----------|
| **R-BOT** | Bot party companions (follow + assist + combat) - CAPACIDAD COMPARTIDA | validated (GM 2026-09-02) | REF-PLAYER-CLIENTLESS, REF-PARTY-AUTOPLAY, REF-SQL-PROVISION | java0.log 13:31-13:40; audit0.log 13:31:13; knowledge/21 |

### Fachadas de entrada (invocan R-BOT)

| id | title | status | composes | evidence |
|----|-------|--------|----------|----------|
| **R-BS001** | Chat command `//bs` (summon/dismiss party bots) | validated (GM 2026-09-02) | R-BOT, REF-BOOT | java0.log 13:31-13:40; knowledge/21 |
| **R-PREPMASTER** | NPC PrepMaster (talk to summon party bots) | validated (GM M5, 2026-08-31) | R-BOT, REF-BOOT | patch/0001; knowledge/05 |
| **R-GM-DASHBOARD** | Community Board bot squad panel | implemented (C0-C1; C2-C8 pending GM) | R-BOT, REF-BOOT | patch/0002 |

### Recetas independientes

| id | title | status | composes | evidence |
|----|-------|--------|----------|----------|
| **R-SRV001** | Server lifecycle (start/verify/stop/restart/reload) | validated (GM 2026-09-02) | REF-BOOT | java0.log boot 76-102s |
| **R-SP001** | Persistent NPC spawn at boot (datapack-only) | reviewed | REF-BOOT | 900105.xml runtime |

---

## REFERENCIAS (reusable technical references)

| id | purpose | status | used_by |
|----|---------|--------|---------|
| **REF-BOOT** | Server start/verify/shutdown lifecycle | validated | R-BS001, R-PREPMASTER, R-GM-DASHBOARD, R-SRV001, R-SP001 |
| **REF-PLAYER-CLIENTLESS** | Server-side Player (GameClient=null) lifecycle | verified | R-BOT |
| **REF-PARTY-AUTOPLAY** | Native Party + AutoPlay + AssistLeader substrate | verified | R-BOT |
| **REF-SQL-PROVISION** | SQL clone for bot character rows | verified | R-BOT |

---

## STATUS LEGEND

- `reviewed` - identificado reutilizable, chequeado source
- `validated` - PASS en runtime con evidencia (log + GM)
- `implemented` - desplegado; validacion parcial o pendiente GM
- `verified` - referencia revalidada contra source + runtime

---

## DEPENDENCY MAP

```
                    REF-BOOT
                       |
    ┌──────────────────┼──────────────────┐
    │                  │                  │
 R-SRV001           R-BS001           R-SP001
 (server lifecycle)  │                  (spawn NPC)
                     │
              ┌──────┴──────┐
              │    R-BOT    │ ← capacidad compartida
              │  (shared)   │
              └──────┬──────┘
                     │
         ┌───────────┼───────────┐
         │           │           │
   REF-PLAYER    REF-PARTY    REF-SQL
   -CLIENTLESS   -AUTOPLAY    -PROVISION

 R-BS001 ──→ R-BOT ←── R-PREPMASTER
 (chat)      ↑         (npc)
             │
        R-GM-DASHBOARD
           (CB panel)
```

---

*Fin de RECETARIO_INDEX.md - 6 recipes · 4 references. R-BOT = shared capability layer.*