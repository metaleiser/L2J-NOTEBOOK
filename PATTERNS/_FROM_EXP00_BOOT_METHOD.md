# BOOT — Método del Experimento 0 (`exp00/`)

> ⚠️ **Nota de procedencia**: este método fue **bootstrapped dentro de esta tarea** (2026-09-01).
> No existía `method/BOOT.md` en el workspace antes; existía `INTELIGENCIA_ARTESANAL_L2J/knowledge/BOOT_PROCEDURE.md`
> (procedimiento operativo de arranque del servidor, HISTORICAL/REQUIRES REVALIDATION) y la capa de contratos
> `contracts/00..08`. Este documento define el **método experimental de `exp00/`** reutilizando ambas fuentes;
> no las sustituye. Clasificación: RUNTIME_LOCAL (metodología de esta zona experimental).

- **status**: FACT (definido y usado en esta tarea)
- **type**: method / procedure
- **scope**: `exp00/` (zona experimental para convertir intención de producto → receta → implementación)
- **confidence**: verificada por uso (recetas B y A derivadas y re-verificadas con este método)

## 1. Cadena objetivo (criterio de éxito del Experimento 0)

```
GM INTENT → DISCOVER → UNDERSTAND → COMPOSE → VALIDATE → RECIPE
          → FRESH AGENT → RE-VERIFY → ADAPT → IMPLEMENT
          → //bs → PARTY DE BOTS → FOLLOW → ASSIST → COMBAT → PERSISTENCIA
```

## 2. Reglas del método

1. **La misión se expresa como intención de producto** (qué debe sentir/obtener el GM), nunca como
   instrucciones técnicas. La técnica se DERIVA del source, no se recibe.
2. **Antes de inventar código, determinar qué capacidades ya existen** y cómo se componen.
   Prioridad: (1) capacidad nativa → (2) composición de nativas → (3) punto de extensión existente →
   (4) prior-art verificable → (5) gap real → (6) extensión mínima.
3. **Toda capacidad se verifica contra el source UPSTREAM** con ancla `file:line` antes de entrar en una receta.
   El residuo local de spikes se admite como pista/prior-art, **jamás como parte nativa de Mobius**.
4. **Procedencia separada** (heredada de contracts/02): cada afirmación distingue
   `UPSTREAM SOURCE EVIDENCE` (source leído), `RUNTIME EXECUTION EVIDENCE` (observado en LIVE) y
   `CANONICAL DECISION` (decisión registrada).
5. **Clasificación obligatoria** de todo elemento:
   - `NATIVO_PLATAFORMA` — existe en UPSTREAM source; Mobius lo proporciona.
   - `RUNTIME_LOCAL` — decisión/estado de despliegue de este runtime (datapack edit, config, SQL de datos).
   - `RESIDUO_LOCAL` — restos de spikes/harness desechables; NO es nativo; NO es feature.
   - `EXTERNO` — dependencias fuera de Mobius (JDK, MySQL, cliente).
   - `DESCONOCIDO` — no verificado; se registra como límite, nunca se extrapolá.
6. **Receta = conocimiento transferible.** Debe permitir a un agente que NO participó en la investigación
   re-derivar la implementación. Mínimo: PROBLEM CLASS · NATIVE CAPABILITIES · COMPOSITION ·
   EXTENSION POINTS · ORDER OF OPERATIONS · CONSTRAINTS · VALIDATION · RE-DERIVATION · ADAPTATION.
7. **Fresh-agent pass (Fase 2)**: un agente lógicamente nuevo recibe SOLO la intención + `recipe.md` +
   source/runtime autorizado, re-verifica cada ancla contra el source, detecta información obsoleta,
   adapta la receta y decide el camino exacto. No se asume que una receta es correcta porque exista.
8. **Protocolo interactivo (runtime)**: nunca asumir servidor encendido ni GM conectado; pedir confirmación
   y esperarla antes de tocar runtime/DB o ejecutar pruebas interactivas (heredado de contracts/07).
9. **No ampliar alcance.** Un hallazgo arquitectónico faltante se REGISTRA; no dispara otra arquitectura.

## 3. Roles de entorno (contracts/01, verificados por Test-Path en esta tarea)

| Rol | Ruta |
|---|---|
| CANONICAL | `INTELIGENCIA_ARTESANAL_L2J/` (conocimiento + patches) |
| UPSTREAM (solo lectura) | `UPSTREAM/L2J_Mobius/L2J_Mobius_CT_2.6_HighFive/` (baseline e2518ab108) |
| LIVE RUNTIME | `L2J MOBIUS H5 SERVER/` (clean rebuild de e2518ab + datapack) |
| CLIENT | `Lineage2-TCT-273-client/` (validación visual GM) |
| Experimento | `exp00/` (este método, recetas, worklogs, archivos de sesión) |

## 4. Ciclo por slice

```
intención → DISCOVER (grep/read UPSTREAM + residuo como pista) → UNDERSTAND (anclas file:line)
→ COMPOSE (cadena de llamadas nativas) → VALIDATE (compilación estática / evidencia runtime existente)
→ recipe.md + worklog.md → FRESH AGENT re-verify → ADAPT → IMPLEMENT (patch staged) → runtime test interactivo
```

## 5. Separación harness vs substrate (lección central del Experimento 0)

Los spikes previos mezclaron dos cosas que la receta debe separar explícitamente:

| Comportamiento observado | Pertenece a | Evidencia |
|---|---|---|
| Carga de Player clientless, spawn, AutoPlay, party, assist, follow, combate real | **SUBSTRATE nativo** | D-0001 (knowledge/19), PartyAutoPlay (knowledge/20), source file:line |
| Ventana de 60s, detección de humano, observación 75s, teardown forzado, **shutdown programado** | **HARNESS desechable** | headers de BotSpike*.java (`Shutdown.startShutdown`), gates .ini |
| Aparición/desaparición "artificial" de bots | **HARNESS** (teardown del ciclo) | BotSpikeParty.runTeardown() |
| AutoPlay.ini en True sin dueño | **RESIDUO de config** | estado actual del runtime |
| `BotSpikeHumanEnabled = True` (gate abierto que auto-dispara spike + shutdown en el boot) | **RESIDUO activo (riesgo)** | `custom/BotSpikeHuman/BotSpikeHuman.ini` |

La feature limpia usa SOLO el substrate y elimina el harness del camino vivo.
