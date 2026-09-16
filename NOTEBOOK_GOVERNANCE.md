# NOTEBOOK GOVERNANCE — Reglas del NOTEBOOK GATE

> **Estado:** CANONICO. Autoritativo sobre la gestion de conocimiento del Notebook.
> **Baseline:** L2J Mobius CT 2.6 HighFive @ e2518ab108
> **Fecha:** 2026-09-12
> **Alcance:** Toda incorporacion, actualizacion o retiro de conocimiento en `L2J Notebook`.

---

## 1. Proposito

`L2J Notebook` es la **memoria tecnica curada** del proyecto L2J Mobius.

NO es:
* un historial de conversaciones;
* un diario de trabajo;
* un deposito de Markdown por obligacion;
* un registro de logs rutinarios.

El Notebook preserva conocimiento tecnico **reutilizable, verificable y no duplicado**.

---

## 2. Principio rector

```text
ONE KNOWLEDGE → ONE AUTHORITATIVE LOCATION
```

Cuando sea razonablemente posible, cada pieza de conocimiento debe tener una **unica ubicacion autoritativa**.

Usar **referencias cruzadas** en lugar de duplicar contenido.

---

## 3. Que SÍ debe conservarse

El Notebook preserva exclusivamente:

| Categoria | Ejemplo |
|-----------|---------|
| Investigaciones tecnicas reutilizables | APIs verificadas, flujos end-to-end, arquitectura de subsistemas |
| Decisiones arquitectonicas | ADRs con alternativas rechazadas y justificacion |
| Claims respaldados por evidencia | File:line anchors, logs, hashes, resultados de spikes |
| APIs verificadas | Firmas exactas, comportamiento documentado, restricciones |
| Restricciones importantes | APIs peligrosas, persistence hazards, limites operativos |
| Errores y trampas | Enfoques descartados, APIs inexistentes, comportamientos inesperados |
| Resultados de verificacion | Spikes, validaciones runtime, procedimientos documentados |
| Conocimiento estable del proyecto | Ciclo de vida, configuracion, arquitectura, dependencias |

---

## 4. Que NO debe conservarse

El Notebook NO debe contener:

| Categoria | Ejemplo |
|-----------|---------|
| Logs rutinarios de ejecucion | "Se ejecuto X, resultado OK" |
| Conversaciones completas | Prompts y respuestas de IA |
| Prompts completos | Instrucciones textuales de tareas |
| Respuestas completas de IA | Salida literal de un agente |
| Informes de progreso sin conocimiento nuevo | "Tarea completada con exito" |
| Informacion temporal | Estados de tareas en curso, resultados preliminares |
| Duplicados | Misma informacion en otro documento existente |
| Hipotesis sin evidencia | "Creemos que X funciona asi" sin verificar |
| Markdown creado por obligacion | Documentos vacios o relleno sin valor tecnico |

---

## 5. NOTEBOOK GATE

El **NOTEBOOK GATE** es el proceso que decide si el resultado de una tarea merece convertirse en conocimiento permanente.

### 5.1 Flujo del Gate

```text
TASK COMPLETADA
       ↓
¿HAY CONOCIMIENTO REUTILIZABLE?
       │
   ┌───┴───┐
   NO      SÍ
   │       │
   ↓       ↓
 FIN    BUSCAR EXISTENTE
             │
             ↓
       ¿YA EXISTE?
        │       │
       SÍ       NO
        │       │
        ↓       ↓
    ACTUALIZAR CREAR
        │       │
        └───┬───┘
            ↓
         VERIFY
            ↓
       INDEX SI CORRESPONDE
```

### 5.2 Regla critica de duda

> Si existe duda razonable sobre si algo constituye conocimiento permanente, **NO crear automaticamente un documento nuevo**.

En ese caso:
1. registrar la duda en el reporte de la task;
2. no contaminar el Notebook;
3. continuar sin crear conocimiento nuevo.

La incorporacion al Notebook debe ser **deliberada**, no automatica.

---

## 6. Busqueda obligatoria antes de crear Markdown

Antes de crear cualquier documento nuevo en futuras tasks, Cline debe:

1. identificar que conocimiento se quiere conservar;
2. consultar `INDEX.md`;
3. buscar documentos relacionados en las carpetas tematicas;
4. determinar si el conocimiento ya existe;
5. si existe → actualizar el documento autoritativo;
6. si no existe → crear un nuevo documento solamente si es necesario;
7. actualizar `INDEX.md` si corresponde.

---

## 7. Estados de conocimiento

Estados canonicos del Notebook:

### VERIFIED

Confirmado directamente mediante:
* codigo fuente (file:line anchor);
* archivo de configuracion;
* ejecucion (logs, spikes, runtime);
* evidencia confiable del proyecto.

### INFERRED

Deduccion razonable basada en evidencia existente, pero **no comprobada directamente**.

### PROPOSED

Diseno o solucion futura que todavia **no esta implementada ni verificada**.

### EXPERIMENTAL

Prueba temporal cuyo comportamiento todavia **debe validarse**.

### NOT FOUND

Algo fue **buscado explicitamente** y no fue encontrado.

### Regla de separacion estricta

```text
PROPOSED ≠ VERIFIED
INFERRED ≠ VERIFIED
EXPERIMENTAL ≠ VERIFIED
NOT FOUND ≠ ABSENCIA ABSOLUTA
```

**NOT FOUND** significa unicamente que la busqueda realizada no encontro ese elemento. No implica que el elemento no exista en ninguna version o configuracion.

**Nunca convertir una hipotesis en VERIFIED por repeticion en otro documento.**

---

## 8. Taxonomia existente

El Notebook ya utiliza la siguiente estructura. NO crear una taxonomia nueva.

| Carpeta/Archivo | Proposito |
|-----------------|-----------|
| `knowledge/` | Conocimiento duradero por dominio (mecanismos, gameplay, config, ciclo de vida) |
| `decisions/` | Architecture Decision Records (ADRs) |
| `investigations/` | Reportes fechados de investigacion con hallazgos duraderos |
| `evidence/` | Evidencia inmutable (logs, spikes, manifests con SHA-256) |
| `CLAIMS.md` | Ledger de claims atomicos granulares (CL-NNNN) |
| `KNOWN_TRAPS/` | Trampas conocidas, enfoques descartados, APIs inexistentes |
| `ROUTING/` | Documentos de enrutamiento ("necesito X, donde miro") |
| `inbox/` | Cuarentena de investigacion externa (no confiable hasta verificar) |
| `OPEN_QUESTIONS.md` | Preguntas abiertas centralizadas y gaps de verificacion |

Esta estructura sera auditada y limpiada posteriormente en `NOTEBOOK-02 — AUDITORIA DE AUTORIDAD Y CURACION`.

---

## 9. Relacion con INDEX.md

`INDEX.md` es el indice principal del Notebook.

* NO crear `NOTEBOOK_INDEX.md`.
* Actualizar `INDEX.md` de forma conservadora.
* Debe servir como **mapa de navegacion**, no como duplicado del contenido.
* Incluir documentos relevantes y autoritativos.
* No inventar documentos ni estados.

---

## 10. Relacion con USER_MANUAL.md

```text
NOTEBOOK_GOVERNANCE.md = reglas autoritativas
USER_MANUAL.md         = instrucciones practicas de uso
```

`NOTEBOOK_GOVERNANCE.md` define las reglas.
`USER_MANUAL.md` explica operacionalmente como aplicarlas.

---

## 11. Relacion con .clinerules/project.md

Las reglas del NOTEBOOK GATE estan integradas en `.clinerules/project.md`.

En caso de conflicto entre documentos, el orden de precedencia es:

1. `.clinerules/project.md` (reglas activas para Cline)
2. `NOTEBOOK_GOVERNANCE.md` (definicion autoritativa)
3. `USER_MANUAL.md` (guia practica)
4. `INDEX.md` (mapa de navegacion)

---

FIN DE NOTEBOOK GOVERNANCE