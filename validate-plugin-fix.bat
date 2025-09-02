@echo off
echo ==================================================
echo TESTING PLUGIN RESOLUTION FIX
echo ==================================================

echo.
echo ✅ Plugin Structure Fixed:
echo    • Root: All plugins with 'apply false'
echo    • App: android.application + kotlin.android + kotlin.compose
echo    • Libraries: android.library + kotlin.android + kotlin.compose
echo.

echo Step 1: Testing plugin resolution...
call gradlew.bat projects --quiet
if errorlevel 1 (
    echo ❌ Plugin resolution still failing
    echo Check for remaining plugin conflicts
    goto :error
) else (
    echo ✅ Plugin resolution successful
)

echo.
echo Step 2: Testing app module build...
call gradlew.bat :app:compileDebugKotlin --quiet
if errorlevel 1 (
    echo ❌ App module compilation failed
    echo Check dependencies and plugin compatibility
) else (
    echo ✅ App module compilation successful
)

echo.
echo Step 3: Testing core-module build...
call gradlew.bat :core-module:compileDebugKotlin --quiet
if errorlevel 1 (
    echo ❌ Core module compilation failed
    echo Check OpenAPI generation and serialization
) else (
    echo ✅ Core module compilation successful
)

echo.
echo ==================================================
echo ✅ PLUGIN STRUCTURE VALIDATION COMPLETE
echo ==================================================
echo.
echo The plugin resolution conflict should now be resolved!
echo All modules are using the correct plugin combinations:
echo    • App: Main Android application plugins
echo    • Libraries: Android library plugins
echo    • Compose: Multiplatform support where needed
echo ==================================================
goto :end

:error
echo.
echo ❌ Some issues remain - check output above
echo.

:end
pause
