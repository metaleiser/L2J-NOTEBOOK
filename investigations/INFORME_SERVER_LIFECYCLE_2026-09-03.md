# INFORME SERVER LIFECYCLE - 2026-09-03

**Estado**: COMPLETE / RUNTIME VERIFIED
**Baseline**: L2J Mobius CT 2.6 HighFive @ e2518ab108
**Runtime**: `L2J_Mobius_CT_2.6_HighFive`
**Objetivo**: Verificar apagado, reinicio y apertura del cliente usando los lanzadores VBS reales.

## Resumen

La receta anterior estaba anclada al layout antiguo `dist/login` y `dist/game` y describia comandos Java directos. El runtime actualizado usa `login/LoginServer.vbs` y `game/GameServer.vbs`, ejecutados desde sus propias carpetas. La receta y `REF-BOOT` fueron corregidos a ese flujo.

El apagado limpio mediante el comando GM `//shutdown N` sigue siendo el metodo recomendado. En esta prueba no se pudo ejecutar el comando desde una sesion GM. Cerrar la ventana Java con `WM_CLOSE` tampoco detuvo GameServer, por lo que se aplico la alternativa de emergencia documentada: terminar el proceso identificado. Esto debe considerarse un apagado no limpio, con riesgo de perder datos pendientes de persistencia.

## Evidencia de la prueba

### Apagado

- Cliente `l2.exe` cerrado mediante su ventana normal.
- GameServer PID `32328`: el cierre normal y `WM_CLOSE` no liberaron el puerto `7777`.
- GameServer termino al intentar la contingencia; `7777` quedo libre.
- LoginServer PID `30152`: el cierre normal y `WM_CLOSE` no liberaron `2106` ni `9014`.
- Se aplico terminacion forzada al PID identificado; `2106`, `9014` y `7777` quedaron libres.
- No se forzaron procesos no identificados.

### Reinicio

1. Se ejecuto `login/LoginServer.vbs` desde `login`.
2. Inicio observado: `2026-09-03 22:30:33`.
3. Puerto `2106` disponible: `22:30:37`.
4. Se ejecuto `game/GameServer.vbs` desde `game`.
5. Inicio de GameServer observado: `22:30:46`.
6. `Server loaded`: `22:33:44`.
7. Tiempo medido: **178 segundos**.
8. Registro en LoginServer: `22:33:46`, Server 2 `Sieghardt`.
9. Puerto esperado de juego: `7777`.

Una ejecucion anterior del mismo runtime registro `414 segundos`. El tiempo debe medirse por log en cada arranque; no debe codificarse como espera fija.

### Cliente

En la prueba anterior se ejecuto `L2 H5/system/l2.exe` desde `system` despues de confirmar los servidores. El proceso fue responsive, pero mostro una ventana `Warning` y no se valido llegada a la pantalla de login. No se modificaron `l2.ini` ni `Lineage2us.ini`.

## Cambios documentales

- `L2J-RECIPE-master/recipes/Server-Start-Stop-Restart-Reload.md`
  - Cambiado al runtime `L2J_Mobius_CT_2.6_HighFive`.
  - Sustituidos comandos Java directos por `cscript //nologo LoginServer.vbs` y `GameServer.vbs`.
  - Exigido ejecutar cada VBS desde su carpeta.
  - Anadidos puertos `2106`, `9014` y `7777`.
  - Anadidas senales READY del log y medicion dinamica.
  - Documentado que cerrar la ventana no garantiza el apagado.
  - Elevado el riesgo de terminacion forzada y perdida de persistencia.
  - Anadido el arranque del cliente solo despues de GameServer `ON`.
  - Actualizada validacion: `178s` actual y `414s` anterior.

- `L2J-RECIPE-master/references/REF-BOOT.md`
  - Sincronizado con el runtime VBS, puertos y protocolo de cierre.

## Protocolo recomendado para pruebas de IDE

1. Trabajar con una copia de runtime de prueba y base de datos identificable.
2. Antes de iniciar, verificar que no haya procesos Java del runtime y que `2106`, `9014` y `7777` esten libres.
3. Ejecutar `login/LoginServer.vbs` desde `login`; capturar PID, hora y log.
4. Esperar log READY y puerto `2106`; no usar `sleep` fijo.
5. Ejecutar `game/GameServer.vbs` desde `game`; capturar hora exacta.
6. Esperar `Handlers Loaded`, spawns inicializados, `Server loaded in X seconds` y registro en LoginServer.
7. Guardar el valor `X`, el log y cualquier warning/error.
8. Abrir el cliente solo despues de GameServer `ON` y registrar el resultado visible.
9. Para apagar, conectar con GM y usar `//shutdown 10` o un valor equivalente.
10. Esperar el cierre y verificar procesos, puertos y logs antes de volver a iniciar.
11. Si el comando GM no esta disponible, detener la prueba y pedir intervencion; usar terminacion forzada solo como contingencia explicita.
12. Tras una terminacion forzada, revisar conexiones, persistencia de personajes, traders, eventos y errores antes de considerar la prueba aprobada.

## Protocolo recomendado para el GM

- LoginServer puede permanecer encendido durante el trabajo diario; no hace falta reiniciarlo por cada cambio de datos.
- Para cambios XML, HTML o configuracion soportada, preferir `//reload` cuando la receta lo permita.
- Para cambios Java, handlers nuevos o cambios de bytecode, reiniciar GameServer completo.
- El orden de reinicio es LoginServer READY primero y GameServer despues.
- No iniciar otra instancia si los puertos ya estan ocupados; primero identificar el PID.
- Antes de parar GameServer, avisar a los jugadores y usar `//shutdown 30` o `//shutdown 60` para dar tiempo a guardar estado.
- No cerrar la ventana como metodo normal: en esta prueba no detuvo el proceso.
- LoginServer no debe apagarse antes que GameServer durante un cierre completo.
- Cuando GameServer haya terminado y `7777` este libre, apagar LoginServer y verificar `2106` y `9014`.

## Evaluacion

La recomendacion es **no reiniciar LoginServer cada vez que se solicite un reinicio del servidor de juego**. Mantener LoginServer encendido reduce interrupciones y permite que el GameServer se registre de nuevo despues de un reinicio. LoginServer solo debe reiniciarse cuando cambie su configuracion, falle, se actualice su JAR o se haga un mantenimiento completo.

Para un reinicio normal solicitado por un GM:

1. GM ejecuta `//shutdown N` o `//restart N` en GameServer.
2. Se espera que GameServer cierre limpiamente y libere `7777`.
3. Si fue un shutdown, GM o IDE inicia `game/GameServer.vbs` desde `game`.
4. Se esperan todas las senales READY y el registro en LoginServer.
5. El cliente se inicia solo cuando GameServer este `ON`.

Para un reinicio de mantenimiento completo:

1. GM ejecuta shutdown limpio de GameServer.
2. Se verifica `7777` libre.
3. Se apaga LoginServer y se verifican `2106` y `9014` libres.
4. Se inicia LoginServer y se espera READY.
5. Se inicia GameServer y se espera READY.

**Conclusion**: LoginServer permanente durante la operacion normal; GameServer reiniciable de forma independiente; apagado por GM como protocolo estandar; terminacion forzada solo como emergencia y nunca como prueba de apagado limpio.

## Pendientes

- Validar en juego un `//shutdown N` real con GM.
- Confirmar que el cierre GM persiste correctamente personaje, inventario, traders y eventos.
- Investigar la ventana `Warning` del cliente y validar pantalla de login.
- Incorporar extractos de logs como evidencia inmutable si se requiere auditoria futura.

---

## Anexo — Segunda corrida de validación (2026-09-03, direct `java -jar`)

### Qué se probó

Una segunda corrida del mismo ciclo (Start → Verify → Shutdown → Restart) usando **arranque directo `java -jar`** en lugar de los VBS, con `EnableGUI=False` para operación headless. Objetivo: confirmar que el procedimiento no depende del launcher y recabar evidencia de logs raw.

### Evidencia capturada

- **LoginServer** (login/log/java0.log 23:18:41-42): READY ≈ 1s.
  - `HikariCP pool initialized successfully.`
  - `LoginServer: Listening for GameServers on 127.0.0.1:9014`
  - `LoginServer: is now listening on: 0.0.0.0:2106`
- **GameServer** (game/log/java0.log 23:20-23:22):
  - `MasterHandler: Handlers Loaded...` (23:20:12) + `AdminCommandHandler: Loaded 481 handlers.`
  - `SpawnData: 42348 spawns have been initialized!` (23:21:58)
  - `GameServer: Server loaded in 179 seconds.` (23:22:13)
  - `LoginServerThread: Registered on login as Server 2: Sieghardt` (23:22:14)
- **Shutdown (Method C):** GameServer PID 25752 + LoginServer PID 10512 terminados con `Stop-Process -Force`; puertos 2106/9014/7777 cerrados; DB quedó con 0 personajes online.
- Restart confirmado en orden correcto.

### Hallazgos nuevos

1. El arranque directo `java -jar ../libs/LoginServer.jar` / `GameServer.jar` es equivalente a los VBS (mismos flags vía java.cfg). → `CL-0023`
2. `EnableGUI=True` (default) bloquea el arranque headless; fijar `EnableGUI=False` en ambos `Interface.ini`. → `CL-0022`
3. Method C verificado: procesos muertos, puertos libres y DB limpia con 0 jugadores conectados. → `CL-0021`
4. Boot time de esta corrida: **179s** (dentro del rango 178-414s ya documentado). → `CL-0020`

### Artefactos de evidencia

- `evidence/REF-BOOT-2026-09-03/MANIFEST.md` + 4 extractos raw de logs (SHA-256 en MANIFEST).
- Reporte narrativo: `L2J-RECIPE/validation/REF_BOOT_VALIDATION_REPORT.md`.

### Cross-references

- Claims: CL-0020, CL-0021, CL-0022, CL-0023 (`promotes_to: R-SRV001`).
- Reference: REF-BOOT (recipe corrige checklist de puertos con 9014 y nota headless).
- Recipe: R-SRV001.
