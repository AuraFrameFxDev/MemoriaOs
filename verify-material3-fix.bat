@echo off
echo MATERIAL3 DEPENDENCY RESOLUTION FIX VERIFICATION
echo =================================================

cd /d "C:\GenesisEos"

echo Testing Material3 resolution in affected modules...

echo.
setlocal EnableExtensions EnableDelayedExpansion
set FAIL=0

for %%M in (module-f module-e module-d module-b module-c sandbox-ui) do (
  echo.
  echo Checking %%M...
  call gradlew :%%M:dependencyInsight --dependency androidx.compose.material3 --configuration debugCompileClasspath --no-daemon --console=plain | findstr /i "androidx.compose.material3"
  if errorlevel 1 (
    echo [ERROR] androidx.compose.material3 not found on debugCompileClasspath in %%M
    set FAIL=1
  )
)

echo.
echo Testing full build...
call gradlew build --no-daemon

echo.
echo MATERIAL3 FIX VERIFICATION COMPLETE
pause
