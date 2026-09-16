# BOTAI-P10 — RUNTIME GROUP HEAL / PARTY HEAL VALIDATION (Skill 1027)

> **SPRINT:** BOTAI-P10
> **FASE:** Spike de validación runtime (Caso A / Caso B)
> **MODO:** INVESTIGACIÓN + DOCUMENTACIÓN (sin cambios funcionales)
> **Estado:** P10-A PASS — VERIFIED (cast directo clientless 1027, Run 2) / P10-B PASS - VERIFIED (RUN 1, AutoUse _autoBuffs)
> **Fecha:** 2026-09-15
> **TARGET:** `C:\L2J MOBIUS IA\L2J_Mobius_CT_2.6_HighFive`
> **UPSTREAM:** NO tocado (solo contraste heredado de B0)
> **Baseline:** L2J Mobius CT 2.6 HighFive @ e2518ab108 (runtime)

Leyenda: **VERIFIED** | **INFERRED** | **PROPOSED** | **NOT FOUND** | **PENDING**

> **Nota de alcance:** esta sesión completa la pre-validación estática
> (skill XML + handler + cadena de casteo + gates clientless) con evidencia
> VERIFIED del TARGET. La ejecución runtime en servidor vivo queda PENDING [HISTORICO - superseded: PASS] con
> protocolo cerrado, porque ejecutarla exigiría build/deploy/restart o código,
> ambos fuera de alcance (regla de seguridad del spike; reglas §13 y §8).
> Cero archivos funcionales modificados. Cero RoleStrategy/BotAI/ThinkLoop.

## 1. Objetivo

Responder con evidencia, sin implementar nada:

> ¿Puede un `Player` bot clientless real (`_client == null`, persistido en DB,
> en Party real) ejecutar **Group Heal 1027** y que Mobius aplique el efecto a
> los miembros de la Party?

| Pregunta | Estado al cierre |
|---|---|
| A. ¿1027 funciona en Player clientless real? | PENDING runtime; estático VERIFIED (nada exige cliente) |
| B. ¿A quién cura? | INFERRED (caster + miembros en radio); PENDING runtime |
| C. ¿Necesita target explícito? | VERIFIED estático: NO |
| D. ¿Funciona dentro de Party? | INFERRED (requiere isInParty + radio); PENDING runtime |
| E. ¿Diferencia directa vs AutoUse/AutoPlay? | Diseño A/B listo; ejecución PENDING |

## 2. Hipótesis

1. **H1 (mecánica):** 1027 (`targetType=PARTY`, `affectRange=1000`, efecto
   `Heal`, `operateType=A1`) resuelve vía `handlers.skill.targets.Party` →
   caster + miembros en radio → sin target explícito. [INFERRED]
2. **H2 (clientless):** ningún eslabón exige `getClient() != null`; gates
   AutoUse/AutoPlay aceptan `isOfflinePlay=true`. [INFERRED]
3. **H3 (Caso B):** 1027 → `_autoBuffs` (`hasNegativeEffect()==false`),
   cadencia reuse 6 s + tick 300 ms, sin umbral HP; BUFFS salta ticks con
   `isCastingNow/isAttackingNow`. [mecanismo VERIFIED / cadencia INFERRED]

## 3. Entorno

| Elemento | Valor | Estado |
|---|---|---|
| TARGET datapack | `L2J_Mobius_CT_2.6_HighFive\game\data\...` leido directo | VERIFIED |
| Skill XML 1027 | `game\data\stats\skills\01000-01099.xml:1402-1438` | VERIFIED |
| Handler PARTY | `game\data\scripts\handlers\skill\targets\Party.java:32-91` | VERIFIED |
| Handler ONE (contraste) | `...targets\One.java:35-46` | VERIFIED |
| Registro handlers | `MasterHandler.java` (via B0 seccion 10) | VERIFIED (cita B0) |
| Servidor vivo / GM / Party real | NO instanciados (start/deploy fuera de alcance) | PENDING |

## 4. Player utilizado

**Runtime futuro (protocolo, no ejecutado):** `Player.load(charId)` (p. ej.
`BSBOT01/268483130`, `BOT_RECIPE.md` seccion 6), `getClient()==null`,
`setOnlineStatus(true,false)`, `setOfflinePlay(true)`, `spawnMe(...)` —
patron RUNTIME VERIFIED de `BotSpikeD01`/`BotSpikeParty`. Clase con 1027:
ramas healer (06-D seccion 5.7: clases `10,15,25,29,38,42`, niveles
`14,20,25,30,35`). [seleccion PROPOSED; patron VERIFIED]

**Esta sesion:** ningun Player cargado/modificado. [VERIFIED por ausencia]

## 5. Estado clientless

* Patron clientless load-null-online-offlinePlay-spawnMe: RUNTIME VERIFIED
  (D0001/PartyAutoPlay, B0 seccion 18, BOT_RECIPE secciones 2-3). [VERIFIED]
* Gate AutoUse/AutoPlay: `!isOnline() || (isInOfflineMode() &&

## 6. Party

```java
// Party.java:38-81 (VERIFIED lectura)
targetList.add(creature);                  // caster siempre entra
if (onlyFirst) return targetList;          // onlyFirst=true → SOLO caster
final int radius = skill.getAffectRange(); // 1000 para 1027
if (creature.isInParty()) {
    for (Player partyMember : creature.getParty().getMembers()) {
        if ((partyMember == null) || (partyMember == player)) continue;
        if (Skill.addCharacter(creature, partyMember, radius, false))
            targetList.add(partyMember);
        if (Skill.addSummon(creature, partyMember, radius, false))
            targetList.add(partyMember.getSummon());
    }
}
```

* Sin party la lista = solo caster (+summon). Con party, cada miembro entra
  solo si `Skill.addCharacter(...)` lo acepta (core congelado). [INFERRED]
* Protocolo runtime: lider-humano + bot-healer (+ 1 extra para distinguir
  cura-a-party de cura-a-self), todos dentro de 1000, `isInParty()` true.
  [PROPOSED]

## 7. Skill 1027 — ficha VERIFIED del TARGET


## 8. Metodo de prueba (PENDING, dos casos separados)

Caso A — mecanica directa (PRIORITARIO): cargar bot, party real, verificar
`getKnownSkill(1027)`, bajar HP a >=1 miembro, ejecutar directo
`doCast(skill)` o `useMagic(skill,true,false)` SIN AutoPlay/AutoUse, registrar
inicio/fin (hitTime 7 s), HP antes-despues por miembro, quien recibio efecto,
repetir >=2 veces.

Caso B — via AutoUse/AutoPlay (SEPARADO, tras A): `getAutoBuffs().add(1027)`
+ `startAutoUseTask`, healer sin accion 2; observar cadencia (reuse 6 s +
tick 300 ms), sin umbral HP, bloqueos `isCastingNow/isAttackingNow`.

Spike temporal desechable estilo D01/Party, cero-writes, eliminado al final.
En esta sesion no se creo ninguno. [VERIFIED ausencia]

## 9. Resultados

**Ejecucion runtime: PENDING — no ejecutada** (servidor no levantado).
Veredicto al cierre: **PARTIAL** (estatica PASS; runtime PENDING).

## 10. HP antes/despues

PENDING (plantilla): caster / lider / extra → HP antes, despues, delta,
efecto (si/no), distancia al caster. Sin datos observados en esta sesion.

## 11. Targeting

* PARTY ignora el target actual salvo `onlyFirst`; self-cast suficiente.
* Caster siempre incluido; miembros por loop + `addCharacter` (radio 1000);
  summons por `addSummon`. [VERIFIED estructura; interior addCharacter PENDING]
* ONE exige target (contraste VERIFIED). 1027 no. Seleccion por HP del aliado:
  NOT FOUND (B0 seccion 9). Via BUFFS basta truco self-cast. [INFERRED]

## 12. AutoUse / AutoPlay (solo estatico)

| Aspecto | Estado |
|---|---|
| 1027 → `_autoBuffs` (Heal positivo) | VERIFIED (mecanismo) |
| Gates BUFFS / sin umbral HP / `canCastBuff` | VERIFIED (heredado B0 sec 5) |
| hitTime 7 s bloquea BUFFS; reuse 6 s + tick 300 ms cadencia | INFERRED |
| `castRange` default (ausente en XML) | PENDING |
| Healer sin accion 2 | VERIFIED mec / PROPOSED comp |

## 13. Evidencia

Directa TARGET (esta sesion): `01000-01099.xml:1402-1438` (ficha 1027);
`targets\Party.java:32-91`; `targets\One.java:35-46`. Heredada VERIFIED:
B0 4/5/6/7/9/10/11/19; 06-D 5.7; 06-B sec 9/P10; BOTAI-05.1 sec 6-7;
D01/PartyAutoPlay; FASE4 freeze. Ausente: logs/HP/trazas vivo. [PENDING]

## 14. Conclusion

1. Estatica: PASS — cadena 1027→PARTY sin cliente/target/seleccion.
   [VERIFIED/INFERRED]
2. Runtime: PENDING — A–E cierran solo en vivo (A primero, B despues).
3. Descarte futuro si falla (PROPOSED): (i) sin 1027/sin MP; (ii) sin party;
   (iii) fuera de 1000/`addCharacter`; (iv) casteo 7 s / ataque bloquea BUFFS;
   (v) paz / reuse.
4. Sin impacto: no crea RoleStrategy/healer-AI. [VERIFIED alcance]

## 15. Limitaciones

Sin vivo: sin HP/logs/`addCharacter` en vivo. Core congelado no tocado.
`castRange`/`onlyFirst` finos PENDING. Índice clases citado no regenerado.
Nada INFERRED = VERIFIED runtime.

## 16. Proximo paso

1. Caso A en laboratorio, rellenar 9-10, cerrar A–D. 2. Caso B, cerrar E.
3. Volcar a 06-B P10 y B0 sec 17-p1. 4. NO RoleStrategy/healer-AI aún.

## P10-A — Runtime Direct Cast (2026-09-15)

> **Resultado: PASS — VERIFIED.** Evidencia principal: Run 2 (09:31→09:35).
> Complementaria: Run 3 (09:39→09:44). Cierre documental + spike desarmado:
> `BOTAI-P10-A_RECOVERY_AFTER_PC_FREEZE.md`.

### Pregunta

¿Puede un `Player` REAL clientless ejecutar directamente Group Heal 1027 dentro
de una Party REAL compuesta únicamente por Players clientless? [VERIFIED: SÍ]

### Spike (artefacto histórico de laboratorio, DESARMADO)

- Ruta: `game/data/scripts/custom/BotSpikeP10A/` — `BotSpikeP10A.java`
  (327 líneas, 10613 B, mtime 09:38:05;
  SHA-256 `895390163ba4705408ae23648fb85cbd6b25987c43e1148dd56c9482325c9beb`).
- Gate: `BotSpikeP10A.ini` → `False` (desarmado en el cierre; era `True`).
- Protocolo VERIFIED (código + logs): `Player.load` ×3 (CASTER 268483130 =
  BSBOT01, MEMBER 268483131 = BSBOT02, OBSERVER 268483132 = BSBOT03); ABORT si
  cualquier `getClient() != null`; `setOnlineStatus(true,false)` +
  `setOfflinePlay(true)`; `spawnMe` (-96221,243497,-3544) ±60; Party de 3
  `FINDERS_KEEPERS`; `SkillData(1027,15)` + `addSkill(skill,false)`
  (runtime-only, sin `storeSkill`, sin DB); `doCast` directo; `removeSkill` en
  teardown. Sin AutoPlay/AutoUse, sin core edits.

### Evidencia principal — Run 2 [VERIFIED]

`game/log/java1.log`: `clientless ok (client=NULL gm/caster/member)`; party
n=3 líder=BSBOT03 ok=true; sin EnterWorld ni GameClient; temp-grant lvl=1;
CAST1 caster 1903→1960 / member 1832→1915; CAST2 caster 2417→2474 / member
782→865; `RESULT=C (both) VERDICT=PASS`; `revoked knownSkill(1027)=null (OK)`.
Clientless: **VERIFIED — 100% CLIENTLESS**.

### Evidencia complementaria — Run 3

`game/log/java0.log` (lvl=15): CTRL9s sin cast Δ=+57/+82; CAST12s Δ=+333/+367
(RAW caster 628→961, member 447→814); `exceptions=false`. Datos VERIFIED;
interpretación manual (INFERRED, coherente con Run 2). NO se presenta como
prueba independiente adicional.

### Conclusión P10-A [VERIFIED]

Un `Player` REAL clientless puede ejecutar directamente el skill 1027 Group
Heal dentro de una Party REAL compuesta por Players clientless, y el handler
PARTY aplica correctamente el Heal al caster y al miembro observado.

### Explícitamente NO concluido [PENDING → P10-B y posteriores]

AutoUse/AutoPlay, healer automático, selección del miembro herido,
RoleStrategy. Nada de esto se afirma.

### Persistencia [VERIFIED]

Skill temporal revocada OK ×2; `character_skills` intacta (mtime 14/09);
cero cambios en core/Bot*/XML/DB. Spike conservado desarmado.

## P10-B — AutoUse/_autoBuffs Runtime (2026-09-15) — BLOCKED

> **Resultado: BLOCKED — NO EJECUTADO.** Sin evidencia runtime. P10-B PENDING.
> Borrador ELIMINADO. P10-A intacto (PASS VERIFIED).

### Pregunta

¿Puede un `Player` real clientless (`_client==null`) usar `_autoBuffs` para
ejecutar Heal 1027 en Party clientless? ¿Que target usa AutoUse para 1027?

### Diseno autorizado (NO ejecutado)

- Borrador ELIMINADO: `custom/BotSpikeP10B/BotSpikeP10B.java` (borrador parcial,
  compilaba `javac EXIT=0` con salida a dir temporal fuera del TARGET, gate
  `False` = nunca armado).
- Protocolo (patron P10-A): `Player.load` x3 (130=BSBOT01 caster, 131=BSBOT02
  member, 132=BSBOT03 observer); ABORT si `getClient()!=null`;
  `setOnlineStatus(true,false)` + `setOfflinePlay(true)`; `spawnMe` ±60;
  Party 3 `FINDERS_KEEPERS`; `SkillData(1027,1)` + `addSkill(skill,false)`
  (sin `storeSkill`/DB); `getAutoBuffs().add(1027)` (API nativa =
  voiced `.playskills`, TARGET `voiced/AutoPlay.java:410-434`);
  `AutoUseTaskManager.startAutoUseTask(caster)` (UPSTREAM :431-454); IDLE.
- TEST-A (IDLE/sin target, ambos 40%): `setTarget(null)`, ventana ~45 s
  (tick 300 ms + reuse 6000 ms + hitTime 7000 ms), registrar target/HP
  antes/despues, casting/attacking, peace.
- TEST-B (IDLE/target=miembro, caster 100% + miembro 30%): `setTarget(member)`.
- TEST-C/D (ATTACKING/CASTING): NO disenados (sin IA segura; prohibido). PENDING.
- Teardown: `stopAutoUseTask` + `_autoBuffs.remove(1027)` +
  `removeSkill(1027,false)` + `leaveParty` x3 + `setOfflinePlay(false)` +
  `PlayerAutoSaveTaskManager.remove` + `stopVitalityTask` +
  `setOnlineStatus(false,false)` + `decayMe`.

### Mecanismo (estatico VERIFIED, runtime NOT FOUND)

- Holder: `AutoUseSettingsHolder` (UPSTREAM :32-58; uso TARGET
  `voiced/AutoPlay.java:91,132,425-431,459`). API:
  `getAutoUseSettings().getAutoBuffs().add(1027)` + `startAutoUseTask`.
- 1027 → `_autoBuffs` (`hasNegativeEffect()==false`; TARGET
  `01000-01099.xml:1402-1438`: PARTY/1000/A1/7000/6000/Heal). [VERIFIED]
- BUFFS (UPSTREAM :176-264): gates peace/casting/attacking/teleport → break;
  `target=getTarget()`; `canCastBuff` (:370-401); playable inocente o misma
  party → `doCast` sobre target; si no → `setTarget(self)→doCast→restore`.
  Sin seleccion por HP (B0 sec 6). [VERIFIED estatico]
- Handler PARTY (TARGET `targets/Party.java:32-91`): caster + miembros en
  radio, resuelve Mobius. [VERIFIED]

### Por que BLOCKED (VERIFIED)

1. `tasklist` mostro `java.exe PID 10124` (~997 MB) sin identidad confirmada
   (terminal truncada, sin `commandline`/cwd/puertos) → regla §16 DETENERSE.
2. Regla §13: arrancar Login+Game con java ajeno vivo = restart encubierto.
3. `BotSpikeP10A.ini=False` VERIFIED; P10-A protegido (§9/§11).

### Resultados / Evidencia / Interpretacion

- TEST-A/B/C/D: NOT EXECUTED → NOT FOUND. Evidencia runtime: NINGUNA.
- VERIFIED: spike nunca armado/ejecutado; P10-A intacto; cero core/DB/XML/Bot*.
- INFERRED (prediccion, NO veredicto): probable
  `PASS — AutoUse ejecuta 1027 (PARTY resuelve caster+miembros, sin seleccion
  por HP; target irrelevante salvo onlyFirst)`.
- PENDING: runtime A/B/C/D con precondiciones §13.

### Limpieza / Archivos (VERIFIED)

Borrador `custom/BotSpikeP10B/` ELIMINADO VERIFIED (lectura ENOENT del `.java`;
busqueda `BotSpikeP10B.class/.ini` = 0 resultados; solo quedan las menciones
de este documento). `BotSpikeP10A/` intacto `False`; Notebook: este doc +
`INDEX.md`.

## P10-B — Runtime Validation — RUN 1 (2026-09-15, ACT) — PASS — VERIFIED

> **Resultado: PASS — VERIFIED.** Supersede el bloqueo previo (la seccion
> "BLOCKED" de arriba queda como registro historico del diseno; ese diseno es
> el que se ejecuto). P10-A NO tocado.

### 1. Objetivo

¿Puede un `Player` real clientless (`_client==null`) usar el mecanismo nativo
`_autoBuffs` de AutoUse para ejecutar Heal 1027 en una Party clientless? Y,
si puede, ¿que target usa realmente AutoUse?

### 2. Setup

- Precondiciones VERIFIED: unico `java.exe` = JDT LS (PID 10124, padre
  Code.exe); puertos 2106/7777/9014 libres; MySQL vivo (`mysqld.exe` XAMPP
  PID 496, listener 3306, data `C:\xampp\mysql\data\l2jmobiush5`).
- Spike recreado: `game/data/scripts/custom/BotSpikeP10B/`
  (`BotSpikeP10B.java` 319 lineas + `BotSpikeP10B.ini` gate). Compilacion
  check `javac -cp libs/GameServer.jar -sourcepath game/data/scripts -d
  Temp_p10b_act` → EXIT=0 tras corregir API de paz (`isInPeaceZone()` NO
  existe en este Player → real: `isInsideZone(ZoneId.PEACE)`). El GameServer
  compila el script en memoria al boot (sin `.class` en `game/`).
- Arranque: `login/LoginServer.vbs` READY (10:54:56, listeners 2106+9014 PID
  7892); `game/GameServer.vbs` → `Server loaded in 49 seconds` + `Registered
  on login as Server 2: Sieghardt` (java0.log 10:56:05, 7777 listening).
  Boot 1 con gate `False` (solo `gate disabled - idle`); luego gate `True` +
  reinicio del proceso Game (metodo C documentado) para cargar el script
  armado (Game final PID 2732, 7777 listening).
- Participantes (java0.log:259-263): `clientless ok (client=NULL
  gm/caster/member)`; `party n=3 ok=true`; ST0 caster BSBOT01 3140/3140,
  member BSBOT02 2235/2235, tgt=null, peace=false, casting/attacking=false,
  known1027=false pre-grant.

### 3. Mecanismo AutoUse utilizado (APIs reales)

- Holder: `AutoUseSettingsHolder._autoBuffs` (`Collection<Integer>`,
  ConcurrentHashMap.newKeySet — UPSTREAM `AutoUseSettingsHolder.java:36,55-58`).
- Registro 1027: `caster.getAutoUseSettings().getAutoBuffs().add(1027)`
  (`BotSpikeP10B.java:152`; mismo metodo que voiced `.playskills` para skills
  positivas: TARGET `voiced/AutoPlay.java:410-433`,
  `hasNegativeEffect()==false` → add). 1027 = Heal PARTY/1000/hasNeg=false
  (TARGET `01000-01099.xml:1402-1438`; VERIFIED en ST0).
- Activacion: `AutoUseTaskManager.getInstance().startAutoUseTask(caster)`
  (`BotSpikeP10B.java:154`; UPSTREAM `AutoUseTaskManager.java:431-454`).
- Desactivacion: `stopAutoUseTask(caster)` (UPSTREAM `:456-466`).
- Consumo: bloque BUFFS de `AutoUseTaskManager$AutoUse.run()` (UPSTREAM
  `:176-264`): gates peace/casting/attacking/teleport → break; usa
  `player.getTarget()`; `canCastBuff` (`:370-401`, sin check de HP del
  objetivo); target playable inocente/misma party → `doCast(skill)` directo;
  si no (incluye target null) → truco `setTarget(self)→doCast→setTarget(saved)`.
- Desviacion del diseno: NINGUNA (APIs exactamente las previstas).

### 4. Casos ejecutados y resultados [VERIFIED]

Log: `game/log/java0.log:267-280` (tag `[BotSpikeP10B-LAB]`).

**TEST-A — IDLE / sin target (TA0 11:01:37 → TA1 11:02:22, ventana ~45 s):**
- TA0: `tgtBefore=null`; caster fijado a 40% (1256/3140), member a 40%
  (894/2235) via `setCurrentHp` (solo runtime); `casting=true` — el caster YA
  estaba casteando: AutoUse venia castiando 1027 desde el setup aunque ambos
  estaban al 100% HP (sin umbral de HP; coincide con B0 A4).
- TA1: caster 1256→1871 (Δ+615), member 894→1610 (Δ+716), `tgt=null`
  (intacto), `casting=true`.
- Interpretacion: con target null, BUFFS usa la rama self-trick
  (`setTarget(self)→doCast→setTarget(saved=null)`); el doCast es targetType
  PARTY → `Party.java` resuelve caster+miembros en radio → **ambos heridos
  curados sin seleccion de nadie**.

**TEST-B — IDLE / target explicito = miembro (TB0 11:02:22 → TB1 11:03:07):**
- TB0: caster a 100% (3140/3140), member a 30% (670/2235), target = BSBOT02
  (`tgt=Player:BSBOT02`).
- TB1: caster 3140→3140 (Δ+0, ya full), member 670→1167 (Δ+496), target
  permanece `Player:BSBOT02` (sin self-trick: rama playable/misma-party
  castea directo sobre el target actual).

### 5. Target behavior de `_autoBuffs` para 1027

- VERIFIED: `getTarget()` por tick (UPSTREAM:231); target null → rama
  self-trick y el target queda null despues; target playable inocente/misma
  party → doCast directo y el target NO cambia (TA1 tgt=null; TB1
  tgt=Player:BSBOT02).
- VERIFIED: NO hay seleccion por HP del miembro herido (casteo observado
  incluso con HP 100%; la curacion del herido en A fue efecto del handler
  PARTY, no de una eleccion).
- INFERRED: el casteo ciclico observado (`casting=true` a lo largo de ~90 s)
  es 1027 — unico skill conocido del caster, `_autoSkills` vacio, AutoPlay
  apagado, sin ataque.

### 6. Party heal behavior

- TEST-A: caster+member ambos curados (PARTY, radio 1000).
- TEST-B: member curado (+496); caster Δ0 (ya full; el heal PARTY le aplicaria
  igualmente, capado por HP max).
- A/B/C separados: A (AutoUse decide lanzar) VERIFIED; B (seleccion de
  target) VERIFIED = target actual o self, jamas elige herido; C (handler
  PARTY expande) VERIFIED.

### 7. Limitaciones

- Deltas HP incluyen regeneracion natural (P10-A Run 3 control ~+57/+82 por
  9 s); neto-heal positivo pero separacion exacta heal/regen no observable
  sin control simultaneo. La prueba de casteo por AutoUse no depende de los
  deltas (`casting=true` ciclico con `_autoSkills` vacio + AutoPlay off).
- CP no medido (irrelevante para Heal). ID de skill del cast en curso no
  emitido por log nativo. TEST-C/D no ejecutados (fuera de alcance).

### 8. Cleanup

- In-spike (java0.log:270-276): `TD autoBuffs=[]`; `TD revoked
  known1027=null(OK)`; leaveParty x3; `worldGone=true` x3.
- Post-run: gate `False`; Game (PID 2732) + Login (PID 7892) detenidos via
  metodo C documentado (sin sesion GM); puertos 2106/7777/9014 sin
  listeners; unico java.exe restante = JDT LS 10124; `Temp_p10b_act`
  (salida javac, fuera del TARGET) eliminado; sin `.class` en `game/`;
  spike eliminado (ver Cierre P10) (convencion P10-A).
- Persistencia [VERIFIED]: `character_skills.ibd` 14/09 13:49,
  `characters.ibd` 14/09 14:13, `character_variables.ibd` 14/09 13:54 — sin
  mtimes de hoy; sin `storeSkill`, sin PlayerVariables writes.

### 9. Respuestas

1. ¿AutoUse/_autoBuffs ejecuto realmente Heal 1027? **SI** (VERIFIED).
2. ¿Sobre que target? **El target actual**: null → self-trick; miembro de
   party → doCast directo sobre el target (sin cambio de target).
3. ¿PARTY termino curando caster/miembro? **SI ambos** en radio 1000
   (A: +615/+716; B: +0 caster ya full / +496 member).
4. ¿Seleccion automatica del miembro herido? **NO** (VERIFIED: casteo con
   HP 100%; sin llamadas HP/party en BUFFS — B0).
5. ¿Funciono con `_client == null`? **SI** (VERIFIED x3).

**Verdict P10-B: PASS — VERIFIED (AutoUse ejecuta 1027; target = actual/self;
PARTY cura a todos en radio; sin seleccion por HP; 100% clientless).**

**Fin P10-B RUN 1 (PASS 2026-09-15; P10-A PASS intacto).**

## Cierre P10 (2026-09-15) - ELIMINACION COMPLETA DE SPIKES (decision del usuario)

> Autorizacion: 'Opcion 1: cierre solamente' - aceptar evidencia existente
> (P10-A Run 2 PASS VERIFIED; P10-B RUN 1 PASS VERIFIED) sin re-ejecutar;
> eliminar spikes con hash previo; no tocar logs; corregir cabecera;
> sin commit ni push; detenerse despues del cierre.

### Hashes SHA-256 previos a la eliminacion (trazabilidad)

| Archivo | SHA-256 |
|---|---|
| custom/BotSpikeP10A/BotSpikeP10A.java (10613 B, 15/09 09:38:05) | 895390163BA4705408AE23648FB85CBD6B25987C43E1148DD56C9482325C9BEB |
| custom/BotSpikeP10A/BotSpikeP10A.ini (29 B, 15/09 10:28:16) | 64DBD6F5A7F3B5ADF5210F99CB933CC5F465FD3FCF1A90AB5151BDA04D3C8D75 |
| custom/BotSpikeP10B/BotSpikeP10B.java (10425 B, 15/09 10:53:48) | 90FB2A8C0EACEF44DB2683F74B0CA9C5C8734C1649272BA017A61C2F0FC3F984 |
| custom/BotSpikeP10B/BotSpikeP10B.ini (29 B, 15/09 11:06:23) | 6014E5F5041B20ED3F8659D55DF5B386BE3C13D8BCADF8A0B16F0AC1CBAFCB10 |

Hash de BotSpikeP10A.java identico al registrado en
BOTAI-P10-A_RECOVERY_AFTER_PC_FREEZE.md (integridad confirmada).

### Eliminacion y verificacion (15/09 ~18:30)

- ELIMINADOS: custom/BotSpikeP10A/ y custom/BotSpikeP10B/ completos (4 archivos).
- Cero residuos: busqueda *BotSpikeP10* en todo game/ = 0 resultados
  (sin .java, .ini, .class ni directorios). custom/ conserva unicamente
  sus directorios preexistentes.
- Logs preservados intactos: java4.log (Run 1 FAIL), java3.log (Run 2 PASS),
  java2.log (Run 3), java1.log (P10-B boot idle), java0.log (P10-B RUN 1 VERDICT).
- DB intacta: character_skills.ibd 14/09 13:49, characters.ibd 14/09 14:13,
  character_variables.ibd 14/09 13:54 - cero escrituras del 15/09.
- Sin procesos java; puertos 2106/7777/9014 sin listeners; gates False antes
  de eliminar; LAB no re-ejecutado; cero cambios permanentes.

**ESTADO CIERRE: COMPLETADO.** Estado final P10: P10-A PASS VERIFIED (Run 2) +
P10-B PASS VERIFIED (RUN 1); spikes eliminados; cero cambios permanentes.
