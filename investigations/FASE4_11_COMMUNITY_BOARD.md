# FASE 4.11 — Community Board como panel de control

## Estado: VERIFIED (extensible por registro)

## Dos sistemas coexisten en HighFive
1. Moderno por handlers: `handler/CommunityBoardHandler.java`
   - `registerHandler(IParseBoardHandler)` indexa `getCommandList()`.
   - `getHandler(cmd)` hace match por prefijo (startsWith).
   - `handleParseCommand(command, player)`: valida player!=null y
     `GeneralConfig.ENABLE_COMMUNITY_BOARD`, luego `cb.onCommand(command, player)`.
   - `separateAndSend(html, player)` → `HtmlUtil.sendCBHtml(player, html)`.
2. Legacy: paquete `communitybbs/` (BaseBBSManager, foros).

## Flujo de bypass
CLIENT → `network/clientpackets/RequestBypassToServer` (routing `_bbs`/`bbs`) →
CommunityBoardHandler → handler concreto.

## Cómo agregar el BOT MANAGER (propuesta)
Implementar `IParseBoardHandler` (p.ej. `BotBoard`) con comandos `_bbsbot...` y
registrarlo con `CommunityBoardHandler.getInstance().registerHandler(new BotBoard())`
al startup. HTML vía `separateAndSend`. NO editar CommunityBoardHandler.java.

## PENDIENTE / OBSERVACIÓN
Los handlers de board CONCRETOS no aparecen en este source. Confirmar en runtime si
el board activo es el de handlers o el legacy communitybbs.

## Confianza: ALTA (infra) / MEDIA (board activo por defecto)
