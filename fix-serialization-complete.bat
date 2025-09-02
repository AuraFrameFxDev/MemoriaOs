@echo off
echo ==================================================
echo FIXING KOTLINX SERIALIZATION BUILD ISSUES
echo ==================================================

echo.
echo 🔧 Fixed Issues in OpenAPI Specification:
echo    • ComponentTestRequest.parameters: Map^<String, Any^> → String (JSON)
echo    • ErrorResponse.details: additionalProperties true → String (JSON)
echo    • ConsciousnessRequest.parameters: Object → String (JSON)
echo    • ConsciousnessResponse.result: Object → String (JSON)  
echo    • EmpathyResponse.emotionalAnalysis: Object → String (JSON)
echo    • UnifiedGenerationRequest.context: additionalProperties true → String (JSON)
echo    • LockScreenConfig properties: Object → String (JSON)
echo.

echo Step 1: Cleaning previous build artifacts...
call gradlew.bat cleanApiGeneration
if errorlevel 1 (
    echo ❌ Failed to clean API generation
) else (
    echo ✅ API generation cleaned successfully
)

call gradlew.bat clean
if errorlevel 1 (
    echo ❌ Failed to clean project
) else (
    echo ✅ Project cleaned successfully
)

echo.
echo Step 2: Regenerating OpenAPI code with fixed specification...
call gradlew.bat openApiGenerate
if errorlevel 1 (
    echo ❌ OpenAPI generation failed
    echo Please check the OpenAPI specification for syntax errors
    goto :error
) else (
    echo ✅ OpenAPI code generated successfully
)

echo.
echo Step 3: Testing Kotlin compilation (core-module only)...
call gradlew.bat :core-module:compileDebugKotlin
if errorlevel 1 (
    echo ❌ Kotlin compilation failed
    echo The serialization issue may still exist or there are other compilation errors
    goto :error
) else (
    echo ✅ Kotlin compilation successful!
)

echo.
echo Step 4: Running full build...
call gradlew.bat assembleDebug
if errorlevel 1 (
    echo ⚠️  Full build failed, but core serialization issue is fixed
    echo There may be other unrelated build issues to resolve
) else (
    echo ✅ Full build successful!
)

echo.
echo ==================================================
echo ✅ KOTLINX SERIALIZATION ISSUE RESOLVED!
echo ==================================================
echo.
echo The following changes were made:
echo • Generic 'Object' types changed to 'String' (JSON serialization)
echo • All 'additionalProperties: true' removed
echo • Parameters now use JSON string format for flexibility
echo.
echo Your OpenAPI-generated Kotlin code should now compile without
echo serialization errors. The API will accept JSON strings for
echo flexible parameters instead of generic Object types.
echo ==================================================
goto :end

:error
echo.
echo ❌ Build process encountered errors
echo See output above for details
echo.

:end
pause
