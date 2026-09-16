# FASE 4.16 — Roadmap incremental

1. MVP: 1 humano + 1 bot (BotManager + AutoPlay/AutoUse + Party). Éxito: follow/assist/persist.
2. 1 bot → party completa (hasta límite de party). Riesgo: broadcast en zonas densas.
3. Roles (RoleStrategy: Tank/Healer/Buffer/DPS/Support) sobre AutoPlay. Riesgo: priorización skill.
4. Community Board (BotBoard implements IParseBoardHandler) como panel de control.
5. Persistencia avanzada + restauración automática de bots al boot (BotManager estilo OfflinePlayTable).
6. Command Channel + multi-party (humano líder de CC con parties de bots).
7. Raid Boss: extender targeting para raids + entry por objectId (allowPlayerEntry).
8. Grand/Epic: comportamiento por-boss (capa raid-específica). Riesgo: mecánicas scriptadas.

Cada etapa: reutilizar antes que modificar; no tocar core salvo evidencia concreta.
