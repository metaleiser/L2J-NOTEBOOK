# Metodo permanente de investigacion nativa de Mobius

> **Estado:** VERIFIED · **Vigencia:** CURRENT · **Authority:** Metodología canónica de investigación
> **No es** un procedimiento de un solo componente: es la regla operativa del Notebook.
> **Origen:** derivado del estudio end-to-end del Community Board (FASE 1A + 1B).

---

## 1. Principio fundamental

**NO modificamos Mobius porque podamos modificarlo. Lo modificamos solamente cuando la funcionalidad que queremos no existe nativa, o existe una razon concreta para cambiar su comportamiento.**

Jerarquia obligatoria ante cualquier necesidad:
`NECESIDAD -> BUSCAR MECANISMO EXISTENTE -> IDENTIFICAR PLANTILLA/CONFIG/DATA -> MODIFICAR -> VALIDAR -> SOLO SI NO EXISTE, PROGRAMAR`

Preferencia de implementacion (de menor a mayor costo/riesgo):
1. HTML/plantilla (0 codigo, recargable en caliente).
2. Config/INI (0 codigo, recargable).
3. Datos XML/DB (0 codigo, recargable).
4. Script Java (`data\scripts`, recompilable en runtime).
5. Core Java (rebuild + restart, ultimo recurso).

## 2. Metodologia (24 pasos, generalizable)

Aplicar **siempre** en este orden para cualquier subsistema (Player Clientless, FakePlayers, AI, Bots, Quests, ...):

1. **Identificar source of truth:** rama/commit concreto del upstream local. No usar forks ni web como fuente tecnica.
2. **Delimitar componente:** nombre, responsabilidad, scope (que si / que NO se estudia).
3. **Localizar arquitectura:** framework en jar + scripts en `data\scripts` + entry points.
4. **Localizar interfaces/handlers:** `I*XxxHandler`, clases abstractas, registration point.
5. **Seguir flujo end-to-end:** cliente -> packet -> handler -> logica -> HTML/config/data -> efecto.
6. **Localizar configuracion:** archivos INI, loader (ConfigReader/Properties), defaults, hot-reload.
7. **Localizar HTML/templates:** carpetas, HtmCache, placeholders, localizacion.
8. **Localizar datos:** XML, buylists, multisells, skills.
9. **Localizar DB:** tablas, queries, esquema.
10. **Localizar packets/bypass:** clientpackets, serverpackets, registro de comandos.
11. **Localizar lifecycle/startup:** `GameServer`, `ConfigLoader`, `ScriptEngine`, orden de carga.
12. **Localizar reload/hot reload:** admin commands (`AdminReload`), condiciones de recarga en caliente vs restart.
13. **Identificar validaciones:** gates, checks, permisos.
14. **Identificar restricciones:** estados prohibidos, dependencias de estado.
15. **Identificar dependencias:** otros managers, Data classes, Config.
16. **Identificar puntos de extension:** donde enchufar codigo sin tocar core.
17. **Identificar que puede hacerse sin HTML/config/data.**
18. **Identificar que requiere Java** (script vs core).
19. **Identificar que requiere core** (rebuild+restart).
20. **Identificar como validar** cada cambio.
21. **Identificar como revertir** cada cambio.
22. **Registrar OPEN QUESTIONS** (no inventar respuestas).
23. **Comprobar cobertura** (checklist por area: completa/evidencia/pendiente).
24. **Convertir conocimiento validado en Recipes** cuando corresponda.

## 3. Herramientas de busqueda sistematica

Select-string recursivo sobre `java\` y `dist\` con terminos del componente (handler, config, packet, bypass, etc.) y clasificacion de resultados en: **A. Core/framework · B. Handlers/boards · C. HTML/templates · D. Config · E. Data · F. Packets/bypass · G. Startup/loading · H. Dependencies.** Excluir falsos positivos.

## 4. Criterio de completitud

Una investigacion **no** se considera completa por cantidad de archivos estudiados, sino cuando el conocimiento permite **entender, modificar, validar y revertir** razonablemente el componente. Cierre = checklist de cobertura con toda area en COMPLETA salvo OPEN QUESTIONS documentadas.

## 5. Separacion de fuentes (obligatoria)

- **Fuente de verdad tecnica:** upstream local jamas el fork.
- **Forks:** solo como fuente de ideas; no se copia su Java como solucion.
- **Personalizaciones historicas del usuario:** evidencia de necesidades reales, NO definicion de como funciona Mobius.
- Si una solucion existe nativa, registrarla siempre como primera opcion.

## 6. Salida al Notebook

- Conocimiento canonico -> `knowledge/<DOMINIO>.md` (estado VERIFIED-SOURCE, baseline).
- Reglas permanentes -> `knowledge/MOBIUS_INVESTIGATION_METHOD.md` + `knowledge/DECISION_RULES.md`.
- Recetas reproducibles -> `knowledge/RECIPES_<DOMINIO>.md`.
- Preguntas abiertas -> `OPEN_QUESTIONS.md` (seccion por componente).
- Trazabilidad -> INDEX.md actualizado.

## 7. Reutilizacion obligatoria

Antes de iniciar CUALQUIER estudio futuro, el agente debe:
1. Leer `MOBIUS_INVESTIGATION_METHOD.md` y `DECISION_RULES.md`.
2. Aplicar los 24 pasos.
3. No saltar pasos aunque el componente parezca simple.
4. Pedir confirmacion al GM si detecta necesidad de tocar core.
