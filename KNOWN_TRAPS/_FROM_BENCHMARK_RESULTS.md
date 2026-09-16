# Benchmark: qwen2.5-coder:1.5b en L2J Mobius H5 Server

## Hardware del equipo

| Componente | Valor | Impacto |
|---|---|---|
| **CPU** | Intel Core i3-4150 (2C/4T @ 3.5 GHz) | Cuello de botella: solo 2 núcleos reales |
| **RAM** | 12 GB (6,6 GB libres) | Suficiente para 1.5B-3B |
| **GPU** | NVIDIA GTX 750 — **1 GB VRAM** | Insuficiente para el modelo completo; ~704 MB libres |
| **Disco E:** | 16,5 GB libres | Espacio ajustado |
| **SO** | Windows 10 Pro 64-bit | — |

## Resultados de velocidad (medidos)

| Prueba | Prompt (tokens) | Generados (tokens) | Tokens/s | Tiempo total (s) |
|---|---|---|---|---|
| **Baseline** (prompt corto) | 50 | 400 | **14.96** | 27.28 |

> **Nota:** La prueba con `Ballista.java` (~3KB de código + pregunta) excede el límite de 30s del entorno automatizado.  
> Ejecuta `run_benchmark.ps1` manualmente para resultados completos.

## Evaluación de calidad

### 1. Baseline — Información general ❌
El modelo **alucinó gravemente**: dijo que "L2J Mobius es una versión modificada de L2J, *un juego de rol*" y que usa "JME (Java Media Environment)".  
- ❌ L2J no es un juego, es un emulador de servidor
- ❌ JME no existe; L2J Mobius usa Java SE + Netty
- ❌ El modelo claramente no conoce el dominio L2J

### 2. Ballista.java — Comprensión de código ⚠️
*Pendiente de ejecutar manualmente*

### 3. ClassMaster — Análisis de código grande ⚠️
*Pendiente de ejecutar manualmente*

### 4. Generación de código ⚠️
*Pendiente de ejecutar manualmente*

### 5. Completar método ⚠️
*Pendiente de ejecutar manualmente*

### 6. Arquitectura del sistema ⚠️
*Pendiente de ejecutar manualmente*

## Veredicto del análisis de hardware

| Modelo | Tamaño | Tok/s estimado | ¿Recomendado? |
|---|---|---|---|
| **qwen2.5-coder:0.5b** | ~400 MB | ~25-40 tok/s | ✅ Para autocompletar rápido |
| **qwen2.5-coder:1.5b** (actual) | ~986 MB | ~15 tok/s | ⭐ **Punto dulce — quédate con este** |
| **qwen2.5-coder:3b** | ~1.9 GB | ~3-5 tok/s | ⚠️ Mejor calidad, pero lento |
| **qwen2.5-coder:7b** | ~4.7 GB | ~1-2 tok/s | ❌ No recomendado (no cabe en RAM/VRAM) |

## Recomendación final

1. **Quédate con `qwen2.5-coder:1.5b`** — es el óptimo para tu hardware actual.
2. **Ejecuta `run_benchmark.ps1` manualmente** para obtener las métricas completas de velocidad + calidad.
3. **Calidad esperada:** El 1.5B alucinará APIs específicas de L2J Mobius (`AbstractNpcAI`, `SystemMessageId`, etc.) porque su conocimiento del dominio es limitado. Para tareas donde la precisión importe, considera subir a `qwen2.5-coder:3b` (si toleras ~3-5 tok/s).
4. **Tips de rendimiento:**
   - Cierra Chrome/navegador antes de usar el modelo
   - Usa `num_ctx: 8192` por defecto (no 32768) para mejor velocidad
   - La GPU GTX 750 aporta poco; el modelo se ejecuta casi 100% en CPU

## Archivos generados

```
others/benchmark/
├── run_benchmark.ps1      # Script de benchmark completo (ejecutar manualmente)
├── run_benchmark.cmd       # Acceso directo al script
├── prompts/                # Prompts usados en cada prueba
├── results/                # Respuestas del modelo + reportes
└── RESULTS.md              # Este archivo
```