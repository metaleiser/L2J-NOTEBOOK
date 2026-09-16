# BOTAI-06-C - Equipment Master Catalog

> **SPRINT:** BOTAI-06-C
> **FASE:** Investigacion - Equipment Master Catalog
> **MODO:** INVESTIGACION + DOCUMENTACION (sin cambios funcionales)
> **TARGET:** `C:\L2J MOBIUS IA\L2J_Mobius_CT_2.6_HighFive`
> **UPSTREAM:** solo contraste (reglas de slot). No se usa para completar IDs.
> **Estado:** GENERADO DESDE EL DATAPACK REAL (VERIFIED)
> **Fecha:** 2026-09-14
> **Baseline:** L2J Mobius CT 2.6 HighFive @ `e2518ab108` (runtime)
> **Generador:** script temporal (no forma parte del Notebook); el catalogo es regenerable.

---

## 1. Scope

Catalogo unico y reutilizable del equipo realmente presente en el datapack del TARGET:
armor, weapons, jewelry, sets, grades, slots, tipos e IDs. Sirve como fuente documental
para construir despues los perfiles de bots, sin volver a recorrer el datapack.

NO incluye (y NO se completa desde otras versiones):

- apariencia (hair/hair2/hairall/face), underwear, alldress;
- accesorios fuera de combate (cloaks/back, belts/waist, talismans/deco1, bracelets);
- variantes de evento (Fortune/Event/limited period/Player Commendation), copias Common/Shadow,
  items Sealed (intermedios de craft), variantes {PvP} y variantes SA/elementales (sufijo ` - X`);
- EtcItem, quest items, recipes, life stones, soul/spirit shots.

---

## 2. Sources

### 2.1 TARGET (fuente principal)

| Fuente | Uso |
|--------|-----|
| `game/data/stats/items/*.xml` (195 archivos) | name, crystal_type, armor_type, bodypart, weapon_type, pAtk, mAtk |
| `game/data/stats/armorsets/*.xml` (21 archivos) | Composicion autoritativa de sets (piezas + skill) |
| `game/data/stats/players/classList.xml` | classId de las clases objetivo |
| `game/data/stats/players/initialEquipment.xml` | Equipo inicial nativo (baseline No-Grade) |
| `game/config/Player.ini`, `game/config/General.ini` | ExpertisePenalty / SkillCheck (contexto) |

### 2.2 UPSTREAM (solo contraste)

| Fuente | Uso |
|--------|-----|
| `entity/itemcontainer/Inventory.java` (`equipItem`, ~1263-1400) | Reglas nativas de slot |
| `entity/item/ItemTemplate.java` (`checkCondition`, ~816-880) | Condiciones (hero/olympiad/raza) |

---

## 3. Verification Method

1. Se recorren TODOS los XML de `stats/items` y se extrae cada bloque `<item ...>` con sus `<set name="...">`.
2. Clasificacion por `crystal_type` (grade). Item sin `crystal_type` = **NONE** (No-Grade).
3. Se aplican filtros de ruido documentados y se registran los conteos exactos.
4. Los sets se leen de `stats/armorsets`, incluidos los bloques comentados (`<!-- -->`) marcados `DISABLED/RESERVED`.
5. Pertenencia a set resuelta por ID de pieza (incluye IDs alternativos 11xxx/12xxx/16xxx).
6. Reglas de slot contrastadas con `Inventory.equipItem` (UPSTREAM) y con el `bodypart` real de cada item.

### 3.1 Volumen medido

| Metrica | Valor |
|---------|-------|
| Items totales parseados | 19199 |
| `type="Armor"` (crudo, antes de filtros) | 3825 |
| `type="Weapon"` (crudo, antes de filtros) | 3893 |
| Armor equipable no-quest con slot relevante | 1067 |
| Jewelry (mismo criterio) | 261 |
| Armor equipable fuera de alcance (apariencia/accesorios/underwear) | 814 |
| Armas base (sin SA/evento/PvP/common/shadow/fortune/Test) | 917 |
| Sets declarados | 217 bloques (204 activos + 13 deshabilitados) |

### 3.2 Filtros de ruido (por nombre)

`*Sealed*` `*Fortune*` `*Event*` `*Shadow*` `*Monster*` `*Common*` `*Not In Use*` `*limited*`
`*Recommendation*` `*{PvP}*` `*Test*`; en armas ademas `* - *` (SA/elemental) y `*Foundation*`.

Ademas se excluyen los `Armor`/`Weapon` cuyo icono es `icon.item_*` y los marcados `is_questitem`: son
placeholders de quest/usables modelados como equipo (p.ej. 990 Mandragora Essence con `bodypart=gloves`).

### 3.3 Nota de codificacion

Generado en ASCII (sin acentos) de forma intencional: PowerShell 5.1 lee scripts `.ps1` sin BOM como ANSI;
ASCII evita mojibake en el catalogo.


## 4. No-Grade Equipment

### Armor

Pieces: 163 (slot-relevant armor for this grade, noise variants excluded).

| ID | Name | Grade | Armor type | Slot | Set membership |
|----|------|-------|------------|------|----------------|
| 35 | Cloth Shoes | NONE | (none) | feet | (not in a set) |
| 36 | Leather Sandals | NONE | (none) | feet | (not in a set) |
| 37 | Leather Shoes | NONE | (none) | feet | (not in a set) |
| 38 | Low Boots | NONE | (none) | feet | (not in a set) |
| 39 | Boots | NONE | (none) | feet | (not in a set) |
| 1121 | Apprentice's Shoes | NONE | (none) | feet | (not in a set) |
| 1122 | Cotton Shoes | NONE | (none) | feet | (not in a set) |
| 1129 | Crude Leather Shoes | NONE | (none) | feet | (not in a set) |
| 1323 | Leather Shoes | NONE | (none) | feet | (not in a set) |
| 1324 | Low Boots | NONE | (none) | feet | (not in a set) |
| 1325 | Leather Boots | NONE | (none) | feet | (not in a set) |
| 1326 | Iron Boots | NONE | (none) | feet | (not in a set) |
| 1327 | Boots | NONE | (none) | feet | (not in a set) |
| 4227 | Dream Boots | NONE | (none) | feet | (not in a set) |
| 4231 | Ubiquitous Boots | NONE | (none) | feet | (not in a set) |
| 5590 | Squeaking Shoes | NONE | (none) | feet | (not in a set) |
| 48 | Short Gloves | NONE | (none) | gloves | (not in a set) |
| 49 | Gloves | NONE | (none) | gloves | (not in a set) |
| 50 | Leather Gloves | NONE | (none) | gloves | (not in a set) |
| 51 | Bracer | NONE | (none) | gloves | (not in a set) |
| 992 | Shilen's 1st Mark | NONE | (none) | gloves | (not in a set) |
| 993 | Shilen's 2nd Mark | NONE | (none) | gloves | (not in a set) |
| 996 | Alex's Dagger | NONE | (none) | gloves | (not in a set) |
| 1119 | Short Leather Gloves | NONE | (none) | gloves | (not in a set) |
| 1318 | Gloves | NONE | (none) | gloves | (not in a set) |
| 1319 | Leather Gloves | NONE | (none) | gloves | (not in a set) |
| 1320 | Crafted Leather Gloves | NONE | (none) | gloves | (not in a set) |
| 1321 | Rip Gauntlets | NONE | (none) | gloves | (not in a set) |
| 1322 | Bracer | NONE | (none) | gloves | (not in a set) |
| 4226 | Dream Gloves | NONE | (none) | gloves | (not in a set) |
| 4230 | Ubiquitous Gloves | NONE | (none) | gloves | (not in a set) |
| 41 | Cloth Cap | NONE | (none) | head | (not in a set) |
| 42 | Leather Cap | NONE | (none) | head | (not in a set) |
| 43 | Wooden Helmet | NONE | (none) | head | no_grade.xml#1 |
| 44 | Leather Helmet | NONE | (none) | head | no_grade.xml#2 |
| 1148 | Hard Leather Helmet | NONE | (none) | head | (not in a set) |
| 9669 | Native Helmet | NONE | (none) | head | special.xml#81 |
| 13802 | Native's Hood | NONE | (none) | head | (not in a set) |
| 13805 | Guards of the Dawn Helmet | NONE | (none) | head | (not in a set) |
| 18 | Leather Shield | NONE | (none) | lhand | (not in a set) |
| 19 | Small Shield | NONE | (none) | lhand | (not in a set) |
| 20 | Buckler | NONE | (none) | lhand | (not in a set) |
| 102 | Round Shield | NONE | (none) | lhand | (not in a set) |
| 625 | Bone Shield | NONE | (none) | lhand | (not in a set) |
| 945 | Skeleton Buckler | NONE | (none) | lhand | (not in a set) |
| 1328 | Shield of Grace | NONE | (none) | lhand | (not in a set) |
| 1329 | Shield of Victory | NONE | (none) | lhand | (not in a set) |
| 1330 | Zubei's Shield | NONE | (none) | lhand | (not in a set) |
| 1331 | Otherworldly Shield | NONE | (none) | lhand | (not in a set) |
| 1332 | Knight's Shield | NONE | (none) | lhand | (not in a set) |
| 4222 | Dream Shield | NONE | (none) | lhand | (not in a set) |
| 4223 | Ubiquitous Shield | NONE | (none) | lhand | (not in a set) |
| 6902 | Pledge Shield | NONE | (none) | lhand | (not in a set) |
| 7015 | Shield of Castle Pledge | NONE | (none) | lhand | (not in a set) |
| 13525 | Gracian Soldier Shield | NONE | (none) | lhand | (not in a set) |
| 14791 | Baguette's Shield | NONE | (none) | lhand | (not in a set) |
| 25 | Piece Bone Breastplate | NONE | HEAVY | chest | (not in a set) |
| 26 | Bronze Breastplate | NONE | HEAVY | chest | (not in a set) |
| 1308 | Compound Scale Mail | NONE | HEAVY | chest | (not in a set) |
| 1309 | Mithril Breastplate | NONE | HEAVY | chest | (not in a set) |
| 4224 | Dream Armor | NONE | HEAVY | chest | (not in a set) |
| 4228 | Ubiquitous Armor | NONE | HEAVY | chest | (not in a set) |
| 32 | Piece Bone Gaiters | NONE | HEAVY | legs | (not in a set) |
| 34 | Bronze Gaiters | NONE | HEAVY | legs | (not in a set) |
| 1313 | Compound Scale Gaiters | NONE | HEAVY | legs | (not in a set) |
| 1314 | Mithril Gaiters | NONE | HEAVY | legs | (not in a set) |
| 4225 | Dream Stockings | NONE | HEAVY | legs | (not in a set) |
| 4229 | Ubiquitous Stockings | NONE | HEAVY | legs | (not in a set) |
| 21 | Shirt | NONE | LIGHT | chest | (not in a set) |
| 22 | Leather Shirt | NONE | LIGHT | chest | (not in a set) |
| 23 | Wooden Breastplate | NONE | LIGHT | chest | no_grade.xml#1 |
| 24 | Bone Breastplate | NONE | LIGHT | chest | (not in a set) |
| 27 | Hard Leather Shirt | NONE | LIGHT | chest | (not in a set) |
| 390 | Cotton Shirt | NONE | LIGHT | chest | (not in a set) |
| 485 | Tattoo of Power | NONE | LIGHT | chest | (not in a set) |
| 1146 | Squire's Shirt | NONE | LIGHT | chest | (not in a set) |
| 1311 | Puma Skin Shirt | NONE | LIGHT | chest | (not in a set) |
| 2506 | Wolf's Leather Armor | NONE | LIGHT | chest | (not in a set) |
| 3891 | Wolf's Hide Armor | NONE | LIGHT | chest | (not in a set) |
| 3892 | Wolf's Hard Leather Armor | NONE | LIGHT | chest | (not in a set) |
| 3893 | Wolf's Wooden Armor | NONE | LIGHT | chest | (not in a set) |
| 3894 | Wolf's Ring Mail | NONE | LIGHT | chest | (not in a set) |
| 3895 | Wolf's Bone Armor | NONE | LIGHT | chest | (not in a set) |
| 3896 | Wolf's Scale Mail | NONE | LIGHT | chest | (not in a set) |
| 3897 | Wolf's Bronze Armor | NONE | LIGHT | chest | (not in a set) |
| 3898 | Wolf's Plate Mail | NONE | LIGHT | chest | (not in a set) |
| 3899 | Wolf's Steel Armor | NONE | LIGHT | chest | (not in a set) |
| 3900 | Wolf's Luxury Plate | NONE | LIGHT | chest | (not in a set) |
| 3901 | Wolf's Mithril Armor | NONE | LIGHT | chest | (not in a set) |
| 3912 | Hatchling's Soft Leather | NONE | LIGHT | chest | (not in a set) |
| 3913 | Hatchling's Scale Mail | NONE | LIGHT | chest | (not in a set) |
| 3914 | Hatchling's Brigandine | NONE | LIGHT | chest | (not in a set) |
| 3915 | Hatchling's Bronze Coat | NONE | LIGHT | chest | (not in a set) |
| 3916 | Hatchling's Steel Coat | NONE | LIGHT | chest | (not in a set) |
| 3918 | Hatchling's Mithril Coat | NONE | LIGHT | chest | (not in a set) |
| 4234 | Hatchling's Level 65 Armor | NONE | LIGHT | chest | (not in a set) |
| 4235 | Hatchling's Level 75 Armor | NONE | LIGHT | chest | (not in a set) |
| 4236 | Gara Item | NONE | LIGHT | chest | (not in a set) |
| 5170 | Mithril Panzer Coat | NONE | LIGHT | chest | (not in a set) |
| 5171 | Brigadine Panzer Coat | NONE | LIGHT | chest | (not in a set) |
| 5172 | Draconic Panzer Coat | NONE | LIGHT | chest | (not in a set) |
| 5173 | Blood Panzer Coat | NONE | LIGHT | chest | (not in a set) |
| 5174 | Ophidian Panzer Coat | NONE | LIGHT | chest | (not in a set) |
| 5175 | Inferno Panzer Coat | NONE | LIGHT | chest | (not in a set) |
| 5182 | Hatchling's Gorgon Coat | NONE | LIGHT | chest | (not in a set) |
| 5183 | Hatchling's Ophidian Plate | NONE | LIGHT | chest | (not in a set) |
| 5184 | Hatchling's Crimson Plate | NONE | LIGHT | chest | (not in a set) |
| 5185 | Hatchling's Draconic Plate | NONE | LIGHT | chest | (not in a set) |
| 5186 | Hatchling's Inferno Plate | NONE | LIGHT | chest | (not in a set) |
| 5216 | Wolf's Level 75 Armor | NONE | LIGHT | chest | (not in a set) |
| 8541 | Little Harness | NONE | LIGHT | chest | (not in a set) |
| 9662 | Great Wolf Scale Armor | NONE | LIGHT | chest | (not in a set) |
| 9663 | Great Wolf Bronze Armor | NONE | LIGHT | chest | (not in a set) |
| 9664 | Great Wolf Plate Armor | NONE | LIGHT | chest | (not in a set) |
| 9665 | Great Wolf Mithril Armor | NONE | LIGHT | chest | (not in a set) |
| 9666 | Great Wolf Oriharukon Armor | NONE | LIGHT | chest | (not in a set) |
| 9667 | Great Wolf Orichalcum Armor | NONE | LIGHT | chest | (not in a set) |
| 9670 | Native Tunic | NONE | LIGHT | chest | special.xml#81 |
| 11482 | Great Wolf Oriharukon Armor | NONE | LIGHT | chest | (not in a set) |
| 11511 | Great Wolf Orichalcum Armor | NONE | LIGHT | chest | (not in a set) |
| 12740 | Baby Pet Scale Armor | NONE | LIGHT | chest | (not in a set) |
| 12741 | Baby Pet Bronze Armor | NONE | LIGHT | chest | (not in a set) |
| 12742 | Baby Pet Plate Armor | NONE | LIGHT | chest | (not in a set) |
| 12743 | Baby Pet Mithril Armor | NONE | LIGHT | chest | (not in a set) |
| 12744 | Baby Pet Oriharukon Armor | NONE | LIGHT | chest | (not in a set) |
| 12745 | Baby Pet Orichalcum Armor | NONE | LIGHT | chest | (not in a set) |
| 13050 | Tigress exclusive armor | NONE | LIGHT | chest | (not in a set) |
| 13803 | Native's Tunic | NONE | LIGHT | chest | (not in a set) |
| 13806 | Guards of the Dawn Tunic | NONE | LIGHT | chest | (not in a set) |
| 28 | Pants | NONE | LIGHT | legs | (not in a set) |
| 29 | Leather Pants | NONE | LIGHT | legs | (not in a set) |
| 30 | Hard Leather Pants | NONE | LIGHT | legs | (not in a set) |
| 31 | Bone Gaiters | NONE | LIGHT | legs | (not in a set) |
| 33 | Hard Leather Gaiters | NONE | LIGHT | legs | (not in a set) |
| 412 | Cotton Pants | NONE | LIGHT | legs | (not in a set) |
| 1147 | Squire's Pants | NONE | LIGHT | legs | (not in a set) |
| 1316 | Puma Skin Gaiters | NONE | LIGHT | legs | (not in a set) |
| 2386 | Wooden Gaiters | NONE | LIGHT | legs | no_grade.xml#1 |
| 9671 | Native Pants | NONE | LIGHT | legs | special.xml#81 |
| 13804 | Native's Trousers | NONE | LIGHT | legs | (not in a set) |
| 13807 | Guards of the Dawn Trousers | NONE | LIGHT | legs | (not in a set) |
| 425 | Apprentice's Tunic | NONE | MAGIC | chest | (not in a set) |
| 426 | Tunic | NONE | MAGIC | chest | (not in a set) |
| 428 | Feriotic Tunic | NONE | MAGIC | chest | (not in a set) |
| 429 | Leather Tunic | NONE | MAGIC | chest | (not in a set) |
| 1100 | Cotton Tunic | NONE | MAGIC | chest | (not in a set) |
| 1101 | Tunic of Devotion | NONE | MAGIC | chest | no_grade.xml#2 |
| 1102 | Tunic of Magic | NONE | MAGIC | chest | (not in a set) |
| 1310 | Tunic of Magic | NONE | MAGIC | chest | (not in a set) |
| 1312 | White Tunic | NONE | MAGIC | chest | (not in a set) |
| 461 | Apprentice's Stockings | NONE | MAGIC | legs | (not in a set) |
| 462 | Stockings | NONE | MAGIC | legs | (not in a set) |
| 463 | Feriotic Stockings | NONE | MAGIC | legs | (not in a set) |
| 464 | Leather Stockings | NONE | MAGIC | legs | (not in a set) |
| 1103 | Cotton Stockings | NONE | MAGIC | legs | (not in a set) |
| 1104 | Stockings of Devotion | NONE | MAGIC | legs | no_grade.xml#2 |
| 1105 | Stockings of Magic | NONE | MAGIC | legs | (not in a set) |
| 1315 | Stockings of Magic | NONE | MAGIC | legs | (not in a set) |
| 1317 | Dark Stockings | NONE | MAGIC | legs | (not in a set) |
| 427 | Cotton Robe | NONE | MAGIC | onepiece | (not in a set) |
| 430 | Robe of Devotion | NONE | MAGIC | onepiece | (not in a set) |
| 431 | Robe of Magic | NONE | MAGIC | onepiece | (not in a set) |
| 14796 | Baguette's Sigil | NONE | SIGIL | lhand | (not in a set) |

### Weapons

Base weapons: 249 (SA/elemental, event, PvP, common, shadow and fortune variants excluded).

| ID | Name | Grade | Weapon type | Slot | Class | pAtk | mAtk |
|----|------|-------|-------------|------|-------|------|------|
| 14634 | Santa Claus' Durendal | NONE | ANCIENTSWORD | lrhand | 2H (lrhand) | 65 | 38 |
| 14793 | Baguette's Ancient Sword | NONE | ANCIENTSWORD | lrhand | 2H (lrhand) | 0 | 0 |
| 14631 | Santa Claus' Daimon Crystal | NONE | BLUNT | lrhand | 2H (lrhand) | 62 | 56 |
| 14624 | Santa Claus' Behemoth Tuning Fork | NONE | BLUNT | lrhand | 2H (lrhand) | 62 | 38 |
| 14623 | Santa Claus' Barakiel Axe | NONE | BLUNT | rhand | 1H (rhand) | 51 | 38 |
| 14630 | Santa Claus' Hand of Cabrio | NONE | BLUNT | rhand | 1H (rhand) | 50 | 51 |
| 155 | Flanged Mace | NONE | BLUNT | rhand | 1H (rhand) | 31 | 21 |
| 4221 | Ubiquitous Axe | NONE | BLUNT | rhand | 1H (rhand) | 31 | 21 |
| 9903 | Improved Iron Hammer | NONE | BLUNT | rhand | 1H (rhand) | 31 | 21 |
| 87 | Iron Hammer | NONE | BLUNT | rhand | 1H (rhand) | 31 | 21 |
| 9907 | Improved Flanged Mace | NONE | BLUNT | rhand | 1H (rhand) | 31 | 21 |
| 177 | Mage Staff | NONE | BLUNT | lrhand | 2H (lrhand) | 30 | 31 |
| 9908 | Improved Mage Staff | NONE | BLUNT | lrhand | 2H (lrhand) | 30 | 31 |
| 7817 | Apprentice Adventurer's Bone Club | NONE | BLUNT | rhand | 1H (rhand) | 24 | 17 |
| 2501 | Bone Club | NONE | BLUNT | rhand | 1H (rhand) | 24 | 17 |
| 176 | Apprentice's Staff | NONE | BLUNT | lrhand | 2H (lrhand) | 23 | 24 |
| 7816 | Apprentice Adventurer's Staff | NONE | BLUNT | lrhand | 2H (lrhand) | 23 | 24 |
| 1301 | Big Hammer | NONE | BLUNT | rhand | 1H (rhand) | 22 | 6 |
| 1300 | Apprentice's Rod | NONE | BLUNT | rhand | 1H (rhand) | 22 | 6 |
| 154 | Dwarven Mace | NONE | BLUNT | rhand | 1H (rhand) | 17 | 12 |
| 9 | Cedar Staff | NONE | BLUNT | lrhand | 2H (lrhand) | 16 | 18 |
| 1304 | Conjuror's Staff | NONE | BLUNT | lrhand | 2H (lrhand) | 15 | 17 |
| 744 | Staff of Sentinel | NONE | BLUNT | lrhand | 2H (lrhand) | 13 | 15 |
| 754 | Red Sunset Staff | NONE | BLUNT | lrhand | 2H (lrhand) | 13 | 14 |
| 1511 | Silversmith Hammer | NONE | BLUNT | rhand | 1H (rhand) | 13 | 10 |
| 2373 | Eldritch Staff | NONE | BLUNT | lrhand | 2H (lrhand) | 13 | 13 |
| 153 | Sigil | NONE | BLUNT | rhand | 1H (rhand) | 12 | 9 |
| 13539 | Staff of Master Yogi | NONE | BLUNT | lrhand | 2H (lrhand) | 11 | 12 |
| 8 | Willow Staff | NONE | BLUNT | lrhand | 2H (lrhand) | 11 | 13 |
| 747 | Wand of Adept | NONE | BLUNT | rhand | 1H (rhand) | 11 | 13 |
| 5 | Mace | NONE | BLUNT | rhand | 1H (rhand) | 11 | 9 |
| 152 | Heavy Chisel | NONE | BLUNT | rhand | 1H (rhand) | 10 | 8 |
| 748 | Gallint's Oak Wand | NONE | BLUNT | lrhand | 2H (lrhand) | 10 | 11 |
| 4 | Club | NONE | BLUNT | rhand | 1H (rhand) | 8 | 6 |
| 7 | Apprentice's Rod | NONE | BLUNT | rhand | 1H (rhand) | 6 | 8 |
| 2370 | Guild Member's Club | NONE | BLUNT | rhand | 1H (rhand) | 6 | 5 |
| 6 | Apprentice's Wand | NONE | BLUNT | rhand | 1H (rhand) | 5 | 7 |
| 7058 | Chrono Darbuka | NONE | BLUNT | lrhand | 2H (lrhand) | 1 | 1 |
| 14783 | Baguette's Mace | NONE | BLUNT | rhand | 1H (rhand) | 0 | 0 |
| 14790 | Baguette's Two-handed Staff | NONE | BLUNT | lrhand | 2H (lrhand) | 0 | 0 |
| 14789 | Baguette's Staff | NONE | BLUNT | rhand | 1H (rhand) | 0 | 0 |
| 1307 | Bow | NONE | BOW | lrhand | 2H (lrhand) | 120 | 6 |
| 14627 | Santa Claus' Shyeed Bow | NONE | BOW | lrhand | 2H (lrhand) | 116 | 42 |
| 273 | Composite Bow | NONE | BOW | lrhand | 2H (lrhand) | 64 | 21 |
| 9906 | Improved Composite Bow | NONE | BOW | lrhand | 2H (lrhand) | 64 | 21 |
| 1213 | Guard's Bow | NONE | BOW | lrhand | 2H (lrhand) | 49 | 17 |
| 7820 | Apprentice Adventurer's Bow | NONE | BOW | lrhand | 2H (lrhand) | 49 | 17 |
| 272 | Forest Bow | NONE | BOW | lrhand | 2H (lrhand) | 49 | 17 |
| 1181 | Neti's Bow | NONE | BOW | lrhand | 2H (lrhand) | 45 | 16 |
| 271 | Hunting Bow | NONE | BOW | lrhand | 2H (lrhand) | 34 | 12 |
| 3028 | Crescent Moon Bow | NONE | BOW | lrhand | 2H (lrhand) | 34 | 12 |
| 10212 | For NPC (Bow) | NONE | BOW | rhand | 1H (rhand) | 31 | 21 |
| 14 | Bow | NONE | BOW | lrhand | 2H (lrhand) | 23 | 9 |
| 13 | Short Bow | NONE | BOW | lrhand | 2H (lrhand) | 16 | 6 |
| 9141 | Redemption Bow | NONE | BOW | lrhand | 2H (lrhand) | 1 | 1 |
| 9140 | Salvation Bow | NONE | BOW | lrhand | 2H (lrhand) | 1 | 1 |
| 14786 | Baguette's Bow | NONE | BOW | lrhand | 2H (lrhand) | 0 | 0 |
| 14633 | Santa Claus' Screaming Vengeance | NONE | CROSSBOW | lrhand | 2H (lrhand) | 56 | 38 |
| 14794 | Baguette's Crossbow | NONE | CROSSBOW | lrhand | 2H (lrhand) | 0 | 0 |
| 1305 | Knife | NONE | DAGGER | rhand | 1H (rhand) | 30000 | 6 |
| 14625 | Santa Claus' Naga Storm | NONE | DAGGER | rhand | 1H (rhand) | 45 | 38 |
| 219 | Sword Breaker | NONE | DAGGER | rhand | 1H (rhand) | 27 | 21 |
| 9904 | Improved Sword Breaker | NONE | DAGGER | rhand | 1H (rhand) | 27 | 21 |
| 4220 | Dream Knife | NONE | DAGGER | rhand | 1H (rhand) | 27 | 21 |
| 1306 | Crafted Dagger | NONE | DAGGER | rhand | 1H (rhand) | 22 | 6 |
| 218 | Throwing Knife | NONE | DAGGER | rhand | 1H (rhand) | 21 | 17 |
| 217 | Shining Knife | NONE | DAGGER | rhand | 1H (rhand) | 21 | 17 |
| 7818 | Apprentice Adventurer's Knife | NONE | DAGGER | rhand | 1H (rhand) | 21 | 17 |
| 1182 | Neti's Dagger | NONE | DAGGER | rhand | 1H (rhand) | 19 | 16 |
| 946 | Skeleton Dagger | NONE | DAGGER | rhand | 1H (rhand) | 16 | 13 |
| 216 | Dirk | NONE | DAGGER | rhand | 1H (rhand) | 15 | 12 |
| 2372 | Dagger of Adept | NONE | DAGGER | rhand | 1H (rhand) | 11 | 10 |
| 2374 | Red Sunset Dagger | NONE | DAGGER | rhand | 1H (rhand) | 11 | 10 |
| 989 | Eldritch Dagger | NONE | DAGGER | rhand | 1H (rhand) | 11 | 10 |
| 12 | Knife | NONE | DAGGER | rhand | 1H (rhand) | 10 | 9 |
| 215 | Doom Dagger | NONE | DAGGER | rhand | 1H (rhand) | 10 | 9 |
| 3471 | Cybellin's Dagger | NONE | DAGGER | rhand | 1H (rhand) | 10 | 9 |
| 4665 | Pipette Knife | NONE | DAGGER | rhand | 1H (rhand) | 10 | 9 |
| 11 | Bone Dagger | NONE | DAGGER | rhand | 1H (rhand) | 7 | 6 |
| 10 | Dagger | NONE | DAGGER | rhand | 1H (rhand) | 5 | 5 |
| 14781 | Baguette's Dagger | NONE | DAGGER | rhand | 1H (rhand) | 0 | 0 |
| 1299 | Great Sword | NONE | DUAL | lrhand | dual (lrhand) | 200 | 6 |
| 14674 | Santa Claus' Tallum Blade*Damascus | NONE | DUAL | lrhand | dual (lrhand) | 61 | 51 |
| 8350 | Chrono Maracas | NONE | DUAL | lrhand | dual (lrhand) | 1 | 1 |
| 14795 | Baguette's Dualsword | NONE | DUAL | lrhand | dual (lrhand) | 0 | 0 |
| 14797 | Baguette's Two-handed Dagger | NONE | DUALDAGGER | lrhand | dual dagger (lrhand) | 0 | 0 |
| 14628 | Santa Claus' Sobekk Hurricane | NONE | DUALFIST | lrhand | dual fist (lrhand) | 41 | 38 |
| 9905 | Improved Viper Fang | NONE | DUALFIST | lrhand | dual fist (lrhand) | 38 | 21 |
| 257 | Viper Fang | NONE | DUALFIST | lrhand | dual fist (lrhand) | 38 | 21 |
| 7819 | Apprentice Adventurer's Cestus | NONE | DUALFIST | lrhand | dual fist (lrhand) | 29 | 17 |
| 256 | Cestus | NONE | DUALFIST | lrhand | dual fist (lrhand) | 29 | 17 |
| 255 | Fox Claw Gloves | NONE | DUALFIST | lrhand | dual fist (lrhand) | 21 | 12 |
| 2371 | Fist of Butcher | NONE | DUALFIST | lrhand | dual fist (lrhand) | 16 | 10 |
| 254 | Iron Gloves | NONE | DUALFIST | lrhand | dual fist (lrhand) | 13 | 9 |
| 253 | Spiked Gloves | NONE | DUALFIST | lrhand | dual fist (lrhand) | 10 | 6 |
| 2368 | Training Gloves | NONE | DUALFIST | lrhand | dual fist (lrhand) | 7 | 5 |
| 21990 | Warm Bear Paws | NONE | DUALFIST | lrhand | dual fist (lrhand) | 5 | 5 |
| 21110 | Warm Bear Paws | NONE | DUALFIST | lrhand | dual fist (lrhand) | 5 | 5 |
| 5133 | Chrono Unitus | NONE | DUALFIST | lrhand | dual fist (lrhand) | 1 | 1 |
| 14787 | Baguette's Fist | NONE | DUALFIST | lrhand | dual fist (lrhand) | 0 | 0 |
| 311 | Crucifix of Blessing | NONE | ETC | rhand | 1H (rhand) | 25 | 28 |
| 100 | Voodoo Doll | NONE | ETC | rhand | 1H (rhand) | 25 | 28 |
| 310 | Relic of the Saints | NONE | ETC | rhand | 1H (rhand) | 19 | 22 |
| 309 | Tears of Eva | NONE | ETC | rhand | 1H (rhand) | 19 | 22 |
| 99 | Apprentice's Spellbook | NONE | ETC | rhand | 1H (rhand) | 9 | 12 |
| 308 | Buffalo's Horn | NONE | ETC | rhand | 1H (rhand) | 6 | 8 |
| 21163 | Wedding Bouquet | NONE | ETC | rhand | 1H (rhand) | 1 | 1 |
| 6529 | Baby Duck Rod | NONE | FISHINGROD | lrhand | 2H (lrhand) | 1 | 1 |
| 8190 | Demonic Sword Zariche | NONE | FIST | lrhand | 2H (lrhand) | 361 | 137 |
| 8689 | Blood Sword Akamanah | NONE | FIST | lrhand | 2H (lrhand) | 361 | 137 |
| 250 | Dark Mystic Fist | NONE | FIST | rhand | 1H (rhand) | 0 | 0 |
| 251 | Human Mystic Fist | NONE | FIST | rhand | 1H (rhand) | 0 | 0 |
| 252 | Orc Shaman Fist | NONE | FIST | rhand | 1H (rhand) | 0 | 0 |
| 244 | Elven Fighter Fist | NONE | FIST | rhand | 1H (rhand) | 0 | 0 |
| 247 | Dwarven Fighter Fist | NONE | FIST | rhand | 1H (rhand) | 0 | 0 |
| 246 | Human Fighter Fist | NONE | FIST | rhand | 1H (rhand) | 0 | 0 |
| 248 | Orc Fighter Fist | NONE | FIST | rhand | 1H (rhand) | 0 | 0 |
| 249 | Elven Mystic Fist | NONE | FIST | rhand | 1H (rhand) | 0 | 0 |
| 245 | Dark Fighter Fist | NONE | FIST | rhand | 1H (rhand) | 0 | 0 |
| 13535 | Flag of Innadril | NONE | FLAG | lrhand | 2H (lrhand) | 0 | 0 |
| 13534 | Flag of Aden | NONE | FLAG | lrhand | 2H (lrhand) | 0 | 0 |
| 13532 | Flag of Giran | NONE | FLAG | lrhand | 2H (lrhand) | 0 | 0 |
| 13536 | Goddard Flag | NONE | FLAG | lrhand | 2H (lrhand) | 0 | 0 |
| 13537 | Flag of Rune | NONE | FLAG | lrhand | 2H (lrhand) | 0 | 0 |
| 13538 | Flag of Schuttgart | NONE | FLAG | lrhand | 2H (lrhand) | 0 | 0 |
| 13533 | Flag of Oren | NONE | FLAG | lrhand | 2H (lrhand) | 0 | 0 |
| 9819 | Combat flag | NONE | FLAG | lrhand | 2H (lrhand) | 0 | 0 |
| 13530 | Flag of Gludio | NONE | FLAG | lrhand | 2H (lrhand) | 0 | 0 |
| 13531 | Flag of Dion | NONE | FLAG | lrhand | 2H (lrhand) | 0 | 0 |
| 13568 | Schuttgart Ward | NONE | OWNTHING | lrhand | 2H (lrhand) | 0 | 0 |
| 13567 | Rune Ward | NONE | OWNTHING | lrhand | 2H (lrhand) | 0 | 0 |
| 13564 | Aden Ward | NONE | OWNTHING | lrhand | 2H (lrhand) | 0 | 0 |
| 13565 | Innadril Ward | NONE | OWNTHING | lrhand | 2H (lrhand) | 0 | 0 |
| 13561 | Dion Ward | NONE | OWNTHING | lrhand | 2H (lrhand) | 0 | 0 |
| 13562 | Giran Ward | NONE | OWNTHING | lrhand | 2H (lrhand) | 0 | 0 |
| 13563 | Oren Ward | NONE | OWNTHING | lrhand | 2H (lrhand) | 0 | 0 |
| 13560 | Gludio Ward | NONE | OWNTHING | lrhand | 2H (lrhand) | 0 | 0 |
| 13566 | Goddard Ward | NONE | OWNTHING | lrhand | 2H (lrhand) | 0 | 0 |
| 1303 | Lance | NONE | POLE | lrhand | 2H (lrhand) | 30000 | 6 |
| 14626 | Santa Claus' Tiphon Spear | NONE | POLE | lrhand | 2H (lrhand) | 51 | 38 |
| 9902 | Improved Great Spear | NONE | POLE | lrhand | 2H (lrhand) | 31 | 21 |
| 16 | Great Spear | NONE | POLE | lrhand | 2H (lrhand) | 31 | 21 |
| 15 | Short Spear | NONE | POLE | lrhand | 2H (lrhand) | 24 | 17 |
| 3026 | Talins Spear | NONE | POLE | lrhand | 2H (lrhand) | 24 | 17 |
| 1302 | Bec de Corbin | NONE | POLE | lrhand | 2H (lrhand) | 22 | 6 |
| 21754 | Server Ziggi's Magic Pencil | NONE | POLE | lrhand | 2H (lrhand) | 1 | 1 |
| 5817 | Chrono Campana | NONE | POLE | lrhand | 2H (lrhand) | 1 | 1 |
| 14785 | Baguette's Spear | NONE | POLE | lrhand | 2H (lrhand) | 0 | 0 |
| 14784 | Baguette's Two-handed Hammer | NONE | POLE | lrhand | 2H (lrhand) | 0 | 0 |
| 14632 | Santa Claus' Éclair Bijou | NONE | RAPIER | rhand | 1H (rhand) | 46 | 38 |
| 9720 | Warrior's Sword | NONE | RAPIER | rhand | 1H (rhand) | 12 | 10 |
| 14792 | Baguette's Rapier | NONE | RAPIER | rhand | 1H (rhand) | 0 | 0 |
| 9661 | Enchanted Fenrir Fang | NONE | SWORD | rhand | 1H (rhand) | 537 | 5 |
| 9660 | Orichalcum Fang | NONE | SWORD | rhand | 1H (rhand) | 477 | 5 |
| 11132 | Enchanted Cerberus Fang | NONE | SWORD | rhand | 1H (rhand) | 413 | 5 |
| 9659 | Enchanted Cerberus Fang | NONE | SWORD | rhand | 1H (rhand) | 413 | 5 |
| 9658 | Enchanted Saltydog Fang | NONE | SWORD | rhand | 1H (rhand) | 350 | 5 |
| 9657 | Enchanted Coyote Fang | NONE | SWORD | rhand | 1H (rhand) | 289 | 5 |
| 9656 | Enchanted Wolf Fang | NONE | SWORD | rhand | 1H (rhand) | 233 | 5 |
| 5217 | Wolf's Level 75 Weapon | NONE | SWORD | rhand | 1H (rhand) | 194 | 136 |
| 3911 | Fang of Fenrir | NONE | SWORD | rhand | 1H (rhand) | 93 | 66 |
| 3910 | Fang of the Blue Wolf | NONE | SWORD | rhand | 1H (rhand) | 80 | 58 |
| 7903 | Frintezza's Sword | NONE | SWORD | lrhand | 2H (lrhand) | 78 | 39 |
| 3909 | Crystallized Ice Canine | NONE | SWORD | rhand | 1H (rhand) | 69 | 50 |
| 14622 | Santa Claus' Sword of Ipos | NONE | SWORD | lrhand | 2H (lrhand) | 62 | 38 |
| 3908 | Fang of Coyote | NONE | SWORD | rhand | 1H (rhand) | 58 | 42 |
| 14621 | Santa Claus' Sirra Blade | NONE | SWORD | rhand | 1H (rhand) | 51 | 38 |
| 3907 | Fang of Cerberus | NONE | SWORD | rhand | 1H (rhand) | 49 | 36 |
| 14629 | Santa Claus' Themis Tongue | NONE | SWORD | rhand | 1H (rhand) | 41 | 51 |
| 3906 | Fang of Saltydog | NONE | SWORD | rhand | 1H (rhand) | 40 | 30 |
| 5284 | Zweihander | NONE | SWORD | lrhand | 2H (lrhand) | 38 | 21 |
| 4238 | Hatchling's Level 75 Weapon | NONE | SWORD | rhand | 1H (rhand) | 31 | 43 |
| 4219 | Dream Sword | NONE | SWORD | rhand | 1H (rhand) | 31 | 21 |
| 3905 | Orikarukon Canine | NONE | SWORD | rhand | 1H (rhand) | 31 | 24 |
| 68 | Falchion | NONE | SWORD | rhand | 1H (rhand) | 31 | 21 |
| 9644 | For NPC (Crossbow) | NONE | SWORD | rhand | 1H (rhand) | 31 | 21 |
| 9645 | For NPC (Sword) | NONE | SWORD | rhand | 1H (rhand) | 31 | 21 |
| 9646 | For NPC (Rapier) | NONE | SWORD | rhand | 1H (rhand) | 31 | 21 |
| 9901 | Improved Falchion | NONE | SWORD | rhand | 1H (rhand) | 31 | 21 |
| 3027 | Old Knight Sword | NONE | SWORD | lrhand | 2H (lrhand) | 29 | 17 |
| 5181 | Diamond Drill | NONE | SWORD | rhand | 1H (rhand) | 27 | 37 |
| 5191 | Diabolic Grinder | NONE | SWORD | rhand | 1H (rhand) | 27 | 37 |
| 5190 | Draconic Chopper | NONE | SWORD | rhand | 1H (rhand) | 25 | 34 |
| 5180 | Ohpdian Lance | NONE | SWORD | rhand | 1H (rhand) | 25 | 34 |
| 2915 | Old Knight Sword | NONE | SWORD | rhand | 1H (rhand) | 24 | 17 |
| 13524 | Gracian Soldier One-handed Sword | NONE | SWORD | rhand | 1H (rhand) | 24 | 17 |
| 4237 | Hatchling's Level 65 Weapon | NONE | SWORD | rhand | 1H (rhand) | 24 | 32 |
| 13755 | Olympiad Warrior's Weapon (undetermined) | NONE | SWORD | rhand | 1H (rhand) | 24 | 17 |
| 7821 | Apprentice Adventurer's Long Sword | NONE | SWORD | rhand | 1H (rhand) | 24 | 17 |
| 120 | Sword of Reflection | NONE | SWORD | rhand | 1H (rhand) | 24 | 17 |
| 2 | Long Sword | NONE | SWORD | rhand | 1H (rhand) | 24 | 17 |
| 3904 | Sylvan Canine | NONE | SWORD | rhand | 1H (rhand) | 23 | 18 |
| 5179 | Draconic Slicer | NONE | SWORD | rhand | 1H (rhand) | 23 | 30 |
| 5189 | Crimson Blood Fang | NONE | SWORD | rhand | 1H (rhand) | 23 | 30 |
| 1297 | Bastard Sword | NONE | SWORD | rhand | 1H (rhand) | 22 | 6 |
| 1298 | Caliburs | NONE | SWORD | rhand | 1H (rhand) | 22 | 6 |
| 1296 | Gladius | NONE | SWORD | rhand | 1H (rhand) | 22 | 6 |
| 1295 | Long Sword | NONE | SWORD | rhand | 1H (rhand) | 22 | 6 |
| 5178 | Assault Alicorn | NONE | SWORD | rhand | 1H (rhand) | 21 | 27 |
| 5188 | Fang of Dahak | NONE | SWORD | rhand | 1H (rhand) | 21 | 27 |
| 1333 | Brandish | NONE | SWORD | lrhand | 2H (lrhand) | 21 | 12 |
| 5187 | Serpentine Grinder | NONE | SWORD | rhand | 1H (rhand) | 19 | 24 |
| 5177 | Drake Horn | NONE | SWORD | rhand | 1H (rhand) | 19 | 24 |
| 1142 | Rusted Bronze Sword | NONE | SWORD | rhand | 1H (rhand) | 18 | 21 |
| 122 | Handmade Sword | NONE | SWORD | rhand | 1H (rhand) | 17 | 12 |
| 3925 | Antiplague | NONE | SWORD | rhand | 1H (rhand) | 17 | 21 |
| 3029 | Sword of Binding | NONE | SWORD | rhand | 1H (rhand) | 17 | 12 |
| 67 | Orcish Sword | NONE | SWORD | rhand | 1H (rhand) | 17 | 12 |
| 66 | Gladius | NONE | SWORD | rhand | 1H (rhand) | 17 | 12 |
| 5176 | Serpentine Spike | NONE | SWORD | rhand | 1H (rhand) | 17 | 21 |
| 3903 | Mithril Canine | NONE | SWORD | rhand | 1H (rhand) | 16 | 14 |
| 981 | Red Sunset Sword | NONE | SWORD | lrhand | 2H (lrhand) | 16 | 10 |
| 3924 | Unuk Alhay Fang | NONE | SWORD | rhand | 1H (rhand) | 15 | 18 |
| 743 | Sword of Sentinel | NONE | SWORD | rhand | 1H (rhand) | 14 | 11 |
| 975 | Blood Saber | NONE | SWORD | rhand | 1H (rhand) | 14 | 11 |
| 1510 | Butcher's Sword | NONE | SWORD | rhand | 1H (rhand) | 13 | 10 |
| 3923 | Torturer | NONE | SWORD | rhand | 1H (rhand) | 13 | 16 |
| 738 | Sword of Solidarity | NONE | SWORD | rhand | 1H (rhand) | 12 | 9 |
| 3922 | Alya Fang | NONE | SWORD | rhand | 1H (rhand) | 12 | 13 |
| 3 | Broadsword | NONE | SWORD | rhand | 1H (rhand) | 11 | 9 |
| 3902 | Ghost Canine | NONE | SWORD | rhand | 1H (rhand) | 11 | 10 |
| 3920 | Viperbite | NONE | SWORD | rhand | 1H (rhand) | 9 | 10 |
| 1 | Short Sword | NONE | SWORD | rhand | 1H (rhand) | 8 | 6 |
| 4027 | Bouquet | NONE | SWORD | rhand | 1H (rhand) | 8 | 6 |
| 3919 | Serpent Fang | NONE | SWORD | rhand | 1H (rhand) | 8 | 7 |
| 3439 | Shining Canine | NONE | SWORD | rhand | 1H (rhand) | 7 | 8 |
| 2369 | Squire's Sword | NONE | SWORD | rhand | 1H (rhand) | 6 | 5 |
| 2505 | Iron Canine | NONE | SWORD | rhand | 1H (rhand) | 4 | 5 |
| 12803 | Congratulatory Flowerpot A | NONE | SWORD | rhand | 1H (rhand) | 1 | 1 |
| 12802 | Condolence Flowerpot B | NONE | SWORD | rhand | 1H (rhand) | 1 | 1 |
| 13558 | Airship Cannon Briquet | NONE | SWORD | rhand | 1H (rhand) | 1 | 1 |
| 4202 | Chrono Cithara | NONE | SWORD | rhand | 1H (rhand) | 1 | 1 |
| 21371 | Aqua Elf Transformation Harp | NONE | SWORD | rhand | 1H (rhand) | 1 | 1 |
| 10167 | Pig Lollipop | NONE | SWORD | rhand | 1H (rhand) | 1 | 1 |
| 12798 | Snowman Transformation Stick | NONE | SWORD | rhand | 1H (rhand) | 1 | 1 |
| 12801 | Condolence Flowerpot A | NONE | SWORD | rhand | 1H (rhand) | 1 | 1 |
| 12799 | Scarecrow Transformation Stick | NONE | SWORD | rhand | 1H (rhand) | 1 | 1 |
| 12808 | Fruit Basket | NONE | SWORD | rhand | 1H (rhand) | 1 | 1 |
| 13556 | Airship Helm | NONE | SWORD | rhand | 1H (rhand) | 1 | 1 |
| 12809 | Arranged Clams | NONE | SWORD | rhand | 1H (rhand) | 1 | 1 |
| 13557 | Airship Cannon | NONE | SWORD | rhand | 1H (rhand) | 1 | 1 |
| 12806 | Bomb | NONE | SWORD | rhand | 1H (rhand) | 1 | 1 |
| 12807 | Direction Board | NONE | SWORD | rhand | 1H (rhand) | 1 | 1 |
| 12810 | Halloween Pumpkin | NONE | SWORD | rhand | 1H (rhand) | 1 | 1 |
| 12804 | Congratulatory Flowerpot B | NONE | SWORD | rhand | 1H (rhand) | 1 | 1 |
| 12805 | Flower Arrangement | NONE | SWORD | rhand | 1H (rhand) | 1 | 1 |
| 14780 | Baguette's Bread Sword | NONE | SWORD | rhand | 1H (rhand) | 0 | 0 |
| 14788 | Baguette's Magic Sword | NONE | SWORD | rhand | 1H (rhand) | 0 | 0 |
| 14782 | Baguette's Two-handed Sword | NONE | SWORD | lrhand | 2H (lrhand) | 0 | 0 |

### Jewelry

Pieces: 38.

| ID | Name | Grade | Slot |
|----|------|-------|------|
| 118 | Necklace of Magic | NONE | neck |
| 906 | Necklace of Knowledge | NONE | neck |
| 907 | Necklace of Anguish | NONE | neck |
| 908 | Necklace of Wisdom | NONE | neck |
| 909 | Blue Diamond Necklace | NONE | neck |
| 1506 | Necklace of Courage | NONE | neck |
| 1507 | Necklace of Valor | NONE | neck |
| 12746 | Crystal Pendant | NONE | neck |
| 12747 | Ruby Pendant | NONE | neck |
| 12748 | Sapphire Pendant | NONE | neck |
| 12749 | Diamond Pendant | NONE | neck |
| 12750 | Enria Pendant | NONE | neck |
| 12751 | Thons Pendant | NONE | neck |
| 12752 | Asofe Pendant | NONE | neck |
| 17199 | National Representative Warrior's Necklace | NONE | neck |
| 112 | Apprentice's Earring | NONE | rear;lear |
| 113 | Mystic's Earring | NONE | rear;lear |
| 114 | Earring of Strength | NONE | rear;lear |
| 115 | Earring of Wisdom | NONE | rear;lear |
| 845 | Cat's Eye Earring | NONE | rear;lear |
| 846 | Coral Earring | NONE | rear;lear |
| 17200 | National Representative Warrior's Earring | NONE | rear;lear |
| 116 | Magic Ring | NONE | rfinger;lfinger |
| 875 | Ring of Knowledge | NONE | rfinger;lfinger |
| 876 | Ring of Anguish | NONE | rfinger;lfinger |
| 877 | Ring of Wisdom | NONE | rfinger;lfinger |
| 878 | Blue Coral Ring | NONE | rfinger;lfinger |
| 1508 | Ring of Raccoon | NONE | rfinger;lfinger |
| 1509 | Ring of Firefly | NONE | rfinger;lfinger |
| 9899 | Weight Loss Ring | NONE | rfinger;lfinger |
| 9900 | Quiet Footsteps Ring | NONE | rfinger;lfinger |
| 10140 | Blessed Ring of Escape | NONE | rfinger;lfinger |
| 10211 | Blessed Ring of Resurrection | NONE | rfinger;lfinger |
| 17050 | Shiny Couple Ring | NONE | rfinger;lfinger |
| 17198 | National Representative Warrior's Ring | NONE | rfinger;lfinger |
| 21159 | Wedding Ring - Male | NONE | rfinger;lfinger |
| 21160 | Wedding Ring - Female | NONE | rfinger;lfinger |
| 22237 | Shiny Couple Ring | NONE | rfinger;lfinger |

## 5. D-Grade Equipment

### Armor

Pieces: 98 (slot-relevant armor for this grade, noise variants excluded).

| ID | Name | Grade | Armor type | Slot | Set membership |
|----|------|-------|------------|------|----------------|
| 40 | Leather Boots | D | (none) | feet | (not in a set) |
| 553 | Iron Boots | D | (none) | feet | (not in a set) |
| 1123 | Blue Buckskin Boots | D | (none) | feet | (not in a set) |
| 1124 | Boots of Power | D | (none) | feet | (not in a set) |
| 1125 | Assault Boots | D | (none) | feet | (not in a set) |
| 2422 | Reinforced Leather Boots | D | (none) | feet | d_grade.xml#4 |
| 2423 | Boots of Knowledge | D | (none) | feet | (not in a set) |
| 2424 | Manticore Skin Boots | D | (none) | feet | d_grade.xml#6 |
| 2425 | Brigandine Boots | D | (none) | feet | (not in a set) |
| 2426 | Elven Mithril Boots | D | (none) | feet | (not in a set) |
| 2427 | Salamander Skin Boots | D | (none) | feet | (not in a set) |
| 2428 | Plate Boots | D | (none) | feet | (not in a set) |
| 7853 | Clan Oath Sabaton - Heavy Armor | D | (none) | feet | clan.xml#59 |
| 7856 | Clan Oath Boots - Light Armor | D | (none) | feet | clan.xml#60 |
| 7859 | Clan Oath Sandals - Robe | D | (none) | feet | clan.xml#61 |
| 61 | Mithril Plate Gloves | D | (none) | gloves | (not in a set) |
| 63 | Gauntlets | D | (none) | gloves | (not in a set) |
| 604 | Crafted Leather Gloves | D | (none) | gloves | (not in a set) |
| 605 | Leather Gauntlets | D | (none) | gloves | (not in a set) |
| 606 | Rip Gauntlets | D | (none) | gloves | (not in a set) |
| 607 | Ogre Power Gauntlets | D | (none) | gloves | (not in a set) |
| 2446 | Reinforced Leather Gloves | D | (none) | gloves | (not in a set) |
| 2447 | Gloves of Knowledge | D | (none) | gloves | d_grade.xml#5 |
| 2448 | Manticore Skin Gloves | D | (none) | gloves | (not in a set) |
| 2449 | Brigandine Gauntlets | D | (none) | gloves | (not in a set) |
| 2450 | Mithril Gloves | D | (none) | gloves | d_grade.xml#8 |
| 2451 | Sage's Worn Gloves | D | (none) | gloves | (not in a set) |
| 7852 | Clan Oath Gauntlets - Heavy Armor | D | (none) | gloves | clan.xml#59 |
| 7855 | Clan Oath Leather Gloves - Light Armor | D | (none) | gloves | clan.xml#60 |
| 7858 | Clan Oath Padded Gloves - Robe | D | (none) | gloves | clan.xml#61 |
| 45 | Bone Helmet | D | (none) | head | (not in a set) |
| 46 | Bronze Helmet | D | (none) | head | (not in a set) |
| 47 | Helmet | D | (none) | head | d_grade.xml#3 |
| 2411 | Brigandine Helmet | D | (none) | head | d_grade.xml#7 |
| 2412 | Plate Helmet | D | (none) | head | (not in a set) |
| 7850 | Clan Oath Helm | D | (none) | head | clan.xml#61 |
| 626 | Bronze Shield | D | (none) | lhand | (not in a set) |
| 627 | Aspis | D | (none) | lhand | (not in a set) |
| 628 | Hoplon | D | (none) | lhand | d_grade.xml#3 |
| 629 | Kite Shield | D | (none) | lhand | (not in a set) |
| 630 | Square Shield | D | (none) | lhand | (not in a set) |
| 2493 | Brigandine Shield | D | (none) | lhand | d_grade.xml#7 |
| 2494 | Plate Shield | D | (none) | lhand | (not in a set) |
| 5799 | Nephilim Lord | D | (none) | lhand | (not in a set) |
| 58 | Mithril Breastplate | D | HEAVY | chest | d_grade.xml#3 |
| 347 | Ring Mail Breastplate | D | HEAVY | chest | (not in a set) |
| 348 | Scale Mail | D | HEAVY | chest | (not in a set) |
| 349 | Compound Scale Mail | D | HEAVY | chest | (not in a set) |
| 350 | Dwarven Scale Mail | D | HEAVY | chest | (not in a set) |
| 351 | Blast Plate | D | HEAVY | chest | (not in a set) |
| 352 | Brigandine Tunic | D | HEAVY | chest | d_grade.xml#7 |
| 353 | Half Plate Armor | D | HEAVY | chest | (not in a set) |
| 10019 | Ring Mail Breastplate | D | HEAVY | chest | (not in a set) |
| 59 | Mithril Gaiters | D | HEAVY | legs | d_grade.xml#3 |
| 376 | Iron Plate Gaiters | D | HEAVY | legs | (not in a set) |
| 377 | Scale Gaiters | D | HEAVY | legs | (not in a set) |
| 378 | Compound Scale Gaiters | D | HEAVY | legs | (not in a set) |
| 379 | Dwarven Scale Gaiters | D | HEAVY | legs | (not in a set) |
| 380 | Plate Gaiters | D | HEAVY | legs | (not in a set) |
| 2377 | Mithril Scale Gaiters | D | HEAVY | legs | (not in a set) |
| 2378 | Brigandine Gaiters | D | HEAVY | legs | d_grade.xml#7 |
| 10020 | Iron Plate Gaiters | D | HEAVY | legs | (not in a set) |
| 7851 | Clan Oath Armor | D | HEAVY | onepiece | clan.xml#59 |
| 391 | Puma Skin Shirt | D | LIGHT | chest | (not in a set) |
| 392 | Lion Skin Shirt | D | LIGHT | chest | (not in a set) |
| 393 | Mithril Banded Mail | D | LIGHT | chest | (not in a set) |
| 394 | Reinforced Leather Shirt | D | LIGHT | chest | d_grade.xml#4 |
| 395 | Manticore Skin Shirt | D | LIGHT | chest | d_grade.xml#6 |
| 486 | Tattoo of Fire | D | LIGHT | chest | (not in a set) |
| 487 | Tattoo of Resolve | D | LIGHT | chest | (not in a set) |
| 492 | Tattoo of Soul | D | LIGHT | chest | (not in a set) |
| 10021 | Puma Skin Shirt | D | LIGHT | chest | (not in a set) |
| 413 | Puma Skin Gaiters | D | LIGHT | legs | (not in a set) |
| 414 | Lion Skin Gaiters | D | LIGHT | legs | (not in a set) |
| 415 | Mithril Banded Gaiters | D | LIGHT | legs | (not in a set) |
| 416 | Reinforced Leather Gaiters | D | LIGHT | legs | d_grade.xml#4 |
| 417 | Manticore Skin Gaiters | D | LIGHT | legs | d_grade.xml#6 |
| 10022 | Puma Skin Gaiters | D | LIGHT | legs | (not in a set) |
| 396 | Salamander Skin Mail | D | LIGHT | onepiece | (not in a set) |
| 7854 | Clan Oath Brigandine | D | LIGHT | onepiece | clan.xml#60 |
| 432 | Cursed Tunic | D | MAGIC | chest | (not in a set) |
| 433 | Elven Tunic | D | MAGIC | chest | (not in a set) |
| 434 | White Tunic | D | MAGIC | chest | (not in a set) |
| 435 | Mystic's Tunic | D | MAGIC | chest | (not in a set) |
| 436 | Tunic of Knowledge | D | MAGIC | chest | d_grade.xml#5 |
| 437 | Mithril Tunic | D | MAGIC | chest | d_grade.xml#8 |
| 2396 | Elven Mithril Tunic | D | MAGIC | chest | (not in a set) |
| 10023 | Cursed Tunic | D | MAGIC | chest | (not in a set) |
| 465 | Cursed Stockings | D | MAGIC | legs | (not in a set) |
| 466 | Elven Stockings | D | MAGIC | legs | (not in a set) |
| 467 | Dark Stockings | D | MAGIC | legs | (not in a set) |
| 468 | Mystic's Stockings | D | MAGIC | legs | (not in a set) |
| 469 | Stockings of Knowledge | D | MAGIC | legs | d_grade.xml#5 |
| 470 | Mithril Stockings | D | MAGIC | legs | d_grade.xml#8 |
| 2401 | Elven Mithril Stockings | D | MAGIC | legs | (not in a set) |
| 10024 | Cursed Stockings | D | MAGIC | legs | (not in a set) |
| 438 | Sage's Rag | D | MAGIC | onepiece | (not in a set) |
| 7857 | Clan Oath Aketon | D | MAGIC | onepiece | clan.xml#61 |

### Weapons

Base weapons: 180 (SA/elemental, event, PvP, common, shadow and fortune variants excluded).

| ID | Name | Grade | Weapon type | Slot | Class | pAtk | mAtk |
|----|------|-------|-------------|------|-------|------|------|
| 9226 | General's Katzbalger | D | ANCIENTSWORD | lrhand | 2H (lrhand) | 100 | 54 |
| 9223 | Katzbalger | D | ANCIENTSWORD | lrhand | 2H (lrhand) | 86 | 47 |
| 9220 | Field Sword | D | ANCIENTSWORD | lrhand | 2H (lrhand) | 69 | 39 |
| 9216 | Military Talwar | D | ANCIENTSWORD | lrhand | 2H (lrhand) | 55 | 32 |
| 9211 | Talwar | D | ANCIENTSWORD | lrhand | 2H (lrhand) | 43 | 26 |
| 9210 | Talwar | D | ANCIENTSWORD | lrhand | 2H (lrhand) | 43 | 26 |
| 7896 | Titan Hammer | D | BLUNT | lrhand | 2H (lrhand) | 96 | 47 |
| 159 | Bonebreaker | D | BLUNT | rhand | 1H (rhand) | 92 | 54 |
| 188 | Ghost Staff | D | BLUNT | lrhand | 2H (lrhand) | 90 | 79 |
| 190 | Atuba Mace | D | BLUNT | lrhand | 2H (lrhand) | 90 | 79 |
| 187 | Atuba Hammer | D | BLUNT | lrhand | 2H (lrhand) | 90 | 79 |
| 158 | Tarbar | D | BLUNT | rhand | 1H (rhand) | 79 | 47 |
| 169 | Skull Breaker | D | BLUNT | rhand | 1H (rhand) | 79 | 47 |
| 172 | Heavy Bone Club | D | BLUNT | rhand | 1H (rhand) | 79 | 47 |
| 88 | Morning Star | D | BLUNT | rhand | 1H (rhand) | 79 | 47 |
| 90 | Goat Head Staff | D | BLUNT | lrhand | 2H (lrhand) | 77 | 69 |
| 189 | Staff of Life | D | BLUNT | rhand | 1H (rhand) | 74 | 72 |
| 157 | Spiked Club | D | BLUNT | rhand | 1H (rhand) | 64 | 39 |
| 7890 | Priest Mace | D | BLUNT | rhand | 1H (rhand) | 63 | 63 |
| 186 | Staff of Magic | D | BLUNT | lrhand | 2H (lrhand) | 62 | 57 |
| 7829 | Traveler's Tomahawk | D | BLUNT | rhand | 1H (rhand) | 51 | 32 |
| 86 | Tomahawk | D | BLUNT | rhand | 1H (rhand) | 51 | 32 |
| 183 | Mystic Staff | D | BLUNT | lrhand | 2H (lrhand) | 50 | 47 |
| 184 | Conjuror's Staff | D | BLUNT | lrhand | 2H (lrhand) | 50 | 47 |
| 7825 | Traveler's Staff | D | BLUNT | lrhand | 2H (lrhand) | 50 | 47 |
| 185 | Staff of Mana | D | BLUNT | lrhand | 2H (lrhand) | 50 | 47 |
| 181 | Mace of Miracle | D | BLUNT | rhand | 1H (rhand) | 41 | 43 |
| 182 | Doom Hammer | D | BLUNT | rhand | 1H (rhand) | 41 | 43 |
| 179 | Mace of Prayer | D | BLUNT | rhand | 1H (rhand) | 41 | 43 |
| 180 | Mace of Judgment | D | BLUNT | rhand | 1H (rhand) | 41 | 43 |
| 7822 | Traveler's Mace | D | BLUNT | rhand | 1H (rhand) | 41 | 43 |
| 166 | Heavy Mace | D | BLUNT | rhand | 1H (rhand) | 40 | 26 |
| 156 | Hand Axe | D | BLUNT | rhand | 1H (rhand) | 40 | 26 |
| 167 | Scalpel | D | BLUNT | rhand | 1H (rhand) | 40 | 26 |
| 168 | Work Hammer | D | BLUNT | rhand | 1H (rhand) | 40 | 26 |
| 178 | Bone Staff | D | BLUNT | lrhand | 2H (lrhand) | 39 | 39 |
| 749 | 0 | D | BLUNT | lrhand | 2H (lrhand) | 21 | 32 |
| 280 | Light Crossbow | D | BOW | lrhand | 2H (lrhand) | 191 | 54 |
| 279 | Reinforced Long Bow | D | BOW | lrhand | 2H (lrhand) | 179 | 51 |
| 278 | Gastraphetes | D | BOW | lrhand | 2H (lrhand) | 132 | 39 |
| 275 | Long Bow | D | BOW | lrhand | 2H (lrhand) | 114 | 35 |
| 277 | Dark Elven Bow | D | BOW | lrhand | 2H (lrhand) | 105 | 32 |
| 276 | Elven Bow | D | BOW | lrhand | 2H (lrhand) | 105 | 32 |
| 7824 | Traveler's Long Bow | D | BOW | lrhand | 2H (lrhand) | 105 | 32 |
| 7823 | Traveler's Dark Elven Bow | D | BOW | lrhand | 2H (lrhand) | 105 | 32 |
| 274 | Reinforced Bow | D | BOW | lrhand | 2H (lrhand) | 82 | 26 |
| 9227 | Cranequin | D | CROSSBOW | lrhand | 2H (lrhand) | 117 | 54 |
| 9224 | Arm Breaker | D | CROSSBOW | lrhand | 2H (lrhand) | 100 | 47 |
| 9221 | Crossbow | D | CROSSBOW | lrhand | 2H (lrhand) | 81 | 39 |
| 9217 | Hand Crossbow | D | CROSSBOW | lrhand | 2H (lrhand) | 64 | 32 |
| 9996 | Hand Crossbow | D | CROSSBOW | lrhand | 2H (lrhand) | 64 | 32 |
| 9995 | Hand Crossbow | D | CROSSBOW | lrhand | 2H (lrhand) | 64 | 32 |
| 9212 | Field Gun | D | CROSSBOW | lrhand | 2H (lrhand) | 51 | 26 |
| 225 | Mithril Dagger | D | DAGGER | rhand | 1H (rhand) | 80 | 54 |
| 224 | Maingauche | D | DAGGER | rhand | 1H (rhand) | 69 | 47 |
| 1660 | Cursed Maingauche | D | DAGGER | rhand | 1H (rhand) | 62 | 42 |
| 223 | Kukuri | D | DAGGER | rhand | 1H (rhand) | 56 | 39 |
| 241 | Shilen Knife | D | DAGGER | rhand | 1H (rhand) | 45 | 52 |
| 7830 | Traveler's Poniard Dagger | D | DAGGER | rhand | 1H (rhand) | 45 | 32 |
| 222 | Poniard Dagger | D | DAGGER | rhand | 1H (rhand) | 45 | 32 |
| 239 | Mystic Knife | D | DAGGER | rhand | 1H (rhand) | 45 | 52 |
| 238 | Dagger of Mana | D | DAGGER | rhand | 1H (rhand) | 45 | 52 |
| 240 | Conjurer's Knife | D | DAGGER | rhand | 1H (rhand) | 45 | 52 |
| 220 | Crafted Dagger | D | DAGGER | rhand | 1H (rhand) | 35 | 26 |
| 221 | Assassin Knife | D | DAGGER | rhand | 1H (rhand) | 35 | 26 |
| 5127 | Dailaon Knife | D | DAGGER | rhand | 1H (rhand) | 5 | 5 |
| 5128 | Crokian Blade | D | DAGGER | rhand | 1H (rhand) | 5 | 5 |
| 5130 | Nos Sword | D | DAGGER | rhand | 1H (rhand) | 5 | 5 |
| 2507 | Lizardspear | D | DAGGER | rhand | 1H (rhand) | 5 | 5 |
| 9641 | Tears l1 | D | DAGGER | rhand | 1H (rhand) | 5 | 5 |
| 4028 | Giant Cannon | D | DAGGER | rhand | 1H (rhand) | 5 | 5 |
| 9643 | Tears l3 | D | DAGGER | rhand | 1H (rhand) | 5 | 5 |
| 9642 | Tears l2 | D | DAGGER | rhand | 1H (rhand) | 5 | 5 |
| 13843 | Draconic Peltast Weapon | D | DAGGER | rhand | 1H (rhand) | 5 | 5 |
| 1471 | Silenos Blowgun | D | DAGGER | rhand | 1H (rhand) | 5 | 5 |
| 5132 | Giant Trident | D | DAGGER | rhand | 1H (rhand) | 5 | 5 |
| 5131 | Parhit Staff | D | DAGGER | rhand | 1H (rhand) | 5 | 5 |
| 5129 | Doll Knife | D | DUAL | lrhand | dual (lrhand) | 200 | 6 |
| 13036 | Sword of Ice and Fire | D | DUAL | lrhand | dual (lrhand) | 147 | 92 |
| 13035 | Enhanced Sprite's Sword | D | DUAL | lrhand | dual (lrhand) | 127 | 82 |
| 2529 | Bastard Sword*Crimson Sword | D | DUAL | lrhand | dual (lrhand) | 107 | 51 |
| 2530 | Bastard Sword*Elven Sword | D | DUAL | lrhand | dual (lrhand) | 107 | 51 |
| 2537 | Spinebone Sword*Elven Sword | D | DUAL | lrhand | dual (lrhand) | 107 | 51 |
| 2536 | Spinebone Sword*Crimson Sword | D | DUAL | lrhand | dual (lrhand) | 107 | 51 |
| 2543 | Artisan's Sword*Elven Sword | D | DUAL | lrhand | dual (lrhand) | 107 | 51 |
| 2548 | Knight's Sword*Elven Sword | D | DUAL | lrhand | dual (lrhand) | 107 | 51 |
| 2547 | Knight's Sword*Crimson Sword | D | DUAL | lrhand | dual (lrhand) | 107 | 51 |
| 13034 | Sprite's Sword | D | DUAL | lrhand | dual (lrhand) | 107 | 72 |
| 2542 | Artisan's Sword*Crimson Sword | D | DUAL | lrhand | dual (lrhand) | 107 | 51 |
| 2526 | Bastard Sword*Spinebone Sword | D | DUAL | lrhand | dual (lrhand) | 96 | 47 |
| 2525 | Bastard Sword*Bastard Sword | D | DUAL | lrhand | dual (lrhand) | 96 | 47 |
| 2527 | Bastard Sword*Artisan's Sword | D | DUAL | lrhand | dual (lrhand) | 96 | 47 |
| 2540 | Artisan's Sword*Artisan's Sword | D | DUAL | lrhand | dual (lrhand) | 96 | 47 |
| 2528 | Bastard Sword*Knight's Sword | D | DUAL | lrhand | dual (lrhand) | 96 | 47 |
| 2522 | Saber*Elven Sword | D | DUAL | lrhand | dual (lrhand) | 96 | 47 |
| 2535 | Spinebone Sword*Knight's Sword | D | DUAL | lrhand | dual (lrhand) | 96 | 47 |
| 2546 | Knight's Sword*Knight's Sword | D | DUAL | lrhand | dual (lrhand) | 96 | 47 |
| 2541 | Artisan's Sword*Knight's Sword | D | DUAL | lrhand | dual (lrhand) | 96 | 47 |
| 2521 | Saber*Crimson Sword | D | DUAL | lrhand | dual (lrhand) | 96 | 47 |
| 2533 | Spinebone Sword*Spinebone Sword | D | DUAL | lrhand | dual (lrhand) | 96 | 47 |
| 2534 | Spinebone Sword*Artisan's Sword | D | DUAL | lrhand | dual (lrhand) | 96 | 47 |
| 2518 | Saber*Spinebone Sword | D | DUAL | lrhand | dual (lrhand) | 83 | 41 |
| 2517 | Saber*Bastard Sword | D | DUAL | lrhand | dual (lrhand) | 83 | 41 |
| 2520 | Saber*Knight's Sword | D | DUAL | lrhand | dual (lrhand) | 83 | 41 |
| 2519 | Saber*Artisan's Sword | D | DUAL | lrhand | dual (lrhand) | 83 | 41 |
| 2516 | Saber*Saber | D | DUAL | lrhand | dual (lrhand) | 73 | 37 |
| 9640 | Tears r3 | D | DUAL | lrhand | dual (lrhand) | 5 | 5 |
| 9639 | Tears r2 | D | DUAL | lrhand | dual (lrhand) | 5 | 5 |
| 9638 | Tears r1 | D | DUAL | lrhand | dual (lrhand) | 5 | 5 |
| 262 | Scallop Jamadhr | D | DUALFIST | lrhand | dual fist (lrhand) | 112 | 54 |
| 261 | Bich'Hwa | D | DUALFIST | lrhand | dual fist (lrhand) | 96 | 47 |
| 260 | Triple-Edged Jamadhr | D | DUALFIST | lrhand | dual fist (lrhand) | 78 | 39 |
| 259 | Single-Edged Jamadhr | D | DUALFIST | lrhand | dual fist (lrhand) | 62 | 32 |
| 7828 | Traveler's Jamadhr | D | DUALFIST | lrhand | dual fist (lrhand) | 62 | 32 |
| 258 | Bagh-Nakh | D | DUALFIST | lrhand | dual fist (lrhand) | 49 | 26 |
| 322 | Vajra Wands | D | ETC | rhand | 1H (rhand) | 74 | 72 |
| 323 | Ancient Reagent | D | ETC | rhand | 1H (rhand) | 74 | 72 |
| 321 | Demon Fangs | D | ETC | rhand | 1H (rhand) | 67 | 66 |
| 320 | Blue Crystal Skull | D | ETC | rhand | 1H (rhand) | 67 | 66 |
| 318 | Crucifix of Blood | D | ETC | rhand | 1H (rhand) | 63 | 63 |
| 319 | Eye of Infinity | D | ETC | rhand | 1H (rhand) | 63 | 63 |
| 317 | Tome of Blood | D | ETC | rhand | 1H (rhand) | 51 | 52 |
| 316 | Sage's Blood | D | ETC | rhand | 1H (rhand) | 51 | 52 |
| 315 | Divine Tome | D | ETC | rhand | 1H (rhand) | 41 | 43 |
| 7827 | Traveler's Wand | D | ETC | rhand | 1H (rhand) | 41 | 43 |
| 101 | Scroll of Wisdom | D | ETC | rhand | 1H (rhand) | 32 | 35 |
| 314 | Proof of Revenge | D | ETC | rhand | 1H (rhand) | 32 | 35 |
| 313 | Temptation of Abyss | D | ETC | rhand | 1H (rhand) | 32 | 35 |
| 312 | Branch of Life | D | ETC | rhand | 1H (rhand) | 32 | 35 |
| 6530 | Albatross Rod | D | FISHINGROD | lrhand | 2H (lrhand) | 1 | 1 |
| 297 | Glaive | D | POLE | lrhand | 2H (lrhand) | 92 | 54 |
| 93 | Winged Spear | D | POLE | lrhand | 2H (lrhand) | 79 | 47 |
| 294 | War Pick | D | POLE | lrhand | 2H (lrhand) | 79 | 47 |
| 296 | Dwarven Pike | D | POLE | lrhand | 2H (lrhand) | 64 | 39 |
| 293 | War Hammer | D | POLE | lrhand | 2H (lrhand) | 64 | 39 |
| 295 | Dwarven Trident | D | POLE | lrhand | 2H (lrhand) | 51 | 32 |
| 7831 | Traveler's Pike | D | POLE | lrhand | 2H (lrhand) | 51 | 32 |
| 292 | Pike | D | POLE | lrhand | 2H (lrhand) | 51 | 32 |
| 3939 | Lady's Fan | D | POLE | lrhand | 2H (lrhand) | 50 | 26 |
| 1376 | Guard Spear | D | POLE | lrhand | 2H (lrhand) | 50 | 26 |
| 3938 | Giant Rod | D | POLE | lrhand | 2H (lrhand) | 50 | 26 |
| 3937 | Giant Bar | D | POLE | lrhand | 2H (lrhand) | 50 | 26 |
| 1472 | Dreadbane | D | POLE | lrhand | 2H (lrhand) | 50 | 26 |
| 291 | Trident | D | POLE | lrhand | 2H (lrhand) | 40 | 26 |
| 9225 | Grand Epee | D | RAPIER | rhand | 1H (rhand) | 83 | 54 |
| 9222 | Epee | D | RAPIER | rhand | 1H (rhand) | 72 | 47 |
| 9218 | Estoc | D | RAPIER | rhand | 1H (rhand) | 58 | 39 |
| 9219 | Estoc | D | RAPIER | rhand | 1H (rhand) | 58 | 39 |
| 9215 | Fleuret | D | RAPIER | rhand | 1H (rhand) | 46 | 32 |
| 9214 | Fleuret | D | RAPIER | rhand | 1H (rhand) | 46 | 32 |
| 9213 | Fleuret | D | RAPIER | rhand | 1H (rhand) | 46 | 32 |
| 10003 | Fleuret | D | RAPIER | rhand | 1H (rhand) | 46 | 32 |
| 9209 | Rapier | D | RAPIER | rhand | 1H (rhand) | 36 | 26 |
| 70 | Claymore | D | SWORD | lrhand | 2H (lrhand) | 112 | 54 |
| 7881 | Titan Sword | D | SWORD | lrhand | 2H (lrhand) | 96 | 47 |
| 2499 | Elven Long Sword | D | SWORD | rhand | 1H (rhand) | 92 | 54 |
| 129 | Sword of Revolution | D | SWORD | rhand | 1H (rhand) | 79 | 47 |
| 13842 | Tiat Two-Handed Weapon | D | SWORD | lrhand | 2H (lrhand) | 78 | 39 |
| 5792 | Tomb Guard B | D | SWORD | lrhand | 2H (lrhand) | 78 | 39 |
| 5795 | Tomb Guard A | D | SWORD | lrhand | 2H (lrhand) | 78 | 39 |
| 5791 | Tomb Guard A | D | SWORD | lrhand | 2H (lrhand) | 78 | 39 |
| 124 | Two-Handed Sword | D | SWORD | lrhand | 2H (lrhand) | 78 | 39 |
| 14606 | Gracian Soldier Two-Handed Sword | D | SWORD | lrhand | 2H (lrhand) | 78 | 39 |
| 5796 | Tomb Guard B | D | SWORD | lrhand | 2H (lrhand) | 78 | 39 |
| 9137 | Sword of Valakas (2-Handed) | D | SWORD | lrhand | 2H (lrhand) | 78 | 39 |
| 127 | Crimson Sword | D | SWORD | rhand | 1H (rhand) | 64 | 39 |
| 130 | Elven Sword | D | SWORD | rhand | 1H (rhand) | 64 | 39 |
| 7886 | Sword of Magic Fog | D | SWORD | rhand | 1H (rhand) | 63 | 63 |
| 128 | Knight's Sword | D | SWORD | rhand | 1H (rhand) | 51 | 32 |
| 126 | Artisan's Sword | D | SWORD | rhand | 1H (rhand) | 51 | 32 |
| 69 | Bastard Sword | D | SWORD | rhand | 1H (rhand) | 51 | 32 |
| 125 | Spinebone Sword | D | SWORD | rhand | 1H (rhand) | 51 | 32 |
| 7826 | Traveler's Bastard Sword | D | SWORD | rhand | 1H (rhand) | 51 | 32 |
| 5285 | Heavy Sword | D | SWORD | lrhand | 2H (lrhand) | 49 | 26 |
| 7880 | Steel Sword | D | SWORD | lrhand | 2H (lrhand) | 49 | 26 |
| 143 | Sword of Mystic | D | SWORD | rhand | 1H (rhand) | 43 | 45 |
| 83 | Sword of Magic | D | SWORD | rhand | 1H (rhand) | 43 | 45 |
| 144 | Sword of Occult | D | SWORD | rhand | 1H (rhand) | 43 | 45 |
| 123 | Saber | D | SWORD | rhand | 1H (rhand) | 40 | 26 |
| 7885 | Priest Sword | D | SWORD | rhand | 1H (rhand) | 32 | 35 |

### Jewelry

Pieces: 20.

| ID | Name | Grade | Slot |
|----|------|-------|------|
| 910 | Necklace of Devotion | D | neck |
| 911 | Enchanted Necklace | D | neck |
| 912 | Near Forest Necklace | D | neck |
| 913 | Elven Necklace | D | neck |
| 914 | Necklace of Darkness | D | neck |
| 10123 | Necklace of Devotion | D | neck |
| 847 | Red Crescent Earring | D | rear;lear |
| 848 | Enchanted Earring | D | rear;lear |
| 849 | Tiger's Eye Earring | D | rear;lear |
| 850 | Elven Earring | D | rear;lear |
| 851 | Omen Beast's Eye Earring | D | rear;lear |
| 10122 | Red Crescent Earring | D | rear;lear |
| 13293 | Pailaka Earring | D | rear;lear |
| 879 | Enchanted Ring | D | rfinger;lfinger |
| 880 | Black Pearl Ring | D | rfinger;lfinger |
| 881 | Elven Ring | D | rfinger;lfinger |
| 882 | Mithril Ring | D | rfinger;lfinger |
| 890 | Ring of Devotion | D | rfinger;lfinger |
| 10124 | Ring of Devotion | D | rfinger;lfinger |
| 13294 | Pailaka Ring | D | rfinger;lfinger |

## 6. C-Grade Equipment

### Armor

Pieces: 81 (slot-relevant armor for this grade, noise variants excluded).

| ID | Name | Grade | Armor type | Slot | Set membership |
|----|------|-------|------------|------|----------------|
| 62 | Reinforced Mithril Boots | C | (none) | feet | c_grade.xml#12 |
| 64 | Composite Boots | C | (none) | feet | (not in a set) |
| 603 | Divine Boots | C | (none) | feet | (not in a set) |
| 1126 | Crimson Boots | C | (none) | feet | (not in a set) |
| 1127 | Forgotten Boots | C | (none) | feet | (not in a set) |
| 1128 | Adamantite Boots | C | (none) | feet | (not in a set) |
| 2429 | Chain Boots | C | (none) | feet | (not in a set) |
| 2430 | Karmian Boots | C | (none) | feet | (not in a set) |
| 2431 | Plated Leather Boots | C | (none) | feet | c_grade.xml#15 |
| 2432 | Dwarven Chain Boots | C | (none) | feet | (not in a set) |
| 2433 | Boots of Seal | C | (none) | feet | (not in a set) |
| 2434 | Rind Leather Boots | C | (none) | feet | (not in a set) |
| 2435 | Demon's Boots | C | (none) | feet | (not in a set) |
| 2436 | Theca Leather Boots | C | (none) | feet | c_grade.xml#21 |
| 2437 | Drake Leather Boots | C | (none) | feet | c_grade.xml#22 |
| 2438 | Full Plate Boots | C | (none) | feet | (not in a set) |
| 608 | Mithril Gauntlets | C | (none) | gloves | (not in a set) |
| 609 | Gauntlets of Ghost | C | (none) | gloves | (not in a set) |
| 1120 | Pa'agrian Hand | C | (none) | gloves | (not in a set) |
| 2452 | Reinforced Mithril Gloves | C | (none) | gloves | (not in a set) |
| 2453 | Chain Gloves | C | (none) | gloves | (not in a set) |
| 2454 | Karmian Gloves | C | (none) | gloves | c_grade.xml#14 |
| 2455 | Plated Leather Gloves | C | (none) | gloves | (not in a set) |
| 2456 | Dwarven Chain Gloves | C | (none) | gloves | (not in a set) |
| 2457 | Gloves of Seal | C | (none) | gloves | (not in a set) |
| 2458 | Rind Leather Gloves | C | (none) | gloves | (not in a set) |
| 2459 | Demon's Gloves | C | (none) | gloves | c_grade.xml#20 |
| 2460 | Theca Leather Gloves | C | (none) | gloves | (not in a set) |
| 2461 | Drake Leather Gloves | C | (none) | gloves | (not in a set) |
| 2462 | Full Plate Gauntlets | C | (none) | gloves | (not in a set) |
| 2463 | Divine Gloves | C | (none) | gloves | c_grade.xml#24 |
| 2468 | Blessed Gloves | C | (none) | gloves | (not in a set) |
| 497 | Chain Helmet | C | (none) | head | (not in a set) |
| 498 | Steel Plate Helmet | C | (none) | head | (not in a set) |
| 499 | Mithril Helmet | C | (none) | head | (not in a set) |
| 500 | Great Helmet | C | (none) | head | (not in a set) |
| 517 | Composite Helmet | C | (none) | head | c_grade.xml#19 |
| 529 | Cap of Mana | C | (none) | head | (not in a set) |
| 531 | Paradia Hood | C | (none) | head | (not in a set) |
| 533 | Hood of Solar Eclipse | C | (none) | head | (not in a set) |
| 535 | Hood of Summoning | C | (none) | head | (not in a set) |
| 537 | Elemental Hood | C | (none) | head | (not in a set) |
| 539 | Hood of Grace | C | (none) | head | (not in a set) |
| 541 | Phoenix Hood | C | (none) | head | (not in a set) |
| 543 | Hood of Aid | C | (none) | head | (not in a set) |
| 545 | Flame Helm | C | (none) | head | (not in a set) |
| 549 | Helm of Avadon | C | (none) | head | (not in a set) |
| 551 | Helmet of Pledge | C | (none) | head | (not in a set) |
| 1149 | Shining Circlet | C | (none) | head | (not in a set) |
| 2413 | Chain Hood | C | (none) | head | c_grade.xml#13 |
| 2414 | Full Plate Helmet | C | (none) | head | c_grade.xml#23 |
| 103 | Tower Shield | C | (none) | lhand | (not in a set) |
| 107 | Composite Shield | C | (none) | lhand | c_grade.xml#19 |
| 631 | Eldarake | C | (none) | lhand | (not in a set) |
| 632 | Knight's Shield | C | (none) | lhand | (not in a set) |
| 2495 | Chain Shield | C | (none) | lhand | c_grade.xml#13 |
| 2496 | Dwarven Chain Shield | C | (none) | lhand | (not in a set) |
| 2497 | Full Plate Shield | C | (none) | lhand | c_grade.xml#23 |
| 354 | Chain Mail Shirt | C | HEAVY | chest | c_grade.xml#13 |
| 355 | Dwarven Chain Mail Shirt | C | HEAVY | chest | (not in a set) |
| 381 | Chain Gaiters | C | HEAVY | legs | c_grade.xml#13 |
| 382 | Dwarven Chain Gaiters | C | HEAVY | legs | (not in a set) |
| 60 | Composite Armor | C | HEAVY | onepiece | c_grade.xml#19 |
| 356 | Full Plate Armor | C | HEAVY | onepiece | c_grade.xml#23 |
| 397 | Mithril Shirt | C | LIGHT | chest | c_grade.xml#12 |
| 398 | Plated Leather | C | LIGHT | chest | c_grade.xml#15 |
| 399 | Rind Leather Armor | C | LIGHT | chest | (not in a set) |
| 400 | Theca Leather Armor | C | LIGHT | chest | c_grade.xml#21 |
| 489 | Tattoo of Bravery | C | LIGHT | chest | (not in a set) |
| 418 | Plated Leather Gaiters | C | LIGHT | legs | c_grade.xml#15 |
| 419 | Rind Leather Gaiters | C | LIGHT | legs | (not in a set) |
| 420 | Theca Leather Gaiters | C | LIGHT | legs | c_grade.xml#21 |
| 2387 | Reinforced Mithril Gaiters | C | LIGHT | legs | c_grade.xml#12 |
| 401 | Drake Leather Armor | C | LIGHT | onepiece | c_grade.xml#22 |
| 439 | Karmian Tunic | C | MAGIC | chest | c_grade.xml#14 |
| 441 | Demon's Tunic | C | MAGIC | chest | c_grade.xml#20 |
| 442 | Divine Tunic | C | MAGIC | chest | c_grade.xml#24 |
| 471 | Karmian Stockings | C | MAGIC | legs | c_grade.xml#14 |
| 472 | Demon's Stockings | C | MAGIC | legs | c_grade.xml#20 |
| 473 | Divine Stockings | C | MAGIC | legs | c_grade.xml#24 |
| 440 | Robe of Seal | C | MAGIC | onepiece | (not in a set) |

### Weapons

Base weapons: 152 (SA/elemental, event, PvP, common, shadow and fortune variants excluded).

| ID | Name | Grade | Weapon type | Slot | Class | pAtk | mAtk |
|----|------|-------|-------------|------|-------|------|------|
| 9296 | Saber Tooth | C | ANCIENTSWORD | lrhand | 2H (lrhand) | 169 | 83 |
| 9284 | Immortal Edge | C | ANCIENTSWORD | lrhand | 2H (lrhand) | 151 | 76 |
| 9232 | Schlager | C | ANCIENTSWORD | lrhand | 2H (lrhand) | 116 | 61 |
| 7897 | Dwarven Hammer | C | BLUNT | lrhand | 2H (lrhand) | 190 | 83 |
| 7898 | Karik Horn | C | BLUNT | lrhand | 2H (lrhand) | 169 | 76 |
| 2503 | Yaksa Mace | C | BLUNT | rhand | 1H (rhand) | 156 | 83 |
| 205 | Ghoul's Staff | C | BLUNT | lrhand | 2H (lrhand) | 152 | 122 |
| 206 | Demon's Staff | C | BLUNT | lrhand | 2H (lrhand) | 152 | 122 |
| 204 | Deadman's Staff | C | BLUNT | lrhand | 2H (lrhand) | 152 | 122 |
| 203 | Pa'agrian Axe | C | BLUNT | lrhand | 2H (lrhand) | 141 | 114 |
| 162 | War Axe | C | BLUNT | rhand | 1H (rhand) | 139 | 76 |
| 199 | Pa'agrian Hammer | C | BLUNT | lrhand | 2H (lrhand) | 135 | 111 |
| 198 | Inferno Staff | C | BLUNT | lrhand | 2H (lrhand) | 135 | 111 |
| 197 | Paradia Staff | C | BLUNT | lrhand | 2H (lrhand) | 135 | 111 |
| 200 | Sage's Staff | C | BLUNT | lrhand | 2H (lrhand) | 135 | 111 |
| 7891 | Ecliptic Axe | C | BLUNT | rhand | 1H (rhand) | 125 | 111 |
| 2502 | Dwarven War Hammer | C | BLUNT | rhand | 1H (rhand) | 122 | 68 |
| 195 | Cursed Staff | C | BLUNT | lrhand | 2H (lrhand) | 119 | 100 |
| 201 | Club of Nature | C | BLUNT | rhand | 1H (rhand) | 111 | 101 |
| 174 | Nirvana Axe | C | BLUNT | rhand | 1H (rhand) | 111 | 101 |
| 202 | Mace of Underworld | C | BLUNT | rhand | 1H (rhand) | 111 | 101 |
| 196 | Stick of Eternity | C | BLUNT | rhand | 1H (rhand) | 111 | 101 |
| 89 | Big Hammer | C | BLUNT | rhand | 1H (rhand) | 107 | 61 |
| 13791 | Small Red Boing Hammer | C | BLUNT | rhand | 1H (rhand) | 107 | 61 |
| 13790 | Blue Boing Hammer | C | BLUNT | rhand | 1H (rhand) | 107 | 61 |
| 13789 | Red Boing Hammer | C | BLUNT | rhand | 1H (rhand) | 107 | 61 |
| 13792 | Small Blue Boing Hammer | C | BLUNT | rhand | 1H (rhand) | 107 | 61 |
| 173 | Skull Graver | C | BLUNT | rhand | 1H (rhand) | 107 | 61 |
| 161 | Silver Axe | C | BLUNT | rhand | 1H (rhand) | 107 | 61 |
| 160 | Battle Axe | C | BLUNT | rhand | 1H (rhand) | 107 | 61 |
| 192 | Crystal Staff | C | BLUNT | lrhand | 2H (lrhand) | 103 | 89 |
| 191 | Heavy Doom Hammer | C | BLUNT | lrhand | 2H (lrhand) | 103 | 89 |
| 194 | Heavy Doom Axe | C | BLUNT | lrhand | 2H (lrhand) | 103 | 89 |
| 193 | Stick of Faith | C | BLUNT | rhand | 1H (rhand) | 85 | 81 |
| 13971 | Red Boing Fantasy Hammer | C | BLUNT | rhand | 1H (rhand) | 1 | 61 |
| 13974 | Small Blue Boing Fantasy Hammer | C | BLUNT | rhand | 1H (rhand) | 1 | 61 |
| 13973 | Small Red Boing Fantasy Hammer | C | BLUNT | rhand | 1H (rhand) | 1 | 61 |
| 13972 | Blue Boing Fantasy Hammer | C | BLUNT | rhand | 1H (rhand) | 1 | 61 |
| 286 | Eminence Bow | C | BOW | lrhand | 2H (lrhand) | 323 | 83 |
| 283 | Akat Long Bow | C | BOW | lrhand | 2H (lrhand) | 316 | 84 |
| 282 | Elemental Bow | C | BOW | lrhand | 2H (lrhand) | 277 | 75 |
| 285 | Noble Elven Bow | C | BOW | lrhand | 2H (lrhand) | 252 | 68 |
| 281 | Crystallized Ice Bow | C | BOW | lrhand | 2H (lrhand) | 220 | 61 |
| 9300 | Sharpshooter | C | CROSSBOW | lrhand | 2H (lrhand) | 198 | 83 |
| 9288 | Tathlum | C | CROSSBOW | lrhand | 2H (lrhand) | 176 | 76 |
| 9260 | Ballista | C | CROSSBOW | lrhand | 2H (lrhand) | 155 | 68 |
| 9256 | Ballista | C | CROSSBOW | lrhand | 2H (lrhand) | 155 | 68 |
| 9236 | Arbalest | C | CROSSBOW | lrhand | 2H (lrhand) | 135 | 61 |
| 228 | Crystal Dagger | C | DAGGER | rhand | 1H (rhand) | 136 | 83 |
| 231 | Grace Dagger | C | DAGGER | rhand | 1H (rhand) | 122 | 76 |
| 233 | Dark Screamer | C | DAGGER | rhand | 1H (rhand) | 122 | 76 |
| 227 | Stiletto | C | DAGGER | rhand | 1H (rhand) | 107 | 68 |
| 230 | Wolverine Needle | C | DAGGER | rhand | 1H (rhand) | 94 | 61 |
| 226 | Cursed Dagger | C | DAGGER | rhand | 1H (rhand) | 94 | 61 |
| 232 | Dark Elven Dagger | C | DAGGER | rhand | 1H (rhand) | 94 | 61 |
| 242 | Soulfire Dirk | C | DAGGER | rhand | 1H (rhand) | 86 | 91 |
| 2574 | Shamshir*Spirit Sword | C | DUAL | lrhand | dual (lrhand) | 190 | 83 |
| 2575 | Shamshir*Raid Sword | C | DUAL | lrhand | dual (lrhand) | 190 | 83 |
| 2572 | Shamshir*Shamshir | C | DUAL | lrhand | dual (lrhand) | 190 | 83 |
| 2573 | Shamshir*Katana | C | DUAL | lrhand | dual (lrhand) | 190 | 83 |
| 2591 | Spirit Sword*Spirit Sword | C | DUAL | lrhand | dual (lrhand) | 190 | 83 |
| 2592 | Spirit Sword*Raid Sword | C | DUAL | lrhand | dual (lrhand) | 190 | 83 |
| 2599 | Raid Sword*Raid Sword | C | DUAL | lrhand | dual (lrhand) | 190 | 83 |
| 2582 | Katana*Katana | C | DUAL | lrhand | dual (lrhand) | 190 | 83 |
| 2583 | Katana*Spirit Sword | C | DUAL | lrhand | dual (lrhand) | 190 | 83 |
| 2584 | Katana*Raid Sword | C | DUAL | lrhand | dual (lrhand) | 190 | 83 |
| 2562 | Stormbringer*Shamshir | C | DUAL | lrhand | dual (lrhand) | 183 | 81 |
| 2564 | Stormbringer*Spirit Sword | C | DUAL | lrhand | dual (lrhand) | 183 | 81 |
| 2565 | Stormbringer*Raid Sword | C | DUAL | lrhand | dual (lrhand) | 183 | 81 |
| 2563 | Stormbringer*Katana | C | DUAL | lrhand | dual (lrhand) | 183 | 81 |
| 2561 | Stormbringer*Stormbringer | C | DUAL | lrhand | dual (lrhand) | 175 | 78 |
| 2560 | Elven Long Sword*Elven Long Sword | C | DUAL | lrhand | dual (lrhand) | 162 | 73 |
| 2559 | Sword of Revolution*Elven Long Sword | C | DUAL | lrhand | dual (lrhand) | 155 | 70 |
| 2557 | Elven Sword*Elven Long Sword | C | DUAL | lrhand | dual (lrhand) | 148 | 68 |
| 2554 | Crimson Sword*Elven Long Sword | C | DUAL | lrhand | dual (lrhand) | 148 | 68 |
| 2558 | Sword of Revolution*Sword of Revolution | C | DUAL | lrhand | dual (lrhand) | 148 | 68 |
| 2553 | Crimson Sword*Sword of Revolution | C | DUAL | lrhand | dual (lrhand) | 136 | 63 |
| 2545 | Artisan's Sword*Elven Long Sword | C | DUAL | lrhand | dual (lrhand) | 136 | 63 |
| 2539 | Spinebone Sword*Elven Long Sword | C | DUAL | lrhand | dual (lrhand) | 136 | 63 |
| 2550 | Knight's Sword*Elven Long Sword | C | DUAL | lrhand | dual (lrhand) | 136 | 63 |
| 2532 | Bastard Sword*Elven Long Sword | C | DUAL | lrhand | dual (lrhand) | 136 | 63 |
| 2556 | Elven Sword*Sword of Revolution | C | DUAL | lrhand | dual (lrhand) | 136 | 63 |
| 2524 | Saber*Elven Long Sword | C | DUAL | lrhand | dual (lrhand) | 130 | 61 |
| 2538 | Spinebone Sword*Sword of Revolution | C | DUAL | lrhand | dual (lrhand) | 124 | 58 |
| 2544 | Artisan's Sword*Sword of Revolution | C | DUAL | lrhand | dual (lrhand) | 124 | 58 |
| 2549 | Knight's Sword*Sword of Revolution | C | DUAL | lrhand | dual (lrhand) | 124 | 58 |
| 2531 | Bastard Sword*Sword of Revolution | C | DUAL | lrhand | dual (lrhand) | 124 | 58 |
| 2523 | Saber*Sword of Revolution | C | DUAL | lrhand | dual (lrhand) | 118 | 56 |
| 2551 | Crimson Sword*Crimson Sword | C | DUAL | lrhand | dual (lrhand) | 118 | 56 |
| 2552 | Crimson Sword*Elven Sword | C | DUAL | lrhand | dual (lrhand) | 118 | 56 |
| 2555 | Elven Sword*Elven Sword | C | DUAL | lrhand | dual (lrhand) | 118 | 56 |
| 266 | Great Pata | C | DUALFIST | lrhand | dual fist (lrhand) | 190 | 83 |
| 265 | Fisted Blade | C | DUALFIST | lrhand | dual fist (lrhand) | 169 | 76 |
| 4233 | Knuckle Duster | C | DUALFIST | lrhand | dual fist (lrhand) | 148 | 68 |
| 263 | Chakram | C | DUALFIST | lrhand | dual fist (lrhand) | 130 | 61 |
| 328 | Candle of Wisdom | C | ETC | rhand | 1H (rhand) | 125 | 111 |
| 331 | Cerberus Eye | C | ETC | rhand | 1H (rhand) | 125 | 111 |
| 332 | Scroll of Destruction | C | ETC | rhand | 1H (rhand) | 125 | 111 |
| 334 | Three Eyed Crow's Feather | C | ETC | rhand | 1H (rhand) | 125 | 111 |
| 330 | Phoenix Feather | C | ETC | rhand | 1H (rhand) | 125 | 111 |
| 329 | Blessed Branch | C | ETC | rhand | 1H (rhand) | 125 | 111 |
| 333 | Claws of Black Dragon | C | ETC | rhand | 1H (rhand) | 125 | 111 |
| 327 | Hex Doll | C | ETC | rhand | 1H (rhand) | 111 | 101 |
| 326 | Heathen's Book | C | ETC | rhand | 1H (rhand) | 111 | 101 |
| 324 | Tears of Fairy | C | ETC | rhand | 1H (rhand) | 98 | 91 |
| 325 | Horn of Glory | C | ETC | rhand | 1H (rhand) | 98 | 91 |
| 6531 | Pelican Rod | C | FISHINGROD | lrhand | 2H (lrhand) | 1 | 1 |
| 299 | Orcish Poleaxe | C | POLE | lrhand | 2H (lrhand) | 156 | 83 |
| 303 | Widow Maker | C | POLE | lrhand | 2H (lrhand) | 144 | 78 |
| 301 | Scorpion | C | POLE | lrhand | 2H (lrhand) | 144 | 78 |
| 95 | Poleaxe | C | POLE | lrhand | 2H (lrhand) | 139 | 76 |
| 94 | Bec de Corbin | C | POLE | lrhand | 2H (lrhand) | 122 | 68 |
| 302 | Body Slasher | C | POLE | lrhand | 2H (lrhand) | 107 | 61 |
| 298 | Orcish Glaive | C | POLE | lrhand | 2H (lrhand) | 107 | 61 |
| 96 | Scythe | C | POLE | lrhand | 2H (lrhand) | 107 | 61 |
| 9292 | Admiral's Estoc | C | RAPIER | rhand | 1H (rhand) | 141 | 83 |
| 9264 | Blinzlasher | C | RAPIER | rhand | 1H (rhand) | 126 | 76 |
| 9280 | Blinzlasher | C | RAPIER | rhand | 1H (rhand) | 126 | 76 |
| 9276 | Blinzlasher | C | RAPIER | rhand | 1H (rhand) | 126 | 76 |
| 9268 | Blinzlasher | C | RAPIER | rhand | 1H (rhand) | 126 | 76 |
| 9272 | Blinzlasher | C | RAPIER | rhand | 1H (rhand) | 126 | 76 |
| 9240 | Chevalier Rapier | C | RAPIER | rhand | 1H (rhand) | 111 | 68 |
| 9244 | Chevalier Rapier | C | RAPIER | rhand | 1H (rhand) | 111 | 68 |
| 9252 | Chevalier Rapier | C | RAPIER | rhand | 1H (rhand) | 111 | 68 |
| 9248 | Chevalier Rapier | C | RAPIER | rhand | 1H (rhand) | 111 | 68 |
| 9228 | Soldat Estoc | C | RAPIER | rhand | 1H (rhand) | 97 | 61 |
| 5286 | Berserker Blade | C | SWORD | lrhand | 2H (lrhand) | 190 | 83 |
| 7882 | Pa'agrian Sword | C | SWORD | lrhand | 2H (lrhand) | 169 | 76 |
| 5801 | For NPC (Dusk) | C | SWORD | rhand | 1H (rhand) | 156 | 83 |
| 5802 | For NPC (Dawn) | C | SWORD | rhand | 1H (rhand) | 156 | 83 |
| 5797 | Tomb Savant A | C | SWORD | rhand | 1H (rhand) | 156 | 83 |
| 5800 | Nephilim Lord | C | SWORD | rhand | 1H (rhand) | 156 | 83 |
| 5794 | Tomb Savant B | C | SWORD | rhand | 1H (rhand) | 156 | 83 |
| 5793 | Tomb Savant A | C | SWORD | rhand | 1H (rhand) | 156 | 83 |
| 5798 | Tomb Savant B | C | SWORD | rhand | 1H (rhand) | 156 | 83 |
| 9136 | Sword of Valakas (1-Handed) | C | SWORD | rhand | 1H (rhand) | 156 | 83 |
| 135 | Samurai Longsword | C | SWORD | rhand | 1H (rhand) | 156 | 83 |
| 134 | Sword of Nightmare | C | SWORD | rhand | 1H (rhand) | 139 | 76 |
| 77 | Tsurugi | C | SWORD | rhand | 1H (rhand) | 139 | 76 |
| 132 | Sword of Limit | C | SWORD | rhand | 1H (rhand) | 139 | 76 |
| 76 | Sword of Delusion | C | SWORD | rhand | 1H (rhand) | 139 | 76 |
| 75 | Caliburs | C | SWORD | rhand | 1H (rhand) | 139 | 76 |
| 71 | Flamberge | C | SWORD | lrhand | 2H (lrhand) | 130 | 61 |
| 7888 | Ecliptic Sword | C | SWORD | rhand | 1H (rhand) | 125 | 111 |
| 133 | Raid Sword | C | SWORD | rhand | 1H (rhand) | 122 | 68 |
| 74 | Katana | C | SWORD | rhand | 1H (rhand) | 122 | 68 |
| 131 | Spirit Sword | C | SWORD | rhand | 1H (rhand) | 122 | 68 |
| 73 | Shamshir | C | SWORD | rhand | 1H (rhand) | 122 | 68 |
| 84 | Homunkulus's Sword | C | SWORD | rhand | 1H (rhand) | 111 | 101 |
| 145 | Sword of Whispering Death | C | SWORD | rhand | 1H (rhand) | 111 | 101 |
| 72 | Stormbringer | C | SWORD | rhand | 1H (rhand) | 107 | 61 |
| 7887 | Mysterious Sword | C | SWORD | rhand | 1H (rhand) | 85 | 81 |

### Jewelry

Pieces: 15.

| ID | Name | Grade | Slot |
|----|------|-------|------|
| 119 | Necklace of Seal | C | neck |
| 915 | Aquastone Necklace | C | neck |
| 916 | Necklace of Protection | C | neck |
| 917 | Necklace of Mermaid | C | neck |
| 919 | Blessed Necklace | C | neck |
| 852 | Moonstone Earring | C | rear;lear |
| 853 | Earring of Protection | C | rear;lear |
| 854 | Earring of Seal | C | rear;lear |
| 855 | Nassen's Earring | C | rear;lear |
| 857 | Blessed Earring | C | rear;lear |
| 883 | Aquastone Ring | C | rfinger;lfinger |
| 884 | Ring of Protection | C | rfinger;lfinger |
| 885 | Ring of Ages | C | rfinger;lfinger |
| 886 | Ring of Seal | C | rfinger;lfinger |
| 888 | Blessed Ring | C | rfinger;lfinger |

## 7. B-Grade Equipment

### Armor

Pieces: 287 (slot-relevant armor for this grade, noise variants excluded).

| ID | Name | Grade | Armor type | Slot | Set membership |
|----|------|-------|------------|------|----------------|
| 556 | Wolf Boots | B | (none) | feet | (not in a set) |
| 557 | Shining Dragon Boots | B | (none) | feet | (not in a set) |
| 558 | Boots of Victory | B | (none) | feet | (not in a set) |
| 559 | Boots of Valor | B | (none) | feet | (not in a set) |
| 560 | Glorious Boots | B | (none) | feet | (not in a set) |
| 562 | Elven Crystal Boots | B | (none) | feet | (not in a set) |
| 564 | Implosion Boots | B | (none) | feet | (not in a set) |
| 565 | Dark Dragon Boots | B | (none) | feet | (not in a set) |
| 566 | Elven Vagian Boots | B | (none) | feet | (not in a set) |
| 567 | Dark Vagian Boots | B | (none) | feet | (not in a set) |
| 568 | _ | B | (none) | feet | (not in a set) |
| 569 | Hell Boots | B | (none) | feet | (not in a set) |
| 570 | Art of Boots | B | (none) | feet | (not in a set) |
| 571 | Masterpiece Boots | B | (none) | feet | (not in a set) |
| 572 | Boots of Silence | B | (none) | feet | (not in a set) |
| 574 | Prairie Boots | B | (none) | feet | (not in a set) |
| 576 | Boots of Concentration | B | (none) | feet | (not in a set) |
| 577 | Ace's Boots | B | (none) | feet | (not in a set) |
| 578 | Guardian's Boots | B | (none) | feet | (not in a set) |
| 579 | Marksman Boots | B | (none) | feet | (not in a set) |
| 580 | Boots of Mana | B | (none) | feet | (not in a set) |
| 581 | Sage's Boots | B | (none) | feet | (not in a set) |
| 582 | Paradia Boots | B | (none) | feet | (not in a set) |
| 584 | Boots of Solar Eclipse | B | (none) | feet | (not in a set) |
| 585 | Boots of Black Ore | B | (none) | feet | (not in a set) |
| 586 | Boots of Summoning | B | (none) | feet | (not in a set) |
| 587 | Otherworldly Boots | B | (none) | feet | (not in a set) |
| 588 | Elemental Boots | B | (none) | feet | (not in a set) |
| 590 | Boots of Grace | B | (none) | feet | (not in a set) |
| 591 | Boots of Holy Spirit | B | (none) | feet | (not in a set) |
| 594 | Boots of Aid | B | (none) | feet | (not in a set) |
| 595 | Boots of Blessing | B | (none) | feet | (not in a set) |
| 596 | Flame Boots | B | (none) | feet | (not in a set) |
| 597 | Boots of Bravery | B | (none) | feet | (not in a set) |
| 599 | Absolute Boots | B | (none) | feet | (not in a set) |
| 602 | Boots of Pledge | B | (none) | feet | (not in a set) |
| 5726 | Zubei's Boots - Heavy Armor | B | (none) | feet | b_grade.xml#25 |
| 5727 | Zubei's Boots - Light Armor | B | (none) | feet | b_grade.xml#27 |
| 5728 | Zubei's Boots - Robe | B | (none) | feet | b_grade.xml#29 |
| 5730 | Avadon Boots - Heavy Armor | B | (none) | feet | b_grade.xml#26 |
| 5731 | Avadon Boots - Light Armor | B | (none) | feet | b_grade.xml#28 |
| 5732 | Avadon Boots - Robe | B | (none) | feet | b_grade.xml#30 |
| 5734 | Blue Wolf Boots - Heavy Armor | B | (none) | feet | b_grade.xml#32 |
| 5735 | Blue Wolf Boots - Light Armor | B | (none) | feet | b_grade.xml#34 |
| 5736 | Blue Wolf Boots - Robe | B | (none) | feet | b_grade.xml#36 |
| 5738 | Doom Boots - Heavy Armor | B | (none) | feet | b_grade.xml#33 |
| 5739 | Doom Boots - Light Armor | B | (none) | feet | b_grade.xml#35 |
| 5740 | Doom Boots - Robe | B | (none) | feet | b_grade.xml#37 |
| 11359 | Zubei's Boots - Heavy Armor | B | (none) | feet | b_grade.xml#25 |
| 11360 | Zubei's Boots - Light Armor Use | B | (none) | feet | b_grade.xml#27 |
| 11361 | Zubei's Boots - Robe | B | (none) | feet | b_grade.xml#29 |
| 11370 | Avadon Boots - Heavy Armor | B | (none) | feet | b_grade.xml#26 |
| 11371 | Avadon Boots - Light Armor Use | B | (none) | feet | b_grade.xml#28 |
| 11372 | Avadon Boots - Robe | B | (none) | feet | b_grade.xml#30 |
| 11382 | Doom Boots - Heavy Armor | B | (none) | feet | b_grade.xml#33 |
| 11383 | Doom Boots - Light Armor Use | B | (none) | feet | b_grade.xml#35 |
| 11384 | Doom Boots - Robe | B | (none) | feet | b_grade.xml#37 |
| 11396 | Blue Wolf Boots - Heavy Armor | B | (none) | feet | b_grade.xml#32 |
| 11397 | Blue Wolf Boots - Light Armor Use | B | (none) | feet | b_grade.xml#34 |
| 11398 | Blue Wolf Boots - Robe | B | (none) | feet | b_grade.xml#36 |
| 610 | Saint Knight's Gauntlets | B | (none) | gloves | (not in a set) |
| 611 | Soul Leech Gauntlets | B | (none) | gloves | (not in a set) |
| 2465 | Chain Gloves of Silence | B | (none) | gloves | (not in a set) |
| 2466 | Guardian's Gloves | B | (none) | gloves | (not in a set) |
| 2467 | Gloves of Blessing | B | (none) | gloves | (not in a set) |
| 2480 | Elemental Gloves | B | (none) | gloves | (not in a set) |
| 2481 | Gloves of Grace | B | (none) | gloves | (not in a set) |
| 2485 | Implosion Gauntlets | B | (none) | gloves | (not in a set) |
| 2486 | Paradia Gloves | B | (none) | gloves | (not in a set) |
| 5710 | Zubei's Gauntlets - Heavy Armor | B | (none) | gloves | b_grade.xml#25 |
| 5711 | Zubei's Gauntlets - Light Armor | B | (none) | gloves | b_grade.xml#27 |
| 5712 | Zubei's Gauntlets - Robe | B | (none) | gloves | b_grade.xml#29 |
| 5714 | Avadon Gloves - Heavy Armor | B | (none) | gloves | b_grade.xml#26 |
| 5715 | Avadon Gloves - Light Armor | B | (none) | gloves | b_grade.xml#28 |
| 5716 | Avadon Gloves - Robe | B | (none) | gloves | b_grade.xml#30 |
| 5718 | Blue Wolf Gloves - Heavy Armor | B | (none) | gloves | b_grade.xml#32 |
| 5719 | Blue Wolf Gloves - Light Armor | B | (none) | gloves | b_grade.xml#34 |
| 5720 | Blue Wolf Gloves - Robe | B | (none) | gloves | b_grade.xml#36 |
| 5722 | Doom Gloves - Heavy Armor | B | (none) | gloves | b_grade.xml#33 |
| 5723 | Doom Gloves - Light Armor | B | (none) | gloves | b_grade.xml#35 |
| 5724 | Doom Gloves - Robe | B | (none) | gloves | b_grade.xml#37 |
| 11356 | Zubei's Gauntlet - Heavy Armor | B | (none) | gloves | b_grade.xml#25 |
| 11357 | Zubei's Gauntlet - Light Armor Use | B | (none) | gloves | b_grade.xml#27 |
| 11358 | Zubei's Gauntlet - Robe | B | (none) | gloves | b_grade.xml#29 |
| 11365 | Avadon Gloves - Heavy Armor | B | (none) | gloves | b_grade.xml#26 |
| 11366 | Avadon Gloves - Light Armor Use | B | (none) | gloves | b_grade.xml#28 |
| 11367 | Avadon Gloves - Robe | B | (none) | gloves | b_grade.xml#30 |
| 11379 | Doom Gloves - Heavy Armor | B | (none) | gloves | b_grade.xml#33 |
| 11380 | Doom Gloves - Light Armor Use | B | (none) | gloves | b_grade.xml#35 |
| 11381 | Doom Gloves - Robe | B | (none) | gloves | b_grade.xml#37 |
| 11399 | Blue Wolf Gloves - Heavy Armor | B | (none) | gloves | b_grade.xml#32 |
| 11400 | Blue Wolf Gloves - Light Armor Use | B | (none) | gloves | b_grade.xml#34 |
| 11401 | Blue Wolf Gloves - Robe | B | (none) | gloves | b_grade.xml#36 |
| 501 | Armet | B | (none) | head | (not in a set) |
| 503 | Zubei's Helmet | B | (none) | head | b_grade.xml#29 |
| 505 | Wolf Helmet | B | (none) | head | (not in a set) |
| 506 | Shining Dragon Helmet | B | (none) | head | (not in a set) |
| 507 | Helmet of Victory | B | (none) | head | (not in a set) |
| 508 | Helmet of Valor | B | (none) | head | (not in a set) |
| 511 | Elven Crystal Helmet | B | (none) | head | (not in a set) |
| 513 | Implosion Helmet | B | (none) | head | (not in a set) |
| 514 | Dark Dragon Helmet | B | (none) | head | (not in a set) |
| 519 | Art of Helmet | B | (none) | head | (not in a set) |
| 521 | Helmet of Silence | B | (none) | head | (not in a set) |
| 522 | Gust Helmet | B | (none) | head | (not in a set) |
| 523 | Prairie Helmet | B | (none) | head | (not in a set) |
| 524 | Helm of Underworld | B | (none) | head | (not in a set) |
| 525 | Helmet of Concentration | B | (none) | head | (not in a set) |
| 526 | Ace's Helmet | B | (none) | head | (not in a set) |
| 527 | Guardian's Helmet | B | (none) | head | (not in a set) |
| 528 | Marksman Helmet | B | (none) | head | (not in a set) |
| 530 | Sage's Cap | B | (none) | head | (not in a set) |
| 532 | Inferno Hood | B | (none) | head | (not in a set) |
| 534 | Hood of Black Ore | B | (none) | head | (not in a set) |
| 536 | Otherworldly Hood | B | (none) | head | (not in a set) |
| 538 | Hood of Phantom | B | (none) | head | (not in a set) |
| 540 | Hood of Holy Spirit | B | (none) | head | (not in a set) |
| 542 | Cerberus Hood | B | (none) | head | (not in a set) |
| 544 | Hood of Blessing | B | (none) | head | (not in a set) |
| 546 | Helm of Bravery | B | (none) | head | (not in a set) |
| 548 | Absolute Helm | B | (none) | head | (not in a set) |
| 550 | Helm of Doom | B | (none) | head | (not in a set) |
| 552 | Divine Helm | B | (none) | head | (not in a set) |
| 2415 | Avadon Circlet | B | (none) | head | b_grade.xml#30 |
| 2416 | Blue Wolf Helmet | B | (none) | head | b_grade.xml#36 |
| 2417 | Doom Helmet | B | (none) | head | b_grade.xml#37 |
| 11363 | Zubei's Helmet - Heavy Armor | B | (none) | head | b_grade.xml#25 |
| 11373 | Avadon Circlet - Heavy Armor | B | (none) | head | b_grade.xml#26 |
| 11387 | Doom Helmet - Heavy Armor | B | (none) | head | b_grade.xml#33 |
| 11403 | Blue Wolf Helmet - Heavy Armor | B | (none) | head | b_grade.xml#32 |
| 12978 | Zubei's Helmet - Light Armor Use | B | (none) | head | b_grade.xml#27 |
| 12979 | Zubei's Helmet - Robe | B | (none) | head | b_grade.xml#29 |
| 12980 | Avadon Circlet - Light Armor Use | B | (none) | head | b_grade.xml#28 |
| 12981 | Avadon Circlet - Robe | B | (none) | head | b_grade.xml#30 |
| 12982 | Doom Helmet - Light Armor Use | B | (none) | head | b_grade.xml#35 |
| 12983 | Doom Helmet - Robe | B | (none) | head | b_grade.xml#37 |
| 12984 | Blue Wolf Helmet - Light Armor Use | B | (none) | head | b_grade.xml#34 |
| 12985 | Blue Wolf Helmet - Robe | B | (none) | head | b_grade.xml#36 |
| 104 | Shield of Victory | B | (none) | lhand | (not in a set) |
| 105 | Implosion Shield | B | (none) | lhand | (not in a set) |
| 106 | Dark Dragon Shield | B | (none) | lhand | (not in a set) |
| 108 | Masterpiece Shield | B | (none) | lhand | (not in a set) |
| 109 | Shield of Solar Eclipse | B | (none) | lhand | (not in a set) |
| 110 | Doom Shield | B | (none) | lhand | b_grade.xml#33 |
| 111 | Shield of Pledge | B | (none) | lhand | (not in a set) |
| 633 | Zubei's Shield | B | (none) | lhand | (not in a set) |
| 635 | Wolf Shield | B | (none) | lhand | (not in a set) |
| 636 | Shining Dragon Shield | B | (none) | lhand | (not in a set) |
| 637 | Shield of Valor | B | (none) | lhand | (not in a set) |
| 638 | Glorious Shield | B | (none) | lhand | (not in a set) |
| 639 | Red Flame Shield | B | (none) | lhand | (not in a set) |
| 640 | Elven Crystal Shield | B | (none) | lhand | (not in a set) |
| 642 | Elven Vagian Shield | B | (none) | lhand | (not in a set) |
| 643 | Dark Vagian Shield | B | (none) | lhand | (not in a set) |
| 644 | Hell Shield | B | (none) | lhand | (not in a set) |
| 645 | Art of Shield | B | (none) | lhand | (not in a set) |
| 646 | Shield of Silence | B | (none) | lhand | (not in a set) |
| 647 | Gust Shield | B | (none) | lhand | (not in a set) |
| 648 | Prairie Shield | B | (none) | lhand | (not in a set) |
| 649 | Shield of Underworld | B | (none) | lhand | (not in a set) |
| 650 | Shield of Concentration | B | (none) | lhand | (not in a set) |
| 651 | Ace's Shield | B | (none) | lhand | (not in a set) |
| 652 | Guardian's Shield | B | (none) | lhand | (not in a set) |
| 653 | Marksman Shield | B | (none) | lhand | (not in a set) |
| 654 | Shield of Mana | B | (none) | lhand | (not in a set) |
| 655 | Sage's Shield | B | (none) | lhand | (not in a set) |
| 656 | Paradia Shield | B | (none) | lhand | (not in a set) |
| 657 | Inferno Shield | B | (none) | lhand | (not in a set) |
| 658 | Shield of Black Ore | B | (none) | lhand | (not in a set) |
| 659 | Shield of Summoning | B | (none) | lhand | (not in a set) |
| 660 | Otherworldly Shield | B | (none) | lhand | (not in a set) |
| 661 | Elemental Shield | B | (none) | lhand | (not in a set) |
| 662 | Shield of Phantom | B | (none) | lhand | (not in a set) |
| 663 | Shield of Grace | B | (none) | lhand | (not in a set) |
| 664 | Shield of Holy Spirit | B | (none) | lhand | (not in a set) |
| 665 | Phoenix Shield | B | (none) | lhand | (not in a set) |
| 666 | Cerberus Shield | B | (none) | lhand | (not in a set) |
| 667 | Shield of Aid | B | (none) | lhand | (not in a set) |
| 668 | Shield of Blessing | B | (none) | lhand | (not in a set) |
| 669 | Flame Shield | B | (none) | lhand | (not in a set) |
| 670 | Shield of Bravery | B | (none) | lhand | (not in a set) |
| 671 | Blood Shield | B | (none) | lhand | (not in a set) |
| 672 | Absolute Shield | B | (none) | lhand | (not in a set) |
| 673 | Avadon Shield | B | (none) | lhand | b_grade.xml#26 |
| 674 | Divine Shield | B | (none) | lhand | (not in a set) |
| 11362 | Zubei's Shield | B | (none) | lhand | (not in a set) |
| 11374 | Avadon Shield | B | (none) | lhand | b_grade.xml#26 |
| 11385 | Doom Shield | B | (none) | lhand | b_grade.xml#33 |
| 357 | Zubei's Breastplate | B | HEAVY | chest | b_grade.xml#25 |
| 358 | Blue Wolf Breastplate | B | HEAVY | chest | b_grade.xml#32 |
| 360 | Armor of Victory | B | HEAVY | chest | (not in a set) |
| 361 | Breastplate of Valor | B | HEAVY | chest | (not in a set) |
| 364 | Elven Crystal Breastplate | B | HEAVY | chest | (not in a set) |
| 2376 | Avadon Breastplate | B | HEAVY | chest | b_grade.xml#26 |
| 11364 | Zubei's Breastplate | B | HEAVY | chest | (not in a set) |
| 11376 | Avadon Breastplate | B | HEAVY | chest | (not in a set) |
| 11405 | Blue Wolf Breastplate | B | HEAVY | chest | (not in a set) |
| 383 | Zubei's Gaiters | B | HEAVY | legs | b_grade.xml#25 |
| 384 | Wolf Gaiters | B | HEAVY | legs | (not in a set) |
| 385 | Gaiters of Victory | B | HEAVY | legs | (not in a set) |
| 386 | Gaiters of Valor | B | HEAVY | legs | (not in a set) |
| 387 | Elven Crystal Gaiters | B | HEAVY | legs | (not in a set) |
| 2379 | Avadon Gaiters | B | HEAVY | legs | b_grade.xml#26 |
| 2380 | Blue Wolf Gaiters | B | HEAVY | legs | b_grade.xml#32 |
| 11355 | Zubei's Gaiters | B | HEAVY | legs | b_grade.xml#25 |
| 11375 | Avadon Gaiters | B | HEAVY | legs | b_grade.xml#26 |
| 11394 | Blue Wolf Gaiters | B | HEAVY | legs | b_grade.xml#32 |
| 359 | Shining Dragon Armor | B | HEAVY | onepiece | (not in a set) |
| 362 | Glorious Armor | B | HEAVY | onepiece | (not in a set) |
| 363 | Red Flame Armor | B | HEAVY | onepiece | (not in a set) |
| 366 | Implosion Armor | B | HEAVY | onepiece | (not in a set) |
| 367 | Dark Dragon Armor | B | HEAVY | onepiece | (not in a set) |
| 368 | Elven Vagian Armor | B | HEAVY | onepiece | (not in a set) |
| 369 | Dark Vagian Armor | B | HEAVY | onepiece | (not in a set) |
| 370 | Complete Set of Plate Armor | B | HEAVY | onepiece | (not in a set) |
| 371 | Hell Plate | B | HEAVY | onepiece | (not in a set) |
| 372 | Art of Plate | B | HEAVY | onepiece | (not in a set) |
| 373 | Masterpiece Armor | B | HEAVY | onepiece | (not in a set) |
| 2381 | Doom Plate Armor | B | HEAVY | onepiece | b_grade.xml#33 |
| 11386 | Doom Plate Armor | B | HEAVY | onepiece | (not in a set) |
| 404 | Prairie Leather Armor | B | LIGHT | chest | (not in a set) |
| 405 | Leather Armor of Underworld | B | LIGHT | chest | (not in a set) |
| 408 | Guardian's Leather Armor | B | LIGHT | chest | (not in a set) |
| 409 | Marksman's Leather Armor | B | LIGHT | chest | (not in a set) |
| 488 | Tattoo of Flame | B | LIGHT | chest | (not in a set) |
| 493 | Tattoo of Avadon | B | LIGHT | chest | (not in a set) |
| 494 | Tattoo of Doom | B | LIGHT | chest | (not in a set) |
| 496 | Tattoo of Divine | B | LIGHT | chest | (not in a set) |
| 2384 | Zubei's Leather Shirt | B | LIGHT | chest | b_grade.xml#27 |
| 11354 | Zubei's Leather Shirt | B | LIGHT | chest | (not in a set) |
| 421 | Prairie Leather Gaiters | B | LIGHT | legs | (not in a set) |
| 422 | Gaiters of Underworld | B | LIGHT | legs | (not in a set) |
| 423 | Guardian's Leather Gaiters | B | LIGHT | legs | (not in a set) |
| 424 | Marksman's Leather Gaiters | B | LIGHT | legs | (not in a set) |
| 2388 | Zubei's Leather Gaiters | B | LIGHT | legs | b_grade.xml#27 |
| 11353 | Zubei's Leather Gaiters | B | LIGHT | legs | b_grade.xml#27 |
| 402 | Chain Mail of Silence | B | LIGHT | onepiece | (not in a set) |
| 403 | Gust Chain Mail | B | LIGHT | onepiece | (not in a set) |
| 406 | Leather Armor of Concentration | B | LIGHT | onepiece | (not in a set) |
| 407 | Ace's Leather Armor | B | LIGHT | onepiece | (not in a set) |
| 2390 | Avadon Leather Armor | B | LIGHT | onepiece | b_grade.xml#28 |
| 2391 | Blue Wolf Leather Armor | B | LIGHT | onepiece | b_grade.xml#34 |
| 2392 | Leather Armor of Doom | B | LIGHT | onepiece | b_grade.xml#35 |
| 11368 | Avadon Leather Armor | B | LIGHT | onepiece | (not in a set) |
| 11388 | Leather Armor of Doom | B | LIGHT | onepiece | (not in a set) |
| 11395 | Blue Wolf Leather Armor | B | LIGHT | onepiece | (not in a set) |
| 443 | Tunic of Mana | B | MAGIC | chest | (not in a set) |
| 445 | Paradia Tunic | B | MAGIC | chest | (not in a set) |
| 446 | Inferno Tunic | B | MAGIC | chest | (not in a set) |
| 447 | Tunic of Solar Eclipse | B | MAGIC | chest | (not in a set) |
| 449 | Tunic of Summoning | B | MAGIC | chest | (not in a set) |
| 451 | Elemental Tunic | B | MAGIC | chest | (not in a set) |
| 452 | Tunic of Phantom | B | MAGIC | chest | (not in a set) |
| 453 | Tunic of Grace | B | MAGIC | chest | (not in a set) |
| 455 | Phoenix Tunic | B | MAGIC | chest | (not in a set) |
| 456 | Cerberus Tunic | B | MAGIC | chest | (not in a set) |
| 457 | Tunic of Aid | B | MAGIC | chest | (not in a set) |
| 2397 | Tunic of Zubei | B | MAGIC | chest | b_grade.xml#29 |
| 2398 | Blue Wolf Tunic | B | MAGIC | chest | b_grade.xml#36 |
| 2399 | Tunic of Doom | B | MAGIC | chest | b_grade.xml#37 |
| 11377 | Tunic of Zubei | B | MAGIC | chest | (not in a set) |
| 11393 | Tunic of Doom | B | MAGIC | chest | (not in a set) |
| 11402 | Blue Wolf Tunic | B | MAGIC | chest | (not in a set) |
| 474 | Stockings of Mana | B | MAGIC | legs | (not in a set) |
| 475 | Paradia Stockings | B | MAGIC | legs | (not in a set) |
| 476 | Inferno Stockings | B | MAGIC | legs | (not in a set) |
| 477 | Stockings of Solar Eclipse | B | MAGIC | legs | (not in a set) |
| 478 | Stockings of Summoning | B | MAGIC | legs | (not in a set) |
| 479 | Elemental Stockings | B | MAGIC | legs | (not in a set) |
| 480 | Stockings of Phantom | B | MAGIC | legs | (not in a set) |
| 481 | Stockings of Grace | B | MAGIC | legs | (not in a set) |
| 482 | Phoenix Stockings | B | MAGIC | legs | (not in a set) |
| 483 | Cerberus Stockings | B | MAGIC | legs | (not in a set) |
| 484 | Stockings of Aid | B | MAGIC | legs | (not in a set) |
| 2402 | Stockings of Zubei | B | MAGIC | legs | b_grade.xml#29 |
| 2403 | Blue Wolf Stockings | B | MAGIC | legs | b_grade.xml#36 |
| 2404 | Stockings of Doom | B | MAGIC | legs | b_grade.xml#37 |
| 11378 | Stockings of Zubei | B | MAGIC | legs | b_grade.xml#29 |
| 11404 | Blue Wolf Stockings | B | MAGIC | legs | b_grade.xml#36 |
| 11406 | Stockings of Doom | B | MAGIC | legs | b_grade.xml#37 |
| 444 | Sage's Robe | B | MAGIC | onepiece | (not in a set) |
| 448 | Robe of Black Ore | B | MAGIC | onepiece | (not in a set) |
| 450 | Otherworldly Robe | B | MAGIC | onepiece | (not in a set) |
| 454 | Robe of Holy Spirit | B | MAGIC | onepiece | (not in a set) |
| 458 | Robe of Blessing | B | MAGIC | onepiece | (not in a set) |
| 2406 | Avadon Robe | B | MAGIC | onepiece | b_grade.xml#30 |
| 11369 | Avadon Robe | B | MAGIC | onepiece | (not in a set) |

### Weapons

Base weapons: 96 (SA/elemental, event, PvP, common, shadow and fortune variants excluded).

| ID | Name | Grade | Weapon type | Slot | Class | pAtk | mAtk |
|----|------|-------|-------------|------|-------|------|------|
| 9320 | Dismantler | B | ANCIENTSWORD | lrhand | 2H (lrhand) | 210 | 99 |
| 9308 | Innominate Victory | B | ANCIENTSWORD | lrhand | 2H (lrhand) | 190 | 91 |
| 7901 | Star Buster | B | BLUNT | lrhand | 2H (lrhand) | 236 | 99 |
| 7900 | Ice Storm Hammer | B | BLUNT | lrhand | 2H (lrhand) | 213 | 91 |
| 7834 | Art of Battle Axe | B | BLUNT | rhand | 1H (rhand) | 194 | 99 |
| 171 | Deadman's Glory | B | BLUNT | rhand | 1H (rhand) | 194 | 99 |
| 175 | Art of Battle Axe | B | BLUNT | rhand | 1H (rhand) | 194 | 99 |
| 209 | Divine Staff | B | BLUNT | lrhand | 2H (lrhand) | 189 | 132 |
| 211 | Staff of Nobility | B | BLUNT | lrhand | 2H (lrhand) | 189 | 132 |
| 210 | Staff of Evil Spirits | B | BLUNT | lrhand | 2H (lrhand) | 189 | 145 |
| 91 | Heavy War Axe | B | BLUNT | rhand | 1H (rhand) | 175 | 91 |
| 208 | Staff of Seal | B | BLUNT | lrhand | 2H (lrhand) | 170 | 122 |
| 92 | Sprite's Staff | B | BLUNT | lrhand | 2H (lrhand) | 170 | 134 |
| 207 | Staff of Phantom | B | BLUNT | lrhand | 2H (lrhand) | 170 | 122 |
| 7893 | Kaim Vanul's Bones | B | BLUNT | rhand | 1H (rhand) | 155 | 132 |
| 7892 | Spell Breaker | B | BLUNT | rhand | 1H (rhand) | 140 | 122 |
| 287 | Bow of Peril | B | BOW | lrhand | 2H (lrhand) | 400 | 99 |
| 284 | Dark Elven Long Bow | B | BOW | lrhand | 2H (lrhand) | 397 | 100 |
| 9324 | Hell Hound | B | CROSSBOW | lrhand | 2H (lrhand) | 245 | 99 |
| 9312 | Peacemaker | B | CROSSBOW | lrhand | 2H (lrhand) | 221 | 91 |
| 234 | Demon's Dagger | B | DAGGER | rhand | 1H (rhand) | 170 | 99 |
| 229 | Kris | B | DAGGER | rhand | 1H (rhand) | 153 | 91 |
| 243 | Hell Knife | B | DAGGER | rhand | 1H (rhand) | 122 | 122 |
| 2626 | Samurai Long Sword*Samurai Long Sword | B | DUAL | lrhand | dual (lrhand) | 236 | 99 |
| 2623 | Sword of Nightmare*Samurai Long Sword | B | DUAL | lrhand | dual (lrhand) | 228 | 97 |
| 2611 | Caliburs*Samurai Long Sword | B | DUAL | lrhand | dual (lrhand) | 228 | 97 |
| 2620 | Sword of Delusion*Samurai Long Sword | B | DUAL | lrhand | dual (lrhand) | 228 | 97 |
| 2625 | Tsurugi*Samurai Long Sword | B | DUAL | lrhand | dual (lrhand) | 228 | 97 |
| 2616 | Sword of Limit*Samurai Long Sword | B | DUAL | lrhand | dual (lrhand) | 228 | 97 |
| 2605 | Raid Sword*Samurai Long Sword | B | DUAL | lrhand | dual (lrhand) | 220 | 94 |
| 2598 | Spirit Sword*Samurai Long Sword | B | DUAL | lrhand | dual (lrhand) | 220 | 94 |
| 2581 | Shamshir*Samurai Long Sword | B | DUAL | lrhand | dual (lrhand) | 220 | 94 |
| 2590 | Katana*Samurai Long Sword | B | DUAL | lrhand | dual (lrhand) | 220 | 94 |
| 2608 | Caliburs*Sword of Delusion | B | DUAL | lrhand | dual (lrhand) | 213 | 91 |
| 2609 | Caliburs*Sword of Nightmare | B | DUAL | lrhand | dual (lrhand) | 213 | 91 |
| 2622 | Sword of Nightmare*Tsurugi | B | DUAL | lrhand | dual (lrhand) | 213 | 91 |
| 2607 | Caliburs*Sword of Limit | B | DUAL | lrhand | dual (lrhand) | 213 | 91 |
| 2618 | Sword of Delusion*Sword of Nightmare | B | DUAL | lrhand | dual (lrhand) | 213 | 91 |
| 2624 | Tsurugi*Tsurugi | B | DUAL | lrhand | dual (lrhand) | 213 | 91 |
| 2606 | Caliburs*Caliburs | B | DUAL | lrhand | dual (lrhand) | 213 | 91 |
| 2610 | Caliburs*Tsurugi | B | DUAL | lrhand | dual (lrhand) | 213 | 91 |
| 2615 | Sword of Limit*Tsurugi | B | DUAL | lrhand | dual (lrhand) | 213 | 91 |
| 2619 | Sword of Delusion*Tsurugi | B | DUAL | lrhand | dual (lrhand) | 213 | 91 |
| 2617 | Sword of Delusion*Sword of Delusion | B | DUAL | lrhand | dual (lrhand) | 213 | 91 |
| 2614 | Sword of Limit*Sword of Nightmare | B | DUAL | lrhand | dual (lrhand) | 213 | 91 |
| 2621 | Sword of Nightmare*Sword of Nightmare | B | DUAL | lrhand | dual (lrhand) | 213 | 91 |
| 2612 | Sword of Limit*Sword of Limit | B | DUAL | lrhand | dual (lrhand) | 213 | 91 |
| 2613 | Sword of Limit*Sword of Delusion | B | DUAL | lrhand | dual (lrhand) | 213 | 91 |
| 9813 | Orc Officer | B | DUAL | lrhand | dual (lrhand) | 213 | 91 |
| 2571 | Stormbringer*Samurai Long sword | B | DUAL | lrhand | dual (lrhand) | 213 | 91 |
| 2586 | Katana*Sword of Limit | B | DUAL | lrhand | dual (lrhand) | 204 | 89 |
| 2585 | Katana*Caliburs | B | DUAL | lrhand | dual (lrhand) | 204 | 89 |
| 2588 | Katana*Sword of Nightmare | B | DUAL | lrhand | dual (lrhand) | 204 | 89 |
| 2587 | Katana*Sword of Delusion | B | DUAL | lrhand | dual (lrhand) | 204 | 89 |
| 2580 | Shamshir*Tsurugi | B | DUAL | lrhand | dual (lrhand) | 204 | 89 |
| 2578 | Shamshir*Sword of Delusion | B | DUAL | lrhand | dual (lrhand) | 204 | 89 |
| 2577 | Shamshir*Sword of Limit | B | DUAL | lrhand | dual (lrhand) | 204 | 89 |
| 2576 | Shamshir*Caliburs | B | DUAL | lrhand | dual (lrhand) | 204 | 89 |
| 2579 | Shamshir*Sword of Nightmare | B | DUAL | lrhand | dual (lrhand) | 204 | 89 |
| 2589 | Katana*Tsurugi | B | DUAL | lrhand | dual (lrhand) | 204 | 89 |
| 2596 | Spirit Sword*Sword of Nightmare | B | DUAL | lrhand | dual (lrhand) | 204 | 89 |
| 2597 | Spirit Sword*Tsurugi | B | DUAL | lrhand | dual (lrhand) | 204 | 89 |
| 2595 | Spirit Sword*Sword of Delusion | B | DUAL | lrhand | dual (lrhand) | 204 | 89 |
| 2593 | Spirit Sword*Caliburs | B | DUAL | lrhand | dual (lrhand) | 204 | 89 |
| 2594 | Spirit Sword*Sword of Limit | B | DUAL | lrhand | dual (lrhand) | 204 | 89 |
| 2603 | Raid Sword*Sword of Nightmare | B | DUAL | lrhand | dual (lrhand) | 204 | 89 |
| 2604 | Raid Sword*Tsurugi | B | DUAL | lrhand | dual (lrhand) | 204 | 89 |
| 2602 | Raid Sword*Sword of Delusion | B | DUAL | lrhand | dual (lrhand) | 204 | 89 |
| 2600 | Raid Sword*Caliburs | B | DUAL | lrhand | dual (lrhand) | 204 | 89 |
| 2601 | Raid Sword*Sword of Limit | B | DUAL | lrhand | dual (lrhand) | 204 | 89 |
| 2567 | Stormbringer*Sword of Limit | B | DUAL | lrhand | dual (lrhand) | 197 | 86 |
| 2566 | Stormbringer*Caliburs | B | DUAL | lrhand | dual (lrhand) | 197 | 86 |
| 2568 | Stormbringer*Sword of Delusion | B | DUAL | lrhand | dual (lrhand) | 197 | 86 |
| 2570 | Stormbringer*Tsurugi | B | DUAL | lrhand | dual (lrhand) | 197 | 86 |
| 2569 | Stormbringer*Sword of Nightmare | B | DUAL | lrhand | dual (lrhand) | 197 | 86 |
| 268 | Bellion Cestus | B | DUALFIST | lrhand | dual fist (lrhand) | 236 | 99 |
| 267 | Arthro Nail | B | DUALFIST | lrhand | dual fist (lrhand) | 213 | 91 |
| 264 | Pata | B | DUALFIST | lrhand | dual fist (lrhand) | 204 | 89 |
| 337 | Scroll of Massacre | B | ETC | rhand | 1H (rhand) | 170 | 143 |
| 336 | Scroll of Mana | B | ETC | rhand | 1H (rhand) | 170 | 143 |
| 338 | Wyvern's Skull | B | ETC | rhand | 1H (rhand) | 170 | 143 |
| 339 | Blood Crystal | B | ETC | rhand | 1H (rhand) | 170 | 143 |
| 340 | Unicorn's Horn | B | ETC | rhand | 1H (rhand) | 170 | 143 |
| 335 | Soul Crystal | B | ETC | rhand | 1H (rhand) | 155 | 132 |
| 6532 | KingFisher Rod | B | FISHINGROD | lrhand | 2H (lrhand) | 1 | 1 |
| 97 | Lance | B | POLE | lrhand | 2H (lrhand) | 194 | 99 |
| 300 | Great Axe | B | POLE | lrhand | 2H (lrhand) | 175 | 91 |
| 9316 | Colichemarde | B | RAPIER | rhand | 1H (rhand) | 176 | 99 |
| 9304 | Military Fleuret | B | RAPIER | rhand | 1H (rhand) | 159 | 91 |
| 7883 | Guardian Sword | B | SWORD | lrhand | 2H (lrhand) | 236 | 99 |
| 78 | Great Sword | B | SWORD | lrhand | 2H (lrhand) | 213 | 91 |
| 79 | Sword of Damascus | B | SWORD | rhand | 1H (rhand) | 194 | 99 |
| 142 | Keshanberk | B | SWORD | rhand | 1H (rhand) | 175 | 91 |
| 7889 | Wizard's Tear | B | SWORD | rhand | 1H (rhand) | 155 | 132 |
| 146 | Ghoulbane | B | SWORD | rhand | 1H (rhand) | 140 | 122 |
| 148 | Sword of Valhalla | B | SWORD | rhand | 1H (rhand) | 140 | 122 |

### Jewelry

Pieces: 48.

| ID | Name | Grade | Slot |
|----|------|-------|------|
| 918 | Adamantite Necklace | B | neck |
| 921 | Necklace of Mana | B | neck |
| 922 | Sage's Necklace | B | neck |
| 923 | Paradia Necklace | B | neck |
| 925 | Necklace of Solar Eclipse | B | neck |
| 926 | Necklace of Black Ore | B | neck |
| 927 | Necklace of Summoning | B | neck |
| 928 | Otherworldly Necklace | B | neck |
| 929 | Elemental Necklace | B | neck |
| 931 | Necklace of Grace | B | neck |
| 932 | Necklace of Holy Spirit | B | neck |
| 935 | Necklace of Aid | B | neck |
| 936 | Necklace of Blessing | B | neck |
| 11576 | Adamantite Necklace | B | neck |
| 11579 | Necklace of Black Ore | B | neck |
| 856 | Adamantite Earring | B | rear;lear |
| 859 | Earring of Mana | B | rear;lear |
| 860 | Sage's Earring | B | rear;lear |
| 861 | Paradia Earring | B | rear;lear |
| 863 | Earring of Solar Eclipse | B | rear;lear |
| 864 | Earring of Black Ore | B | rear;lear |
| 865 | Earring of Summoning | B | rear;lear |
| 866 | Otherworldly Earring | B | rear;lear |
| 867 | Elemental Earring | B | rear;lear |
| 869 | Earring of Grace | B | rear;lear |
| 870 | Earring of Holy Spirit | B | rear;lear |
| 873 | Earring of Aid | B | rear;lear |
| 874 | Earring of Blessing | B | rear;lear |
| 11575 | Adamantite Earring | B | rear;lear |
| 11578 | Earring of Black Ore | B | rear;lear |
| 117 | Ring of Mana | B | rfinger;lfinger |
| 887 | Adamantite Ring | B | rfinger;lfinger |
| 891 | Sage's Ring | B | rfinger;lfinger |
| 892 | Paradia Ring | B | rfinger;lfinger |
| 894 | Ring of Solar Eclipse | B | rfinger;lfinger |
| 895 | Ring of Black Ore | B | rfinger;lfinger |
| 896 | Ring of Summoning | B | rfinger;lfinger |
| 897 | Otherworldly Ring | B | rfinger;lfinger |
| 898 | Elemental Ring | B | rfinger;lfinger |
| 900 | Ring of Grace | B | rfinger;lfinger |
| 901 | Ring of Holy Spirit | B | rfinger;lfinger |
| 904 | Ring of Aid | B | rfinger;lfinger |
| 905 | Ring of Blessing | B | rfinger;lfinger |
| 6660 | Ring of Queen Ant | B | rfinger;lfinger |
| 9677 | Ring of Wind Mastery | B | rfinger;lfinger |
| 11577 | Adamantite Ring | B | rfinger;lfinger |
| 11580 | Ring of Black Ore | B | rfinger;lfinger |
| 22174 | Improved Ring of Queen Ant | B | rfinger;lfinger |

## 8. A-Grade Equipment

### Armor

Pieces: 167 (slot-relevant armor for this grade, noise variants excluded).

| ID | Name | Grade | Armor type | Slot | Set membership |
|----|------|-------|------------|------|----------------|
| 555 | Dragon Boots | A | (none) | feet | (not in a set) |
| 561 | Red Flame Boots | A | (none) | feet | (not in a set) |
| 563 | Dark Crystal Boots | A | (none) | feet | (not in a set) |
| 573 | Gust Boots | A | (none) | feet | (not in a set) |
| 575 | Boots of Underworld | A | (none) | feet | (not in a set) |
| 583 | Majestic Boots | A | (none) | feet | (not in a set) |
| 589 | Boots of Phantom | A | (none) | feet | (not in a set) |
| 592 | Phoenix Boots | A | (none) | feet | (not in a set) |
| 593 | Cerberus Boots | A | (none) | feet | (not in a set) |
| 598 | Blood Boots | A | (none) | feet | (not in a set) |
| 2440 | Boots of Nightmare | A | (none) | feet | (not in a set) |
| 2441 | Dark Legion Boots | A | (none) | feet | (not in a set) |
| 2442 | Dasparion's Boots | A | (none) | feet | (not in a set) |
| 2445 | Dragon Scale Boots | A | (none) | feet | (not in a set) |
| 5777 | Dark Crystal Boots - Heavy Armor | A | (none) | feet | a_grade.xml#39 |
| 5778 | Dark Crystal Boots - Light Armor | A | (none) | feet | a_grade.xml#41 |
| 5779 | Dark Crystal Boots - Robe | A | (none) | feet | a_grade.xml#44 |
| 5780 | Tallum Boots - Heavy Armor | A | (none) | feet | a_grade.xml#40 |
| 5781 | Tallum Boots - Light Armor | A | (none) | feet | a_grade.xml#42 |
| 5782 | Tallum Boots - Robe | A | (none) | feet | a_grade.xml#43 |
| 5783 | Boots of Nightmare - Heavy Armor | A | (none) | feet | a_grade_pvp.xml#103 |
| 5784 | Boots of Nightmare - Light Armor | A | (none) | feet | a_grade_pvp.xml#105 |
| 5785 | Boots of Nightmare - Robe | A | (none) | feet | a_grade_pvp.xml#107 |
| 5786 | Majestic Boots - Heavy Armor | A | (none) | feet | a_grade_pvp.xml#104 |
| 5787 | Majestic Boots - Light Armor | A | (none) | feet | a_grade_pvp.xml#106 |
| 5788 | Majestic Boots - Robe | A | (none) | feet | a_grade_pvp.xml#108 |
| 7863 | Apella Solleret - Heavy Armor | A | (none) | feet | clan.xml#62 |
| 7866 | Apella Boots - Light Armor | A | (none) | feet | clan.xml#63 |
| 7869 | Apella Sandals - Robe | A | (none) | feet | clan.xml#64 |
| 9833 | Improved Apella Solleret - Heavy Armor | A | (none) | feet | clan.xml#78 |
| 9836 | Improved Apella Boots - Light Armor | A | (none) | feet | clan.xml#79 |
| 9839 | Improved Apella Sandals - Robe | A | (none) | feet | clan.xml#80 |
| 11413 | Dark Crystal Boots - Heavy Armor | A | (none) | feet | a_grade.xml#39 |
| 11414 | Dark Crystal Boots - Light Armor Use | A | (none) | feet | a_grade.xml#41 |
| 11415 | Dark Crystal Boots - Robe | A | (none) | feet | a_grade.xml#44 |
| 11441 | Tallum Boots - Heavy Armor | A | (none) | feet | a_grade.xml#40 |
| 11442 | Tallum Boots - Light Armor Use | A | (none) | feet | a_grade.xml#42 |
| 11443 | Tallum Boots - Robe | A | (none) | feet | a_grade.xml#43 |
| 11453 | Majestic Boots - Heavy Armor | A | (none) | feet | a_grade_pvp.xml#104 |
| 11454 | Majestic Boots - Light Armor Use | A | (none) | feet | a_grade_pvp.xml#106 |
| 11455 | Majestic Boots - Robe | A | (none) | feet | a_grade_pvp.xml#108 |
| 11477 | Boots of Nightmare - Heavy Armor | A | (none) | feet | a_grade_pvp.xml#103 |
| 11478 | Boots of Nightmare - Light Armor Use | A | (none) | feet | a_grade_pvp.xml#105 |
| 11479 | Boots of Nightmare - Robe | A | (none) | feet | a_grade_pvp.xml#107 |
| 14585 | Apella Combat Boots - Heavy Armor | A | (none) | feet | clan.xml#155 |
| 14588 | Apella Combat Shoes - Tans Armor Use | A | (none) | feet | clan.xml#156 |
| 14591 | Apella Combat Sandals - Robe | A | (none) | feet | clan.xml#157 |
| 613 | Sand Dragon Gloves | A | (none) | gloves | (not in a set) |
| 2469 | Gloves of Underworld | A | (none) | gloves | (not in a set) |
| 2470 | Gloves of Phantom | A | (none) | gloves | (not in a set) |
| 2471 | Dark Legion Gloves | A | (none) | gloves | (not in a set) |
| 2472 | Dark Crystal Gloves | A | (none) | gloves | (not in a set) |
| 2474 | Dasparion's Gloves | A | (none) | gloves | (not in a set) |
| 2478 | Tallum Gloves | A | (none) | gloves | (not in a set) |
| 2479 | Gauntlets of Nightmare | A | (none) | gloves | (not in a set) |
| 2482 | Majestic Gauntlets | A | (none) | gloves | (not in a set) |
| 2483 | Gust Bracer | A | (none) | gloves | (not in a set) |
| 2484 | Cerberus Gloves | A | (none) | gloves | (not in a set) |
| 2488 | Phoenix Gloves | A | (none) | gloves | (not in a set) |
| 2489 | Gloves of Black Ore | A | (none) | gloves | (not in a set) |
| 5765 | Dark Crystal Gloves - Heavy Armor | A | (none) | gloves | a_grade.xml#39 |
| 5766 | Dark Crystal Gloves - Light Armor | A | (none) | gloves | a_grade.xml#41 |
| 5767 | Dark Crystal Gloves - Robe | A | (none) | gloves | a_grade.xml#44 |
| 5768 | Tallum Gloves - Heavy Armor | A | (none) | gloves | a_grade.xml#40 |
| 5769 | Tallum Gloves - Light Armor | A | (none) | gloves | a_grade.xml#42 |
| 5770 | Tallum Gloves - Robe | A | (none) | gloves | a_grade.xml#43 |
| 5771 | Gauntlets of Nightmare - Heavy Armor | A | (none) | gloves | a_grade_pvp.xml#103 |
| 5772 | Gauntlets of Nightmare - Light Armor | A | (none) | gloves | a_grade_pvp.xml#105 |
| 5773 | Gauntlets of Nightmare - Robe | A | (none) | gloves | a_grade_pvp.xml#107 |
| 5774 | Majestic Gauntlets - Heavy Armor | A | (none) | gloves | a_grade_pvp.xml#104 |
| 5775 | Majestic Gauntlets - Light Armor | A | (none) | gloves | a_grade_pvp.xml#106 |
| 5776 | Majestic Gauntlets - Robe | A | (none) | gloves | a_grade_pvp.xml#108 |
| 7862 | Apella Gauntlet - Heavy Armor | A | (none) | gloves | clan.xml#62 |
| 7865 | Apella Leather Gloves - Light Armor | A | (none) | gloves | clan.xml#63 |
| 7868 | Apella Silk Gloves - Robe | A | (none) | gloves | clan.xml#64 |
| 9832 | Improved Apella Gauntlet - Heavy Armor | A | (none) | gloves | clan.xml#78 |
| 9835 | Improved Apella Leather Gloves - Light Armor | A | (none) | gloves | clan.xml#79 |
| 9838 | Improved Apella Silk Gloves - Robe | A | (none) | gloves | clan.xml#80 |
| 11408 | Dark Crystal Gloves - Heavy Armor | A | (none) | gloves | a_grade.xml#39 |
| 11409 | Dark Crystal Gloves - Light Armor Use | A | (none) | gloves | a_grade.xml#41 |
| 11410 | Dark Crystal Gloves - Robe | A | (none) | gloves | a_grade.xml#44 |
| 11437 | Tallum Gloves - Heavy Armor | A | (none) | gloves | a_grade.xml#40 |
| 11438 | Tallum Gloves - Light Armor Use | A | (none) | gloves | a_grade.xml#42 |
| 11439 | Tallum Gloves - Robe | A | (none) | gloves | a_grade.xml#43 |
| 11448 | Majestic Gauntlet - Heavy Armor | A | (none) | gloves | a_grade_pvp.xml#104 |
| 11449 | Majestic Gauntlet - Light Armor Use | A | (none) | gloves | a_grade_pvp.xml#106 |
| 11450 | Majestic Gauntlet - Robe | A | (none) | gloves | a_grade_pvp.xml#108 |
| 11472 | Gauntlet of Nightmare - Heavy Armor | A | (none) | gloves | a_grade_pvp.xml#103 |
| 11473 | Gauntlet of Nightmare - Light Armor Use | A | (none) | gloves | a_grade_pvp.xml#105 |
| 11474 | Gauntlet of Nightmare - Robe | A | (none) | gloves | a_grade_pvp.xml#107 |
| 14584 | Apella Combat Gauntlet - Heavy Armor | A | (none) | gloves | clan.xml#155 |
| 14587 | Apella Combat Leather Gloves - Tans Armor Use | A | (none) | gloves | clan.xml#156 |
| 14590 | Apella Combat Silk Gloves - Robe | A | (none) | gloves | clan.xml#157 |
| 502 | Close Helmet | A | (none) | head | (not in a set) |
| 509 | Glorious Helmet | A | (none) | head | (not in a set) |
| 510 | Red Flame Helmet | A | (none) | head | (not in a set) |
| 512 | Dark Crystal Helmet | A | (none) | head | a_grade.xml#44 |
| 515 | Elven Vagian Helm | A | (none) | head | (not in a set) |
| 516 | Dark Vagian Helm | A | (none) | head | (not in a set) |
| 518 | Hell Helm | A | (none) | head | (not in a set) |
| 520 | Masterpiece Helm | A | (none) | head | (not in a set) |
| 547 | Tallum Helm | A | (none) | head | a_grade.xml#43 |
| 2418 | Helm of Nightmare | A | (none) | head | a_grade_pvp.xml#107 |
| 2419 | Majestic Circlet | A | (none) | head | a_grade_pvp.xml#108 |
| 7860 | Apella Helm | A | (none) | head | clan.xml#64 |
| 9830 | Improved Apella Helm | A | (none) | head | clan.xml#80 |
| 11417 | Dark Crystal Helmet - Heavy Armor | A | (none) | head | a_grade.xml#39 |
| 11446 | Tallum Helmet - Heavy Armor | A | (none) | head | a_grade.xml#40 |
| 11456 | Majestic Circlet - Heavy Armor | A | (none) | head | a_grade_pvp.xml#104 |
| 11481 | Helm of Nightmare - Heavy Armor | A | (none) | head | a_grade_pvp.xml#103 |
| 12986 | Dark Crystal Helmet - Light Armor Use | A | (none) | head | a_grade.xml#41 |
| 12987 | Dark Crystal Helmet - Robe | A | (none) | head | a_grade.xml#44 |
| 12988 | Tallum Helmet - Light Armor Use | A | (none) | head | a_grade.xml#42 |
| 12989 | Tallum Helmet - Robe | A | (none) | head | a_grade.xml#43 |
| 12990 | Majestic Circlet - Light Armor Use | A | (none) | head | a_grade_pvp.xml#106 |
| 12991 | Majestic Circlet - Robe | A | (none) | head | a_grade_pvp.xml#108 |
| 12992 | Helm of Nightmare - Light Armor Use | A | (none) | head | a_grade_pvp.xml#105 |
| 12993 | Helm of Nightmare - Robe | A | (none) | head | a_grade_pvp.xml#107 |
| 14582 | Apella Combat Helmet | A | (none) | head | clan.xml#157 |
| 641 | Dark Crystal Shield | A | (none) | lhand | a_grade.xml#39 |
| 2498 | Shield of Nightmare | A | (none) | lhand | a_grade_pvp.xml#103 |
| 11416 | Dark Crystal Shield | A | (none) | lhand | a_grade.xml#39 |
| 11480 | Shield of Nightmare | A | (none) | lhand | a_grade_pvp.xml#103 |
| 365 | Dark Crystal Breastplate | A | HEAVY | chest | a_grade.xml#39 |
| 11418 | Dark Crystal Breastplate | A | HEAVY | chest | (not in a set) |
| 388 | Dark Crystal Gaiters | A | HEAVY | legs | a_grade.xml#39 |
| 11407 | Dark Crystal Gaiters | A | HEAVY | legs | a_grade.xml#39 |
| 374 | Armor of Nightmare | A | HEAVY | onepiece | a_grade.xml#46 |
| 2382 | Tallum Plate Armor | A | HEAVY | onepiece | a_grade.xml#40 |
| 2383 | Majestic Plate Armor | A | HEAVY | onepiece | a_grade.xml#47 |
| 7861 | Apella Plate Armor | A | HEAVY | onepiece | clan.xml#62 |
| 9831 | Improved Apella Plate Armor | A | HEAVY | onepiece | clan.xml#78 |
| 11445 | Tallum Plate Armor | A | HEAVY | onepiece | (not in a set) |
| 11457 | Majestic Plate Armor | A | HEAVY | onepiece | (not in a set) |
| 11471 | Armor of Nightmare | A | HEAVY | onepiece | (not in a set) |
| 14583 | Apella Combat Armor | A | HEAVY | onepiece | clan.xml#155 |
| 490 | Tattoo of Blood | A | LIGHT | chest | (not in a set) |
| 491 | Tattoo of Absolute | A | LIGHT | chest | (not in a set) |
| 2385 | Dark Crystal Leather Armor | A | LIGHT | chest | a_grade.xml#41 |
| 2410 | Nightmarish Tattoo | A | LIGHT | chest | (not in a set) |
| 11411 | Dark Crystal Leather Armor | A | LIGHT | chest | (not in a set) |
| 2389 | Dark Crystal Leggings | A | LIGHT | legs | a_grade.xml#41 |
| 11419 | Dark Crystal Leggings | A | LIGHT | legs | a_grade.xml#41 |
| 410 | Unicorn Leather Armor | A | LIGHT | onepiece | (not in a set) |
| 2393 | Tallum Leather Armor | A | LIGHT | onepiece | a_grade.xml#42 |
| 2394 | Leather Armor of Nightmare | A | LIGHT | onepiece | a_grade.xml#48 |
| 2395 | Majestic Leather Armor | A | LIGHT | onepiece | a_grade.xml#49 |
| 7864 | Apella Brigandine | A | LIGHT | onepiece | clan.xml#63 |
| 9834 | Improved Apella Brigandine | A | LIGHT | onepiece | clan.xml#79 |
| 11440 | Tallum Leather Armor | A | LIGHT | onepiece | (not in a set) |
| 11451 | Majestic Leather Armor | A | LIGHT | onepiece | (not in a set) |
| 11475 | Leather Armor of Nightmare | A | LIGHT | onepiece | (not in a set) |
| 14586 | Apella Combat Clothes | A | LIGHT | onepiece | clan.xml#156 |
| 2400 | Tallum Tunic | A | MAGIC | chest | a_grade.xml#43 |
| 11444 | Tallum Tunic | A | MAGIC | chest | (not in a set) |
| 2405 | Tallum Stockings | A | MAGIC | legs | a_grade.xml#43 |
| 11447 | Tallum Stockings | A | MAGIC | legs | a_grade.xml#43 |
| 459 | Dasparion's Robe | A | MAGIC | onepiece | (not in a set) |
| 2407 | Dark Crystal Robe | A | MAGIC | onepiece | a_grade.xml#44 |
| 2408 | Robe of Nightmare | A | MAGIC | onepiece | a_grade.xml#50 |
| 2409 | Majestic Robe | A | MAGIC | onepiece | a_grade.xml#51 |
| 7867 | Apella Doublet | A | MAGIC | onepiece | clan.xml#64 |
| 9837 | Improved Apella Doublet | A | MAGIC | onepiece | clan.xml#80 |
| 11412 | Dark Crystal Robe | A | MAGIC | onepiece | (not in a set) |
| 11452 | Majestic Robe | A | MAGIC | onepiece | (not in a set) |
| 11476 | Robe of Nightmare | A | MAGIC | onepiece | (not in a set) |
| 14589 | Apella Combat Overcoat | A | MAGIC | onepiece | clan.xml#157 |

### Weapons

Base weapons: 73 (SA/elemental, event, PvP, common, shadow and fortune variants excluded).

| ID | Name | Grade | Weapon type | Slot | Class | pAtk | mAtk |
|----|------|-------|-------------|------|-------|------|------|
| 9356 | Durendal | A | ANCIENTSWORD | lrhand | 2H (lrhand) | 272 | 121 |
| 9344 | Undertaker | A | ANCIENTSWORD | lrhand | 2H (lrhand) | 251 | 114 |
| 9332 | Divine Pain | A | ANCIENTSWORD | lrhand | 2H (lrhand) | 231 | 107 |
| 8681 | Behemoth's Tuning Fork | A | BLUNT | lrhand | 2H (lrhand) | 305 | 121 |
| 7902 | Doom Crusher | A | BLUNT | lrhand | 2H (lrhand) | 282 | 114 |
| 7899 | Destroyer Hammer | A | BLUNT | lrhand | 2H (lrhand) | 259 | 107 |
| 8680 | Barakiel's Axe | A | BLUNT | rhand | 1H (rhand) | 251 | 121 |
| 8688 | Daimon Crystal | A | BLUNT | lrhand | 2H (lrhand) | 245 | 177 |
| 164 | Elysian | A | BLUNT | rhand | 1H (rhand) | 232 | 114 |
| 213 | Branch of the Mother Tree | A | BLUNT | lrhand | 2H (lrhand) | 226 | 167 |
| 2504 | Meteor Shower | A | BLUNT | rhand | 1H (rhand) | 213 | 107 |
| 4903 | Dasparion's Staff | A | BLUNT | lrhand | 2H (lrhand) | 207 | 143 |
| 4904 | Dasparion's Staff | A | BLUNT | lrhand | 2H (lrhand) | 207 | 143 |
| 212 | Dasparion's Staff | A | BLUNT | lrhand | 2H (lrhand) | 207 | 157 |
| 4905 | Dasparion's Staff | A | BLUNT | lrhand | 2H (lrhand) | 207 | 143 |
| 8687 | Cabrio's Hand | A | BLUNT | rhand | 1H (rhand) | 202 | 161 |
| 7895 | Flaming Dragon Skull | A | BLUNT | rhand | 1H (rhand) | 186 | 152 |
| 7894 | Spiritual Eye | A | BLUNT | rhand | 1H (rhand) | 170 | 143 |
| 8763 | Elrokian Trap | A | BLUNT | rhand | 1H (rhand) | 0 | 0 |
| 8684 | Shyeed's Bow | A | BOW | lrhand | 2H (lrhand) | 570 | 133 |
| 289 | Soul Bow | A | BOW | lrhand | 2H (lrhand) | 528 | 125 |
| 288 | Carnage Bow | A | BOW | lrhand | 2H (lrhand) | 440 | 107 |
| 9360 | Screaming Vengeance | A | CROSSBOW | lrhand | 2H (lrhand) | 318 | 121 |
| 9348 | Reaper | A | CROSSBOW | lrhand | 2H (lrhand) | 294 | 114 |
| 9336 | Doomchanter | A | CROSSBOW | lrhand | 2H (lrhand) | 270 | 107 |
| 8682 | Naga Storm | A | DAGGER | rhand | 1H (rhand) | 220 | 121 |
| 236 | Soul Separator | A | DAGGER | rhand | 1H (rhand) | 203 | 114 |
| 235 | Bloody Orchid | A | DAGGER | rhand | 1H (rhand) | 186 | 107 |
| 8938 | Damascus * Tallum Blade | A | DUAL | lrhand | dual (lrhand) | 305 | 121 |
| 5706 | Damascus*Damascus | A | DUAL | lrhand | dual (lrhand) | 282 | 114 |
| 5705 | Keshanberk*Damascus | A | DUAL | lrhand | dual (lrhand) | 275 | 112 |
| 5704 | Keshanberk*Keshanberk | A | DUAL | lrhand | dual (lrhand) | 259 | 107 |
| 5233 | Keshanberk*Keshanberk | A | DUAL | lrhand | dual (lrhand) | 259 | 107 |
| 8685 | Sobekk's Hurricane | A | DUALFIST | lrhand | dual fist (lrhand) | 305 | 121 |
| 270 | Dragon Grinder | A | DUALFIST | lrhand | dual fist (lrhand) | 282 | 114 |
| 269 | Blood Tornado | A | DUALFIST | lrhand | dual fist (lrhand) | 259 | 107 |
| 342 | Enchanted Flute | A | ETC | rhand | 1H (rhand) | 186 | 152 |
| 343 | Headless Arrow | A | ETC | rhand | 1H (rhand) | 186 | 152 |
| 341 | Forgotten Tome | A | ETC | rhand | 1H (rhand) | 186 | 152 |
| 345 | Deathbringer Sword | A | ETC | rhand | 1H (rhand) | 186 | 152 |
| 344 | Proof of Overlord | A | ETC | rhand | 1H (rhand) | 186 | 152 |
| 6533 | Cygnus Pole | A | FISHINGROD | lrhand | 2H (lrhand) | 1 | 1 |
| 13054 | Complete Spear of Silenos | A | POLE | lrhand | 2H (lrhand) | 291 | 181 |
| 13053 | Enhanced Spear of Silenos | A | POLE | lrhand | 2H (lrhand) | 271 | 171 |
| 8683 | Tiphon's Spear | A | POLE | lrhand | 2H (lrhand) | 251 | 121 |
| 13052 | Spear of Silenos | A | POLE | lrhand | 2H (lrhand) | 251 | 161 |
| 305 | Tallum Glaive | A | POLE | lrhand | 2H (lrhand) | 232 | 114 |
| 5630 | Orcish Halberd | A | POLE | lrhand | 2H (lrhand) | 219 | 109 |
| 5629 | Orcish Halberd | A | POLE | lrhand | 2H (lrhand) | 219 | 109 |
| 5631 | Orcish Halberd | A | POLE | lrhand | 2H (lrhand) | 219 | 109 |
| 304 | Orcish Halberd | A | POLE | lrhand | 2H (lrhand) | 219 | 109 |
| 98 | Halberd | A | POLE | lrhand | 2H (lrhand) | 213 | 107 |
| 9352 | Éclair Bijou | A | RAPIER | rhand | 1H (rhand) | 228 | 121 |
| 9340 | Lacerator | A | RAPIER | rhand | 1H (rhand) | 210 | 114 |
| 9328 | White Lightning | A | RAPIER | rhand | 1H (rhand) | 193 | 107 |
| 8679 | Sword of Ipos | A | SWORD | lrhand | 2H (lrhand) | 305 | 121 |
| 13044 | Complete Ancient Legacy Sword | A | SWORD | lrhand | 2H (lrhand) | 299 | 163 |
| 81 | Dragon Slayer | A | SWORD | lrhand | 2H (lrhand) | 282 | 114 |
| 13043 | Enhanced Ancient Legacy Sword | A | SWORD | lrhand | 2H (lrhand) | 279 | 153 |
| 13042 | Ancient Legacy Sword | A | SWORD | lrhand | 2H (lrhand) | 259 | 143 |
| 7884 | Infernal Master | A | SWORD | lrhand | 2H (lrhand) | 259 | 107 |
| 8678 | Sirra's Blade | A | SWORD | rhand | 1H (rhand) | 251 | 121 |
| 2500 | Dark Legion's Edge | A | SWORD | rhand | 1H (rhand) | 232 | 114 |
| 80 | Tallum Blade | A | SWORD | rhand | 1H (rhand) | 213 | 107 |
| 8686 | Themis' Tongue | A | SWORD | rhand | 1H (rhand) | 202 | 161 |
| 21973 | Mardil's Fan | A | SWORD | rhand | 1H (rhand) | 186 | 152 |
| 151 | Sword of Miracles | A | SWORD | rhand | 1H (rhand) | 186 | 152 |
| 85 | Phantom Sword | A | SWORD | rhand | 1H (rhand) | 170 | 143 |
| 149 | Sword of Life | A | SWORD | rhand | 1H (rhand) | 170 | 143 |
| 150 | Elemental Sword | A | SWORD | rhand | 1H (rhand) | 170 | 143 |
| 147 | Tear of Darkness | A | SWORD | rhand | 1H (rhand) | 170 | 143 |
| 13845 | Attribute Master Yin's Sword | A | SWORD | rhand | 1H (rhand) | 140 | 120 |
| 13881 | Attribute Master Yang's Sword | A | SWORD | rhand | 1H (rhand) | 140 | 120 |

### Jewelry

Pieces: 51.

| ID | Name | Grade | Slot |
|----|------|-------|------|
| 924 | Majestic Necklace | A | neck |
| 930 | Necklace of Phantom | A | neck |
| 933 | Phoenix Necklace | A | neck |
| 934 | Cerberus Necklace | A | neck |
| 8191 | Necklace of Frintezza | A | neck |
| 11584 | Phoenix Necklace | A | neck |
| 11587 | Majestic Necklace | A | neck |
| 13740 | Gludio Water Resistance Necklace | A | neck |
| 13741 | Dion Holy Resistance Necklace | A | neck |
| 13742 | Giran Wind Resistance Necklace | A | neck |
| 13743 | Oren Dark Resistance Necklace | A | neck |
| 13744 | Aden Earth Resistance Necklace | A | neck |
| 13745 | Innadril Water Resistance Necklace | A | neck |
| 13746 | Goddard Fire Resistance Necklace | A | neck |
| 13747 | Rune Fire Resistance Necklace | A | neck |
| 13748 | Schuttgart Wind Resistance Necklace | A | neck |
| 13753 | Olympiad Warrior's Necklace | A | neck |
| 862 | Majestic Earring | A | rear;lear |
| 868 | Earring of Phantom | A | rear;lear |
| 871 | Phoenix Earring | A | rear;lear |
| 872 | Cerberus Earring | A | rear;lear |
| 6661 | Earring of Orfen | A | rear;lear |
| 11586 | Phoenix Earring | A | rear;lear |
| 11589 | Majestic Earring | A | rear;lear |
| 13754 | Olympiad Warrior's Earring | A | rear;lear |
| 14664 | Gludio Protection Earring | A | rear;lear |
| 14665 | Dion Protection Earring | A | rear;lear |
| 14666 | Giran Protection Earring | A | rear;lear |
| 14667 | Oren Protection Earring | A | rear;lear |
| 14668 | Aden Protection Earring | A | rear;lear |
| 14669 | Innadril Protection Earring | A | rear;lear |
| 14670 | Goddard Protection Earring | A | rear;lear |
| 14671 | Rune Protection Earring | A | rear;lear |
| 14672 | Schuttgart Protection Earring | A | rear;lear |
| 893 | Majestic Ring | A | rfinger;lfinger |
| 899 | Ring of Phantom | A | rfinger;lfinger |
| 902 | Phoenix Ring | A | rfinger;lfinger |
| 903 | Cerberus Ring | A | rfinger;lfinger |
| 6662 | Ring of Core | A | rfinger;lfinger |
| 11585 | Phoenix Ring | A | rfinger;lfinger |
| 11588 | Majestic Ring | A | rfinger;lfinger |
| 13752 | Olympiad Warrior's Ring | A | rfinger;lfinger |
| 14592 | Gludio Earth Resistance Ring | A | rfinger;lfinger |
| 14593 | Dion Water Resistance Ring | A | rfinger;lfinger |
| 14594 | Giran Fire Resistance Ring | A | rfinger;lfinger |
| 14595 | Oren Earth Resistance Ring | A | rfinger;lfinger |
| 14596 | Aden Holy Resistance Ring | A | rfinger;lfinger |
| 14597 | Innadril Holy Resistance Ring | A | rfinger;lfinger |
| 14598 | Goddard Dark Resistance Ring | A | rfinger;lfinger |
| 14599 | Rune Wind Resistance Ring | A | rfinger;lfinger |
| 14600 | Schuttgart Dark Resistance Ring | A | rfinger;lfinger |

## 9. S-Grade Equipment

### Armor

Pieces: 139 (slot-relevant armor for this grade, noise variants excluded).

| ID | Name | Grade | Armor type | Slot | Set membership |
|----|------|-------|------------|------|----------------|
| 2443 | Dragon Leather Boots | S | (none) | feet | (not in a set) |
| 2444 | The Boots | S | (none) | feet | (not in a set) |
| 6376 | Imperial Crusader Boots | S | (none) | feet | s_grade_pvp.xml#109 |
| 6381 | Draconic Leather Boots | S | (none) | feet | s_grade_pvp.xml#110 |
| 6385 | Major Arcana Boots | S | (none) | feet | s_grade_pvp.xml#111 |
| 9424 | Dynasty Boots - Heavy Armor | S | (none) | feet | s80_dynasty_pvp.xml#132 |
| 9431 | Dynasty Leather Boots - Light Armor | S | (none) | feet | s80_dynasty_pvp.xml#138 |
| 9440 | Dynasty Shoes - Robe | S | (none) | feet | s80_dynasty_pvp.xml#142 |
| 11484 | Draconic Leather Boots | S | (none) | feet | s_grade_pvp.xml#110 |
| 11489 | Major Arcana Boots | S | (none) | feet | s_grade_pvp.xml#111 |
| 11507 | Imperial Crusader Boots | S | (none) | feet | s_grade_pvp.xml#109 |
| 11524 | Dynasty Leather Boots | S | (none) | feet | s80_dynasty_pvp.xml#138 |
| 11526 | Dynasty Boots | S | (none) | feet | s80_dynasty_pvp.xml#132 |
| 11533 | Dynasty Shoes | S | (none) | feet | s80_dynasty_pvp.xml#142 |
| 2473 | The Gloves | S | (none) | gloves | (not in a set) |
| 2476 | Dragon Gauntlets | S | (none) | gloves | (not in a set) |
| 2477 | Dragon Leather Gloves | S | (none) | gloves | (not in a set) |
| 6375 | Imperial Crusader Gauntlets | S | (none) | gloves | s_grade_pvp.xml#109 |
| 6380 | Draconic Leather Gloves | S | (none) | gloves | s_grade_pvp.xml#110 |
| 6384 | Major Arcana Gloves | S | (none) | gloves | s_grade_pvp.xml#111 |
| 9423 | Dynasty Gauntlet - Heavy Armor | S | (none) | gloves | s80_dynasty_pvp.xml#132 |
| 9430 | Dynasty Leather Gloves - Light Armor | S | (none) | gloves | s80_dynasty_pvp.xml#138 |
| 9439 | Dynasty Gloves - Robe | S | (none) | gloves | s80_dynasty_pvp.xml#142 |
| 11483 | Draconic Leather Gloves | S | (none) | gloves | s_grade_pvp.xml#110 |
| 11487 | Major Arcana Gloves | S | (none) | gloves | s_grade_pvp.xml#111 |
| 11506 | Imperial Crusader Gauntlet | S | (none) | gloves | s_grade_pvp.xml#109 |
| 11513 | Dynasty Gauntlet | S | (none) | gloves | s80_dynasty_pvp.xml#132 |
| 11514 | Dynasty Gloves | S | (none) | gloves | s80_dynasty_pvp.xml#142 |
| 11515 | Dynasty Leather Gloves | S | (none) | gloves | s80_dynasty_pvp.xml#138 |
| 504 | Dragon Helmet | S | (none) | head | (not in a set) |
| 2420 | Dragon Headgear | S | (none) | head | (not in a set) |
| 2421 | The Hood | S | (none) | head | (not in a set) |
| 6378 | Imperial Crusader Helmet | S | (none) | head | s_grade_pvp.xml#109 |
| 6382 | Draconic Leather Helmet | S | (none) | head | s_grade_pvp.xml#110 |
| 6386 | Major Arcana Circlet | S | (none) | head | s_grade_pvp.xml#111 |
| 9422 | Dynasty Helmet | S | (none) | head | s80_dynasty_pvp.xml#132 |
| 9429 | Dynasty Leather Helmet | S | (none) | head | s80_dynasty_pvp.xml#138 |
| 9438 | Dynasty Circlet | S | (none) | head | s80_dynasty_pvp.xml#142 |
| 11486 | Draconic Leather Helmet | S | (none) | head | s_grade_pvp.xml#110 |
| 11490 | Major Arcana Circlet | S | (none) | head | s_grade_pvp.xml#111 |
| 11509 | Imperial Crusader Helmet | S | (none) | head | s_grade_pvp.xml#109 |
| 11525 | Dynasty Leather Helmet | S | (none) | head | s80_dynasty_pvp.xml#138 |
| 11539 | Dynasty Circlet | S | (none) | head | s80_dynasty_pvp.xml#142 |
| 11557 | Dynasty Helmet | S | (none) | head | s80_dynasty_pvp.xml#132 |
| 634 | Dragon Shield | S | (none) | lhand | (not in a set) |
| 6377 | Imperial Crusader Shield | S | (none) | lhand | s_grade_pvp.xml#109 |
| 9441 | Dynasty Shield | S | (none) | lhand | s80_dynasty_pvp.xml#129 |
| 11508 | Imperial Crusader Shield | S | (none) | lhand | s_grade_pvp.xml#109 |
| 11532 | Dynasty Shield | S | (none) | lhand | s80_dynasty_pvp.xml#129 |
| 375 | Dragon Scale Mail | S | HEAVY | chest | (not in a set) |
| 6373 | Imperial Crusader Breastplate | S | HEAVY | chest | s_grade.xml#56 |
| 9416 | Dynasty Breast Plate | S | HEAVY | chest | s80_dynasty.xml#82 |
| 9417 | Dynasty Breast Plate - Shield Master | S | HEAVY | chest | s80_dynasty.xml#65 |
| 9418 | Dynasty Breast Plate - Weapon Master | S | HEAVY | chest | s80_dynasty.xml#66 |
| 9419 | Dynasty Breast Plate - Force Master | S | HEAVY | chest | s80_dynasty.xml#67 |
| 9420 | Dynasty Breast Plate - Bard | S | HEAVY | chest | s80_dynasty.xml#68 |
| 10227 | Dynasty Platinum Plate | S | HEAVY | chest | (not in a set) |
| 10228 | Dynasty Platinum Plate - Shield Master | S | HEAVY | chest | s80_dynasty.xml#89 |
| 10229 | Dynasty Platinum Plate - Weapon Master | S | HEAVY | chest | s80_dynasty.xml#90 |
| 10230 | Dynasty Platinum Plate - Force Master | S | HEAVY | chest | s80_dynasty.xml#91 |
| 10231 | Dynasty Platinum Plate - Bard | S | HEAVY | chest | s80_dynasty.xml#92 |
| 11510 | Imperial Crusader Breastplate | S | HEAVY | chest | (not in a set) |
| 11527 | Dynasty Breastplate | S | HEAVY | chest | (not in a set) |
| 11528 | Dynasty Breastplate | S | HEAVY | chest | (not in a set) |
| 11529 | Dynasty Breastplate | S | HEAVY | chest | (not in a set) |
| 11530 | Dynasty Breastplate | S | HEAVY | chest | (not in a set) |
| 11531 | Dynasty Breastplate | S | HEAVY | chest | (not in a set) |
| 11552 | Dynasty Platinum Plate | S | HEAVY | chest | (not in a set) |
| 11553 | Dynasty Platinum Plate | S | HEAVY | chest | (not in a set) |
| 11554 | Dynasty Platinum Plate | S | HEAVY | chest | (not in a set) |
| 11555 | Dynasty Platinum Plate | S | HEAVY | chest | (not in a set) |
| 11556 | Dynasty Platinum Plate | S | HEAVY | chest | (not in a set) |
| 389 | Dragon Scale Gaiters | S | HEAVY | legs | (not in a set) |
| 6374 | Imperial Crusader Gaiters | S | HEAVY | legs | s_grade_pvp.xml#109 |
| 9421 | Dynasty Gaiters | S | HEAVY | legs | s80_dynasty_pvp.xml#132 |
| 11505 | Imperial Crusader Gaiters | S | HEAVY | legs | s_grade_pvp.xml#109 |
| 11512 | Dynasty Gaiters | S | HEAVY | legs | s80_dynasty_pvp.xml#132 |
| 9425 | Dynasty Leather Armor | S | LIGHT | chest | s80_dynasty.xml#83 |
| 9426 | Dynasty Leather Armor - Dagger Master | S | LIGHT | chest | s80_dynasty.xml#69 |
| 9427 | Dynasty Leather Armor - Bow Master | S | LIGHT | chest | s80_dynasty.xml#70 |
| 10126 | Dynasty Leather Armor - Force Master | S | LIGHT | chest | s80_dynasty.xml#85 |
| 10127 | Dynasty Leather Armor - Weapon Master | S | LIGHT | chest | s80_dynasty.xml#86 |
| 10168 | Dynasty Leather Armor - Enchanter | S | LIGHT | chest | s80_dynasty.xml#87 |
| 10214 | Dynasty Leather Armor - Summoner | S | LIGHT | chest | s80_dynasty.xml#88 |
| 10232 | Dynasty Jewel Leather Armor | S | LIGHT | chest | (not in a set) |
| 10233 | Dynasty Jewel Leather Armor - Dagger Master | S | LIGHT | chest | s80_dynasty.xml#93 |
| 10234 | Dynasty Jewel Leather Armor - Bow Master | S | LIGHT | chest | s80_dynasty.xml#94 |
| 10487 | Dynasty Jeweled Leather Armor - Force Master | S | LIGHT | chest | s80_dynasty.xml#95 |
| 10488 | Dynasty Jeweled Leather Armor - Weapon Master | S | LIGHT | chest | s80_dynasty.xml#96 |
| 10489 | Dynasty Jeweled Leather Armor - Enchanter | S | LIGHT | chest | s80_dynasty.xml#97 |
| 10490 | Dynasty Jeweled Leather Armor - Summoner | S | LIGHT | chest | s80_dynasty.xml#98 |
| 11517 | Dynasty Leather Armor | S | LIGHT | chest | (not in a set) |
| 11518 | Dynasty Leather Armor | S | LIGHT | chest | (not in a set) |
| 11519 | Dynasty Leather Armor | S | LIGHT | chest | (not in a set) |
| 11520 | Dynasty Leather Armor | S | LIGHT | chest | (not in a set) |
| 11521 | Dynasty Leather Armor | S | LIGHT | chest | (not in a set) |
| 11522 | Dynasty Leather Armor | S | LIGHT | chest | (not in a set) |
| 11523 | Dynasty Leather Armor | S | LIGHT | chest | (not in a set) |
| 11540 | Dynasty Jewel Leather Armor | S | LIGHT | chest | (not in a set) |
| 11541 | Dynasty Jewel Leather Armor | S | LIGHT | chest | (not in a set) |
| 11542 | Dynasty Jewel Leather Armor | S | LIGHT | chest | (not in a set) |
| 11543 | Dynasty Jewel Leather Armor | S | LIGHT | chest | (not in a set) |
| 11544 | Dynasty Jewel Leather Armor | S | LIGHT | chest | (not in a set) |
| 11545 | Dynasty Jewel Leather Armor | S | LIGHT | chest | (not in a set) |
| 11546 | Dynasty Jewel Leather Armor | S | LIGHT | chest | (not in a set) |
| 9428 | Dynasty Leather Leggings | S | LIGHT | legs | s80_dynasty_pvp.xml#138 |
| 11516 | Dynasty Leather Leggings | S | LIGHT | legs | s80_dynasty_pvp.xml#138 |
| 411 | Dragon Leather Armor | S | LIGHT | onepiece | (not in a set) |
| 6379 | Draconic Leather Armor | S | LIGHT | onepiece | s_grade.xml#57 |
| 11485 | Draconic Leather Armor | S | LIGHT | onepiece | (not in a set) |
| 9432 | Dynasty Tunic | S | MAGIC | chest | s80_dynasty.xml#84 |
| 9433 | Dynasty Tunic - Healer | S | MAGIC | chest | s80_dynasty.xml#71 |
| 9434 | Dynasty Tunic - Enchanter | S | MAGIC | chest | s80_dynasty.xml#72 |
| 9435 | Dynasty Tunic - Summoner | S | MAGIC | chest | s80_dynasty.xml#73 |
| 9436 | Dynasty Tunic - Wizard | S | MAGIC | chest | s80_dynasty.xml#74 |
| 10235 | Dynasty Silver Satin Tunic | S | MAGIC | chest | (not in a set) |
| 10236 | Dynasty Silver Satin Tunic - Healer | S | MAGIC | chest | s80_dynasty.xml#99 |
| 10237 | Dynasty Silver Satin Tunic - Enchanter | S | MAGIC | chest | s80_dynasty.xml#100 |
| 10238 | Dynasty Silver Satin Tunic - Summoner | S | MAGIC | chest | s80_dynasty.xml#101 |
| 10239 | Dynasty Silver Satin Tunic - Wizard | S | MAGIC | chest | s80_dynasty.xml#102 |
| 11534 | Dynasty Silver Satin Tunic | S | MAGIC | chest | (not in a set) |
| 11535 | Dynasty Silver Satin Tunic | S | MAGIC | chest | (not in a set) |
| 11536 | Dynasty Silver Satin Tunic | S | MAGIC | chest | (not in a set) |
| 11537 | Dynasty Silver Satin Tunic | S | MAGIC | chest | (not in a set) |
| 11538 | Dynasty Silver Satin Tunic | S | MAGIC | chest | (not in a set) |
| 11547 | Dynasty Tunic | S | MAGIC | chest | (not in a set) |
| 11548 | Dynasty Tunic | S | MAGIC | chest | (not in a set) |
| 11549 | Dynasty Tunic | S | MAGIC | chest | (not in a set) |
| 11550 | Dynasty Tunic | S | MAGIC | chest | (not in a set) |
| 11551 | Dynasty Tunic | S | MAGIC | chest | (not in a set) |
| 9437 | Dynasty Stockings | S | MAGIC | legs | s80_dynasty_pvp.xml#142 |
| 11558 | Dynasty Stockings | S | MAGIC | legs | s80_dynasty_pvp.xml#142 |
| 460 | The Robe | S | MAGIC | onepiece | (not in a set) |
| 6383 | Major Arcana Robe | S | MAGIC | onepiece | s_grade.xml#58 |
| 11488 | Major Arcana Robe | S | MAGIC | onepiece | (not in a set) |
| 10119 | Dynasty Sigil | S | SIGIL | lhand | (not in a set) |
| 12811 | Arcana Sigil | S | SIGIL | lhand | (not in a set) |
| 12812 | Dynasty Sigil | S | SIGIL | lhand | (not in a set) |
| 13078 | Arcana Sigil | S | SIGIL | lhand | (not in a set) |

### Weapons

Base weapons: 80 (SA/elemental, event, PvP, common, shadow and fortune variants excluded).

| ID | Name | Grade | Weapon type | Slot | Class | pAtk | mAtk |
|----|------|-------|-------------|------|-------|------|------|
| 9389 | Infinity Sword | S | ANCIENTSWORD | lrhand | 2H (lrhand) | 568 | 230 |
| 9380 | Dynasty Ancient Sword | S | ANCIENTSWORD | lrhand | 2H (lrhand) | 361 | 151 |
| 14578 | Slicer of Val Turner Family | S | ANCIENTSWORD | lrhand | 2H (lrhand) | 334 | 119 |
| 14580 | Slicer of Esthus Family | S | ANCIENTSWORD | lrhand | 2H (lrhand) | 334 | 119 |
| 9368 | Gram | S | ANCIENTSWORD | lrhand | 2H (lrhand) | 304 | 132 |
| 6615 | Infinity Crusher | S | BLUNT | lrhand | 2H (lrhand) | 638 | 230 |
| 6613 | Infinity Axe | S | BLUNT | rhand | 1H (rhand) | 524 | 230 |
| 6616 | Infinity Scepter | S | BLUNT | lrhand | 2H (lrhand) | 511 | 337 |
| 6614 | Infinity Rod | S | BLUNT | rhand | 1H (rhand) | 420 | 307 |
| 10253 | Dynasty Crusher | S | BLUNT | lrhand | 2H (lrhand) | 405 | 151 |
| 14573 | Great Hammer of Abygail Family | S | BLUNT | lrhand | 2H (lrhand) | 376 | 119 |
| 14565 | Great Hammer of Esthus Family | S | BLUNT | lrhand | 2H (lrhand) | 376 | 119 |
| 6369 | Dragon Hunter Axe | S | BLUNT | lrhand | 2H (lrhand) | 342 | 132 |
| 9448 | Dynasty Cudgel | S | BLUNT | rhand | 1H (rhand) | 333 | 151 |
| 10252 | Dynasty Staff | S | BLUNT | lrhand | 2H (lrhand) | 325 | 222 |
| 14569 | Mace of Cadmus Family | S | BLUNT | rhand | 1H (rhand) | 310 | 119 |
| 14576 | Mace of Orwen Family | S | BLUNT | rhand | 1H (rhand) | 310 | 119 |
| 6365 | Basalt Battlehammer | S | BLUNT | rhand | 1H (rhand) | 281 | 132 |
| 6366 | Imperial Staff | S | BLUNT | lrhand | 2H (lrhand) | 274 | 193 |
| 9449 | Dynasty Mace | S | BLUNT | rhand | 1H (rhand) | 267 | 202 |
| 165 | Yablonski's Hammer | S | BLUNT | rhand | 1H (rhand) | 251 | 121 |
| 14572 | Staff of Abygail Family | S | BLUNT | lrhand | 2H (lrhand) | 247 | 212 |
| 14566 | Staff of Dake Family | S | BLUNT | lrhand | 2H (lrhand) | 247 | 212 |
| 214 | The Staff | S | BLUNT | lrhand | 2H (lrhand) | 245 | 162 |
| 6579 | Arcana Mace | S | BLUNT | rhand | 1H (rhand) | 225 | 175 |
| 14567 | Hall of Dake Family | S | BLUNT | rhand | 1H (rhand) | 203 | 193 |
| 6619 | Infinity Bow | S | BOW | lrhand | 2H (lrhand) | 952 | 230 |
| 9445 | Dynasty Bow | S | BOW | lrhand | 2H (lrhand) | 654 | 151 |
| 14568 | Bow of Cadmus Family | S | BOW | lrhand | 2H (lrhand) | 640 | 119 |
| 6368 | Shining Bow | S | BOW | lrhand | 2H (lrhand) | 581 | 132 |
| 7575 | Draconic Bow | S | BOW | lrhand | 2H (lrhand) | 581 | 132 |
| 290 | The Bow | S | BOW | lrhand | 2H (lrhand) | 519 | 121 |
| 9390 | Infinity Shooter | S | CROSSBOW | lrhand | 2H (lrhand) | 584 | 230 |
| 9384 | Dynasty Crossbow | S | CROSSBOW | lrhand | 2H (lrhand) | 401 | 151 |
| 14581 | Estoc of Cadmus Family | S | CROSSBOW | lrhand | 2H (lrhand) | 392 | 119 |
| 9372 | Sarunga | S | CROSSBOW | lrhand | 2H (lrhand) | 356 | 132 |
| 6617 | Infinity Stinger | S | DAGGER | rhand | 1H (rhand) | 458 | 230 |
| 9446 | Dynasty Knife | S | DAGGER | rhand | 1H (rhand) | 291 | 151 |
| 14560 | Dagger of Val Turner Family | S | DAGGER | rhand | 1H (rhand) | 271 | 119 |
| 14575 | Dagger of Halter Family | S | DAGGER | rhand | 1H (rhand) | 271 | 119 |
| 6367 | Angel Slayer | S | DAGGER | rhand | 1H (rhand) | 246 | 132 |
| 237 | Dragon's Tooth | S | DAGGER | rhand | 1H (rhand) | 220 | 121 |
| 6620 | Infinity Wing | S | DUAL | lrhand | dual (lrhand) | 638 | 230 |
| 10004 | Dynasty Dual Sword | S | DUAL | lrhand | dual (lrhand) | 405 | 151 |
| 21955 | Blades of Delusion | S | DUAL | lrhand | dual (lrhand) | 405 | 151 |
| 14570 | Dual Sword of Hunter Family | S | DUAL | lrhand | dual (lrhand) | 376 | 119 |
| 6580 | Tallum Blade*Dark Legion's Edge | S | DUAL | lrhand | dual (lrhand) | 342 | 132 |
| 13882 | Dynasty Dual Daggers | S | DUALDAGGER | lrhand | dual dagger (lrhand) | 304 | 157 |
| 6618 | Infinity Fang | S | DUALFIST | lrhand | dual fist (lrhand) | 638 | 230 |
| 9450 | Dynasty Bagh-Nakh | S | DUALFIST | lrhand | dual fist (lrhand) | 405 | 151 |
| 14577 | Claw of Orwen Family | S | DUALFIST | lrhand | dual fist (lrhand) | 376 | 119 |
| 14563 | Claw of Ashton Family | S | DUALFIST | lrhand | dual fist (lrhand) | 376 | 119 |
| 6371 | Demon Splinter | S | DUALFIST | lrhand | dual fist (lrhand) | 342 | 132 |
| 346 | Tears of Fallen Angel | S | ETC | rhand | 1H (rhand) | 201 | 162 |
| 6534 | Triton Pole | S | FISHINGROD | lrhand | 2H (lrhand) | 1 | 1 |
| 6621 | Infinity Spear | S | POLE | lrhand | 2H (lrhand) | 524 | 230 |
| 9447 | Dynasty Halberd | S | POLE | lrhand | 2H (lrhand) | 333 | 151 |
| 14571 | Spear of Hunter Family | S | POLE | lrhand | 2H (lrhand) | 310 | 119 |
| 14574 | Spear of Halter Family | S | POLE | lrhand | 2H (lrhand) | 310 | 119 |
| 6370 | Saint Spear | S | POLE | lrhand | 2H (lrhand) | 281 | 132 |
| 307 | Aurakyria Lance | S | POLE | lrhand | 2H (lrhand) | 269 | 128 |
| 306 | Dragon Claw Axe | S | POLE | lrhand | 2H (lrhand) | 251 | 121 |
| 9388 | Infinity Rapier | S | RAPIER | rhand | 1H (rhand) | 475 | 230 |
| 15687 | Triumph Rapier | S | RAPIER | rhand | 1H (rhand) | 344 | 183 |
| 9376 | Dynasty Rapier | S | RAPIER | rhand | 1H (rhand) | 302 | 151 |
| 14579 | Epee of Ashton Family | S | RAPIER | rhand | 1H (rhand) | 281 | 119 |
| 9364 | Laevateinn | S | RAPIER | rhand | 1H (rhand) | 255 | 132 |
| 6612 | Infinity Cleaver | S | SWORD | lrhand | 2H (lrhand) | 638 | 230 |
| 6611 | Infinity Blade | S | SWORD | rhand | 1H (rhand) | 524 | 230 |
| 21959 | Blood Brother | S | SWORD | lrhand | 2H (lrhand) | 405 | 151 |
| 9443 | Dynasty Blade | S | SWORD | lrhand | 2H (lrhand) | 405 | 151 |
| 14564 | Slasher of Esthus Family | S | SWORD | lrhand | 2H (lrhand) | 376 | 119 |
| 14561 | Slasher of Val Turner Family | S | SWORD | lrhand | 2H (lrhand) | 376 | 119 |
| 6372 | Heaven's Divider | S | SWORD | lrhand | 2H (lrhand) | 342 | 132 |
| 9442 | Dynasty Sword | S | SWORD | rhand | 1H (rhand) | 333 | 151 |
| 14562 | Sword of Ashton Family | S | SWORD | rhand | 1H (rhand) | 310 | 119 |
| 6364 | Forgotten Blade | S | SWORD | rhand | 1H (rhand) | 281 | 132 |
| 9444 | Dynasty Phantom | S | SWORD | rhand | 1H (rhand) | 267 | 202 |
| 82 | Gaz Blade | S | SWORD | rhand | 1H (rhand) | 257 | 124 |
| 15310 | Sacred Sword of Einhasad | S | SWORD | rhand | 1H (rhand) | 7 | 5 |

### Jewelry

Pieces: 39.

| ID | Name | Grade | Slot |
|----|------|-------|------|
| 920 | Tateossian Necklace | S | neck |
| 6657 | Necklace of Valakas | S | neck |
| 9456 | Dynasty Necklace | S | neck |
| 9459 | Dynasty Necklace - Stun Resistance | S | neck |
| 9462 | Dynasty Necklace - Poison Resistance | S | neck |
| 9465 | Dynasty Necklace - Bleed Resistance | S | neck |
| 9468 | Dynasty Necklace - Sleep Resistance | S | neck |
| 9471 | Dynasty Necklace - Paralysis Resistance | S | neck |
| 9474 | Dynasty Necklace - Hold Resistance | S | neck |
| 9477 | Dynasty Necklace - Fear Resistance | S | neck |
| 11596 | Tateossian Necklace | S | neck |
| 11599 | Dynasty Necklace | S | neck |
| 858 | Tateossian Earring | S | rear;lear |
| 6656 | Earring of Antharas | S | rear;lear |
| 6659 | Earring of Zaken | S | rear;lear |
| 9455 | Dynasty Earrings | S | rear;lear |
| 9458 | Dynasty Earrings - Stun Resistance | S | rear;lear |
| 9461 | Dynasty Earrings - Poison Resistance | S | rear;lear |
| 9464 | Dynasty Earrings - Bleed Resistance | S | rear;lear |
| 9467 | Dynasty Earrings - Sleep Resistance | S | rear;lear |
| 9470 | Dynasty Earrings - Paralysis Resistance | S | rear;lear |
| 9473 | Dynasty Earrings - Hold Resistance | S | rear;lear |
| 9476 | Dynasty Earrings - Fear Resistance | S | rear;lear |
| 11598 | Tateossian Earring | S | rear;lear |
| 11601 | Dynasty Earrings | S | rear;lear |
| 889 | Tateossian Ring | S | rfinger;lfinger |
| 6658 | Ring of Baium | S | rfinger;lfinger |
| 9457 | Dynasty Ring | S | rfinger;lfinger |
| 9460 | Dynasty Ring - Stun Resistance | S | rfinger;lfinger |
| 9463 | Dynasty Ring - Poison Resistance | S | rfinger;lfinger |
| 9466 | Dynasty Ring - Bleed Resistance | S | rfinger;lfinger |
| 9469 | Dynasty Ring - Sleep Resistance | S | rfinger;lfinger |
| 9472 | Dynasty Ring - Paralysis Resistance | S | rfinger;lfinger |
| 9475 | Dynasty Ring - Hold Resistance | S | rfinger;lfinger |
| 9478 | Dynasty Ring - Fear Resistance | S | rfinger;lfinger |
| 10314 | Ring of Beleth | S | rfinger;lfinger |
| 11597 | Tateossian Ring | S | rfinger;lfinger |
| 11600 | Dynasty Ring | S | rfinger;lfinger |
| 22173 | Improved Ring of Baium | S | rfinger;lfinger |

## 10. S80 / S84 Equipment

### Armor

Pieces: 34 (slot-relevant armor for this grade, noise variants excluded).

| ID | Name | Grade | Armor type | Slot | Set membership |
|----|------|-------|------------|------|----------------|
| 15618 | Moirai Boots | S80 | (none) | feet | s80_moirai_pvp.xml#197 |
| 15619 | Moirai Leather Boots | S80 | (none) | feet | s80_moirai_pvp.xml#198 |
| 15620 | Moirai Shoes | S80 | (none) | feet | s80_moirai_pvp.xml#199 |
| 16301 | Moirai Boots | S80 | (none) | feet | s80_moirai_pvp.xml#197 |
| 16302 | Moirai Leather Boots | S80 | (none) | feet | s80_moirai_pvp.xml#198 |
| 16303 | Moirai Shoes | S80 | (none) | feet | s80_moirai_pvp.xml#199 |
| 15615 | Moirai Gauntlet | S80 | (none) | gloves | s80_moirai_pvp.xml#197 |
| 15616 | Moirai Leather Gloves | S80 | (none) | gloves | s80_moirai_pvp.xml#198 |
| 15617 | Moirai Gloves | S80 | (none) | gloves | s80_moirai_pvp.xml#199 |
| 16298 | Moirai Gauntlet | S80 | (none) | gloves | s80_moirai_pvp.xml#197 |
| 16299 | Moirai Leather Gloves | S80 | (none) | gloves | s80_moirai_pvp.xml#198 |
| 16300 | Moirai Gloves | S80 | (none) | gloves | s80_moirai_pvp.xml#199 |
| 15606 | Moirai Helmet | S80 | (none) | head | s80_moirai_pvp.xml#197 |
| 15607 | Moirai Leather Helmet | S80 | (none) | head | s80_moirai_pvp.xml#198 |
| 15608 | Moirai Circlet | S80 | (none) | head | s80_moirai_pvp.xml#199 |
| 16289 | Moirai Helmet | S80 | (none) | head | s80_moirai_pvp.xml#197 |
| 16290 | Moirai Leather Helmet | S80 | (none) | head | s80_moirai_pvp.xml#198 |
| 16291 | Moirai Circlet | S80 | (none) | head | s80_moirai_pvp.xml#199 |
| 15621 | Moirai Shield | S80 | (none) | lhand | s80_moirai_pvp.xml#197 |
| 16304 | Moirai Shield | S80 | (none) | lhand | s80_moirai_pvp.xml#197 |
| 15609 | Moirai Breastplate | S80 | HEAVY | chest | s80_moirai.xml#182 |
| 16292 | Moirai Breastplate | S80 | HEAVY | chest | (not in a set) |
| 15612 | Moirai Gaiter | S80 | HEAVY | legs | s80_moirai_pvp.xml#197 |
| 16295 | Moirai Gaiter | S80 | HEAVY | legs | s80_moirai_pvp.xml#197 |
| 15610 | Moirai Leather Breastplate | S80 | LIGHT | chest | s80_moirai.xml#183 |
| 16293 | Moirai Leather Breastplate | S80 | LIGHT | chest | (not in a set) |
| 15613 | Moirai Leather Legging | S80 | LIGHT | legs | s80_moirai_pvp.xml#198 |
| 16296 | Moirai Leather Legging | S80 | LIGHT | legs | s80_moirai_pvp.xml#198 |
| 15611 | Moirai Tunic | S80 | MAGIC | chest | s80_moirai.xml#184 |
| 16294 | Moirai Tunic | S80 | MAGIC | chest | (not in a set) |
| 15614 | Moirai Stockings | S80 | MAGIC | legs | s80_moirai_pvp.xml#199 |
| 16297 | Moirai Stockings | S80 | MAGIC | legs | s80_moirai_pvp.xml#199 |
| 15622 | Moirai Sigil | S80 | SIGIL | lhand | (not in a set) |
| 16305 | Moirai Sigil | S80 | SIGIL | lhand | (not in a set) |

### Weapons

Base weapons: 24 (SA/elemental, event, PvP, common, shadow and fortune variants excluded).

| ID | Name | Grade | Weapon type | Slot | Class | pAtk | mAtk |
|----|------|-------|-------------|------|-------|------|------|
| 10225 | Icarus Wingblade | S80 | ANCIENTSWORD | lrhand | 2H (lrhand) | 393 | 163 |
| 10220 | Icarus Hammer | S80 | BLUNT | rhand | 1H (rhand) | 363 | 163 |
| 10222 | Icarus Hall | S80 | BLUNT | rhand | 1H (rhand) | 290 | 217 |
| 10223 | Icarus Spitter | S80 | BOW | lrhand | 2H (lrhand) | 689 | 163 |
| 15302 | Transparent Bow (for NPC) | S80 | BOW | lrhand | 2H (lrhand) | 114 | 35 |
| 10226 | Icarus Shooter | S80 | CROSSBOW | lrhand | 2H (lrhand) | 422 | 163 |
| 15304 | Transparent Bowgun (for NPC) | S80 | CROSSBOW | lrhand | 2H (lrhand) | 64 | 32 |
| 10216 | Icarus Disperser | S80 | DAGGER | rhand | 1H (rhand) | 318 | 163 |
| 10415 | Icarus Dual Sword | S80 | DUAL | lrhand | dual (lrhand) | 442 | 163 |
| 15300 | Transparent Dual (for NPC) | S80 | DUAL | lrhand | dual (lrhand) | 282 | 114 |
| 13883 | Icarus Dual Daggers | S80 | DUALDAGGER | lrhand | dual dagger (lrhand) | 332 | 169 |
| 21935 | Butcher Blades | S80 | DUALDAGGER | lrhand | dual dagger (lrhand) | 332 | 169 |
| 15306 | Transparent Dual Dagger (for NPC) | S80 | DUALDAGGER | lrhand | dual dagger (lrhand) | 304 | 157 |
| 10221 | Icarus Hand | S80 | DUALFIST | lrhand | dual fist (lrhand) | 442 | 163 |
| 15303 | Transparent Claw (for NPC) | S80 | DUALFIST | lrhand | dual fist (lrhand) | 376 | 119 |
| 10219 | Icarus Trident | S80 | POLE | lrhand | 2H (lrhand) | 363 | 163 |
| 15301 | Transparent Pole (for NPC) | S80 | POLE | lrhand | 2H (lrhand) | 156 | 83 |
| 10224 | Icarus Stinger | S80 | RAPIER | rhand | 1H (rhand) | 329 | 163 |
| 15305 | Transparent Rapier (for NPC) | S80 | RAPIER | rhand | 1H (rhand) | 36 | 26 |
| 10218 | Icarus Heavy Arms | S80 | SWORD | lrhand | 2H (lrhand) | 442 | 163 |
| 10215 | Icarus Sawsword | S80 | SWORD | rhand | 1H (rhand) | 363 | 163 |
| 10217 | Icarus Spirit | S80 | SWORD | rhand | 1H (rhand) | 290 | 217 |
| 15281 | Transparent 2HS (for NPC) | S80 | SWORD | lrhand | 2H (lrhand) | 78 | 39 |
| 15280 | Transparent 1HS (for NPC) | S80 | SWORD | rhand | 1H (rhand) | 24 | 17 |

### Jewelry

Pieces: 34.

| ID | Name | Grade | Slot |
|----|------|-------|------|
| 15282 | Gludio Water Royal Guard Necklace | S80 | neck |
| 15283 | Dion Holy Royal Guard Necklace | S80 | neck |
| 15284 | Giran Wind Royal Guard Necklace | S80 | neck |
| 15285 | Oren Dark Royal Guard Necklace | S80 | neck |
| 15286 | Aden Earth Royal Guard Necklace | S80 | neck |
| 15287 | Innadril Water Royal Guard Necklace | S80 | neck |
| 15288 | Goddard Fire Royal Guard Necklace | S80 | neck |
| 15289 | Rune Fire Royal Guard Necklace | S80 | neck |
| 15290 | Schuttgart Wind Royal Guard Necklace | S80 | neck |
| 15725 | Moirai Necklace | S80 | neck |
| 16380 | Moirai Necklace | S80 | neck |
| 10170 | Baylor's Earring | S80 | rear;lear |
| 14801 | Gludio Guard Earring | S80 | rear;lear |
| 14802 | Dion Guard Earring | S80 | rear;lear |
| 14803 | Giran Guard Earring | S80 | rear;lear |
| 14804 | Oren Guard Earring | S80 | rear;lear |
| 14805 | Aden Guard Earring | S80 | rear;lear |
| 14806 | Innadril Guard Earring | S80 | rear;lear |
| 14807 | Goddard Guard Earring | S80 | rear;lear |
| 14808 | Rune Guard Earring | S80 | rear;lear |
| 14809 | Schuttgart Guard Earring | S80 | rear;lear |
| 15724 | Moirai Earring | S80 | rear;lear |
| 16379 | Moirai Earring | S80 | rear;lear |
| 15291 | Gludio Earth Royal Guard Ring | S80 | rfinger;lfinger |
| 15292 | Dion Water Royal Guard Ring | S80 | rfinger;lfinger |
| 15293 | Giran Fire Royal Guard Ring | S80 | rfinger;lfinger |
| 15294 | Oren Earth Royal Guard Ring | S80 | rfinger;lfinger |
| 15295 | Aden Holy Royal Guard Ring | S80 | rfinger;lfinger |
| 15296 | Innadril Holy Royal Guard Ring | S80 | rfinger;lfinger |
| 15297 | Goddard Dark Royal Guard Ring | S80 | rfinger;lfinger |
| 15298 | Rune Wind Royal Guard Ring | S80 | rfinger;lfinger |
| 15299 | Schuttgart Dark Royal Guard Ring | S80 | rfinger;lfinger |
| 15723 | Moirai Ring | S80 | rfinger;lfinger |
| 16378 | Moirai Ring | S80 | rfinger;lfinger |

## 10b. S84 Equipment

### Armor

Pieces: 98 (slot-relevant armor for this grade, noise variants excluded).

| ID | Name | Grade | Armor type | Slot | Set membership |
|----|------|-------|------------|------|----------------|
| 13440 | Vesper Boots | S84 | (none) | feet | s84_vesper_pvp.xml#149 |
| 13443 | Vesper Leather Boots | S84 | (none) | feet | s84_vesper_pvp.xml#150 |
| 13446 | Vesper Shoes | S84 | (none) | feet | s84_vesper_pvp.xml#151 |
| 13450 | Vesper Noble Boots | S84 | (none) | feet | s84_vesper_pvp.xml#152 |
| 13453 | Vesper Noble Leather Boots | S84 | (none) | feet | s84_vesper_pvp.xml#153 |
| 13456 | Vesper Noble Shoes | S84 | (none) | feet | s84_vesper_pvp.xml#154 |
| 15584 | Elegia Boots | S84 | (none) | feet | s84_elegia_pvp.xml#191 |
| 15585 | Elegia Leather Boots | S84 | (none) | feet | s84_elegia_pvp.xml#192 |
| 15586 | Elegia Shoes | S84 | (none) | feet | s84_elegia_pvp.xml#193 |
| 15601 | Vorpal Boots | S84 | (none) | feet | s84_vorpal_pvp.xml#194 |
| 15602 | Vorpal Leather Boots | S84 | (none) | feet | s84_vorpal_pvp.xml#195 |
| 15603 | Vorpal Shoes | S84 | (none) | feet | s84_vorpal_pvp.xml#196 |
| 16318 | Vesper Boots | S84 | (none) | feet | s84_vesper_pvp.xml#149 |
| 16319 | Vesper Leather Boots | S84 | (none) | feet | s84_vesper_pvp.xml#150 |
| 16320 | Vesper Shoes | S84 | (none) | feet | s84_vesper_pvp.xml#151 |
| 16849 | Vesper Noble Boots | S84 | (none) | feet | s84_vesper_pvp.xml#152 |
| 16850 | Vesper Noble Leather Boots | S84 | (none) | feet | s84_vesper_pvp.xml#153 |
| 16851 | Vesper Noble Shoes | S84 | (none) | feet | s84_vesper_pvp.xml#154 |
| 13439 | Vesper Gauntlet | S84 | (none) | gloves | s84_vesper_pvp.xml#149 |
| 13442 | Vesper Leather Gloves | S84 | (none) | gloves | s84_vesper_pvp.xml#150 |
| 13445 | Vesper Gloves | S84 | (none) | gloves | s84_vesper_pvp.xml#151 |
| 13449 | Vesper Noble Gauntlet | S84 | (none) | gloves | s84_vesper_pvp.xml#152 |
| 13452 | Vesper Noble Leather Gloves | S84 | (none) | gloves | s84_vesper_pvp.xml#153 |
| 13455 | Vesper Noble Gloves | S84 | (none) | gloves | s84_vesper_pvp.xml#154 |
| 15581 | Elegia Gauntlet | S84 | (none) | gloves | s84_elegia_pvp.xml#191 |
| 15582 | Elegia Leather Gloves | S84 | (none) | gloves | s84_elegia_pvp.xml#192 |
| 15583 | Elegia Gloves | S84 | (none) | gloves | s84_elegia_pvp.xml#193 |
| 15598 | Vorpal Gauntlet | S84 | (none) | gloves | s84_vorpal_pvp.xml#194 |
| 15599 | Vorpal Leather Gloves | S84 | (none) | gloves | s84_vorpal_pvp.xml#195 |
| 15600 | Vorpal Gloves | S84 | (none) | gloves | s84_vorpal_pvp.xml#196 |
| 16315 | Vesper Gauntlet | S84 | (none) | gloves | s84_vesper_pvp.xml#149 |
| 16316 | Vesper Leather Gloves | S84 | (none) | gloves | s84_vesper_pvp.xml#150 |
| 16317 | Vesper Gloves | S84 | (none) | gloves | s84_vesper_pvp.xml#151 |
| 16846 | Vesper Noble Gauntlet | S84 | (none) | gloves | s84_vesper_pvp.xml#152 |
| 16847 | Vesper Noble Leather Gloves | S84 | (none) | gloves | s84_vesper_pvp.xml#153 |
| 16848 | Vesper Noble Gloves | S84 | (none) | gloves | s84_vesper_pvp.xml#154 |
| 13137 | Vesper Helmet | S84 | (none) | head | s84_vesper_pvp.xml#149 |
| 13138 | Vesper Leather Helmet | S84 | (none) | head | s84_vesper_pvp.xml#150 |
| 13139 | Vesper Circlet | S84 | (none) | head | s84_vesper_pvp.xml#151 |
| 13140 | Vesper Noble Helmet | S84 | (none) | head | s84_vesper_pvp.xml#152 |
| 13141 | Vesper Noble Leather Helmet | S84 | (none) | head | s84_vesper_pvp.xml#153 |
| 13142 | Vesper Noble Circlet | S84 | (none) | head | s84_vesper_pvp.xml#154 |
| 15572 | Elegia Helmet | S84 | (none) | head | s84_elegia_pvp.xml#191 |
| 15573 | Elegia Leather Helmet | S84 | (none) | head | s84_elegia_pvp.xml#192 |
| 15574 | Elegia Circlet | S84 | (none) | head | s84_elegia_pvp.xml#193 |
| 15589 | Vorpal Helmet | S84 | (none) | head | s84_vorpal_pvp.xml#194 |
| 15590 | Vorpal Leather Helmet | S84 | (none) | head | s84_vorpal_pvp.xml#195 |
| 15591 | Vorpal Circlet | S84 | (none) | head | s84_vorpal_pvp.xml#196 |
| 16306 | Vesper Helmet | S84 | (none) | head | s84_vesper_pvp.xml#149 |
| 16307 | Vesper Leather Helmet | S84 | (none) | head | s84_vesper_pvp.xml#150 |
| 16308 | Vesper Circlet | S84 | (none) | head | s84_vesper_pvp.xml#151 |
| 16837 | Vesper Noble Helmet | S84 | (none) | head | s84_vesper_pvp.xml#152 |
| 16838 | Vesper Noble Leather Helmet | S84 | (none) | head | s84_vesper_pvp.xml#153 |
| 16839 | Vesper Noble Circlet | S84 | (none) | head | s84_vesper_pvp.xml#154 |
| 13471 | Vesper Shield | S84 | (none) | lhand | s84_vesper_pvp.xml#152 |
| 15587 | Elegia Shield | S84 | (none) | lhand | s84_elegia_pvp.xml#191 |
| 15604 | Vorpal Shield | S84 | (none) | lhand | s84_vorpal_pvp.xml#194 |
| 16321 | Vesper Shield | S84 | (none) | lhand | s84_vesper_pvp.xml#152 |
| 13432 | Vesper Breastplate | S84 | HEAVY | chest | s84_vesper.xml#143 |
| 13435 | Vesper Noble Breastplate | S84 | HEAVY | chest | s84_vesper.xml#146 |
| 15575 | Elegia Breastplate | S84 | HEAVY | chest | s84_elegia.xml#188 |
| 15592 | Vorpal Breastplate | S84 | HEAVY | chest | s84_vorpal.xml#185 |
| 16309 | Vesper Breastplate | S84 | HEAVY | chest | (not in a set) |
| 16840 | Vesper Noble Breastplate | S84 | HEAVY | chest | (not in a set) |
| 13438 | Vesper Gaiters | S84 | HEAVY | legs | s84_vesper_pvp.xml#149 |
| 13448 | Vesper Noble Gaiters | S84 | HEAVY | legs | s84_vesper_pvp.xml#152 |
| 15578 | Elegia Gaiter | S84 | HEAVY | legs | s84_elegia_pvp.xml#191 |
| 15595 | Vorpal Gaiter | S84 | HEAVY | legs | s84_vorpal_pvp.xml#194 |
| 16312 | Vesper Gaiter | S84 | HEAVY | legs | s84_vesper_pvp.xml#149 |
| 16843 | Vesper Noble Gaiter | S84 | HEAVY | legs | s84_vesper_pvp.xml#152 |
| 13433 | Vesper Leather Breastplate | S84 | LIGHT | chest | s84_vesper.xml#144 |
| 13436 | Vesper Noble Leather Breastplate | S84 | LIGHT | chest | s84_vesper.xml#147 |
| 15576 | Elegia Leather Breastplate | S84 | LIGHT | chest | s84_elegia.xml#189 |
| 15593 | Vorpal Leather Breastplate | S84 | LIGHT | chest | s84_vorpal.xml#186 |
| 16310 | Vesper Leather Breastplate | S84 | LIGHT | chest | (not in a set) |
| 16841 | Vesper Noble Houberk | S84 | LIGHT | chest | (not in a set) |
| 13441 | Vesper Leather Leggings | S84 | LIGHT | legs | s84_vesper_pvp.xml#150 |
| 13451 | Vesper Noble Leather Leggings | S84 | LIGHT | legs | s84_vesper_pvp.xml#153 |
| 15579 | Elegia Leather Legging | S84 | LIGHT | legs | s84_elegia_pvp.xml#192 |
| 15596 | Vorpal Leather Legging | S84 | LIGHT | legs | s84_vorpal_pvp.xml#195 |
| 16313 | Vesper Leather Legging | S84 | LIGHT | legs | s84_vesper_pvp.xml#150 |
| 16844 | Vesper Noble Leather Legging | S84 | LIGHT | legs | s84_vesper_pvp.xml#153 |
| 13434 | Vesper Tunic | S84 | MAGIC | chest | s84_vesper.xml#145 |
| 13437 | Vesper Noble Tunic | S84 | MAGIC | chest | s84_vesper.xml#148 |
| 15577 | Elegia Tunic | S84 | MAGIC | chest | s84_elegia.xml#190 |
| 15594 | Vorpal Tunic | S84 | MAGIC | chest | s84_vorpal.xml#187 |
| 16311 | Vesper Tunic | S84 | MAGIC | chest | (not in a set) |
| 16842 | Vesper Noble Tunic | S84 | MAGIC | chest | (not in a set) |
| 13444 | Vesper Stockings | S84 | MAGIC | legs | s84_vesper_pvp.xml#151 |
| 13454 | Vesper Noble Stockings | S84 | MAGIC | legs | s84_vesper_pvp.xml#154 |
| 15580 | Elegia Stockings | S84 | MAGIC | legs | s84_elegia_pvp.xml#193 |
| 15597 | Vorpal Stockings | S84 | MAGIC | legs | s84_vorpal_pvp.xml#196 |
| 16314 | Vesper Stockings | S84 | MAGIC | legs | s84_vesper_pvp.xml#151 |
| 16845 | Vesper Noble Stockings | S84 | MAGIC | legs | s84_vesper_pvp.xml#154 |
| 12813 | Vesper Sigil | S84 | SIGIL | lhand | (not in a set) |
| 15588 | Elegia Sigil | S84 | SIGIL | lhand | (not in a set) |
| 15605 | Vorpal Sigil | S84 | SIGIL | lhand | (not in a set) |
| 16322 | Vesper Sigil | S84 | SIGIL | lhand | (not in a set) |

### Weapons

Base weapons: 63 (SA/elemental, event, PvP, common, shadow and fortune variants excluded).

| ID | Name | Grade | Weapon type | Slot | Class | pAtk | mAtk |
|----|------|-------|-------------|------|-------|------|------|
| 15556 | Pyseal Blade | S84 | ANCIENTSWORD | lrhand | 2H (lrhand) | 473 | 192 |
| 15570 | Finale Blade | S84 | ANCIENTSWORD | lrhand | 2H (lrhand) | 449 | 183 |
| 15688 | Triumph Ancientsword | S84 | ANCIENTSWORD | lrhand | 2H (lrhand) | 429 | 183 |
| 13470 | Vesper Nagan | S84 | ANCIENTSWORD | lrhand | 2H (lrhand) | 429 | 176 |
| 15547 | Contristo Hammer | S84 | BLUNT | lrhand | 2H (lrhand) | 532 | 192 |
| 15561 | Devilish Maul | S84 | BLUNT | lrhand | 2H (lrhand) | 505 | 183 |
| 13464 | Vesper Retributer | S84 | BLUNT | lrhand | 2H (lrhand) | 482 | 176 |
| 15679 | Triumph Crusher | S84 | BLUNT | lrhand | 2H (lrhand) | 482 | 183 |
| 15546 | Eversor Mace | S84 | BLUNT | rhand | 1H (rhand) | 437 | 192 |
| 15552 | Cyclic Cane | S84 | BLUNT | lrhand | 2H (lrhand) | 426 | 281 |
| 15560 | Vigwik Axe | S84 | BLUNT | rhand | 1H (rhand) | 415 | 183 |
| 15566 | Black Visage | S84 | BLUNT | lrhand | 2H (lrhand) | 404 | 268 |
| 21939 | Claw of Destruction | S84 | BLUNT | rhand | 1H (rhand) | 396 | 176 |
| 13463 | Vesper Avenger | S84 | BLUNT | rhand | 1H (rhand) | 396 | 176 |
| 15678 | Triumph Hammer | S84 | BLUNT | rhand | 1H (rhand) | 396 | 183 |
| 15684 | Triumph Two Hand Staff | S84 | BLUNT | lrhand | 2H (lrhand) | 386 | 268 |
| 13466 | Vesper Singer | S84 | BLUNT | lrhand | 2H (lrhand) | 386 | 257 |
| 15551 | Sacredium | S84 | BLUNT | rhand | 1H (rhand) | 350 | 256 |
| 15565 | Rising Star | S84 | BLUNT | rhand | 1H (rhand) | 332 | 244 |
| 15683 | Triumph Staff | S84 | BLUNT | rhand | 1H (rhand) | 317 | 244 |
| 13465 | Vesper Caster | S84 | BLUNT | rhand | 1H (rhand) | 317 | 234 |
| 15554 | Recurve Thorne Bow | S84 | BOW | lrhand | 2H (lrhand) | 794 | 192 |
| 15568 | Skull Carnium Bow | S84 | BOW | lrhand | 2H (lrhand) | 768 | 183 |
| 15686 | Triumph Bow | S84 | BOW | lrhand | 2H (lrhand) | 724 | 183 |
| 13467 | Vesper Thrower | S84 | BOW | lrhand | 2H (lrhand) | 724 | 176 |
| 15557 | Thorne Crossbow | S84 | CROSSBOW | lrhand | 2H (lrhand) | 487 | 192 |
| 15571 | Dominion Crossbow | S84 | CROSSBOW | lrhand | 2H (lrhand) | 471 | 183 |
| 15689 | Triumph Crossbow | S84 | CROSSBOW | lrhand | 2H (lrhand) | 444 | 183 |
| 13469 | Vesper Shooter | S84 | CROSSBOW | lrhand | 2H (lrhand) | 444 | 176 |
| 15545 | Mamba Edge | S84 | DAGGER | rhand | 1H (rhand) | 382 | 192 |
| 15559 | Skull Edge | S84 | DAGGER | rhand | 1H (rhand) | 363 | 183 |
| 13460 | Vesper Shaper | S84 | DAGGER | rhand | 1H (rhand) | 346 | 176 |
| 15677 | Triumph Dagger | S84 | DAGGER | rhand | 1H (rhand) | 346 | 183 |
| 16158 | Eternal Core Dual Sword | S84 | DUAL | lrhand | dual (lrhand) | 532 | 192 |
| 16154 | Periel Dual Sword | S84 | DUAL | lrhand | dual (lrhand) | 505 | 183 |
| 52 | Vesper Dual Sword | S84 | DUAL | lrhand | dual (lrhand) | 482 | 176 |
| 16156 | Mamba Edge Dual Daggers | S84 | DUALDAGGER | lrhand | dual dagger (lrhand) | 437 | 192 |
| 16152 | Skull Edge Dual Daggers | S84 | DUALDAGGER | lrhand | dual dagger (lrhand) | 415 | 183 |
| 13884 | Vesper Dual Daggers | S84 | DUALDAGGER | lrhand | dual dagger (lrhand) | 360 | 181 |
| 15549 | Jade Claw | S84 | DUALFIST | lrhand | dual fist (lrhand) | 532 | 192 |
| 15563 | Octo Claw | S84 | DUALFIST | lrhand | dual fist (lrhand) | 505 | 183 |
| 13461 | Vesper Fighter | S84 | DUALFIST | lrhand | dual fist (lrhand) | 482 | 176 |
| 15681 | Triumph Jamadhr | S84 | DUALFIST | lrhand | dual fist (lrhand) | 482 | 183 |
| 15550 | Demitelum | S84 | POLE | lrhand | 2H (lrhand) | 437 | 192 |
| 15564 | Doubletop Spear | S84 | POLE | lrhand | 2H (lrhand) | 415 | 183 |
| 15682 | Triumph Spear | S84 | POLE | lrhand | 2H (lrhand) | 396 | 183 |
| 13462 | Vesper Stormer | S84 | POLE | lrhand | 2H (lrhand) | 396 | 176 |
| 15555 | Heavenstare Rapier | S84 | RAPIER | rhand | 1H (rhand) | 396 | 192 |
| 15569 | Gemtail Rapier | S84 | RAPIER | rhand | 1H (rhand) | 376 | 183 |
| 13468 | Vesper Pincer | S84 | RAPIER | rhand | 1H (rhand) | 359 | 176 |
| 15548 | Lava Saw | S84 | SWORD | lrhand | 2H (lrhand) | 532 | 192 |
| 15562 | Feather Eye Blade | S84 | SWORD | lrhand | 2H (lrhand) | 505 | 183 |
| 13458 | Vesper Slasher | S84 | SWORD | lrhand | 2H (lrhand) | 482 | 176 |
| 15680 | Triumph Two Hand Sword | S84 | SWORD | lrhand | 2H (lrhand) | 482 | 183 |
| 15544 | Eternal Core Sword | S84 | SWORD | rhand | 1H (rhand) | 437 | 192 |
| 15558 | Periel Sword | S84 | SWORD | rhand | 1H (rhand) | 415 | 183 |
| 21919 | Hellblade | S84 | SWORD | rhand | 1H (rhand) | 396 | 176 |
| 15676 | Triumph Blade | S84 | SWORD | rhand | 1H (rhand) | 396 | 183 |
| 13457 | Vesper Cutter | S84 | SWORD | rhand | 1H (rhand) | 396 | 176 |
| 15553 | Archangel Sword | S84 | SWORD | rhand | 1H (rhand) | 350 | 256 |
| 15567 | Veniplant Sword | S84 | SWORD | rhand | 1H (rhand) | 332 | 244 |
| 15685 | Triumph Magic Sword | S84 | SWORD | rhand | 1H (rhand) | 317 | 244 |
| 13459 | Vesper Buster | S84 | SWORD | rhand | 1H (rhand) | 317 | 234 |

### Jewelry

Pieces: 16.

| ID | Name | Grade | Slot |
|----|------|-------|------|
| 14164 | Vesper Necklace | S84 | neck |
| 15719 | Elegia Necklace | S84 | neck |
| 15722 | Vorpal Necklace | S84 | neck |
| 16025 | Necklace of Freya | S84 | neck |
| 16026 | Blessed Necklace of Freya | S84 | neck |
| 16377 | Vesper Necklace | S84 | neck |
| 14163 | Vesper Earring | S84 | rear;lear |
| 15718 | Elegia Earring | S84 | rear;lear |
| 15721 | Vorpal Earring | S84 | rear;lear |
| 16376 | Vesper Earring | S84 | rear;lear |
| 21712 | Blessed Earring of Zaken | S84 | rear;lear |
| 22175 | Improved Blessed Earring of Zaken | S84 | rear;lear |
| 14165 | Vesper Ring | S84 | rfinger;lfinger |
| 15717 | Elegia Ring | S84 | rfinger;lfinger |
| 15720 | Vorpal Ring | S84 | rfinger;lfinger |
| 16375 | Vesper Ring | S84 | rfinger;lfinger |

## 11. Elegia / Endgame Equipment

Endgame families found in the TARGET datapack (all VERIFIED by reading their set files):

| Family | Grade (crystal_type) | Set file | Pieces |
|--------|----------------------|----------|--------|
| Imperial Crusader | S | s_grade.xml | 3 set(s) |
| Draconic Leather | S | s_grade.xml | 3 set(s) |
| Major Arcana Robe | S | s_grade.xml | 3 set(s) |
| Dynasty | S | s80_dynasty.xml | 31 set(s) |
| Moirai | S80 | s80_moirai.xml | 3 set(s) |
| Vesper | S84 | s84_vesper.xml | 6 set(s) |
| Vorpal | S84 | s84_vorpal.xml | 3 set(s) |
| Elegia | S84 | s84_elegia.xml | 3 set(s) |

NOTE: the file `s80_dynasty.xml` declares sets whose items are `crystal_type=S` (see section 18, anomaly A4).
NOTE: `Vesper Noble` exists as an upgrade tier inside `s84_vesper.xml` (sets 146-148).
NOTE: shields and sigils exist for S (Imperial Crusader 6377, Dynasty 9441), S80 (Moirai 15621) and S84 (Vesper 13471, Vorpal 15604, Elegia 15587).

## 12. Armor Sets

Source: `game/data/stats/armorsets/*.xml` (21 files). One line per set block.
`skill` is the main set skill id declared inside the set block. `[DISABLED/RESERVED]` marks sets that are commented out in the XML (present but inactive).

Active sets: 204 | Disabled/reserved: 13

- a_grade.xml #39 skill=3006 :: chest:365 (Dark Crystal Breastplate) ; legs:388 (Dark Crystal Gaiters) ; legs:11407 (Dark Crystal Gaiters) ; head:512 (Dark Crystal Helmet) ; head:11417 (Dark Crystal Helmet - Heavy) ; gloves:5765 (Dark Crystal Gloves - Heavy) ; gloves:11408 (Dark Crystal Gloves - Heavy) ; feet:5777 (Dark Crystal Boots - Heavy) ; feet:11413 (Dark Crystal Boots - Heavy) ; shield:641 (Dark Crystal Shield) ; shield:11416 (Dark Crystal Shield)
- a_grade.xml #40 skill=3006 :: chest:2382 (Tallum Plate Armor) ; head:547 (Tallum Helm) ; head:11446 (Tallum Helmet - Heavy) ; gloves:5768 (Tallum Gloves - Heavy) ; gloves:11437 (Tallum Gloves - Heavy) ; feet:5780 (Tallum Boots - Heavy) ; feet:11441 (Tallum Boots - Heavy)
- a_grade.xml #41 skill=3006 :: chest:2385 (Dark Crystal Leather Armor) ; legs:2389 (Dark Crystal Leggings) ; legs:11419 (Dark Crystal Leggings) ; head:512 (Dark Crystal Helmet) ; head:12986 (Dark Crystal Helmet - Light Use) ; gloves:5766 (Dark Crystal Gloves - Light) ; gloves:11409 (Dark Crystal Gloves - Light Use) ; feet:5778 (Dark Crystal Boots - Light) ; feet:11414 (Dark Crystal Boots - Light Use)
- a_grade.xml #42 skill=3006 :: chest:2393 (Tallum Leather Armor) ; head:547 (Tallum Helm) ; head:12988 (Tallum Helmet - Light Use) ; gloves:5769 (Tallum Gloves - Light) ; gloves:11438 (Tallum Gloves - Light Use) ; feet:5781 (Tallum Boots - Light) ; feet:11442 (Tallum Boots - Light Use)
- a_grade.xml #43 skill=3006 :: chest:2400 (Tallum Tunic) ; legs:2405 (Tallum Stockings) ; legs:11447 (Tallum Stockings) ; head:547 (Tallum Helm) ; head:12989 (Tallum Helmet - Robe) ; gloves:5770 (Tallum Gloves - Robe) ; gloves:11439 (Tallum Gloves - Robe) ; feet:5782 (Tallum Boots - Robe) ; feet:11443 (Tallum Boots - Robe)
- a_grade.xml #44 skill=3006 :: chest:2407 (Dark Crystal Robe) ; head:512 (Dark Crystal Helmet) ; head:12987 (Dark Crystal Helmet - Robe) ; gloves:5767 (Dark Crystal Gloves - Robe) ; gloves:11410 (Dark Crystal Gloves - Robe) ; feet:5779 (Dark Crystal Boots - Robe) ; feet:11415 (Dark Crystal Boots - Robe)
- a_grade.xml #46 skill=3006 :: chest:374 (Armor of Nightmare) ; head:2418 (Helm of Nightmare) ; head:11481 (Helm of Nightmare - Heavy) ; gloves:5771 (Gauntlets of Nightmare - Heavy) ; gloves:11472 (Gauntlet of Nightmare - Heavy) ; feet:5783 (Boots of Nightmare - Heavy) ; feet:11477 (Boots of Nightmare - Heavy) ; shield:2498 (Shield of Nightmare) ; shield:11480 (Shield of Nightmare)
- a_grade.xml #47 skill=3006 :: chest:2383 (Majestic Plate Armor) ; head:2419 (Majestic Circlet) ; head:11456 (Majestic Circlet - Heavy) ; gloves:5774 (Majestic Gauntlets - Heavy) ; gloves:11448 (Majestic Gauntlet - Heavy) ; feet:5786 (Majestic Boots - Heavy) ; feet:11453 (Majestic Boots - Heavy)
- a_grade.xml #48 skill=3006 :: chest:2394 (Leather Armor of Nightmare) ; head:2418 (Helm of Nightmare) ; head:12992 (Helm of Nightmare - Light Use) ; gloves:5772 (Gauntlets of Nightmare - Light) ; gloves:11473 (Gauntlet of Nightmare - Light Use) ; feet:5784 (Boots of Nightmare - Light) ; feet:11478 (Boots of Nightmare - Light Use)
- a_grade.xml #49 skill=3006 :: chest:2395 (Majestic Leather Armor) ; head:2419 (Majestic Circlet) ; head:12990 (Majestic Circlet - Light Use) ; gloves:5775 (Majestic Gauntlets - Light) ; gloves:11449 (Majestic Gauntlet - Light Use) ; feet:5787 (Majestic Boots - Light) ; feet:11454 (Majestic Boots - Light Use)
- a_grade.xml #50 skill=3006 :: chest:2408 (Robe of Nightmare) ; head:2418 (Helm of Nightmare) ; head:12993 (Helm of Nightmare - Robe) ; gloves:5773 (Gauntlets of Nightmare - Robe) ; gloves:11474 (Gauntlet of Nightmare - Robe) ; feet:5785 (Boots of Nightmare - Robe) ; feet:11479 (Boots of Nightmare - Robe)
- a_grade.xml #51 skill=3006 :: chest:2409 (Majestic Robe) ; head:2419 (Majestic Circlet) ; head:12991 (Majestic Circlet - Robe) ; gloves:5776 (Majestic Gauntlets - Robe) ; gloves:11450 (Majestic Gauntlet - Robe) ; feet:5788 (Majestic Boots - Robe) ; feet:11455 (Majestic Boots - Robe)
- a_grade_pvp.xml #103 skill=3006 :: chest:10793 (Armor of Nightmare {PvP}) ; head:2418 (Helm of Nightmare) ; head:11481 (Helm of Nightmare - Heavy) ; gloves:5771 (Gauntlets of Nightmare - Heavy) ; gloves:11472 (Gauntlet of Nightmare - Heavy) ; feet:5783 (Boots of Nightmare - Heavy) ; feet:11477 (Boots of Nightmare - Heavy) ; shield:2498 (Shield of Nightmare) ; shield:11480 (Shield of Nightmare)
- a_grade_pvp.xml #104 skill=3006 :: chest:10794 (Majestic Plate Armor {PvP}) ; head:2419 (Majestic Circlet) ; head:11456 (Majestic Circlet - Heavy) ; gloves:5774 (Majestic Gauntlets - Heavy) ; gloves:11448 (Majestic Gauntlet - Heavy) ; feet:5786 (Majestic Boots - Heavy) ; feet:11453 (Majestic Boots - Heavy)
- a_grade_pvp.xml #105 skill=3006 :: chest:10795 (Leather Armor of Nightmare {PvP}) ; head:2418 (Helm of Nightmare) ; head:12992 (Helm of Nightmare - Light Use) ; gloves:5772 (Gauntlets of Nightmare - Light) ; gloves:11473 (Gauntlet of Nightmare - Light Use) ; feet:5784 (Boots of Nightmare - Light) ; feet:11478 (Boots of Nightmare - Light Use)
- a_grade_pvp.xml #106 skill=3006 :: chest:10796 (Majestic Leather Armor {PvP}) ; head:2419 (Majestic Circlet) ; head:12990 (Majestic Circlet - Light Use) ; gloves:5775 (Majestic Gauntlets - Light) ; gloves:11449 (Majestic Gauntlet - Light Use) ; feet:5787 (Majestic Boots - Light) ; feet:11454 (Majestic Boots - Light Use)
- a_grade_pvp.xml #107 skill=3006 :: chest:10797 (Robe of Nightmare {PvP}) ; head:2418 (Helm of Nightmare) ; head:12993 (Helm of Nightmare - Robe) ; gloves:5773 (Gauntlets of Nightmare - Robe) ; gloves:11474 (Gauntlet of Nightmare - Robe) ; feet:5785 (Boots of Nightmare - Robe) ; feet:11479 (Boots of Nightmare - Robe)
- a_grade_pvp.xml #108 skill=3006 :: chest:10798 (Majestic Robe {PvP}) ; head:2419 (Majestic Circlet) ; head:12991 (Majestic Circlet - Robe) ; gloves:5776 (Majestic Gauntlets - Robe) ; gloves:11450 (Majestic Gauntlet - Robe) ; feet:5788 (Majestic Boots - Robe) ; feet:11455 (Majestic Boots - Robe)
- b_grade.xml #25 skill=3006 :: chest:357 (Zubei's Breastplate) ; legs:383 (Zubei's Gaiters) ; legs:11355 (Zubei's Gaiters) ; head:503 (Zubei's Helmet) ; head:11363 (Zubei's Helmet - Heavy) ; gloves:5710 (Zubei's Gauntlets - Heavy) ; gloves:11356 (Zubei's Gauntlet - Heavy) ; feet:5726 (Zubei's Boots - Heavy) ; feet:11359 (Zubei's Boots - Heavy)
- b_grade.xml #26 skill=3006 :: chest:2376 (Avadon Breastplate) ; legs:2379 (Avadon Gaiters) ; legs:11375 (Avadon Gaiters) ; head:2415 (Avadon Circlet) ; head:11373 (Avadon Circlet - Heavy) ; gloves:5714 (Avadon Gloves - Heavy) ; gloves:11365 (Avadon Gloves - Heavy) ; feet:5730 (Avadon Boots - Heavy) ; feet:11370 (Avadon Boots - Heavy) ; shield:673 (Avadon Shield) ; shield:11374 (Avadon Shield)
- b_grade.xml #27 skill=3006 :: chest:2384 (Zubei's Leather Shirt) ; legs:2388 (Zubei's Leather Gaiters) ; legs:11353 (Zubei's Leather Gaiters) ; head:503 (Zubei's Helmet) ; head:12978 (Zubei's Helmet - Light Use) ; gloves:5711 (Zubei's Gauntlets - Light) ; gloves:11357 (Zubei's Gauntlet - Light Use) ; feet:5727 (Zubei's Boots - Light) ; feet:11360 (Zubei's Boots - Light Use)
- b_grade.xml #28 skill=3006 :: chest:2390 (Avadon Leather Armor) ; head:2415 (Avadon Circlet) ; head:12980 (Avadon Circlet - Light Use) ; gloves:5715 (Avadon Gloves - Light) ; gloves:11366 (Avadon Gloves - Light Use) ; feet:5731 (Avadon Boots - Light) ; feet:11371 (Avadon Boots - Light Use)
- b_grade.xml #29 skill=3006 :: chest:2397 (Tunic of Zubei) ; legs:2402 (Stockings of Zubei) ; legs:11378 (Stockings of Zubei) ; head:503 (Zubei's Helmet) ; head:12979 (Zubei's Helmet - Robe) ; gloves:5712 (Zubei's Gauntlets - Robe) ; gloves:11358 (Zubei's Gauntlet - Robe) ; feet:5728 (Zubei's Boots - Robe) ; feet:11361 (Zubei's Boots - Robe)
- b_grade.xml #30 skill=3006 :: chest:2406 (Avadon Robe) ; head:2415 (Avadon Circlet) ; head:12981 (Avadon Circlet - Robe) ; gloves:5716 (Avadon Gloves - Robe) ; gloves:11367 (Avadon Gloves - Robe) ; feet:5732 (Avadon Boots - Robe) ; feet:11372 (Avadon Boots - Robe)
- b_grade.xml #32 skill=3006 :: chest:358 (Blue Wolf Breastplate) ; legs:2380 (Blue Wolf Gaiters) ; legs:11394 (Blue Wolf Gaiters) ; head:2416 (Blue Wolf Helmet) ; head:11403 (Blue Wolf Helmet - Heavy) ; gloves:5718 (Blue Wolf Gloves - Heavy) ; gloves:11399 (Blue Wolf Gloves - Heavy) ; feet:5734 (Blue Wolf Boots - Heavy) ; feet:11396 (Blue Wolf Boots - Heavy)
- b_grade.xml #33 skill=3006 :: chest:2381 (Doom Plate Armor) ; head:2417 (Doom Helmet) ; head:11387 (Doom Helmet - Heavy) ; gloves:5722 (Doom Gloves - Heavy) ; gloves:11379 (Doom Gloves - Heavy) ; feet:5738 (Doom Boots - Heavy) ; feet:11382 (Doom Boots - Heavy) ; shield:110 (Doom Shield) ; shield:11385 (Doom Shield)
- b_grade.xml #34 skill=3006 :: chest:2391 (Blue Wolf Leather Armor) ; head:2416 (Blue Wolf Helmet) ; head:12984 (Blue Wolf Helmet - Light Use) ; gloves:5719 (Blue Wolf Gloves - Light) ; gloves:11400 (Blue Wolf Gloves - Light Use) ; feet:5735 (Blue Wolf Boots - Light) ; feet:11397 (Blue Wolf Boots - Light Use)
- b_grade.xml #35 skill=3006 :: chest:2392 (Leather Armor of Doom) ; head:2417 (Doom Helmet) ; head:12982 (Doom Helmet - Light Use) ; gloves:5723 (Doom Gloves - Light) ; gloves:11380 (Doom Gloves - Light Use) ; feet:5739 (Doom Boots - Light) ; feet:11383 (Doom Boots - Light Use)
- b_grade.xml #36 skill=3006 :: chest:2398 (Blue Wolf Tunic) ; legs:2403 (Blue Wolf Stockings) ; legs:11404 (Blue Wolf Stockings) ; head:2416 (Blue Wolf Helmet) ; head:12985 (Blue Wolf Helmet - Robe) ; gloves:5720 (Blue Wolf Gloves - Robe) ; gloves:11401 (Blue Wolf Gloves - Robe) ; feet:5736 (Blue Wolf Boots - Robe) ; feet:11398 (Blue Wolf Boots - Robe)
- b_grade.xml #37 skill=3006 :: chest:2399 (Tunic of Doom) ; legs:2404 (Stockings of Doom) ; legs:11406 (Stockings of Doom) ; head:2417 (Doom Helmet) ; head:12983 (Doom Helmet - Robe) ; gloves:5724 (Doom Gloves - Robe) ; gloves:11381 (Doom Gloves - Robe) ; feet:5740 (Doom Boots - Robe) ; feet:11384 (Doom Boots - Robe)
- c_grade.xml #12 skill=3006 :: chest:397 (Mithril Shirt) ; legs:2387 (Reinforced Mithril Gaiters) ; feet:62 (Reinforced Mithril Boots)
- c_grade.xml #13 skill=3006 :: chest:354 (Chain Mail Shirt) ; legs:381 (Chain Gaiters) ; head:2413 (Chain Hood) ; shield:2495 (Chain Shield)
- c_grade.xml #14 skill=3006 :: chest:439 (Karmian Tunic) ; legs:471 (Karmian Stockings) ; gloves:2454 (Karmian Gloves)
- c_grade.xml #15 skill=3006 :: chest:398 (Plated Leather) ; legs:418 (Plated Leather Gaiters) ; feet:2431 (Plated Leather Boots)
- c_grade.xml #19 skill=3006 :: chest:60 (Compound Armor) ; head:517 (Compound Helmet) ; shield:107 (Compound Shield)
- c_grade.xml #20 skill=3006 :: chest:441 (Demon's Tunic) ; legs:472 (Demon's Stockings) ; gloves:2459 (Demon's Gloves)
- c_grade.xml #21 skill=3006 :: chest:400 (Theca Leather Armor) ; legs:420 (Theca Leather Gaiters) ; feet:2436 (Theca Leather Boots)
- c_grade.xml #22 skill=3006 :: chest:401 (Drake Leather Armor) ; feet:2437 (Drake Leather Boots)
- c_grade.xml #23 skill=3006 :: chest:356 (Full Plate Armor) ; head:2414 (Full Plate Helmet) ; shield:2497 (Full Plate Shield)
- c_grade.xml #24 skill=3006 :: chest:442 (Divine Tunic) ; legs:473 (Divine Stockings) ; gloves:2463 (Divine Gloves)
- clan.xml #155 skill=3006 :: chest:14583 (Apella Combat Armor) ; head:14582 (Apella Combat Helmet) ; gloves:14584 (Apella Combat Gauntlet - Heavy) ; feet:14585 (Apella Combat Boots - Heavy)
- clan.xml #156 skill=3006 :: chest:14586 (Apella Combat Clothes) ; head:14582 (Apella Combat Helmet) ; gloves:14587 (Apella Combat Leather Gloves - Tans Armor Use) ; feet:14588 (Apella Combat Shoes - Tans Armor Use)
- clan.xml #157 skill=3006 :: chest:14589 (Apella Combat Overcoat) ; head:14582 (Apella Combat Helmet) ; gloves:14590 (Apella Combat Silk Gloves - Robe) ; feet:14591 (Apella Combat Sandals - Robe)
- clan.xml #59 skill=3006 :: chest:7851 (Clan Oath Armor) ; head:7850 (Clan Oath Helm) ; gloves:7852 (Clan Oath Gauntlets - Heavy) ; feet:7853 (Clan Oath Sabaton - Heavy)
- clan.xml #60 skill=3006 :: chest:7854 (Clan Oath Brigandine) ; head:7850 (Clan Oath Helm) ; gloves:7855 (Clan Oath Leather Gloves - Light) ; feet:7856 (Clan Oath Boots - Light)
- clan.xml #61 skill=3006 :: chest:7857 (Clan Oath Aketon) ; head:7850 (Clan Oath Helm) ; gloves:7858 (Clan Oath Padded Gloves - Robe) ; feet:7859 (Clan Oath Sandals - Robe)
- clan.xml #62 skill=3006 :: chest:7861 (Apella Plate Armor) ; head:7860 (Apella Helm) ; gloves:7862 (Apella Gauntlet - Heavy) ; feet:7863 (Apella Solleret - Heavy)
- clan.xml #63 skill=3006 :: chest:7864 (Apella Brigandine) ; head:7860 (Apella Helm) ; gloves:7865 (Apella Leather Gloves - Light) ; feet:7866 (Apella Boots - Light)
- clan.xml #64 skill=3006 :: chest:7867 (Apella Doublet) ; head:7860 (Apella Helm) ; gloves:7868 (Apella Silk Gloves - Robe) ; feet:7869 (Apella Sandals - Robe)
- clan.xml #75 skill=3006 :: chest:9821 (Shadow Item: Clan Oath Armor) ; head:9820 (Shadow Item: Clan Oath Helm) ; gloves:9822 (Shadow Item: Clan Oath Gauntlets - Heavy) ; feet:9823 (Shadow Item: Clan Oath Sabaton - Heavy)
- clan.xml #76 skill=3006 :: chest:9824 (Shadow Item: Clan Oath Brigandine) ; head:9820 (Shadow Item: Clan Oath Helm) ; gloves:9825 (Shadow Item: Clan Oath Leather Gloves - Light) ; feet:9826 (Shadow Item: Clan Oath Boots - Light)
- clan.xml #77 skill=3006 :: chest:9827 (Shadow Item: Clan Oath Aketon) ; head:9820 (Shadow Item: Clan Oath Helm) ; gloves:9828 (Shadow Item: Clan Oath Padded Gloves - Robe) ; feet:9829 (Shadow Item: Clan Oath Sandals - Robe)
- clan.xml #78 skill=3006 :: chest:9831 (Improved Apella Plate Armor) ; head:9830 (Improved Apella Helm) ; gloves:9832 (Improved Apella Gauntlet - Heavy) ; feet:9833 (Improved Apella Solleret - Heavy)
- clan.xml #79 skill=3006 :: chest:9834 (Improved Apella Brigandine) ; head:9830 (Improved Apella Helm) ; gloves:9835 (Improved Apella Leather Gloves - Light) ; feet:9836 (Improved Apella Boots - Light)
- clan.xml #80 skill=3006 :: chest:9837 (Improved Apella Doublet) ; head:9830 (Improved Apella Helm) ; gloves:9838 (Improved Apella Silk Gloves - Robe) ; feet:9839 (Improved Apella Sandals - Robe)
- d_grade.xml #3 skill=3006 :: chest:58 (Mithril Breastplate) ; legs:59 (Mithril Gaiters) ; head:47 (Helmet) ; shield:628 (Hoplon)
- d_grade.xml #4 skill=3006 :: chest:394 (Reinforced Leather Shirt) ; legs:416 (Reinforced Leather Gaiters) ; feet:2422 (Reinforced Leather Boots)
- d_grade.xml #5 skill=3006 :: chest:436 (Tunic of Knowledge) ; legs:469 (Stockings of Knowledge) ; gloves:2447 (Gloves of Knowledge)
- d_grade.xml #6 skill=3006 :: chest:395 (Manticore Skin Shirt) ; legs:417 (Manticore Skin Gaiters) ; feet:2424 (Manticore Skin Boots)
- d_grade.xml #7 skill=3006 :: chest:352 (Brigandine Tunic) ; legs:2378 (Brigandine Gaiters) ; head:2411 (Brigandine Helmet) ; shield:2493 (Brigandine Shield)
- d_grade.xml #8 skill=3006 :: chest:437 (Mithril Tunic) ; legs:470 (Mithril Stockings) ; gloves:2450 (Mithril Gloves)
- friendship.xml #158 skill=3006 :: chest:15092 (Dark Crystal Robe of Fortune - 10-day limited period) ; head:15093 (Dark Crystal Helmet of Fortune - 10-day limited period) ; head:14979 (Dark Crystal Helmet of Fortune - 30-day limited period) ; gloves:15094 (Dark Crystal Gloves of Fortune - Robe - 10-day limited period) ; gloves:14980 (Dark Crystal Gloves of Fortune - Robe - 30-day limited period) ; feet:15095 (Dark Crystal Boots of Fortune - Robe - 10-day limited period) ; feet:14981 (Dark Crystal Boots of Fortune - Robe - 30-day limited period)
- friendship.xml #159 skill=3006 :: chest:14978 (Dark Crystal Robe of Fortune - 30-day limited period) ; head:15093 (Dark Crystal Helmet of Fortune - 10-day limited period) ; head:14979 (Dark Crystal Helmet of Fortune - 30-day limited period) ; gloves:15094 (Dark Crystal Gloves of Fortune - Robe - 10-day limited period) ; gloves:14980 (Dark Crystal Gloves of Fortune - Robe - 30-day limited period) ; feet:15095 (Dark Crystal Boots of Fortune - Robe - 10-day limited period) ; feet:14981 (Dark Crystal Boots of Fortune - Robe - 30-day limited period)
- friendship.xml #160 skill=3006 :: chest:15097 (Avadon Robe of Fortune - 10-day limited period) ; head:14984 (Avadon Circlet of Fortune - 30-day limited period) ; head:15098 (Avadon Circlet of Fortune - 10-day limited period) ; gloves:14985 (Avadon Gloves of Fortune - Robe - 30-day limited period) ; gloves:15099 (Avadon Gloves of Fortune - Robe - 10-day limited period) ; feet:14986 (Avadon Boots of Fortune - Robe - 30-day limited period) ; feet:15100 (Avadon Boots of Fortune - Robe - 10-day limited period)
- friendship.xml #161 skill=3006 :: chest:14983 (Avadon Robe of Fortune - 30-day limited period) ; head:14984 (Avadon Circlet of Fortune - 30-day limited period) ; head:15098 (Avadon Circlet of Fortune - 10-day limited period) ; gloves:14985 (Avadon Gloves of Fortune - Robe - 30-day limited period) ; gloves:15099 (Avadon Gloves of Fortune - Robe - 10-day limited period) ; feet:14986 (Avadon Boots of Fortune - Robe - 30-day limited period) ; feet:15100 (Avadon Boots of Fortune - Robe - 10-day limited period)
- friendship.xml #162 skill=3006 :: chest:15102 (Karmian Tunic of Fortune - 10-day limited period) ; legs:15103 (Karmian Stockings of Fortune - 10-day limited period) ; legs:14989 (Karmian Stockings of Fortune - 30-day limited period) ; gloves:15104 (Karmian Gloves of Fortune - 10-day limited period) ; gloves:14990 (Karmian Gloves of Fortune - 30-day limited period)
- friendship.xml #163 skill=3006 :: chest:14988 (Karmian Tunic of Fortune - 30-day limited period) ; legs:15103 (Karmian Stockings of Fortune - 10-day limited period) ; legs:14989 (Karmian Stockings of Fortune - 30-day limited period) ; gloves:15104 (Karmian Gloves of Fortune - 10-day limited period) ; gloves:14990 (Karmian Gloves of Fortune - 30-day limited period)
- friendship.xml #164 skill=3006 :: chest:15106 (Mithril Tunic of Fortune - 10-day limited period) ; legs:15107 (Mithril Stockings of Fortune - 10-day limited period) ; legs:14993 (Mithril Stockings of Fortune - 30-day limited period) ; gloves:15108 (Elven Mithril Gloves of Fortune - 10-day limited period) ; gloves:14994 (Elven Mithril Gloves of Fortune - 30-day limited period)
- friendship.xml #165 skill=3006 :: chest:14992 (Mithril Tunic of Fortune - 30-day limited period) ; legs:15107 (Mithril Stockings of Fortune - 10-day limited period) ; legs:14993 (Mithril Stockings of Fortune - 30-day limited period) ; gloves:15108 (Elven Mithril Gloves of Fortune - 10-day limited period) ; gloves:14994 (Elven Mithril Gloves of Fortune - 30-day limited period)
- friendship.xml #166 skill=3006 :: chest:15110 (Dark Crystal Leather Armor of Fortune - 10-day limited period) ; legs:15111 (Dark Crystal Leggings of Fortune - 10-day limited period) ; legs:14997 (Dark Crystal Leggings of Fortune - 30-day limited period) ; head:15093 (Dark Crystal Helmet of Fortune - 10-day limited period) ; head:14979 (Dark Crystal Helmet of Fortune - 30-day limited period) ; gloves:15112 (Dark Crystal Gloves of Fortune - Light Armor - 10-day limited period) ; gloves:14998 (Dark Crystal Gloves of Fortune - Light Armor - 30-day limited period) ; feet:15113 (Dark Crystal Boots of Fortune - Light Armor - 10-day limited period) ; feet:14999 (Dark Crystal Boots of Fortune - Light Armor - 30-day limited period)
- friendship.xml #167 skill=3006 :: chest:14996 (Dark Crystal Leather Armor of Fortune - 30-day limited period) ; legs:15111 (Dark Crystal Leggings of Fortune - 10-day limited period) ; legs:14997 (Dark Crystal Leggings of Fortune - 30-day limited period) ; head:15093 (Dark Crystal Helmet of Fortune - 10-day limited period) ; head:14979 (Dark Crystal Helmet of Fortune - 30-day limited period) ; gloves:15112 (Dark Crystal Gloves of Fortune - Light Armor - 10-day limited period) ; gloves:14998 (Dark Crystal Gloves of Fortune - Light Armor - 30-day limited period) ; feet:15113 (Dark Crystal Boots of Fortune - Light Armor - 10-day limited period) ; feet:14999 (Dark Crystal Boots of Fortune - Light Armor - 30-day limited period)
- friendship.xml #168 skill=3006 :: chest:15114 (Leather Armor of Doom of Fortune - 10-day limited period) ; head:15115 (Doom Helmet of Fortune - 10-day limited period) ; head:15001 (Doom Helmet of Fortune - 30-day limited period) ; gloves:15117 (Doom Gloves of Fortune - Light Armor - 10-day limited period) ; gloves:15003 (Doom Gloves of Fortune - Light Armor - 30-day limited period) ; feet:15119 (Doom Boots of Fortune - Light Armor - 10-day limited period) ; feet:15005 (Doom Boots of Fortune - Light Armor - 30-day limited period)
- friendship.xml #169 skill=3006 :: chest:15000 (Leather Armor of Doom of Fortune - 30-day limited period) ; head:15115 (Doom Helmet of Fortune - 10-day limited period) ; head:15001 (Doom Helmet of Fortune - 30-day limited period) ; gloves:15117 (Doom Gloves of Fortune - Light Armor - 10-day limited period) ; gloves:15003 (Doom Gloves of Fortune - Light Armor - 30-day limited period) ; feet:15119 (Doom Boots of Fortune - Light Armor - 10-day limited period) ; feet:15005 (Doom Boots of Fortune - Light Armor - 30-day limited period)
- friendship.xml #170 skill=3006 :: chest:15122 (Plate Leather Armor of Fortune - 10-day limited period) ; legs:15123 (Plate Leather Gaiters of Fortune - 10-day limited period) ; legs:15009 (Plate Leather Gaiters of Fortune - 30-day limited period) ; feet:15124 (Plate Leather Boots of Fortune - 10-day limited period) ; feet:15010 (Plate Leather Boots of Fortune - 30-day limited period)
- friendship.xml #171 skill=3006 :: chest:15008 (Plate Leather Armor of Fortune - 30-day limited period) ; legs:15123 (Plate Leather Gaiters of Fortune - 10-day limited period) ; legs:15009 (Plate Leather Gaiters of Fortune - 30-day limited period) ; feet:15124 (Plate Leather Boots of Fortune - 10-day limited period) ; feet:15010 (Plate Leather Boots of Fortune - 30-day limited period)
- friendship.xml #172 skill=3006 :: chest:15131 (Manticore Skin Shirt of Fortune - 10-day limited period) ; legs:15132 (Manticore Skin Gaiters of Fortune - 10-day limited period) ; legs:15018 (Manticore Skin Gaiters of Fortune - 30-day limited period) ; feet:15133 (Manticore Skin Boots of Fortune - 10-day limited period) ; feet:15019 (Manticore Skin Boots of Fortune - 30-day limited period)
- friendship.xml #173 skill=3006 :: chest:15017 (Manticore Skin Shirt of Fortune - 30-day limited period) ; legs:15132 (Manticore Skin Gaiters of Fortune - 10-day limited period) ; legs:15018 (Manticore Skin Gaiters of Fortune - 30-day limited period) ; feet:15133 (Manticore Skin Boots of Fortune - 10-day limited period) ; feet:15019 (Manticore Skin Boots of Fortune - 30-day limited period)
- friendship.xml #174 skill=3006 :: chest:15141 (Fortune Armor of Nightmare - 10-day limited period) ; head:15142 (Fortune Helm of Nightmare - 10-day limited period) ; head:15028 (Fortune Helm of Nightmare - 30-day limited period) ; gloves:15143 (Fortune Gauntlet of Nightmare - Heavy Armor - 10-day limited period) ; gloves:15029 (Fortune Gauntlet of Nightmare - Heavy Armor - 30-day limited period) ; feet:15144 (Fortune Boots of Nightmare - Heavy Armor - 10-day limited period) ; feet:15030 (Fortune Boots of Nightmare - Heavy Armor - 30-day limited period) ; shield:15145 (Fortune Shield of Nightmare - 10-day limited period) ; shield:15031 (Fortune Shield of Nightmare - 30-day limited period)
- friendship.xml #175 skill=3006 :: chest:15027 (Fortune Armor of Nightmare - 30-day limited period) ; head:15142 (Fortune Helm of Nightmare - 10-day limited period) ; head:15028 (Fortune Helm of Nightmare - 30-day limited period) ; gloves:15143 (Fortune Gauntlet of Nightmare - Heavy Armor - 10-day limited period) ; gloves:15029 (Fortune Gauntlet of Nightmare - Heavy Armor - 30-day limited period) ; feet:15144 (Fortune Boots of Nightmare - Heavy Armor - 10-day limited period) ; feet:15030 (Fortune Boots of Nightmare - Heavy Armor - 30-day limited period) ; shield:15145 (Fortune Shield of Nightmare - 10-day limited period) ; shield:15031 (Fortune Shield of Nightmare - 30-day limited period)
- friendship.xml #176 skill=3006 :: chest:15120 (Doom Plate Armor of Fortune - 10-day limited period) ; head:15001 (Doom Helmet of Fortune - 30-day limited period) ; head:15115 (Doom Helmet of Fortune - 10-day limited period) ; gloves:15002 (Doom Gloves of Fortune - Heavy Armor - 30-day limited period) ; gloves:15116 (Doom Gloves of Fortune - Heavy Armor - 10-day limited period) ; feet:15004 (Doom Boots of Fortune - Heavy Armor - 30-day limited period) ; feet:15118 (Doom Boots of Fortune - Heavy Armor - 10-day limited period) ; shield:15007 (Doom Shield of Fortune - 30-day limited period) ; shield:15121 (Doom Shield of Fortune - 10-day limited period)
- friendship.xml #177 skill=3006 :: chest:15006 (Doom Plate Armor of Fortune - 30-day limited period) ; head:15001 (Doom Helmet of Fortune - 30-day limited period) ; head:15115 (Doom Helmet of Fortune - 10-day limited period) ; gloves:15002 (Doom Gloves of Fortune - Heavy Armor - 30-day limited period) ; gloves:15116 (Doom Gloves of Fortune - Heavy Armor - 10-day limited period) ; feet:15004 (Doom Boots of Fortune - Heavy Armor - 30-day limited period) ; feet:15118 (Doom Boots of Fortune - Heavy Armor - 10-day limited period) ; shield:15007 (Doom Shield of Fortune - 30-day limited period) ; shield:15121 (Doom Shield of Fortune - 10-day limited period)
- friendship.xml #178 skill=3006 :: chest:15127 (Full Plate Armor of Fortune - 10-day limited period) ; head:15012 (Full Plate Helmet of Fortune - 30-day limited period) ; head:15126 (Full Plate Helmet of Fortune - 10-day limited period) ; shield:15016 (Full Plate Shield of Fortune - 30-day limited period) ; shield:15130 (Full Plate Shield of Fortune - 10-day limited period)
- friendship.xml #179 skill=3006 :: chest:15013 (Full Plate Armor of Fortune - 30-day limited period) ; head:15012 (Full Plate Helmet of Fortune - 30-day limited period) ; head:15126 (Full Plate Helmet of Fortune - 10-day limited period) ; shield:15016 (Full Plate Shield of Fortune - 30-day limited period) ; shield:15130 (Full Plate Shield of Fortune - 10-day limited period)
- friendship.xml #180 skill=3006 :: chest:15135 (Brigandine of Fortune - 10-day limited period) ; legs:15022 (Brigandine Gaiters of Fortune - 30-day limited period) ; legs:15136 (Brigandine Gaiters of Fortune - 10-day limited period) ; head:15023 (Brigandine Helmet of Fortune - 30-day limited period) ; head:15137 (Brigandine Helmet of Fortune - 10-day limited period) ; shield:15026 (Brigandine Shield of Fortune - 30-day limited period) ; shield:15140 (Brigandine Shield of Fortune - 10-day limited period)
- friendship.xml #181 skill=3006 :: chest:15021 (Brigandine of Fortune - 30-day limited period) ; legs:15022 (Brigandine Gaiters of Fortune - 30-day limited period) ; legs:15136 (Brigandine Gaiters of Fortune - 10-day limited period) ; head:15023 (Brigandine Helmet of Fortune - 30-day limited period) ; head:15137 (Brigandine Helmet of Fortune - 10-day limited period) ; shield:15026 (Brigandine Shield of Fortune - 30-day limited period) ; shield:15140 (Brigandine Shield of Fortune - 10-day limited period)
- friendship.xml #200 skill=3006 :: chest:16866 (Dark Crystal Robe of Fortune - 90-day limited period) ; head:16867 (Dark Crystal Helmet of Fortune - 90-day limited period) ; gloves:16868 (Dark Crystal Gloves of Fortune - Robe - 90-day limited period) ; feet:16869 (Dark Crystal Boots of Fortune - Robe - 90-day limited period)
- friendship.xml #201 skill=3006 :: chest:16871 (Avadon Robe of Fortune - 90-day limited period) ; head:16872 (Avadon Circlet of Fortune - 90-day limited period) ; gloves:16873 (Avadon Gloves of Fortune - Robe - 90-day limited period) ; feet:16874 (Avadon Boots of Fortune - Robe - 90-day limited period)
- friendship.xml #202 skill=3006 :: chest:16876 (Karmian Tunic of Fortune - 90-day limited period) ; legs:16877 (Karmian Stockings of Fortune - 90-day limited period) ; gloves:16878 (Karmian Gloves of Fortune - 90-day limited period)
- friendship.xml #203 skill=3006 :: chest:16880 (Mithril Tunic of Fortune - 90-day limited period) ; legs:16881 (Mithril Stockings of Fortune - 90-day limited period) ; gloves:16882 (Elven Mithril Gloves of Fortune - 90-day limited period)
- friendship.xml #204 skill=3006 :: chest:16884 (Dark Crystal Leather Armor of Fortune - 90-day limited period) ; legs:16885 (Dark Crystal Leggings of Fortune - 90-day limited period) ; head:16867 (Dark Crystal Helmet of Fortune - 90-day limited period) ; gloves:16886 (Dark Crystal Gloves of Fortune - Light Armor - 90-day limited period) ; feet:16887 (Dark Crystal Boots of Fortune - Light Armor - 90-day limited period)
- friendship.xml #205 skill=3006 :: chest:16888 (Leather Armor of Doom of Fortune - 90-day limited period) ; head:16889 (Doom Helmet of Fortune - 90-day limited period) ; gloves:16891 (Doom Gloves of Fortune - Light Armor - 90-day limited period) ; feet:16893 (Doom Boots of Fortune - Light Armor - 90-day limited period)
- friendship.xml #206 skill=3006 :: chest:16896 (Plate Leather Armor of Fortune - 90-day limited period) ; legs:16897 (Plate Leather Gaiters of Fortune - 90-day limited period) ; feet:16898 (Plate Leather Boots of Fortune - 90-day limited period)
- friendship.xml #207 skill=3006 :: chest:16905 (Manticore Skin Shirt of Fortune - 90-day limited period) ; legs:16906 (Manticore Skin Gaiters of Fortune - 90-day limited period) ; feet:16907 (Manticore Skin Boots of Fortune - 90-day limited period)
- friendship.xml #208 skill=3006 :: chest:16915 (Fortune Armor of Nightmare - 90-day limited period) ; head:16916 (Fortune Helm of Nightmare - 90-day limited period) ; gloves:16917 (Fortune Gauntlet of Nightmare - Heavy Armor - 90-day limited period) ; feet:16918 (Fortune Boots of Nightmare - Heavy Armor - 90-day limited period) ; shield:16919 (Fortune Shield of Nightmare - 90-day limited period)
- friendship.xml #209 skill=3006 :: chest:16894 (Doom Plate Armor of Fortune - 90-day limited period) ; head:16889 (Doom Helmet of Fortune - 90-day limited period) ; gloves:16890 (Doom Gloves of Fortune - Heavy Armor - 90-day limited period) ; feet:16892 (Doom Boots of Fortune - Heavy Armor - 90-day limited period) ; shield:16895 (Doom Shield of Fortune - 90-day limited period)
- friendship.xml #210 skill=3006 :: chest:16901 (Full Plate Armor of Fortune - 90-day limited period) ; head:16900 (Full Plate Helmet of Fortune - 90-day limited period) ; shield:16904 (Full Plate Shield of Fortune - 90-day limited period)
- friendship.xml #211 skill=3006 :: chest:16909 (Brigandine of Fortune - 90-day limited period) ; legs:16910 (Brigandine Gaiters of Fortune - 90-day limited period) ; head:16911 (Brigandine Helmet of Fortune - 90-day limited period) ; shield:16914 (Brigandine Shield of Fortune - 90-day limited period)
- friendship.xml #212 skill=3006 :: chest:21775 (Imperial Crusader Breastplate of Fortune - 90-day limited period) ; legs:21776 (Imperial Crusader Gaiters of Fortune - 90-day limited period) ; head:21779 (Imperial Crusader Helmet of Fortune - 90-day limited period) ; gloves:21777 (Imperial Crusader Gauntlet of Fortune - 90-day limited period) ; feet:21778 (Imperial Crusader Boots of Fortune - 90-day limited period) ; shield:21780 (Imperial Crusader Shield of Fortune - 90-day limited period)
- friendship.xml #213 skill=3006 :: chest:21782 (Draconic Leather Armor of Fortune - 90-day limited period) ; head:21785 (Draconic Leather Helmet of Fortune - 90-day limited period) ; gloves:21783 (Draconic Leather Gloves of Fortune - 90-day limited period) ; feet:21784 (Draconic Leather Boots of Fortune - 90-day limited period)
- friendship.xml #214 skill=3006 :: chest:21787 (Major Arcana Robe of Fortune - 90-day limited period) ; head:21790 (Major Arcana Circlet of Fortune - 90-day limited period) ; gloves:21788 (Major Arcana Gloves of Fortune - 90-day limited period) ; feet:21789 (Major Arcana Boots of Fortune - 90-day limited period)
- friendship.xml #215 skill=3006 :: chest:21793 (Dynasty Breastplate of Fortune - 90-day limited period) ; legs:21794 (Dynasty Gaiters of Fortune - 90-day limited period) ; head:21797 (Dynasty Helmet of Fortune - 90-day limited period) ; gloves:21795 (Dynasty Gauntlet of Fortune - 90-day limited period) ; feet:21796 (Dynasty Boots of Fortune - 90-day limited period) ; shield:21798 (Dynasty Shield of Fortune - 90-day limited period)
- friendship.xml #216 skill=3006 :: chest:21800 (Dynasty Leather Armor of Fortune - 90-day limited period) ; legs:21801 (Dynasty Leather Leggings of Fortune - 90-day limited period) ; head:21802 (Dynasty Leather Helmet of Fortune - 90-day limited period) ; gloves:21803 (Dynasty Leather Gloves of Fortune - 90-day limited period) ; feet:21804 (Dynasty Leather Boots of Fortune - 90-day limited period)
- friendship.xml #217 skill=3006 :: chest:21806 (Dynasty Tunic of Fortune - 90-day limited period) ; legs:21807 (Dynasty Stockings of Fortune - 90-day limited period) ; head:21808 (Dynasty Circlet of Fortune - 90-day limited period) ; gloves:21809 (Dynasty Gloves of Fortune - 90-day limited period) ; feet:21810 (Dynasty Shoes of Fortune - 90-day limited period)
- no_grade.xml #1 skill=3006 :: chest:23 (Wooden Breastplate) ; legs:2386 (Wooden Gaiters) ; head:43 (Wooden Helmet)
- no_grade.xml #2 skill=3006 :: chest:1101 (Tunic of Devotion) ; legs:1104 (Stockings of Devotion) ; head:44 (Leather Helmet)
- s_grade.xml #56 skill=3006 :: chest:6373 (Imperial Crusader Breastplate) ; legs:6374 (Imperial Crusader Gaiters) ; legs:11505 (Imperial Crusader Gaiters) ; head:6378 (Imperial Crusader Helmet) ; head:11509 (Imperial Crusader Helmet) ; gloves:6375 (Imperial Crusader Gauntlets) ; gloves:11506 (Imperial Crusader Gauntlet) ; feet:6376 (Imperial Crusader Boots) ; feet:11507 (Imperial Crusader Boots) ; shield:6377 (Imperial Crusader Shield) ; shield:11508 (Imperial Crusader Shield)
- s_grade.xml #57 skill=3006 :: chest:6379 (Draconic Leather Armor) ; head:6382 (Draconic Leather Helmet) ; head:11486 (Draconic Leather Helmet) ; gloves:6380 (Draconic Leather Gloves) ; gloves:11483 (Draconic Leather Gloves) ; feet:6381 (Draconic Leather Boots) ; feet:11484 (Draconic Leather Boots)
- s_grade.xml #58 skill=3006 :: chest:6383 (Major Arcana Robe) ; head:6386 (Major Arcana Circlet) ; head:11490 (Major Arcana Circlet) ; gloves:6384 (Major Arcana Gloves) ; gloves:11487 (Major Arcana Gloves) ; feet:6385 (Major Arcana Boots) ; feet:11489 (Major Arcana Boots)
- s_grade_pvp.xml #109 skill=3006 :: chest:10799 (Imperial Crusader Breastplate {PvP}) ; legs:6374 (Imperial Crusader Gaiters) ; legs:11505 (Imperial Crusader Gaiters) ; head:6378 (Imperial Crusader Helmet) ; head:11509 (Imperial Crusader Helmet) ; gloves:6375 (Imperial Crusader Gauntlets) ; gloves:11506 (Imperial Crusader Gauntlet) ; feet:6376 (Imperial Crusader Boots) ; feet:11507 (Imperial Crusader Boots) ; shield:6377 (Imperial Crusader Shield) ; shield:11508 (Imperial Crusader Shield)
- s_grade_pvp.xml #110 skill=3006 :: chest:10800 (Draconic Leather Armor {PvP}) ; head:6382 (Draconic Leather Helmet) ; head:11486 (Draconic Leather Helmet) ; gloves:6380 (Draconic Leather Gloves) ; gloves:11483 (Draconic Leather Gloves) ; feet:6381 (Draconic Leather Boots) ; feet:11484 (Draconic Leather Boots)
- s_grade_pvp.xml #111 skill=3006 :: chest:10801 (Major Arcana Robe {PvP}) ; head:6386 (Major Arcana Circlet) ; head:11490 (Major Arcana Circlet) ; gloves:6384 (Major Arcana Gloves) ; gloves:11487 (Major Arcana Gloves) ; feet:6385 (Major Arcana Boots) ; feet:11489 (Major Arcana Boots)
- s80_dynasty.xml #100 skill=3006 :: chest:10237 (Dynasty Silver Satin Tunic - Enchanter) ; legs:9437 (Dynasty Stockings) ; legs:11558 (Dynasty Stockings) ; head:9438 (Dynasty Circlet) ; head:11539 (Dynasty Circlet) ; gloves:9439 (Dynasty Gloves - Robe) ; gloves:11514 (Dynasty Gloves) ; feet:9440 (Dynasty Shoes - Robe) ; feet:11533 (Dynasty Shoes)
- s80_dynasty.xml #101 skill=3006 :: chest:10238 (Dynasty Silver Satin Tunic - Summoner) ; legs:9437 (Dynasty Stockings) ; legs:11558 (Dynasty Stockings) ; head:9438 (Dynasty Circlet) ; head:11539 (Dynasty Circlet) ; gloves:9439 (Dynasty Gloves - Robe) ; gloves:11514 (Dynasty Gloves) ; feet:9440 (Dynasty Shoes - Robe) ; feet:11533 (Dynasty Shoes)
- s80_dynasty.xml #102 skill=3006 :: chest:10239 (Dynasty Silver Satin Tunic - Wizard) ; legs:9437 (Dynasty Stockings) ; legs:11558 (Dynasty Stockings) ; head:9438 (Dynasty Circlet) ; head:11539 (Dynasty Circlet) ; gloves:9439 (Dynasty Gloves - Robe) ; gloves:11514 (Dynasty Gloves) ; feet:9440 (Dynasty Shoes - Robe) ; feet:11533 (Dynasty Shoes)
- s80_dynasty.xml #65 skill=3006 :: chest:9417 (Dynasty Breast Plate - Shield Master) ; legs:9421 (Dynasty Gaiters) ; legs:11512 (Dynasty Gaiters) ; head:9422 (Dynasty Helmet) ; head:11557 (Dynasty Helmet) ; gloves:9423 (Dynasty Gauntlet - Heavy) ; gloves:11513 (Dynasty Gauntlet) ; feet:9424 (Dynasty Boots - Heavy) ; feet:11526 (Dynasty Boots) ; shield:9441 (Dynasty Shield) ; shield:11532 (Dynasty Shield)
- s80_dynasty.xml #66 skill=3006 :: chest:9418 (Dynasty Breast Plate - Weapon Master) ; legs:9421 (Dynasty Gaiters) ; legs:11512 (Dynasty Gaiters) ; head:9422 (Dynasty Helmet) ; head:11557 (Dynasty Helmet) ; gloves:9423 (Dynasty Gauntlet - Heavy) ; gloves:11513 (Dynasty Gauntlet) ; feet:9424 (Dynasty Boots - Heavy) ; feet:11526 (Dynasty Boots)
- s80_dynasty.xml #67 skill=3006 :: chest:9419 (Dynasty Breast Plate - Force Master) ; legs:9421 (Dynasty Gaiters) ; legs:11512 (Dynasty Gaiters) ; head:9422 (Dynasty Helmet) ; head:11557 (Dynasty Helmet) ; gloves:9423 (Dynasty Gauntlet - Heavy) ; gloves:11513 (Dynasty Gauntlet) ; feet:9424 (Dynasty Boots - Heavy) ; feet:11526 (Dynasty Boots)
- s80_dynasty.xml #68 skill=3006 :: chest:9420 (Dynasty Breast Plate - Bard) ; legs:9421 (Dynasty Gaiters) ; legs:11512 (Dynasty Gaiters) ; head:9422 (Dynasty Helmet) ; head:11557 (Dynasty Helmet) ; gloves:9423 (Dynasty Gauntlet - Heavy) ; gloves:11513 (Dynasty Gauntlet) ; feet:9424 (Dynasty Boots - Heavy) ; feet:11526 (Dynasty Boots)
- s80_dynasty.xml #69 skill=3006 :: chest:9426 (Dynasty Leather Armor - Dagger Master) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty.xml #70 skill=3006 :: chest:9427 (Dynasty Leather Armor - Bow Master) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty.xml #71 skill=3006 :: chest:9433 (Dynasty Tunic - Healer) ; legs:9437 (Dynasty Stockings) ; legs:11558 (Dynasty Stockings) ; head:9438 (Dynasty Circlet) ; head:11539 (Dynasty Circlet) ; gloves:9439 (Dynasty Gloves - Robe) ; gloves:11514 (Dynasty Gloves) ; feet:9440 (Dynasty Shoes - Robe) ; feet:11533 (Dynasty Shoes)
- s80_dynasty.xml #72 skill=3006 :: chest:9434 (Dynasty Tunic - Enchanter) ; legs:9437 (Dynasty Stockings) ; legs:11558 (Dynasty Stockings) ; head:9438 (Dynasty Circlet) ; head:11539 (Dynasty Circlet) ; gloves:9439 (Dynasty Gloves - Robe) ; gloves:11514 (Dynasty Gloves) ; feet:9440 (Dynasty Shoes - Robe) ; feet:11533 (Dynasty Shoes)
- s80_dynasty.xml #73 skill=3006 :: chest:9435 (Dynasty Tunic - Summoner) ; legs:9437 (Dynasty Stockings) ; legs:11558 (Dynasty Stockings) ; head:9438 (Dynasty Circlet) ; head:11539 (Dynasty Circlet) ; gloves:9439 (Dynasty Gloves - Robe) ; gloves:11514 (Dynasty Gloves) ; feet:9440 (Dynasty Shoes - Robe) ; feet:11533 (Dynasty Shoes)
- s80_dynasty.xml #74 skill=3006 :: chest:9436 (Dynasty Tunic - Wizard) ; legs:9437 (Dynasty Stockings) ; legs:11558 (Dynasty Stockings) ; head:9438 (Dynasty Circlet) ; head:11539 (Dynasty Circlet) ; gloves:9439 (Dynasty Gloves - Robe) ; gloves:11514 (Dynasty Gloves) ; feet:9440 (Dynasty Shoes - Robe) ; feet:11533 (Dynasty Shoes)
- s80_dynasty.xml #82 skill=3006 :: chest:9416 (Dynasty Breast Plate) ; legs:9421 (Dynasty Gaiters) ; legs:11512 (Dynasty Gaiters) ; head:9422 (Dynasty Helmet) ; head:11557 (Dynasty Helmet) ; gloves:9423 (Dynasty Gauntlet - Heavy) ; gloves:11513 (Dynasty Gauntlet) ; feet:9424 (Dynasty Boots - Heavy) ; feet:11526 (Dynasty Boots) ; shield:9441 (Dynasty Shield) ; shield:11532 (Dynasty Shield)
- s80_dynasty.xml #83 skill=3006 :: chest:9425 (Dynasty Leather Armor) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty.xml #84 skill=3006 :: chest:9432 (Dynasty Tunic) ; legs:9437 (Dynasty Stockings) ; legs:11558 (Dynasty Stockings) ; head:9438 (Dynasty Circlet) ; head:11539 (Dynasty Circlet) ; gloves:9439 (Dynasty Gloves - Robe) ; gloves:11514 (Dynasty Gloves) ; feet:9440 (Dynasty Shoes - Robe) ; feet:11533 (Dynasty Shoes)
- s80_dynasty.xml #85 skill=3006 :: chest:10126 (Dynasty Leather Armor - Force Master) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty.xml #86 skill=3006 :: chest:10127 (Dynasty Leather Armor - Weapon Master) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty.xml #87 skill=3006 :: chest:10168 (Dynasty Leather Armor - Enchanter) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty.xml #88 skill=3006 :: chest:10214 (Dynasty Leather Armor - Summoner) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty.xml #89 skill=3006 :: chest:10228 (Dynasty Platinum Plate - Shield Master) ; legs:9421 (Dynasty Gaiters) ; legs:11512 (Dynasty Gaiters) ; head:9422 (Dynasty Helmet) ; head:11557 (Dynasty Helmet) ; gloves:9423 (Dynasty Gauntlet - Heavy) ; gloves:11513 (Dynasty Gauntlet) ; feet:9424 (Dynasty Boots - Heavy) ; feet:11526 (Dynasty Boots) ; shield:9441 (Dynasty Shield) ; shield:11532 (Dynasty Shield)
- s80_dynasty.xml #90 skill=3006 :: chest:10229 (Dynasty Platinum Plate - Weapon Master) ; legs:9421 (Dynasty Gaiters) ; legs:11512 (Dynasty Gaiters) ; head:9422 (Dynasty Helmet) ; head:11557 (Dynasty Helmet) ; gloves:9423 (Dynasty Gauntlet - Heavy) ; gloves:11513 (Dynasty Gauntlet) ; feet:9424 (Dynasty Boots - Heavy) ; feet:11526 (Dynasty Boots)
- s80_dynasty.xml #91 skill=3006 :: chest:10230 (Dynasty Platinum Plate - Force Master) ; legs:9421 (Dynasty Gaiters) ; legs:11512 (Dynasty Gaiters) ; head:9422 (Dynasty Helmet) ; head:11557 (Dynasty Helmet) ; gloves:9423 (Dynasty Gauntlet - Heavy) ; gloves:11513 (Dynasty Gauntlet) ; feet:9424 (Dynasty Boots - Heavy) ; feet:11526 (Dynasty Boots)
- s80_dynasty.xml #92 skill=3006 :: chest:10231 (Dynasty Platinum Plate - Bard) ; legs:9421 (Dynasty Gaiters) ; legs:11512 (Dynasty Gaiters) ; head:9422 (Dynasty Helmet) ; head:11557 (Dynasty Helmet) ; gloves:9423 (Dynasty Gauntlet - Heavy) ; gloves:11513 (Dynasty Gauntlet) ; feet:9424 (Dynasty Boots - Heavy) ; feet:11526 (Dynasty Boots)
- s80_dynasty.xml #93 skill=3006 :: chest:10233 (Dynasty Jewel Leather Armor - Dagger Master) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty.xml #94 skill=3006 :: chest:10234 (Dynasty Jewel Leather Armor - Bow Master) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty.xml #95 skill=3006 :: chest:10487 (Dynasty Jeweled Leather Armor - Force Master) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty.xml #96 skill=3006 :: chest:10488 (Dynasty Jeweled Leather Armor - Weapon Master) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty.xml #97 skill=3006 :: chest:10489 (Dynasty Jeweled Leather Armor - Enchanter) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty.xml #98 skill=3006 :: chest:10490 (Dynasty Jeweled Leather Armor - Summoner) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty.xml #99 skill=3006 :: chest:10236 (Dynasty Silver Satin Tunic - Healer) ; legs:9437 (Dynasty Stockings) ; legs:11558 (Dynasty Stockings) ; head:9438 (Dynasty Circlet) ; head:11539 (Dynasty Circlet) ; gloves:9439 (Dynasty Gloves - Robe) ; gloves:11514 (Dynasty Gloves) ; feet:9440 (Dynasty Shoes - Robe) ; feet:11533 (Dynasty Shoes)
- s80_dynasty_pvp.xml #112 skill=3006 :: chest:10802 (Dynasty Breastplate {PvP}) ; legs:9421 (Dynasty Gaiters) ; legs:11512 (Dynasty Gaiters) ; head:9422 (Dynasty Helmet) ; head:11557 (Dynasty Helmet) ; gloves:9423 (Dynasty Gauntlet - Heavy) ; gloves:11513 (Dynasty Gauntlet) ; feet:9424 (Dynasty Boots - Heavy) ; feet:11526 (Dynasty Boots) ; shield:9441 (Dynasty Shield) ; shield:11532 (Dynasty Shield)
- s80_dynasty_pvp.xml #113 skill=3006 :: chest:10803 (Dynasty Breastplate {PvP} - Shield Master) ; legs:9421 (Dynasty Gaiters) ; legs:11512 (Dynasty Gaiters) ; head:9422 (Dynasty Helmet) ; head:11557 (Dynasty Helmet) ; gloves:9423 (Dynasty Gauntlet - Heavy) ; gloves:11513 (Dynasty Gauntlet) ; feet:9424 (Dynasty Boots - Heavy) ; feet:11526 (Dynasty Boots) ; shield:9441 (Dynasty Shield) ; shield:11532 (Dynasty Shield)
- s80_dynasty_pvp.xml #114 skill=3006 :: chest:10804 (Dynasty Breastplate {PvP} - Weapon Master) ; legs:9421 (Dynasty Gaiters) ; legs:11512 (Dynasty Gaiters) ; head:9422 (Dynasty Helmet) ; head:11557 (Dynasty Helmet) ; gloves:9423 (Dynasty Gauntlet - Heavy) ; gloves:11513 (Dynasty Gauntlet) ; feet:9424 (Dynasty Boots - Heavy) ; feet:11526 (Dynasty Boots)
- s80_dynasty_pvp.xml #115 skill=3006 :: chest:10805 (Dynasty Breastplate {PvP} - Force Master) ; legs:9421 (Dynasty Gaiters) ; legs:11512 (Dynasty Gaiters) ; head:9422 (Dynasty Helmet) ; head:11557 (Dynasty Helmet) ; gloves:9423 (Dynasty Gauntlet - Heavy) ; gloves:11513 (Dynasty Gauntlet) ; feet:9424 (Dynasty Boots - Heavy) ; feet:11526 (Dynasty Boots)
- s80_dynasty_pvp.xml #116 skill=3006 :: chest:10806 (Dynasty Breastplate {PvP} - Bard) ; legs:9421 (Dynasty Gaiters) ; legs:11512 (Dynasty Gaiters) ; head:9422 (Dynasty Helmet) ; head:11557 (Dynasty Helmet) ; gloves:9423 (Dynasty Gauntlet - Heavy) ; gloves:11513 (Dynasty Gauntlet) ; feet:9424 (Dynasty Boots - Heavy) ; feet:11526 (Dynasty Boots)
- s80_dynasty_pvp.xml #117 skill=3006 :: chest:10807 (Dynasty Leather Armor {PvP}) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty_pvp.xml #118 skill=3006 :: chest:10808 (Dynasty Leather Armor {PvP} - Dagger Master) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty_pvp.xml #119 skill=3006 :: chest:10809 (Dynasty Leather Armor {PvP} - Bow Master) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty_pvp.xml #120 skill=3006 :: chest:10810 (Dynasty Tunic {PvP}) ; legs:9437 (Dynasty Stockings) ; legs:11558 (Dynasty Stockings) ; head:9438 (Dynasty Circlet) ; head:11539 (Dynasty Circlet) ; gloves:9439 (Dynasty Gloves - Robe) ; gloves:11514 (Dynasty Gloves) ; feet:9440 (Dynasty Shoes - Robe) ; feet:11533 (Dynasty Shoes)
- s80_dynasty_pvp.xml #121 skill=3006 :: chest:10811 (Dynasty Tunic {PvP} - Healer) ; legs:9437 (Dynasty Stockings) ; legs:11558 (Dynasty Stockings) ; head:9438 (Dynasty Circlet) ; head:11539 (Dynasty Circlet) ; gloves:9439 (Dynasty Gloves - Robe) ; gloves:11514 (Dynasty Gloves) ; feet:9440 (Dynasty Shoes - Robe) ; feet:11533 (Dynasty Shoes)
- s80_dynasty_pvp.xml #122 skill=3006 :: chest:10812 (Dynasty Tunic {PvP} - Enchanter) ; legs:9437 (Dynasty Stockings) ; legs:11558 (Dynasty Stockings) ; head:9438 (Dynasty Circlet) ; head:11539 (Dynasty Circlet) ; gloves:9439 (Dynasty Gloves - Robe) ; gloves:11514 (Dynasty Gloves) ; feet:9440 (Dynasty Shoes - Robe) ; feet:11533 (Dynasty Shoes)
- s80_dynasty_pvp.xml #123 skill=3006 :: chest:10813 (Dynasty Tunic {PvP} - Summoner) ; legs:9437 (Dynasty Stockings) ; legs:11558 (Dynasty Stockings) ; head:9438 (Dynasty Circlet) ; head:11539 (Dynasty Circlet) ; gloves:9439 (Dynasty Gloves - Robe) ; gloves:11514 (Dynasty Gloves) ; feet:9440 (Dynasty Shoes - Robe) ; feet:11533 (Dynasty Shoes)
- s80_dynasty_pvp.xml #124 skill=3006 :: chest:10814 (Dynasty Tunic {PvP} - Wizard) ; legs:9437 (Dynasty Stockings) ; legs:11558 (Dynasty Stockings) ; head:9438 (Dynasty Circlet) ; head:11539 (Dynasty Circlet) ; gloves:9439 (Dynasty Gloves - Robe) ; gloves:11514 (Dynasty Gloves) ; feet:9440 (Dynasty Shoes - Robe) ; feet:11533 (Dynasty Shoes)
- s80_dynasty_pvp.xml #125 skill=3006 :: chest:10815 (Dynasty Leather Armor {PvP} - Force Master) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty_pvp.xml #126 skill=3006 :: chest:10816 (Dynasty Leather Armor {PvP} - Weapon Master) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty_pvp.xml #127 skill=3006 :: chest:10817 (Dynasty Leather Armor {PvP} - Enchanter) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty_pvp.xml #128 skill=3006 :: chest:10818 (Dynasty Leather Armor {PvP} - Summoner) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty_pvp.xml #129 skill=3006 :: chest:10820 (Dynasty Platinum Breastplate {PvP} - Shield Master) ; legs:9421 (Dynasty Gaiters) ; legs:11512 (Dynasty Gaiters) ; head:9422 (Dynasty Helmet) ; head:11557 (Dynasty Helmet) ; gloves:9423 (Dynasty Gauntlet - Heavy) ; gloves:11513 (Dynasty Gauntlet) ; feet:9424 (Dynasty Boots - Heavy) ; feet:11526 (Dynasty Boots) ; shield:9441 (Dynasty Shield) ; shield:11532 (Dynasty Shield)
- s80_dynasty_pvp.xml #130 skill=3006 :: chest:10821 (Dynasty Platinum Breastplate {PvP} - Weapon Master) ; legs:9421 (Dynasty Gaiters) ; legs:11512 (Dynasty Gaiters) ; head:9422 (Dynasty Helmet) ; head:11557 (Dynasty Helmet) ; gloves:9423 (Dynasty Gauntlet - Heavy) ; gloves:11513 (Dynasty Gauntlet) ; feet:9424 (Dynasty Boots - Heavy) ; feet:11526 (Dynasty Boots)
- s80_dynasty_pvp.xml #131 skill=3006 :: chest:10822 (Dynasty Platinum Breastplate {PvP} - Force Master) ; legs:9421 (Dynasty Gaiters) ; legs:11512 (Dynasty Gaiters) ; head:9422 (Dynasty Helmet) ; head:11557 (Dynasty Helmet) ; gloves:9423 (Dynasty Gauntlet - Heavy) ; gloves:11513 (Dynasty Gauntlet) ; feet:9424 (Dynasty Boots - Heavy) ; feet:11526 (Dynasty Boots)
- s80_dynasty_pvp.xml #132 skill=3006 :: chest:10823 (Dynasty Platinum Breastplate {PvP} - Bard) ; legs:9421 (Dynasty Gaiters) ; legs:11512 (Dynasty Gaiters) ; head:9422 (Dynasty Helmet) ; head:11557 (Dynasty Helmet) ; gloves:9423 (Dynasty Gauntlet - Heavy) ; gloves:11513 (Dynasty Gauntlet) ; feet:9424 (Dynasty Boots - Heavy) ; feet:11526 (Dynasty Boots)
- s80_dynasty_pvp.xml #133 skill=3006 :: chest:10825 (Dynasty Jewel Leather Armor {PvP} - Dagger Master) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty_pvp.xml #134 skill=3006 :: chest:10826 (Dynasty Jewel Leather Armor {PvP} - Bow Master) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty_pvp.xml #135 skill=3006 :: chest:10832 (Dynasty Jewel Leather Armor {PvP} - Force Master) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty_pvp.xml #136 skill=3006 :: chest:10833 (Dynasty Jewel Leather Armor {PvP} - Weapon Master) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty_pvp.xml #137 skill=3006 :: chest:10834 (Dynasty Jewel Leather Armor {PvP} - Enchanter) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty_pvp.xml #138 skill=3006 :: chest:10835 (Dynasty Jewel Leather Armor {PvP} - Summoner) ; legs:9428 (Dynasty Leather Leggings) ; legs:11516 (Dynasty Leather Leggings) ; head:9429 (Dynasty Leather Helmet) ; head:11525 (Dynasty Leather Helmet) ; gloves:9430 (Dynasty Leather Gloves - Light) ; gloves:11515 (Dynasty Leather Gloves) ; feet:9431 (Dynasty Leather Boots - Light) ; feet:11524 (Dynasty Leather Boots)
- s80_dynasty_pvp.xml #139 skill=3006 :: chest:10828 (Dynasty Silver Satin Tunic {PvP} - Healer) ; legs:9437 (Dynasty Stockings) ; legs:11558 (Dynasty Stockings) ; head:9438 (Dynasty Circlet) ; head:11539 (Dynasty Circlet) ; gloves:9439 (Dynasty Gloves - Robe) ; gloves:11514 (Dynasty Gloves) ; feet:9440 (Dynasty Shoes - Robe) ; feet:11533 (Dynasty Shoes)
- s80_dynasty_pvp.xml #140 skill=3006 :: chest:10829 (Dynasty Silver Satin Tunic {PvP} - Enchanter) ; legs:9437 (Dynasty Stockings) ; legs:11558 (Dynasty Stockings) ; head:9438 (Dynasty Circlet) ; head:11539 (Dynasty Circlet) ; gloves:9439 (Dynasty Gloves - Robe) ; gloves:11514 (Dynasty Gloves) ; feet:9440 (Dynasty Shoes - Robe) ; feet:11533 (Dynasty Shoes)
- s80_dynasty_pvp.xml #141 skill=3006 :: chest:10830 (Dynasty Silver Satin Tunic {PvP} - Summoner) ; legs:9437 (Dynasty Stockings) ; legs:11558 (Dynasty Stockings) ; head:9438 (Dynasty Circlet) ; head:11539 (Dynasty Circlet) ; gloves:9439 (Dynasty Gloves - Robe) ; gloves:11514 (Dynasty Gloves) ; feet:9440 (Dynasty Shoes - Robe) ; feet:11533 (Dynasty Shoes)
- s80_dynasty_pvp.xml #142 skill=3006 :: chest:10831 (Dynasty Silver Satin Tunic {PvP} - Wizard) ; legs:9437 (Dynasty Stockings) ; legs:11558 (Dynasty Stockings) ; head:9438 (Dynasty Circlet) ; head:11539 (Dynasty Circlet) ; gloves:9439 (Dynasty Gloves - Robe) ; gloves:11514 (Dynasty Gloves) ; feet:9440 (Dynasty Shoes - Robe) ; feet:11533 (Dynasty Shoes)
- s80_moirai.xml #182 skill=3006 :: chest:15609 (Moirai Breastplate) ; legs:15612 (Moirai Gaiter) ; legs:16295 (Moirai Gaiter) ; head:15606 (Moirai Helmet) ; head:16289 (Moirai Helmet) ; gloves:15615 (Moirai Gauntlet) ; gloves:16298 (Moirai Gauntlet) ; feet:15618 (Moirai Boots) ; feet:16301 (Moirai Boots) ; shield:15621 (Moirai Shield) ; shield:16304 (Moirai Shield)
- s80_moirai.xml #183 skill=3006 :: chest:15610 (Moirai Leather Breastplate) ; legs:15613 (Moirai Leather Legging) ; legs:16296 (Moirai Leather Legging) ; head:15607 (Moirai Leather Helmet) ; head:16290 (Moirai Leather Helmet) ; gloves:15616 (Moirai Leather Gloves) ; gloves:16299 (Moirai Leather Gloves) ; feet:15619 (Moirai Leather Boots) ; feet:16302 (Moirai Leather Boots)
- s80_moirai.xml #184 skill=3006 :: chest:15611 (Moirai Tunic) ; legs:15614 (Moirai Stockings) ; legs:16297 (Moirai Stockings) ; head:15608 (Moirai Circlet) ; head:16291 (Moirai Circlet) ; gloves:15617 (Moirai Gloves) ; gloves:16300 (Moirai Gloves) ; feet:15620 (Moirai Shoes) ; feet:16303 (Moirai Shoes)
- s80_moirai_pvp.xml #197 skill=3006 :: chest:16174 (Moirai Breastplate {PvP}) ; legs:15612 (Moirai Gaiter) ; legs:16295 (Moirai Gaiter) ; head:15606 (Moirai Helmet) ; head:16289 (Moirai Helmet) ; gloves:15615 (Moirai Gauntlet) ; gloves:16298 (Moirai Gauntlet) ; feet:15618 (Moirai Boots) ; feet:16301 (Moirai Boots) ; shield:15621 (Moirai Shield) ; shield:16304 (Moirai Shield)
- s80_moirai_pvp.xml #198 skill=3006 :: chest:16175 (Moirai Leather Breastplate {PvP}) ; legs:15613 (Moirai Leather Legging) ; legs:16296 (Moirai Leather Legging) ; head:15607 (Moirai Leather Helmet) ; head:16290 (Moirai Leather Helmet) ; gloves:15616 (Moirai Leather Gloves) ; gloves:16299 (Moirai Leather Gloves) ; feet:15619 (Moirai Leather Boots) ; feet:16302 (Moirai Leather Boots)
- s80_moirai_pvp.xml #199 skill=3006 :: chest:16176 (Moirai Tunic {PvP}) ; legs:15614 (Moirai Stockings) ; legs:16297 (Moirai Stockings) ; head:15608 (Moirai Circlet) ; head:16291 (Moirai Circlet) ; gloves:15617 (Moirai Gloves) ; gloves:16300 (Moirai Gloves) ; feet:15620 (Moirai Shoes) ; feet:16303 (Moirai Shoes)
- s84_elegia.xml #188 skill=3006 :: chest:15575 (Elegia Breastplate) ; legs:15578 (Elegia Gaiter) ; head:15572 (Elegia Helmet) ; gloves:15581 (Elegia Gauntlet) ; feet:15584 (Elegia Boots) ; shield:15587 (Elegia Shield)
- s84_elegia.xml #189 skill=3006 :: chest:15576 (Elegia Leather Breastplate) ; legs:15579 (Elegia Leather Legging) ; head:15573 (Elegia Leather Helmet) ; gloves:15582 (Elegia Leather Gloves) ; feet:15585 (Elegia Leather Boots)
- s84_elegia.xml #190 skill=3006 :: chest:15577 (Elegia Tunic) ; legs:15580 (Elegia Stockings) ; head:15574 (Elegia Circlet) ; gloves:15583 (Elegia Gloves) ; feet:15586 (Elegia Shoes)
- s84_elegia_pvp.xml #191 skill=3006 :: chest:16168 (Elegia Breastplate {PvP}) ; legs:15578 (Elegia Gaiter) ; head:15572 (Elegia Helmet) ; gloves:15581 (Elegia Gauntlet) ; feet:15584 (Elegia Boots) ; shield:15587 (Elegia Shield)
- s84_elegia_pvp.xml #192 skill=3006 :: chest:16169 (Elegia Leather Breastplate {PvP}) ; legs:15579 (Elegia Leather Legging) ; head:15573 (Elegia Leather Helmet) ; gloves:15582 (Elegia Leather Gloves) ; feet:15585 (Elegia Leather Boots)
- s84_elegia_pvp.xml #193 skill=3006 :: chest:16170 (Elegia Tunic {PvP}) ; legs:15580 (Elegia Stockings) ; head:15574 (Elegia Circlet) ; gloves:15583 (Elegia Gloves) ; feet:15586 (Elegia Shoes)
- s84_vesper.xml #143 skill=3006 :: chest:13432 (Vesper Breastplate) ; legs:13438 (Vesper Gaiters) ; legs:16312 (Vesper Gaiter) ; head:13137 (Vesper Helmet) ; head:16306 (Vesper Helmet) ; gloves:13439 (Vesper Gauntlet) ; gloves:16315 (Vesper Gauntlet) ; feet:13440 (Vesper Boots) ; feet:16318 (Vesper Boots) ; shield:13471 (Vesper Shield) ; shield:16321 (Vesper Shield)
- s84_vesper.xml #144 skill=3006 :: chest:13433 (Vesper Leather Breastplate) ; legs:13441 (Vesper Leather Leggings) ; legs:16313 (Vesper Leather Legging) ; head:13138 (Vesper Leather Helmet) ; head:16307 (Vesper Leather Helmet) ; gloves:13442 (Vesper Leather Gloves) ; gloves:16316 (Vesper Leather Gloves) ; feet:13443 (Vesper Leather Boots) ; feet:16319 (Vesper Leather Boots)
- s84_vesper.xml #145 skill=3006 :: chest:13434 (Vesper Tunic) ; legs:13444 (Vesper Stockings) ; legs:16314 (Vesper Stockings) ; head:13139 (Vesper Circlet) ; head:16308 (Vesper Circlet) ; gloves:13445 (Vesper Gloves) ; gloves:16317 (Vesper Gloves) ; feet:13446 (Vesper Shoes) ; feet:16320 (Vesper Shoes)
- s84_vesper.xml #146 skill=3006 :: chest:13435 (Vesper Noble Breastplate) ; legs:13448 (Vesper Noble Gaiters) ; legs:16843 (Vesper Noble Gaiter) ; head:13140 (Vesper Noble Helmet) ; head:16837 (Vesper Noble Helmet) ; gloves:13449 (Vesper Noble Gauntlet) ; gloves:16846 (Vesper Noble Gauntlet) ; feet:13450 (Vesper Noble Boots) ; feet:16849 (Vesper Noble Boots) ; shield:13471 (Vesper Shield) ; shield:16321 (Vesper Shield)
- s84_vesper.xml #147 skill=3006 :: chest:13436 (Vesper Noble Leather Breastplate) ; legs:13451 (Vesper Noble Leather Leggings) ; legs:16844 (Vesper Noble Leather Legging) ; head:13141 (Vesper Noble Leather Helmet) ; head:16838 (Vesper Noble Leather Helmet) ; gloves:13452 (Vesper Noble Leather Gloves) ; gloves:16847 (Vesper Noble Leather Gloves) ; feet:13453 (Vesper Noble Leather Boots) ; feet:16850 (Vesper Noble Leather Boots)
- s84_vesper.xml #148 skill=3006 :: chest:13437 (Vesper Noble Tunic) ; legs:13454 (Vesper Noble Stockings) ; legs:16845 (Vesper Noble Stockings) ; head:13142 (Vesper Noble Circlet) ; head:16839 (Vesper Noble Circlet) ; gloves:13455 (Vesper Noble Gloves) ; gloves:16848 (Vesper Noble Gloves) ; feet:13456 (Vesper Noble Shoes) ; feet:16851 (Vesper Noble Shoes)
- s84_vesper_pvp.xml #149 skill=3006 :: chest:14520 (Vesper Breastplate {PvP}) ; legs:13438 (Vesper Gaiters) ; legs:16312 (Vesper Gaiter) ; head:13137 (Vesper Helmet) ; head:16306 (Vesper Helmet) ; gloves:13439 (Vesper Gauntlet) ; gloves:16315 (Vesper Gauntlet) ; feet:13440 (Vesper Boots) ; feet:16318 (Vesper Boots) ; shield:13471 (Vesper Shield) ; shield:16321 (Vesper Shield)
- s84_vesper_pvp.xml #150 skill=3006 :: chest:14521 (Vesper Leather Breastplate {PvP}) ; legs:13441 (Vesper Leather Leggings) ; legs:16313 (Vesper Leather Legging) ; head:13138 (Vesper Leather Helmet) ; head:16307 (Vesper Leather Helmet) ; gloves:13442 (Vesper Leather Gloves) ; gloves:16316 (Vesper Leather Gloves) ; feet:13443 (Vesper Leather Boots) ; feet:16319 (Vesper Leather Boots)
- s84_vesper_pvp.xml #151 skill=3006 :: chest:14522 (Vesper Tunic {PvP}) ; legs:13444 (Vesper Stockings) ; legs:16314 (Vesper Stockings) ; head:13139 (Vesper Circlet) ; head:16308 (Vesper Circlet) ; gloves:13445 (Vesper Gloves) ; gloves:16317 (Vesper Gloves) ; feet:13446 (Vesper Shoes) ; feet:16320 (Vesper Shoes)
- s84_vesper_pvp.xml #152 skill=3006 :: chest:14523 (Vesper Noble Breastplate {PvP}) ; legs:13448 (Vesper Noble Gaiters) ; legs:16843 (Vesper Noble Gaiter) ; head:13140 (Vesper Noble Helmet) ; head:16837 (Vesper Noble Helmet) ; gloves:13449 (Vesper Noble Gauntlet) ; gloves:16846 (Vesper Noble Gauntlet) ; feet:13450 (Vesper Noble Boots) ; feet:16849 (Vesper Noble Boots) ; shield:13471 (Vesper Shield) ; shield:16321 (Vesper Shield)
- s84_vesper_pvp.xml #153 skill=3006 :: chest:14524 (Vesper Noble Leather Breastplate {PvP}) ; legs:13451 (Vesper Noble Leather Leggings) ; legs:16844 (Vesper Noble Leather Legging) ; head:13141 (Vesper Noble Leather Helmet) ; head:16838 (Vesper Noble Leather Helmet) ; gloves:13452 (Vesper Noble Leather Gloves) ; gloves:16847 (Vesper Noble Leather Gloves) ; feet:13453 (Vesper Noble Leather Boots) ; feet:16850 (Vesper Noble Leather Boots)
- s84_vesper_pvp.xml #154 skill=3006 :: chest:14525 (Vesper Noble Tunic {PvP}) ; legs:13454 (Vesper Noble Stockings) ; legs:16845 (Vesper Noble Stockings) ; head:13142 (Vesper Noble Circlet) ; head:16839 (Vesper Noble Circlet) ; gloves:13455 (Vesper Noble Gloves) ; gloves:16848 (Vesper Noble Gloves) ; feet:13456 (Vesper Noble Shoes) ; feet:16851 (Vesper Noble Shoes)
- s84_vorpal.xml #185 skill=3006 :: chest:15592 (Vorpal Breastplate) ; legs:15595 (Vorpal Gaiter) ; head:15589 (Vorpal Helmet) ; gloves:15598 (Vorpal Gauntlet) ; feet:15601 (Vorpal Boots) ; shield:15604 (Vorpal Shield)
- s84_vorpal.xml #186 skill=3006 :: chest:15593 (Vorpal Leather Breastplate) ; legs:15596 (Vorpal Leather Legging) ; head:15590 (Vorpal Leather Helmet) ; gloves:15599 (Vorpal Leather Gloves) ; feet:15602 (Vorpal Leather Boots)
- s84_vorpal.xml #187 skill=3006 :: chest:15594 (Vorpal Tunic) ; legs:15597 (Vorpal Stockings) ; head:15591 (Vorpal Circlet) ; gloves:15600 (Vorpal Gloves) ; feet:15603 (Vorpal Shoes)
- s84_vorpal_pvp.xml #194 skill=3006 :: chest:16171 (Vorpal Breastplate {PvP}) ; legs:15595 (Vorpal Gaiter) ; head:15589 (Vorpal Helmet) ; gloves:15598 (Vorpal Gauntlet) ; feet:15601 (Vorpal Boots) ; shield:15604 (Vorpal Shield)
- s84_vorpal_pvp.xml #195 skill=3006 :: chest:16172 (Vorpal Leather Breastplate {PvP}) ; legs:15596 (Vorpal Leather Legging) ; head:15590 (Vorpal Leather Helmet) ; gloves:15599 (Vorpal Leather Gloves) ; feet:15602 (Vorpal Leather Boots)
- s84_vorpal_pvp.xml #196 skill=3006 :: chest:16173 (Vorpal Tunic {PvP}) ; legs:15597 (Vorpal Stockings) ; head:15591 (Vorpal Circlet) ; gloves:15600 (Vorpal Gloves) ; feet:15603 (Vorpal Shoes)
- special.xml #81 skill=3006 :: chest:9670 (Native Tunic) ; legs:9671 (Native Pants) ; head:9669 (Native Helmet)

### 12.1 Disabled / reserved sets (commented out in the source XML)

- a_grade.xml #45 [DISABLED/RESERVED] skill=3006 :: chest:44 (<chest id= 44  />) ; head:547 (<head id= 547  />) ; gloves:2478 (<gloves id= 2478  />) ; feet:598 (<feet id= 598  />)
- a_grade.xml #52 [DISABLED/RESERVED] skill=3006 :: chest:44 (<chest id= 44  />) ; head:2418 (<head id= 2418  />) ; gloves:2479 (<gloves id= 2479  />) ; feet:2440 (<feet id= 2440  />)
- a_grade.xml #53 [DISABLED/RESERVED] skill=3006 :: chest:44 (<chest id= 44  />) ; head:2420 (<head id= 2420  />) ; gloves:2477 (<gloves id= 2477  />) ; feet:2443 (<feet id= 2443  />)
- a_grade.xml #54 [DISABLED/RESERVED] skill=3006 :: chest:44 (<chest id= 44  />) ; head:2421 (<head id= 2421  />) ; gloves:2473 (<gloves id= 2473  />) ; feet:2444 (<feet id= 2444  />)
- a_grade.xml #55 [DISABLED/RESERVED] skill=3006 :: chest:44 (<chest id= 44  />) ; legs:389 (<legs id= 389  />) ; head:504 (<head id= 504  />) ; gloves:2476 (<gloves id= 2476  />) ; feet:2445 (<feet id= 2445  />) ; shield:634 (<shield id= 634  />)
- b_grade.xml #31 [DISABLED/RESERVED] skill=3006 :: chest:44 (<chest id= 44  />) ; head:2415 (<head id= 2415  />) ; gloves:2464 (<gloves id= 2464  />) ; feet:600 (<feet id= 600  />)
- b_grade.xml #38 [DISABLED/RESERVED] skill=3006 :: chest:44 (<chest id= 44  />) ; head:2417 (<head id= 2417  />) ; gloves:2475 (<gloves id= 2475  />) ; feet:601 (<feet id= 601  />)
- c_grade.xml #16 [DISABLED/RESERVED] skill=3006 :: chest:44 (<chest id= 44  />) ; legs:382 (<legs id= 382  />) ; head:500 (<head id= 500  />) ; shield:2496 (<shield id= 2496  />)
- c_grade.xml #17 [DISABLED/RESERVED] skill=3006 :: chest:44 (<chest id= 44  />) ; gloves:2457 (<gloves id= 2457  />)
- c_grade.xml #18 [DISABLED/RESERVED] skill=3006 :: chest:44 (<chest id= 44  />) ; legs:419 (<legs id= 419  />) ; feet:2434 (<feet id= 2434  />)
- d_grade.xml #10 [DISABLED/RESERVED] skill=3006 :: chest:44 (<chest id= 44  />) ; legs:380 (<legs id= 380  />) ; head:2412 (<head id= 2412  />) ; shield:2494 (<shield id= 2494  />)
- d_grade.xml #11 [DISABLED/RESERVED] skill=3006 :: chest:44 (<chest id= 44  />) ; gloves:2451 (<gloves id= 2451  />)
- d_grade.xml #9 [DISABLED/RESERVED] skill=3006 :: chest:44 (<chest id= 44  />) ; feet:2427 (<feet id= 2427  />)

## 13. Weapon Reference

### 13.1 Weapon types present, slot and hand class (base entries: 917)

| Weapon type | Slots found | Class | Entries |
|-------------|-------------|-------|---------|
| ANCIENTSWORD | lrhand | 2H (lrhand) | 26 |
| BLUNT | lrhand, rhand | mixed (ver Slots found) | 175 |
| BOW | lrhand, rhand | mixed (ver Slots found) | 47 |
| CROSSBOW | lrhand | 2H (lrhand) | 29 |
| DAGGER | rhand | 1H (rhand) | 71 |
| DUAL | lrhand | dual (lrhand) | 138 |
| DUALDAGGER | lrhand | dual dagger (lrhand) | 8 |
| DUALFIST | lrhand | dual fist (lrhand) | 41 |
| ETC | rhand | 1H (rhand) | 44 |
| FISHINGROD | lrhand | 2H (lrhand) | 6 |
| FIST | lrhand, rhand | mixed (ver Slots found) | 11 |
| FLAG | lrhand | 2H (lrhand) | 10 |
| OWNTHING | lrhand | 2H (lrhand) | 9 |
| POLE | lrhand | 2H (lrhand) | 58 |
| RAPIER | rhand | 1H (rhand) | 38 |
| SWORD | lrhand, rhand | mixed (ver Slots found) | 206 |

### 13.2 Base entries per grade and weapon type

| Weapon type | NONE | D | C | B | A | S | S80 | S84 |
|-------------|------|---|---|---|---|---|-----|-----|
| ANCIENTSWORD | 2 | 6 | 3 | 2 | 3 | 5 | 1 | 4 |
| BLUNT | 39 | 31 | 35 | 14 | 16 | 21 | 2 | 17 |
| BOW | 16 | 9 | 5 | 2 | 3 | 6 | 2 | 4 |
| CROSSBOW | 2 | 7 | 5 | 2 | 3 | 4 | 2 | 4 |
| DAGGER | 22 | 24 | 8 | 3 | 3 | 6 | 1 | 4 |
| DUAL | 4 | 32 | 35 | 52 | 5 | 5 | 2 | 3 |
| DUALDAGGER | 1 | 0 | 0 | 0 | 0 | 1 | 3 | 3 |
| DUALFIST | 14 | 6 | 4 | 3 | 3 | 5 | 2 | 4 |
| ETC | 7 | 14 | 11 | 6 | 5 | 1 | 0 | 0 |
| FISHINGROD | 1 | 1 | 1 | 1 | 1 | 1 | 0 | 0 |
| FIST | 11 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| FLAG | 10 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| OWNTHING | 9 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| POLE | 11 | 14 | 8 | 2 | 10 | 7 | 2 | 4 |
| RAPIER | 3 | 9 | 11 | 2 | 3 | 5 | 2 | 3 |
| SWORD | 97 | 27 | 26 | 7 | 18 | 13 | 5 | 13 |

### 13.3 Notable entries per weapon type (top pAtk / top mAtk among base entries)

- **ANCIENTSWORD** - top pAtk: 9389 Infinity Sword (S, p568) ; 15556 Pyseal Blade (S84, p473) ; 15570 Finale Blade (S84, p449)
  - top mAtk: 9389 Infinity Sword (S, m230) ; 15556 Pyseal Blade (S84, m192) ; 15688 Triumph Ancientsword (S84, m183)
- **BLUNT** - top pAtk: 6615 Infinity Crusher (S, p638) ; 15547 Contristo Hammer (S84, p532) ; 6613 Infinity Axe (S, p524)
  - top mAtk: 6616 Infinity Scepter (S, m337) ; 6614 Infinity Rod (S, m307) ; 15552 Cyclic Cane (S84, m281)
- **BOW** - top pAtk: 6619 Infinity Bow (S, p952) ; 15554 Recurve Thorne Bow (S84, p794) ; 15568 Skull Carnium Bow (S84, p768)
  - top mAtk: 6619 Infinity Bow (S, m230) ; 15554 Recurve Thorne Bow (S84, m192) ; 15686 Triumph Bow (S84, m183)
- **CROSSBOW** - top pAtk: 9390 Infinity Shooter (S, p584) ; 15557 Thorne Crossbow (S84, p487) ; 15571 Dominion Crossbow (S84, p471)
  - top mAtk: 9390 Infinity Shooter (S, m230) ; 15557 Thorne Crossbow (S84, m192) ; 15689 Triumph Crossbow (S84, m183)
- **DAGGER** - top pAtk: 1305 Knife (NONE, p30000) ; 6617 Infinity Stinger (S, p458) ; 15545 Mamba Edge (S84, p382)
  - top mAtk: 6617 Infinity Stinger (S, m230) ; 15545 Mamba Edge (S84, m192) ; 15677 Triumph Dagger (S84, m183)
- **DUAL** - top pAtk: 6620 Infinity Wing (S, p638) ; 16158 Eternal Core Dual Sword (S84, p532) ; 16154 Periel Dual Sword (S84, p505)
  - top mAtk: 6620 Infinity Wing (S, m230) ; 16158 Eternal Core Dual Sword (S84, m192) ; 16154 Periel Dual Sword (S84, m183)
- **DUALDAGGER** - top pAtk: 16156 Mamba Edge Dual Daggers (S84, p437) ; 16152 Skull Edge Dual Daggers (S84, p415) ; 13884 Vesper Dual Daggers (S84, p360)
  - top mAtk: 16156 Mamba Edge Dual Daggers (S84, m192) ; 16152 Skull Edge Dual Daggers (S84, m183) ; 13884 Vesper Dual Daggers (S84, m181)
- **DUALFIST** - top pAtk: 6618 Infinity Fang (S, p638) ; 15549 Jade Claw (S84, p532) ; 15563 Octo Claw (S84, p505)
  - top mAtk: 6618 Infinity Fang (S, m230) ; 15549 Jade Claw (S84, m192) ; 15681 Triumph Jamadhr (S84, m183)
- **ETC** - top pAtk: 346 Tears of Fallen Angel (S, p201) ; 341 Forgotten Tome (A, p186) ; 345 Deathbringer Sword (A, p186)
  - top mAtk: 346 Tears of Fallen Angel (S, m162) ; 341 Forgotten Tome (A, m152) ; 345 Deathbringer Sword (A, m152)
- **FISHINGROD** - top pAtk: 6532 KingFisher Rod (B, p1) ; 6533 Cygnus Pole (A, p1) ; 6534 Triton Pole (S, p1)
  - top mAtk: 6532 KingFisher Rod (B, m1) ; 6533 Cygnus Pole (A, m1) ; 6534 Triton Pole (S, m1)
- **FIST** - top pAtk: 8190 Demonic Sword Zariche (NONE, p361) ; 8689 Blood Sword Akamanah (NONE, p361) ; 251 Human Mystic Fist (NONE, p0)
  - top mAtk: 8190 Demonic Sword Zariche (NONE, m137) ; 8689 Blood Sword Akamanah (NONE, m137) ; 251 Human Mystic Fist (NONE, m0)
- **FLAG** - top pAtk: 13535 Flag of Innadril (NONE, p0) ; 13534 Flag of Aden (NONE, p0) ; 13536 Goddard Flag (NONE, p0)
  - top mAtk: 13535 Flag of Innadril (NONE, m0) ; 13534 Flag of Aden (NONE, m0) ; 13536 Goddard Flag (NONE, m0)
- **OWNTHING** - top pAtk: 13566 Goddard Ward (NONE, p0) ; 13565 Innadril Ward (NONE, p0) ; 13568 Schuttgart Ward (NONE, p0)
  - top mAtk: 13566 Goddard Ward (NONE, m0) ; 13565 Innadril Ward (NONE, m0) ; 13568 Schuttgart Ward (NONE, m0)
- **POLE** - top pAtk: 1303 Lance (NONE, p30000) ; 6621 Infinity Spear (S, p524) ; 15550 Demitelum (S84, p437)
  - top mAtk: 6621 Infinity Spear (S, m230) ; 15550 Demitelum (S84, m192) ; 15564 Doubletop Spear (S84, m183)
- **RAPIER** - top pAtk: 9388 Infinity Rapier (S, p475) ; 15555 Heavenstare Rapier (S84, p396) ; 15569 Gemtail Rapier (S84, p376)
  - top mAtk: 9388 Infinity Rapier (S, m230) ; 15555 Heavenstare Rapier (S84, m192) ; 15687 Triumph Rapier (S, m183)
- **SWORD** - top pAtk: 6612 Infinity Cleaver (S, p638) ; 9661 Enchanted Fenrir Fang (NONE, p537) ; 15548 Lava Saw (S84, p532)
  - top mAtk: 15553 Archangel Sword (S84, m256) ; 15685 Triumph Magic Sword (S84, m244) ; 15567 Veniplant Sword (S84, m244)

## 14. Jewelry Reference

### 14.1 Jewelry per grade (counts and examples)

| Grade | Necklaces | Earrings | Rings | Example IDs (neck / earring / ring) |
|-------|-----------|----------|-------|------------------------------------|
| NONE | 15 | 7 | 16 | 118 Necklace of Magic / 112 Apprentice's Earring / 116 Magic Ring |
| D | 6 | 7 | 7 | 910 Necklace of Devotion / 847 Red Crescent Earring / 879 Enchanted Ring |
| C | 5 | 5 | 5 | 119 Necklace of Seal / 852 Moonstone Earring / 883 Aquastone Ring |
| B | 15 | 15 | 18 | 918 Adamantite Necklace / 856 Adamantite Earring / 117 Ring of Mana |
| A | 17 | 17 | 17 | 924 Majestic Necklace / 862 Majestic Earring / 893 Majestic Ring |
| S | 12 | 13 | 14 | 920 Tateossian Necklace / 858 Tateossian Earring / 889 Tateossian Ring |
| S80 | 11 | 12 | 11 | 15282 Gludio Water Royal Guard Necklace / 10170 Baylor's Earring / 15291 Gludio Earth Royal Guard Ring |
| S84 | 6 | 6 | 4 | 14164 Vesper Necklace / 14163 Vesper Earring / 14165 Vesper Ring |

### 14.2 Unique / boss jewelry (base entries)

| ID | Name | Grade | Slot |
|----|------|-------|------|
| 858 | Tateossian Earring | S | rear;lear |
| 862 | Majestic Earring | A | rear;lear |
| 871 | Phoenix Earring | A | rear;lear |
| 872 | Cerberus Earring | A | rear;lear |
| 889 | Tateossian Ring | S | rfinger;lfinger |
| 893 | Majestic Ring | A | rfinger;lfinger |
| 902 | Phoenix Ring | A | rfinger;lfinger |
| 903 | Cerberus Ring | A | rfinger;lfinger |
| 920 | Tateossian Necklace | S | neck |
| 924 | Majestic Necklace | A | neck |
| 933 | Phoenix Necklace | A | neck |
| 934 | Cerberus Necklace | A | neck |
| 6656 | Earring of Antharas | S | rear;lear |
| 6657 | Necklace of Valakas | S | neck |
| 6658 | Ring of Baium | S | rfinger;lfinger |
| 6659 | Earring of Zaken | S | rear;lear |
| 6660 | Ring of Queen Ant | B | rfinger;lfinger |
| 6661 | Earring of Orfen | A | rear;lear |
| 6662 | Ring of Core | A | rfinger;lfinger |
| 8191 | Necklace of Frintezza | A | neck |
| 9455 | Dynasty Earrings | S | rear;lear |
| 9456 | Dynasty Necklace | S | neck |
| 9457 | Dynasty Ring | S | rfinger;lfinger |
| 9458 | Dynasty Earrings - Stun Resistance | S | rear;lear |
| 9459 | Dynasty Necklace - Stun Resistance | S | neck |
| 9460 | Dynasty Ring - Stun Resistance | S | rfinger;lfinger |
| 9461 | Dynasty Earrings - Poison Resistance | S | rear;lear |
| 9462 | Dynasty Necklace - Poison Resistance | S | neck |
| 9463 | Dynasty Ring - Poison Resistance | S | rfinger;lfinger |
| 9464 | Dynasty Earrings - Bleed Resistance | S | rear;lear |
| 9465 | Dynasty Necklace - Bleed Resistance | S | neck |
| 9466 | Dynasty Ring - Bleed Resistance | S | rfinger;lfinger |
| 9467 | Dynasty Earrings - Sleep Resistance | S | rear;lear |
| 9468 | Dynasty Necklace - Sleep Resistance | S | neck |
| 9469 | Dynasty Ring - Sleep Resistance | S | rfinger;lfinger |
| 9470 | Dynasty Earrings - Paralysis Resistance | S | rear;lear |
| 9471 | Dynasty Necklace - Paralysis Resistance | S | neck |
| 9472 | Dynasty Ring - Paralysis Resistance | S | rfinger;lfinger |
| 9473 | Dynasty Earrings - Hold Resistance | S | rear;lear |
| 9474 | Dynasty Necklace - Hold Resistance | S | neck |
| 9475 | Dynasty Ring - Hold Resistance | S | rfinger;lfinger |
| 9476 | Dynasty Earrings - Fear Resistance | S | rear;lear |
| 9477 | Dynasty Necklace - Fear Resistance | S | neck |
| 9478 | Dynasty Ring - Fear Resistance | S | rfinger;lfinger |
| 10170 | Baylor's Earring | S80 | rear;lear |
| 11584 | Phoenix Necklace | A | neck |
| 11585 | Phoenix Ring | A | rfinger;lfinger |
| 11586 | Phoenix Earring | A | rear;lear |
| 11587 | Majestic Necklace | A | neck |
| 11588 | Majestic Ring | A | rfinger;lfinger |
| 11589 | Majestic Earring | A | rear;lear |
| 11596 | Tateossian Necklace | S | neck |
| 11597 | Tateossian Ring | S | rfinger;lfinger |
| 11598 | Tateossian Earring | S | rear;lear |
| 11599 | Dynasty Necklace | S | neck |
| 11600 | Dynasty Ring | S | rfinger;lfinger |
| 11601 | Dynasty Earrings | S | rear;lear |
| 14163 | Vesper Earring | S84 | rear;lear |
| 14164 | Vesper Necklace | S84 | neck |
| 14165 | Vesper Ring | S84 | rfinger;lfinger |
| 15717 | Elegia Ring | S84 | rfinger;lfinger |
| 15718 | Elegia Earring | S84 | rear;lear |
| 15719 | Elegia Necklace | S84 | neck |
| 15720 | Vorpal Ring | S84 | rfinger;lfinger |
| 15721 | Vorpal Earring | S84 | rear;lear |
| 15722 | Vorpal Necklace | S84 | neck |
| 15723 | Moirai Ring | S80 | rfinger;lfinger |
| 15724 | Moirai Earring | S80 | rear;lear |
| 15725 | Moirai Necklace | S80 | neck |
| 16375 | Vesper Ring | S84 | rfinger;lfinger |
| 16376 | Vesper Earring | S84 | rear;lear |
| 16377 | Vesper Necklace | S84 | neck |
| 16378 | Moirai Ring | S80 | rfinger;lfinger |
| 16379 | Moirai Earring | S80 | rear;lear |
| 16380 | Moirai Necklace | S80 | neck |
| 21712 | Blessed Earring of Zaken | S84 | rear;lear |
| 22173 | Improved Ring of Baium | S | rfinger;lfinger |
| 22174 | Improved Ring of Queen Ant | B | rfinger;lfinger |
| 22175 | Improved Blessed Earring of Zaken | S84 | rear;lear |

## 15. Equipment by Role

Dos niveles de evidencia, siempre separados:

- **VERIFIED**: el item existe en el datapack con ese ID, grade, tipo y slot.
- **RECOMMENDATION**: seleccion de ese item para un rol (decision de diseno, NO hecho del datapack).

Ninguna recomendacion usa IDs no verificados. Detalle exhaustivo por familia: secciones 4-14.

### 15.1 Healer (Bishop / Cardinal)

| Grade | Armor (MAGIC) | Weapon | Jewelry | Estado |
|-------|---------------|--------|---------|--------|
| NONE | 1101 + 1104 + 44 (Devotion) | 6 Apprentice's Wand (BLUNT rhand) | 118 / 112 / 116 | VERIFIED / RECOMMENDATION |
| D | 436 + 469 + 2447 (Knowledge) | 188 Ghost Staff (lrhand, m79) o 189 Staff of Life (rhand, m72) | 910 / 847-851 / 879-882 | VERIFIED / RECOMMENDATION |
| C | 439 + 471 + 2454 (Karmian) | 206 Demon's Staff (lrhand, m122) | 119 / 852-855 / 883-886 | VERIFIED / RECOMMENDATION |
| B | 2397 + 2402 + 503 + 5712 + 5728 (Zubei robe) | 210 Staff of Evil Spirits (lrhand, m145) | 918 / 856-861 / 887-892 | VERIFIED / RECOMMENDATION |
| A | 2407 (onepiece) + 512 + 5767 + 5779 (Dark Crystal Robe) | 8688 Daimon Crystal (lrhand, m177) o 151 Sword of Miracles (rhand, m152) | 924 / 862-872 / 893-903 | VERIFIED / RECOMMENDATION |
| S | 6383 (onepiece) + 6386 + 6384 + 6385 (Major Arcana) | 6579 Arcana Mace (rhand, m175) o 6366 Imperial Staff (lrhand, m193) | 920 / 858 / 889 | VERIFIED / RECOMMENDATION |
| S80 | 15611 + 15614 + 15608 + 15617 + 15620 (Moirai) | ver seccion 13 | 15725 / 15724 / 15723 | VERIFIED (armor/jewelry) / NOT VERIFIED (arma) |
| S84 | Vesper 13434+13444+13139+13445+13446 ; Vorpal 15594+15597+15591+15600+15603 ; Elegia 15577+15580+15574+15583+15586 | ver seccion 13 | ver 14.1 | VERIFIED (armor) / NOT VERIFIED (arma) |

### 15.2 Tank (Paladin / Dark Avenger / Shillien Knight)

| Grade | Armor (HEAVY) | Weapon 1H (rhand) | Shield (lhand) | Estado |
|-------|---------------|-------------------|----------------|--------|
| NONE | no hay set HEAVY No-Grade verificado (ver 19) | 2369 Squire's Sword | 18 / 19 / 20 / 625 | VERIFIED (item) / NOT VERIFIED (set) |
| D | Mithril 58 + 59 + 47 ; Brigandine 352 + 2378 + 2411 | 129 Sword of Revolution (p79) o 159 Bonebreaker (p92) | 628 Hoplon o 2493 Brigandine Shield | VERIFIED / RECOMMENDATION |
| C | Full Plate 356 (onepiece) + 2414 ; Compound 60 (onepiece) + 517 ; Chain 354 + 381 + 2413 | 135 Samurai Longsword (p156) o 2503 Yaksa Mace | 2497 / 107 / 2495 | VERIFIED / RECOMMENDATION |
| B | Avadon 2376 + 2379 + 2415 + 5714 + 5730 ; Doom 2381 + 2417 + 5722 + 5738 ; Zubei 357 + 383 + 503 + 5710 + 5726 | 4719 Sword of Damascus - Haste (SA) o 4751 Deadman's Glory - Health (SA) | 673 Avadon o 110 Doom (Zubei no tiene shield) | VERIFIED / RECOMMENDATION |
| A | Dark Crystal 365 + 388 + 512 + 5765 + 5777 ; Nightmare 374 + 2418 + 5771 + 5783 ; Majestic 2383 + 2419 + 5774 + 5786 | 8678 Sirra's Blade (p251) o 2500 Dark Legion's Edge (p232) | 641 Dark Crystal o 2498 Shield of Nightmare | VERIFIED / RECOMMENDATION |
| S | Imperial Crusader 6373 + 6374 + 6378 + 6375 + 6376 | 6364 Forgotten Blade (p281) o 9442 Dynasty Sword (p333) | 6377 Imperial Crusader (o 634 Dragon Shield, grade S) | VERIFIED / RECOMMENDATION |
| S80 | Moirai 15609 + 15612 + 15606 + 15615 + 15618 | ver seccion 13 | 15621 Moirai Shield / 15622 Moirai Sigil | VERIFIED (existencia) |
| S84 | Vesper 13432+13438+13137+13439+13440 ; Vorpal 15592+15595+15589+15598+15601 ; Elegia 15575+15578+15572+15581+15584 | ver seccion 13 | 13471 / 15604 / 15587 | VERIFIED (existencia) |

REGLA DE ROL (nativa, obligatoria): Tank = arma `rhand` (1H) + escudo `lhand`. Un dual/POLE (`lrhand`) desequipa el escudo y viceversa (seccion 16).

### 15.3 Fighter / DPS (Gladiator / Warlord / Titan / Destroyer)

| Grade | Armor (HEAVY) | Weapon | Estado |
|-------|---------------|--------|--------|
| NONE | sin set HEAVY verificado | 3 Broadsword (NONE, p11) | VERIFIED (item) - anomalia A1 |
| D | Brigandine 352 + 2378 + 2411 | DUAL: 2536 Spinebone Sword*Crimson Sword o 2529 Bastard Sword*Crimson Sword | VERIFIED / RECOMMENDATION |
| C | Full Plate 356 (onepiece) + 2414 | DUAL: 2582 Katana*Katana (p190) ; POLE del ladder actual: 301 Scorpion - anomalia A2 | VERIFIED / RECOMMENDATION |
| B | Avadon 2376 + 2379 + 2415 + 5714 + 5730 | DUAL: 2600 Raid Sword*Caliburs (p236) | VERIFIED / RECOMMENDATION |
| A | Dark Crystal 365 + 388 + 512 + 5765 + 5777 | DUAL: 5706 Damascus*Damascus (p282) o 8938 Damascus * Tallum Blade (p305) | VERIFIED / RECOMMENDATION |
| S | Imperial Crusader 6373-6376 (o Dark Crystal, como el ladder actual) | DUAL: 6580 Tallum Blade*Dark Legion's Edge (p342) | VERIFIED / RECOMMENDATION |
| S80/S84 | Moirai / Vesper / Vorpal / Elegia (secciones 10 y 10b) | ver seccion 13 | VERIFIED (existencia) |

### 15.4 Archer (Hawkeye / Silver Ranger / Phantom Ranger / Sagittarius)

| Grade | Armor (LIGHT) | Weapon (BOW, lrhand) | Estado |
|-------|---------------|----------------------|--------|
| D | 394 + 416 + 2422 ; 395 + 417 + 2424 | BOW base del grade (13.2) | VERIFIED / RECOMMENDATION (tipo) |
| C | 397 + 2387 + 62 ; 398 + 418 + 2431 ; 400 + 420 + 2436 ; 401 + 2437 (onepiece) | BOW base del grade | VERIFIED / RECOMMENDATION |
| B | 2384 + 2388 + 503 + 5711 + 5727 ; onepiece 2390/2391/2392 | BOW base del grade | VERIFIED / RECOMMENDATION |
| A | 2385 + 2389 + 512 + 5766 + 5778 ; onepiece 2393/2394/2395 | BOW base del grade | VERIFIED / RECOMMENDATION |
| S | 6379 Draconic Leather (onepiece) + 6382 + 6380 + 6381 | BOW base del grade | VERIFIED / RECOMMENDATION |

NOTA: BOW ocupa `lrhand`; el `lhand` se usa para flechas (EtcItem), no para escudo (16.2).

### 15.5 Dagger (Treasure Hunter / Abyss Walker / Plains Walker / Adventurer)

| Grade | Armor (LIGHT) | Weapon | Estado |
|-------|---------------|--------|--------|
| D | 394/395 (+416/417 + 2422/2424) | DAGGER `rhand` (13.2) | VERIFIED / RECOMMENDATION (tipo) |
| C | 397/398/400/401 | DAGGER `rhand`; DUALDAGGER `lrhand` existe (13.1) | VERIFIED / RECOMMENDATION |
| B | 2384/2390/2391/2392 | DAGGER `rhand` | VERIFIED / RECOMMENDATION |
| A | 2385/2393/2394/2395 | DAGGER `rhand` | VERIFIED / RECOMMENDATION |
| S | 6379 Draconic (set 57) | DAGGER `rhand` | VERIFIED / RECOMMENDATION |

NOTA: `DUALDAGGER` existe en NONE/S/S80/S84 pero suele llevar `categoryType` de clase (Dagger Master): verificar antes de usarlo.

### 15.6 Mage (Sorcerer / Spellsinger / Spellhowler / Archmage / Soultaker)

| Grade | Armor (MAGIC) | Weapon | Estado |
|-------|---------------|--------|--------|
| NONE | 1101 + 1104 + 44 (Devotion) | 6 Apprentice's Wand (BLUNT rhand) | VERIFIED / RECOMMENDATION |
| D | 436 + 469 + 2447 ; 437 + 470 + 2450 | 188 Ghost Staff (lrhand, m79) | VERIFIED / RECOMMENDATION |
| C | 439 + 471 + 2454 ; 441 + 472 + 2459 ; 442 + 473 + 2463 | 206 Demon's Staff (lrhand, m122) | VERIFIED / RECOMMENDATION |
| B | 2397 + 2402 + 503 + 5712 + 5728 ; alt 2398 / 2399 ; 2406 (onepiece) | 210 Staff of Evil Spirits (lrhand, m145) | VERIFIED / RECOMMENDATION |
| A | 2407 (onepiece) + 512 + 5767 + 5779 ; 2408 ; 2409 ; 2400 + 2405 | 8688 Daimon Crystal (lrhand, m177) o 151 Sword of Miracles (rhand, m152) o 213 Branch of the Mother Tree (lrhand, m167) | VERIFIED / RECOMMENDATION |
| S | 6383 (onepiece) + 6386 + 6384 + 6385 | 6579 Arcana Mace (rhand, m175) o 9449 Dynasty Mace (rhand, m202) o 9444 Dynasty Phantom (rhand, m202) o 6366 Imperial Staff (lrhand, m193) | VERIFIED / RECOMMENDATION |

### 15.7 Support (Prophet / Warcryer / Elven Elder / Shillien Elder / Hierophant)

| Grade | Armor | Weapon | Estado |
|-------|-------|--------|--------|
| NONE | 1101 + 1104 + 44 | 6 Apprentice's Wand | VERIFIED / RECOMMENDATION |
| D | MAGIC 436/437 o LIGHT 394/395 | 188 / 189 (BLUNT m72-m79) | VERIFIED / RECOMMENDATION |
| C | MAGIC 439/441/442 o LIGHT 397/398/400/401 | 206 Demon's Staff | VERIFIED / RECOMMENDATION |
| B | MAGIC 2397/2398/2399 o LIGHT 2384/2390/2391/2392 | 210 Staff of Evil Spirits | VERIFIED / RECOMMENDATION |
| A | MAGIC 2407/2408/2409/2400 o LIGHT 2385/2393/2394/2395 | 8688 / 151 / 213 | VERIFIED / RECOMMENDATION |
| S | MAGIC 6383 o LIGHT 6379 | 6579 / 6366 / 9444 / 9449 | VERIFIED / RECOMMENDATION |

NOTA: no hay restriccion por clase (16.5); la eleccion MAGIC vs LIGHT es de diseno del perfil.

### 15.8 Arquetipos por tipo de arma (verificado)

| Arquetipo | Weapon types y slot real | Evidencia |
|-----------|--------------------------|-----------|
| Melee 1H + escudo | SWORD / BLUNT / DAGGER `rhand` + escudo `lhand` | 13.1 + 16.2 |
| Melee 2H | SWORD / BLUNT / POLE / ANCIENTSWORD `lrhand` | 13.1 |
| Dual | DUAL / DUALFIST / DUALDAGGER `lrhand` | 13.1 |
| Ranged | BOW / CROSSBOW `lrhand` | 13.1 |
| Caster | BLUNT (staff/mace) `rhand` o `lrhand`; algunos SWORD (p.ej. 151) | 13.3 |


## 16. Native Slot Rules

### 16.1 El slot lo determina SOLO `bodypart`

No lo determina `weapon_type` ni `armor_type`. Valores `bodypart` observados en el catalogo:

| bodypart | Uso | En catalogo |
|----------|-----|-------------|
| `rhand` | arma 1 mano | si |
| `lrhand` | arma 2 manos / dual / bow / pole / fist | si |
| `lhand` | escudo o sigil | si |
| `chest` / `legs` / `onepiece` | peto / piernas / armadura completa | si |
| `head` / `gloves` / `feet` | cabeza / guantes / botas | si |
| `neck` | collar (1) | si |
| `rear;lear` | 2 pendientes | si |
| `rfinger;lfinger` | 2 anillos | si |
| `back`/`waist`/`deco1`/`lbracelet`/`rbracelet`/`hair`/`hair2`/`hairall`/`underwear`/`alldress` | cloak / belt / talisman / bracelets / apariencia | NO (fuera de alcance) |

### 16.2 Incompatibilidades nativas (UPSTREAM `Inventory.equipItem`, ~1263-1400)

| Regla | Comportamiento | Consecuencia practica |
|-------|----------------|------------------------|
| `LR_HAND` equipado | limpia `lhand` | dual / 2H / POLE / BOW **desequipa el escudo** |
| `L_HAND` (escudo) equipado | si el `rhand` tiene `LR_HAND` (salvo bow+arrow, crossbow+bolt, rod+lure) limpia `rhand` | equipar escudo **desequipa el arma de 2 manos** |
| `FULL_ARMOR` (onepiece) equipado | limpia `legs` | no se puede usar onepiece + piernas a la vez |
| `LEGS` equipado | si el `chest` es `FULL_ARMOR`, limpia `chest` | idem, se expulsan mutuamente |
| pendientes (`rear;lear`) | 1ro slot libre `LEAR`, luego `REAR`, luego reemplaza `LEAR` | 2 items del mismo ID ocupan los 2 slots |
| anillos (`rfinger;lfinger`) | 1ro slot libre `LFINGER`, luego `RFINGER`, luego reemplaza `LFINGER` | idem |
| `neck` | reemplazo directo | 1 solo collar |

### 16.3 Escudos y sigils

- Escudos: `bodypart=lhand`, **sin `armor_type`** (en el catalogo aparecen con armor type `(none)`).
- Sigils: `bodypart=lhand` + `armor_type=SIGIL`. Existen en S (10119 Dynasty, 12811/13078 Arcana), S80 (15622 Moirai) y S84 (12813 Vesper, 15588 Elegia, 15605 Vorpal).
- Escudos por grade (verificados): NONE 18/19/20/102/625 ; D 626/627/628/629/630/2493/2494 ; C 103/107/631/632/2495/2496/2497 ; B 104-111 + 633/635-674 ; A 641/2498 ; S 634/6377/9441.

### 16.4 Grado / expertise: penaliza, no bloquea

- `getExpertiseLevel()` = nivel del skill 239; `refreshExpertisePenalty()` aplica `WEAPON_GRADE_PENALTY` / `ARMOR_GRADE_PENALTY` (0-4) si el `crystal_type` supera el expertise.
- TARGET: `ExpertisePenalty = True` (`config/Player.ini`). Grado inferior nunca penaliza ni bloquea.
- Detalle: `BOTAI-06-B2_LOCAL_EQUIPMENT_SKILLS_VERIFICATION.md` (7.1).

### 16.5 Restricciones por clase y por raza

- **Clase: NO EXISTEN** en las piezas verificadas (0 condiciones `classId=`). El motor no impide que una clase vista otro tipo de armadura.
- **Raza: SI EXISTEN** (`<conditions><player races="...">`). En las piezas inspeccionadas: `HUMAN,ELF,DARK_ELF,ORC,DWARF` (incluye HUMAN).
  Riesgo: un bot KAMAEL quedaria bloqueado en esas armaduras (ver 19).
- Hero/olympiad: `ItemTemplate.checkCondition` bloquea items hero para no-hero/no-GM y en Olympiad. TARGET: `GMItemRestriction = True` (el GM NO queda exento).


## 17. Verified IDs

Trazabilidad: cada ID se vuelve a leer del datapack y se registra nombre, grade, tipo, slot y archivo XML de origen.

| ID | Name | Grade | Type | Slot | Source file |
|----|------|-------|------|------|-------------|
| 3 | Broadsword | NONE | SWORD | rhand | 00000-00099.xml |
| 6 | Apprentice's Wand | NONE | BLUNT | rhand | 00000-00099.xml |
| 112 | Apprentice's Earring | NONE | Armor | rear;lear | 00100-00199.xml |
| 116 | Magic Ring | NONE | Armor | rfinger;lfinger | 00100-00199.xml |
| 118 | Necklace of Magic | NONE | Armor | neck | 00100-00199.xml |
| 129 | Sword of Revolution | D | SWORD | rhand | 00100-00199.xml |
| 135 | Samurai Longsword | C | SWORD | rhand | 00100-00199.xml |
| 151 | Sword of Miracles | A | SWORD | rhand | 00100-00199.xml |
| 159 | Bonebreaker | D | BLUNT | rhand | 00100-00199.xml |
| 164 | Elysian | A | BLUNT | rhand | 00100-00199.xml |
| 188 | Ghost Staff | D | BLUNT | lrhand | 00100-00199.xml |
| 189 | Staff of Life | D | BLUNT | rhand | 00100-00199.xml |
| 206 | Demon's Staff | C | BLUNT | lrhand | 00200-00299.xml |
| 210 | Staff of Evil Spirits | B | BLUNT | lrhand | 00200-00299.xml |
| 213 | Branch of the Mother Tree | A | BLUNT | lrhand | 00200-00299.xml |
| 301 | Scorpion | C | POLE | lrhand | 00300-00399.xml |
| 352 | Brigandine Tunic | D | HEAVY | chest | 00300-00399.xml |
| 356 | Full Plate Armor | C | HEAVY | onepiece | 00300-00399.xml |
| 365 | Dark Crystal Breastplate | A | HEAVY | chest | 00300-00399.xml |
| 374 | Armor of Nightmare | A | HEAVY | onepiece | 00300-00399.xml |
| 383 | Zubei's Gaiters | B | HEAVY | legs | 00300-00399.xml |
| 388 | Dark Crystal Gaiters | A | HEAVY | legs | 00300-00399.xml |
| 394 | Reinforced Leather Shirt | D | LIGHT | chest | 00300-00399.xml |
| 397 | Mithril Shirt | C | LIGHT | chest | 00300-00399.xml |
| 400 | Theca Leather Armor | C | LIGHT | chest | 00400-00499.xml |
| 401 | Drake Leather Armor | C | LIGHT | onepiece | 00400-00499.xml |
| 436 | Tunic of Knowledge | D | MAGIC | chest | 00400-00499.xml |
| 437 | Mithril Tunic | D | MAGIC | chest | 00400-00499.xml |
| 439 | Karmian Tunic | C | MAGIC | chest | 00400-00499.xml |
| 441 | Demon's Tunic | C | MAGIC | chest | 00400-00499.xml |
| 442 | Divine Tunic | C | MAGIC | chest | 00400-00499.xml |
| 469 | Stockings of Knowledge | D | MAGIC | legs | 00400-00499.xml |
| 471 | Karmian Stockings | C | MAGIC | legs | 00400-00499.xml |
| 503 | Zubei's Helmet | B | Armor | head | 00500-00599.xml |
| 512 | Dark Crystal Helmet | A | Armor | head | 00500-00599.xml |
| 547 | Tallum Helm | A | Armor | head | 00500-00599.xml |
| 628 | Hoplon | D | Armor | lhand | 00600-00699.xml |
| 634 | Dragon Shield | S | Armor | lhand | 00600-00699.xml |
| 641 | Dark Crystal Shield | A | Armor | lhand | 00600-00699.xml |
| 673 | Avadon Shield | B | Armor | lhand | 00600-00699.xml |
| 852 | Moonstone Earring | C | Armor | rear;lear | 00800-00899.xml |
| 856 | Adamantite Earring | B | Armor | rear;lear | 00800-00899.xml |
| 858 | Tateossian Earring | S | Armor | rear;lear | 00800-00899.xml |
| 862 | Majestic Earring | A | Armor | rear;lear | 00800-00899.xml |
| 868 | Earring of Phantom | A | Armor | rear;lear | 00800-00899.xml |
| 871 | Phoenix Earring | A | Armor | rear;lear | 00800-00899.xml |
| 872 | Cerberus Earring | A | Armor | rear;lear | 00800-00899.xml |
| 882 | Mithril Ring | D | Armor | rfinger;lfinger | 00800-00899.xml |
| 889 | Tateossian Ring | S | Armor | rfinger;lfinger | 00800-00899.xml |
| 893 | Majestic Ring | A | Armor | rfinger;lfinger | 00800-00899.xml |
| 902 | Phoenix Ring | A | Armor | rfinger;lfinger | 00900-00999.xml |
| 903 | Cerberus Ring | A | Armor | rfinger;lfinger | 00900-00999.xml |
| 910 | Necklace of Devotion | D | Armor | neck | 00900-00999.xml |
| 918 | Adamantite Necklace | B | Armor | neck | 00900-00999.xml |
| 919 | Blessed Necklace | C | Armor | neck | 00900-00999.xml |
| 920 | Tateossian Necklace | S | Armor | neck | 00900-00999.xml |
| 924 | Majestic Necklace | A | Armor | neck | 00900-00999.xml |
| 926 | Necklace of Black Ore | B | Armor | neck | 00900-00999.xml |
| 2376 | Avadon Breastplate | B | HEAVY | chest | 02300-02399.xml |
| 2378 | Brigandine Gaiters | D | HEAVY | legs | 02300-02399.xml |
| 2379 | Avadon Gaiters | B | HEAVY | legs | 02300-02399.xml |
| 2381 | Doom Plate Armor | B | HEAVY | onepiece | 02300-02399.xml |
| 2382 | Tallum Plate Armor | A | HEAVY | onepiece | 02300-02399.xml |
| 2383 | Majestic Plate Armor | A | HEAVY | onepiece | 02300-02399.xml |
| 2384 | Zubei's Leather Shirt | B | LIGHT | chest | 02300-02399.xml |
| 2385 | Dark Crystal Leather Armor | A | LIGHT | chest | 02300-02399.xml |
| 2389 | Dark Crystal Leggings | A | LIGHT | legs | 02300-02399.xml |
| 2397 | Tunic of Zubei | B | MAGIC | chest | 02300-02399.xml |
| 2399 | Tunic of Doom | B | MAGIC | chest | 02300-02399.xml |
| 2402 | Stockings of Zubei | B | MAGIC | legs | 02400-02499.xml |
| 2407 | Dark Crystal Robe | A | MAGIC | onepiece | 02400-02499.xml |
| 2409 | Majestic Robe | A | MAGIC | onepiece | 02400-02499.xml |
| 2411 | Brigandine Helmet | D | Armor | head | 02400-02499.xml |
| 2414 | Full Plate Helmet | C | Armor | head | 02400-02499.xml |
| 2415 | Avadon Circlet | B | Armor | head | 02400-02499.xml |
| 2417 | Doom Helmet | B | Armor | head | 02400-02499.xml |
| 2418 | Helm of Nightmare | A | Armor | head | 02400-02499.xml |
| 2419 | Majestic Circlet | A | Armor | head | 02400-02499.xml |
| 2493 | Brigandine Shield | D | Armor | lhand | 02400-02499.xml |
| 2495 | Chain Shield | C | Armor | lhand | 02400-02499.xml |
| 2497 | Full Plate Shield | C | Armor | lhand | 02400-02499.xml |
| 2498 | Shield of Nightmare | A | Armor | lhand | 02400-02499.xml |
| 2500 | Dark Legion's Edge | A | SWORD | rhand | 02500-02599.xml |
| 2503 | Yaksa Mace | C | BLUNT | rhand | 02500-02599.xml |
| 2529 | Bastard Sword*Crimson Sword | D | DUAL | lrhand | 02500-02599.xml |
| 2536 | Spinebone Sword*Crimson Sword | D | DUAL | lrhand | 02500-02599.xml |
| 2582 | Katana*Katana | C | DUAL | lrhand | 02500-02599.xml |
| 2600 | Raid Sword*Caliburs | B | DUAL | lrhand | 02600-02699.xml |
| 4719 | Sword of Damascus - Haste | B | SWORD | rhand | 04700-04799.xml |
| 4751 | Deadman's Glory - Health | B | BLUNT | rhand | 04700-04799.xml |
| 5706 | Damascus*Damascus | A | DUAL | lrhand | 05700-05799.xml |
| 5710 | Zubei's Gauntlets - Heavy Armor | B | Armor | gloves | 05700-05799.xml |
| 5714 | Avadon Gloves - Heavy Armor | B | Armor | gloves | 05700-05799.xml |
| 5722 | Doom Gloves - Heavy Armor | B | Armor | gloves | 05700-05799.xml |
| 5726 | Zubei's Boots - Heavy Armor | B | Armor | feet | 05700-05799.xml |
| 5730 | Avadon Boots - Heavy Armor | B | Armor | feet | 05700-05799.xml |
| 5738 | Doom Boots - Heavy Armor | B | Armor | feet | 05700-05799.xml |
| 5765 | Dark Crystal Gloves - Heavy Armor | A | Armor | gloves | 05700-05799.xml |
| 5766 | Dark Crystal Gloves - Light Armor | A | Armor | gloves | 05700-05799.xml |
| 5767 | Dark Crystal Gloves - Robe | A | Armor | gloves | 05700-05799.xml |
| 5770 | Tallum Gloves - Robe | A | Armor | gloves | 05700-05799.xml |
| 5773 | Gauntlets of Nightmare - Robe | A | Armor | gloves | 05700-05799.xml |
| 5776 | Majestic Gauntlets - Robe | A | Armor | gloves | 05700-05799.xml |
| 5777 | Dark Crystal Boots - Heavy Armor | A | Armor | feet | 05700-05799.xml |
| 5780 | Tallum Boots - Heavy Armor | A | Armor | feet | 05700-05799.xml |
| 5783 | Boots of Nightmare - Heavy Armor | A | Armor | feet | 05700-05799.xml |
| 5786 | Majestic Boots - Heavy Armor | A | Armor | feet | 05700-05799.xml |
| 6364 | Forgotten Blade | S | SWORD | rhand | 06300-06399.xml |
| 6365 | Basalt Battlehammer | S | BLUNT | rhand | 06300-06399.xml |
| 6366 | Imperial Staff | S | BLUNT | lrhand | 06300-06399.xml |
| 6373 | Imperial Crusader Breastplate | S | HEAVY | chest | 06300-06399.xml |
| 6374 | Imperial Crusader Gaiters | S | HEAVY | legs | 06300-06399.xml |
| 6375 | Imperial Crusader Gauntlets | S | Armor | gloves | 06300-06399.xml |
| 6376 | Imperial Crusader Boots | S | Armor | feet | 06300-06399.xml |
| 6377 | Imperial Crusader Shield | S | Armor | lhand | 06300-06399.xml |
| 6378 | Imperial Crusader Helmet | S | Armor | head | 06300-06399.xml |
| 6379 | Draconic Leather Armor | S | LIGHT | onepiece | 06300-06399.xml |
| 6380 | Draconic Leather Gloves | S | Armor | gloves | 06300-06399.xml |
| 6383 | Major Arcana Robe | S | MAGIC | onepiece | 06300-06399.xml |
| 6386 | Major Arcana Circlet | S | Armor | head | 06300-06399.xml |
| 6579 | Arcana Mace | S | BLUNT | rhand | 06500-06599.xml |
| 6580 | Tallum Blade*Dark Legion's Edge | S | DUAL | lrhand | 06500-06599.xml |
| 8678 | Sirra's Blade | A | SWORD | rhand | 08600-08699.xml |
| 8680 | Barakiel's Axe | A | BLUNT | rhand | 08600-08699.xml |
| 8688 | Daimon Crystal | A | BLUNT | lrhand | 08600-08699.xml |
| 8938 | Damascus * Tallum Blade | A | DUAL | lrhand | 08900-08999.xml |
| 9417 | Dynasty Breast Plate - Shield Master | S | HEAVY | chest | 09400-09499.xml |
| 9441 | Dynasty Shield | S | Armor | lhand | 09400-09499.xml |
| 9442 | Dynasty Sword | S | SWORD | rhand | 09400-09499.xml |
| 9444 | Dynasty Phantom | S | SWORD | rhand | 09400-09499.xml |
| 9449 | Dynasty Mace | S | BLUNT | rhand | 09400-09499.xml |
| 10119 | Dynasty Sigil | S | SIGIL | lhand | 10100-10199.xml |
| 12813 | Vesper Sigil | S84 | SIGIL | lhand | 12800-12899.xml |
| 13432 | Vesper Breastplate | S84 | HEAVY | chest | 13400-13499.xml |
| 13471 | Vesper Shield | S84 | Armor | lhand | 13400-13499.xml |
| 15572 | Elegia Helmet | S84 | Armor | head | 15500-15599.xml |
| 15575 | Elegia Breastplate | S84 | HEAVY | chest | 15500-15599.xml |
| 15586 | Elegia Shoes | S84 | Armor | feet | 15500-15599.xml |
| 15587 | Elegia Shield | S84 | Armor | lhand | 15500-15599.xml |
| 15592 | Vorpal Breastplate | S84 | HEAVY | chest | 15500-15599.xml |
| 15604 | Vorpal Shield | S84 | Armor | lhand | 15600-15699.xml |
| 15609 | Moirai Breastplate | S80 | HEAVY | chest | 15600-15699.xml |
| 15612 | Moirai Gaiter | S80 | HEAVY | legs | 15600-15699.xml |
| 15621 | Moirai Shield | S80 | Armor | lhand | 15600-15699.xml |
| 15622 | Moirai Sigil | S80 | SIGIL | lhand | 15600-15699.xml |
| 15723 | Moirai Ring | S80 | Armor | rfinger;lfinger | 15700-15799.xml |
| 15724 | Moirai Earring | S80 | Armor | rear;lear | 15700-15799.xml |
| 15725 | Moirai Necklace | S80 | Armor | neck | 15700-15799.xml |

Los IDs de las secciones 4-14 estan igualmente verificados (la tabla de arriba es la muestra de trazabilidad).


## 18. Anomalies

Se DOCUMENTAN, no se corrigen. Todas son VERIFIED en el datapack/config del TARGET.

| # | Anomalia | Evidencia |
|---|----------|-----------|
| A1 | `3 Broadsword` no tiene `crystal_type` (NONE) y su pAtk es 11; se usa como arma de grado D en el ladder DPS vigente | `stats/items/00000-00099.xml` (item 3) + `BotPresets.java` |
| A2 | `301 Scorpion` es **POLE** (`lrhand`), no DUAL; el loadout interno se llama `FIGHTER_DUAL_C` | `stats/items/00300-00399.xml` (item 301) |
| A3 | `852` es **Moonstone Earring** (`crystal_type=C`), usada como "Majestic Earring" (A) en el ladder | `stats/items/00800-00899.xml` + `BotPresets.java:101-102` |
| A4 | `s80_dynasty.xml` declara sets cuyos items son `crystal_type=S` (Dynasty = S en este datapack, no S80) | `armorsets/s80_dynasty.xml` + items 9417-9441 |
| A5 | El XML de sets usa `<chest>` tambien para items `onepiece` (356, 374, 2381, 2382, 2383, 2406-2409, 6379, 6383, 13433/13434/13436/13437, 15576/15577, 15593/15594, 15610/15611) | secciones 12 + items XML |
| A6 | Piezas con IDs alternativos duplicados (11xxx / 12xxx / 16xxx / 168xx) para el mismo item (variantes de scroll/upgrade) | `armorsets/*.xml` (varias lineas por pieza) |
| A7 | Los escudos no llevan `armor_type` (solo `bodypart=lhand`); los sigils si llevan `armor_type=SIGIL` | items 641, 6377, 15621 / 15622, 12813 |
| A8 | 13 sets estan comentados/deshabilitados en el XML (d 9-11, c 16-18, b 38, a 45 y 52-55) | seccion 12.1 |
| A9 | `634 Dragon Shield` es `crystal_type=S`: unico escudo S "clasico" fuera de Imperial Crusader/Dynasty/Arcana | items XML (634) |
| A10 | Los unicos items `armor_type=HEAVY` sin `crystal_type` son de desarrollo ("temporary item for Kamex", ids 1308-1314) -> no hay set HEAVY No-Grade canonico | `stats/items/01300-01399.xml` |
| A11 | Jewelry de guardia de ciudad (14801-14809, 15282-15299) y `10170 Baylor's Earring` son `crystal_type=S80` sin indicarlo en el nombre | seccion 14.2 / items XML |
| A12 | Existe `13529 Test Sigil` (item de desarrollo, `armor_type=SIGIL`) y ~15 items "Monster Only" con `lhand` | items XML |
| A13 | Proliferacion de variantes SA/elementales de armas (3075 entradas con SA vs 919 base): elegir un ID de variante por error es un riesgo real | seccion 3.1 / 13 |
| A14 | `INDEX.md` del Notebook declara `Workspace: E:\L2J MOBIUS IA` (obsoleto: el real es `C:\L2J MOBIUS IA`) | `L2J Notebook/INDEX.md:10` |
| A15 | `2494 Plate Shield` (D) existe pero su set (`d_grade.xml #10`) esta deshabilitado | seccion 12.1 |


## 19. Unverified / Pending Data

| # | Elemento | Estado | Nota |
|---|----------|--------|------|
| U1 | Set HEAVY No-Grade canonico | NOT VERIFIED | Solo aparecen items HEAVY sin grade de desarrollo (A10). Para NONE usar el baseline nativo o dejarlo vacio |
| U2 | Gloves/Boots HEAVY de grado D | NOT VERIFIED | Los sets D (Mithril, Brigandine) no declaran gloves/feet |
| U3 | Asignacion de armas S80/S84 por rol | NOT VERIFIED | Las armas existen (13.2: S80=111, S84=261 base segun filtro), pero no se propone ninguna por rol en este documento |
| U4 | Umbrales level -> grade para S80/S84 | PROPOSED | No hay mapeo nativo; nuestro `BotProfile.resolveGrade` llega solo hasta S (>=76) |
| U5 | Compatibilidad SIGIL vs escudo | INFERRED | Ambos usan `lhand` -> mutuamente excluyentes por bodypart; no validado en runtime |
| U6 | Efecto de `categoryType` (p.ej. DUALDAGGER "Dagger Master") al equipar | NOT VERIFIED | Existe en el datapack; no se evaluo aqui el bloqueo por clase |
| U7 | Inventario completo de condiciones no-raca (hero/olympiad/quest) sobre todo el catalogo | NOT VERIFIED | Solo se verifico que no hay condiciones de clase en las piezas candidatas |
| U8 | Variantes SA/elementales de armas | EXCLUIDO (documentado) | 3075 entradas con SA vs 919 base; el catalogo lista las base |
| U9 | Cloaks / belts / talismans / bracelets | EXCLUIDO (documentado) | 363 items fuera de alcance; podrian incorporarse en otra fase |
| U10 | Validacion en runtime (equipar y verificar penalizacion/inventario) | PENDIENTE | Este sprint es solo investigacion + documentacion; no se arranco el servidor |
| U11 | Atributos de combate adicionales (critRate, pAtkSpd, SA skills) | NO INCLUIDO | El catalogo registra pAtk/mAtk; el resto de stats esta en los XML |

SEARCHED-BUT-NOT-FOUND en este sprint: mapeo nativo level->grade; `classId` en condiciones de items de equipo; set HEAVY No-Grade; gloves/boots HEAVY de grado D.


## 20. Future Use for Bot Profiles

Este documento NO define perfiles. Solo entrega el material para definirlos en un sprint posterior (BOTAI-06-B ACT-2+).

### 20.1 Como usar el catalogo

1. Elegir rol (seccion 15) y grade objetivo.
2. Tomar los IDs EXCLUSIVAMENTE de las secciones 4-14 o 17 (nunca de memoria ni de otra version de L2J).
3. Validar el loadout contra las reglas de slot (seccion 16): `onepiece` XOR `legs`; `lrhand` XOR escudo `lhand`.
4. Jewelry: 1 `neck` + 2 `rear;lear` + 2 `rfinger;lfinger` (2 instancias del mismo ID).
5. Tank: arma `rhand` + escudo `lhand` (15.2 y 16.2).

### 20.2 Escalera de grades (PROPOSED)

| Grade | Rango de nivel | Origen |
|-------|----------------|--------|
| NONE | < 20 | Nuestro codigo (`BotProfile.resolveGrade`) - VERIFIED como codigo propio, NO como regla nativa |
| D | 20 - 39 | idem |
| C | 40 - 51 | idem |
| B | 52 - 60 | idem |
| A | 61 - 75 | idem |
| S | >= 76 | idem |
| S80 | 80 - 83 | PROPOSED (sin mapeo nativo verificado) |
| S84 | 84+ | PROPOSED (sin mapeo nativo verificado) |

NOTA: el datapack no contiene tabla nivel->grado. Los rangos son decisiones nuestras; el codigo actual solo llega a S.

### 20.3 Ejemplo de esqueleto (PROPOSED, NO implementado)

```text
PROFILE: CARDINAL (Bishop -> Cardinal, HEALER)
  LEVEL 1  : armor 1101 + 1104 + 44 | weapon 6   | jewelry 118 / 112 / 116
  LEVEL 40 : armor 439 + 471 + 2454 | weapon 206 | jewelry 119 / 852-855 / 883-886
  LEVEL 76 : armor 6383 + 6386 + 6384 + 6385 | weapon 6579 o 6366 | jewelry 920 / 858 / 889

PROFILE: PALADIN (TANK)
  LEVEL 76 : armor 6373 + 6374 + 6378 + 6375 + 6376 | weapon 6364 (rhand) | shield 6377 (lhand) | jewelry 920 / 858 / 889
```

Bloques ILUSTRATIVOS: no existen como codigo ni como perfil.

### 20.4 Advertencias para el sprint de perfiles

- No mezclar `onepiece` con `legs` (A5 / 16.2).
- Preferir IDs base: las variantes SA/elementales (A13) no estan catalogadas.
- No asumir grade por el nombre: Dynasty = S, Moirai = S80, Vesper/Vorpal/Elegia = S84 (A4 / 9-11).
- Limpieza de skills al cambiar de clase = OPEN RISK R1 en `BOTAI-06-B2_LOCAL_EQUIPMENT_SKILLS_VERIFICATION.md`.


## 21. Conclusion

| Bloque | Resultado |
|--------|-----------|
| ARMOR | 3825 entradas crudas; 1067 equipables no-quest con slot relevante + 814 fuera de alcance |
| WEAPONS | 3893 entradas crudas; 917 base catalogadas por grade y tipo, con slot real y pAtk/mAtk |
| JEWELRY | 261 piezas catalogadas + matriz por grade + boss/unicos |
| SETS | 217 bloques declarados en 21 archivos, con composicion real; 204 activos + 13 DISABLED/RESERVED |
| GRADES | NONE, D, C, B, A, S, S80, S84 (los ocho `crystal_type` presentes) |
| SLOTS | Documentados y contrastados con `Inventory.equipItem` (incompatibilidades) |
| ANOMALIAS | 15 documentadas (A1-A15); ninguna corregida |
| NO VERIFICADO | 11 puntos explicitos (U1-U11) |

### 21.1 Valor como Knowledge Base

- Fuente unica para "que equipo existe y con que ID" en el TARGET (equipment inventory).
- Los documentos de DECISION siguen siendo otros: `BOTAI-06-B2_LOCAL_EQUIPMENT_SKILLS_VERIFICATION.md`
  (matriz de loadout por rol + limpieza de skills) y `BOTAI-06-ACT1_CONSOLIDATION.md` (arquitectura BotProfile).
- Regenerable: si cambia el datapack, se reejecuta el generador y el catalogo se actualiza.

### 21.2 Cierre

**STATUS: GO** (catalogo completo generado desde el datapack real; sin cambios funcionales).

BOTAI-06-C - EQUIPMENT MASTER CATALOG COMPLETE

---

FIN DEL DOCUMENTO

