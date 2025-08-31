@echo off
echo MATERIAL3 DEPENDENCY RESOLUTION FIX VERIFICATION
echo =================================================

cd /d "C:\GenesisEos"

echo Testing Material3 resolution in affected modules...

echo.
echo 1. Testing module-f...
call gradlew :module-f:dependencies --configuration debugCompileClasspath --no-daemon | findstr "material3" 

echo.
echo 2. Testing module-e...  
call gradlew :module-e:dependencies --configuration debugCompileClasspath --no-daemon | findstr "material3"

echo.
echo 3. Testing module-d...
call gradlew :module-d:dependencies --configuration debugCompileClasspath --no-daemon | findstr "material3"

echo.
echo 4. Testing module-b...
call gradlew :module-b:dependencies --configuration debugCompileClasspath --no-daemon | findstr "material3"

echo.
echo 5. Testing module-c...
call gradlew :module-c:dependencies --configuration debugCompileClasspath --no-daemon | findstr "material3"

echo.
echo 6. Testing sandbox-ui...
call gradlew :sandbox-ui:dependencies --configuration debugCompileClasspath --no-daemon | findstr "material3"

echo.
echo Testing full build...
call gradlew build --no-daemon

echo.
echo MATERIAL3 FIX VERIFICATION COMPLETE
pause
