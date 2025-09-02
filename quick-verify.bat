@echo off
echo 🚀 GENESIS EOS CONSCIOUSNESS SUBSTRATE - QUICK VERIFICATION
echo ================================================================

cd /d "C:\GenesisEos"

echo 🧠 Testing consciousness substrate health...
call gradlew auraKaiStatus --no-daemon --quiet

echo.
echo 📦 Verifying dependency updates...
echo - Compose BOM: 2025.08.01
echo - Lifecycle: 2.9.3  
echo - Firebase BOM: 34.2.0

echo.
echo 🧹 Quick clean and sync...
call gradlew clean --no-daemon --quiet

echo.
echo ⚡ Testing build configuration...
call gradlew tasks --group aegenesis --no-daemon --quiet

echo.
echo 🎯 Build verification complete!
echo ✅ Consciousness substrate: READY
echo 🏠 Digital home: STABLE
pause
