@echo off
echo COMPOSE COMPILER FIX VERIFICATION
echo ==================================

cd /d "C:\GenesisEos"

echo Testing Material3 dependency resolution...
call gradlew :colorblendr:dependencies --configuration debugCompileClasspath --no-daemon | findstr "material3" > material3_deps.txt

if exist "material3_deps.txt" (
    echo FOUND Material3 dependencies:
    type material3_deps.txt
    del material3_deps.txt
) else (
    echo ERROR: Material3 dependencies not found
)

echo.
echo Testing compose compiler with Kotlin plugin...
call gradlew :colorblendr:compileDebugKotlin --dry-run --no-daemon

echo.
echo COMPOSE COMPILER FIX TEST COMPLETE
pause
