# Reglas de decision del Notebook

> **Estado:** VERIFIED · **Vigencia:** CURRENT · **Authority:** Reglas canónicas del Notebook
> **Ambito:** toda modificacion/estudio de subsistemas Mobius.

---

**REGLA 1 -- Usar primero capacidades nativas.**
Si Mobius ya proporciona el mecanismo, usarlo. No reinventar.

**REGLA 2 -- No implementar Java si HTML/config/XML resuelven la necesidad.**
Preferencia: HTML (0 codigo) > INI > XML/DB > Script Java > Core Java.

**REGLA 3 -- Si la capacidad no existe, buscar primero un punto de extension nativo.**
Handlers en scripts, bypasses existentes, placeholders, Data classes extensibles.

**REGLA 4 -- Solo tocar core cuando la extension mediante scripts/config/data no sea suficiente.**
Tocar GameServer.jar = rebuild + restart = ultimo recurso. Requiere justificacion concreta.

**REGLA 5 -- Los forks son fuente de ideas, no codigo autorizado para copiar.**
Idea del fork -> reimplementar con el patron nativo de Mobius. No copiar Java de fork.

**REGLA 6 -- Las personalizaciones historicas del usuario son evidencia de necesidades reales, no definicion de como funciona Mobius.**
game muestra QUE se quiso lograr, no COMO funciona el upstream.

**REGLA 7 -- Toda modificacion debe poder explicarse mediante el conocimiento del Notebook.**
Si no se puede explicar con el Notebook, falta investigar.

**REGLA 8 -- Toda Recipe nueva debe indicar: mecanismo nativo usado, archivos afectados, validacion, rollback.**
Ver RECIPES_COMMUNITY_BOARD.md para el modelo.

**REGLA 9 -- Completitud por capacidad, no por volumen.**
Una investigacion es completa cuando permite entender, modificar, validar y revertir el componente; no cuando leyo N archivos.

**REGLA 10 -- Jerarquia de recarga.**
//reload html < //reload config < //reload multisell|skill < //reload handler|quest < rebuild+restart. Siempre preferir el nivel mas bajo que alcance el cambio.

**REGLA 11 -- Evidencia directa sobre inferencia.**
Toda afirmacion tecnica importante debe citar archivo:clase:metodo (y lineas cuando sea posible). Las inferencias se marcan como tales.
