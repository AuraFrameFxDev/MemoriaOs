@echo off
echo BUILD STATUS CHECK AFTER FIXES
echo ================================

cd /d "C:\GenesisEos"

echo Testing secure-comm module compilation...
call gradlew :secure-comm:compileDebugKotlin --no-daemon

echo.
echo Testing libs.versions.toml parsing...
call gradlew :secure-comm:dependencies --configuration debugCompileClasspath --dry-run --no-daemon

echo.
echo Testing KSP task status...
call gradlew :app:tasks --group ksp --no-daemon

echo.
echo BUILD CHECK COMPLETE
pause
