# CONCEPTS — Forks de bots: ideas, NO codigo (REFERENCIA_EXTERNA)

> **Estado:** INFERRED · **Evidence:** EXTERNAL · **Vigencia:** HISTORICAL
> Fuentes SOLO README (no se leyo/copio su Java). Todo lo de abajo es HIPOTESIS hasta verificar en 43ac8878f5.

## C1. L2jRoboto (aCis 372, WIP, no live)

- Fuente: `others/investigacion features y forks/L2jRoboto-master/README.md`.
- Problema: fake players que actuen casi como players reales.
- Como: entidad FakePlayer + `FakePlayerAI` extensible (prioridad ofensiva/defensiva/heal/support), equipamiento por clase, enchant con chance del server.
- Ventajas: roles por clase; comandos `//fakes //takecontrol //releasecontrol //spawnrandom //deletefake //spawnenchanter`; teleport a villa al morir.
- Limitaciones: OTRO core (aCis, no Mobius); WIP; su FakePlayer NO es nuestro Player (ver CL-0004).
- Comprobar en Mobius: FakePlayerInfo.java:38; PlayerAI 341-413; si takecontrol tiene equivalente (NO copiar su AI).

## C2. L2Autobots (aCis 382, Kotlin)

- Fuente: `others/investigacion features y forks/L2Autobots-master/README.md`.
- Problema: control total in-game de autobots.
- Como: dashboard `//a b` (spawn/despawn/delete, inventario/status/skills/buffs, radio target, agresion PvP, pociones, buff/consumible infinito, filtro); bots persistentes en DB (login donde desloguearon); comportamiento por clase (archer kitea, spoiler spoilea, pet owners con pets); creacion in-game (nombre/nivel/clase/apariencia/random); chat/clan control; retorno a zona de muerte con gatekeeper.
- Ventajas: mejor lista de features-objetivo (roles, dashboard, persistencia opcional, retorno muerte).
- Limitaciones: Kotlin + aCis; persistente (nosotros queremos TEMPORAL primero); buff infinito/pociones = economia distinta.
- Comprobar: CommunityBoard dashboard (0002) como alternativa a `//a b`; PlayerVariables (CL-0012) vs tabla bots; gatekeeper/teleport nativo; AutoPlay por clase.

## C3. l2-smartbot (C4 Scions, net.sf.l2j, GPLv3)

- Fuente: `others/investigacion features y forks/l2-smartbot-main/README.md` (presets ARCHER/MAGE/HEALER/BUFFER/DAGGER/TANK).
- Problema: helpers PvE + experimentos AI NPC + automatizacion local.
- Como: presets por rol (HEALER cura low-HP + resurrect; BUFFER detecta buffs faltantes fighter/mage; TANK aggro 28/18; ARCHER stun 101; MAGE nukes 1239/1230/1235/1220/1177); follow/assist/attack/stop; `party invite support`; comandos grupales `all`; chat Ollama opcional (gemma3, fallback corto); knowledge file-based `[tags] fact`.
- Ventajas: mapa directo a nuestro objetivo (TANK/HEALER/BUFFER/DPS/DPS); heuristica healer/buffer reutilizable como IDEA; skill IDs C4 como referencia de CLASE de skill (NO copiar IDs a H5).
- Limitaciones: core C4 antiguo (L2PcInstance/L2World/L2Party/SkillTable/CharTemplateTable); FakePlayer instances; IDs H5 distintos; Ollama opcional.
- Comprobar: Party nativa (Party.java:88, Player.java:6862); healer: seleccion low-HP + cast + resurrect en AutoPlay/PlayerAI; buffer: deteccion buffs + SkillTreeData + addSkill(false); follow/assist nativo.

## Cadena obligatoria

`REFERENCIA_EXTERNA -> HIPOTESIS -> VERIFICACION EN 43ac8878f5 -> EXPERIMENTO -> RESULTADO -> CLAIM`. La fuente externa nunca supera al source.
