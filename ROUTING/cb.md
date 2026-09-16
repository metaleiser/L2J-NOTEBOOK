# ROUTING — Community Board (canonico, VERIFIED-SOURCE)

> Canon: knowledge/COMMUNITY_BOARD.md (4 capas). No modificar aun.

| Necesito | Donde mirar |
|---|---|
| Nueva pagina HTML | data/html/CommunityBoard/** + HtmCache + //reload html |
| Mas buffs/teleports/precios | config/Custom/CommunityBoard.ini (17 claves) + //reload config |
| Mas multisells/skills | data/multisell/custom/*.xml + data/stats/skills |
| Nuevo board con logica | data/scripts/handlers/bypass/communityboard/*.java + MasterHandler + //reload handler |
| Bypass/comandos | _bbsbuff _bbsteleport _bbsmultisell _bbstop _bbsheal _bbsdelevel _bbspremium _bbs_search_* + Favorite/Region/Clan/Mail/Friends |
| Restricciones | combat/karma/peace (HomeBoard), flood parse, sin validateHtmlAction |
| NO posible sin core | write URL nueva, buylist 423 (hardcode HomeBoard:192), target editable, precios variables |
