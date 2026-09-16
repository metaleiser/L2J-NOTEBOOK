# BOTAI-06-B — ROLE PROFILES / MULTICLASS BATTERY RESEARCH

> **SPRINT:** BOTAI-06-B
> **FASE:** Investigación (sin implementación)
> **MODO:** INVESTIGACIÓN + DOCUMENTACIÓN (sin cambios funcionales)
> **Estado:** COMPLETADO (investigación). Ninguna implementación.
> **Fecha:** 2026-09-15
> **TARGET:** `C:\L2J MOBIUS IA\L2J_Mobius_CT_2.6_HighFive` (runtime `libs\GameServer.jar` + datapack `game\data\...`)
> **UPSTREAM:** `C:\L2J MOBIUS\UPSTREAM\L2J_Mobius\L2J_Mobius_CT_2.6_HighFive` (solo contraste vía documentos previos; NO tocado)
> **Baseline:** L2J Mobius CT 2.6 HighFive @ e2518ab108 (runtime)
> **Predecesores (autoridades):** `investigations/BOTAI-06-C_EQUIPMENT_MASTER_CATALOG.md` (equipment), `investigations/BOTAI-06-D_SKILL_BUFF_COMBAT_MASTER_CATALOG.md` (skills), `investigations/BOTAI-06-B0_AUTO_SKILLS_AUTO_BUFFS_PARTY_TARGETING_RESEARCH.md` (AutoSkills/AutoBuffs/targeting), `bots/BOT_RECIPE.md`, `decisions/FASE4_BOT_ARCHITECTURE_FREEZE.md`, `investigations/BOTAI-06-ACT1_CONSOLIDATION.md`, `investigations/BOTAI-02_ROLES_BEHAVIORS_API_RESEARCH.md`

Leyenda de estados: **VERIFIED** | **INFERRED** | **PROPOSED** | **NOT FOUND** | **PENDING**

Convención de evidencia (heredada de B0):

* **[TARGET/JAR]** = verificado por desensamblado/inspección de `libs\GameServer.jar` (vía B0).
* **[TARGET/DATAPACK]** = leído del datapack del TARGET (XML o source legible).
* **[TARGET/config]** = leído de `game\config\...` del TARGET.
* **[UPSTREAM]** = contraste con source de UPSTREAM (vía B0/06-D).
* **[RUNTIME]** = validado en ejecución por spikes previos (D0001, BOT_RECIPE).
* **[06-C §x]** / **[06-D §x]** / **[B0 §x]** = sección de la autoridad correspondiente; no se duplican aquí sus tablas completas.
* **[VERIF 06-B]** = comprobación directa realizada en esta tarea sobre el TARGET (lectura de archivo/config/XML).

Regla anti-ambigüedad de esta tarea (heredada de 06-C §15):

* **VERIFIED** = el dato existe con ese ID/grado/tipo en el TARGET.
* **RECOMMENDATION/PROPOSED** = selección de ese dato para un rol (decisión de diseño, NO comportamiento nativo).
* Ninguna recomendación de este documento es comportamiento del servidor. **LA IA DECIDE (rol/perfil); MOBIUS EJECUTA (AutoPlay/AutoUse/PlayerAI/Party).** [BOTAI-02 INV7, VERIFIED como regla de proyecto]

## 1. Metadata

| Campo | Valor |
|---|---|
| Sprint | BOTAI-06-B |
| Tipo | Investigación + documentación (NO implementación) |
| Autoridad equipment | BOTAI-06-C (no se duplican tablas completas) |
| Autoridad skills | BOTAI-06-D (no se duplican catálogos) |
| Autoridad AutoSkills/AutoBuffs/targeting | BOTAI-06-B0 |
| Arquitectura congelada | `decisions/FASE4_BOT_ARCHITECTURE_FREEZE.md` |
| Infraestructura de bots actual | `bots/BOT_RECIPE.md` + `BOTAI-06-ACT1_CONSOLIDATION.md` (BotProfile/BotRole/BotPresets/BotProvisioning) |
| Roles de la batería | TANK, DPS_FIGHTER, MAGE, HEALER, SUPPORT |
| Alcance de implementación | NINGUNO (0 líneas de código en esta tarea) |

## 2. Objetivo

Responder, con evidencia, a la pregunta:

> **¿Cómo debería describirse técnicamente cada tipo de bot (TANK, DPS_FIGHTER, MAGE, HEALER, SUPPORT) utilizando las capacidades que ya sabemos que Mobius tiene?**

El "perfil" aquí es una **configuración declarativa** del bot (equipamiento + skills + AutoPlay + AutoUse), no una clase Java ni una RoleStrategy. RoleStrategy/ThinkLoop/BotAI/party coordinator/BotBoard/raid AI quedan explícitamente FUERA de alcance.

## 3. Alcance

Dentro de alcance:

* Ficha técnica por rol (equipment / skills / AutoSkills / AutoBuffs / AutoPlay / targeting).
* Matriz multiclass (roles ↔ clases reales de High Five del TARGET).
* Documentación de limitaciones nativas, conflictos, huecos de datos y propuestas de diseño marcadas PROPOSED.

Fuera de alcance (NO tratado aquí):

* Implementar nada: sin RoleStrategy, sin ThinkLoop, sin BotAI, sin healer AI, sin party coordinator, sin BotBoard, sin raid AI.
* Modificar core, Player.java, clases congeladas, UPSTREAM, configs productivas o DB.
* Build/deploy/restart.
* Dyes/Henna (ya fuera de alcance en ACT1, ACT1 §13.5).
* Resolver el bloqueo BUFFS-durante-ataque/casteo con código (solo documentar la consecuencia).

## 4. Fuentes de autoridad

| Fuente | Autoridad sobre | Uso en este documento |
|---|---|---|
| `BOTAI-06-C_EQUIPMENT_MASTER_CATALOG.md` | EQUIPO: armor/weapons/jewelry/sets/grades/slots/IDs/anomalías | Secciones de equipment por rol; reglas de slot; escalera de grados |
| `BOTAI-06-D_SKILL_BUFF_COMBAT_MASTER_CATALOG.md` | SKILLS: XML, skill trees, clasificación funcional, condiciones, buffs/heals, party, AutoPlay/AutoUse | Clasificación por comportamiento real; clases; condiciones útiles |
| `BOTAI-06-B0_AUTO_SKILLS_AUTO_BUFFS_PARTY_TARGETING_RESEARCH.md` | `_autoSkills`/`_autoBuffs`, motores AutoUse/AutoPlay, party heal/buff targeting | Contrato de entrada por rol; limitaciones de targeting |
| `bots/BOT_RECIPE.md` | Arquitectura implementada (lifecycle, provisioning, AutoPlay/AutoUse RUNTIME) | Punto de partida de infraestructura |
| `decisions/FASE4_BOT_ARCHITECTURE_FREEZE.md` | Arquitectura congelada (NO TOCAR lista; crear: BotManager, RoleStrategy, BotBoard...) | Marco de lo que un perfil puede y no puede hacer |
| `BOTAI-06-ACT1_CONSOLIDATION.md` | BotProfile declarativo ya implementado | Modelo declarativo existente sobre el que la batería se apoyará |
| `BOTAI-02_ROLES_BEHAVIORS_API_RESEARCH.md` | Regla "LA IA DECIDE; MOBIUS EJECUTA"; APIs NOT FOUND | Principio rector; APIs que no existen |

## 5. Modelo conceptual de Role Profile

### 5.1 Qué es un "perfil" en este proyecto (estado actual)

**VERIFIED [VERIF 06-B]** — Ya existe un modelo declarativo implementado (BOTAI-06-ACT1):

```text
BotProfile (DATA ONLY, immutable)                        [TARGET/DATAPACK BotProfile.java]
 ├── id (String, p.ej. "DPS_FIGHTER")
 ├── role (BotRole: TANK, HEALER, MAGE, DPS, SUPPORT)    [BotRole.java:23-30]
 ├── classId / baseClassId (int)                          [regla classid == base_class, BOT_RECIPE §8]
 ├── targetLevel (int)
 ├── SkillMode (AUTO_BY_CLASS -> giveAvailableSkills(false,true,false))
 ├── profileVersion (int, idempotencia)
 └── gradeLadder (EnumMap<Grade, BotGradeLoadout>)        [Grade: NONE..S; resolveGrade(level)]
```

* `BotPresets` solo declara `DPS_FIGHTER` (classId=2 Gladiator, targetLevel 80, ladder D..S con IDs verificados). [VERIFIED, BotPresets.java]
* `BotProvisioning.apply(Player, BotProfile)` ya aplica clase/nivel/skills/equipo. [VERIFIED, ACT1]
* **Lo que el perfil actual NO contiene todavía:** ninguna asignación a `AutoPlaySettingsHolder` ni `AutoUseSettingsHolder` (targetMode, pickup, `_autoSkills`, `_autoBuffs`, `_autoActions`, potion). Ese mapeo es exactamente el hueco que la futura RoleStrategy llenaría [B0 §13, PROPOSED].

### 5.2 Contrato nativo disponible para un perfil (resumen VERIFIED)

Un perfil declarativo puede describir, sin tocar core, los siguientes knobs nativos [B0 §4-§7, §13; RUNTIME BotSpikeD01]:

| Knob | Titular | Valores / semántica | Consumidor nativo |
|---|---|---|---|
| `targetMode` | `AutoPlaySettingsHolder` | 1=Monster, 2=Characters, 3=NPC, 0/def=Any | AutoPlayTaskManager (`isTargetModeValid`) |
| `pickup` | AutoPlaySettingsHolder | recoger drops | AutoPlay (tick 700 ms) |
| `shortRange` | AutoPlaySettingsHolder | rango 600 / 1400 (`AutoPlay.ini`) | AutoPlay (búsqueda de target) |
| `respectfulHunting` | AutoPlaySettingsHolder | no robar targets | AutoPlay |
| `autoPotionPercent` | AutoUseSettingsHolder | % HP propio para autopotion | AutoUse (bloque potion) |
| `_autoSkills` | AutoUseSettingsHolder | `List<Integer>` orden inserción, round-robin | AutoUse SKILLS (solo con `isAutoPlaying()`) |
| `_autoBuffs` | AutoUseSettingsHolder | `Set<Integer>` sin orden | AutoUse BUFFS (300 ms, corre sin AutoPlay) |
| `_autoSupplyItems` / `_autoPotionItem` | AutoUseSettingsHolder | items/poción | AutoUse ITEMS/potion |
| `_autoActions` | AutoUseSettingsHolder | contener `2` = melee attacker (`isMageCaster=false`) | AutoPlay (`setIntentionAttack` vs `MoveTo`) |

### 5.3 Regla de clasificación skill → lista (única regla nativa)

**VERIFIED [B0 §5, TARGET/DATAPACK + UPSTREAM Skill.java:941-944]:**

```text
hasNegativeEffect() = (effectPoint < 0) && (targetType != SELF)
   true  -> _autoSkills (ofensiva/debuff; SKILLS loop, exige target isAutoAttackable + isAutoPlaying)
   false -> _autoBuffs  (buffs/heals; BUFFS loop, corre aunque AutoPlay esté apagado)
```

El voiced command `.playskills` usa exactamente esta regla [B0 §4; AutoPlay.java:389-435]. Cualquier perfil server-side debe copiar la misma regla para no desincronizarse [B0 §13].

Adicionalmente el voiced command excluye de su menú a pasivas (`isPassive()`) y toggles (`isToggle()`) y respeta `AutoPlayConfig.DISABLED_AUTO_SKILLS` [VERIF 06-B, AutoPlay.java:308-333].

### 5.4 Estructura hipotética de la batería (NO validada como implementación)

La estructura conceptual pedida para esta batería es [PROPOSED — hipótesis a verificar en un sprint futuro, NO implementación]:

```text
ROLE (TANK | DPS_FIGHTER | MAGE | HEALER | SUPPORT)
 ├── class variants        (clases reales compatibles con el rol, con classId/baseClassId)
 ├── equipment profile     (ladder NONE..S de EquipEntry por variante de clase)
 ├── skill profile         (skills aprendibles por variante: 06-D §5.7 + clasificación 06-D §7)
 ├── AutoPlay configuration (targetMode / pickup / shortRange / respectfulHunting / potionPercent / action 2 sí-no)
 └── AutoUse configuration (_autoSkills / _autoBuffs / _autoSupplyItems / _autoPotionItem)
```

Tensión declarada con el modelo actual: `BotProfile` hoy amarra **1 perfil = 1 classId** [VERIFIED BotProfile.java], mientras que un "rol" agrupa **N clases**. Dos shapes posibles (mutuamente excluyentes hoy): (a) N constantes `BotProfile` por rol (p.ej. `TANK_PALADIN`, `TANK_TEMPLE_KNIGHT`...) y el rol queda como etiqueta de agrupación; (b) un perfil por rol con variante de clase como parámetro. **Decisión pendiente; no se adopta ninguna aquí.** (Ver §21.1 y §22.)

### 5.5 Principio rector aplicado

> **LA IA DECIDE (rol/perfil); MOBIUS EJECUTA (AutoPlay/AutoUse/PlayerAI/Party).** [BOTAI-02 INV7]

Un perfil solo puede: elegir clase/nivel/equipo/skills (vía provisioning existente) y **rellenar/afinar los knobs de la tabla 5.2**. Todo lo demás (validación de casteo, cooldown, resolución de targets por targetType, movimiento, ataque, autopotion tick) es del motor nativo y NO se reimplementa [B0 §10/§13; FASE4 FREEZE].

---

## 6. TANK

### 6.1 Equipment (autoridad: 06-C)

**Regla de rol nativa (obligatoria) — VERIFIED [06-C §15.2, §16.2]:** Tank = arma 1 mano (`rhand`) + escudo (`lhand`). Un arma `lrhand` (DUAL/POLE/BOW/2H) desequipa el escudo y viceversa: **el perfil TANK no puede llevar ni dual ni pole ni arco**.

| Grade | Armor (HEAVY, VERIFIED en datapack) | Weapon 1H `rhand` (VERIFIED) | Shield `lhand` (VERIFIED) | Estado |
|---|---|---|---|---|
| NONE | sin set HEAVY No-Grade verificado [06-C A10/U1] | 2369 Squire's Sword | 18/19/20/625 | item VERIFIED / set NOT VERIFIED |
| D | Mithril 58+59+47; Brigandine 352+2378+2411 | 129 Sword of Revolution (p79); 159 Bonebreaker (p92) | 628 Hoplon; 2493 Brigandine Shield | VERIFIED / RECOMMENDATION |
| C | Full Plate 356 (onepiece)+2414; Compound 60+517; Chain 354+381+2413 | 135 Samurai Longsword (p156); 2503 Yaksa Mace | 2497/107/2495 | VERIFIED / RECOMMENDATION |
| B | Avadon 2376+2379+2415+5714+5730; Doom 2381+2417+5722+5738; Zubei 357+383+503+5710+5726 | 4719 Sword of Damascus - Haste (SA); 4751 Deadman's Glory - Health (SA) | 673 Avadon; 110 Doom | VERIFIED / RECOMMENDATION |
| A | Dark Crystal 365+388+512+5765+5777; Nightmare 374+2418+5771+5783; Majestic 2383+2419+5774+5786 | 8678 Sirra's Blade (p251); 2500 Dark Legion's Edge (p232) | 641 Dark Crystal; 2498 Shield of Nightmare | VERIFIED / RECOMMENDATION |
| S | Imperial Crusader 6373+6374+6378+6375+6376 | 6364 Forgotten Blade (p281); 9442 Dynasty Sword (p333) | 6377 Imperial Crusader; 634 Dragon Shield | VERIFIED / RECOMMENDATION |
| S80 | Moirai 15609+15612+15606+15615+15618 | arma S80: **NOT VERIFIED por rol** [06-C U3] | 15621 Moirai Shield; 15622 Moirai Sigil | VERIFIED (existencia) |
| S84 | Vesper 13432+13438+13137+13439+13440; Vorpal 15592+15595+15589+15598+15601; Elegia 15575+15578+15572+15581+15584 | arma S84: **NOT VERIFIED por rol** [06-C U3] | 13471/15604/15587 | VERIFIED (existencia) |

* Jewelry: 1 `neck` + 2 `rear;lear` + 2 `rfinger;lfinger` (regla nativa de slots; ejemplos A/S en 06-C §14; boss jewelry §14.2). Matriz exacta: 06-C §14.1/§14.2.
* Restricciones: sin condiciones de clase en piezas [06-C §16.5]; **raza**: piezas con `<player races>` incluyen HUMAN..DWARF → un bot KAMAEL quedaría bloqueado en esas armaduras [06-C §16.5/§19]. Grado superior penaliza, no bloquea (`ExpertisePenalty=True`) [06-C §16.4].
* Sets: la composición de cada set y su skill de set está en 06-C §12 (217 bloques; Dynasty = S en este datapack [06-C A4]).

### 6.2 Skills (autoridad: 06-D, clasificación por comportamiento real)

| Comportamiento (operateType + targetType + effects) | Skills citadas en autoridad | Estado |
|---|---|---|
| Buff defensivo SELF | 1040 Shield, 1243 Bless Shield, 1304 Advanced Block, 1036 Magic Barrier [06-D §11.6, §16.1] | VERIFIED (IDs) |
| Aggro | efectos `GetAgro`/`AddHate` [06-D §16.1 "Tank ... agro"] — **IDs concretos NO censados en el Notebook** | PENDING (IDs) |
| CC ofensivo | categoría CROWD_CONTROL (331 skills; Stun/Sleep/Root/Paralyze/...) [06-D §7.1/§7.2] | VERIFIED (categoría) |
| Debuff | categoría DEBUFF (263 skills) [06-D §7.2] | VERIFIED (categoría) |
| Pasivas | operarType P (2356 skills totales; p.ej. masterías de armadura) — **por clase NO censado en el Notebook** | PENDING (derivable de 06-D §5.7) |
| Toggles | operateType T (71 skills) [06-D §4.2/§7.1] | VERIFIED (categoría) |
| Condiciones nativas útiles | `<player hp=>` (<= %), `target aggro`, `mindistance` [06-D §8.3/§16.1] | VERIFIED (mecanismo) |

Nota: la enumeración exacta de skills ofensivas/aggro de cada clase tank del TARGET **existe** en el índice maestro 06-D §5.7 (932 skills aprendibles por clase) pero no está incrustada en el Notebook; regenerable con el método de 06-D. No se inventan IDs aquí.

### 6.3 AutoSkills / AutoBuffs / AutoPlay (mapeo conceptual; PROPOSED el contenido concreto)

| Lista/Knob | Contenido conceptual para TANK | Fundamento nativo | Estado |
|---|---|---|---|
| `_autoSkills` | rotación ofensiva + skills de aggro (todas `hasNegativeEffect()==true`) contra el monstruo targeteado | AutoUse SKILLS loop; round-robin por orden de inserción; exige `isAutoPlaying()` y target `isAutoAttackable` [B0 §6] | mecanismo VERIFIED; contenido PROPOSED |
| `_autoBuffs` | self-buffs defensivos (p.ej. 1040/1243/1304/1036) | BUFFS loop castea en self si no hay target playable; chequeo de presencia por `getBuffInfoBySkillId/AbnormalType` con margen 3 s [B0 §5] | mecanismo VERIFIED; contenido PROPOSED |
| `_autoActions` | incluir `2` (melee attacker) | `isMageCaster=false` → AutoPlay `setIntentionAttack` [B0 §7] | mecanismo VERIFIED |
| targetMode | 1 (Monster) | AutoPlay búsqueda de monstruos no-raid [B0 §7] | VERIFIED (modo) |
| pickup / shortRange / potionPercent | decisión de diseño (p.ej. pickup=false para tank) | knobs nativos [B0 §7] | PROPOSED |

Limitaciones que el perfil TANK hereda (VERIFIED):

* Sin prioridad/peso por skill en `_autoSkills`: solo orden de inserción (NF5 [B0 §15]).
* No existe condición de "target HP %" ni "aggro del target" como selector automático de skill por situación más allá de las condiciones XML nativas [06-D §8.4/§22.2; B0 NF3].
* Las skills de aggro entran por la misma regla `hasNegativeEffect`; si una skill de aggro tuviera effectPoint >= 0, caería en `_autoBuffs` por la regla nativa (verificar skill por skill; PENDING censo de aggro).

---

## 7. DPS_FIGHTER

### 7.1 Equipment (autoridad: 06-C §15.3 + §15.4 + §15.5 + §13)

El rol DPS cubre en 06-C **tres arquetipos de arma** distintos (matriz, no elección):

| Arquetipo | Armor | Weapon (tipo/slot real) | Nota nativa |
|---|---|---|---|
| Melee dual (Gladiator/Warlord/Titan/Tyrant) [06-C §15.3] | HEAVY por grado (Brigandine D → Full Plate C → Avadon B → Dark Crystal A → Imperial Crusader o Dark Crystal S) | DUAL `lrhand` (2536/2529 D; 2582 Katana*Katana C; 2600 Raid Sword*Caliburs B; 5706/8938 A; 6580 Tallum Blade*Dark Legion's Edge S) | ladder actual BSBOT01 validado [BOT_RECIPE; BotPresets] |
| Dagger [06-C §15.5] | LIGHT (394/395 → 397/398/400/401 → 2384/2390-2392 → 2385/2393-2395 → 6379 Draconic) | DAGGER `rhand`; DUALDAGGER existe pero suele llevar `categoryType` de clase (Dagger Master) → verificar [06-C §15.5] | burst melee |
| Archer [06-C §15.4] | LIGHT (mismos sets) | BOW `lrhand` — **el `lhand` queda ocupado por flechas (EtcItem), no escudo** [06-C §15.4 nota] | ranged físico |

* NONE: 3 Broadsword (p11) — anomalía A1 documentada (usada como arma D en el ladder vigente) [06-C A1].
* Arquetipos por tipo de arma verificados en 06-C §15.8 (melee 1H+escudo / melee 2H / dual / ranged / caster).
* SA: las variantes con SA (p.ej. 4719, 4751) están censadas como items base con SA en el nombre [06-C]; el vínculo SA→skill NO está verificado [06-D §19] → **excluir SA de la batería hasta cerrar 06-D §19 (PENDING)** y preferir IDs base [06-C §20.4].

### 7.2 Skills (autoridad: 06-D)

| Comportamiento | Skills citadas en autoridad | Estado |
|---|---|---|
| Ataque físico condicionado a arma | 176 Frenzy (precondición nativa `hp<=60`; bonos por `<using kind>`/`<using slot>`), 190 Fatal Strike [06-D §9, §16.1] | VERIFIED (IDs) |
| Categoría PHYSICAL_ATTACK | 474 skills [06-D §7.2] | VERIFIED (categoría) |
| Backstab/FatalBlow (dagger) | `Backstab` (1 skill), `FatalBlow` (29) [06-D §16.1] | VERIFIED (categoría/IDs por nombre de efecto) |
| Buffs ofensivos self | 1068 Might, 1077 Focus, 1086 Haste, 1242 Death Whisper, 1204 Wind Walk [06-D §11.6] | VERIFIED (IDs) |
| Pasivas/toggles por clase | NO censadas por clase en el Notebook | PENDING (06-D §5.7) |
| Condiciones nativas útiles | `<using kind>` (160 usos), `<using slot>` (5), `<player hp>` [06-D §8.5, §9] | VERIFIED |

**Dependencia skill↔equipment (VERIFIED [06-D §18]):** un arma no "añade" skills; **habilita o deshabilita** efectos/precondiciones vía `<using kind>`/`<using slot>`. Consecuencia para el perfil: cambiar el arma puede dejar skills sin efecto (p.ej. skills con `<using kind="DAGGER">` con un dual). El emparejamiento weapon-family ↔ skill-list debe hacerse por `kind`, no por nombre.

### 7.3 AutoSkills / AutoBuffs / AutoPlay (mapeo conceptual)

| Lista/Knob | Contenido conceptual para DPS_FIGHTER | Fundamento | Estado |
|---|---|---|---|
| `_autoSkills` | rotación de daño (orden de inserción = "prioridad"; round-robin, sin pesos) | B0 §6/§14 (NF5: no existe prioridad real) | mecanismo VERIFIED; contenido PROPOSED |
| `_autoBuffs` | self-buffs ofensivos (1068/1077/1086/1242/1204...) + toggles si procede (verificar clasificación por skill) | BUFFS loop + presencia por abnormal [B0 §5] | mecanismo VERIFIED; contenido PROPOSED |
| `_autoActions` | incluir `2` | melee attacker [B0 §7] | VERIFIED (mecanismo) |
| targetMode | 1 (Monster) | [B0 §7] | VERIFIED (modo) |

Caso especial VERIFIED [B0 §4/§6]: skill 254 (Spoil) en `_autoSkills` hace que AutoPlay no re-targetee monstruos muertos (sweeping). Solo relevante para variantes con Spoiler/FortuneSeeker (fuera del rol DPS puro; se documenta como capacidad nativa).

---

## 8. MAGE

### 8.1 Equipment (autoridad: 06-C §15.6 + §13.3)

| Grade | Armor (MAGIC/robe, VERIFIED) | Weapon (VERIFIED, top mAtk del grade) | Estado |
|---|---|---|---|
| NONE | 1101 + 1104 + 44 (Devotion) | 6 Apprentice's Wand (BLUNT `rhand`) | VERIFIED / RECOMMENDATION |
| D | 436+469+2447; 437+470+2450 (Knowledge) | 188 Ghost Staff (`lrhand`, m79) | VERIFIED / RECOMMENDATION |
| C | 439+471+2454 (Karmian); 441+472+2459; 442+473+2463 | 206 Demon's Staff (`lrhand`, m122) | VERIFIED / RECOMMENDATION |
| B | 2397+2402+503+5712+5728 (Zubei); alt 2398/2399; 2406 (onepiece) | 210 Staff of Evil Spirits (`lrhand`, m145) | VERIFIED / RECOMMENDATION |
| A | 2407 (onepiece)+512+5767+5779 (Dark Crystal Robe); 2408; 2409; 2400+2405 | 8688 Daimon Crystal (`lrhand`, m177); 151 Sword of Miracles (`rhand`, m152); 213 Branch of the Mother Tree | VERIFIED / RECOMMENDATION |
| S | 6383 (onepiece)+6386+6384+6385 (Major Arcana) | 6579 Arcana Mace (`rhand`, m175); 9449 Dynasty Mace (`rhand`, m202); 9444 Dynasty Phantom (`rhand`, m202); 6366 Imperial Staff (`lrhand`, m193) | VERIFIED / RECOMMENDATION |
| S80/S84 | Moirai 15611+15614+15608+15617+15620; Vesper/Vorpal/Elegia robe [06-C §15.1 S80/S84] | **NOT VERIFIED por rol** [06-C U3] | VERIFIED (armor) / NOT VERIFIED (arma) |

* **Sigils** (`armor_type=SIGIL`, `lhand`): existen en S (10119 Dynasty, 12811/13078 Arcana), S80 (15622), S84 (12813 Vesper, 15588 Elegia, 15605 Vorpal) [06-C §16.3]. Un caster con arma `rhand` + sigil es la pareja nativa; **sigil y escudo comparten `lhand` → mutuamente excluyentes** [06-C §16.3/U5, INFERRED no validado en runtime].
* Escudo + `lrhand` es incompatible [06-C §16.2] → un mage con staff 2H no lleva escudo/sigil.

### 8.2 Skills (autoridad: 06-D)

| Comportamiento | Evidencia | Estado |
|---|---|---|
| Daño mágico | categoría MAGIC_ATTACK: 450 skills; efecto `MagicalDamage` en 311 skills [06-D §7.2/§16.1] | VERIFIED (categoría) |
| Buffs mágicos self | 1059 Empower, 1085 Acumen, 1303 Wild Magic, 1078 Concentration, 1048 Blessed Soul [06-D §11.6] | VERIFIED (IDs) |
| Debuffs | categoría DEBUFF 263 skills [06-D §7.2] | VERIFIED (categoría) |
| MP propio | 1048 Blessed Soul; efecto `ManaHeal*` (68 skills MP_HEAL) [06-D §11.1/§16.1] | VERIFIED (categoría) |
| Summon (variantes summoner) | categoría SUMMON 226 skills; targetTypes SUMMON/SERVITOR/OWNER_PET [06-D §7.1/§12.2] | VERIFIED (categoría) |
| Condiciones útiles | `<player mp=>` (operador exacto PENDING [06-D §8.3]), `<using kind>` para staves | VERIFIED / PENDING (mp operator) |

### 8.3 AutoSkills / AutoBuffs / AutoPlay (mapeo conceptual)

| Lista/Knob | Contenido conceptual para MAGE | Fundamento | Estado |
|---|---|---|---|
| `_autoSkills` | nukes (MAGIC_ATTACK); round-robin | B0 §6 | mecanismo VERIFIED; contenido PROPOSED |
| `_autoBuffs` | self-buffs mágicos (1059/1085/1303/1078/1048) | BUFFS loop self-cast | mecanismo VERIFIED; contenido PROPOSED |
| `_autoActions` | **NO incluir `2`** | sin acción 2 → `isMageCaster=true`: AutoPlay **mueve** (`MoveTo` si dist>900) en lugar de auto-atacar [B0 §7] | VERIFIED (mecanismo) |
| targetMode | 1 (Monster) | [B0 §7] | VERIFIED (modo) |

Consecuencia arquitectónica del bloqueo BUFFS (mecánica VERIFIED [B0 §5], impacto INFERRED): cada nuke pone `isCastingNow=true` → el bloque BUFFS se salta ese tick; el mage re-buffea solo en las ventanas entre casts. Con buffs self de larga duración es tolerable; el impacto real es para HEALER/SUPPORT (ver §18.1).

---

## 9. HEALER

### 9.1 Equipment (autoridad: 06-C §15.1)

| Grade | Armor (MAGIC) | Weapon | Jewelry | Estado |
|---|---|---|---|---|
| NONE | 1101 + 1104 + 44 (Devotion) | 6 Apprentice's Wand (BLUNT `rhand`) | 118/112/116 | VERIFIED / RECOMMENDATION |
| D | 436+469+2447 (Knowledge) | 188 Ghost Staff (`lrhand` m79) o 189 Staff of Life (`rhand` m72) | 910/847-851/879-882 | VERIFIED / RECOMMENDATION |
| C | 439+471+2454 (Karmian) | 206 Demon's Staff (`lrhand` m122) | 119/852-855/883-886 | VERIFIED / RECOMMENDATION |
| B | 2397+2402+503+5712+5728 (Zubei robe) | 210 Staff of Evil Spirits (`lrhand` m145) | 918/856-861/887-892 | VERIFIED / RECOMMENDATION |
| A | 2407 (onepiece)+512+5767+5779 (Dark Crystal Robe) | 8688 Daimon Crystal o 151 Sword of Miracles | 924/862-872/893-903 | VERIFIED / RECOMMENDATION |
| S | 6383 (onepiece)+6386+6384+6385 (Major Arcana) | 6579 Arcana Mace (`rhand` m175) o 6366 Imperial Staff (`lrhand` m193) | 920/858/889 | VERIFIED / RECOMMENDATION |
| S80 | Moirai 15611+15614+15608+15617+15620 | arma: NOT VERIFIED por rol [U3] | 15725/15724/15723 | VERIFIED (armor/jewelry) |
| S84 | Vesper 13434+13444+13139+13445+13446; Vorpal 15594+15597+15591+15600+15603; Elegia 15577+15580+15574+15583+15586 | arma: NOT VERIFIED por rol [U3] | ver 06-C §14.1 | VERIFIED (armor) |

Anomalía relevante para perfiles: 852 es Moonstone Earring **C** (usada como "Majestic" A en ladders antiguos) [06-C A3]; los ladders nuevos deben usar IDs de §14/§15.1 (862 Majestic Earring A; 858/889/920 Tateossian S).

### 9.2 Skills (autoridad: 06-D §11 + B0 §9/§19)

Heals/limpieza/resurrección con IDs VERIFIED en el TARGET:

| Tipo (por effects/targetType, NO por nombre) | Skills | Comportamiento de target nativo |
|---|---|---|
| Heal single-target (`ONE`) | 1011 Heal, 1015 Battle Heal, 1020 Vitalize, 1217 Greater Heal, 1218 Greater Battle Heal, 1258 Restore Life, 1401 Major Heal, 1487 Restoration [B0 §19] | exige target explícito (`One.java`); si no → `THAT_IS_AN_INCORRECT_TARGET` |
| Heal de grupo (`PARTY`) | 1027 Group Heal, 1219 Greater Group Heal, 1229 Chant of Life, 1271 Benediction, 1428 Mass Recharge (MP), 1552 Mass Vitalize, 787 Touch of Eva [B0 §19] | `Party.java` resuelve caster+miembros en `affectRange`; self-cast suficiente |
| Heal de área friendly | 1553 Chain Heal (`AREA_FRIENDLY`, castRange 900, effectRange 1400) [B0 §19]; 1505 Sublime Self-Sacrifice (`AURA_FRIENDLY`) [B0 §19] | resuelve lista de aliados en área |
| Clan heal/buff | 1256 The Heart of Pa'agrio, 1305 The Honor of Pa'agrio, 1416 Pa'agrio's Fist (`PARTY_CLAN`) [B0 §19] | `PartyClan.java` |
| Limpieza | 1018 Purify [06-D §11.4] | (targetType según XML; verificar por skill) |
| Resurrección | efectos `Resurrection` (26 skills) / `ResurrectionSpecial` (5); citadas: 1016 Resurrection, 1254 Mass Resurrection [06-D §11.1/§11.4] | `canCastBuff` permite target muerto SOLO si targetType ∈ {SELF, CORPSE, PC_BODY} [B0 §5] |
| Redistribución | 1335 Balance Life [06-D §11.4] | (verificar targetType por skill) |
| Buffs self | 1045 Blessed Body, 1048 Blessed Soul, 1044 Regeneration [06-D §11.6] | BUFFS self-cast |

Reglas nativas que condicionan el heal de bot (todas VERIFIED [B0 §5/§9]):

1. Una heal en `_autoBuffs` **no tiene umbral de HP** del objetivo: se castea por cadencia (`reuseDelay` + tick 300 ms) y presencia del buff.
2. Heal `ONE` sobre un aliado **solo ocurre si el target actual del bot es ese aliado**; como AutoPlay jamás targetea aliados [B0 §7], en la práctica la heal `ONE` cae en **self**.
3. `canCastBuff` exige distancia `calculateDistance3D <= castRange` y MP del caster; target muerto bloqueado salvo SELF/CORPSE/PC_BODY.
4. El bloque BUFFS no corre si el bot está en zona de paz, casteando, atacando o teleportando [B0 §5].

### 9.3 AutoSkills / AutoBuffs / AutoPlay (mapeo conceptual)

| Lista/Knob | Contenido conceptual para HEALER | Fundamento | Estado |
|---|---|---|---|
| `_autoBuffs` | heals de **grupo/área** (PARTY/AURA_FRIENDLY/AREA_FRIENDLY) + self-buffs; heals `ONE` **solo si** se resuelve el targeting explícito (futuro) | B0 §9/§14: las PARTY se resuelven solas; las ONE requieren target | VERIFIED (mecánica); composición PROPOSED |
| `_autoSkills` | vacío o mínimo (el healer no debería gastar ticks ofensivos); cualquier skill ofensiva exigiría `isAutoPlaying()` + target atacable | B0 §6 | PROPOSED |
| `_autoActions` | **NO incluir `2`** | si el healer auto-ataca, `isAttackingNow` bloquea BUFFS → **no cura mientras golpea** [B0 §14 restricción 3] | VERIFIED (mecánica); decisión PROPOSED |
| targetMode | 1 (Monster) para moverse con la party (AutoPlay solo mueve/ataca monstruos) | B0 §7 | PROPOSED |
| `autoPotionPercent` | aplicable al propio bot | AutoUse potion block [B0 §6] | VERIFIED (mecanismo) |

**Lo que Mobius resuelve nativamente para un HEALER bot (VERIFIED):** heals de grupo/área sobre toda la party en radio, self-buffs, re-cadencia por reuse, autopotion propio, movimiento/assist con AutoPlay.

**Lo que NO resuelve (NOT FOUND [B0 §15]):** seleccionar el miembro herido (no existe selector por HP), umbral de HP de aliados, fijar target a un aliado (`setTarget(ally)` no ocurre nunca nativamente), resucitar (target muerto nunca es seleccionado), MP-heal a terceros con skills `ONE`.

## 10. SUPPORT

### 10.1 Equipment (autoridad: 06-C §15.7)

| Grade | Armor (MAGIC o LIGHT — sin restricción de clase [06-C §16.5]) | Weapon | Estado |
|---|---|---|---|
| NONE | 1101 + 1104 + 44 | 6 Apprentice's Wand | VERIFIED / RECOMMENDATION |
| D | MAGIC 436/437 o LIGHT 394/395 | 188/189 (BLUNT m72-m79) | VERIFIED / RECOMMENDATION |
| C | MAGIC 439/441/442 o LIGHT 397/398/400/401 | 206 Demon's Staff | VERIFIED / RECOMMENDATION |
| B | MAGIC 2397/2398/2399 o LIGHT 2384/2390-2392 | 210 Staff of Evil Spirits | VERIFIED / RECOMMENDATION |
| A | MAGIC 2407/2408/2409/2400 o LIGHT 2385/2393-2395 | 8688/151/213 | VERIFIED / RECOMMENDATION |
| S | MAGIC 6383 o LIGHT 6379 | 6579/6366/9444/9449 | VERIFIED / RECOMMENDATION |

La elección MAGIC vs LIGHT es decisión de diseño del perfil (no hay regla nativa) [06-C §15.7 nota].

### 10.2 Skills (autoridad: 06-D §11.5/§11.6)

| Comportamiento | Skills citadas | targetType habitual | Estado |
|---|---|---|---|
| Buffs de stats party | 1068 Might, 1040 Shield, 1204 Wind Walk, 1077 Focus, 1086 Haste, 1085 Acumen, 1059 Empower, 1087 Agility, 1240 Guidance, 1242 Death Whisper, 1035 Mental Shield, 1036 Magic Barrier, 1045 Blessed Body, 1048 Blessed Soul, 1388 Greater Might, 1389 Greater Shield, 1390 War Chant, 1391 Earth Chant, 1397 Clarity, 1268 Vampiric Rage, 1243 Bless Shield, 1304 Advanced Block, 1043 Holy Weapon, 1542 Counter Critical [06-D §11.5/§11.6] | PARTY / AURA / ONE / SELF | VERIFIED (IDs); targetType por skill PENDING censo individual |
| Dances | 271-277, 307, 309, 310, 311, 365, 915 [06-D §11.5] | PARTY/AURA | VERIFIED (IDs); targetType PENDING |
| Songs | 264-270 [06-D §11.5] | PARTY/AURA | VERIFIED (IDs); targetType PENDING |
| Chants orc (clan/party) | 1256/1305/1416 (`PARTY_CLAN`) [B0 §19] | PARTY_CLAN | VERIFIED |
| Categorías | PARTY_BUFF 178; SELF_BUFF 646; SINGLE_BUFF 327 [06-D §7.2] | — | VERIFIED (categorías) |

### 10.3 AutoSkills / AutoBuffs / AutoPlay (mapeo conceptual)

| Lista/Knob | Contenido conceptual para SUPPORT | Fundamento | Estado |
|---|---|---|---|
| `_autoBuffs` | buffs de grupo con targetType PARTY/AURA (se auto-resuelven sobre la party en radio) + self-buffs; **los `ONE` requieren targeting explícito (futuro)** | B0 §10: PARTY/AURA self-cast; ONE/PARTY_MEMBER/TARGET_PARTY exigen target | VERIFIED (mecánica); composición PROPOSED |
| `_autoSkills` | vacío o mínimo | B0 §14 | PROPOSED |
| `_autoActions` | **NO incluir `2`** (mismo razonamiento que HEALER: atacar bloquea BUFFS) | B0 §5/§14 | VERIFIED (mecánica); decisión PROPOSED |
| targetMode | 1 (Monster) | B0 §7 | PROPOSED |

Diferencia operativa clave HEALER vs SUPPORT (INFERRED): el SUPPORT con targetType PARTY/AURA refresca el buff de **toda la party en radio** en cada recast (el handler resuelve la lista), mientras que un buff `ONE` sobre un miembro concreto solo se refresca sobre **el target actual o self** (el chequeo de presencia `canCastBuff` se hace sobre `playableTarget` = target actual o self) [B0 §5]. Perfiles con buffs `ONE` por miembro necesitarían targeting explícito o quedarían limitados a self.

---

## 11. Matriz multiclass


### 11.1 Clases reales por rol (classIds VERIFIED [06-D §5.6]; agrupación por rol = RECOMMENDATION/PROPOSED [06-C §15, 06-D §16])

| Rol | Variante de clase (1ª→2ª→3ª) | classId 3ª clase | Diferencias relevantes para el perfil |
|---|---|---|---|
| TANK | Paladin→PhoenixKnight | 90 | humano; HEAVY + sword + shield |
| TANK | DarkAvenger→HellKnight | 91 | humano; skillset propio (p.ej. aggro, no censado aquí) |
| TANK | TempleKnight→Eva'sTemplar | 99 | elfo |
| TANK | ShillienKnight→ShillienTemplar | 106 | delf; cubics/summons propios (no censados aquí) |
| TANK | **Kamael: sin tank clásico en el TARGET** | — | hueco documentado; Kamael además bloqueado por `races` en muchas piezas [06-C §16.5] |
| DPS melee | Gladiator→Duelist | 88 | **perfil actual DPS_FIGHTER = classId 2 (Gladiator)**; DUAL |
| DPS melee | Warlord→Dreadnought | 89 | POLE |
| DPS melee | Destroyer→Titan | 113 | orco, 2H BLUNT |
| DPS melee | Tyrant→GrandKhavatari | 114 | orco, DUALFIST |
| DPS dagger | TreasureHunter→Adventurer; PlainsWalker→WindRider; AbyssWalker→GhostHunter | 93/101/108 | LIGHT + DAGGER; ¿agrupar bajo DPS? decisión abierta |
| DPS archer | Hawkeye→Sagittarius; SilverRanger→MoonlightSentinel; PhantomRanger→GhostSentinel | 92/102/109 | LIGHT + BOW; ídem |

| MAGE nuker | Sorcerer→Archmage | 94 | humano |
| MAGE nuker | Spellsinger→MysticMuse | 103 | elfo |
| MAGE nuker | Spellhowler→StormScreamer | 110 | delf |
| MAGE summoner | Warlock→ArcanaLord; ElementalSummoner→ElementalMaster; PhantomSummoner→SpectralMaster; Necromancer→Soultaker | 96/104/111/95 | comportamiento distinto (SUMMON/SERVITOR); ¿mismo rol MAGE o batería propia? — decisión abierta |
| MAGE (Kamael) | MaleSoulBreaker→MaleSoulHound; FemaleSoulBreaker→FemaleSoulHound | 132/133 | caster Kamael; restricciones de raza en piezas [06-C §16.5] |
| HEALER | Bishop→Cardinal | 97 | heals completas |
| HEALER | ElvenElder→Eva'sSaint | 105 | heals + buffs elfo |
| HEALER | ShillienElder→ShillienSaint | 112 | heals + buffs delf |
| SUPPORT | Prophet→Hierophant | 98 | buffer puro |
| SUPPORT | Warcryer→Doomcryer | 116 | chants (incl. heal de party 1229 Chant of Life [B0 §19]) |
| SUPPORT | Swordsinger→Swordmuse; Bladedancer→SpectralDancer | 100/107 | dances/songs (híbrido melee-caster) |
| SUPPORT (clan) | Overlord→Dominator | 115 | buffs de clan (PARTY_CLAN); alcance party vs clan sin verificar |

### 11.2 Qué clases NO deberían agruparse sin más (INFERRED/PROPOSED)

1. **Summoners bajo MAGE**: el ciclo de un summoner (re-invocación, buffs de servitor, targetTypes SUMMON/SERVITOR/OWNER_PET) no queda descrito por "nukes en `_autoSkills` + sin acción 2". 06-D §16.1 los trata como fila aparte.
2. **Archers/daggers bajo DPS melee**: comparten rol pero NO arquetipo de arma ni condiciones (`<using kind>` distinto; BOW ocupa `lrhand` y deshabilita escudo; DUALDAGGER con `categoryType` de clase) ni ladder (LIGHT vs HEAVY).
3. **Warcryer como HEALER**: tiene heal de party (1229) pero su núcleo es chant/buff (SUPPORT). Evidencia insuficiente para cerrar la agrupación.
4. **Overlord/Dominator como SUPPORT de party**: sus skills clave son PARTY_CLAN (clan). El handler existe [B0 §10], pero el alcance real en party de bots (¿clan compartido?) está sin verificar.
5. **Inspector (135) / Judicator (136)**: árboles atípicos (anomalía A15 [06-D §15.2]); no agrupar sin censo propio.

### 11.3 Skills comunes vs específicos

* **Comunes a todo bot (VERIFIED, categorías [06-D §7]):** pasivas (P), toggles (T), self-buffs (SELF_BUFF 646).
* **Comunes dentro de un arquetipo (VERIFIED, IDs [06-D §11.5/§11.6]):** la familia de buffs Might/Shield/Focus/Haste/DW/Acumen/Empower/dances/songs; el detalle de qué clase aprende qué nivel está en 06-D §5.7 (índice maestro, regenerable).
* **Específicos por variante (VERIFIED estructura [06-D §15.2]):** los skill trees son INCREMENTALES (3ª clase = 9-34 entradas nuevas, 76-85). Por tanto **dos variantes del mismo rol NO comparten skill profile completo**; la batería necesita el índice por clase para cerrar loadouts por variante (PENDING de generar).

---

## 12. Equipment ↔ Role

| Rol | Armor | Weapon (slot nativo) | Extra | Grado |
|---|---|---|---|---|
| TANK | HEAVY | 1H `rhand` + escudo `lhand` (regla nativa) | — | ladder NONE→S [06-C §20.2]; S80/S84 PROPOSED [U4] |
| DPS_FIGHTER melee | HEAVY | DUAL/POLE/2H `lrhand` | — | ídem |
| DPS_FIGHTER dagger | LIGHT | DAGGER `rhand` | DUALDAGGER: verificar categoryType | ídem |
| DPS_FIGHTER archer | LIGHT | BOW `lrhand` | `lhand` = flechas (EtcItem) | ídem |
| MAGE | MAGIC/robe | BLUNT/SWORD caster (`rhand` o `lrhand`) | sigil S/S80/S84 con arma `rhand` | ídem |
| HEALER | MAGIC/robe | BLUNT (staff/mace) | — | ídem |
| SUPPORT | MAGIC o LIGHT | BLUNT (staff) | — | ídem |

Reglas transversales (todas VERIFIED [06-C §16]): `onepiece` XOR `legs`; `lrhand` XOR escudo; jewelry 1 neck + 2 ears + 2 rings (2 instancias del mismo ID); grado superior penaliza (no bloquea); sin restricciones de clase en piezas; restricciones de raza presentes (Kamael); preferir IDs base (no variantes SA/elementales [A13]).

Escalera de grados del proyecto (PROPOSED, no regla nativa [06-C §20.2]): NONE <20 · D 20-39 · C 40-51 · B 52-60 · A 61-75 · S ≥76 · S80/S84 sin mapeo nativo.

## 13. Skills ↔ Role

| Rol | Categorías primarias que lo definen [06-D §7.1] | targetType dominante | Condiciones nativas relevantes |
|---|---|---|---|
| TANK | SELF_BUFF, CROWD_CONTROL, DEBUFF, (aggro: GetAgro/AddHate — IDs PENDING) | SELF / ONE / AURA | `player hp<=`, `target aggro`, `mindistance` |
| DPS_FIGHTER | PHYSICAL_ATTACK, SELF_BUFF, CROWD_CONTROL | SELF / ONE | `using kind/slot`, `player hp` (Frenzy 176) |
| MAGE | MAGIC_ATTACK, SELF_BUFF, DEBUFF | ONE / AREA | `player mp` (operador PENDING), `using kind` |
| HEALER | HP_HEAL, MP_HEAL (recharge), RESURRECTION, CP_HEAL, SELF_BUFF | ONE / PARTY / AURA_FRIENDLY / AREA_FRIENDLY / CORPSE / PC_BODY | `player hp` propio; HP de party NO existe como condición XML |
| SUPPORT | PARTY_BUFF, SELF_BUFF, SINGLE_BUFF | PARTY / AURA / PARTY_CLAN | chequeo de buff ausente por API (`EffectList`), no XML |

Regla de clasificación por comportamiento (VERIFIED [06-D §4.1/§7.1]): el nombre NO determina comportamiento; se clasifica por `operateType + targetType + effects + conditions`. Esta matriz usa esas categorías, no nombres.

## 14. AutoSkills ↔ Role

| Rol | `_autoSkills` conceptual | Restricciones nativas que hereda |
|---|---|---|
| TANK | rotación ofensiva + aggro | requiere `isAutoPlaying()`; target `isAutoAttackable`; `target==player` → break; round-robin sin pesos [B0 §6] |
| DPS_FIGHTER | rotación de daño (orden inserción) | ídem; caso especial 254 Spoil [B0 §4] |
| MAGE | nukes | ídem; al castear, bloquea BUFFS ese tick [B0 §5] |
| HEALER | vacío o mínimo (PROPOSED) | una skill ofensiva del healer competiría con heals por `isCastingNow` [B0 §5/§6] |
| SUPPORT | vacío o mínimo (PROPOSED) | ídem |

NOTA VERIFIED: el bloque SKILLS **solo corre con AutoPlay activo** (`isAutoPlaying()`), mientras que BUFFS corre siempre que `startAutoUseTask` esté activo [B0 §6].

## 15. AutoBuffs ↔ Role

| Rol | `_autoBuffs` conceptual | Comportamiento nativo |
|---|---|---|
| TANK | self-buffs defensivos | self-cast con truco `setTarget(self)→doCast→restore` si el target no es playable [B0 §5] |
| DPS_FIGHTER | self-buffs ofensivos (+toggles según clasificación por skill) | ídem |
| MAGE | self-buffs mágicos | ídem |
| HEALER | heals PARTY/AURA_FRIENDLY/AREA_FRIENDLY + self-buffs; (heals ONE solo con target explícito) | PARTY resuelve lista nativa; ONE castea sobre target actual si es aliado de party/inocente, si no self [B0 §5/§9] |
| SUPPORT | buffs PARTY/AURA/PARTY_CLAN + self-buffs | PARTY/AURA self-resuelven; PARTY_CLAN via PartyClan [B0 §10] |

Condiciones de ejecución del bloque BUFFS (VERIFIED [B0 §5]): `ENABLE_AUTO_SKILL`; no estar en zona de paz; no castear; no atacar; no teleportar. Por skill: conocido por player/summon/pet (si no → remove); presencia del buff con margen 3 s; distancia <= castRange; MP/items/reuse/checkCondition.

## 16. AutoPlay ↔ Role

| Rol | action 2 | targetMode | Efecto nativo |
|---|---|---|---|
| TANK | SÍ | 1 Monster | persigue y auto-ataca (melee) |
| DPS_FIGHTER melee | SÍ | 1 | ídem |
| DPS_FIGHTER dagger | SÍ | 1 | ídem |
| DPS_FIGHTER archer | decisión abierta: con action 2 auto-ataca melee; sin ella `isMageCaster` solo mueve (dist>900 MoveTo). El disparo automático con arco NO está verificado como comportamiento distinto [PENDING] | 1 | — |
| MAGE | NO | 1 | mueve hacia el target; nukes via SKILLS loop |
| HEALER | NO (para no bloquear BUFFS) | 1 | se mueve con la party; no interrumpe heals |
| SUPPORT | NO | 1 | ídem |

(VERIFIED: `isMageCaster(player)` = ausencia de acción 2 en `_autoActions` [B0 §7]; `AssistLeader=True` en la config del TARGET [VERIF 06-B].)

## 17. Targeting ↔ Role

Modelo nativo (VERIFIED [B0 §11]):

```text
target = player.getTarget()   ← lo fija el cliente (humano) o código server-side
AutoPlay  → SOLO objetivos atacables (monstruo/NPC/playable isAutoAttackable); JAMÁS un aliado
AutoUse BUFFS → target actual si es playable inocente o misma party; si no → self
AutoUse SKILLS → target actual SOLO si isAutoAttackable
TargetType handlers (datapack) → resuelven la LISTA final de objetivos según targetType
```

| Situación | TANK | DPS | MAGE | HEALER | SUPPORT |
|---|---|---|---|---|---|
| Self (buff/heal propio) | nativo | nativo | nativo | nativo | nativo |
| Enemy (monstruo) | nativo (AutoPlay) | nativo | nativo | AutoPlay solo para moverse/atacar; SKILLS loop si `_autoSkills` | ídem |
| Party (targetType PARTY/AURA/PARTY_CLAN) | n/a | n/a | n/a | **nativo**: self-cast y el handler resuelve la lista | **nativo** |
| Explicit ally (ONE/PARTY_MEMBER/TARGET_PARTY sobre un miembro) | n/a | n/a | n/a | **NO resuelto por Mobius**: requiere `setTarget(aliado)` externo (futuro RoleStrategy); AutoPlay nunca targetea aliados | ídem |
| Dead ally (resurrección) | n/a | n/a | n/a | NO resuelto (target muerto nunca seleccionado; `canCastBuff` lo permite solo con targetType CORPSE/PC_BODY) | ídem |

Referencia de patrón (NO comportamiento de Player): el AI de monstruos healer NPC hace `setTarget(leader) → doCast(heal) → setTarget(saved)` y usa `World.getFirstVisibleObjectInRange` para targets `ONE` [UPSTREAM AttackableAI:1067-1160, B0 §9]. Es el único "party heal targeting" existente en el producto y es NPC-side.

**Qué resuelve nativamente Mobius (para HEALER/SUPPORT):** heals/buffs de grupo y área sobre la party en radio; self-buffs; cadencia por reuse; autopotion propio; movimiento/assist/attack con AutoPlay.
**Qué NO resuelve:** seleccionar miembro herido (no hay selector por HP en AutoUse/AutoPlay/PlayerAI [B0 NF1/NF7]); fijar target a un aliado; resucitar; buffs/heals `ONE` sobre terceros.
**Cuándo haría falta seleccionar explícitamente un miembro:** para heals `ONE` potentes (p.ej. 1217/1401) por necesidad real de HP, para buffs `ONE` por miembro, y para resurrección. Todo ello queda como **diseño futuro** (PROPOSED), apoyado en APIs nativas ya verificadas (`Party.getMembers()`, `getCurrentHp()/getMaxHp()`, `setTarget(...)`) [06-D §12.4; B0 §13].

---

## 18. Limitaciones nativas

### 18.1 El bloque BUFFS no corre mientras el bot ataca o castea (hallazgo B0)

Mecánica VERIFIED [B0 §5]: el bloque BUFFS de AutoUse se salta si `isInPeaceZone || isCastingNow || isAttackingNow || isTeleporting`.

Consecuencia arquitectónica por rol (INFERRED, documentada — NO se resuelve aquí):

| Rol | Consecuencia |
|---|---|
| HEALER | Si el healer auto-ataca (acción 2) o castea nukes/debuffs ofensivos, **deja de curar durante esos ticks**. El perfil HEALER debe: (a) no llevar acción 2, (b) no llevar `_autoSkills` ofensivos, o (c) aceptar exclusión mutua curar/atacar por tick. Curar y atacar son mutuamente excluyentes por diseño del motor. |
| SUPPORT | Igual que HEALER: buffear y atacar compiten. Un support que golpea no refresca buffs de party mientras ataca. |
| MAGE | Cada nuke (`isCastingNow`) salta el bloque BUFFS: los self-buffs se re-aplican solo entre casts. Con buffs de larga duración es tolerable; con buffs cortos se pierde cobertura. |
| DPS con buffs | El DPS melee con acción 2 está **casi siempre** en `isAttackingNow` → sus `_autoBuffs` solo se ejecutan en los gaps entre ataques/while moving/idle. Un DPS auto-buffeado tendrá cobertura de buffs irregular y dependiente del combate. Este es el hallazgo más importante de B0 para el diseño de perfiles. |

No se propone aquí ninguna solución de código (fuera de alcance). Queda registrado como restricción que la futura RoleStrategy debe asumir o mitigar por composición de listas.

### 18.2 `DISABLED_AUTO_SKILLS` = 42 (config del TARGET)

* `game\config\Custom\AutoPlay.ini`: `DisabledSkillIds = 42` [VERIF 06-B]. La constante Java es `AutoPlayConfig.DISABLED_AUTO_SKILLS` [B0 §13/A8].
* El skill 42 es **Sweeper** (dwarf; usar en cadáveres de monstruos spoiled) [VERIF 06-B, `skills\00000-00099.xml`].
* Dónde se aplica (VERIF 06-B): **solo** en el voiced command `.play` (construcción del menú de skills candidatas, `AutoPlay.java:308-333`; ídem `DISABLED_AUTO_ITEMS` en :507/:609). **No** hay evidencia de que AutoUseTaskManager/AutoPlayTaskManager consulten esta lista en runtime.
* Impacto en perfiles: el skill 42 (Sweeper) no pertenece al núcleo de ninguno de los 5 roles → **sin impacto directo hoy**. Para provisioning server-side, respetar la lista es una **decisión de diseño del perfil** (no hay enforcement nativo fuera del voiced command) [INFERRED]. Si el perfil decide copiar la regla del voiced command, además de `DISABLED_AUTO_SKILLS` el menú nativo excluye `isPassive()`/`isToggle()`.

### 18.3 Otras limitaciones nativas (VERIFIED, heredadas de B0/06-D)

| # | Limitación | Fuente |
|---|---|---|
| L1 | AutoPlay jamás selecciona un aliado como target | B0 §7 (`isTargetModeValid`) |
| L2 | SKILLS loop nunca castea sobre aliados (`!isAutoAttackable` → break) | B0 §6 |
| L3 | No hay selector de miembro herido ni umbral de HP de terceros | B0 NF1/NF7 |
| L4 | No hay condición XML de HP de target ni de miembro de party ni de "buff ausente" | 06-D §8.4/§11.7/§22.2 |
| L5 | No hay prioridad/pesos por skill en `_autoSkills` (solo orden inserción + round-robin) | B0 NF5 |
| L6 | Cambio de clase limpia `_autoSkills` y `_autoBuffs` (`Player.java:10839-10840`) | B0 §4 |
| L7 | Persistencia de AutoUse solo vía voiced command (logout) u OfflinePlayTable (boot); los bots provisionados server-side NO persisten listas por esos caminos | B0 §12/A7 |
| L8 | Los skills ofensivos requieren `isAutoPlaying()`; los buffs/heals no | B0 §6 |
| L9 | BUFFS no corre en zona de paz (no hay "pre-buff en town" automático) | B0 §5 |
| L10 | `player mp` como condición XML: operador exacto no verificado | 06-D §8.3 |
| L11 | El voiced command no registra pasivas ni toggles (criterio propio del menú; clasificación de toggles para `_autoBuffs` = decisión del perfil) | VERIF 06-B AutoPlay.java:308-333 |

## 19. Conflictos / incompatibilidades

| # | Conflicto | Impacto en perfiles | Estado |
|---|---|---|---|
| C1 | BUFFS vs ataque/casteo (§18.1) | HEALER/SUPPORT sin acción 2; DPS con cobertura de buffs irregular | VERIFIED mecánica |
| C2 | TANK: dual/pole vs escudo | El perfil TANK no puede optimizar daño con dual | VERIFIED [06-C §16.2] |
| C3 | Archer: BOW `lrhand` vs escudo/flechas | `lhand` reservado a flechas | VERIFIED [06-C §15.4/§16.2] |
| C4 | Sigil vs escudo (mismo `lhand`) | Caster con sigil excluye escudo | INFERRED [06-C U5] |
| C5 | `onepiece` vs `legs` | Ladders deben elegir un solo esquema de set | VERIFIED [06-C §16.2/A5] |
| C6 | `<using kind>` de skills vs arma equipada | Cambiar arma puede desactivar skills del profile | VERIFIED [06-D §18.1] |
| C7 | Cambio de clase limpia listas AutoUse | La batería multiclass (re-provisioning de clase) debe re-aplicar listas | VERIFIED [B0 §4] |
| C8 | Dynasty = S (no S80) y Moirai = S80 en nombres/grades del datapack | No asumir grado por nombre | VERIFIED [06-C A4/A11] |
| C9 | 852 es C-grade aunque se usó como A en ladders previos | Evitar 852 en ladders A | VERIFIED [06-C A3] |
| C10 | Kamael bloqueado por `races` en piezas no-Kamael | Variantes Kamael necesitan ladder propio | VERIFIED [06-C §16.5] |
| C11 | Regla `hasNegativeEffect` puede clasificar "raro": una skill de aggro con effectPoint >= 0 iría a `_autoBuffs` | Verificar cada skill de aggro antes de asignar lista | INFERRED (mecánica VERIFIED, casos concretos PENDING) |

## 20. Datos todavía faltantes

| # | Dato | Estado | Dónde se cerraría |
|---|---|---|---|
| P1 | IDs de skills de aggro (Aggression/Hate/...) y su `hasNegativeEffect` real | PENDING | parseo 06-D §5.7 + skills XML |
| P2 | Skill profile exacto por clase (932 skills × 103 clases) | PENDING (derivable, NO incrustado) | 06-D §5.7 índice maestro (regenerable) |
| P3 | Armas S80/S84 por rol | NOT VERIFIED | 06-C U3 (cruce §13.2 × §15) |
| P4 | Mapeo nivel→grado S80/S84 | PROPOSED sin cerrar | 06-C U4 |
| P5 | targetType por skill concreta de buffs/heals (más allá de la tabla B0 §19/06-D §11) | PENDING | parseo skills XML |
| P6 | Toggles aprendibles por clase y su clasificación (operateType T → ¿`_autoBuffs`?) | PENDING | 06-D §5.7 + decisión de diseño |
| P7 | Operador exacto de `<player mp=>` | PENDING | 06-D §8.3 |
| P8 | Comportamiento arco en AutoPlay (¿auto-ataque a distancia?) | PENDING | spike runtime |
| P9 | ¿PartyClan funciona en party de bots sin clan común? | PENDING | 06-D §12.2/B0 §10 + runtime |
| P10 | Runtime: heal PARTY (1027) en `_autoBuffs` de bot en party cura a todos en radio | PENDING (recomendado como primer spike de perfiles) | B0 §17 pregunta 1 |
| P11 | Runtime: comportamiento de BUFFS con bot atacado teniendo target aliado | PENDING | B0 §17 pregunta 2 |
| P12 | Persistencia de listas AutoUse para bots (¿PlayerVariables, re-aplicación por provisioning, o nada?) | Decisión de diseño pendiente | B0 §12/§17.3 |
| P13 | Dyes/Henna por rol | FUERA DE ALCANCE (ACT1 §13.5) | sprint futuro |
| P14 | SA y augmentations (vínculo SA→skill) | PENDING | 06-D §19 |
| P15 | Documento `BOTAI-06-B2_LOCAL_EQUIPMENT_SKILLS_VERIFICATION.md` citado por 06-C §16.4/§20.4 (matriz de loadout por rol + limpieza de skills, "OPEN RISK R1") | **NOT FOUND en el Notebook** (no existe aún como archivo) | crear/ubicar antes de implementar ladders definitivos |

## 21. Propuestas de diseño (PROPOSED — nada implementado)

### 21.1 Shape de la batería

* **Opción A (compatibilidad con el modelo actual, PROPOSED):** una constante `BotProfile` por (rol × variante de clase), p.ej. `TANK_PHOENIX_KNIGHT`, `HEALER_CARDINAL`. Reutiliza el modelo vigente 1-perfil-1-clase sin tocar `BotProfile.java`; `BotRole` actúa como etiqueta de familia y `AdminBotManager` elegiría por slot.
* **Opción B (PROPOSED):** un `BotProfile` por rol con campo "class variants"; requiere extender el modelo declarativo (clases custom propias, no core).
* En ambas opciones la capa que faltaría es la misma: **mapear rol → (AutoPlaySettings + listas AutoUse)** [B0 §13] y decidir persistencia (P12).

### 21.2 Contenido conceptual por rol (síntesis ejecutable en un sprint futuro)

| Rol | classId inicial sugerido | action 2 | `_autoSkills` | `_autoBuffs` |
|---|---|---|---|---|
| TANK | 5 Paladin (tank clásico) | SÍ | ofensivas+aggro de la clase | self-buffs defensivos |
| DPS_FIGHTER | 2 Gladiator (ya validado BSBOT01) | SÍ | rotación de daño | self-buffs ofensivos |
| MAGE | 12 Sorcerer | NO | nukes | self-buffs mágicos |
| HEALER | 16 Bishop | NO | vacío | heals PARTY/AURA + self-buffs |
| SUPPORT | 17 Prophet | NO | vacío | buffs PARTY/AURA + self-buffs |

(El classId sugerido es RECOMMENDATION/PROPOSED; las variantes multiclass quedan para cerrar §11.)

### 21.3 Reglas que cualquier implementación futura debe respetar (VERIFIED)

1. Clasificar skills con `hasNegativeEffect()` (regla nativa), no por nombre.
2. No reimplementar la resolución de targets de PARTY/AURA (handlers del datapack).
3. Validar cada loadout contra las reglas de slot (06-C §16) y tomar IDs solo de 06-C.
4. Respetar `DISABLED_AUTO_SKILLS` por diseño (no hay enforcement server-side).
5. Re-aplicar listas AutoUse tras cualquier cambio de clase (L6).
6. No duplicar validaciones de casteo/cooldown (motor nativo) [06-D §22.3].
7. Heals `ONE`/resurrección/buffs `ONE` por miembro: NO implementables con el motor actual sin un mecanismo externo de `setTarget(aliado)`; documentar como gap y decidir en un sprint de diseño (no aquí).

## 22. Preguntas abiertas

1. ¿La batería multiclass se implementa como N `BotProfile` (opción A) o extendiendo el modelo (opción B)? → decisión del GM.
2. ¿Qué arquetipos entran en la batería v1? (¿solo melee-DPS como hoy, o también dagger/archer/summoner?) → decisión de alcance.
3. ¿HEALER y SUPPORT se separan por criterio "heals de party en `_autoBuffs`" vs "solo buffs"? ¿Warcryer/Doomcryer van a HEALER (por 1229) o SUPPORT? → decisión de diseño; evidencia insuficiente para cerrarlo.
4. ¿Se exige persistencia de listas AutoUse para bots (P12) antes de la batería? → afecta al orden de sprints.
5. ¿Se requiere spike P10/P11 (runtime de heals de party) antes de declarar HEALER implementable? → recomendado por B0 §17.
6. ¿Quién crea el documento `BOTAI-06-B2` citado por 06-C (P15) — existe en otro workspace o está pendiente de redactar? → verificar antes de implementar ladders.

## 23. Conclusión

1. **Un perfil es describible al 100% con capacidades nativas ya verificadas**: clase/nivel/skills/equipo (BotProvisioning existente) + knobs de AutoPlay + listas de AutoUse. Ninguna pieza exige tocar el core [B0 §13; BotSpikeD01 RUNTIME].
2. **El 06-C cubre equipment por rol con IDs reales** (healer/tank/fighter/archer/dagger/mage/support, §15), reglas de slot (§16) y anomalías documentadas; solo faltan armas S80/S84 por rol y el ladder nivel→grado S80/S84.
3. **El 06-D cubre skills por comportamiento real** (8144 skills, 162 efectos, 14 categorías primarias, 103 clases, condiciones nativas) y demuestra que las reglas por rol son expresables sin condiciones nuevas.
4. **El B0 define el contrato de ejecución**: `_autoSkills` (round-robin, requiere AutoPlay) vs `_autoBuffs` (corre sin AutoPlay, se bloquea al atacar/castear), y el límite duro de targeting de aliados.
5. **El bloqueo BUFFS-durante-ataque/casteo es la restricción dominante del diseño de perfiles**: para HEALER/SUPPORT implica no-atacar; para DPS implica cobertura de buffs irregular. Queda documentado, sin solución en esta tarea.
6. **La batería multiclass es factible pero no trivial**: los skill trees son incrementales por clase, así que "mismo rol" ≠ "mismo skill profile"; se necesita el índice por clase (06-D §5.7) para cerrar loadouts por variante.
7. **El targeting de aliados (heal `ONE`, resurrección, buff `ONE` por miembro) es el único gap estructural** que el motor no resuelve; queda como diseño futuro apoyado en APIs nativas verificadas.

**Estado del sprint:** COMPLETADO (investigación y documentación; sin implementación).

## 24. Evidencia

### 24.1 Documentos (autoridad)

* `investigations/BOTAI-06-C_EQUIPMENT_MASTER_CATALOG.md` — §3.1 (volúmenes), §4-§11 (items por grado), §12 (217 sets), §13 (917 armas base, tipos/slots/pAtk/mAtk), §14 (261 jewelry), §15 (equipment por rol, VERIFIED vs RECOMMENDATION), §16 (reglas de slot/incompatibilidades/raza/expertise), §18 (anomalías A1-A15), §19 (U1-U11), §20 (uso futuro para perfiles, escalera PROPOSED), §21 (conclusión).
* `investigations/BOTAI-06-D_SKILL_BUFF_COMBAT_MASTER_CATALOG.md` — §4 (8144 skills, operateType/targetType/effects), §5 (114 trees, 103 clases, índice §5.7: 932 skills), §6 (skill learning APIs), §7 (clasificación funcional: 14 categorías), §8 (condiciones nativas; §8.3 semántica HP/CP; §8.4 sin ConditionTargetHp), §9 (Frenzy), §10 (combat/casting), §11 (buffs/heals; §11.6 ejemplos por rol), §12 (party APIs + límites), §13 (AutoPlay/AutoUse/PlayerAI), §14 (Scheme Buffer precedente), §15 (class reference; trees incrementales), §16 (role matrix documental), §17 (BotProfile cross-ref), §18 (cross-ref con 06-C: `<using>`), §19 (SA PENDING), §22 (conclusiones/fronteras).
* `investigations/BOTAI-06-B0_AUTO_SKILLS_AUTO_BUFFS_PARTY_TARGETING_RESEARCH.md` — §4 (`_autoSkills`), §5 (`_autoBuffs`, `hasNegativeEffect`, `canCastBuff`), §6 (AutoUse ciclo), §7 (AutoPlay ciclo, `isMageCaster`, `isTargetModeValid`), §9 (party heal: NO hay selección automática), §10 (party buff: resolución por targetType), §11 (modelo de targeting), §12 (persistencia), §13 (relación con BotProvisioning + flujo PROPOSED), §14 (tabla rol→config nativa + restricciones), §15 (NOT FOUND NF1-NF8), §16 (anomalías A1-A8), §19 (tabla heals/buffs de party con targetTypes).
* `bots/BOT_RECIPE.md` — §2/§3 (arquitectura/lifecycle), §8/§9 (classid==base_class), §10/§11 (skill progression, level sync), §12 (AutoPlay RUNTIME), §13-§15 (AutoUse RUNTIME + criterio ofensivo), §4/§6 (party 9 miembros, BOT_IDS).
* `decisions/FASE4_BOT_ARCHITECTURE_FREEZE.md` — arquitectura congelada; NO TOCAR lista; RoleStrategy como pieza futura.
* `investigations/BOTAI-06-ACT1_CONSOLIDATION.md` — criterios 1-15; `BotProfile`/`BotRole`/`BotPresets`/`BotProvisioning`; §13 pendientes para 06-B.
* `investigations/BOTAI-02_ROLES_BEHAVIORS_API_RESEARCH.md` — INV7 ("LA IA DECIDE; MOBIUS EJECUTA"), APIs NOT FOUND (p.ej. `getLowestHpPartyMember` no existe), restricciones operacionales.

### 24.2 Comprobaciones directas de esta tarea [VERIF 06-B] sobre el TARGET

| Comprobación | Resultado | Fuente |
|---|---|---|
| `AutoPlay.ini` real | `EnableAutoPlay=True`, `EnableAutoPotion=True`, `EnableAutoSkill=True`, `EnableAutoItem=True`, `ResumeAutoPlay=False`, `AssistLeader=True`, `ShortRange=600`, `LongRange=1400`, `AutoPlayPremium=False`, `DisabledSkillIds=42`, `DisabledItemIds=` (vacío), `IgnoredAutoPickItems=8190, 8689` | `game\config\Custom\AutoPlay.ini` |
| Identidad del skill 42 | `Sweeper` (levels=1; uso en cadáveres spoiled) | `game\data\stats\skills\00000-00099.xml` |
| Alcance de `DISABLED_AUTO_SKILLS` | Solo se consulta en el voiced command (menú de skills candidatas; también excluye `isPassive()`/`isToggle()`); `DISABLED_AUTO_ITEMS` ídem para items | `game\data\scripts\handlers\chat\commands\voiced\AutoPlay.java:308-333, 507, 609` |
| Estado del modelo declarativo | `BotProfile` (id/role/classId/baseClassId/targetLevel/SkillMode/profileVersion/gradeLadder + `resolveGrade`), `BotRole` (5 valores), `BotPresets` (solo DPS_FIGHTER = Gladiator/2, targetLevel 80, ladder D..S con IDs) | `game\data\scripts\handlers\chat\commands\admin\BotProfile.java`, `BotRole.java`, `BotPresets.java` |
| Existencia de `BOTAI-06-B2_LOCAL_EQUIPMENT_SKILLS_VERIFICATION.md` | No existe en `L2J Notebook\investigations` (citado por 06-C) | listado del directorio |
| Backups preservados | `AdminBotManager.java.bak` y `AdminBotManager.java.bak.20260912-BOTAI01` presentes, intactos | mismo directorio admin |

### 24.3 No verificado en esta tarea (heredado como PENDING)

* Cualquier validación en runtime (spikes) — fuera de alcance.
* Bytecode línea-a-línea de `AutoPlay.run()` (NF8 de B0 sigue PENDING).
* El contenido completo del índice 06-D §5.7 no se ha vuelto a generar en esta tarea (se cita como fuente regenerable).

**Fin del documento BOTAI-06-B.**
