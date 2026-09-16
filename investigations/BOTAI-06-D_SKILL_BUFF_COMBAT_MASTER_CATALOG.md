# BOTAI-06-D - Skill / Buff / Combat Master Catalog

> **SPRINT:** BOTAI-06-D
> **FASE:** Investigacion - Skill / Buff / Combat Master Catalog
> **MODO:** INVESTIGACION + DOCUMENTACION (sin cambios funcionales)
> **TARGET:** `C:\L2J MOBIUS IA\L2J_Mobius_CT_2.6_HighFive`
> **UPSTREAM:** `C:\L2J MOBIUS\UPSTREAM\L2J_Mobius\L2J_Mobius_CT_2.6_HighFive` (SOLO contraste; evidencia marcada UPSTREAM)
> **Estado:** GENERADO DESDE EL DATAPACK REAL DEL TARGET (VERIFIED)
> **Fecha:** 2026-09-14
> **Baseline:** L2J Mobius CT 2.6 HighFive (runtime `libs\GameServer.jar` + datapack)
> **Generadores temporales (fuera del workspace):** `%TEMP%\botai06d\gen1.ps1`, `gen3.ps1`, `gen4.ps1`, `gen5.ps1`

Leyenda de estados: **VERIFIED** | **INFERRED** | **PROPOSED** | **NOT FOUND** | **PENDING**

---

## 1. Scope

Catalogo maestro, generado desde el TARGET real, de todo lo necesario para construir
mas adelante comportamiento configurable para bots SIN duplicar el motor nativo:

- inventario global de skills del datapack;
- skill trees de clase y flujo nativo de aprendizaje;
- clasificacion funcional basada en ESTRUCTURA (operateType / targetType / effects), no en el nombre;
- buffs, heals, resurreccion y combate;
- el sistema nativo de CONDICIONES y su vocabulario real;
- caso de estudio Frenzy;
- casteo/combate nativo y su limite conceptual;
- party y mecanicas de party buff;
- AutoPlay / AutoUse / PlayerAI;
- Scheme Buffer como precedente nativo;
- matriz de roles y referencia de clases;
- cross-reference con BOTAI-06-C (equipment) y con nuestra infra de bots.

**Fuera de alcance (NO tratado):** implementar Scheme / BotAI / ThinkLoop, condiciones nuevas,
perfiles de bots, dyes/henna, SA, y cualquier cambio funcional.

**Alcance de detalle (enfoque B acordado):** agregados globales + detalle completo de las
skills aprendibles por clase (todas las clases, sin limitar a las de interes); monstruos,
eventos y summons se documentan SOLO como agregados. Ver 5.4 para una salvedad de tamano
perfectamente delimitada.

---

## 2. Evidence Method

| Metodo | Fuente | Uso |
|---|---|---|
| `javap -cp libs\GameServer.jar -p <clase>` | TARGET (JAR real) | firmas `class:method` VERIFIED del build real |
| Parseo XML del datapack (PowerShell 5.1) | TARGET `game\data\...` | conteos y campos por skill |
| Grep de fuente Java | UPSTREAM `java\org\l2jmobius\...` | contraste de semantica (marcado UPSTREAM) |

Notas de metodo:

- El JAR del TARGET usa paquetes **refactorizados**: `org.l2jmobius.gameserver.entity`,
  `.mechanics`, `.ai`, `.taskmanagers`, `.handler`, `.data.xml`. NO existe `SkillHandler`
  (ver anomalia A4).
- La extraccion definitiva se hizo con scripts temporales en `%TEMP%\botai06d` (nunca dentro
  del workspace). Los scripts NO forman parte del Notebook; el catalogo es regenerable.
- **IMPORTANTE:** el conteo de EFECTOS es conteo de **instancias de efectos**, NO de skills.
  Ambos se reportan por separado (ver 4.4 y 11.1).
- Detalle de metodo (consola): PowerShell envuelve y trunca salidas largas; por eso los
  volumenes se calcularon a archivo (`Set-Content`) y se leyeron despues, nunca "a ojo".

---

## 3. Global Inventory

| Metrica | Valor | Estado |
|---|---|---|
| XML de skills parseados | 95 (90 en `stats/skills` + 5 en `stats/skills/custom`) | VERIFIED |
| Nodos `<skill>` crudos | 8144 | VERIFIED |
| Nodos `<skill>` activos (sin comentarios) | 8144 | VERIFIED |
| Skill IDs unicos | 8144 (sin duplicados) | VERIFIED |
| Skills con elemento `<conditions>` | 1443 | VERIFIED |
| Skills con condiciones a nivel de efecto | 346 | VERIFIED |
| Archivos de skill trees | 114 (103 de clase + 11 especiales) | VERIFIED |
| Nodos `<skill>` en trees | 17702 | VERIFIED |
| Sentencias de aprendizaje en classSkillTree | 17177 | VERIFIED |
| Nodos en trees especiales (no clase) | 525 | VERIFIED |
| Skill IDs distintos referenciados por trees | 1173 | VERIFIED |
| Skill IDs distintos solo en classSkillTree | 932 | VERIFIED |
| Clases con classSkillTree | 103 | VERIFIED |
| IDs en XML no referenciados por ningun tree | 6971 | VERIFIED |
| IDs referenciados por trees pero ausentes en XML | 0 | VERIFIED |

**Interpretacion:** de 8144 skills declaradas, solo 1173 son aprendibles (clase / clan /
hero / noble / subclass / transform / etc.); las 6971 restantes son skills de NPC, monstruo,
evento, summon o auxiliares internas, y se documentan solo como agregados (enfoque B).
---

## 4. Skill XML Inventory

### 4.1 Formato real (VERIFIED)

Extraido del TARGET. Ejemplo minimo real (Frenzy, `game\data\stats\skills\00100-00199.xml:3088`):

```xml
<skill id="176" levels="3" name="Frenzy">
  <table name="#mpConsume">14 21 25</table>
  <icon>icon.skill0176</icon>
  <operateType>A2</operateType>
  <targetType>SELF</targetType>
  <abnormalLevel>#abnormalLevels</abnormalLevel>
  <abnormalTime>90</abnormalTime>
  <abnormalType>PINCH</abnormalType>
  <hitTime>1500</hitTime>
  <magicLevel>#magicLevel</magicLevel>
  <mpConsume>#mpConsume</mpConsume>
  <reuseDelay>300000</reuseDelay>
  <conditions msgId="113" addName="1">
    <player hp="60" />
  </conditions>
  <effects>
    <effect name="Buff">
      <mul stat="pAtk">#all</mul>
    </effect>
  </effects>
</skill>
```

Observaciones de estructura:

- `<skill>` declara `id`, `levels` y `name`; los valores por nivel se definen con `<table name="#x">`.
- Los escalares pueden ser literales o referencias `#tabla`.
- Los efectos van en `<effects><effect name="...">`, con funciones de stat (`mul`, `add`, `set`, ...).
- Las condiciones pueden aparecer en DOS lugares distintos (ver 9): en `<conditions>` y dentro
  de bloques `<and>` de los efectos.
- El nombre del skill NO determina su comportamiento; el comportamiento se determina por
  operateType + targetType + effects + condiciones.

### 4.2 operateType (VERIFIED)

| operateType | Significado | Skills |
|---|---|---|
| A1 | active (perfil magico) | 2959 |
| A2 | active (perfil fisico) | 2697 |
| P | passive | 2356 |
| T | toggle | 71 |
| A3 | active (variante) | 16 |
| CA1 | channeled active | 14 |
| DA1 | active (dance/song) | 14 |
| CA5 | channeled active (variante) | 9 |
| DA2 | active (dance/song, variante) | 7 |
| A4 | active (variante) | 1 |
| **Total** | | **8144** |

### 4.3 targetType (VERIFIED)

| targetType | Skills | | targetType | Skills |
|---|---|---|---|---|
| SELF | 4330 | | PARTY_CLAN | 21 |
| ONE | 1678 | | PARTY_MEMBER | 17 |
| NONE | 1158 | | CLAN_MEMBER | 12 |
| AURA | 343 | | GROUND | 7 |
| AREA | 179 | | CORPSE_MOB | 7 |
| PARTY | 124 | | AREA_CORPSE_MOB | 5 |
| FRONT_AREA | 88 | | FRONT_AURA | 5 |
| CLAN | 42 | | BEHIND_AURA | 4 |
| UNLOCKABLE | 31 | | TARGET_PARTY | 3 |
| PC_BODY | 25 | | AREA_SUMMON | 3 |
| SERVITOR | 23 | | COMMAND_CHANNEL | 2 |
| OWNER_PET | 21 | | PARTY_NOTME | 2 |
| (vacio) | 2 | | ENEMY_SUMMON | 2 |

Valores con 1 sola ocurrencia: `CORPSE_CLAN`, `PARTY_OTHER`, `FLAGPOLE`, `SUMMON`, `CORPSE`,
`AREA_FRIENDLY`, `AURA_CORPSE_MOB`, `AURA_FRIENDLY`, `HOLY`, y el placeholder `#targetType`
(ver anomalia A13).

### 4.4 Effects (VERIFIED)

**Instancias de efecto vs skills que los contienen** (NO confundir; ver 11.1):

| Effect | Instancias | Skills | | Effect | Instancias | Skills |
|---|---|---|---|---|---|---|
| Buff | 2471 | 2246 | | Escape | 37 | 37 |
| PhysicalDamage | 554 | 537 | | Paralyze | 35 | 34 |
| DefenceTrait | 500 | 476 | | CpHeal | 34 | 30 |
| RestorationRandom | 324 | 324 | | Summon | 54 | 30 |
| MagicalDamage | 328 | 311 | | Mute | 30 | 30 |
| Transformation | 267 | 267 | | HealOverTime | 35 | 30 |
| Debuff | 314 | 263 | | TargetCancel | 43 | 30 |
| DamOverTime | 175 | 158 | | Fear | 30 | 29 |
| Stun | 166 | 144 | | FatalBlow | 29 | 29 |
| TriggerSkillByDamageReceived | 145 | 141 | | DispelByCategory | 30 | 28 |
| SetSkill | 137 | 129 | | PhysicalSoulDamage | 34 | 28 |
| SummonAgathion | 117 | 117 | | OpenDoor | 27 | 27 |
| AttackTrait | 109 | 106 | | Resurrection | 27 | 26 |
| DispelBySlot | 105 | 99 | | Invincible | 22 | 22 |
| Heal | 104 | 85 | | ManaDamOverTime | 30 | 22 |
| EnableCloak | 84 | 84 | | DispelBySlotProbability | 22 | 22 |
| Restoration | 70 | 70 | | ManaHealPercent | 21 | 21 |
| HealPercent | 59 | 55 | | ManaHealByLevel | 23 | 21 |
| SummonNpc | 53 | 52 | | VitalityPointUp | 21 | 21 |
| HpDrain | 57 | 51 | | Root | 42 | 41 |
| CallSkill | 46 | 46 | | BlockAbnormalSlot | 42 | 42 |
| TriggerSkillByDamageDealt | 49 | 41 | | NevitsHourglass | 37 | 37 |

Numero total de nombres de efecto distintos en el datapack: **162** (censo completo obtenido).

### 4.5 Presencia de campos (VERIFIED)

No todos los campos existen para todas las skills. Conteo real de declaraciones:

| Campo / elemento | Skills que lo declaran | Nota |
|---|---|---|
| `<magicLevel>` | 7677 | muy comun |
| `<isMagic>` | 4083 | elemento |
| `<hitTime>` | 3651 | elemento |
| `<effectPoint>` | 3497 | agro/hate generado |
| `<reuseDelay>` | 2831 | cooldown |
| `<abnormalTime>` | 2735 | duracion |
| `<abnormalType>` | 2707 | tipo de abnormal |
| `<mpConsume>` | 2149 | coste de MP |
| `<castRange>` | 1944 | rango de casteo |
| `<effectRange>` | 1907 | rango de efecto |
| `<mpInitialConsume>` | 874 | coste inicial |
| `<coolTime>` | 663 | elemento |
| `<hpConsume>` | 39 | coste de HP (raro) |
| `<toggleGroupId>` | 0 | NO USADO en este datapack |

Cualquier campo no listado debe tratarse como **no declarado / N/A** para esa skill.
---

## 5. Skill Trees

### 5.1 Fuentes, formato y tipos (VERIFIED)

Ruta: `game\data\stats\players\skillTrees\`. Contiene 4 carpetas de clase y 11 archivos
especiales en la raiz.

| Origen | Archivos | Bloques | Tipo declarado |
|---|---|---|---|
| `StartingClass\` | 11 | 11 | classSkillTree |
| `1stClass\` | 20 | 20 | classSkillTree |
| `2ndClass\` | 36 | 36 | classSkillTree |
| `3rdClass\` | 36 | 36 | classSkillTree |
| `heroSkillTree.xml` | 1 | 1 | heroSkillTree |
| `nobleSkillTree.xml` | 1 | 1 | nobleSkillTree |
| `pledgeSkillTree.xml` | 1 | 1 | pledgeSkillTree |
| `subPledgeSkillTree.xml` | 1 | 1 | subPledgeSkillTree |
| `subClassSkillTree.xml` | 1 | 1 | subClassSkillTree |
| `transferSkillTree.xml` | 1 | **3** | transferSkillTree |
| `transformSkillTree.xml` | 1 | 1 | transformSkillTree |
| `collectSkillTree.xml` | 1 | 1 | collectSkillTree |
| `fishingSkillTree.xml` | 1 | 1 | fishingSkillTree |
| `gameMasterSkillTree.xml` | 1 | 1 | gameMasterSkillTree |
| `gameMasterAuraSkillTree.xml` | 1 | 1 | gameMasterAuraSkillTree |
| **Total** | **114** | **116** | |

**Dos sintaxis de entrada coexisten** (hallazgo relevante):

```xml
<!-- Forma A: self-closing (mayoria de classSkillTree) -->
<skill skillName="Wisdom" skillId="328" skillLevel="1" getLevel="76" levelUpSp="10000000" learnedByNpc="true" />

<!-- Forma B: con cuerpo y cierre (pledge / subClass / subPledge / transfer / transform / fishing / collect) -->
<skill skillName="Clan Body" skillId="370" skillLevel="1" getLevel="5" levelUpSp="1500">
  <item id="..." />
</skill>
```

Un parser que solo acepte la Forma A pierde 564 entradas y subestima los IDs distintos
(932 -> 1034 en la primera pasada). Esto queda registrado como **A7 (resuelto)**.

Cabecera de cada bloque:

```xml
<skillTree type="classSkillTree" classId="113" parentClassId="46">
```

- `type`: tipo de arbol; `classId`: clase que aprende; `parentClassId`: clase padre.
- Atributos por entrada: `skillName`, `skillId`, `skillLevel`, `getLevel`, `levelUpSp`,
  `learnedByNpc`, `learnedByFS`.

### 5.4 Cobertura de este catalogo y salvedad de tamano (VERIFIED)

- **Cobertura completa por clase:** 5.6 lista las 103 clases con classSkillTree, con
  entradas, aprendidas por NPC/FS y rango de nivel de aprendizaje.
- **Cobertura completa por skill aprendible:** 5.7 lista las **932 skills distintas**
  aprendibles por clase (id, nombre, nivel maximo, clases que la aprenden y niveles de
  aprendizaje). Ninguna clase queda excluida.
- **Salvedad declarada:** el listado exhaustivo de los **17177 pares (clase, entrada)** no se
  incrusta en este Markdown por tamano (aprox. 1.2 MB de TSV). Dicho dato es **mecanicamente
  reconstruible** desde 5.6 + 5.7 y esta disponible en
  `%TEMP%\botai06d\trees_detail.tsv` (generado por `gen3.ps1`). No es informacion perdida:
  es informacion derivable, y se declara aqui de forma explicita para no confundir ausencia
  de incrustacion con ausencia de dato.

### 5.5 Cruce XML de skills vs skill trees (VERIFIED)

| Comprobacion | Resultado |
|---|---|
| IDs duplicados en skills XML | 0 |
| IDs unicos en skills XML | 8144 |
| Skills en XML no referenciadas por ningun tree | 6971 |
| Skills referenciadas por trees ausentes en XML | 0 |
| IDs distintos en trees (todos los tipos) | 1173 |
| IDs distintos en classSkillTree | 932 |
| Entradas `learnedByNpc="true"` en trees | 16735 |
| Entradas `learnedByFS="true"` en trees | 340 |

Conclusion: los skill trees son un subconjunto consistente del XML (no hay referencias rotas).
Las 6971 skills no referenciadas corresponden a NPC/monstruo/evento/summon/auxiliares.

### 5.6 Tabla de clases con classSkillTree (GENERADA, VERIFIED)

`entries` = sentencias de aprendizaje declaradas en el arbol PROPIO de la clase
(los arboles son incrementales; ver 15.2). `npc` = entradas learnedByNpc=true;
`FS` = entradas learnedByFS=true. `minLvl`/`maxLvl` = rango de `getLevel`.

| classId | Clase | parentClassId | entries | npc | FS | minLvl | maxLvl |
|---|---|---|---|---|---|---|---|
| 0 | HumanFighter |  | 56 | 36 | 2 | 1 | 84 |
| 1 | Warrior | 0 | 104 | 104 |  | 20 | 36 |
| 2 | Gladiator | 1 | 479 | 479 |  | 40 | 76 |
| 3 | Warlord | 1 | 331 | 331 |  | 40 | 76 |
| 4 | HumanKnight | 0 | 93 | 93 |  | 20 | 36 |
| 5 | Paladin | 4 | 426 | 426 |  | 40 | 76 |
| 6 | DarkAvenger | 4 | 409 | 409 |  | 40 | 76 |
| 7 | Rogue | 0 | 99 | 99 |  | 20 | 76 |
| 8 | TreasureHunter | 7 | 281 | 280 | 1 | 40 | 74 |
| 9 | Hawkeye | 7 | 248 | 248 |  | 40 | 76 |
| 10 | HumanMystic |  | 61 | 36 | 2 | 1 | 84 |
| 11 | HumanWizard | 10 | 122 | 122 |  | 20 | 35 |
| 12 | Sorcerer | 11 | 371 | 371 |  | 40 | 76 |
| 13 | Necromancer | 11 | 445 | 445 |  | 40 | 76 |
| 14 | Warlock | 11 | 357 | 357 |  | 40 | 76 |
| 15 | Cleric | 10 | 120 | 120 |  | 20 | 35 |
| 16 | Bishop | 15 | 495 | 495 |  | 40 | 76 |
| 17 | Prophet | 15 | 329 | 327 | 2 | 40 | 76 |
| 18 | ElvenFighter |  | 60 | 40 | 2 | 1 | 84 |
| 19 | ElvenKnight | 18 | 91 | 91 |  | 20 | 36 |
| 20 | TempleKnight | 19 | 394 | 394 |  | 40 | 76 |
| 21 | Swordsinger | 19 | 235 | 235 |  | 40 | 76 |
| 22 | ElvenScout | 18 | 125 | 125 |  | 20 | 36 |
| 23 | PlainsWalker | 22 | 345 | 344 | 1 | 40 | 76 |
| 24 | SilverRanger | 22 | 328 | 328 |  | 40 | 76 |
| 25 | ElvenMystic |  | 59 | 34 | 2 | 1 | 84 |
| 26 | ElvenWizard | 25 | 115 | 115 |  | 20 | 35 |
| 27 | Spellsinger | 26 | 426 | 426 |  | 40 | 76 |
| 28 | ElementalSummoner | 26 | 362 | 362 |  | 40 | 76 |
| 29 | ElvenOracle | 25 | 125 | 125 |  | 20 | 35 |
| 30 | ElvenElder | 29 | 459 | 457 | 2 | 40 | 76 |
| 31 | DarkFighter |  | 60 | 40 | 2 | 1 | 84 |
| 32 | PalusKnight | 31 | 92 | 92 |  | 20 | 36 |
| 33 | ShillienKnight | 32 | 467 | 467 |  | 40 | 76 |
| 34 | Bladedancer | 32 | 278 | 278 |  | 40 | 76 |
| 35 | Assassin | 31 | 127 | 127 |  | 20 | 36 |
| 36 | AbyssWalker | 35 | 405 | 404 | 1 | 40 | 76 |
| 37 | PhantomRanger | 35 | 363 | 363 |  | 40 | 76 |
| 38 | DarkMystic |  | 62 | 37 | 2 | 1 | 84 |
| 39 | DarkWizard | 38 | 121 | 121 |  | 20 | 35 |
| 40 | Spellhowler | 39 | 419 | 419 |  | 40 | 76 |
| 41 | PhantomSummoner | 39 | 379 | 379 |  | 40 | 76 |
| 42 | ShillienOracle | 38 | 124 | 124 |  | 20 | 35 |
| 43 | ShillienElder | 42 | 356 | 354 | 2 | 40 | 76 |
| 44 | OrcFighter |  | 49 | 28 | 2 | 1 | 84 |
| 45 | OrcRaider | 44 | 111 | 111 |  | 20 | 36 |
| 46 | Destroyer | 45 | 365 | 365 |  | 40 | 76 |
| 47 | OrcMonk | 44 | 76 | 76 |  | 20 | 36 |
| 48 | Tyrant | 47 | 337 | 337 |  | 40 | 76 |
| 49 | OrcMystic |  | 53 | 28 | 2 | 1 | 84 |
| 50 | OrcShaman | 49 | 105 | 105 |  | 20 | 35 |
| 51 | Overlord | 50 | 490 | 487 | 3 | 40 | 76 |
| 52 | Warcryer | 50 | 369 | 365 | 4 | 40 | 76 |
| 53 | DwarvenFighter |  | 33 | 12 | 2 | 1 | 84 |
| 54 | Scavenger | 53 | 97 | 97 |  | 20 | 36 |
| 55 | BountyHunter | 54 | 454 | 454 |  | 40 | 76 |
| 56 | Artisan | 53 | 87 | 87 |  | 20 | 36 |
| 57 | Warsmith | 56 | 347 | 347 |  | 40 | 76 |
| 88 | Duelist | 2 | 23 | 15 | 8 | 76 | 83 |
| 89 | Dreadnought | 3 | 21 | 13 | 8 | 76 | 83 |
| 90 | PhoenixKnight | 5 | 34 | 23 | 11 | 76 | 85 |
| 91 | HellKnight | 6 | 34 | 23 | 11 | 76 | 85 |
| 92 | Sagittarius | 9 | 19 | 11 | 8 | 76 | 83 |
| 93 | Adventurer | 8 | 23 | 13 | 10 | 76 | 83 |
| 94 | Archmage | 12 | 20 | 13 | 7 | 76 | 83 |
| 95 | Soultaker | 13 | 21 | 14 | 7 | 76 | 83 |
| 96 | ArcanaLord | 14 | 31 | 23 | 8 | 76 | 85 |
| 97 | Cardinal | 16 | 23 | 16 | 7 | 76 | 83 |
| 98 | Hierophant | 17 | 20 | 13 | 7 | 76 | 83 |
| 99 | Eva'sTemplar | 20 | 33 | 23 | 10 | 76 | 85 |
| 100 | Swordmuse | 21 | 28 | 19 | 9 | 76 | 85 |
| 101 | WindRider | 23 | 23 | 13 | 10 | 76 | 83 |
| 102 | MoonlightSentinel | 24 | 20 | 12 | 8 | 76 | 83 |
| 103 | MysticMuse | 27 | 22 | 15 | 7 | 76 | 83 |
| 104 | ElementalMaster | 28 | 30 | 22 | 8 | 76 | 85 |
| 105 | Eva'sSaint | 30 | 22 | 15 | 7 | 76 | 83 |
| 106 | ShillienTemplar | 33 | 33 | 23 | 10 | 76 | 85 |
| 107 | SpectralDancer | 34 | 27 | 18 | 9 | 76 | 85 |
| 108 | GhostHunter | 36 | 23 | 13 | 10 | 76 | 83 |
| 109 | GhostSentinel | 37 | 20 | 12 | 8 | 76 | 83 |
| 110 | StormScreamer | 40 | 23 | 16 | 7 | 76 | 83 |
| 111 | SpectralMaster | 41 | 31 | 23 | 8 | 76 | 85 |
| 112 | ShillienSaint | 43 | 22 | 15 | 7 | 76 | 83 |
| 113 | Titan | 46 | 20 | 12 | 8 | 76 | 83 |
| 114 | GrandKhavatari | 48 | 22 | 13 | 9 | 76 | 83 |
| 115 | Dominator | 51 | 24 | 16 | 8 | 76 | 83 |
| 116 | Doomcryer | 52 | 19 | 12 | 7 | 76 | 83 |
| 117 | FortuneSeeker | 55 | 33 | 23 | 10 | 76 | 85 |
| 118 | Maestro | 57 | 20 | 11 | 9 | 76 | 83 |
| 123 | KamaelMaleSoldier |  | 54 | 32 | 3 | 1 | 84 |
| 124 | KamaelFemaleSoldier |  | 56 | 34 | 3 | 1 | 84 |
| 125 | Trooper | 123 | 108 | 108 |  | 20 | 36 |
| 126 | Warder | 124 | 115 | 115 |  | 20 | 36 |
| 127 | Berserker | 125 | 370 | 370 |  | 40 | 76 |
| 128 | MaleSoulBreaker | 125 | 393 | 393 |  | 40 | 76 |
| 129 | FemaleSoulBreaker | 126 | 393 | 393 |  | 40 | 76 |
| 130 | Arbalester | 126 | 371 | 371 |  | 40 | 76 |
| 131 | Doombringer | 127 | 17 | 9 | 8 | 76 | 83 |
| 132 | MaleSoulHound | 128 | 22 | 13 | 9 | 76 | 83 |
| 133 | FemaleSoulHound | 129 | 22 | 13 | 9 | 76 | 83 |
| 134 | Trickster | 130 | 17 | 10 | 7 | 76 | 83 |
| 135 | Inspector | 126 | 290 | 287 | 3 | 40 | 79 |
| 136 | Judicator | 135 | 9 | 6 | 3 | 76 | 82 |

### 5.7 Indice maestro de skills aprendibles por clase (GENERADO, VERIFIED)

Las **932 skills distintas** aprendibles por clase. Ninguna clase excluida.

Formato: `skillId|name|maxSkillLevel|classIdsQueLaAprenden|getLevels`

```text
1|Triple Slash|37|2|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
2|Confusion|19|32,33,34,35,36,37|24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
3|Power Strike|9|0,18,31,44|5,10,15
4|Dash|2|7,8|20,46
5|Double Sonic Slash|31|2|49,52,55,58,60,62,64,66,68,70,72,74
6|Sonic Blaster|37|2|43,46,49,52,55,58,60,62,64,66,68,70,72,74
7|Sonic Storm|28|2|49,52,55,58,60,62,64,66,68,70,72,74
8|Sonic Focus|8|2,88|40,43,49,55,60,66,70,79
9|Sonic Buster|34|2|43,46,49,52,55,58,60,62,64,66,68,70,72,74
10|Summon Storm Cubic|8|14,20|40,44,46,52,58,62,66,70,74
11|Trick|12|8,36|49,52,55,58,60,62,64,66,68,70,72,74
12|Switch|14|8,23|43,46,49,52,55,58,60,62,64,66,68,70,72,74
13|Summon Siege Golem|1|57|49
15|Charm|52|19,20,21,22,23,24|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
16|Mortal Blow|24|0,7,18,22,31,35|5,10,15,20,24,28,32,36
17|Force Burst|34|48|43,46,49,52,55,58,60,62,64,66,68,70,72,74
18|Aura of Hate|37|5,6,20,33|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
19|Double Shot|37|9,24,37|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
21|Poison Recovery|3|19,20,21,22,23,24|20,24,40,60
22|Summon Vampiric Cubic|7|33|43,49,55,60,64,68,72
24|Burst Shot|31|9,24|46,49,52,55,58,60,62,64,66,68,70,72,74
25|Summon Mechanic Golem|9|56,57|28,36,43,49,55,60,64,68,72
27|Unlock|14|7,8,22,23,35,36|20,24,28,32,36,40,43,46,52,55,60,64,68,72
28|Aggression|49|4,5,6,19,20,32,33|24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
29|Iron Punch|24|44,47|5,10,15,20,24,28,32,36
30|Backstab|37|8,23,36,55|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
33|Summon Phantom Cubic|8|33,41|40,44,46,52,58,62,66,70,74
34|Bandage|3|45,46,54,55,56,57|20,46,62
35|Force Storm|28|48|49,52,55,58,60,62,64,66,68,70,72,74
36|Whirlwind|37|3,46,55,57|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
42|Sweeper|1|53|10
44|Remedy|3|5|40,49,62
45|Divine Heal|9|4|28,32,36
46|Life Scavenge|15|6|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
48|Thunder Storm|37|3|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
49|Divine Strike|26|5|46,49,52,55,58,60,62,64,66,68,70,72,74
50|Focused Force|8|47,48,114|24,32,40,52,60,66,72,79
51|Lure|1|8,23,36|52
54|Force Blaster|49|47,48|24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
56|Power Shot|24|0,7,18,22,31,35|5,10,15,20,24,28,32,36
58|Elemental Heal|55|18,19,20,21,22,23,24|15,20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
60|Fake Death|1|8,23,55|40
61|Cure Bleeding|3|19,20,21,22,23,24|24,46,62
65|Horror|13|6|46,49,52,55,58,60,62,64,66,68,70,72,74
67|Summon Life Cubic|7|20,28|40,43,48,49,52,55,60,64,68,72
69|Sacrifice|25|5|52,55,58,60,62,64,66,68,70,72,74
70|Drain Health|53|4,6,31,32,33,34,35,36,37|15,20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
72|Iron Will|3|5,6|43,49,55
75|Detect Insect Weakness|1|1|32
76|Bear Spirit Totem|1|47|28
77|Attack Aura|2|18,19,22,31,32,35|10,28
78|War Cry|2|1,2|20,43
80|Detect Beast Weakness|1|2,3|52
81|Punch of Doom|3|48|55,64,72
82|Majesty|3|4,5,6|20,40,58
83|Wolf Spirit Totem|1|47|20
84|Poison Blade Dance|3|34|55,60,72
86|Reflect Damage|3|6|40,46,52
87|Detect Animal Weakness|1|2,3|40
88|Detect Dragon Weakness|1|2,3|58
91|Defense Aura|2|18,19,22,31,32,35|5,20
92|Shield Stun|52|4,5,6|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
94|Rage|2|45,46|24,55
95|Cripple|20|47,48|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
96|Bleed|6|7,8,22,23,35,36|24,32,49,58,66,70
97|Sanctuary|11|5|52,55,58,60,62,64,66,68,70,72,74
98|Sword Symphony|5|21|55,60,64,68,72
99|Rapid Shot|2|7,9,22,24,35,37|32,55
100|Stun Attack|15|1,45,50,54,56|20,24,25,28,30,32,35,36
101|Stun Shot|40|7,9,22,24,35,37|36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
102|Entangle|16|19,20,21,22,23,24|36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
103|Corpse Plague|4|6,33|46,58,62,70
104|Detect Plant Weakness|1|2,3|46
105|Freezing Strike|24|32,33,34,35,36,37|36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
106|Veil|14|8,36|43,46,49,52,55,58,60,62,64,66,68,70,72,74
107|Divine Aura|9|20|58,60,62,64,66,68,70,72,74
109|Ogre Spirit Totem|1|48|46
110|Ultimate Defense|2|4,5,6,19,20,32,33|20,46
111|Ultimate Evasion|2|7,8,22,23,35,36|28,55
112|Deflect Arrow|4|4,5,6,19,20,32,33|24,32,43,49
113|Long Shot|2|7,9,22,24,35,37|20,40
115|Power Break|17|32,33,34,35,36,37|32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
116|Howl|14|3|43,46,49,52,55,58,60,62,64,66,68,70,72,74
118|Magician's Movement|1|10,25,38,49|1
120|Stunning Fist|15|47|20,24,28,32,36
121|Battle Roar|6|1,3,45,46|28,40,49,58,64,70
122|Hex|15|33,34,36,37|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
123|Spirit Barrier|3|20,21,23,24|40,49,58
127|Hamstring|14|6|43,46,49,52,55,58,60,62,64,66,68,70,72,74
129|Poison|5|32,33,34,35,36,37|20,49,58,66,74
130|Thrill Fight|2|3|46,55
131|Hawk Eye|3|9|40,49,58
134|Toughness|1|44,49|1
137|Critical Chance|4|7,8,22,23|28,32,40,49,58
139|Guts|3|45,46|36,43,52
141|Weapon Mastery|3|0,18,31,44,53|5,10,15
142|Armor Mastery|5|0,18,31,44,53|5,10,15
143|Cubic Mastery|2|14,20,28,33,41|43,44,55,56
144|Dual Weapon Mastery|37|2,34|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
146|Anti Magic|45|10,11,12,13,14,15,16,17,25,26,27,28,29,30,38,39,40,41,42,43,49,50,51,52|7,14,20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
147|M. Def.|51|4,5,6,19,20,21,32,33,34|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
148|Vital Force|8|1,7,8,9,45,46,54,55,56,57|24,32,40,46,52,58,64,72
150|Weight Limit|3|53,54,55,56,57|10,24,46
153|Shield Mastery|4|4,5,6,19,20,32,33|20,28,40,52
163|Spellcraft|1|10,25,38,49|1
164|Quick Recycle|3|11,12,13,14,15,16,17,26,27,28,29,30,39,40,41,42,43,50,51,52|20,30,48
168|Boost Attack Speed|3|7,8,47,48|36,46,58
169|Quick Step|2|7,8,9,22,23,24,35,36,37|28,43
171|Esprit|8|7,8,9,22,23,24,35,36,37|36,43,46,49,52,62,68,74
172|Create Item|10|53,56,57,118|5,20,28,36,43,49,55,62,70,82
173|Acrobatics|2|7,8,22,23,35,36|20,55
176|Frenzy|3|45,46|32,46,55
181|Revival|1|3|55
190|Fatal Strike|37|2,46,55,57|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
191|Focus Mind|6|4,5,6,19,20,21,32,33,34|36,43,49,55,64,72
193|Critical Damage|7|7,8,35,36|24,32,40,52,64,72,74
194|Lucky|1|0,10,18,25,31,38,44,49,53,123,124|1
195|Boost Breath|2|7,8,22,23,35,36|20,55
196|Divine Blade|1|5,21|43
197|Divine Armor|2|5,20|40,46
198|Boost Evasion|3|7,8,22,23,35,36|24,46,58
205|Sword/Blunt Weapon Mastery|45|54,55,56,57|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
208|Bow Mastery|52|7,9,22,24,35,37|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
209|Dagger Mastery|45|7,8,22,23,35,36,54,55|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
210|Fist Weapon Mastery|45|47,48|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
211|Boost HP|10|1,3,17,45,46,51,52,54,55,56,57|20,28,36,40,43,44,48,49,52,55,56,62,66,70,74
212|Fast HP Recovery|8|1,2,3,11,12,13,14,15,16,17,26,27,28,29,30,39,40,41,42,43,45,46,50,51,52,54,55,56,57|24,32,35,40,43,44,46,52,58,64,68,74
213|Boost Mana|8|11,12,13,14,15,16,17,26,27,28,29,30,39,40,41,42,43,50,51,52|20,30,40,48,56,60,66,72
214|Mana Recovery|1|10,25,38,49|1
216|Polearm Mastery|45|1,3,45,46,54,55,56,57|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
217|Sword/Blunt Weapon Mastery|45|4,5,6,19,20,21,32,33|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
221|Silent Move|1|8,23,36|40
222|Fury Fists|1|48|43
223|Sting|49|32,33,34,35,36,37|24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
225|Acrobatic Move|3|7,8,9,22,23,24,35,36,37|28,43,55
226|Relax|1|0,44|5
227|Light Armor Mastery|50|1,2,3,45,46,54,55,56,57|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
228|Fast Spell Casting|3|11,12,13,14,15,16,17,26,27,28,29,30,39,40,41,42,43,50,51,52|25,40,56
229|Fast Mana Recovery|7|11,12,13,14,15,16,17,26,27,28,29,30,39,40,41,42,43,50,51,52|25,35,44,52,60,68,74
230|Sprint|2|19,20,21,22,23,24|32,52
231|Heavy Armor Mastery|50|1,2,3,45,46,54,55,56,57|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
232|Heavy Armor Mastery|52|4,5,6,19,20,32,33|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
233|Light Armor Mastery|47|7,8,9,22,23,24,35,36,37,47,48|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
234|Robe Mastery|41|11,12,13,14,26,27,28,39,40,41|20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
235|Robe Mastery|41|15,16,17,29,30,42,43|20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
236|Light Armor Mastery|41|15,16,17,29,30,42,43|20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
239|Expertise D|7|0,10,18,25,31,38,44,49,53,123,124|20,40,52,61,76,80,84
244|Armor Mastery|3|10,25,38|7,14
245|Wild Sweep|15|1,45,54,56|20,24,28,32,36
248|Crystallize|5|54,55,56,57|20,40,52,60,70
249|Weapon Mastery|42|10,11,12,13,14,15,16,17,25,26,27,28,29,30,38,39,40,41,42,43|7,14,20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
250|Weapon Mastery|42|49,50,51,52|7,14,20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
251|Robe Mastery|45|49,50,51,52|7,14,20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
252|Light Armor Mastery|45|49,50,51,52|7,14,20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
253|Heavy Armor Mastery|43|49,50,51,52|14,20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
254|Spoil|11|53,54,55|10,20,28,36,43,49,55,60,64,68,72
255|Power Smash|15|1,45|20,24,28,32,36
256|Accuracy|1|1,7,22,35,45|24
257|Sword/Blunt Weapon Mastery|45|1,2,45,46|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
258|Light Armor Mastery|33|14,28,41|40,44,48,52,56,58,60,62,64,66,68,70,72,74
259|Heavy Armor Mastery|33|17|40,44,48,52,56,58,60,62,64,66,68,70,72,74
260|Hammer Crush|37|2,46,51,52,55,57|40,43,44,46,48,49,52,55,56,58,60,62,64,66,68,70,72,74
261|Triple Sonic Slash|22|2|55,58,60,62,64,66,68,70,72,74
262|Divine Blessing|37|5|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
263|Deadly Blow|37|8,23,36,55|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
264|Song of Earth|1|21|55
265|Song of Life|1|21|52
266|Song of Water|1|21|58
267|Song of Warding|1|21|43
268|Song of Wind|1|21|46
269|Song of Hunter|1|21|40
270|Song of Invocation|1|21|49
271|Dance of the Warrior|1|34|55
272|Dance of Inspiration|1|34|46
273|Dance of the Mystic|1|34|49
274|Dance of Fire|1|34|40
275|Dance of Fury|1|34|58
276|Dance of Concentration|1|34|52
277|Dance of Light|1|34|43
278|Summon Viper Cubic|6|33|49,55,60,64,68,72
279|Lightning Strike|5|33|58,62,66,70,74
280|Burning Fist|37|48|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
281|Soul Breaker|37|48|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
282|Puma Spirit Totem|1|48|40
283|Summon Dark Panther|7|6|40,49,58,62,66,70,74
284|Hurricane Assault|40|47,48|36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
285|Higher Mana Gain|27|11,12,13,26,27,39,40|20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
286|Provoke|3|3|43,55,60
287|Lionheart|3|1,2,3,45,46|36,49,62
288|Guard Stance|4|20,33|43,52,62,70
289|Life Leech|15|33|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
290|Final Frenzy|14|2,3|43,46,49,52,55,58,60,62,64,66,68,70,72,74
291|Final Fortress|11|5,6,20,33|52,55,58,60,62,64,66,68,70,72,74
292|Bison Spirit Totem|1|48|68
293|Two-handed Weapon Mastery|20|45,46|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
294|Shadow Sense|1|31|15
295|Iron Body|1|44|15
296|Chameleon Rest|1|23|46
297|Duelist Spirit|2|2|64,72
298|Rabbit Spirit Totem|1|48|62
299|Summon Wild Hog Cannon|1|57|58
301|Summon Big Boom|5|57|58,62,66,70,74
302|Spoil Festival|9|54,55|28,36,43,49,55,62,66,70,74
303|Soul of Sagittarius|4|9,24,37|40,58,64,70
304|Song of Vitality|1|21|66
305|Song of Vengeance|1|21|74
306|Song of Flame Guard|1|21|62
307|Dance of Aqua Guard|1|34|70
308|Song of Storm Guard|1|21|70
309|Dance of Earth Guard|1|34|62
310|Dance of the Vampire|1|34|74
311|Dance of Protection|1|34|66
312|Vicious Stance|20|1,2,3,7,8,9,22,23,24,35,36,37,45,46|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
313|Snipe|8|9|60,62,64,66,68,70,72,74
314|Fatal Counter|16|37|60,62,64,66,68,70,72,74
315|Crush of Doom|16|46|60,62,64,66,68,70,72,74
316|Aegis|1|20,33|60
317|Focus Attack|5|3|40,49,58,66,74
318|Aegis Stance|1|5,6|46
319|Agile Movement|2|47,48|20,40
320|Wrath|10|3,46,55,57|66,68,70,72,74
321|Blinding Blow|10|23,36|66,68,70,72,74
322|Shield Fortress|6|5,6,20,33|64,66,68,70,72,74
323|Quiver of Arrow: A Grade|1|9,24,37|60
324|Quiver of Arrow: S Grade|1|9,24,37|72
328|Wisdom|1|88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,131,132,133,134,136|76
329|Health|1|88,89,90,91,95,97,98,99,100,105,106,107,110,111,112,113,114,115,116,117,118,131,132,133,134,136|76
330|Skill Mastery|1|88,89,92,93,101,102,108,109,113,114,117,118,131,132,133,134,136|77
331|Skill Mastery|1|94,95,96,97,98,103,104,105,110,111,112,115,116|77
334|Focus Skill Mastery|1|92,93,101,102,108,109,134|78
335|Fortitude|1|90,91,99,106,113,114,131|76
336|Arcane Wisdom|1|97,98,105,112,116|78
337|Arcane Power|1|94,95,103,110,115|78
338|Arcane Agility|1|96,104,111|78
339|Parry Stance|1|89,113,117,118|78
340|Riposte Stance|1|88,114,117,118|77
341|Touch of Life|1|90,99|78
342|Touch of Death|1|91,106|78
343|Lethal Shot|1|92,102,109|76
344|Lethal Blow|1|93,101,108|76
345|Sonic Rage|1|88|78
346|Raging Force|1|114|78
347|Earthquake|1|89,113,117,118|78
348|Spoil Crush|1|117|76
349|Song of Renewal|1|100|77
350|Physical Mirror|1|90,91|78
351|Magical Mirror|1|99,106|78
352|Shield Bash|1|99,106|77
353|Shield Slam|1|90,91|77
354|Hamstring Shot|1|92,102,109|77
355|Focus Death|1|101,108|78
356|Focus Chance|1|93,101|78
357|Focus Power|1|93,108|78
358|Bluff|1|93,101,108|77
359|Eye of Hunter|1|88,89|77
360|Eye of Slayer|1|88,89|78
361|Shock Blast|1|89|77
362|Armor Crush|1|113,117,118|77
363|Song of Meditation|1|100|77
364|Song of Champion|1|100|78
365|Dance of Siren|1|107|78
366|Dance of Shadows|1|107|77
367|Dance of Medusa|1|107|77
368|Vengeance|1|90,91,99,106|77
369|Evade Shot|1|102,109|78
400|Tribunal|10|5,20|55,58,60,62,64,66,68,70,72,74
401|Judgment|10|6,33|55,58,60,62,64,66,68,70,72,74
402|Arrest|10|20,21,33,34|55,58,60,62,64,66,68,70,72,74
403|Shackle|10|5,6|55,58,60,62,64,66,68,70,72,74
404|Mass Shackling|5|5|58,62,66,70,74
405|Banish Undead|10|5|55,58,60,62,64,66,68,70,72,74
406|Angelic Icon|3|5|58,66,74
407|Psycho Symphony|10|21|55,58,60,62,64,66,68,70,72,74
408|Demonic Blade Dance|10|34|55,58,60,62,64,66,68,70,72,74
409|Critical Blow|10|8|55,58,60,62,64,66,68,70,72,74
410|Mortal Strike|3|23,36|58,66,74
411|Stealth|3|8|58,66,74
412|Sand Bomb|10|8,23,36|55,58,60,62,64,66,68,70,72,74
413|Rapid Fire|8|24|60,62,64,66,68,70,72,74
414|Dead Eye|8|37|60,62,64,66,68,70,72,74
415|Spirit of Sagittarius|3|9,24,37|58,66,74
416|Blessing of Sagittarius|3|9,24|58,66,74
417|Pain of Sagittarius|5|9,37|58,62,66,70,74
418|Quiver of Holding|3|9|58,66,74
419|Summon Treasure Key|4|8,23,36|46,58,66,74
420|Zealot|3|46,48|58,66,74
421|Fell Swoop|5|3|58,62,66,70,74
422|Polearm Accuracy|3|3,46,55,57|58,66,74
423|Dark Form|3|46,48|58,66,74
424|War Frenzy|3|2,3,46,48,55,57|58,66,74
425|Hawk Spirit Totem|1|48|74
428|Inner Rhythm|1|100,107|78
429|Knighthood|1|90,91,99,106|78
430|Master of Combat|1|88,89,113,114,117,118|78
431|Archery|1|92,102,109|78
432|Assassination|1|93,101,108|78
433|Arcane Roar|1|94,103,110|78
434|Necromancy|1|95|78
435|Summon Lore|1|96,104,111|78
436|Divine Lore|1|97,98,105,112,115,116|78
437|Song of Silence|1|100|79
438|Soul of the Phoenix|1|90|79
439|Shield of Revenge|1|91|79
440|Braveheart|1|88,89,113,117,118|78
441|Force Meditation|1|114|78
442|Sonic Barrier|1|88|79
443|Force Barrier|1|114|79
444|Sweeper Festival|1|54|28
445|Mirage|1|93|79
446|Dodge|1|101|79
447|Counterattack|1|108|79
448|Summon Swoop Cannon|1|57|68
449|Summon Attractive Cubic|4|20|62,66,70,74
450|Banish Seraph|10|6,33|55,58,60,62,64,66,68,70,72,74
451|Sonic Move|2|2|62,68
452|Shock Stomp|5|3|55,60,64,68,72
453|Escape Shackle|1|8,23,36|60
454|Symbol of Defense|1|90,91,99,106|80
455|Symbol of Noise|1|100,107|80
456|Symbol of Resistance|1|113,117|80
457|Symbol of Honor|1|89,118|80
458|Symbol of Energy|1|88,114|80
459|Symbol of the Sniper|1|92,102,109|80
460|Symbol of the Assassin|1|93,101,108|80
461|Break Duress|2|48|60,66
462|Guilted Body|1|123,124|1
463|Weapon Mastery|3|123,124|5,10,15
464|Armor Mastery|5|123,124|5,10,15
465|Light Armor Mastery|50|125,126,127,128,129,130,135|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
466|Magic Immunity|55|123,124,125,126,127,128,129,130,135|5,10,15,20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
467|Soul Mastery|23|123,124,125,126,127,128,129,130,135|5,10,15,20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
468|Fallen Attack|9|123,124|5,10,15
469|Rapid Attack|1|124|10
470|Detect Trap|7|124,126,130|15,24,36,46,55,66,74
471|Defuse Trap|7|124,126,130|15,24,36,46,55,66,74
472|Ancient Sword Mastery|45|125,127|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
473|Crossbow Mastery|45|126,130|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
474|Rapier Mastery|45|125,126,128,129,135|20,24,28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
475|Strike Back|1|125|20
476|Dark Strike|15|125|20,24,28,32,36
477|Dark Smash|37|127|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
478|Double Thrust|15|125,126|20,24,28,32,36
479|Hard March|1|125,126|28
480|Dark Blade|1|125,126|36
481|Dark Armor|2|125,126,127,128,129,130,135|32,43
482|Furious Soul|2|125,126,127|24,55
483|Sword Shield|2|125,127|36,52
484|Rush|1|125|32
485|Disarm|7|125,127|36,43,49,55,62,68,74
486|Increase Range|2|126,130|24,40
487|Penetrating Shot|15|126|20,24,28,32,36
489|Shift Target|1|130|40
490|Fast Shot|2|126,130|32,55
492|Spread Wing|25|127,128,129,135|52,55,58,60,62,64,66,68,70,72,74
493|Storm Assault|28|127|49,52,55,58,60,62,64,66,68,70,72,74
494|Shoulder Charge|37|127|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
495|Blade Rush|10|127|55,58,60,62,64,66,68,70,72,74
496|Slashing Blade|31|127|46,49,52,55,58,60,62,64,66,68,70,72,74
497|Crush of Pain|16|127|60,62,64,66,68,70,72,74
498|Contagion|12|127|64,66,68,70,72,74
499|Courage|3|125,127|36,49,62
500|True Berserker|2|127|40,52
501|Violent Temper|12|127|49,52,55,58,60,62,64,66,68,70,72,74
502|Life to Soul|5|127,128,129,130,135|40,49,58,66,72
503|Scorn|3|127|43,55,64
504|Triple Thrust|37|128,129,135|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
505|Shining Edge|28|128,129,135|49,52,55,58,60,62,64,66,68,70,72,74
506|Checkmate|4|128,129|52,60,66,72
507|Twin Shot|37|130|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
508|Rising Shot|31|130|46,49,52,55,58,60,62,64,66,68,70,72,74
509|Bleeding Shot|34|130|43,46,49,52,55,58,60,62,64,66,68,70,72,74
510|Deadly Roulette|5|130|66,68,70,72,74
511|Temptation|1|130|49
513|Create Dark Seed|1|130|62
514|Fire Trap|9|126,130|28,36,43,49,55,60,64,68,72
515|Poison Trap|6|130|49,55,60,64,68,72
516|Slow Trap|6|130|52,58,62,66,70,74
517|Flash Trap|5|130|55,60,64,68,72
518|Binding Trap|8|130|40,46,52,58,62,66,70,74
519|Quiver of Bolts: A Grade|1|130|60
520|Quiver of Bolts: S Grade|1|130|74
521|Sharpshooting|8|130|60,62,64,66,68,70,72,74
522|Real Target|4|130|40,52,62,70
523|Imbue Dark Seed|7|130|62,64,66,68,70,72,74
524|Cure Dark Seed|1|130|64
525|Decoy|6|130|43,52,60,66,70,74
526|Enuma Elish|1|131|78
527|Iron Shield|1|90,91,99,106|79
528|Shield of Faith|1|90,91,99,106|79
529|Song of Elemental|1|100|79
530|Dance of Alignment|1|107|79
531|Critical Wound|1|93,101,108|79
532|Counter Chance|1|109|79
533|Counter Rapid Shot|1|102|79
534|Counter Dash|1|92,102|79
535|Counter Mind|1|92,109|79
536|Over the Body|1|113|79
537|Spoil Bomb|1|117|79
538|Final Form|1|131,132,133,134,136|79
620|Quiver of Bolts: B Grade|1|130|52
621|Create Special Bolt|1|130|43
622|Ultimate Escape|2|126,130|36,52
623|Find Trap|1|8,23,36|74
624|Remove Trap|1|8,23,36|74
625|Soul Gathering|1|127,128,129,130,135|46
626|Critical Sense|4|127,128,129,130,135|40,43,52,60,66
627|Soul Shock|40|126,130|36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
628|Warp|1|126|32
755|Protection of Rune|1|88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,131,132,133,134,136|82
756|Protection of Elemental|1|88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,131,132,133,134,136|82
757|Protection of Alignment|1|88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,131,132,133,134,136|82
758|Fighters Will|1|88,89,90,91,92,93,99,100,101,102,106,107,108,109,113,114,117,118,131,132,133,134|81
759|Archers Will|1|88,89,90,91,92,93,99,100,101,102,106,107,108,109,113,114,117,118,131,132,133,134|81
760|Anti-magic Armor|1|90,91,99,106|81
761|Seed of Revenge|1|91|83
762|Insane Crusher|1|91|83
763|Hell Scream|1|91|83
764|Song of Wind Storm|1|100|76
765|Dance of Blade Storm|1|107|76
766|Sixth Sense|1|90,91,93,99,100,101,106,107,108,132,133,134|81
767|Expose Weak Point|1|88,89,93,101,108,113,114,117,118,131,132,133|81
768|Exciting Adventure|1|93|83
769|Wind Riding|1|101|83
770|Ghost Walking|1|108|83
771|Flame Hawk|1|92|83
772|Arrow Rain|1|102|83
773|Ghost Piercing|1|109|83
774|Dread Pool|1|89|83
775|Weapon Blockade|1|88|80
776|Force Of Destruction|1|114|83
777|Demolition Impact|1|113|83
778|Golem Armor|1|118|83
779|Summon Smart Cubic|1|99|80
780|Summon Smart Cubic|1|106|80
781|Summon Smart Cubic|1|96|80
782|Summon Smart Cubic|1|104|80
783|Summon Smart Cubic|1|111|80
784|Spirit of Phoenix|1|90|83
785|Flame Icon|1|90|83
786|Evas Will|1|99|83
787|Touch of Eva|1|99|83
788|Pain of Shilen|1|106|83
789|Spirit of Shilen|1|106|83
790|Wild Shot|1|134|83
791|Lightning Shock|1|132,133|83
792|Betrayal Mark|1|134|78
793|Rush Impact|1|131|78
794|Mass Disarm|1|131|79
810|Vanguard|1|5|40
811|Vanguard|1|6|40
812|Vanguard|1|20|40
813|Vanguard|1|33|40
818|Evasion Counter|1|36|74
819|Evasion Chance|1|23|74
820|Evasion Haste|1|8|74
821|Shadow Step|1|8,23,36|40
822|Repair Golem|3|57|40,49,55
823|Strengthen Golem|3|57|43,52,58
824|Golem Reinforcement|3|57|40,49,55
825|Sharp Edge|1|57|49
826|Spike|1|57|49
827|Restring|1|57|49
828|Case Harden|1|57|46
829|Hard Tanning|1|57|46
830|Embroider|1|57|46
831|Summon Merchant Golem|1|57|52
832|Fast Recovery|2|127,128,129,130,135|43,49
833|Body Reconstruction|1|127|52
834|Blood Pact|1|127|55
835|Imbue Seed of Destruction|4|130|68,70,72,74
836|Oblivion Trap|3|130|64,68,72
837|Painkiller|1|128,129,135|58
840|Final Flying Form|1|123,124,135|79
841|Aura Bird - Falcon|1|0,10,18,25,31,38,44,49,53,123,124,135|75
842|Aura Bird - Owl|1|0,10,18,25,31,38,44,49,53,123,124,135|75
912|Summon Imperial Phoenix|1|90|83
913|Deflect Magic|1|90,91,99,100,106,107|81
914|Song of Purification|1|100|83
915|Dance of Berserker|1|107|83
916|Shield Deflect Magic|4|5,6,20,33|60,64,68,72
917|Final Secret|1|88,89,113,114,117,118,131|81
918|Maximum Focus Force|1|114|83
919|Maximum Sonic Focus|1|88|83
920|Power Crush|37|3|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
921|Cursed Pierce|1|89|76
922|Hide|1|93,101,108|81
923|Dual Dagger Mastery|1|93,101,108,117|81
924|Seven Arrow|1|92,102,109|81
925|Sigil Mastery|1|94,95,103,110|76
926|Sigil Mastery|1|97,105,112,115|76
927|Burning Chop|14|51,52|40,44,48,52,56,58,60,62,64,66,68,70,72,74
928|Dual Blow|1|93,101,108|83
929|Spirit of the Cat|1|96|83
930|Spirit of the Demon|1|111|83
931|Spirit of the Unicorn|1|104|83
933|Detection|1|9,24,37|74
934|Sigil Mastery|1|96,104,111|76
935|Sigil Mastery|1|98,116|76
939|Soul Rage|1|131,132,133,134,136|78
945|Magician Will|1|94,95,96,97,98,103,104,105,110,111,112,115,116|81
946|Silent Mind|1|92,102,109|81
947|Lucky Strike|1|117|83
948|Eye for Eye|1|131|83
949|Onslaught of Paagrio|1|115|83
952|Collector's Experience|5|55|58,62,66,70,74
964|Children of Shilen|1|38|14
982|Combat Aura|3|5,6,20,33|52,64,70
983|Patience|1|5,6,20,33|60
984|Shield Strike|25|5,6,20,33,90,91,99,106|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74,76,77,78,79,80,81,82,83,84,85
985|Challenge for Fate|1|90,91,99,106|83
986|Deadly Strike|25|21,34,100,107|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74,76,77,78,79,80,81,82,83,84,85
987|Multiple Shot|1|92,102,109,134|82
988|Battle Whisper|3|21|46,52,60
989|Defense Motion|1|34|60
990|Death Shot|1|92,102,109,134|83
991|Throwing Dagger|1|93,101,108|80
992|Sonic Mastery|8|2,88|40,43,49,55,60,66,70,79
993|Force Mastery|8|47,48,114|24,32,40,52,60,66,72,79
994|Rush|1|2,3,46,48,57|40
995|Rush Impact|1|88,89,113,114,118|83
997|Crushing Strike|25|55,117|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74,76,77,78,79,80,81,82,83,84,85
998|Blazing Boost|1|55|40
1001|Soul Cry|10|49,50,51,52|1,14,25,35,40,48,56,60,66,72
1002|Flame Chant|3|50,52|30,44,56
1003|Pa'agrian Gift|3|50,51|30,40,48
1004|The Wisdom of Pa'agrio|3|51|40,48,56
1005|Blessings of Pa'agrio|3|50,51|35,44,52
1006|Chant of Fire|3|50,52|20,40,52
1007|Chant of Battle|3|49,50,52|14,25,44
1008|The Glory of Pa'agrio|3|51|40,48,56
1009|Chant of Shielding|3|50,52|20,30,48
1010|Soul Shield|3|49,50|7,25,35
1011|Heal|18|10,15,25,29,38,42|7,14,20,25,30,35
1012|Cure Poison|3|10,15,16,25,29,30,38,42,43|7,35,58
1013|Recharge|32|29,30,42,43|30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1015|Battle Heal|15|10,15,25,29,38,42|14,20,25,30,35
1016|Resurrection|9|15,16,29,30,42|20,30,40,48,56,60,64,70,74
1018|Purify|3|16,43|44,52,62
1020|Vitalize|27|16,30|48,52,56,58,60,62,64,66,68,70,72,74
1027|Group Heal|15|10,15,25,29,38,42|14,20,25,30,35
1028|Might of Heaven|19|16,30|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1031|Disrupt Undead|8|15,29,42|20,25,30,35
1032|Invigor|3|17|40,48,56
1033|Resist Poison|3|17,29,30|35,40,44,60,64,68
1034|Repose|13|16|44,48,52,56,58,60,62,64,66,68,70,72,74
1035|Mental Shield|4|15,17,29,30,42,43|25,40,48,56
1036|Magic Barrier|2|17|44,52
1040|Shield|3|10,15,17,25,29,30,38,42,43|7,25,44
1042|Hold Undead|12|16|48,52,56,58,60,62,64,66,68,70,72,74
1043|Holy Weapon|1|15,29|25
1044|Regeneration|3|15,17,29,30|35,48,56
1045|Bless the Body|6|17|44,48,52,56,64,70
1047|Mana Regeneration|4|27|40,48,60,70
1048|Bless the Soul|6|17|44,48,52,56,62,70
1049|Requiem|14|16|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1050|Return|2|17,30|40,44,56
1056|Cancellation|12|12,27|48,52,56,58,60,62,64,66,68,70,72,74
1059|Empower|3|42,43|25,44,52
1062|Berserker Spirit|2|15,17|35,52
1064|Silence|14|13,40|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1068|Might|3|10,15,17,25,29,30,38,42,43|7,20,40
1069|Sleep|42|11,12,13,15,16,26,27,29,30,39,40,42|25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1071|Surrender To Water|14|27|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1072|Sleeping Cloud|5|12,27|44,56,62,66,70
1073|Kiss of Eva|2|15,17,29,30,42,43|20,52
1074|Surrender To Wind|14|12,40|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1075|Peace|15|15,16|35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1077|Focus|3|15,17,42,43|25,44,52
1078|Concentration|6|11,12,15,17,26,29,30,39,42,43|20,30,44,52,60,68
1083|Surrender To Fire|17|11,12|25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1085|Acumen|3|15,17|20,35,48
1086|Haste|2|17|44,52
1087|Agility|3|29,30|25,44,52
1090|Life Drain|6|49,50|7,14,20,25,30,35
1092|Fear|19|49,50,51,52|14,20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1095|Venom|5|49,50,52|7,14,20,40,52
1096|Seal of Chaos|16|50,51|30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1097|Dreaming Spirit|20|49,50,51,52|7,14,20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1099|Seal of Slow|15|50,51|35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1100|Chill Flame|2|49|7,14
1101|Blaze Quake|2|50|25,35
1102|Aura Sink|6|50,52|25,35,44,52,64,70
1104|Seal of Winter|14|51|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1105|Madness|18|50,51,52|20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1107|Frost Flame|2|50|20,30
1108|Seal of Flame|4|51|48,56,68,74
1111|Summon Kat the Cat|18|11,14|20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1126|Servitor Recharge|34|11,14,26,28,39,41|25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1127|Servitor Heal|45|11,14,26,28,39,41|20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1128|Summon Shadow|18|39,41|20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1129|Summon Reanimated Man|7|13|44,52,60,64,68,72,74
1139|Servitor Magic Shield|2|14,28,41|44,52
1140|Servitor Physical Shield|3|14,28,41|40,48,56
1141|Servitor Haste|2|14,28,41|44,52
1144|Servitor Wind Walk|2|11,14|35,48
1145|Bright Servitor|3|26,28|35,48,56
1146|Mighty Servitor|3|39,41|35,48,56
1147|Vampiric Touch|6|10,11,38,39|14,20,25
1148|Death Spike|13|13,40|44,48,52,56,58,60,62,64,66,68,70,72,74
1151|Corpse Life Drain|16|11,13,39,40|30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1154|Summon Corrupted Man|6|13|40,48,56,62,66,70
1155|Corpse Burst|15|13|48,52,56,58,60,62,64,66,68,70,72,74
1156|Forget|13|13|44,48,52,56,58,60,62,64,66,68,70,72,74
1157|Body To Mind|5|11,13,39,40|25,40,52,58,66
1159|Curse Death Link|22|13,40|52,56,58,60,62,64,66,68,70,72,74
1160|Slow|15|11,12,39,40|35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1163|Curse Discord|14|13|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1164|Curse Weakness|19|10,11,13,25,26,27|14,20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1167|Poisonous Cloud|6|11,13,39,40|25,35,48,56,64,74
1168|Curse Poison|7|10,11,13,38,39,40,41|7,20,30,44,52,62,72
1169|Curse Fear|14|12,13,27,40|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1170|Anchor|13|13|40,48,52,56,58,60,62,64,66,68,70,72,74
1171|Blazing Circle|19|12|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1172|Aura Burn|8|11,26,39|20,25,30,35
1174|Frost Wall|22|27|52,56,58,60,62,64,66,68,70,72,74
1175|Aqua Swirl|8|26|20,25,30,35
1176|Tempest|15|40|48,52,56,58,60,62,64,66,68,70,72,74
1177|Wind Strike|5|10,25,38|1,7,14
1178|Twister|8|39|20,25,30,35
1181|Flame Strike|3|11,26,39|20,25,30
1182|Resist Aqua|3|17,26,27|25,40,44,58,62,66
1183|Freezing Shackle|4|27|44,52,64,74
1184|Ice Bolt|6|10,11,25,26,38,39|7,14,20
1189|Resist Wind|3|17,42,43|35,40,44,58,62,66
1191|Resist Fire|3|15,17|30,40,44
1201|Dryad Root|33|15,17,29,42,43|25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1204|Wind Walk|2|15,29,42|20,30
1206|Wind Shackle|19|25,26,28,29,30,38,39,41,42,43|14,20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1208|Seal of Binding|17|50,51|25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1209|Seal of Poison|6|50,51|20,30,40,52,62,70
1210|Seal of Gloom|4|51|44,52,64,72
1213|Seal of Mirage|13|51|44,48,52,56,58,60,62,64,66,68,70,72,74
1216|Self Heal|1|10,25,38|1
1217|Greater Heal|33|16,30,43|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1218|Greater Battle Heal|33|16|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1219|Greater Group Heal|33|16,30,43|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1220|Blaze|8|11|20,25,30,35
1222|Curse Chaos|15|11,13,39,40|35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1223|Surrender To Earth|15|26,27|35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1224|Surrender To Poison|17|39,40|25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1225|Summon Mew the Cat|18|11,14|20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1226|Summon Boxer the Unicorn|18|26,28|20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1227|Summon Mirage the Unicorn|18|26,28|20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1228|Summon Silhouette|18|39,41|20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1229|Chant of Life|18|50,52|20,25,30,35,40,44,48,52,56,58,60,62,64,66,68,70,72,74
1230|Prominence|28|12|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1231|Aura Flare|28|12,27|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1232|Blazing Skin|3|12|40,48,56
1233|Decay|4|12|48,56,64,74
1234|Vampiric Claw|28|13,40|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1235|Hydro Blast|28|27|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1236|Frost Bolt|19|27|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1237|Ice Dagger|17|27|44,48,52,56,58,60,62,64,66,68,70,72,74
1238|Freezing Skin|3|27|40,48,56
1239|Hurricane|28|40|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1240|Guidance|3|17,43|40,48,56
1242|Death Whisper|3|17,43|40,48,56
1243|Bless Shield|6|17,30|40,48,56,62,66,70
1244|Freezing Flame|4|52|40,52,64,72
1245|Steal Essence|14|51,52|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1246|Seal of Silence|12|51|48,52,56,58,60,62,64,66,68,70,72,74
1247|Seal of Scourge|14|51|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1248|Seal of Suspension|12|51|48,52,56,58,60,62,64,66,68,70,72,74
1249|The Vision of Pa'agrio|3|51|44,52,56
1250|Under the Protection of Pa'agrio|3|51|40,48,56
1251|Chant of Fury|2|52|48,56
1252|Chant of Evasion|3|52|40,48,56
1253|Chant of Rage|3|52|44,52,56
1254|Mass Resurrection|6|16|40,44,52,56,58,68
1255|Party Recall|2|30|48,56
1256|The Heart of Pa'agrio|13|51|44,48,52,56,58,60,62,64,66,68,70,72,74
1257|Decrease Weight|3|29,30|35,44,52
1258|Restore Life|4|16|44,48,52,56
1259|Resist Shock|4|30|40,52,64,72
1260|The Tact of Pa'agrio|3|51|40,48,56
1261|Rage of Pa'agrio|2|51|44,52
1262|Transfer Pain|5|13,14,28,41|40,48,56,58,70
1263|Curse Gloom|13|13|44,48,52,56,58,60,62,64,66,68,70,72,74
1264|Solar Spark|3|26|25,30,35
1265|Solar Flare|14|27|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1266|Shadow Spark|3|39|25,30,35
1267|Shadow Flare|14|40|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1268|Vampiric Rage|4|42,43|30,44,58,72
1269|Curse Disease|9|13|58,60,62,64,66,68,70,72,74
1271|Benediction|1|16|66
1272|Word of Fear|13|17|44,48,52,56,58,60,62,64,66,68,70,72,74
1273|Serenade of Eva|13|30|44,48,52,56,58,60,62,64,66,68,70,72,74
1274|Energy Bolt|4|11,26|20,25,30,35
1275|Aura Bolt|14|12,27|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1276|Summon Kai the Cat|14|14|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1277|Summon Merrow the Unicorn|14|28|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1278|Summon Soulless|14|41|40,44,48,52,56,58,60,62,64,66,68,70,72,74
1279|Summon Binding Cubic|9|14|40,44,48,52,56,60,64,68,72
1280|Summon Aqua Cubic|9|28|40,44,48,52,56,62,66,70,74
1281|Summon Spark Cubic|9|41|40,44,48,52,56,60,64,68,72
1282|Pa'agrian Haste|2|51|58,64
1283|Soul Guard|13|51|44,48,52,56,58,60,62,64,66,68,70,72,74
1284|Chant of Revenge|3|52|62,68,74
1285|Seed of Fire|1|12|66
1286|Seed of Water|1|27|66
1287|Seed of Wind|1|40|66
1288|Aura Symphony|1|12,27,40|68
1289|Inferno|1|12|70
1290|Blizzard|1|27|70
1291|Demon Wind|1|40|70
1292|Elemental Assault|1|12|72
1293|Elemental Symphony|1|27|72
1294|Elemental Storm|1|40|72
1295|Aqua Splash|9|27|58,60,62,64,66,68,70,72,74
1296|Rain of Fire|9|12|58,60,62,64,66,68,70,72,74
1297|Clear Mind|6|12,27,40|40,48,56,62,68,74
1298|Mass Slow|14|13|62,64,66,68,70,72,74
1299|Servitor Empowerment|2|14,28,41|52,70
1300|Servitor Cure|3|14,28,41|40,48,60
1301|Servitor Blessing|1|14,28,41|62
1303|Wild Magic|2|30,43|62,70
1304|Advanced Block|3|30|58,66,72
1305|The Honor of Pa'agrio|5|51|66,68,70,72,74
1306|Ritual of Life|6|51|64,66,68,70,72,74
1307|Prayer|3|16|66,70,74
1308|Chant of Predator|3|52|40,60,68
1309|Chant of Eagle|3|52|48,58,64
1310|Chant of Vampire|4|52|44,58,66,74
1311|Body of Avatar|6|16|48,56,60,64,68,72
1320|Create Common Item|9|0,10,18,25,31,38,44,49,53,123,124|5,20,28,36,43,49,55,62,70
1321|Dwarven Craft|1|53|1
1322|Common Craft|1|0,10,18,25,31,38,44,49,53,123,124|1
1328|Mass Summon Storm Cubic|8|14|40,44,52,58,62,66,70,74
1329|Mass Summon Aqua Cubic|9|28|40,44,48,52,56,62,66,70,74
1330|Mass Summon Phantom Cubic|8|41|40,44,52,58,62,66,70,74
1331|Summon Feline Queen|10|14|56,58,60,62,64,66,68,70,72,74
1332|Summon Seraphim the Unicorn|10|28|56,58,60,62,64,66,68,70,72,74
1333|Summon Nightshade|10|41|56,58,60,62,64,66,68,70,72,74
1334|Summon Cursed Man|7|13|56,60,64,68,70,72,74
1335|Balance Life|1|97|76
1336|Curse of Doom|1|95|77
1337|Curse of Abyss|1|95|78
1338|Arcane Chaos|1|94,103,110|78
1339|Fire Vortex|1|94|77
1340|Ice Vortex|1|103|77
1341|Wind Vortex|1|110|77
1342|Light Vortex|1|103|76
1343|Dark Vortex|1|95,110|76
1344|Mass Warrior Bane|1|95|77
1345|Mass Mage Bane|1|95|78
1346|Warrior Servitor|1|96|77
1347|Wizard Servitor|1|104|77
1348|Assassin Servitor|1|111|77
1349|Final Servitor|1|96,104,111|78
1350|Warrior Bane|1|96,104|76
1351|Mage Bane|1|96,111|77
1352|Elemental Protection|1|98|76
1353|Divine Protection|1|97,105|77
1354|Arcane Protection|1|105,112|76
1355|Prophecy of Water|1|105|78
1356|Prophecy of Fire|1|98|78
1357|Prophecy of Wind|1|112|78
1358|Block Shield|1|98,112|77
1359|Block Wind Walk|1|98,105|77
1360|Mass Block Shield|1|97|77
1361|Mass Block Wind Walk|1|97|78
1362|Chant of Spirit|1|116|77
1363|Chant of Victory|1|116|78
1364|Eye of Pa'agrio|1|115|77
1365|Soul of Pa'agrio|1|115|77
1366|Seal of Despair|1|115|78
1367|Seal of Disease|1|115|76
1380|Betray|10|14,28,41|56,58,60,62,64,66,68,70,72,74
1381|Mass Fear|5|13|58,62,66,70,74
1382|Mass Gloom|5|13|58,62,66,70,74
1383|Mass Surrender to Fire|5|14|58,62,66,70,74
1384|Mass Surrender to Water|5|28|58,62,66,70,74
1385|Mass Surrender to Wind|5|41|58,62,66,70,74
1386|Arcane Disruption|10|14|56,58,60,62,64,66,68,70,72,74
1388|Greater Might|3|17|58,66,74
1389|Greater Shield|3|17|58,66,74
1390|War Chant|3|52|58,66,74
1391|Earth Chant|3|52|58,66,74
1392|Resist Holy|3|17,43|58,66,74
1393|Resist Dark|3|17,30|58,66,74
1394|Trance|10|16,30|56,58,60,62,64,66,68,70,72,74
1395|Erase|10|16,17,30,43|56,58,60,62,64,66,68,70,72,74
1396|Magical BackFire|10|16|56,58,60,62,64,66,68,70,72,74
1397|Clarity|3|30|58,66,74
1398|Mana Burn|10|16,17,30,43|56,58,60,62,64,66,68,70,72,74
1399|Mana Storm|5|16|58,62,66,70,74
1400|Turn Undead|10|16,30|56,58,60,62,64,66,68,70,72,74
1401|Major Heal|11|16,30|56,58,60,62,64,66,68,70,72,74
1402|Major Group Heal|5|16|58,62,66,70,74
1403|Summon Friend|1|14,28,41|56
1405|Divine Inspiration|4|2,3,5,6,7,9,12,13,14,16,17,20,21,23,24,27,28,30,33,34,36,37,40,41,43,46,48,51,52,55,57,127,128,129,130,135|52,61,76
1406|Summon Feline King|1|96|79
1407|Summon Magnus the Unicorn|1|104|79
1408|Summon Spectral Lord|1|111|79
1409|Cleanse|1|97|78
1410|Salvation|1|97|79
1411|Mystic Immunity|1|98|79
1412|Spell Turning|1|98|79
1413|Magnus' Chant|1|116|79
1414|Victory of Pa'agrio|1|115|79
1415|Pa'agrio's Emblem|1|115|78
1416|Pa'agrio's Fist|1|115|79
1417|Aura Flash|5|12,27,40|58,62,66,70,74
1418|Celestial Shield|1|16|64
1419|Volcano|1|94|80
1420|Cyclone|1|110|80
1421|Raging Waves|1|103|80
1422|Day of Doom|1|95|80
1423|Gehenna|1|95|80
1424|Anti-Summoning Field|1|96,104,111|80
1425|Purification Field|1|97|80
1426|Miracle|1|97|80
1427|Flames of Invincibility|1|115|80
1428|Mass Recharge|1|105,112|80
1429|Gate Chant|1|116|78
1430|Invocation|5|16,30,43|56,60,64,68,72
1431|Fallen Arrow|6|123,124|5,10,15
1432|Increase Power|2|123,125|10,28
1433|Abyssal Blaze|10|125,126|20,24,28,32,36
1434|Dark Explosion|4|125,126|24,28,32,36
1435|Death Mark|10|125,126,128,129|24,32,40,46,52,58,62,66,70,74
1436|Soul of Pain|30|128,129|40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
1437|Dark Flame|26|128,129|46,49,52,55,58,60,62,64,66,68,70,72,74
1438|Annihilation Circle|9|128,129|58,60,62,64,66,68,70,72,74
1439|Curse of Divinity|5|128,129|66,68,70,72,74
1440|Steal Divinity|5|128,129|55,60,64,68,72
1441|Soul to Empower|3|128,129|43,55,64
1442|Protection from Darkness|3|128,129|58,66,74
1443|Dark Weapon|1|128,129|46
1444|Pride of Kamael|1|128,129|49
1445|Surrender to Dark|18|125,126,128,129|28,32,36,40,43,46,49,52,55,58,60,62,64,66,68,70,72,74
1446|Shadow Bind|11|128,129|52,55,58,60,62,64,66,68,70,72,74
1447|Voice Bind|9|128,129|58,60,62,64,66,68,70,72,74
1448|Blink|1|128,129|60
1451|Fire Vortex Buster|1|94|79
1452|Count of Fire|1|94|79
1453|Ice Vortex Crusher|1|103|79
1454|Diamond Dust|1|103|79
1455|Throne of Ice|1|103|79
1456|Wind Vortex Slug|1|110|79
1457|Empowering Echo|1|110|79
1458|Throne of Wind|1|110|79
1459|Divine Power|1|97|79
1460|Mana Gain|1|105,112|79
1461|Chant of Protection|1|116|79
1462|Seal of Blockade|1|115|79
1467|Meteor|1|94,95|81
1468|Star Fall|1|103,110|81
1469|Leopold|1|132,133|78
1470|Prahnah|1|134|78
1473|Change Weapon|1|125,126|20
1474|Abyssal Power|1|128,129|43
1475|Erase Mark|3|125,126,128,129|24,46,66
1476|Appetite for Destruction|3|135|40,49,58
1477|Vampiric Impulse|3|135|52,64,72
1478|Protection Instinct|2|135|46,60
1479|Magic Impulse|3|135|55,62,68
1480|Soul Harmony|1|135|70
1481|Oblivion|7|135|43,49,55,60,64,68,72
1482|Weak Constitution|4|135|62,66,70,74
1483|Thin Skin|7|135|46,52,58,62,66,70,74
1484|Enervation|4|135|60,64,68,72
1485|Spite|3|135|66,70,74
1486|Mental Impoverish|4|135|68,70,72,74
1487|Restoration|8|135|40,46,52,58,62,66,70,74
1488|Restoration Impact|3|135|49,60,68
1492|Flame Armor|1|94|83
1493|Frost Armor|1|103|83
1494|Hurricane Armor|1|110|83
1495|Vampiric Mist|1|95|83
1496|Servitor Barrier|1|96,104,111|81
1497|Excessive Loyalty|1|96,104,111|81
1498|Mutual Response|1|96,104,111|81
1499|Improved Combat|1|17|70
1500|Improve Magic|1|43|70
1501|Improved Condition|1|17|70
1502|Improve Critical|1|43|70
1503|Improve Shield Defense|1|30|70
1504|Improve Movement|1|30|70
1505|Sublime Self-sacrifice|1|97|83
1506|Blessing of Eva|1|105|83
1507|Lord of Vampire|1|112|83
1508|Thorn Root|1|112|80
1509|Seal of Limit|1|115|83
1510|Soul Cleanse|1|127,128,129,130,135|66
1511|Curse of Life Flow|8|128,129|60,62,64,66,68,70,72,74
1512|Soul Vortex|1|132,133|77
1513|Soul Vortex Extinction|1|132,133|79
1514|Soul Barrier|1|127,130|58
1515|Lightning Barrier|1|132,133,136|80
1516|Soul Strike|1|132,133|76
1517|Chant of Combat|1|52|70
1518|Chant of Critical Attack|1|52|72
1519|Chant of Blood Awakening|1|52|74
1520|Inquisitor|1|16|40
1521|Inquisitor|1|30|40
1522|Inquisitor|1|43|40
1526|Steal Mana|3|17|44,56,64
1527|Expert Casting|3|128,129,135|40,49,55
1529|Soul Web|7|128,129|62,64,66,68,70,72,74
1530|Death Spike|13|41|44,48,52,56,58,60,62,64,66,68,70,72,74
1531|Blessed Blood|7|43|40,48,56,60,64,68,72
1532|Enlightement|1|94,95,103,110,132,133|81
1533|Enlightement|1|97,98,105,112,115,116|81
1535|Chant of Movement|1|52|72
1536|Combat of Paagrio|1|51|70
1537|Critical of Paagrio|1|51|74
1538|Condition of Paagrio|1|51|72
1539|Stigma of Shilien|4|43|40,52,62,70
1540|Turn Stone|1|97,98,105,112,115,116|81
1542|Counter Critical|1|98|83
1543|Great Fury|1|116|83
1547|Spirit Sharing|3|14,28,41|44,62,70
1548|Resist Earth|3|17|60,64,68
1549|Chant of Elements|1|116|76
1550|Mass Cure Poison|1|105,112|76
1551|Mass Purify|1|112|76
1552|Mass Vitalize|1|105|76
1553|Chain Heal|1|97,105,112,115|83
1554|Aura Blast|1|94,103,110,132,133|81
1555|Aura Cannon|1|94,103,110,132,133|82
1556|Arcane Shield|1|94,103,110,132,133|83
1557|Servitor Share|1|95,96,104,111|83
1558|Dimension Spiral|24|14,28,41,96,104,111|40,44,48,52,56,58,60,62,64,66,68,70,72,74,76,77,78,79,80,81,82,83,84,85
1559|Potential Ability|3|54,55|28,40,49
1560|Lucky Blow|1|117|83
1561|Battle Cry|5|57|40,49,58,64,70
1562|Chant of Berserker|2|52|44,52
1563|Fury of Pa'agrio|2|51|48,56
1564|Piercing Attack|1|98|80
1565|Mana Pump|6|128,129|40,49,58,64,68,72
20006|Soul Roar|1|127|62
```
---

## 6. Skill Learning

### 6.1 APIs nativas relevantes (VERIFIED - javap sobre el JAR del TARGET)

| Firma | Clase |
|---|---|
| `public void rewardSkills()` | `entity.actor.Player` |
| `public int giveAvailableSkills(boolean, boolean, boolean)` | `entity.actor.Player` |
| `public void setPlayerClass(int)` | `entity.actor.Player` |
| `public Skill addSkill(Skill)` / `addSkill(Skill, boolean)` | `entity.actor.Player` |
| `public Skill removeSkill(Skill)` / `removeSkill(Skill, boolean)` / `removeSkill(Skill, boolean, boolean)` | `entity.actor.Player` |
| `private void restoreSkills()` | `entity.actor.Player` |
| `public Collection<SkillLearn> getAvailableSkills(Player, PlayerClass, boolean, boolean)` | `data.xml.SkillTreeData` |
| `public Collection<Skill> getAllAvailableSkills(Player, PlayerClass, boolean, boolean, boolean)` | `data.xml.SkillTreeData` |
| `public void addSkills(Player, boolean)` | `data.xml.SkillTreeData` |
| `public boolean isSkillAllowed(Player, Skill)` | `data.xml.SkillTreeData` |
| `public Map<Integer, SkillLearn> getCompleteClassSkillTree(PlayerClass)` | `data.xml.SkillTreeData` |
| `public int getMinLevelForNewSkill(Player, Map<Integer, SkillLearn>)` | `data.xml.SkillTreeData` |
| `public List<SkillLearn> getAvailableAutoGetSkills(Player)` | `data.xml.SkillTreeData` |

### 6.2 Flujo normal de aprendizaje (VERIFIED - bytecode del TARGET)

Bytecode observado de `Player.rewardSkills()`:

```text
0:  getstatic     PlayerConfig.AUTO_LEARN_SKILLS
3:  ifeq          21
6:  aload_0
11: getstatic     PlayerConfig.AUTO_LEARN_SKILLS_WITHOUT_ITEMS
14: invokevirtual giveAvailableSkills:(ZZZ)I
21: aload_0
22: invokevirtual giveAvailableAutoGetSkills:()V
```

Es decir: con `AutoLearnSkills = True`, `rewardSkills()` delega en
`giveAvailableSkills(AUTO_LEARN_SKILLS_WITHOUT_ITEMS, ..., ...)`; en caso contrario usa la
via "auto get" (premios automaticos).

`giveAvailableSkills(...)` consulta `SkillTreeData.getInstance()` y materializa las skills
aprendibles de la clase/nivel, gestionando `CommonSkill.getSkill()` y `removeSkill(Skill)`.

### 6.3 Cambio de clase (VERIFIED - bytecode; nombre del metodo externo INFERRED)

Bytecode de `Player.setPlayerClass(int)`:

```text
216: invokevirtual SubClassHolder.setPlayerClass:(I)V
221: invokevirtual setTarget:(L...WorldObject;)V
361: invokevirtual rewardSkills:()V
365: invokevirtual isGM:()Z
368: ifne          381
371: getstatic     PlayerConfig.DECREASE_SKILL_LEVEL
```

El metodo de cambio de subclase (identificado por su bytecode; su nombre no aparecio en la
salida filtrada) contiene esta secuencia:

```text
291: invokevirtual removeSkill:(L...Skill;ZZ)L...Skill;   <- elimina skills antiguas
299: invokevirtual stopAllEffectsExceptThoseThatLastThroughDeath:()V
328: invokevirtual restoreSkills:()V
332: invokevirtual rewardSkills:()V
336: invokevirtual regiveTemporarySkills:()V
```

Y `lambda$setPlayerClass$0()` termina en `PlayerInventory.applyItemSkills()`.

Respuestas verificadas:

- **A) Como aprende normalmente un Player:** por `rewardSkills()` (invocado en login y subida
  de nivel), que con `AutoLearnSkills=True` entrega las skills disponibles segun skill tree.
- **B) Que sucede al cambiar de clase:** se actualiza la clase (`SubClassHolder.setPlayerClass`),
  se eliminan skills antiguas, se detienen efectos y se re-ejecuta
  `restoreSkills() -> rewardSkills() -> regiveTemporarySkills()`.
- **C) Se eliminan skills antiguas:** **SI**, en la ruta de cambio de clase
  (`removeSkill(Skill, boolean, boolean)` antes de restaurar).
- **D) Deteccion de skills invalidas al cargar:** `restoreSkills()` + validacion por
  `SkillCheckEnable` / `SkillCheckRemove` (ver 6.4).
- **E) Que parte es completamente nativa:** TODO el ciclo anterior es nativo; no requiere
  codigo custom.

### 6.4 Validacion y eliminacion (VERIFIED - config del TARGET)

| Config | Archivo:linea | Valor en este TARGET |
|---|---|---|
| `SkillCheckEnable` | `game\config\General.ini:145` | True |
| `SkillCheckRemove` | `game\config\General.ini:150` | True |
| `SkillCheckGM` | `game\config\General.ini:154` | False |
| `DecreaseSkillOnDelevel` | `game\config\Player.ini:20` | True |
| `AutoLearnSkills` | `game\config\Player.ini:90` | True |
| `AutoLearnSkillsWithoutItems` | `game\config\Player.ini:95` | False |
| `AutoLearnForgottenScrollSkills` | `game\config\Player.ini:99` | False |
| `AutoLearnDivineInspiration` | `game\config\Player.ini:131` | False |
| `StoreSkillCooltime` | `game\config\Player.ini:149` | True |
| `AltGameSkillLearn` | `game\config\Player.ini:213` | False |
| `EnableModifySkillDuration` | `game\config\Player.ini:56` | False |
| `EnableModifySkillReuse` | `game\config\Player.ini:84` | False |

`General.ini` describe `SkillCheckEnable` como "Check players for non-allowed skills" y
`SkillCheckRemove` como "If true, remove invalid skills from player and database".
Con los valores actuales, la validacion y la eliminacion de skills invalidas estan **ACTIVAS**.
### 6.5 Distinciones conceptuales (importante para no disenar de mas)

| Concepto | Mecanismo nativo |
|---|---|
| **Aprender** skills | `rewardSkills()` / `giveAvailableSkills(...)` + SkillTreeData |
| **Restaurar** skills | `restoreSkills()` (desde DB, al cargar / cambiar clase) |
| **Validar** skills | `SkillCheckEnable` (+ `SkillTreeData.isSkillAllowed`) |
| **Eliminar** invalidas | `removeSkill(...)` + `SkillCheckRemove` / `DecreaseSkillOnDelevel` |
| **Cambiar clase** | `setPlayerClass(int)` + secuencia remove / restore / reward |

No se propone ninguna implementacion nueva en este catalogo.

### 6.6 Evidencia UPSTREAM (contraste)

`Player.java:2503`, `:2688`, `:2693`, `:2799`, `:7377`, `:10856` (rewardSkills /
giveAvailableSkills) | `SkillTreeData.java:532`, `:534` (getAvailableSkills) |
`GeneralConfig.java:234` (SkillCheckEnable) | `RebirthManager.java:1000` (giveAvailableSkills).
Estado: **UPSTREAM** (misma estructura de paquetes; revision de contraste).

---

## 7. Functional Classification

### 7.1 Regla de clasificacion (VERIFIED - determinista, NO por nombre)

La clasificacion no usa el nombre del skill. Se aplica esta prioridad sobre datos reales:

| Orden | Categoria primaria | Condicion evaluada |
|---|---|---|
| 1 | PASSIVE | `operateType == P` |
| 2 | TOGGLE | `operateType == T` |
| 3 | RESURRECTION | effects contienen `Resurrection` o `ResurrectionSpecial` |
| 4 | CP_HEAL | `CpHeal` o `CpHealPercent` |
| 5 | MP_HEAL | `ManaHeal*` |
| 6 | HP_HEAL | `Heal`, `HealPercent`, `HealOverTime`, `HpByLevel` |
| 7 | CROWD_CONTROL | `Stun/Sleep/Root/Paralyze/Mute/Fear/Confuse/Bluff/Disarm/Petrification/Anchor/ThrowUp/Reeling/Betray/Distrust/PhysicalDamageMute` |
| 8 | SUMMON | `Summon/SummonNpc/SummonPet/SummonCubic/SummonTrap/SummonAgathion` |
| 9 | TRANSFORMATION | `Transformation` |
| 10 | DEBUFF | `Debuff / DispelBy* / DispelAll / TargetCancel / StealAbnormal` |
| 11 | PHYSICAL_ATTACK | `PhysicalDamage/PhysicalSoulDamage/Backstab/FatalBlow/SoulBlow/EnergyDamage/PolearmSingleTarget` |
| 12 | MAGIC_ATTACK | `MagicalDamage/MagicalSoulDamage/MagicalDamageMp/DamOverTime/DamOverTimePercent/ManaDamOverTime/HpDrain` |
| 13 | SELF_BUFF / PARTY_BUFF / SINGLE_BUFF / BUFF_OTHER | `Buff/ImmobileBuff/ImmobilePetBuff/MaxHp/MaxCp/MaxMp`, desdoblado por `targetType` |
| 14 | OTHER | sin coincidencia |

### 7.2 Conteos por categoria primaria (VERIFIED)

| Categoria | Skills | | Categoria | Skills |
|---|---|---|---|---|
| PASSIVE | 2356 | | TRANSFORMATION | 267 |
| OTHER | 2124 | | SUMMON | 226 |
| SELF_BUFF | 646 | | PARTY_BUFF | 178 |
| PHYSICAL_ATTACK | 474 | | HP_HEAL | 144 |
| MAGIC_ATTACK | 450 | | TOGGLE | 71 |
| DEBUFF | 410 | | MP_HEAL | 68 |
| CROWD_CONTROL | 331 | | CP_HEAL | 35 |
| SINGLE_BUFF | 327 | | RESURRECTION | 31 |
| BUFF_OTHER | 6 | | **Total** | **8144** |

### 7.3 Casos ambiguos y limitaciones (declarado)

- **OTHER = 2124**: agrupa utilidades (teleport, open door, spoil, grow, mounts, cosmetica,
  flags de zona, triggers internos...). No se ha inventado una categoria por cada una porque
  no hay evidencia suficiente que las agrupe de forma util para un bot.
- Un skill puede contener VARIOS efectos de categorias distintas; esta tabla usa la
  **categoria primaria** por prioridad. Los conteos de efectos (4.4 / 11.1) son independientes.
- La clasificacion es **VERIFIED respecto a la REGLA** (reproducible y auditable), pero la
  *interpretacion funcional* de una categoria para un bot futuro es **PROPOSED**.
---

## 8. Conditions

Seccion central del catalogo: documenta el sistema de condiciones NATIVO que permitiria
expresar reglas futuras (del tipo "HP < X% -> skill") **sin crear un sistema paralelo**.

### 8.1 Arquitectura (VERIFIED)

Hay **dos mecanismos distintos** que usan las mismas condiciones:

| Mecanismo | Ubicacion XML | Efecto |
|---|---|---|
| **Precondicion de skill** | `<conditions>...</conditions>` dentro de `<skill>` | Decide si el skill **puede castearse** |
| **Condicion de efecto/stat** | bloques `<and>` dentro de `<effects><effect>` | Decide si un **valor de stat se aplica** |

Backing nativo en el TARGET (javap):

- `mechanics.skill.Skill` tiene `private List<Condition> _preCondition` y
  `private List<Condition> _itemPreCondition`.
- `Skill.checkCondition(Creature, WorldObject, boolean)` evalua las precondiciones.
- `Skill.attach(Condition, boolean)` permite asociar condiciones.
- `Creature.checkDoCastConditions(Skill)` es la puerta de entrada del casteo.

La fabrica XML -> objeto es `DocumentBase.parseCondition(Node, Object)`:

| Nodo XML | Metodo | Clase producida |
|---|---|---|
| `and` | `parseLogicAnd` | `ConditionLogicAnd` |
| `or` | `parseLogicOr` | `ConditionLogicOr` |
| `not` | `parseLogicNot` | `ConditionLogicNot` |
| `player` | `parsePlayerCondition` | condiciones de jugador |
| `target` | `parseTargetCondition` | condiciones de objetivo |
| `using` | `parseUsingCondition` | condiciones de item / arma |
| `game` | `parseGameCondition` | condiciones de juego |

Anchors UPSTREAM (mismos paquetes que el JAR): `util/DocumentBase.java:708-741` (dispatch),
`:798` (parsePlayerCondition), `:1276` (parseTargetCondition), `:1468` (parseUsingCondition),
`:1570` (parseGameCondition).

### 8.2 Vocabulario real de `<player>` (VERIFIED)

Atributos aceptados por `parsePlayerCondition` y frecuencia real de uso en el datapack:

| Atributo | Condicion | Usos |
|---|---|---|
| `invsize` | `ConditionPlayerInvSize` | 393 |
| `weight` | `ConditionPlayerWeight` | 393 |
| `cantransform` | `ConditionPlayerCanTransform` | 266 |
| `agathionid` | `ConditionPlayerAgathionId` | 149 |
| `level` | `ConditionPlayerLevel` | 130 |
| `active_skill_id` | `ConditionPlayerActiveSkillId` | 124 |
| `class_id_restriction` | `ConditionPlayerClassIdRestriction` | 123 |
| `insidezoneid` | `ConditionPlayerInsideZoneId` | 59 |
| `canescape` | `ConditionPlayerCanEscape` | 43 |
| `cansummon` | `ConditionPlayerCanSummon` | 33 |
| `flymounted` | `ConditionPlayerFlyMounted` | 28 |
| `levelrange` | `ConditionPlayerLevelRange` | 24 |
| `charges` | `ConditionPlayerCharges` | 22 |
| `canresurrect` | `ConditionPlayerCanResurrect` | 18 |
| **`hp`** | **`ConditionPlayerHp`** | **11** |
| `souls` | `ConditionPlayerSouls` | 9 |
| **`mp`** | **`ConditionPlayerMp`** | **6** |
| `callpc` | `ConditionPlayerCallPc` | 5 |
| `npcidradius` | `ConditionPlayerRangeFromNpc` | 5 |
| `sex` | `ConditionPlayerSex` | 4 |
| `transformationid` | `ConditionPlayerTransformationId` | 4 |
| `active_effect_id_lvl` | `ConditionPlayerActiveEffectId` | 4 |
| `siegezone` | `ConditionSiegeZone` | 4 |
| **`cp`** | **`ConditionPlayerCp`** | **3** |
| `cansummonsiegegolem` | `ConditionPlayerCanSummonSiegeGolem` | 3 |
| `active_effect_id` | `ConditionPlayerActiveEffectId` | 2 |
| `active_skill_id_lvl` | `ConditionPlayerActiveSkillId` | 2 |
| `canuntransform` | `ConditionPlayerCanUntransform` | 2 |
| `cancreatebase` | `ConditionPlayerCanCreateBase` | 2 |
| `canrefuelairship` | `ConditionPlayerCanRefuelAirship` | 2 |
| (1 uso c/u) | `chaotic`, `clanhall`, `cantakecastle`, `isridingstrider`, `fort`, `cansweep`, `cantakefort`, `instanceid`, `checkabnormal`, `flying`, `cancreateoutpost` | 1 c/u |

Ademas el parser reconoce (aunque 0 usos en skills): `races`, `ishero`, `ispvpflagged`,
`resting`, `moving`, `running`, `standing`, `behind`, `front`, `olympiad`, `grade`, `pkcount`,
`siegeside`, `pledgeclass`, `isclanleader`.
### 8.3 SEMANTICA REAL de HP / CP / MP (VERIFIED en codigo fuente - CRITICO)

No asumir la semantica por el nombre. Operadores reales:

```java
// ConditionPlayerHp.java:43
return (effector != null) && (((effector.getCurrentHp() * 100) / effector.getMaxHp()) <= _hp);

// ConditionPlayerCp.java:46
return (effector != null) && (((effector.getCurrentCp() * 100) / effector.getMaxCp()) >= _cp);
```

| XML | Semantica REAL | Ejemplo |
|---|---|---|
| `<player hp="60" />` | HP actual **<=** 60% del maximo | Frenzy: solo casteable con HP <= 60% |
| `<player cp="70" />` | CP actual **>=** 70% del maximo | umbral MINIMO, no maximo |
| `<player mp="X" />` | `ConditionPlayerMp` (patron analogo) | PENDING: operador exacto no impreso en la evidencia capturada |

**Trampa documentada:** HP y CP usan **operadores opuestos** (`<=` vs `>=`). Reutilizar la
logica de HP para CP produce el comportamiento inverso. Ver anomalia A10.

### 8.4 Vocabulario real de `<target>` (VERIFIED)

| Atributo | Condicion | Usos en skills |
|---|---|---|
| `npcid` | `ConditionTargetNpcId` | 59 |
| `mindistance` | `ConditionMinDistance` | 22 |
| `race` | `ConditionTargetRace` | 21 |
| `npctype` | `ConditionTargetNpcType` | 12 |
| `active_effect_id` | `ConditionTargetActiveEffectId` | 7 |
| `mypartyexceptme` | `ConditionTargetMyPartyExceptMe` | 5 |
| `abnormal` | `ConditionTargetAbnormal` | 1 |
| `active_skill_id` | `ConditionTargetActiveSkillId` | 1 |
| `class_id_restriction` | `ConditionTargetClassIdRestriction` | 1 |
| (0 usos en skills) | `aggro`, `level`, `levelrange`, `playable`, `siegezone`, `using`, `weight`, `invsize`, `active_effect_id_lvl`, `active_skill_id_lvl` | 0 |

**NOT FOUND (verificado dos veces):** **NO EXISTE `ConditionTargetHp`.**
`parseTargetCondition` no tiene clausula `hp`. Por tanto **"target HP percentage" NO es una
condicion XML nativa disponible** y no debe documentarse como tal. Ver anomalia A9.

### 8.5 Vocabulario real de `<using>` (VERIFIED) - clave para equipo

| Atributo | Condicion | Usos | Nota |
|---|---|---|---|
| `kind` | `ConditionUsingItemType` | 160 | tipos de arma (`SWORD`, `BLUNT`, ...) y armadura, resueltos por `mask()` sobre `WeaponType` / `ArmorType` |
| `slot` | `ConditionUsingSlotType` | 5 | `BodyPart` (`lrhand`, ...) |
| `slotitem` | `ConditionSlotItemId` | 0 en skills | formato `id;slot;enchant` - **incluye nivel de enchant** |
| `skill` | `ConditionUsingSkill` | 0 en skills | skill en uso |
| `weaponchange` | `ConditionChangeWeapon` | 1 | permite o no cambio de arma |
### 8.6 Vocabulario real de `<game>` (VERIFIED)

| Atributo | Condicion | Usos |
|---|---|---|
| `night` | `ConditionGameTime` | 1 |
| `skill` | `ConditionWithSkill` | 0 |
| `chance` | `ConditionGameChance` | 0 |

### 8.7 Inventario de clases de condicion (VERIFIED)

`org.l2jmobius.gameserver.mechanics.conditions` contiene **88 artefactos** (aprox. 85 clases de
condicion concretas + `Condition` base, `ConditionCategoryType`, `ConditionListener`).
Familias principales:

- **Player stats / estado:** `ConditionPlayerHp`, `ConditionPlayerMp`, `ConditionPlayerCp`,
  `ConditionPlayerLevel`, `ConditionPlayerLevelRange`, `ConditionPlayerState`,
  `ConditionPlayerGrade`, `ConditionPlayerCharges`, `ConditionPlayerSouls`,
  `ConditionPlayerWeight`, `ConditionPlayerInvSize`, `ConditionPlayerSex`,
  `ConditionPlayerRace`, `ConditionPlayerClassIdRestriction`, `ConditionPlayerSubclass`,
  `ConditionPlayerTransformationId`.
- **Capacidades del jugador:** `ConditionPlayerCanResurrect`, `ConditionPlayerCanSummon`,
  `ConditionPlayerCanTransform`, `ConditionPlayerCanEscape`, `ConditionPlayerCanSweep`,
  `ConditionPlayerCanTakeCastle`, `ConditionPlayerCanTakeFort`,
  `ConditionPlayerCanCreateBase`, `ConditionPlayerCanCreateOutpost`.
- **Buffs / abnormals del jugador:** `ConditionPlayerActiveEffectId`,
  `ConditionPlayerActiveSkillId`, `ConditionPlayerCheckAbnormal`.
- **Objetivo:** `ConditionTargetLevel`, `ConditionTargetLevelRange`, `ConditionTargetNpcId`,
  `ConditionTargetNpcType`, `ConditionTargetRace`, `ConditionTargetAbnormal`,
  `ConditionTargetMyPartyExceptMe`, `ConditionTargetPlayable`, `ConditionTargetAggro`,
  `ConditionTargetClassIdRestriction`, `ConditionTargetUsesWeaponKind`,
  `ConditionTargetWeight`, `ConditionTargetInvSize`, `ConditionTargetNone`.
- **Item / arma:** `ConditionUsingItemType`, `ConditionUsingSlotType`, `ConditionUsingSkill`,
  `ConditionSlotItemId`, `ConditionSlotItemType`, `ConditionChangeWeapon`.
- **Logica:** `ConditionLogicAnd`, `ConditionLogicOr`, `ConditionLogicNot`.
- **Otros:** `ConditionMinDistance`, `ConditionItemId`, `ConditionInventory`,
  `ConditionGameTime`, `ConditionGameChance`, `ConditionWithSkill`, `ConditionSiegeZone`.
### 8.8 Uso real de condiciones en el datapack (VERIFIED)

| Metrica | Valor |
|---|---|
| Skills con elemento `<conditions>` | 1443 |
| Skills con condiciones a nivel de efecto | 346 |
| Nodos `<player>` dentro de conditions | 1192 |
| Nodos `<and>` dentro de conditions | 616 |
| Nodos `<not>` dentro de conditions | 192 |
| Nodos `<using>` dentro de conditions | 161 |
| Nodos `<target>` dentro de conditions | 129 |
| Nodos `<game>` dentro de conditions | 1 |

### 8.9 Reutilizabilidad nativa (INFERRED -> PROPOSED, NO implementado)

Las condiciones existentes son **suficientes** para expresar de forma declarativa buena parte de
las reglas objetivo del proyecto futuro:

| Regla futura objetivo | Soporte nativo | Estado |
|---|---|---|
| HP < 30% -> Frenzy | SI: `ConditionPlayerHp` (`<=`) como precondicion | VERIFIED (ya en el datapack) |
| HP < 40% -> skill defensiva | SI: `ConditionPlayerHp` | VERIFIED (mecanismo) |
| Party HP < 60% -> Heal | PARCIAL: no existe condicion de HP de OTRO miembro | ver 12.4 |
| Buff ausente -> aplicar buff | NO como condicion XML; si via `EffectList` en codigo | ver 11.4 |
| Cooldown -> respetar disponibilidad | NO como condicion XML; el motor ya lo aplica al castear | ver 10.4 |
| Target HP % | **NO EXISTE** (`ConditionTargetHp` ausente) | NOT FOUND |
| Distancia al objetivo | `ConditionMinDistance` / `isInsideRadius2D|3D` | VERIFIED |
| Arma equipada | `ConditionUsingItemType` (`kind`) / `ConditionUsingSlotType` (`slot`) | VERIFIED |
| Item+slot+enchant | `ConditionSlotItemId` (`slotitem`) | VERIFIED (parser) / 0 usos en skills |

Todo lo marcado INFERRED/PROPOSED **no esta implementado**; es solo analisis de requisitos.
---

## 9. Frenzy Case Study

Investigado y documentado; **NO implementado**. Fuente: `game\data\stats\skills\00100-00199.xml`
(bloque completo en lineas 3088-3143).

### 9.1 Ficha real (VERIFIED)

| Campo | Valor real |
|---|---|
| skillId | **176** |
| name | Frenzy |
| levels | 3 |
| source XML | `game\data\stats\skills\00100-00199.xml:3088` |
| operateType | **A2** |
| targetType | **SELF** |
| abnormalType | **PINCH** |
| abnormalTime | 90 (segundos) |
| reuseDelay | **300000** (5 minutos) |
| mpConsume | 14 / 21 / 25 |
| magicLevel | 32 / 46 / 55 |
| effectPoint | 303 / 438 / 523 |
| hitTime | 1500 |
| Referencias en trees (VERIFIED) | `2ndClass\Destroyer.xml:95`, `:172` y `1stClass\OrcRaider.xml:92`. **NO** aparece en `3rdClass\Titan.xml` |

Nota (VERIFIED): Frenzy se declara en el arbol de Destroyer / OrcRaider y **no** se re-declara en
el arbol de Titan. Esto ilustra que los skill trees son **incrementales**: cada clase lista sus
skills nuevas y el motor compone el arbol completo (ver 15.2 y anomalia A14).

### 9.2 Precondicion nativa (mecanismo A: decide si se PUEDE castear)

```xml
<conditions msgId="113" addName="1">
  <player hp="60" />
</conditions>
```

- `parsePlayerCondition` -> `ConditionPlayerHp(60)` (`DocumentBase.java:918-923`).
- `ConditionPlayerHp.testImpl` -> `(getCurrentHp()*100/getMaxHp()) <= 60`.
- Via `Skill._preCondition` + `Skill.checkCondition(...)`.

**Conclusion:** Mobius **YA** evalua la condicion de HP automaticamente. Un futuro Scheme
**no necesita** comprobar el HP para poder lanzar Frenzy: el engine lo bloquea por si mismo si
el HP supera el umbral.

### 9.3 Condiciones de efecto (mecanismo B: decide si un STAT se aplica)

Dentro de `<effects><effect name="Buff">` hay funciones de stat con bloques `<and>`:

```xml
<mul stat="pAtk">
  <value>#swordblunt</value>
  <and>
    <player hp="60" />
    <using kind="SWORD,BLUNT" />
    <using slot="lrhand" />
  </and>
</mul>
```

- `#all` = 1.1 / 1.12 / 1.15 (P. Atk. base, siempre activo).
- `#swordblunt` = 1.2 / 1.3 / 1.4 (bonus condicional).
- `#twohand` = 1.05 / 1.07 / 1.1 (bonus condicional).
- `#accCombat` = 2 / 4 / 6 (add de Accuracy, condicional).

**Conclusion:** el bono condicional se aplica solo si se cumplen las condiciones de stat; eso
ocurre en el calculo de stats, no en el casteo. Es un mecanismo DISTINTO del de 9.2 y no deben
mezclarse.

### 9.4 Que es nativo y que decidiria un futuro Scheme

| Parte | Responsable |
|---|---|
| Puede castearse con HP <= 60% | **Nativo** (precondicion) |
| Consumo de MP, cooldown de 5 min, duracion 90 s | **Nativo** |
| Aplicacion condicional de bonos segun arma / slot | **Nativo** (stat functions) |
| Decidir "si me conviene activar Frenzy AHORA" | Futuro **Scheme** (no implementado) |
| Detectar si ya tengo el buff PINCH activo | Nativo via `EffectList` (ver 11.3) |

### 9.5 Casos vecinos en el mismo archivo (VERIFIED)

| skillId | Name | Condicion HP real |
|---|---|---|
| 176 | Frenzy | `<player hp="60" />` |
| 181 | Revival | `<player hp="10" />` |
| 190 | Fatal Strike | sin `<conditions>` (solo requisitos de arma) |

En total, solo **11** skills de todo el datapack usan `<player hp="...">` como precondicion
(ver 8.2). Es un mecanismo potente pero **poco usado**, lo que explica que casi ningun "umbral
de HP" exista ya resuelto para los roles de bot.

### 9.6 Anomalias asociadas a Frenzy

- **A1**: el valor real es `hp="60"` (60%), no el 30-40% que suele citarse para retail. Es el
  valor de ESTE datapack y debe respetarse como verdad local.
- **A5**: las dos ramas de bono (`#swordblunt` y `#twohand`) exigen **ambas**
  `<using slot="lrhand" />`. Los comentarios del propio XML describen una rama para arma de una
  mano y otra para dos manos, por lo que la condicion duplicada es, como minimo, **sospechosa**.
  Registrado como inconsistencia de datapack; **NO corregido**.
---

## 10. Combat / Casting

### 10.1 APIs nativas de casteo (VERIFIED - javap sobre el JAR del TARGET)

| Firma | Clase | Uso |
|---|---|---|
| `public void doCast(Skill)` | `entity.actor.Creature` | lanzar skill |
| `public void doCast(Skill, Creature, List<WorldObject>)` | `entity.actor.Creature` | lanzar con caster y objetivos explicitos |
| `public void doSimultaneousCast(Skill)` | `entity.actor.Creature` | casteo simultaneo |
| `public void doSimultaneousCast(Skill, Creature, List<WorldObject>)` | `entity.actor.Creature` | idem con objetivos |
| `public boolean checkDoCastConditions(Skill)` | `entity.actor.Creature` | **puerta de validacion del casteo** |
| `public boolean isSkillDisabled(Skill)` | `entity.actor.Creature` | skill bloqueada (p.ej. silencio) |
| `public boolean hasSkillReuse(int)` | `entity.actor.Creature` | si hay cooldown pendiente |
| `public long getSkillRemainingReuseTime(int)` | `entity.actor.Creature` | tiempo restante de reuse |
| `public boolean isCastingNow()` | `entity.actor.Creature` | esta casteando |
| `public Skill getLastSkillCast()` / `setLastSkillCast(Skill)` | `entity.actor.Creature` | ultimo skill |
| `public boolean checkCondition(Creature, WorldObject, boolean)` | `mechanics.skill.Skill` | evalua precondiciones |
| `public void attach(Condition, boolean)` | `mechanics.skill.Skill` | asociar condicion |

Anchors UPSTREAM (contraste): `Creature.java:1739`, `:1761`, `:1780` (checkDoCastConditions) |
`Creature.java:2477`, `:2488`, `:2569` (reuse) | `Player.java:9112`, `:9114` | `Options.java:219` |
`AttackableAI.java:1944`, `FortSiegeGuardAI.java:409`, `:456` (AI consultando `isSkillDisabled`).

### 10.2 Resolucion de objetivos (VERIFIED)

Existe un sistema nativo de handlers de target:

- `handler.TargetHandler` + `handler.ITargetTypeHandler` (interfaz).
- `mechanics.skill.targets.TargetType`, `AffectObject`, `AffectScope`.

Esto significa que la seleccion de a quien afecta un skill segun `targetType` ya esta resuelta
por el motor; no hay que reimplementarla.

### 10.3 Metricas de combate relevantes (VERIFIED)

| API | Clase | Uso |
|---|---|---|
| `getCurrentHp()` / `getMaxHp()` | `entity.actor.Creature` | HP (double / int) |
| `getCurrentMp()` / `getMaxMp()` | `entity.actor.Creature` | MP |
| `getCurrentCp()` / `getMaxCp()` | `entity.actor.Creature` | CP |
| `isDead()` | `entity.actor.Creature` | estado vital |
| `getStatus()` | `entity.actor.Creature` | `CreatureStatus` / `PlayerStatus` |
| `isInsideRadius2D(ILocational, int)` / `isInsideRadius3D(int,int,int,int)` | `entity.actor.Creature` | distancia |
| `hasSkillReuse(int)` / `getSkillRemainingReuseTime(int)` | `entity.actor.Creature` | cooldown |
| `isCastingNow()` | `entity.actor.Creature` | ocupacion |

### 10.4 Limite conceptual (conclusion del sprint)

**El motor ya sabe si una skill puede castearse.** La secuencia nativa cubre:

1. `checkDoCastConditions(Skill)` - silencio, estado, restricciones;
2. `Skill.checkCondition(...)` - precondiciones declaradas en XML (HP/CP/target/using/game);
3. `isSkillDisabled(Skill)` / `hasSkillReuse(int)` - bloqueos y cooldown;
4. consumo de MP y requisitos de item/arma;
5. resolucion de objetivos via `TargetHandler` / `TargetType`.

Por tanto una futura capa de comportamiento (Scheme) **solo** tendria que decidir
**CUANDO** intentar y **QUE** skill intentar, apoyandose en estas APIs, **sin duplicar ninguna
validacion**. Este catalogo NO implementa esa capa.

### 10.5 Ausencia notable (NOT FOUND)

No existe una clase `SkillHandler` en este build (paquetes refactorizados). Quien busque
`org.l2jmobius.gameserver.handler.SkillHandler` **no la encontrara**. La logica vive en
`mechanics.skill.*` + `entity.actor.Creature`. Ver anomalia A4.
---

## 11. Buffs / Heals

### 11.1 Conteo de efectos: instancias vs skills (VERIFIED)

Regla metodologica: **nunca** presentar conteos de instancias de efecto como conteos de skills.

| Effect | Instancias | Skills que lo contienen |
|---|---|---|
| Buff | 2471 | 2246 |
| RestorationRandom | 324 | 324 |
| Restoration | 70 | 70 |
| Heal | 104 | 85 |
| HealPercent | 59 | 55 |
| HealOverTime | 35 | 30 |
| HpDrain | 57 | 51 |
| CpHeal | 34 | 30 |
| CpHealPercent | 6 | 5 |
| ManaHeal | 21 | 18 |
| ManaHealPercent | 21 | 21 |
| ManaHealByLevel | 23 | 21 |
| ManaHealOverTime | 15 | 15 |
| Resurrection | 27 | 26 |
| ResurrectionSpecial | 5 | 5 |
| HpByLevel | 8 | 6 |
| MaxHp | 22 | 20 |
| MaxCp | 5 | 5 |
| Recovery / Relax / RebalanceHP | 1 c/u | 1 c/u |

### 11.2 Como determina Mobius los objetivos validos de un buff (VERIFIED)

Por `targetType` + el sistema nativo de targets (`handler.TargetHandler`,
`handler.ITargetTypeHandler`, `mechanics.skill.targets.TargetType`, `AffectObject`,
`AffectScope`). No hay que reimplementar la seleccion de objetivos.

| Pregunta del proyecto | Respuesta nativa |
|---|---|
| Buffs self-only | `targetType = SELF` (4330 skills en total, no solo buffs) |
| Buffs de party | `targetType = PARTY` (124), `AURA` (343), `PARTY_CLAN` (21), `CLAN` (42) |
| Buffs single-target | `targetType = ONE` (1678), `PARTY_MEMBER` (17), `OWNER_PET` (21), `SERVITOR` (23) |
| Area | `targetType = AREA` (179), `FRONT_AREA` (88), `AURA` (343) |

### 11.3 Como saber si un buff ya esta aplicado (VERIFIED - APIs reales del TARGET)

`entity.actor.holders.creature.EffectList`:

| Firma | Uso |
|---|---|
| `public BuffInfo getBuffInfoBySkillId(int)` | **detectar si tengo ese skill como buff activo** |
| `public BuffInfo getBuffInfoByAbnormalType(AbnormalType)` | detectar por tipo de abnormal |
| `public int getBuffCount()` | cuantos buffs activos |
| `public Queue<BuffInfo> getBuffs()` | listado completo |

`mechanics.skill.BuffInfo`:

| Firma | Uso |
|---|---|
| `public int getTime()` | **tiempo restante** |
| `public int getAbnormalTime()` | duracion declarada |
| `public int getPeriodStartTicks()` | inicio del periodo |
| `public Skill getSkill()` | skill que lo genero |
| `public Creature getEffector()` | **quien lo aplico** |
| `public Creature getEffected()` | quien lo recibe |
| `public boolean isAbnormalType(AbnormalType)` | comparacion por tipo |
| `public boolean isRemoved()` | estado |

**Conclusion:** "buff ausente" y "buff propio vs buff de otro" se determinan de forma nativa:

- ausente -> `getBuffInfoBySkillId(id) == null` (o comparar con `getBuffCount()`);
- restante -> `getBuffInfoBySkillId(id).getTime()`;
- origen -> `getEffector()` vs `getEffected()`.

Esto NO es una condicion XML: es API de codigo. Ver 11.7.

### 11.4 Heals verificados (VERIFIED - IDs reales del datapack)

| skillId | Name | levels | Tipo |
|---|---|---|---|
| 1015 | Battle Heal | 15 | heal single-target |
| 1027 | Group Heal | 15 | heal de grupo |
| 1217 | Greater Heal | 33 | heal single-target |
| 1219 | Greater Group Heal | 33 | heal de grupo |
| 1401 | Major Heal | 11 | heal single-target |
| 1020 | Vitalize | 27 | heal |
| 1018 | Purify | 3 | limpieza |
| 1335 | Balance Life | 1 | redistribucion de HP |
| 1016 | Resurrection | 9 | resurreccion |
| 1254 | Mass Resurrection | 6 | resurreccion multiple |
### 11.5 Buffs verificados usados como referencia (VERIFIED)

Fuente: `game\data\SchemeBufferSkills.xml` (ids y nombres reales, con su categoria):

| Categoria | Ejemplos verificados (id) |
|---|---|
| Buffs | 1035 Mental Shield, 1036 Magic Barrier, 1040 Shield, 1043 Holy Weapon, 1044 Regeneration, 1045 Blessed Body, 1048 Blessed Soul, 1059 Empower, 1062 Berserker Spirit, 1068 Might, 1077 Focus, 1078 Concentration, 1085 Acumen, 1086 Haste, 1087 Agility, 1204 Wind Walk, 1240 Guidance, 1242 Death Whisper, 1243 Bless Shield, 1268 Vampiric Rage, 1303 Wild Magic, 1304 Advanced Block, 1388 Greater Might, 1389 Greater Shield, 1390 War Chant, 1391 Earth Chant, 1397 Clarity, 1542 Counter Critical |
| Dances | 271 Dance of the Warrior, 272 Dance of Inspiration, 273 Dance of the Mystic, 274 Dance of Fire, 275 Dance of Fury, 276 Dance of Concentration, 277 Dance of Light, 307 Dance of Aqua Guard, 309 Dance of Earth Guard, 310 Dance of the Vampire, 311 Dance of Protection, 365 Siren's Dance, 915 Dance of Berserker |
| Songs | 264 Song of Earth, 265 Song of Life, 266 Song of Water, 267 Song of Warding, 268 Song of Wind, 269 Song of Hunter, 270 Song of Invocation |
| Chants / Special / Resist / MAGE_GROUP / FIGHTER_GROUP | categorias declaradas; el archivo tiene 142 entradas en total |

### 11.6 Ejemplos por rol (VERIFIED, con IDs reales del datapack)

| Rol | Buffs / heals verificados |
|---|---|
| Healer | 1217 Greater Heal, 1015 Battle Heal, 1401 Major Heal, 1219 Greater Group Heal, 1027 Group Heal, 1018 Purify, 1016 Resurrection, 1254 Mass Resurrection |
| Buffer | 1068 Might, 1040 Shield, 1204 Wind Walk, 1077 Focus, 1086 Haste, 1085 Acumen, 1059 Empower, 1087 Agility, 1240 Guidance, 1242 Death Whisper, 1035 Mental Shield, 1036 Magic Barrier, 1045 Blessed Body, 1048 Blessed Soul, 1388 Greater Might, 1389 Greater Shield, 1390 War Chant, 1391 Earth Chant |
| Tank | 1040 Shield, 1243 Bless Shield, 1304 Advanced Block, 1036 Magic Barrier |
| Fighter | 1068 Might, 1077 Focus, 1086 Haste, 1242 Death Whisper, 1204 Wind Walk; skill propio: 176 Frenzy |
| Mage | 1059 Empower, 1085 Acumen, 1303 Wild Magic, 1078 Concentration, 1048 Blessed Soul |

### 11.7 Que existe nativamente y que NO (VERIFIED / NOT FOUND)

| Necesidad futura | Soporte |
|---|---|
| Saber si un buff esta aplicado | SI - `EffectList.getBuffInfoBySkillId(int)` |
| Saber duracion restante | SI - `BuffInfo.getTime()` |
| Distinguir buff propio de ajeno | SI - `BuffInfo.getEffector()` |
| Saber si falta un buff en un miembro de party | SI por codigo: iterar `Party.getMembers()` + `EffectList` de cada uno |
| Expresar "buff ausente" como **condicion XML de skill** | **NOT FOUND** (no hay condicion XML de "effect missing") |
| Expresar "HP de miembro de party < X%" como **condicion XML** | **NOT FOUND** (ver 12.4) |
| Expresar "target HP %" como **condicion XML** | **NOT FOUND** (ver A9) |

### 11.8 Nota sobre RestorationRandom / Restoration (VERIFIED)

`RestorationRandom` (324 instancias / 324 skills) y `Restoration` (70/70) son efectos de
restauracion propios de NPC/monstruo y de algunas skills de jugador; NO implican la categoria
HP_HEAL en la clasificacion primaria (7.1), que usa solo `Heal`, `HealPercent`, `HealOverTime`
y `HpByLevel`. Esto evita inflar artificialmente el numero de "heals de jugador".
---

## 12. Party

### 12.1 APIs nativas de party (VERIFIED - javap sobre el JAR del TARGET)

| Firma | Clase |
|---|---|
| `public boolean isInParty()` | `entity.actor.Player` |
| `public Party getParty()` | `entity.actor.Player` |
| `public PartyDistributionType getPartyDistributionType()` | `entity.actor.Player` |
| `public boolean isInPartyWith(Creature)` | `entity.actor.Player` |
| `public boolean isInCommandChannelWith(Creature)` | `entity.actor.Player` |
| `public boolean isInClanWith(Creature)` / `isInAllyWith(Creature)` / `isInDuelWith(Creature)` | `entity.actor.Player` |
| `public boolean isOnSameSiegeSideWith(Creature)` | `entity.actor.Player` |
| `public Player getLeader()` | `entity.groups.Party` |
| `public List<Player> getMembers()` | `entity.groups.Party` |

Conclusion: obtener party, lider y miembros, y comprobar pertenencia relativa ya es 100% nativo.

### 12.2 targetType orientados a party/colectivo (VERIFIED)

| targetType | Skills | Uso |
|---|---|---|
| AURA | 343 | efecto en area alrededor del caster |
| PARTY | 124 | toda la party |
| CLAN | 42 | clan |
| PARTY_CLAN | 21 | party o clan |
| PARTY_MEMBER | 17 | un miembro concreto de la party |
| CLAN_MEMBER | 12 | un miembro del clan |
| TARGET_PARTY | 3 | party del objetivo |
| COMMAND_CHANNEL | 2 | command channel |
| PARTY_NOTME | 2 | party excepto el caster |
| AREA_FRIENDLY | 1 | area solo aliados |
| AURA_FRIENDLY | 1 | aura solo aliados |

Nota: `PARTY` (124) es mucho menos frecuente que `AURA` (343) porque muchos buffs de grupo se
implementan como aura centrada en el caster.

### 12.3 Condiciones nativas relacionadas con party (VERIFIED)

| Condicion | Uso |
|---|---|
| `ConditionTargetMyPartyExceptMe` (XML `<target mypartyexceptme="true"/>`) | 5 usos |

Es la **unica** condicion XML nativa que discrimina por party. No existe una condicion
equivalente para "HP de un miembro de la party".

### 12.4 Capacidades y LIMITES nativos (VERIFIED / NOT FOUND)

| Capacidad | Estado nativo |
|---|---|
| Obtener la party y sus miembros | VERIFIED: `Party.getMembers()` |
| Identificar lider | VERIFIED: `Party.getLeader()` |
| Saber si un Creature es de mi party | VERIFIED: `isInPartyWith(Creature)` |
| Aplicar un buff de party | VERIFIED: `targetType` PARTY / AURA + motor de targets |
| Seleccionar un miembro concreto | VERIFIED por codigo: elegir de `getMembers()` |
| Detectar HP/CP de cada miembro | VERIFIED por codigo: `member.getCurrentHp()` / `getMaxHp()` / `getCurrentCp()` |
| Detectar muerto/vivo de un miembro | VERIFIED por codigo: `member.isDead()` |
| Resurreccion | VERIFIED: effects `Resurrection` (26 skills) y `ResurrectionSpecial` (5 skills) |
| **Condicion XML "HP de miembro de party < X%"** | **NOT FOUND**. `parseTargetCondition` no expone HP de otros; solo `mypartyexceptme` |
| **Condicion XML "target HP < X%"** | **NOT FOUND** (`ConditionTargetHp` ausente, ver A9) |

**Implicacion practica:** una regla "Party HP < 60% -> Heal" **SI** es expresable, pero no como
condicion XML del skill: se expresa en la capa que elige el objetivo
(`Party.getMembers()` + `getCurrentHp()/getMaxHp()`), dejando que el motor valide el casteo.
Esto es analisis de requisitos; NO se implementa Party AI en este sprint.

### 12.5 Distancia y targeting (VERIFIED)

- `Creature.isInsideRadius2D(ILocational, int)` / `isInsideRadius3D(int, int, int, int)`.
- `ConditionMinDistance` (22 usos) para distancia minima al objetivo.
- Config nativa de rangos de AutoPlay: `ShortRange = 600`, `LongRange = 1400` (`AutoPlay.ini`).
---

## 13. AutoPlay / AutoUse / PlayerAI

Sistemas **NATIVOS del TARGET**. Documentados como referencia; **NO modificados**.

### 13.1 Clases y firmas (VERIFIED - javap sobre el JAR del TARGET)

| Clase | Firmas relevantes |
|---|---|
| `taskmanagers.AutoPlayTaskManager` | `startAutoPlay(Player)`, `stopAutoPlay(Player)`, `getInstance()` |
| `taskmanagers.AutoUseTaskManager` | `startAutoUseTask(Player)`, `stopAutoUseTask(Player)`, `getInstance()` |
| `taskmanagers.AutoPotionTaskManager` | `getInstance()` (+ logica de pociones automaticas) |
| `entity.actor.holders.player.AutoPlaySettingsHolder` | `getOptions()`, `setOptions(int)`, `doPickup()`, `setPickup(boolean)`, `getNextTargetMode()`, `setNextTargetMode(int)`, `isShortRange()`, `setShortRange(boolean)`, `isRespectfulHunting()`, `setRespectfulHunting(boolean)`, `getAutoPotionPercent()`, `setAutoPotionPercent(int)` |
| `entity.actor.holders.player.AutoUseSettingsHolder` | `getAutoSupplyItems()`, `getAutoActions()`, `getAutoBuffs()`, `getAutoSkills()`, `getAutoPotionItem()`, `setAutoPotionItem(int)`, `isAutoSkill(int)`, `getNextSkillId()`, `incrementSkillOrder()`, `resetSkillOrder()`, `isEmpty()` |
| `entity.actor.Player` | `getAutoPlaySettings()`, `getAutoUseSettings()`, `isAutoPlaying()`, `setAutoPlaying(boolean)` |
| `ai.PlayerAI` | `getNextIntention()`, `setIntentionCast(Skill, WorldObject)`, `setIntentionMoveTo(ILocational)`, `setIntentionRest()`, `setIntentionActive()`, `notifyActionThink()`, `notifyActionFinishCasting()`, `notifyActionCancel()`, `notifyActionReadyToAct()`; internos `thinkAttack()`, `thinkCast()`, `thinkPickUp()`, `thinkInteract()` |

`PlayerAI` es un `PlayableAI` -> `AbstractAI`, con intenciones (`Intention`) gestionadas por el
motor de AI. Esto significa que el "como actuar" (atacar, castear, recoger, interactuar) ya
existe de forma nativa y estructurada.

### 13.2 Configuracion real (VERIFIED - TARGET)

`game\config\Custom\AutoPlay.ini`:

| Opcion | Valor |
|---|---|
| `EnableAutoPlay` | True |
| `EnableAutoPotion` | True |
| `EnableAutoSkill` | True |
| `EnableAutoItem` | True |
| `ResumeAutoPlay` | False |
| `AssistLeader` | True (asistir al lider de party) |
| `ShortRange` | 600 |
| `LongRange` | 1400 |
| `AutoPlayPremium` | False |
| `DisabledSkillIds` | 42 |
| `DisabledItemIds` | (vacio) |
| `IgnoredAutoPickItems` | 8190, 8689 |

`game\config\Custom\AutoPotions.ini`:

| Opcion | Valor |
|---|---|
| `AutoPotionsEnabled` | **false** |
| `AutoPotionsInOlympiad` | false |
| `AutoPotionMinimumLevel` | 1 |
| `AutoCpEnabled` / `AutoCpPercentage` | true / **70** |
| `AutoHpEnabled` / `AutoHpPercentage` | true / **70** |
| `AutoMpEnabled` / `AutoMpPercentage` | true / **70** |
| `AutoCpItemIds` | 5592, 5591 |
| `AutoHpItemIds` | 1540, 1539, 1061, 1060 |
| `AutoMpItemIds` | 728 |

Nota importante: los umbrales de HP/CP/MP **existen** de forma nativa (70%), pero el sistema
completo esta **desactivado** (`AutoPotionsEnabled = false`). Ver anomalia A8.

### 13.3 Que decide cada sistema (VERIFIED / INFERRED)

| Sistema | Decide | Estado |
|---|---|---|
| `AutoPlayTaskManager` | activar/desactivar el modo auto-play de un Player | VERIFIED (firmas) |
| `AutoUseSettingsHolder` | lista ordenada de skills/buffs a auto-usar y el **siguiente skill** (`getNextSkillId()` + `incrementSkillOrder()`) | VERIFIED (firmas) |
| `AutoPotionTaskManager` | uso automatico de pociones con umbrales % | VERIFIED (config + clase) |
| `AutoPlaySettingsHolder` | opciones de comportamiento: pickup, target mode, rango corto/largo, % de pocion | VERIFIED (firmas) |
| `PlayerAI` | intenciones: atacar, castear, recoger, interactuar | VERIFIED (firmas) |
| Seleccion concreta de skill por situacion | NO existe una "IA de rotacion" configurable; hay lista ordenada + `getNextSkillId()` | INFERRED |

### 13.4 Relacion con clientless / offline (VERIFIED - UPSTREAM contrast)

Anchors UPSTREAM: `AutoPlayTaskManager.java:366` (`startAutoPlay`) |
`OfflinePlayTable.java:174` (`startAutoPlay`), `:363`, `:369` (`isAutoPlaying`) |
`Creature.java:6158` (`isAutoPlaying`).

Interpretacion (INFERRED): el auto-play nativo esta integrado con la ruta de **offline play**,
que es justamente el terreno donde viven nuestros bots clientless. Esto sugiere que la
infraestructura de auto-play podria reutilizarse, pero **no se ha verificado en runtime** en
este sprint (fuera de alcance).

### 13.5 Encaje conceptual con nuestros bots (sin cambios)

Nuestra infra (`BotProfile`, `BotProvisioning`, `BotPresets`, `AdminBotManager`) ya decide
CLASE + EQUIPO + NIVEL + POLITICA DE SKILLS (`SkillMode`). Lo que este catalogo anade es la
evidencia de que:

- el motor nativo ya resuelve validacion, cooldown, targeting y ejecucion;
- existe auto-play nativo con listas de skills/buffs y orden;
- existen APIs (`EffectList`, `Party`, condiciones XML) suficientes para una capa declarativa
  futura sin duplicar cerebro de combate.

**NO se modifica nada de esto en el sprint.**
---

## 14. Scheme Buffer (precedente nativo)

Documentado UNICAMENTE como precedente arquitectonico. Su logica **no** se copia ni se implementa.

### 14.1 Datos reales (VERIFIED)

`game\data\SchemeBufferSkills.xml` (14.977 bytes):

| Categoria declarada | Contenido |
|---|---|
| Buffs | 30 entradas verificadas |
| Dances | 13 entradas verificadas |
| Songs | 7 entradas verificadas (minimo comprobado) |
| Chants / Special / Resist / MAGE_GROUP / FIGHTER_GROUP | categorias declaradas |

Total: **142 entradas `<buff>`** y **8 categorias** (`<category type="...">`).

Formato por entrada:

```xml
<buff id="1068" level="1" price="0" desc="Increases P. Atk." />  <!-- Might -->
```

`game\config\Custom\SchemeBuffer.ini`:

| Opcion | Valor |
|---|---|
| `BufferMaxSchemesPerChar` | **4** |
| `BufferItemId` | 57 |
| `BufferStaticCostPerBuff` | -1 |

### 14.2 Que demuestra arquitectonicamente (INFERRED)

1. Mobius **ya** usa un modelo declarativo "categoria -> lista de buffs (id, level, price, desc)"
   para un servicio de buffs. Es el mismo patron que un Scheme futuro necesitaria, ya validado
   por el datapack y por el motor.
2. Los buffs se referencian por **id + level** (no por nombre), igual que hariamos nosotros.
3. Existe un limite por personaje (4 schemes) y un coste configurable (item + precio): hay
   precedente de persistencia por jugador y de coste.
4. El sistema es de **NPC buffer**, no de IA: **no** decide cuando ni a quien aplicar; el
   jugador elige. El precedente cubre *que buffs existen y como se agrupan*, pero **no** cubre
   *cuando aplicarlos*. Esa segunda parte sigue siendo responsabilidad de una capa futura
   (fuera del alcance de este sprint).

---

## 15. Class Reference

### 15.1 Clases de 3a clase verificadas (VERIFIED)

Fuente: skill trees de `3rdClass\` + `class_names2.tsv`. `entries` = sentencias de aprendizaje
declaradas en el arbol PROPIO de esa clase (ver 15.2).

| classId | Clase | parentClassId | entries | npc | FS | minLvl | maxLvl |
|---|---|---|---|---|---|---|---|
| 88 | Duelist | 2 | 23 | 15 | 8 | 76 | 83 |
| 89 | Dreadnought | 3 | 21 | 13 | 8 | 76 | 83 |
| 90 | PhoenixKnight | 5 | 34 | 23 | 11 | 76 | 85 |
| 91 | HellKnight | 6 | 34 | 23 | 11 | 76 | 85 |
| 92 | Sagittarius | 9 | 19 | 11 | 8 | 76 | 83 |
| 93 | Adventurer | 8 | 23 | 13 | 10 | 76 | 83 |
| 94 | Archmage | 12 | 20 | 13 | 7 | 76 | 83 |
| 95 | Soultaker | 13 | 21 | 14 | 7 | 76 | 83 |
| 96 | ArcanaLord | 14 | 31 | 23 | 8 | 76 | 85 |
| 97 | Cardinal | 16 | 23 | 16 | 7 | 76 | 83 |
| 98 | Hierophant | 17 | 20 | 13 | 7 | 76 | 83 |
| 99 | Eva'sTemplar | 20 | 33 | 23 | 10 | 76 | 85 |
| 100 | Swordmuse | 21 | 28 | 19 | 9 | 76 | 85 |
| 101 | WindRider | 23 | 23 | 13 | 10 | 76 | 83 |
| 102 | MoonlightSentinel | 24 | 20 | 12 | 8 | 76 | 83 |
| 103 | MysticMuse | 27 | 22 | 15 | 7 | 76 | 83 |
| 104 | ElementalMaster | 28 | 30 | 22 | 8 | 76 | 85 |
| 105 | Eva'sSaint | 30 | 22 | 15 | 7 | 76 | 83 |
| 106 | ShillienTemplar | 33 | 33 | 23 | 10 | 76 | 85 |
| 107 | SpectralDancer | 34 | 27 | 18 | 9 | 76 | 85 |
| 108 | GhostHunter | 36 | 23 | 13 | 10 | 76 | 83 |
| 109 | GhostSentinel | 37 | 20 | 12 | 8 | 76 | 83 |
| 110 | StormScreamer | 40 | 23 | 16 | 7 | 76 | 83 |
| 111 | SpectralMaster | 41 | 31 | 23 | 8 | 76 | 85 |
| 112 | ShillienSaint | 43 | 22 | 15 | 7 | 76 | 83 |
| 113 | Titan | 46 | 20 | 12 | 8 | 76 | 83 |
| 114 | GrandKhavatari | 48 | 22 | 13 | 9 | 76 | 83 |
| 115 | Dominator | 51 | 24 | 16 | 8 | 76 | 83 |
| 116 | Doomcryer | 52 | 19 | 12 | 7 | 76 | 83 |
| 117 | FortuneSeeker | 55 | 33 | 23 | 10 | 76 | 85 |
| 118 | Maestro | 57 | 20 | 11 | 9 | 76 | 83 |
| 131 | Doombringer | 127 | 17 | 9 | 8 | 76 | 83 |
| 132 | MaleSoulHound | 128 | 22 | 13 | 9 | 76 | 83 |
| 133 | FemaleSoulHound | 129 | 22 | 13 | 9 | 76 | 83 |
| 134 | Trickster | 130 | 17 | 10 | 7 | 76 | 83 |
| 136 | Judicator | 135 | 9 | 6 | 3 | 76 | 82 |

La tabla completa de las **103 clases** (StartingClass, 1a, 2a y 3a clase, incluidos Kamael)
esta en **5.6**.

### 15.2 Hallazgo estructural: los skill trees son INCREMENTALES (VERIFIED)

Los arboles de 3a clase contienen solo **9 a 34 entradas** (niveles 76-85), mientras que los de
2a clase contienen cientos (p.ej. `Gladiator` = 479, `Necromancer` = 445, de niveles 40-76).
Esto demuestra que **cada clase declara unicamente sus skills nuevas** y que el motor compone el
arbol completo (via `SkillTreeData.getCompleteClassSkillTree(PlayerClass)` +
`getAvailableSkills(...)`).

Consecuencia practica: para saber "que aprende Titan" hay que unir
`OrcFighter -> OrcRaider -> Destroyer -> Titan`. Ver anomalia A14.

Excepcion detectada: `Inspector` (135) tiene **290 entradas** con rango 40-79, patron de 2a
clase. Ver anomalia A15.
---

## 16. Role Matrix (documental, NO codigo)

Asignaciones basadas en evidencia verificada del catalogo (targetType / effects / IDs reales).
Cada fila separa lo **VERIFIED** de lo **PROPOSED** (diseno futuro, no implementado).

### 16.1 Matriz

| Rol | Capacidades observadas (VERIFIED) | Skills tipicas | targetType habitual | Buffs | Heals | Debuffs | Interaccion con party | Condiciones utiles |
|---|---|---|---|---|---|---|---|---|
| DPS melee | ataques fisicos con requisito de arma; boost propio | Frenzy (176), Fatal Strike (190) | SELF / ONE | Might (1068), Focus (1077), Haste (1086), Death Whisper (1242) | no | no | baja | `player hp`, `using kind`, `using slot`, reuse |
| Tank | defensivos + agro | Shield (1040), Bless Shield (1243), Advanced Block (1304) | SELF / ONE / AURA | Shield, Magic Barrier (1036) | no | agro (`GetAgro`, `AddHate`) | media (aguanta) | `player hp`, `target aggro`, `mindistance` |
| Healer | heals single y group + resurreccion + limpieza | 1217, 1015, 1401, 1219, 1027, 1018, 1016, 1254 | ONE / PARTY / AURA / CORPSE | Blessed Body (1045), Blessed Soul (1048), Regeneration (1044) | SI (`Heal*`) | no | **alta** (party-wide) | HP propio <= X como precondicion nativa; HP de party por API |
| Mage | dano magico + boost magico | `MagicalDamage` (311 skills) | ONE / AREA | Empower (1059), Acumen (1085), Wild Magic (1303), Concentration (1078) | no (MP propio: 1048) | `Debuff` (263 skills) | media | `player mp`, `using kind` |
| Archer | ataques a distancia fisica | `PhysicalDamage` con `kind` de arco | ONE | Guidance (1240), Focus, Haste, Death Whisper | no | ocasional | media | `using kind` (arco), `mindistance` |
| Dagger | melee de burst con `Backstab`/`FatalBlow` | `Backstab` (1 skill), `FatalBlow` (29 skills) | ONE | Focus, Haste, Death Whisper | no | posible | baja | `using kind` (dagger), `using slot` |
| Buffer | buffs de stats, dances, songs | 1068, 1040, 1204, 271-277, 264-270 | PARTY / AURA / ONE / SELF | masivo (ver 11.5) | no | no | **alta** | buff ausente por API (`EffectList`) |
| Debuffer | reduce capacidades del enemigo | `Debuff` (263 skills), `TargetCancel` (30) | ONE / AREA | no | no | alto | media | `target race`, `target npctype`, `target mypartyexceptme=false` |
| Summoner | invoca servidores | `Summon` (30 skills), `SummonNpc` (52), `SummonCubic` (18) | SUMMON / SERVITOR / NONE | buffs propios | no | no | baja (servitor) | `player active_skill_id`, `servitor` |
| Hybrid / support | mezcla 2-3 de los anteriores | depende de clase | mixto | mixto | posible | posible | alta | combinaciones `and` |

### 16.2 Como leer esta matriz (advertencia metodologica)

- **VERIFIED**: los conteos de efectos, los IDs citados y los `targetType` provienen del parseo
  real del datapack (secciones 4, 7, 11) y de la config real (seccion 13).
- **PROPOSED**: la asociacion "este rol deberia hacer X automaticamente" es DISENO FUTURO.
  Este catalogo **no** implementa comportamiento por rol.
- No se ha creado ningun perfil, loadout, scheme ni prioridad. La matriz es vocabulario
  documental para una fase posterior.

### 16.3 Limitaciones declaradas

| Limitacion | Detalle |
|---|---|
| Sin runtime | No se ha verificado en juego la eficacia de ningun rol |
| Sin margen de dano | Este catalogo NO contiene formulas de dano ni potencias; solo estructura |
| Roles no oficiales | "Buffer" o "Debuffer" no existen como entidad en el motor; son agrupaciones nuestras (INFERRED) |
| Clases objetivo | Ver 15.1 para classId reales; no se ha decidido ningun loadout
---

## 17. BotProfile / Bot Infrastructure Cross-Reference

Solo documental: relaciona este catalogo con nuestra infra existente. **NO se modifica nada.**

### 17.1 Ubicacion real (VERIFIED)

| Archivo | Ruta exacta en el TARGET |
|---|---|
| `BotProfile.java` | `game\data\scripts\handlers\chat\commands\admin\BotProfile.java` |
| `BotProvisioning.java` | `game\data\scripts\handlers\chat\commands\admin\BotProvisioning.java` |
| `BotPresets.java` | `game\data\scripts\handlers\chat\commands\admin\BotPresets.java` |
| `AdminBotManager.java` | `game\data\scripts\handlers\chat\commands\admin\AdminBotManager.java` |

Contenido observado (grep): `BotProfile` define `public enum SkillMode`, campo `_skillMode` y
`getSkillMode()`; `BotProvisioning` tiene
`applySkills(Player bot, BotProfile.SkillMode skillMode)` con `switch` sobre `SkillMode`;
`BotPresets` usa `BotProfile.SkillMode.AUTO_BY_CLASS`.

### 17.2 Distincion CUSTOM vs NATIVO (VERIFIED)

| Elemento | Origen |
|---|---|
| `SkillMode`, `BotProfile`, `BotProvisioning`, `BotPresets`, `AdminBotManager` | **CUSTOM del proyecto** (en `game\data\scripts`) |
| `AutoPlay*`, `AutoUse*`, `PlayerAI`, `SkillTreeData`, `Skill`, `EffectList`, condiciones | **NATIVO** (en `libs\GameServer.jar`) |

Comprobacion: la busqueda de `SkillMode` en **UPSTREAM** (`java\org\l2jmobius\...`) **no**
devuelve resultados. UPSTREAM **no** contiene nuestro BotAI ni `SkillMode`.

### 17.3 Conclusion de la investigacion (INFERRED -> PROPOSED)

El catalogo aporta evidencia para que una futura capa **declarativa** combine:

```text
CLASE (classId, parents, arbol incremental)      -> secciones 5, 15
EQUIPO (BOTAI-06-C)                              -> secciones 18, 19
NIVEL (getLevel / levelUpSp)                     -> secciones 5, 15
DYES                                             -> fuera de alcance (no investigado aqui)
AUTO PLAY / SKILL POLICY futura                  -> secciones 8, 10, 13
```

**sin crear un nuevo cerebro de combate**: el motor ya valida, calcula cooldown, resuelve
objetivos y ejecuta. Nuestro `SkillMode` actual (p.ej. `AUTO_BY_CLASS`) es el punto de extension
natural: decide QUE skills tiene el bot; una futura politica decidiria CUANDO usarlas.
Eso es **PROPOSED**, no implementado en este sprint.

---

## 18. Equipment Master Cross-Reference (BOTAI-06-C)

Referencia: `investigations\BOTAI-06-C_EQUIPMENT_MASTER_CATALOG.md` (no se copian sus tablas).

### 18.1 Punto de union real entre equipo y skills (VERIFIED)

El mecanismo nativo que conecta EQUIPO con SKILLS en el datapack de skills son las condiciones
`<using>`:

| Elemento | Que comprueba | Fuente |
|---|---|---|
| `<using kind="SWORD,BLUNT" />` | tipo(s) de arma/armadura (`WeaponType`/`ArmorType` + `mask()`) | `DocumentBase.parseUsingCondition:1477` |
| `<using slot="lrhand" />` | slot de `BodyPart` | `:1510` |
| `<using slotitem="id;slot;enchant" />` | item concreto en slot concreto **con enchant** | `:1539` |
| `<using weaponchange="true|false" />` | si se permite cambiar de arma | `:1553` |
| `<using skill="id" />` | skill en uso | `:1533` |
| `<target using="..." />` | tipo de arma del OBJETIVO | `parseTargetCondition:1383` |

Frecuencia real en skills: `kind` = 160, `slot` = 5, `weaponchange` = 1, `slotitem` = 0.

### 18.2 Implicaciones (VERIFIED / INFERRED)

- Un arma no "anade" una skill en el datapack de skills: **habilita o deshabilita** efectos y
  precondiciones via `kind` / `slot` (VERIFIED con Frenzy).
- El nivel de **enchant** es expresable en condiciones (`slotitem`), lo que enlaza con
  BOTAI-06-C; en la practica **no se usa** en las skills actuales (0 usos) (VERIFIED).
- Los sets de armadura otorgan skills via `stats\armorsets\*.xml` (documentado en BOTAI-06-C);
  esas skills se comportan como cualquier otra en este catalogo (INFERRED por coherencia entre
  ambos catalogos; el detalle de sets pertenece a BOTAI-06-C).

### 18.3 Regla de no duplicacion

```text
BOTAI-06-C = EQUIPO / ITEMS / SETS / WEAPONS / JEWELRY / SLOTS
BOTAI-06-D = SKILLS / BUFFS / HEALS / COMBAT / CONDITIONS / NATIVE MECHANISMS
```

El unico punto de contacto documentado aqui es la capa de condiciones de uso de item
(`<using>`); el inventario de items permanece en BOTAI-06-C.
---

## 19. SA / Weapon Interaction

Investigacion documental. **NO se implementa SA.**

### 19.1 Estado de la evidencia (VERIFIED / PENDING)

| Pregunta | Estado |
|---|---|
| Existe restriccion nativa de skill por tipo de arma? | **SI (VERIFIED)** - `ConditionUsingItemType` (`<using kind>`, 160 usos) y `ConditionTargetUsesWeaponKind` |
| Existe restriccion por slot (una / dos manos)? | **SI (VERIFIED)** - `ConditionUsingSlotType` (`<using slot>`, 5 usos; Frenzy usa `lrhand`) |
| Como se representa una SA en el TARGET? | **PENDING** - requeriria parsear `stats\items\*.xml`; NO verificado en este sprint |
| Afecta la SA a que skills puede usar el personaje? | **PENDING** - sin evidencia directa en el datapack de skills |
| Existe relacion SA -> skill declarada en XML de skills? | **NOT FOUND** - ninguna condicion `<using>` referencia una SA por nombre |
| Augmentation | Existe `stats\augmentation\augmentation_skillmap.xml` (VERIFIED que existe); su contenido NO se analizo aqui -> **PENDING** |

### 19.2 Que puede afirmarse con seguridad (VERIFIED)

- Las skills pueden exigir TIPO de arma y SLOT, y el motor lo comprueba
  (`ConditionUsingItemType`, `ConditionUsingSlotType`, `ConditionTargetUsesWeaponKind`).
- Los bonos condicionales de Frenzy (9.3) demuestran que un mismo skill cambia su efecto segun
  el arma y el slot, via bloques `<and>` con `<using kind>` + `<using slot>`.
- El enchant de un item es expresable en condiciones (`slotitem = id;slot;enchant`), con 0 usos
  reales en las skills actuales.

### 19.3 Lo que NO se afirma

No se afirma que exista un vinculo directo "SA -> skill" en este datapack. Sin evidencia
suficiente se marca **PENDING** y **no se inventa**. Cualquier trabajo futuro sobre SA debe
partir de parsear el XML de items (competencia de BOTAI-06-C) antes de concluir nada.

### 19.4 Riesgo operativo detectado (INFERRED)

Si en el futuro una capa de comportamiento decidiera usar skills con requisitos de arma
(`kind`/`slot`) sin consultar el equipo real del bot, el motor simplemente las rechazaria en el
casteo (`checkDoCastConditions`). Esto refuerza la conclusion de 10.4: **reutilizar la validacion
nativa, no duplicarla**.
---

## 20. Anomalies

Registradas, **NO corregidas**. Una anomalia no bloquea el catalogo: lo hace mas honesto.

| # | Anomalia | Estado | Evidencia / nota |
|---|---|---|---|
| **A1** | Frenzy usa `hp="60"` (60%), no el 30-40% que suele citarse | **VERIFIED** | `00100-00199.xml:3112`; el comentario del XML tambien dice 60% |
| **A2** | Skills comentadas / IDs duplicados | **REFUTED** | 8144 nodos crudos = 8144 activos = 8144 IDs unicos |
| **A3** | `SkillCheck*` NO esta en `Player.ini` sino en `General.ini` | **VERIFIED** | `General.ini:145`, `:150`, `:154` |
| **A4** | `SkillHandler` no existe en este build | **NOT FOUND** | paquetes refactorizados; logica en `mechanics.skill.*` + `Creature` |
| **A5** | Frenzy: las dos ramas de bono exigen `slot="lrhand"` | **VERIFIED** | `:3117-3140`; contradice la descripcion del propio comentario. Inconsistencia de datapack |
| **A6** | Placeholders de efecto (`#effectname1`) y de target (`#targetType`) | **VERIFIED** | `#effectname1` = 1 instancia; `#targetType` = 1 skill |
| **A7** | Entradas de tree sin `/>` | **RESUELTO** | coexistencia de 2 sintaxis (`<skill ... />` y `<skill ...>...</skill>`); parser corregido: 17702 nodos |
| **A8** | `AutoPotionsEnabled = false` | **VERIFIED** | umbrales nativos 70% existen pero el sistema esta apagado |
| **A9** | `ConditionTargetHp` no existe | **NOT FOUND** | `parseTargetCondition` sin clausula `hp`; "target HP %" no es nativo |
| **A10** | HP usa `<=` y CP usa `>=` (operadores opuestos) | **VERIFIED** | `ConditionPlayerHp.java:43` vs `ConditionPlayerCp.java:46` |
| **A11** | `EnableModifySkillDuration` / `EnableModifySkillReuse` pueden alterar duracion/reuse | **VERIFIED** | `Player.ini:56`, `:84` (ambos False ahora). `SkillDurationList` en `:65` |
| **A12** | `libs\GameServer.jar` = 2465 entradas | **PENDING** | VERIFIED que contiene las clases clave usadas (`Player`, `Creature`, `Skill`, `SkillTreeData`, `PlayerAI`, `AutoPlayTaskManager`, condiciones). NO verificado si contiene absolutamente todas las clases del core -> PENDING, sin inferir |
| **A13** | `targetType` con valores vacios o placeholder | **VERIFIED** | `(vacio)`=2 skills; `#targetType`=1 skill |
| **A14** | Skill trees son incrementales: 3a clase declara solo el delta | **VERIFIED** | Titan=20 entradas (76-83) vs Gladiator=479 (40-76); Frenzy vive en Destroyer |
| **A15** | `Inspector` (classId 135) tiene 290 entradas con rango 40-79 | **VERIFIED (observado) / PENDING (causa)** | patron de 2a clase en una 3a clase; causa no determinada |

### 20.1 Anomalias NO corregidas por diseno

Ninguna de las anteriores se ha modificado. A5 (condicion duplicada en Frenzy) y A15 (Inspector)
son candidatas a revision futura **solo** con autorizacion explicita, y su correccion quedaria
FUERA del alcance de un sprint de investigacion.

### 20.2 Riesgos derivados

| Riesgo | Mitigacion documental |
|---|---|
| Copiar la semantica de HP a CP (A10) | Semantica real documentada en 8.3 |
| Asumir "target HP %" disponible (A9) | Marcado NOT FOUND en 8.4 y 11.7 |
| Creer que un parser simple de trees basta (A7) | Dos sintaxis documentadas en 5.1 |
| Asumir que el arbol de 3a clase es completo (A14) | 15.2 explica la composicion incremental |
| Creer que el auto-potion funciona (A8) | Config real documentada en 13.2 |
---

## 21. Validation

### 21.1 Reproducibilidad (VERIFIED)

| Elemento | Valor |
|---|---|
| Fecha de investigacion | 2026-09-14 |
| Target analizado | `C:\L2J MOBIUS IA\L2J_Mobius_CT_2.6_HighFive` |
| Baseline | L2J Mobius CT 2.6 HighFive (`libs\GameServer.jar` + datapack) |
| XML de skills procesados | 95 |
| XML de skill trees procesados | 114 |
| Herramientas | OpenJDK 25 (`javap`, `jar`), PowerShell 5.1 |
| Generadores temporales | `%TEMP%\botai06d\gen1.ps1`, `gen3.ps1`, `gen4.ps1`, `gen5.ps1` |
| Intermedios en `%TEMP%` | `skills_inventory.tsv`, `skills_aggregates.txt`, `skills_presence.txt`, `trees_detail.tsv`, `trees_aggregates2.txt`, `class_names2.tsv`, `class_skill_index.txt` |
| Ubicacion de los temporales | Fuera del workspace (NO en TARGET, NO en Notebook) |
| Metodo de parseo | Regex sobre texto completo (no DOM), para no depender de XSD/entidades |
| Nota de consola | PowerShell envuelve/trunca salidas largas; los volumenes se midieron a archivo |

### 21.2 Checklist final de validacion

| Comprobacion | Resultado |
|---|---|
| Catalogo generado desde el TARGET real | SI |
| Conteos derivados de parseo real (no copiados) | SI |
| SkillTreeData / learning documentado | SI (seccion 6) |
| Buffs / heals / combat documentados | SI (secciones 11, 10) |
| Condiciones documentadas con evidencia | SI (seccion 8) |
| Frenzy investigada y NO implementada | SI (seccion 9) |
| AutoPlay / AutoUse / PlayerAI documentados | SI (seccion 13) |
| Party / party buff mechanics documentados | SI (seccion 12) |
| Nativo vs NO existente identificado | SI (7.3, 8.4, 11.7, 12.4, 19.1) |
| VERIFIED / INFERRED / PROPOSED / NOT FOUND / PENDING separados | SI |
| TARGET intacto | SI (solo lectura) |
| UPSTREAM intacto | SI (solo lectura / contraste) |
| Java / XML / SQL / config / DB sin cambios funcionales | SI |
| Sin build / sin restart / sin runtime | SI |
| Sin Scheme / BotAI / ThinkLoop / condiciones nuevas | SI |
| Sin perfiles de bots / sin loadouts | SI |
| BOTAI-06-C referenciado sin copiar tablas | SI (seccion 18) |
| INDEX.md con exactamente UNA fila nueva | SI |

### 21.3 Conteos finales (resumen)

| Metrica | Valor |
|---|---|
| XML de skills | 95 |
| Nodos `<skill>` XML | 8144 |
| Skill IDs unicos XML | 8144 |
| Skills con `<conditions>` | 1443 |
| Skills con condiciones de efecto | 346 |
| Nombres de efecto distintos | 162 |
| Archivos de skill trees | 114 |
| Nodos `<skill>` en trees | 17702 |
| Sentencias de aprendizaje (clase) | 17177 |
| Clases con classSkillTree | 103 |
| Skills aprendibles distintas (clase) | 932 |
| Skills distintas en trees (todos los tipos) | 1173 |
| Artefactos en `mechanics.conditions` | 88 (aprox. 85 condiciones concretas) |
---

## 22. Conclusions / Future Boundaries

### 22.1 Conclusiones (VERIFIED)

1. **El datapack esta completamente inventariado**: 8144 skills en 95 XML, 17702 entradas de
   aprendizaje en 114 trees, 103 clases con classSkillTree, 162 efectos distintos.
2. **Solo 1173 skills (932 de clase) son aprendibles**; las 6971 restantes son de NPC /
   monstruo / evento / summon y quedan documentadas como agregados (enfoque B).
3. **El sistema de condiciones nativo es real y suficiente** para buena parte de las reglas
   objetivo: `ConditionPlayerHp` (`<=`), `ConditionPlayerCp` (`>=`), `ConditionPlayerMp`,
   `ConditionUsingItemType` / `ConditionUsingSlotType`, `ConditionMinDistance`,
   `ConditionTargetMyPartyExceptMe`, y la logica `and`/`or`/`not`.
4. **Frenzy ya tiene su precondicion de HP resuelta por el motor** (`hp="60"`), y sus bonos
   condicionales por arma/slot tambien. Un futuro Scheme no necesita comprobar el HP para
   Frenzy: solo decidir si le conviene usarla.
5. **El motor ya valida el casteo**: `checkDoCastConditions`, `Skill.checkCondition`,
   `hasSkillReuse`, `getSkillRemainingReuseTime`, `isSkillDisabled` y el sistema de targets.
6. **Buffs y heals son totalmente inspeccionables en codigo**: `EffectList.getBuffInfoBySkillId`,
   `BuffInfo.getTime`, `BuffInfo.getEffector`, `EffectList.getBuffCount`. "Buff ausente" es
   trivialmente expresable.
7. **Party es nativo**: `getParty()`, `getLeader()`, `getMembers()`, `isInPartyWith(Creature)`.
8. **AutoPlay / AutoUse / PlayerAI existen** y ya resuelven listas ordenadas de skills/buffs,
   orden de uso (`getNextSkillId`, `incrementSkillOrder`), pickup, target modes, rangos y
   umbrales de pocion.
9. **Scheme Buffer demuestra** que el patron declarativo "categoria -> ids de buff" ya existe en
   Mobius para un NPC buffer (142 entradas, 8 categorias, max 4 schemes).
10. **El catalogo de equipo (BOTAI-06-C) y este se tocan solo** en la capa de condiciones de uso
    de item (`<using>`).

### 22.2 Lo que NO existe (NOT FOUND) - limites reales

| Ausencia | Consecuencia |
|---|---|
| `ConditionTargetHp` | "target HP %" NO es condicion XML; requiere API |
| Condicion XML de HP de un miembro de party | "Party HP < X%" requiere API (`getMembers()` + vitals) |
| Condicion XML de "buff ausente" | requiere API (`EffectList`) |
| Condicion XML de cooldown / reuse | el motor ya lo aplica; no es declarable en XML |
| `SkillHandler` | no buscar APIs con ese nombre en este build |
| Vinculo SA -> skill | no evidenciado (PENDING) |

### 22.3 Fronteras futuras (PROPOSED - NO implementado aqui)

Una fase posterior **podria** construir una capa declarativa del tipo:

```text
CONDITION (hp%, cp%, mp%, level, using kind/slot, distance, party, buff ausente via API)
   -> ACTION (intentar castear skill X sobre objetivo Y)
   -> PRIORITY (emergency / high / normal / low)
```

apoyandose **exclusivamente** en APIs nativas documentadas aqui, dejando que el motor rechace lo
que no proceda. Ese trabajo:

- **NO** empieza en este sprint;
- **NO** debe crear condiciones nuevas si las nativas bastan;
- **NO** debe duplicar validaciones de casteo, targeting ni cooldown;
- **NO** debe duplicar el inventario de equipo (BOTAI-06-C) ni el catalogo de skills (este doc).

### 22.4 Estado final del catalogo

Este documento es la fuente autoritativa de **SKILLS / SKILL TREES / BUFFS / HEALS / COMBAT /
CONDITIONS / NATIVE MECHANISMS** para este proyecto, complementaria (y no solapada) con
`BOTAI-06-C_EQUIPMENT_MASTER_CATALOG.md`.

Las decisiones futuras de Scheme/Behavior **no** se recogen aqui porque todavia no estan
decididas.

---

## Cross-References

| Documento | Relacion |
|---|---|
| `investigations/BOTAI-06-C_EQUIPMENT_MASTER_CATALOG.md` | equipo / items / sets / slots; punto de union: condiciones `<using>` |
| `investigations/BOTAI-02_ROLES_BEHAVIORS_API_RESEARCH.md` | investigacion previa de roles y behaviors |
| `investigations/BOTAI-04-B2_MOBIUS_NATIVE_EQUIPMENT_SKILLS_INVESTIGATION.md` | skills nativos ligados a equipo |
| `investigations/FASE4_01_AUTOPLAY.md` | AutoPlay / AutoUse (investigacion previa) |
| `investigations/FASE4_03_PARTY.md` | sistema de party |
| `investigations/FASE4_09_FOLLOW_ASSIST_COMBAT.md` | follow / assist / combat |
| `bots/BOT_RECIPE.md` | arquitectura implementada de bots |
| `decisions/FASE4_BOT_ARCHITECTURE_FREEZE.md` | congelacion de arquitectura de bots |
