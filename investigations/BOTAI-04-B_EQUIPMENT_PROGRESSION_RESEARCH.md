# BOTAI-04-B — EQUIPMENT PROGRESSION RESEARCH

> **SPRINT:** BOTAI-04-B
> **FASE:** Runtime Validation
> **MODO:** PLAN
> **Estado:** INVESTIGACIÓN COMPLETADA
> **Fecha:** 2026-09-12

---

## 1. Problema observado

El preset actual DPS_FIGHTER implementado en BotPreset.createDpsFighter() **no representa correctamente el equipamiento esperado para un Gladiator de nivel alto**.

### 1.1 Arma incorrecta
- **Actual:** Sword of Revolution (ID 129) — Grado **D**, una mano (bodypart hand), weapon_type=SWORD.
- **Problema:** Un Gladiator DPS usa **Dual Swords** (weapon_type=DUAL, bodypart lrhand). Un arma grado D de una mano no representa un Gladiador de nivel 80.

### 1.2 Armadura incorrecta / incompleta (no forma un SET real)
- **Actual:** Mezcla IDs que no corresponden a un set completo:
  - 365 Dark Crystal Breastplate (A) ✓
  - 388 Dark Crystal Gaiters (A) ✓
  - 512 Dark Crystal Helmet (A) ✓
  - 563 Dark Crystal Boots (A) ✗ **INCORRECTO** — el set Dark Crystal Heavy id=39 usa 5777
  - 2472 Dark Crystal Gloves (A) ✗ **INCORRECTO** — el set Dark Crystal Heavy id=39 usa 5765
- **Consecuencia:** Gloves y boots no activan el set bonus completo.

### 1.3 Joyería incompleta
- **Actual:** Solo 1 Earring (862), 1 Ring (893), 1 Necklace (924).
- **Problema:** Faltan 1 Earring y 1 Ring (slots: REAR+LEAR para earrings, RFINGER+LFINGER para rings).

---

## 2. Reglas verificadas de grade

### 2.1 crystal_type → Grade (verificado en AdminSearch.java:150-188)
| crystal_type | Grade |
|--------------|-------|
| NONE | NG |
| D | D |
| C | C |
| B | B |
| A | A |
| S | S |
| S80 | S80 |
| S84 | S84 |

### 2.2 Mapeo nivel → grade (INFERRED / PENDING_VERIFICATION)
20 → D, 40 → C, 52 → B, 61 → A, 76 → S
**NOTA:** NO hay API nativa getGradeForLevel. Este mapeo es inferido y debe implementarse como dato de configuración propio.

---

## 3. Sets de armadura verificados (game/data/stats/armorsets)

**A-Grade (a_grade.xml):**
- Set id=39 Dark Crystal Heavy: chest=365, legs=388, head=512, **gloves=5765**, **feet=5777**, shield=641, skill=3530, enchant6skill=3620
- Set id=40 Tallum Heavy: chest=2382, head=547, gloves=5768, feet=5780

**S-Grade (s_grade.xml):**
- Set id=56 Imperial Crusader Heavy: chest=6373, legs=6374, head=6378, gloves=6375, feet=6376, shield=6377
- Set id=57 Draconic Leather: chest=6379, head=6382, gloves=6380, feet=6381
- Set id=58 Major Arcana Robe: chest=6383, head=6386, gloves=6384, feet=6385

---

## 4. Gladiator: arma Dual Sword

### 4.1 A-Grade Dual Swords verificados (05700-05799.xml)
- 5704 Keshanberk*Keshanberk — DUAL, A, pAtk=259, enchant_enabled=true
- 5705 Keshanberk*Damascus — DUAL, A, pAtk=275, enchant_enabled=true
- 5706 Damascus*Damascus — DUAL, A, pAtk=282, enchant_enabled=true

### 4.2 S-Grade Dual Swords
**NOT_FOUND** — no se verificaron IDs en datapack. Requiere investigación adicional.

---

## 5. Joyería

### 5.1 Slots (Inventory.java:82-104)
- 2 earrings (REAR + LEAR)
- 2 rings (RFINGER + LFINGER)
- 1 necklace (NECK)

### 5.2 Tateossian S-Grade (verificados en 06700-06799.xml)
- 6724 Sealed Tateossian Earring — Sealed (requiere craft)
- 6725 Sealed Tateossian Ring — Sealed (requiere craft)
- 6726 Sealed Tateossian Necklace — Sealed (requiere craft)

**NOTA:** Los IDs actuales del preset (862/893/924) son Majestic A-Grade. Para S-Grade se necesitan los IDs correctos (PENDING).

---

## 6. Propuesta de arquitectura

BotLoadout interface con resolve(classId, level) que determine:
- gradeForLevel: 76→S, 61→A, 52→B, 40→C, <40→D
- weaponTypePerClass: Gladiator→DUAL
- armorSetPerGrade: A→Dark Crystal Heavy, S→Imperial Crusader Heavy
- jewelryPerGrade: 2 earrings + 2 rings + necklace
- enchantBase: 4 (PENDING: verificar máximo por grade)

---

## 7. Riesgos

| Riesgo | Severidad | Mitigación |
|--------|-----------|------------|
| Dual Sword S no existen | ALTA | Usar A-Grade (5706) hasta verificar S |
| Jewelry S usable NOT_FOUND | ALTA | Buscar IDs correctos antes de implementar |
| Slots dobles earring/ring | MEDIA | Test runtime |
| OVER_ENCHANT_PROTECTION aplica a bots | MEDIA | Verificar config |
| Mapeo nivel→grade inferido | BAJO | Dato de configuración propio |

---

## 8. ¿Qué NO está verificado

1. **Dual Sword S-Grade**: NOT_FOUND
2. **Jewelry Tateossian S usable**: NOT_FOUND
3. **1 earring = 2 slots vs 2 items**: UNVERIFIED
4. **OVER_ENCHANT_PROTECTION valor**: PENDING
5. **Enchant máximo por grade**: PENDING
6. **Weapon type por cada clase**: PENDING (solo Gladiator→DUAL asumido)

---

## 9. Recomendación

**NO implementar todavía.**

Cerrar PENDIENTES bloqueantes:
1. Buscar Dual Sword S-Grade (o confirmar que no existen)
2. Buscar joyería Tateossian S usable
3. Verificar OVER_ENCHANT_PROTECTION y getMaxEnchant
4. Confirmar slots dobles earrings/rings en runtime

Después implementar BotLoadoutResolver y reemplazar createDpsFighter() por esolve(classId, level).

---

## 10. Reporte final

SPRINT: BOTAI-04-B
FASE: Runtime Validation
MODO: PLAN
ESTADO: INVESTIGACIÓN COMPLETADA

DOCUMENTO:
C:\L2J MOBIUS IA\L2J Notebook\investigations\BOTAI-04-B_EQUIPMENT_PROGRESSION_RESEARCH.md

PRODUCCIÓN MODIFICADA: NO
CÓDIGO CREADO: NO

ESTADO FINAL: BOTAI-04-B INVESTIGADO Y CERRADO.

---
**FIN DEL DOCUMENTO**

