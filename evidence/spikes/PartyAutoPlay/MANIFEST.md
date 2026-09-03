# MANIFEST — Party + AutoPlay Spike Evidence

**Spike:** Party + AutoPlay + AssistLeader composition
**Date:** 2026-09-01
**UPSTREAM baseline:** e2518ab10872b28cd4c6860e102b493656ba8728
**Verdict:** PASS

## SHA-256 Manifest

| File | Size (bytes) | SHA-256 |
|------|-------------|---------|
| BotSpikeParty.java | 25852 | 1A8374D0C4674A693F68CF68303BCD3E2C0AAC521D788120EEDA73144920F053 |
| BotSpikeParty_provisioning.sql | 11330 | A16B703BF14D8F23F1FCA1E5F328B36635DE102CDCB59AC9D4C322CBA2FC0DD5 |
| runtime_BotSpikeParty.log | 80960 | AEBEBCA60D5E127AAA15413C46B0D98D17C8A1139607BE813343E9B52759CF40 |
| runtime_BotSpikeParty_ascii.log | 40485 | 3811AD883D8997B27D54F7EFD671E645BD4BB2FCD904DB8421C2667641E5AE05 |

## Post-Spike State Verification

| Artifact | Size (bytes) | SHA-256 | Notes |
|----------|-------------|---------|-------|
| AutoPlay.ini (restored) | 1307 | B128353128CBD9E7CBDC3D214A9342AA1865F17D3DB9B7AB38AFA9A6AF255E72 | EnableAutoPlay=False, AssistLeader=False |

## Provenance

- BotSpikeParty.java: harness fuente (scripting engine), desechable
- BotSpikeParty_provisioning.sql: SQL de aprovisionamiento de bots de prueba
- runtime_BotSpikeParty.log: log runtime original (UTF-16-LE, capturado por ScriptLogger)
- runtime_BotSpikeParty_ascii.log: conversion ASCII del log runtime para lectura
- AutoPlay.ini (restaurado): config post-spike verificando restauracion del gate

## Knowledge Artifacts

| Artifact | Path |
|----------|------|
| Spike report | knowledge/20_PARTY_AUTOPLAY_SPIKE.md |

---

*Manifest generado 2026-09-01. Todos los hashes SHA-256 verificables con Get-FileHash.*