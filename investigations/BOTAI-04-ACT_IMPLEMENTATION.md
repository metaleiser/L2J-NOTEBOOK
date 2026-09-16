
# BOTAI-04 ACT — Implementación BotPreset + BotProvisioning (BSBOT01 / DPS_FIGHTER)

> **SPRINT:** BOTAI-04
> **FASE:** BotProvisioning MVP — implementación controlada
> **MODO:** ACT
> **Estado:** IMPLEMENTADO — PENDIENTE VALIDACIÓN RUNTIME · **Vigencia:** CURRENT
> **Fecha:** 2026-09-12
> **Diseño base:** BOTAI-04_PROVISIONING_DESIGN.md
> **Integración:** AdminBotManager.summonBot (línea ~190)

---

## 1. Archivos creados

| Archivo | SHA-256 |
|---------|---------|
| `game/data/scripts/handlers/chat/commands/admin/BotPreset.java` | `685a99fe7744ff48e83086af3a32dbadf38ac8606b0b759f9188d36bbb142653` |
| `game/data/scripts/handlers/chat/commands/admin/BotProvisioning.java` | `f123f3b04bc5d98c4d916e3b75be98e5ba3762c1ebf0d09cbed62889b30335f3` |

## 2. Archivo modificado (UNO, integración mínima)

| Archivo | SHA-256 |
|---------|---------|
| `game/data/scripts/handlers/chat/commands/admin/AdminBotManager.java` | `17b6c8e9161295ed6c5b7479267c09c529578f0d2d40ed8cb8f880dc97d0667e` |

Diff conceptual (+5 líneas, solo BSBOT01, tras spawnMe y antes de setOnlineStatus/AutoPlay):

```java
// BOTAI-04: preparar persistentemente SOLO BSBOT01 antes de AutoPlay/AutoUse.
if (botId == 268483130)
{
    BotProvisioning.apply(bot, BotPreset.createDpsFighter());
}
```

No se refactorizó AdminBotManager. No se modificaron BotSession.java ni ningún otro archivo.

## 3. Punto exacto de integración

`AdminBotManager.summonBot`: después de `bot.spawnMe(gmX + dx, gmY + dy, gmZ)` y
antes de `startAutoPlay`/`startAutoUseTask` (verificado en CHECKPOINT 1).
BotSession.java no participa en load/spawn (solo estado), por lo que la llamada vive
en el flujo summon — mínimo acoplamiento, una sola línea de llamada + guardia BSBOT01.

## 4. Preset DPS_FIGHTER final (todos los IDs verificados contra game/data/stats/items)

classId=2 (Gladiator), level=80, SkillMode=AUTO_BY_CLASS, version=1, enchant fijo +4.

| Item | ID | Tipo | Slot | Enchant |
|------|----|------|------|---------|
| Sword of Revolution | 129 | Weapon/SWORD | rhand | +4 |
| Dark Crystal Breastplate | 365 | Armor | chest | +4 |
| Dark Crystal Gaiters | 388 | Armor | legs | +4 |
| Dark Crystal Helmet | 512 | Armor | head | +4 |
| Dark Crystal Boots | 563 | Armor | feet | +4 |
| Dark Crystal Gloves | 2472 | Armor | gloves | +4 |
| Majestic Earring | 862 | Armor | rear;lear | +4 |
| Majestic Ring | 893 | Armor | rfinger;lfinger | +4 |
| Majestic Necklace | 924 | Armor | neck | +4 |

## 5. Idempotencia

`PlayerVariables` clave `BOT_PROVISION_VERSION` (int). Flujo: leer marker →
si coincide con la versión del preset, SKIP sin tocar nada (ni `addItem`, que no es
idempotente); si no, provisionar → solo tras éxito total escribir marker →
`vars.saveNow()` → `bot.store(true)`.

## 6. Timing setPlayerClass (verificado)

`rewardSkills()` corre síncrono dentro de `setPlayerClass`; el schedule de 100ms solo
toca `applyItemSkills()` + `sendSkillList()` (null-safe para bot clientless). No hay
`Thread.sleep()` en ningún archivo. Tras `setPlayerClass` + `setLevel`/`setExp`, el
paso explícito `giveAvailableSkills(false, true, false)` (idempotente) refuerza las
skills de la clase/nivel final.

## 7. Compilación

`javac 25 -cp libs/GameServer.jar` sobre los 2 archivos nuevos + AdminBotManager:
**JAVAC_EXIT_CODE=0** (6 clases generadas). Sin warnings ni errores relacionados.

## 8. Alcance confirmado

- Core modificado: NO
- UPSTREAM modificado: NO
- DB modificada: NO
- BSBOT02–08 modificados: NO
- Restart automático: NO (servidor no iniciado ni reiniciado)
- Roles/Healer/Tank/Buffer/Community Board: NO incluidos

## 9. Runtime pendiente (manual, por GM)

Primera invocación `//bot 1`: verificar clase/nivel/skills/arma/armadura/joyería/
enchant/equipado en BSBOT01; desinvocar. Segunda invocación: mismo estado, sin items
duplicados, provisioning omitido por versión. BSBOT01 DB: PENDING_REVERIFICATION.

---

**VEREDICTO: BOTAI-04 MVP IMPLEMENTADO — PENDIENTE VALIDACIÓN RUNTIME**
