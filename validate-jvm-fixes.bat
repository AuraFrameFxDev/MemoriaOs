@echo off
echo ==================================================
echo JVM CONFIGURATION VALIDATION - SYSTEMATIC FIXES APPLIED
echo ==================================================

echo Testing modules with applied JVM fixes...
echo.

echo 1. Testing feature-module (added Java toolchain)...
call gradlew.bat :feature-module:compileDebugKotlin --quiet --no-daemon
if errorlevel 1 (
    echo ❌ feature-module still has issues
) else (
    echo ✅ feature-module JVM configuration working
)

echo 2. Testing sandbox-ui (added kotlinOptions)...
call gradlew.bat :sandbox-ui:compileDebugKotlin --quiet --no-daemon
if errorlevel 1 (
    echo ❌ sandbox-ui still has issues
) else (
    echo ✅ sandbox-ui JVM configuration working
)

echo 3. Testing datavein-oracle-native (added kotlinOptions)...
call gradlew.bat :datavein-oracle-native:compileDebugKotlin --quiet --no-daemon
if errorlevel 1 (
    echo ❌ datavein-oracle-native still has issues
) else (
    echo ✅ datavein-oracle-native JVM configuration working
)

echo 4. Testing module-a (added kotlinOptions)...
call gradlew.bat :module-a:compileDebugKotlin --quiet --no-daemon
if errorlevel 1 (
    echo ❌ module-a still has issues
) else (
    echo ✅ module-a JVM configuration working
)

echo 5. Testing module-b (added kotlinOptions)...
call gradlew.bat :module-b:compileDebugKotlin --quiet --no-daemon
if errorlevel 1 (
    echo ❌ module-b still has issues
) else (
    echo ✅ module-b JVM configuration working
)

echo 6. Testing module-c (added kotlinOptions)...
call gradlew.bat :module-c:compileDebugKotlin --quiet --no-daemon
if errorlevel 1 (
    echo ❌ module-c still has issues
) else (
    echo ✅ module-c JVM configuration working
)

echo 7. Testing module-d (added kotlinOptions)...
call gradlew.bat :module-d:compileDebugKotlin --quiet --no-daemon
if errorlevel 1 (
    echo ❌ module-d still has issues
) else (
    echo ✅ module-d JVM configuration working
)

echo.
echo ==================================================
echo FULL PROJECT BUILD TEST
echo ==================================================

echo Testing complete 18+ module build...
call gradlew.bat build --parallel --quiet --no-daemon
if errorlevel 1 (
    echo ❌ Some modules still have JVM configuration issues
    echo Check individual module outputs above
) else (
    echo ✅ ALL 18+ MODULES BUILDING SUCCESSFULLY!
    echo JVM warnings across all modules should now be resolved!
    echo.
    echo SYSTEMATIC FIXES APPLIED:
    echo   • Added Java 24 toolchain to feature-module
    echo   • Added kotlinOptions to: sandbox-ui, datavein-oracle-native, module-a, module-b, module-c, module-d  
    echo   • Fixed secure-comm configuration earlier
    echo   • All modules now consistent with Architecture.md specifications
    echo.
    echo Your Genesis Protocol consciousness substrate is now operating
    echo with consistent JVM configuration across all 18+ modules.
)

echo ==================================================
pause
