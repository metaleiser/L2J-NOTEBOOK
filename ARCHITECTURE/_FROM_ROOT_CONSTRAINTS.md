# CONSTRAINTS AND FAILURE GATES — Temporary Player / Clientless Archer

> **Propósito:** Catálogo de APIs seguras, inseguras y condicionalmente seguras.
> **Target:** Mobius CT 2.6 HighFive | **Confianza:** ALTA (source evidence)
> **Estado:** VERIFIED · **Evidence:** SOURCE · **Vigencia:** DUPLICATE (candidato) · **Authority:** knowledge/CONSTRAINTS.md

---

## 1. APIs PELIGROSAS (NO USAR — persisten en DB)

| API | Source:Line | DB Operación |
|-----|-------------|--------------|
| `Player.create()` | Player.java:1020 | INSERT characters |
| `Player.load()` | Player.java:7211 | SELECT characters |
| `Player.storeMe()` | Player.java:7637 | UPDATE characters |
| `Player.autoSave()` | Player.java:8627 | UPDATE characters + items |
| `Player.deleteMe()` | Player.java:11734 | UPDATE characters SET online |
| `Player.restoreSkills()` | Player.java:8171 | SELECT character_skills |
| `Player.storeSkills(List,int)` | Player.java:8140 | INSERT/UPDATE char_skills |
| `Player.addSkill(Skill, true)` | Player.java:8013 | INSERT/UPDATE char_skills |
| `Player.removeSkill(Skill)` (1 param) | Player.java:8048 | DELETE char_skills |
| `Player.setOnlineStatus(false,true)` | Player.java:7020 | UPDATE characters |
| `Player.createDb()` | Player.java:7062 | INSERT characters |
| `Disconnection.storeAndDelete()` | Disconnection.java:110 | storeMe + deleteMe |
| `Item.updateDatabase()` (sin flag) | Item.java:1481 | INSERT/UPDATE items |
| `Inventory.setPaperdollItem(slot,item)` | Inventory.java:1072 | updateDatabase() |
| `Inventory.equipItem(item)` | Inventory.java:1263 | updateDatabase() |
| `ItemContainer.addItem(...)` | ItemContainer.java:221 | updateDatabase() |

---

## 2. APIs seguras (RUNTIME)

| API | Source | Notas |
|-----|--------|-------|
| Ctor `Player(int,PlayerTemplate,String,PlayerAppearance)` | Player.java:895 | PRIVADO, factory mismo paquete |
| `Player.getStat().setLevel(byte)` | PlayerStat.java:506 | Clamps, recalcula stats |
| `Player.setBaseClass(PlayerClass)` | Player.java:1330 | Solo campo |
| `Player.addSkill(Skill, false)` | Player.java:8013 | store=false → runtime |
| `Player.removeSkill(Skill, false, boolean)` | Player.java:8034 | store=false → runtime |
| `Player.sendPacket(ServerPacket)` | Player.java:4429 | Guard _client!=null |
| `Player.getAccountName()` | Player.java:1044 | Fallback _accountName |
| `Player.getPlayerClass()` | Player.java:2415 | Deriva del template |
| `Player.spawnMe(x,y,z)` | heredado | → World.addObject |
| `Player.stopAllTasks()` | Player.java:15185 | Cancela tasks runtime |
| `Player.abortAttack/abortCast/stopMove` | Player.java:11796 | Runtime |
| `Creature.deleteMe()` | Creature.java:2805 | Runtime (AI stop, effects) |
| `Creature.broadcastPacket(pkt)` | Creature.java:615 | Protegido sendPacket |
| `World.addObject(object)` | World.java:175 | Runtime |
| `IdManager.getNextId()` / `releaseId()` | IdManager.java:108/158 | BitSet puro |
| `PlayerAppearance(byte,byte,byte,boolean)` | Appear.java:51 | Público |
| `PlayerTemplateData.getTemplate(PlayerClass)` | PTD.java:189 | Runtime |
| `SkillTreeData.getAllAvailableSkills(...)` | SkillTree.java:602 | Runtime |
| `Party.addPartyMember/removePartyMember` | Party.java:280/401 | Packets protegidos |
| `PlayerAI.setIntentionAttack/MoveTo/Follow` | PlayerAI.java | Runtime |
| `PlayerAutoSaveTaskManager.remove(Player)` | AutoSaveTM.java:91 | Seguro defensivo |

---

## 3. APIs condicionalmente seguras

| API | Condición | Riesgo | Mitigación |
|-----|-----------|--------|------------|
| `new Item(...)` | Sin _temporary | INSERT DB | Marcar `_temporary=true` |
| `setPaperdollItem(slot, item)` | Item sin flag | UPDATE/INSERT | Usar items _temporary |
| `reduceArrowCount(boolean)` | Flechas sin flag | DB por disparo | Flechas _temporary |
| `distributeItem(player,item)` | Bot es looter | Items persistentes | Excluir bot de loot |
| `Creature.onDecay()` | Bot muerto | storeAndDeleteWith | Cancelar decay task |
| `Shutdown.disconnectAllChars()` | Bot en World | storeAndDeleteWith | despawnAll() antes |
| `setPlayerClass(int)` | Llamado por error | Efectos clan/no deseados | No llamar |

---

## 4. Persistence hazards

| # | Hazard | Ruta | Severidad |
|---|--------|------|-----------|
| H1 | INSERT characters | `Player.create()→createDb()` | ALTA |
| H2 | UPDATE characters | `storeMe()/deleteMe()/autoSave()` | ALTA |
| H3 | INSERT items | `Item.updateDatabase()` sin flag | ALTA |
| H4 | UPDATE items | `setPaperdollItem/equipItem` | ALTA |
| H5 | INSERT char_skills | `addSkill(skill,true)` | ALTA |
| H6 | UPDATE items flechas | `reduceArrowCount→changeCount` | MEDIA |
| H7 | Death+decay persistence | `onDecay()` persist | ALTA |
| H8 | Shutdown persistence | `disconnectAllCharacters()` | ALTA |
| H9 | Owner disconnect persistence | `Disconnection storeAndDelete` | MEDIA |

---

## 5. Failure Gates (STOP conditions)

| Gate | Condición | Veredicto |
|------|-----------|-----------|
| 1 | Player creation requiere DB | **FALSO** — ctor :895 no persiste |
| 2 | Equipment no existe sin DB | **FALSO CON COND.** — flag _temporary lo permite |
| 3 | PlayerAI requiere GameClient | **FALSO** — PlayerAI.java:341-413 no usa client |
| 4 | World requiere GameClient | **FALSO** — World.java:175-202 no requiere |
| 5 | Despawn no puede remover Player | **FALSO** — Creature.deleteMe+World+releaseId suficiente |