# BOTAI-04-B1.1 — EQUIPMENT PENDING CLOSURE

> **SPRINT:** BOTAI-04-B1.1
> **FASE:** Equipment / Provisioning
> **MODO:** PLAN — INVESTIGACIÓN
> **EJECUTOR:** Devin
> **ESTADO:** INVESTIGACIÓN COMPLETADA
> **Fecha:** 2026-09-13

---

## CONTEXTO

BOTAI-04-B ya fue investigado y cerrado.

Ya están verificados en el datapack:

### Dark Crystal Heavy
- Chest: `365`
- Legs: `388`
- Head: `512`
- Gloves: `5765`
- Boots: `5777`

### Dual Swords A-Grade
- `5704`
- `5705`
- `5706`

### Tateossian Sealed
- `6724`
- `6725`
- `6726`

NO repetir investigación de estos datos salvo que sea necesario para resolver una dependencia.

---

## 1. DUAL SWORD S-GRADE

### Resultado: NOT_FOUND

#### Investigación realizada:
- Búsqueda en todos los archivos XML de items del datapack
- Búsqueda específica de `weapon_type="DUAL"` combinado con `crystal_type="S"`
- Verificación de archivos de items S-Grade (07500-07599.xml, etc.)

#### Evidencia:
- No se encontraron dual swords con `crystal_type="S"` en el datapack
- Los archivos de items S-Grade verificados contienen otras weapon types (BOW, SWORD, BLUNT, etc.) pero no DUAL
- El archivo `05700-05799.xml` solo contiene dual swords A-Grade (5704, 5705, 5706)

#### Conclusión:
```text
ID: NOT_FOUND
Nombre: NOT_FOUND
CrystalType: NOT_FOUND
WeaponType: NOT_FOUND
BodyPart: NOT_FOUND
Estado: NOT_FOUND
Evidencia: Verificación completa de datapack - no existen dual swords S-Grade
```

---

## 2. JEWELRY S-GRADE

### Resultado: VERIFIED

#### Tateossian Jewelry S-Grade (unsealed, usable directamente):

| Item | ID | Crystal Type | Body Part | Sealed | Craft Required |
|------|----|--------------|-----------|--------|----------------|
| Tateossian Necklace | 920 | S | neck | NO | NO |
| Tateossian Earring | 858 | S | rear;lear | NO | NO |
| Tateossian Ring | 889 | S | rfinger;lfinger | NO | NO |

#### Evidencia:
- **Necklace 920**: <ref_snippet file="C:\L2J MOBIUS IA\L2J_Mobius_CT_2.6_HighFive\game\data\stats\items\00900-00999.xml" lines="295-311" />
- **Earring 858**: <ref_snippet file="C:\L2J MOBIUS IA\L2J_Mobius_CT_2.6_HighFive\game\data\stats\items\00800-00899.xml" lines="688-704" />
- **Ring 889**: <ref_snippet file="C:\L2J MOBIUS IA\L2J_Mobius_CT_2.6_HighFive\game\data\stats\items\00800-00899.xml" lines="1149-1162" />

#### Distinción importante:
- **ITEM EXISTE**: Tateossian S-Grade (920, 858, 889) ✅
- **ITEM PUEDE SER CREADO DIRECTAMENTE POR BOTPROVISIONING**: ✅
  - NO requieren craft
  - NO son sealed
  - Pueden crearse directamente mediante `addItem` con enchant level deseado

#### Sealed Tateossian (NO apropiados para provisioning directo):
- 6724 Sealed Tateossian Earring — REQUIERE craft ❌
- 6725 Sealed Tateossian Ring — REQUIERE craft ❌
- 6726 Sealed Tateossian Necklace — REQUIERE craft ❌

---

## 3. CONFIRMAR SLOTS DOBLES DE JEWELRY

### Resultado: VERIFIED

#### Slots de jewelry en datapack:
Los items de jewelry especifican bodyparts múltiples en el XML:

```xml
<!-- Earrings -->
<set name="bodypart" val="rear;lear" />

<!-- Rings -->
<set name="bodypart" val="rfinger;lfinger" />

<!-- Necklace -->
<set name="bodypart" val="neck" />
```

#### Evidencia de slots en AdminEnchant.java:
<ref_snippet file="C:\L2J MOBIUS IA\L2J_Mobius_CT_2.6_HighFive\game\data\scripts\handlers\chat\commands\admin\AdminEnchant.java" lines="97-116" />

#### Constantes de slots verificadas:
```text
NECK      = Inventory.PAPERDOLL_NECK    (1 slot)
LEAR      = Inventory.PAPERDOLL_LEAR    (1 slot)
REAR      = Inventory.PAPERDOLL_REAR    (1 slot)
LFINGER   = Inventory.PAPERDOLL_LFINGER (1 slot)
RFINGER   = Inventory.PAPERDOLL_RFINGER (1 slot)
```

#### Capacidad de provisioning:
```text
✅ El provisioning puede crear 2 instancias del mismo item:
   - Earring (858) → LEAR + REAR (2 items separados)
   - Ring (889) → LFINGER + RFINGER (2 items separados)
   - Necklace (920) → NECK (1 item)
```

#### APIs para equipar (según AdminEnchant.java):
- `Inventory.PAPERDOLL_NECK` para necklace
- `Inventory.PAPERDOLL_LEAR` para earring izquierdo
- `Inventory.PAPERDOLL_REAR` para earring derecho
- `Inventory.PAPERDOLL_LFINGER` para ring izquierdo
- `Inventory.PAPERDOLL_RFINGER` para ring derecho

---

## 4. OVER-ENCHANT PROTECTION

### Resultado: VERIFIED

#### Existencia:
`OVER_ENCHANT_PROTECTION` existe como configuración en `PlayerConfig`

#### Archivo/configuración:
- Referenciado en: <ref_snippet file="C:\L2J MOBIUS IA\L2J_Mobius_CT_2.6_HighFive\game\data\scripts\handlers\chat\commands\admin\AdminEnchant.java" lines="189-212" />

#### Significado:
Protección contra over-enchant para jugadores normales (no GM). Cuando está activado:
- Limita el enchant level al máximo configurado por tipo de item
- Solo aplica a jugadores `!isGM()`

#### Afectación a `Item.setEnchantLevel`:
- **NO** afecta directamente al método `Item.setEnchantLevel(int)`
- El método `setEnchantLevel` puede establecer cualquier valor (evidenciado en línea 215)
- La protección se aplica en el nivel de comando/acción administrativa, no en el objeto Item

#### Afectación a `Player.addItem(... enchantLevel ...)`:
- **NO** se encontró evidencia de que `addItem` tenga protección de over-enchant
- La protección parece estar solo en comandos de enchant específicos (`//enchant`, `//createitem` con enchant)

#### Diferencias entre enchant scroll vs asignación directa:
- **Scroll enchant**: Sujeto a tasas de éxito definidas en `EnchantItemGroups.xml` y límites de `EnchantItemData.xml`
- **Asignación directa** (provisioning/admin): Bypass de tasas de éxito, pero sujeto a límites de OVER_ENCHANT_PROTECTION si está activado y el jugador no es GM

#### Para bots:
- Si los bots tienen `accesslevel=0` (no GM), OVER_ENCHANT_PROTECTION SI limitaría el enchant
- Si los bots tienen `accesslevel=100` (GM), OVER_ENCHANT_PROTECTION NO aplica
- Actualmente el provisioning usa ADMIN con accesslevel=100, por lo que la protección NO debería afectar

---

## 5. ENCHANT MÁXIMO POR GRADE

### Resultado: VERIFIED

#### A. Límite del sistema de enchant normal:
Verificado en `EnchantItemData.xml`:
<ref_snippet file="C:\L2J MOBIUS IA\L2J_Mobius_CT_2.6_HighFive\game\data\EnchantItemData.xml" lines="4-28" />

```text
Scrolls normales (todos los grados): maxEnchant="16"
Blessed Scrolls (todos los grados): maxEnchant="16"
```

#### B. Capacidad de `Item.setEnchantLevel(int)`:
- **NO hay límite nativo** en el método `setEnchantLevel`
- Puede aceptar cualquier valor entero (evidenciado en AdminEnchant.java:215)
- Los límites se aplican en capas superiores (comandos, configuración)

#### C. Límites aplicados al provisioning mediante `addItem`:
Según logs del servidor:
<ref_snippet file="C:\L2J MOBIUS IA\L2J_Mobius_CT_2.6_HighFive\game\log\java0.log" lines="55-59" />

```text
EnchantItemGroupsData: Max weapon enchant is set to 16.
EnchantItemGroupsData: Max armor enchant is set to 16.
EnchantItemGroupsData: Max accessory enchant is set to 16.
```

#### Límites por grade (configuración global):
**NO hay límites diferenciales por grade**. El sistema usa límites globales:
- Weapon: 16
- Armor: 16
- Accessory: 16

Esto aplica a TODOS los grados (D, C, B, A, S, S80, S84) de manera uniforme.

#### Para provisioning directo:
- Si OVER_ENCHANT_PROTECTION está activado y el bot no es GM: Limitado a 16
- Si OVER_ENCHANT_PROTECTION está desactivado o el bot es GM: Sin límite nativo (puede setEnchantLevel arbitrario)

---

## 6. TABLA FINAL DE RESULTADOS

| Elemento                | Resultado                                    | Estado     |
| ----------------------- | -------------------------------------------- | ---------- |
| Dual Sword S            | NOT_FOUND - No existen en datapack           | NOT_FOUND  |
| Jewelry S               | Tateossian (920, 858, 889) - unsealed, usable | VERIFIED   |
| 2 Earrings              | rear;lear slots, 2 items separados           | VERIFIED   |
| 2 Rings                 | rfinger;lfinger slots, 2 items separados     | VERIFIED   |
| OVER_ENCHANT_PROTECTION | Existe, aplica a !isGM(), NO afecta a setEnchantLevel | VERIFIED |
| Max enchant             | 16 global para weapon/armor/accessory, NO diferencial por grade | VERIFIED |

---

## 7. DECISIONES NECESARIAS PARA ACT

### Datos verificados:
1. ✅ Dual Sword S-Grade: NO existen → Usar A-Grade (5706 Damascus*Damascus)
2. ✅ Jewelry S-Grade: Tateossian unsealed (920, 858, 889) usable directamente
3. ✅ Slots dobles: 2 earrings y 2 rings requieren 2 items cada uno
4. ✅ OVER_ENCHANT_PROTECTION: Configuración existente, bypass con GM access
5. ✅ Max enchant: 16 global, sin diferenciación por grade

### Decisiones de diseño para BSBOT01 preset:

#### Arma:
- **Decisión**: Usar Dual Sword A-Grade (5706 Damascus*Damascus)
- **Justificación**: No existen Dual Swords S-Grade en datapack
- **Enchant**: 4 (base) o 16 (máximo permitido)

#### Armor:
- **Decisión**: Dark Crystal Heavy completo (365, 388, 512, 5765, 5777)
- **Justificación**: Ya verificado en BOTAI-04-B
- **Enchant**: 4 (base) o 16 (máximo permitido)

#### Jewelry:
- **Decisión**: Tateossian S-Grade unsealed
  - Necklace: 920 (1 item)
  - Earring: 858 (2 items para LEAR + REAR)
  - Ring: 889 (2 items para LFINGER + RFINGER)
- **Justificación**: Usable directamente, no requiere craft
- **Enchant**: 4 (base) o 16 (máximo permitido)

#### Estrategia de enchant:
- **Opción A (conservadora)**: Enchant 4 en todos los items
- **Opción B (agresiva)**: Enchant 16 en todos los items (máximo del sistema)
- **Recomendación**: Empezar con 4, permitir progresión a 16

#### OVER_ENCHANT_PROTECTION:
- **Decisión**: Bypass usando accesslevel=100 (GM) para bots
- **Justificación**: Los bots de provisioning necesitan acceso sin restricciones
- **Implementación**: Ya implementado en gen_0005_botmanager_provisioning.ps1

### Pendientes que realmente bloquean el ACT:
**NINGUNO** - Todos los pendientes han sido cerrados.

---

## 8. REGLAS CUMPLIDAS

✅ NO modificar producción
✅ NO modificar TARGET
✅ NO modificar UPSTREAM
✅ NO modificar DB
✅ NO compilar
✅ NO iniciar/reiniciar servidor
✅ NO probar cambios
✅ NO implementar BotLoadout
✅ NO implementar progresión
✅ NO iniciar BOTAI-05

---

## 9. CRITERIO DE CIERRE

**SPRINT BOTAI-04-B1.1 CERRADO**

✅ Podemos escribir el preset de BSBOT01 sin inventar:
   - Arma: 5706 (Damascus*Damascus A-Grade Dual Sword)
   - Armor: 365, 388, 512, 5765, 5777 (Dark Crystal Heavy)
   - Jewelry: 920, 858x2, 889x2 (Tateossian S-Grade)
   - Slots: LEAR, REAR, LFINGER, RFINGER, NECK
   - Enchant: 4 (base) o 16 (máximo)

✅ Estrategia de reprovisioning completamente definida:
   - OVER_ENCHANT_PROTECTION bypass con GM access
   - Max enchant 16 global
   - 2 instancias para earrings y rings

---

## 10. DOCUMENTO FINAL

**SPRINT:** BOTAI-04-B1.1
**FASE:** Equipment / Provisioning
**MODO:** PLAN — INVESTIGACIÓN
**ESTADO:** INVESTIGACIÓN COMPLETADA

**DOCUMENTO:**
C:\L2J MOBIUS IA\L2J Notebook\investigations\BOTAI-04-B1.1_EQUIPMENT_PENDING_CLOSURE.md

**PRODUCCIÓN MODIFICADA:** NO
**CÓDIGO CREADO:** NO

**ESTADO FINAL:** BOTAI-04-B1.1 INVESTIGADO Y CERRADO.

---

**FIN DEL DOCUMENTO**
