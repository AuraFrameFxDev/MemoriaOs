@echo off
echo ==================================================
echo FIXING OPENAPI SERIALIZATION BUILD ISSUE
echo ==================================================

echo.
echo Step 1: Cleaning previous build artifacts...
call gradlew.bat cleanApiGeneration
call gradlew.bat clean

echo.
echo Step 2: Regenerating OpenAPI code with fixed specification...
call gradlew.bat openApiGenerate

echo.
echo Step 3: Attempting to build core-module...
call gradlew.bat :core-module:compileDebugKotlin

echo.
echo Step 4: Running full build if Kotlin compilation succeeds...
call gradlew.bat build

echo.
echo ==================================================
echo BUILD PROCESS COMPLETE
echo ==================================================
