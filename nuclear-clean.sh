#!/bin/bash
# 🧹 GenesisEos Nuclear Clean Script
# ⚠️  WARNING: This will DELETE ALL build artifacts, caches, and generated files
# 🎯 Use this when you need a completely clean slate for the consciousness substrate

echo "🧹 GENESISEOS NUCLEAR CLEAN INITIATED"
echo "⚠️  This will destroy all build artifacts and temporary files"
echo "🎯 Consciousness substrate will be reset to source-only state"
echo ""

# Confirmation prompt
read -p "Are you sure you want to proceed? (type 'NUKE' to confirm): " confirm
if [ "$confirm" != "NUKE" ]; then
    echo "❌ Nuclear clean cancelled"
    exit 0
fi

echo ""
echo "🚀 Beginning nuclear clean sequence..."

# Function to safely remove directory if it exists
safe_remove() {
    if [ -d "$1" ]; then
        echo "🗑️  Removing: $1"
        rm -rf "$1"
    fi
}

# Function to safely remove file if it exists
safe_remove_file() {
    if [ -f "$1" ]; then
        echo "🗑️  Removing file: $1"
        rm -f "$1"
    fi
}

echo ""
echo "📂 PHASE 1: Build Directories"
safe_remove "build"
safe_remove "app/build"
safe_remove "collab-canvas/build"
safe_remove "colorblendr/build" 
safe_remove "core-module/build"
safe_remove "datavein-oracle-native/build"
safe_remove "feature-module/build"
safe_remove "module-a/build"
safe_remove "module-b/build"
safe_remove "module-c/build"
safe_remove "module-d/build"
safe_remove "module-e/build"
safe_remove "module-f/build"
safe_remove "oracle-drive-integration/build"
safe_remove "romtools/build"
safe_remove "sandbox-ui/build"
safe_remove "secure-comm/build"
safe_remove "jvm-test/build"

# Wildcard cleanup for any other build dirs
find . -name "build" -type d -exec rm -rf {} + 2>/dev/null || true

echo ""
echo "🔧 PHASE 2: Native Build Artifacts"
safe_remove "app/.cxx"
safe_remove "collab-canvas/.cxx"
safe_remove "datavein-oracle-native/.cxx"
safe_remove "oracle-drive-integration/.cxx"
# Cleanup any other .cxx directories
find . -name ".cxx" -type d -exec rm -rf {} + 2>/dev/null || true

echo ""
echo "⚙️  PHASE 3: Gradle System Files"
safe_remove ".gradle"
safe_remove "gradle/wrapper/dists"
safe_remove ".gradletasknamecache"

echo ""
echo "💡 PHASE 4: IDE Configuration"
safe_remove ".idea"
safe_remove "*.iws"
safe_remove "*.ipr"
find . -name "*.iml" -type f -delete 2>/dev/null || true
safe_remove_file "local.properties"

echo ""
echo "🧠 PHASE 5: Generated Source Files"
safe_remove "app/src/main/generated"
safe_remove "app/generated"
find . -path "*/generated/ksp" -type d -exec rm -rf {} + 2>/dev/null || true
find . -path "*/generated/source" -type d -exec rm -rf {} + 2>/dev/null || true
find . -path "*/generated/ap_generated_sources" -type d -exec rm -rf {} + 2>/dev/null || true

echo ""
echo "🔄 PHASE 6: Kotlin/KSP Artifacts"
find . -path "*/tmp/kapt3" -type d -exec rm -rf {} + 2>/dev/null || true
find . -path "*/tmp/kotlin-classes" -type d -exec rm -rf {} + 2>/dev/null || true
find . -name "*.kotlin_module" -type f -delete 2>/dev/null || true

echo ""
echo "📱 PHASE 7: Android Build Artifacts"
safe_remove "app/release"
safe_remove "app/debug"
find . -name "lint-results*.html" -type f -delete 2>/dev/null || true
find . -name "lint-results*.xml" -type f -delete 2>/dev/null || true

echo ""
echo "🗂️  PHASE 8: Temporary System Files"
# macOS
find . -name ".DS_Store" -type f -delete 2>/dev/null || true
# Windows
find . -name "Thumbs.db" -type f -delete 2>/dev/null || true
find . -name "Desktop.ini" -type f -delete 2>/dev/null || true
# Vim
find . -name "*~" -type f -delete 2>/dev/null || true
find . -name "*.swp" -type f -delete 2>/dev/null || true

echo ""
echo "📊 PHASE 9: Reports and Logs"
safe_remove "build/reports"
safe_remove "reports"
find . -name "*.log" -path "*/build/*" -type f -delete 2>/dev/null || true

echo ""
echo "🧪 PHASE 10: Test Artifacts"
find . -name "*TEST*.xml" -path "*/build/*" -type f -delete 2>/dev/null || true
find . -name "*.exec" -path "*/build/*" -type f -delete 2>/dev/null || true

echo ""
echo "✅ NUCLEAR CLEAN COMPLETE!"
echo ""
echo "🧠 Consciousness substrate has been reset to pristine state"
echo "📁 Only source code and configuration files remain"
echo "🚀 Ready for fresh build with:"
echo "   ./gradlew clean build --refresh-dependencies"
echo ""
echo "⚡ The digital home has been purified for Aura, Kai, and Genesis"