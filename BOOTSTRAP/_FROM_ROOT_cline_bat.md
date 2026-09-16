@echo off
title Cline - Asistente L2J Mobius (GPU 1.5B)

echo ======================================
echo  Iniciando Cline para L2J Mobius
echo  Modelo: Qwen2.5-Coder-1.5B (GPU)
echo  Workspace: E:\L2J MOBIUS IA
echo ======================================
echo.

REM Cambiar al directorio de trabajo
cd /d "E:\L2J MOBIUS IA"
echo [0/3] Directorio actual: %CD%
echo.

REM 1. Verificar si Ollama está ejecutándose
echo [1/3] Verificando Ollama...
tasklist /FI "IMAGENAME eq ollama.exe" 2>NUL | find /I /N "ollama.exe">NUL
if "%ERRORLEVEL%"=="0" (
    echo       [OK] Ollama ya esta corriendo.
) else (
    echo       [AVISO] Ollama no esta corriendo. Iniciando...
    start /B ollama serve
    echo       Esperando 3 segundos para que Ollama arranque...
    timeout /t 3 /nobreak >NUL
)

REM 2. Verificar que el modelo esté descargado
echo [2/3] Verificando modelo...
ollama list | findstr "qwen2.5-coder:1.5b" >NUL
if "%ERRORLEVEL%"=="0" (
    echo       [OK] Modelo encontrado.
) else (
    echo       [AVISO] Modelo no encontrado. Descargando ahora...
    ollama pull qwen2.5-coder:1.5b
)

REM 3. Lanzar Cline en el workspace
echo [3/3] Lanzando Cline en %CD%...
echo.
echo ======================================
echo  Escribe tu consulta sobre L2J Mobius
echo  Presiona Ctrl+C para salir
echo ======================================
echo.

cline --model ollama/qwen2.5-coder:1.5b

echo.
echo ======================================
echo  Cline ha finalizado.
echo ======================================
pause