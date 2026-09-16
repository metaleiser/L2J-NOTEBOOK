# Community Board — Conocimiento Canonico Mobius H5

> **Estado:** VERIFIED · **Evidence:** SOURCE · **Vigencia:** CURRENT · **Baseline:** L2J_Mobius_CT_2.6_HighFive
> **Fuente de verdad:** E:\L2J MOBIUS IA\UPSTREAM\L2J_Mobius\L2J_Mobius_CT_2.6_HighFive
> **Alcance:** Buffer, Teleport/Gatekeeper, Merchant + arquitectura completa de los 9 boards nativos.
> **No normativo:** game y forks (solo referencia de ideas/personalizaciones del usuario).


---

## A. Arquitectura general

El Community Board es un subsistema **data-driven** de cuatro capas:

```
Jugador --bypass/packet--> CORE (despacho + config)
                               |
                               v
                          SCRIPTS (boards: logica por bypass)
                               |
                               v
                     CONFIG (CommunityBoard.ini) --> CommunityBoardConfig (jar, arranque)
                               |
                               v
                   HTML (plantillas) <-- HtmCache (jar)     DATA (multisell XML / buylist / skills / DB)
```

- **CORE (GameServer.jar, no editable sin rebuild):** CommunityBoardHandler, IParseBoardHandler, IWriteBoardHandler, HtmCache, HtmlUtil, CommunityBoardConfig, ConfigLoader, ShowBoard.
- **SCRIPTS (data/scripts/handlers/bypass/communityboard/*.java):** 9 boards. Se recompilan en runtime con //reload handler / //reload quest. No requieren rebuild del jar.
- **CONFIG (config/Custom/CommunityBoard.ini):** leida una vez en arranque por CommunityBoardConfig.load() (ConfigLoader.java:108). Recargable en caliente con //reload config.
- **HTML (data/html/CommunityBoard/**):** servidas via HtmCache. Recargables en caliente con //reload html [ruta].
- **DATA:** multisell XML (data/multisell/custom/<id>.xml), buylist 0000423 (hardcodeada), skills (data/stats/skills), tablas DB (bbs_favorites, clanhall).


Regla de capas:** los datos/config/HTML se editan fuera del codigo; los scripts se recompilan en runtime; el core solo se toca cuando la extension mediante capas superiores no baste.


## B. Flujo cliente -> packet -> handler -> board -> efecto

1. **Cliente** envia bypass (-h _bbs...) -> packet RequestBypassToServer.
2. RequestBypassToServer.java:55-67: _bbs esta en _possibleNonHtmlCommands -> NO pasa player.validateHtmlAction() (no requiere HTML previo). Flood protector canUseServerBypass() si aplica (linea 120).
3. Lineas 131-133: si CommunityBoardHandler.isCommunityBoardCommand() -> handleParseCommand().
4. CommunityBoardHandler.handleParseCommand() (core): gate GeneralConfig.ENABLE_COMMUNITY_BOARD; getHandler(cmd) resuelve por startsWith sobre getCommandList() (lineas 55-70).
5. IParseBoardHandler.onCommand(command, player) (script): checks de CommunityBoardConfig (combat/karma/peace) -> HtmCache.getHtm(...) -> reemplaza solo %navigation% -> CommunityBoardHandler.separateAndSend().
6. separateAndSend -> HtmlUtil.sendCBHtml() -> paquetes ShowBoard de hasta 16250 chars (101/102/103).


**Write path:** RequestBBSwrite.java (packet separado: url + arg1..5) -> CommunityBoardHandler.handleWriteCommand() -> switch(url) hardcodeado en core {Topic->_bbstop, Post->_bbspos(TODO), Region->_bbsloc, Notice->_bbsclan} -> exige instanceof IWriteBoardHandler -> writeCommunityBoardCommand(arg1..5).


## C. IParseBoardHandler

- interface (jar): boolean onCommand(String command, Player player) + String[] getCommandList().
- Despacho usa startsWith (case-insensitive). Parametros viajan en la linea; cada board parsea manualmente (split ";", ",").
- Registro: CommunityBoardHandler.registerHandler() indexa cada comando en minusculas -> invocado por MasterHandler.java.


## D. IWriteBoardHandler

- interface extends IParseBoardHandler (jar): boolean writeCommunityBoardCommand(Player, String arg1..arg5).
- Boards que lo implementan: ClanBoard (notice funcional), RegionBoard, MailBoard, MemoBoard (write = TODO).
- Extension limitada: el switch(url) vive en el core (4 casos fijos).


## E. CommunityBoardHandler (core)

- Map<String,IParseBoardHandler> _datatable + Map<Integer,String> _bypasses (ultimo bypass por player).
- getHandler(cmd): startsWith sobre todos (55-70).
- handleParseCommand: gate ENABLE_COMMUNITY_BOARD -> cb.onCommand() (102-122).
- handleWriteCommand: gate -> switch(url) -> instanceof IWriteBoardHandler -> writeCommunityBoardCommand() (135-192).
- addBypass/removeBypass: guardan title+& bypass por objectId.
- separateAndSend: split 16250 chars -> HtmlUtil.sendCBHtml (221-224).


## F. MasterHandler

- dist/game/data/scripts/handlers/MasterHandler.java: registra todos los handlers (import 66-74; lista 483-492).
- Script recompilable con //reload handler (solo este archivo) o //reload quest (todos). Hot reload de boards sin restart.
- Orden 483-492: ClanBoard, DropSearchBoard, FavoriteBoard, FriendsBoard, HomeBoard, HomepageBoard, MailBoard, MemoBoard, RegionBoard.


## G. HomeBoard (IParseBoardHandler) -- board central

**Comandos:** _bbshome, _bbstop, _bbspremium, _bbsexcmultisell, _bbsmultisell, _bbssell, _bbsteleport, _bbsbuff, _bbsheal, _bbsdelevel (custom condicionados por flags INI).


**Validaciones (HomeBoard.java:91-137):** COMBAT_CHECK sobre custom (casting/duel/olympiad/siege/PvPzone/pvpflag/alikeDead/onEvent/inStoreMode); KARMA_CHECK; PEACE_ONLY.


**Bypasses (147-330):** _bbstop;<ruta>.html solo sirve si endsWith(".html") (sin sanitizacion). _bbsteleport;Nombre (196-213): cobro -> lookup COMMUNITY_AVAILABLE_TELEPORTS -> disableAllSkills -> cierra CB -> teleToLocation -> re-enable 3s; no encontrado = silencioso. _bbsbuff;...;pagina (214-258): cobro ANTES de aplicar; SkillData.getSkill(id,lvl); whitelist COMMUNITY_AVAILABLE_BUFFS (skip silencioso); targets player+summon; applyEffects; MagicSkillUse opcional. _bbsheal (259-284): full HP/MP/CP. _bbsdelevel (285-307): -1 nivel via ExperienceData. _bbspremium (308-330): 1-30 dias. _bbsmultisell/_bbsexcmultisell/_bbssell (168-195): multisell + buylist 423 hardcodeada.


## H. DropSearchBoard (IParseBoardHandler)

_bbs_search_item, _bbs_search_drop, _bbs_npc_trace. Sin checks de combate. DROP_INDEX_CACHE desde NpcData; bloquea Adena; paginado. _bbs_npc_trace;<npcId> -> SpawnTable -> player.getRadar().addMarker(). HTML: Custom/dropsearch/main.html.


## I. ClanBoard (IWriteBoardHandler)

_bbsclan + sub. Requiere clan nivel>=2 para home; isClanLeader() para notice. HTML en codigo. Write: guarda clan notice.


## J. RegionBoard (IWriteBoardHandler)

_bbsloc, _bbsloc;id. CastleManager + SQL clanhall. HTML region_list/region_show (placeholders). Write=TODO.


## K. MailBoard (IWriteBoardHandler) -- _maillist -> mail.html. Write=TODO.
## L. MemoBoard (IWriteBoardHandler) -- _bbsmemo, _bbstopics -> memo.html. Write=TODO.
## M. FriendsBoard (IParseBoardHandler) -- _friendlist, _friendblocklist.
## N. HomepageBoard (IParseBoardHandler) -- _bbslink -> homepage.html.
## O. FavoriteBoard (IParseBoardHandler)

_bbsgetfav, bbs_add_fav (removeBypass), _bbsdelfav_<id>. HTML favorite.html+_list.html. Datos: tabla bbs_favorites.


## P. RequestBypassToServer -- _bbs non-html (55-67), sin validateHtmlAction, flood (120) -> handleParseCommand (131-133).
## Q. RequestBBSwrite -- packet separado (url+arg1..5) -> handleWriteCommand (55). Ni validateHtmlAction ni flood.
## R. ShowBoard / HtmlUtil -- sendCBHtml parte en 16250 chars.
## S. HtmCache -- HTM_CACHE (General.ini); precarga o lazy (ambos cachean tras 1a lectura). //reload html recarga en caliente (AdminReload 311-335). Solo .htm/.html, elimina comentarios/tabs. Localizacion por player.getHtmlPrefix(). Aviso no-ASCII. Debug GMDebugHtmlPaths. Init GameServer:325.
## T. ScriptEngine/ScriptExecutor/ScriptManager -- executeScriptList compila .java (MasterHandler constante). //reload handler recompila MasterHandler; //reload quest todo.
## U. ConfigLoader/CommunityBoardConfig/ConfigReader -- ConfigLoader.init() llama CommunityBoardConfig.load() (108), recargable via //reload config. 17 props; Properties.load (nativa continuacion \); defaults en codigo (warning si falta); lista malformada = excepcion en arranque (sin try por entrada).
## V. AdminReload (scripts) -- recargas en caliente: //reload handler (285-298), //reload quest (430-452), //reload config (ConfigLoader.init, 167-172), //reload html (HtmCache.reload, 311-335), //reload multisell (394-399), //reload skill...
## W. HTML CommunityBoard

Stock: home, homepage, mail, memo, favorite(+_list), friends_list, friends_block_list, region(+_list, region_clanhall_list, show).
Custom: home, navigation, buffer/main, devel/{main,ask,complete}, dropsearch/main, gatekeeper/main, premium/{main,thankyou}.
Validos reales: cualquier .htm(l) cacheado. Solo %navigation% (HomeBoard) + los de Favorite/Region/Clan se reemplazan. Otro %placeholder% se muestra literal.


## X. CommunityBoard.ini -- 17 claves: CustomCommunityBoard, CommunityCurrencyId(57), CommunityEnable{ Multisells,Teleports,Buffs,Heal,Delevel }, Community{ Teleport,Buff,Heal,Delevel }Price(0), CommunityBoardPeaceOnly, CommunityCombatDisabled, CommunityKarmaDisabled, CommunityCastAnimations, CommunityPremium{ System,BuyCoinId,PricePerDay }, CommunityAvailableBuffs (CSV), CommunityTeleportList (Nombre,X,Y,Z; multilinea con \).


## Y. Multisell -- data/multisell/custom/<id>.xml. <npc>-1</npc>=CB. Upstream incluye 600010/12/14/16/24. 600025 NO existe upstream (fork).
## Z. Buylist 423 -- hardcode HomeBoard:192. Cambiar requiere Java.
## AA. bbs_favorites -- CRUD FavoriteBoard.
## AB. clanhall -- RegionBoard query.
## AC. Skills -- data/stats/skills; whitelist INI.
## AD. Restricciones -- combat/karma/peace (HomeBoard custom).
## AE. Flood -- parse no write.
## AF. Multilenguaje -- player.getHtmlPrefix().
## AG. Lifecycle -- init config; HtmCache; scripts MasterHandler.
## AH. Reload matriz -- html/config/multisell: //reload. Scripts: //reload handler/quest. Core: rebuild+restart.
## AI. Puntos extension -- pagina nueva (0 Java); mas datos (0 Java); nuevo board (+MasterHandler+reload); nuevo write URL (core); nuevo placeholder (Java).
## AJ. Limitaciones -- buylist 423 fijo; sin target editable; precio global; sin paginacion native; _bbstop sin sanitizar.
## AK. Hardcodeados -- 423; REGIONS; startsWith; 5 args write; 16250; 3s re-enable.
## AL. Seguridad -- _bbs* sin validateHtmlAction; _bbstop sin sanitizar; flood no aplica a write.
## AM. Dependencias -- SkillData, MultisellData, BuyListData, ExperienceData, PremiumManager, PcCafePointsManager, ClanTable, CastleManager, SpawnTable, NpcData, DatabaseFactory, HtmlUtil, HtmCache, ConfigReader, player.getHtmlPrefix().
---

## Capacidad: preguntas practicas

- **Solo HTML?** -> nueva pagina (_bbstop), favoritos, friends, homepage, region, clanview, dropsearch. SI.
- **INI?** -> mas buffs, mas teleports, precios, moneda, flags, premium. SI.
- **XML/data?** -> mas multisells, skills. SI (cambiar buylist 423 NO, es hardcode).
- **Java (script)?** -> nuevo board con logica, nuevos placeholders HTML. SI (recompilable runtime).
- **Java (core)?** -> nuevo write URL, sanitizar _bbstop, target editable, precios variables, paginacion buffer. SI.
- **Handler:** HomeBoard (buff/tp/multisell/heal/delevel/premium/navegacion); DropSearch; Clan/Region/Mail/Friends/Homepage/Favorite.
- **Bypass:** _bbsbuff;skill,lvl;...;pag , _bbsteleport;Nombre , _bbsmultisell;id,pag , _bbstop;pag.html , _bbsheal , _bbsdelevel , _bbspremium;dias , _bbs_search_* , _bbsloc , _bbsclan* , _maillist , _bbsmemo , _friend* , _bbslink , _bbsgetfav/bbs_add_fav/_bbsdelfav_.
- **Config que controlar:** ver seccion X.
- **Datos:** skills, multisell XML, buylist 423, bbs_favorites, clanhall, teleports (INI).
- **Restricciones:** combat/karma/peace (HomeBoard), flood (parse), sin validateHtmlAction (todo _bbs).
- **Validar:** skill exista + en whitelist; id multisell exista como XML; nombre teleport == clave INI; bypass en getCommandList; HTML recargado con //reload html; INI con //reload config.
- **Recargar:** //reload html | //reload config | //reload multisell | //reload handler / //reload quest.
- **Revertir:** restaurar archivo + reload correspondiente (o restart).