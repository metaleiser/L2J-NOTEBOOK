# Modelo Recipe + ejemplos Community Board

> **Estado:** VERIFIED · **Vigencia:** CURRENT · **Authority:** Modelo canónico de recetas
> **Proposito:** plantilla reproducible y reversible para futuras recetas.

---

## Modelo de Recipe (campos obligatorios)

- Necesidad: que se quiere lograr (una frase).
- Mecanismo nativo: que pieza de Mobius lo habilita (REGLA 1).
- Prerrequisitos: flags INI, tablas, permisos.
- Archivos afectados: rutas concretas.
- Cambios: contenido exacto anadir/modificar.
- Reload/restart: nivel de recarga necesario (REGLA 10).
- Validacion: como comprobar que funciono.
- Rollback: como volver al estado anterior.
- Evidencia: archivo:clase:metodo del upstream que lo respalda.
- Riesgos / OPEN QUESTIONS.

---

## Modelo Hacer / Deshacer (transformacion reversible)

ESTADO INICIAL -> CAMBIO -> VALIDACION -> ESTADO FINAL
ESTADO FINAL  -> ROLLBACK -> ESTADO INICIAL

Toda receta debe poder trazarse en ambos sentidos.

---

## Ejemplo 1 -- Anadir un teleport nativo

- Necesidad: nuevo destino de teletransporte en el Community Board.
- Mecanismo nativo: CommunityTeleportList (INI) + _bbsteleport;Nombre (HomeBoard).
- Archivo: config/Custom/CommunityBoard.ini.
- Cambio: anadir linea NombreDestino,X,Y,Z; (mismo formato multilinea). El Nombre debe ser unico y coincidir exactamente con el texto del boton HTML.
- HTML (opcional): anadir <button value="NombreDestino" action="bypass _bbsteleport;NombreDestino" ...> en Custom/gatekeeper/*.html.
- Reload: //reload config (INI) + //reload html (si se toco HTML).
- Validacion: abrir CB -> click -> aparece en el mapa; verificar que el Nombre este en COMMUNITY_AVAILABLE_TELEPORTS.
- Rollback: eliminar la linea de la INI + reload config; eliminar boton del HTML + reload html.
- Evidencia: HomeBoard.java:196-213; CommunityBoardConfig.java:75-82.
- Riesgos: nombre duplicado o clave con comas rompe el split (crash en carga). Solo X,Y,Z numericos tras el nombre.

## Ejemplo 2 -- Anadir un buff permitido

- Necesidad: permitir un nuevo skill de buff en el buffer.
- Mecanismo nativo: CommunityAvailableBuffs (INI) + _bbsbuff (HomeBoard).
- Prerrequisito: el skill debe existir en data/stats/skills y (si nivel>100) tener enchantGroup.
- Archivo: config/Custom/CommunityBoard.ini, clave CommunityAvailableBuffs.
- Cambio: anadir ,skillId al final de la lista CSV.
- HTML (opcional): anadir <button action="bypass _bbsbuff;skillId,nivel;pagina" ...> en Custom/buffer/*.html.
- Reload: //reload config.
- Validacion: abrir buffer -> el skill aplica sin "skill null" en log.
- Rollback: quitar el id de la lista + reload config; quitar boton + reload html.
- Evidencia: HomeBoard.java:214-258; CommunityBoardConfig.java:73-74.
- Riesgos: skill no en whitelist = skip silencioso. Nivel invalido = excepcion en getSkill.

## Ejemplo 3 -- Anadir pagina HTML al CB

- Necesidad: nueva seccion/pagina en el Community Board.
- Mecanismo nativo: _bbstop;<ruta>.html (HomeBoard dispatcher).
- Archivos: data/html/CommunityBoard/Custom/<ruta>.html + boton que la invoque (p.ej. Custom/navigation.html).
- Cambio: crear HTML (incluir %navigation%) + <button action="bypass _bbstop;<ruta>.html"> .
- Reload: //reload html.
- Validacion: click -> pagina se muestra.
- Rollback: borrar HTML + boton + reload html.
- Evidencia: HomeBoard.java:159-167; HtmCache.java.
- Riesgos: sin sanitizacion de ruta (no usar .. ni nombres que choquen con HTMLs existentes); placeholders distintos de %navigation% se muestran literal.

## Ejemplo 4 -- Anadir un multisell al Merchant

- Necesidad: nueva categoria de compra en el merchant.
- Mecanismo nativo: _bbsmultisell;id,pagina (HomeBoard) + data/multisell/custom/<id>.xml.
- Archivos: nuevo data/multisell/custom/<id>.xml + boton en data/html/CommunityBoard/Custom/merchant/main.html.
- Cambio: XML con <npc>-1</npc> + ingredientes/producciones; boton <button value="..." action="bypass _bbsmultisell;<id>,merchant/main"> .
- Reload: //reload multisell.
- Validacion: abrir merchant -> boton -> multisell abre -> compra funciona.
- Rollback: borrar XML + boton + reload multisell.
- Evidencia: HomeBoard.java:168-185; multisell XSD data/xsd/multisell.xsd.
- Riesgos: XML no parsea -> multisell ignorada; id duplicado -> conflicto.

---

## Nota

Estos 4 ejemplos son derivaciones directas de mecanismos nativos ya demostrados. No son features nuevas. Cada receta futura debe ajustarse al modelo y citar evidencia del Notebook.
