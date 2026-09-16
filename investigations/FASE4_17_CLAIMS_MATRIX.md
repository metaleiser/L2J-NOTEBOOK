# FASE 4.17 — Matriz de afirmaciones

| Afirmación | Clasificación | Evidencia |
|---|---|---|
| Player puede funcionar server-side | VERIFIED | OfflinePlayTable carga+spawnea+autoplay |
| Player.load deja `_client == null` | VERIFIED | load termina sin setClient |
| setOnlineStatus no toca `_client` | VERIFIED | solo cambia `_isOnline` |
| OfflinePlay corre con null client | VERIFIED | restoreOfflinePlayers + guard AutoPlay |
| Party acepta bots | VERIFIED | joinParty sobre players sin cliente |
| CommandChannel acepta bots | VERIFIED | Collection<Party>, líder Player |
| AutoPlay/AutoUse reutilizables | VERIFIED | startAutoPlay/startAutoUseTask |
| FakePlayer sirve como base | FALSE/CORRECTED | es NPC (AttackableAI) |
| TemporaryPlayer existe | FALSE/CORRECTED | 0 coincidencias en el source |
| GameClient detached fabricable sin socket | FALSE/CORRECTED | solo ctor con Connection |
| Visibilidad inicial por knownlist | PROBABLE | precedente offline shops |
| Visibilidad dinámica del bot | UNVERIFIED | requiere runtime |
| Barrido completo getClient() server-side | UNVERIFIED | no auditado línea a línea |
| Requisitos entry por-boss | UNVERIFIED | scripts no leídos |
| 18 bots viables | PROBABLE | pools de 200; cuello broadcast/Geo |
