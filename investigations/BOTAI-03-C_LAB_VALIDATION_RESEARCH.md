# BOTAI-03-C — LAB VALIDATION RESEARCH

## 1. Resumen ejecutivo

Toda la infraestructura necesaria para ejecutar el laboratorio LOAD → PREPARE → SAVE → UNLOAD → RELOAD → VERIFY **existe y es verificable en el source actual**. El hallazgo más importante y que cierra el UNVERIFIED de BOTAI-03-B es el siguiente:

**`PlayerAutoSaveTaskManager` usa `isOnline()` (que devuelve `_isOnline` puro), NO `isOnlineInt()`.** Por lo tanto, un bot con `_client == null` **SÍ es guardado por autosave** siempre que `_isOnline == true`.

El segundo hallazgo decisivo es que **existe una ruta de unload limpia y probada en producción para Players sin cliente**: `player.storeMe(); player.deleteMe();` — exactamente el patrón que usa el propio Mobius en `GameClient.load()` cuando encuentra un Player con `getClient() == null`.

Veredicto: **GO** para diseñar la fase ACT. La visibilidad runtime clientless sigue siendo el único punto `RUNTIME REQUIRED`.

---

## 2. Estado de cada experimento

| Experimento | Estado |
| --- | --- |
| LOAD → SAVE → RELOAD | **VERIFIED** (source) |
| Visibilidad clientless | **RUNTIME REQUIRED** (parcialmente refutada por source) |
| isOnline / autosave | **VERIFIED** |
| getClient sweep | **VERIFIED** (bajo riesgo) |

---

## 3. Evidencia exacta

### EXPERIMENTO 3 — isOnline() y autosave (el más crítico, ahora cerrado)

```text
Archivo: Player.java
Método: isOnline()
Línea aprox.: 7905-7908
Comportamiento: return _isOnline;  (booleano puro, NO consulta _client)
Conclusión: VERIFIED
```

```text
Archivo: Player.java
Método: isOnlineInt()
Línea aprox.: 7910-7918
Comportamiento: si (_isOnline && _client != null) → 1 (o 2 si detached); si no → 0
Conclusión: con _client == null SIEMPRE devuelve 0
```

```text
Archivo: PlayerAutoSaveTaskManager.java
Método: run()
Línea aprox.: 71-76
Comportamiento: if ((player != null) && player.isOnline()) { player.autoSave(); ... }
                si isOnline()==false → iterator.remove() (se autoelimina del manager)
Conclusión: AUTOSAVE usa isOnline() (no isOnlineInt) → funciona con _client == null
```

Respuestas directas del EXPERIMENTO 3:
- **A.** `isOnline()` → `_isOnline` (boolean puro). VERIFIED.
- **B.** `isOnlineInt()` → 0/1/2 según `_isOnline` **y** `_client != null`. VERIFIED.
- **C.** `PlayerAutoSaveTaskManager` usa **`isOnline()`**. VERIFIED.
- **D.** `_client == null` + `_isOnline == true` → **SÍ se guarda por autosave**. VERIFIED.
- **E.** `_client == null` + `_isOnline == false` → autosave hace `iterator.remove()` y deja de guardar; el bot no persiste automáticamente. VERIFIED.
- **F.** Implicación BSBOT: tras `Player.load` + `setOnlineStatus(true, …)` el bot queda con `_isOnline == true` y entra al autosave (de hecho `restore()` ya lo añade con `PlayerAutoSaveTaskManager.getInstance().add(player)`).

```text
AUTOSAVE CLIENTLESS: VERIFIED
```


### EXPERIMENTO 1 — LOAD → SAVE → RELOAD

**A. LOAD** — `Player.load(objectId)` delega en `restore(objectId)`. Restaura inventario, warehouse, freight, skills, macros, shortcuts, henna, bookmarks, recipe book, premium items, pet inventory, reward skills, item reuse, CP/HP/MP, y termina en `setOnlineStatus(true, false)` + `PlayerAutoSaveTaskManager.add(player)`. **Nunca llama `setClient()`**, por lo que `_client` queda en su valor por defecto (`null`). VERIFIED.

**C. SAVE** — Jerarquía verificada:
- `storeMe()` → llama `store(true)`.
- `store(boolean storeActiveEffects)` → `storeCharBase()` + `storeCharSub()` + `storeEffect()` + `storeItemReuseDelay()` + SevenSigns + **`PlayerVariables.storeMe()`** + AccountVariables + inventario/warehouse/freight. Persiste nivel, clase, posición, XP/SP, HP/MP/CP, título, etc.
- `autoSave()` → `storeMe()` + `storeRecommendations(false)` + (opcional) update de inventario.

Recomendación de SAVE del laboratorio: **`player.store(true)`** (sincrónico, persiste todo incl. PlayerVariables inline). No depender del autosave asíncrono de PlayerVariables para la verificación. VERIFIED.

**D. UNLOAD (punto crítico) — RUTA REAL ENCONTRADA.** No hay que inventar nada. Para un Player con `_client == null`, el propio Mobius usa:
```java
player.storeMe();
player.deleteMe();
```
Esto está en `GameClient.load()` (rama `getClient() == null`) y en `LoginServerThread.doKickPlayer()` (rama `isDetached()` con player no nulo).

- `deleteMe()` (nivel `Creature`) detiene AI task, limpia efectos, quita de `CreatureSeeTaskManager`, para buff task y pone `worldRegion = null`. `Player` tiene su propio override de `deleteMe()` que además se encarga de party/limpieza.
- Alternativa de orden superior: `Disconnection.of(player).storeAndDeleteWith(LeaveWorld.STATIC_PACKET)` — usada por `OfflinePlayTable` en su rama de error, envuelve store+delete+world cleanup.

Para quitarlo del mundo visible: `decayMe()` (= `removeVisibleObject` + `removeObject`).

Secuencia de unload recomendada para el lab (reversible, sin residuos):
1. `AutoPlayTaskManager.getInstance().stopAutoPlay(bot)` y `AutoUseTaskManager.getInstance().stopAutoUseTask(bot)`.
2. Sacar de party si aplica (`bot.getParty().removePartyMember(...)`).
3. `bot.setOnlineStatus(false, false)` → provoca que el autosave se autoelimine en el próximo ciclo (`iterator.remove()`).
4. `PlayerAutoSaveTaskManager.getInstance().remove(bot)` (explícito, no esperar al ciclo).
5. `bot.storeMe()` (o `store(true)`).
6. `bot.deleteMe()` (limpia AI/efectos/region) — o `decayMe()` para solo sacar del World.

**E/F. RELOAD + VERIFY** — Nuevo `Player.load(268483130)` recarga desde DB. Comparar contra los valores de PREPARE.

### EXPERIMENTO 2 — Visibilidad clientless

`spawnMe()` existe y registra el objeto en el World/region, y `World.addVisibleObject` hace que los observadores reciban `wo.sendInfo(observador)` — es decir, la visibilidad hacia el humano depende del **knownlist del observador**, no del `_client` del bot.

PERO: `broadcastCharInfo()` hace early-return si `isOnlineInt() == 0`, que es exactamente el caso de un bot con `_client == null`. Por eso `OfflinePlayTable` **comenta** `// player.broadcastUserInfo();`.

Separación honesta:
- **Demostrado por source:** el spawn y el registro en World funcionan sin cliente; la aparición inicial se hace por `sendInfo` del observador; las actualizaciones que el propio bot emite vía `broadcastCharInfo` **NO** se propagan mientras `isOnlineInt()==0`; movimiento/ataque/status van por `broadcastPacket`, que NO chequea ese flag.
- **RUNTIME REQUIRED:** confirmar en ejecución que el humano ve al bot al acercarse, lo puede seleccionar y atacar, y que ve sus movimientos/combate. Esto NO puede afirmarse solo porque `spawnMe()` exista.

### EXPERIMENTO 4 — Sweep de getClient()

- En `Player.java` **no existe ni un solo `getClient().`** (dereferencia directa). El acceso a cliente pasa por `sendPacket()`, que es null-safe (`if (_client != null)`). VERIFIED.
- Call sites externos relevantes están **protegidos por null-check o por `isOnlineInt()==1`** (ej. `EnterWorld` hace `plr.getClient().getHardwareInfo()` solo dentro de `if (plr.isOnlineInt() == 1)`, que excluye bots). `GameClient.load` y `LoginServerThread` ya ramifican explícitamente por `getClient() == null`/`isDetached()`.

Clasificación:
- **Categoría A (seguro con null):** `sendPacket`, autosave, store, spawnMe/decayMe, deleteMe.
- **Categoría B (null-check):** `setPrivateStoreType` (`(_client == null) || _client.isDetached()`), `GameClient.load`, `doKickPlayer`.
- **Categoría C (potencialmente peligroso):** call sites server-side que dereferencien `getClient()` sin guard fuera de los flujos del lifecycle del bot. No se hizo barrido exhaustivo global → riesgo **bajo** por el precedente de offline auto players, pero **UNVERIFIED** al 100%.
- **Categoría D (no relevante):** todo lo disparado exclusivamente por packets de cliente (nunca ocurren para un bot).


---

## 4. Diseño del laboratorio (fase ACT, NO ejecutar ahora)

```text
PRECONDICIONES
- Servidor arrancado, BSBOT01 (268483130) NO logueado por nadie.
- BSBOT02..08 intactos, no tocar.
        ↓
BACKUP
- Export SQL de las filas de charId=268483130 en: characters, character_subclasses,
  character_skills, items (owner_id=268483130), character_variables.
- Registrar valores originales: classId, level, exp, sp, posición, lista de items+enchant.
        ↓
LOAD
- Player bot = Player.load(268483130);
- Asserts: getObjectId()==268483130, getClient()==null, isOnline()==true, isOnlineInt()==0.
- bot.spawnMe(x, y, z);
        ↓
PREPARE (mínimo y reversible)
- PlayerVariables marca: bot.getVariables().set("BOTAI_LAB_TEST", "OK"); storeMe();
- NO cambiar classId ni level salvo que sea estrictamente necesario.
        ↓
SAVE
- bot.store(true);   // persiste todo incl. PlayerVariables inline
        ↓
STOP RUNTIME
- AutoPlayTaskManager.getInstance().stopAutoPlay(bot);
- AutoUseTaskManager.getInstance().stopAutoUseTask(bot);
        ↓
UNLOAD
- si en party: removePartyMember(bot);
- bot.setOnlineStatus(false, false);
- PlayerAutoSaveTaskManager.getInstance().remove(bot);
- bot.storeMe();
- bot.deleteMe();            // patrón GameClient.load para _client==null
- Assert: World.getPlayer(268483130) == null
        ↓
RELOAD
- Player reloaded = Player.load(268483130);
        ↓
VERIFY
- getVariables().getString("BOTAI_LAB_TEST") == "OK"  → persistencia confirmada
- classId/level/exp/sp/posición == valores esperados
        ↓
ROLLBACK SI FALLA / al terminar
- Restaurar filas desde el BACKUP SQL.
- Eliminar la variable de prueba.
```

### Matriz VERIFY

| Dato | Verificar | Por qué |
| --- | --- | --- |
| charId | Sí | Identidad; debe ser 268483130 |
| objectId | Sí | Debe coincidir con charId al recargar |
| PlayerVariables | **Sí (clave)** | Marca reversible que prueba persistencia |
| classId | Opcional | Solo si PREPARE lo cambió |
| level / XP / SP | Opcional | Solo si PREPARE los cambió |
| posición | Sí | Confirma store/restore de coordenadas |
| HP/MP/CP | Recomendado | Confirma restore de vitals |

---

## 5. Matriz DEBE SER VERIFICADO

| Check | Fuente | Método | Estado |
| --- | --- | --- | --- |
| clientless (_client==null) | Player.restore | `load()` no llama `setClient` | VERIFIED |
| autosave | PlayerAutoSaveTaskManager | `run()` usa `isOnline()` | VERIFIED |
| persistence | Player | `store(true)`/`autoSave()` | VERIFIED |
| reload | Player | `Player.load(charId)` | VERIFIED |
| equipment | Inventory/Player | `addItem` / `equipItemAndRecord` | VERIFIED (API) |
| enchant | Item/Player | `setEnchantLevel` / `addItem(...enchant...)` | VERIFIED (API) |
| skills | Player | `rewardSkills`/`restoreSkills` | VERIFIED |
| visibility | World/Player | `spawnMe`+`sendInfo`; `broadcastCharInfo` early-return | RUNTIME REQUIRED |
| party | Party | `joinParty`/`removePartyMember` | VERIFIED (API) |
| AutoPlay | AutoPlayTaskManager | `startAutoPlay`/`stopAutoPlay` | VERIFIED |
| AutoUse | AutoUseTaskManager | `startAutoUseTask`/`stopAutoUseTask` | VERIFIED |
| unload limpio | GameClient/Creature | `storeMe()`+`deleteMe()` / `decayMe()` | VERIFIED |


---

## 6. Riesgos

| Riesgo | Nivel | Mitigación |
| --- | --- | --- |
| Pérdida de datos de BSBOT01 | **MEDIUM** | Backup SQL previo; PREPARE reversible (solo PlayerVariables) |
| Personaje duplicado en World | LOW | `GameClient.load` ya detecta duplicados; asegurar que nadie loguea BSBOT01 |
| Player residual en World tras unload | LOW | `deleteMe()`/`decayMe()` verifican `World.getPlayer()==null` |
| Autosave residual | LOW | `isOnline()==false` autoelimina + `remove()` explícito |
| Party residual | LOW | `removePartyMember` antes de unload |
| AutoPlay/AutoUse residual | LOW | `stopAutoPlay`/`stopAutoUseTask` explícitos |
| Clientless crash (getClient() sin guard) | LOW-MEDIUM | Sweep C UNVERIFIED; monitorizar logs durante ACT |
| Visibilidad incorrecta | **MEDIUM** | RUNTIME REQUIRED; es objetivo primario del ACT |

---

## 7. GO / NO-GO

```text
BOTAI-03-C LABORATORIO:
GO
```

Razones: los 3 experimentos de source (LOAD→SAVE→RELOAD, isOnline/autosave, getClient sweep) están **VERIFIED**. El unload limpio tiene ruta real y probada en producción. El único punto abierto (visibilidad clientless) es precisamente lo que la fase ACT debe medir en runtime — no bloquea el diseño ni la ejecución del laboratorio. GO significa que hay conocimiento suficiente para ejecutar el ACT con backup y rollback seguros, **no** que el laboratorio ya se haya ejecutado.

---

## 8. Recomendación siguiente — BOTAI-03-C-ACT

Estructura recomendada:
1. **Solo BSBOT01.** BSBOT02..08 congelados.
2. **Backup SQL obligatorio** antes de tocar nada.
3. **PREPARE mínimo:** una sola `PlayerVariables` de prueba (reversible), sin cambiar clase/nivel.
4. Ejecutar el flujo de la sección 4 con asserts en cada paso.
5. **Test de visibilidad separado:** con el humano cerca, confirmar aparición/selección/ataque del bot (cierra el único `RUNTIME REQUIRED`).
6. Registrar evidencia: valores antes/después, logs de excepciones (para cerrar el sweep C), captura de visibilidad.
7. **Rollback** al finalizar aunque el test pase (dejar BSBOT01 idéntico al estado original).

Investigación adicional que conviene cerrar en el ACT (no bloqueante): comportamiento exacto del override `Player.deleteMe()` (party/summon cleanup) y barrido runtime de Categoría C de `getClient()`.

---

## Clasificación de conocimiento (resumen)

| Conclusión | Epistemológico | Vigencia | Evidencia |
| --- | --- | --- | --- |
| `Player.load` deja `_client == null` | VERIFIED | CURRENT | SOURCE |
| autosave usa `isOnline()` y funciona clientless | VERIFIED | CURRENT | SOURCE |
| `store(true)` persiste todo incl. PlayerVariables | VERIFIED | CURRENT | SOURCE |
| unload limpio = `storeMe()`+`deleteMe()` | VERIFIED | CURRENT | SOURCE |
| visibilidad clientless funcional | EXPERIMENTAL | PENDING_REVERIFICATION | RUNTIME REQUIRED |
| getClient() Categoría C sin barrido total | UNVERIFIED | CURRENT | riesgo bajo |
| `TemporaryPlayer` | REFUTED (NOT_FOUND) | HISTORICAL | — |

---

## OfflinePlayTable vs futura BotSession (referencia, no duplicar)

`OfflinePlayTable.restoreOfflinePlayers()` es la plantilla de **lifecycle**: `Player.load` → `setOnlineStatus` → `spawnMe` → `setOfflinePlay(true)` → `setOnlineStatus(true,true)` → `restoreEffects`/`setRunning` → `startAutoPlay`/`startAutoUseTask`, y en error `Disconnection.of(player).storeAndDeleteWith(...)`. BotSession debe **reutilizar** ese lifecycle (no reimplementarlo); el provisioning (clase/equipo/enchant/skills) es una fase separada anterior al arranque del lifecycle. No modificar `OfflinePlayTable`.


---

```text
SPRINT: BOTAI-03-C
FASE: Laboratorio de validación de Provisioning, Lifecycle, Clientless y Persistencia
MODO: PLAN
ESTADO: INVESTIGACIÓN COMPLETADA

PRODUCCIÓN MODIFICADA: NO
UPSTREAM MODIFICADO: NO
L2J-RECIPE MODIFICADO: NO
DB MODIFICADA: NO
CÓDIGO CREADO: NO
LABORATORIO EJECUTADO: NO

EXPERIMENTOS ANALIZADOS:
1. LOAD → SAVE → RELOAD ......... VERIFIED (source)
2. VISIBILIDAD CLIENTLESS ....... RUNTIME REQUIRED
3. isOnline / AUTOSAVE .......... VERIFIED (autosave clientless = VERIFIED)
4. SWEEP getClient() ............ VERIFIED (riesgo bajo; Categoría C no barrida al 100%)

RESULTADO:
GO

SIGUIENTE FASE PROPUESTA:
BOTAI-03-C-ACT — Laboratorio BSBOT01 (solo 268483130, con backup SQL y rollback obligatorio;
test de visibilidad runtime como objetivo primario).

BOTAI-04:
NO INICIADO.
```

**DETENTE AQUÍ.** No se implementó nada, no se modificó ningún archivo, no se ejecutó el laboratorio.
