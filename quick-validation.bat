@echo off
echo ==================================================
echo QUICK VALIDATION TEST
echo ==================================================

echo.
echo Testing OpenAPI specification validity...
call gradlew.bat openApiGenerate --quiet
if errorlevel 1 (
    echo ❌ OpenAPI generation failed - check specification syntax
    goto :end
) else (
    echo ✅ OpenAPI specification is valid
)

echo.
echo Testing Kotlin compilation...
call gradlew.bat :core-module:compileDebugKotlin --quiet
if errorlevel 1 (
    echo ❌ Kotlin compilation still failing
    echo Check build output for remaining issues
) else (
    echo ✅ Kotlin compilation successful - serialization issue fixed!
)

:end
echo.
pause
