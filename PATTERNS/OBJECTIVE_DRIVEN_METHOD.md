# Metodo dirigido por objetivos (canonico FASE 1)

> **Estado:** VERIFIED · **Vigencia:** CURRENT · **Authority:** Guía corta del método de investigación
﻿# Metodo dirigido por objetivos (canonico FASE 1)

> Sintesis: `knowledge/MOBIUS_INVESTIGATION_METHOD.md` (24 pasos) + `knowledge/DECISION_RULES.md` (11 reglas) + `PATTERNS/_FROM_EXP00_BOOT_METHOD.md` (GM INTENT->RECIPE). No los sustituye: es la guia corta.

## Cadena

```
OBJETIVO (ej. bot healer cura al party)
  -> que sistema controla? (ROUTING/bots.md o cb.md)
  -> punto de entrada (packet/handler/bypass)
  -> seguir flujo (cliente->packet->handler->logica->HTML/config/data->efecto)
  -> clases relevantes (max 5-10, con file:line)
  -> config/DB si aplica
  -> patrones existentes (no reinventar)
  -> depende de GameClient? (auditar, no asumir)
  -> experimento minimo (1 spike, 1 pregunta)
  -> documentar (CLAIMS atómico + SOURCE_MAP)
  -> implementar (RECIPE, datapack-only primero)
  -> compilar -> probar -> documentar resultado
```

## Jerarquia de implementacion (menor costo primero)

`HTML (0 codigo) > INI > XML/DB > Script Java (runtime reload) > Core Java (rebuild+restart, ultimo recurso)`

## Separacion de fuentes

`1.Source real 43ac8878f5 > 2.Comportamiento servidor > 3.Notebook > 4.Forks/Internet (REFERENCIA_EXTERNA) > 5.Suposiciones (HIPOTESIS)`

## Ejemplo: healer cura

`AI? -> AutoPlayTaskManager -> como selecciona target? -> como lanza skill? -> que necesita Player? -> que necesita Party? -> depende de GameClient? -> spike minimo -> claim`

## Cierre

Investigacion completa = permite entender + modificar + validar + revertir. Checklist por area, no por volumen.
