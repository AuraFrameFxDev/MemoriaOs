# 🧹 Nuclear Clean Scripts - GenesisEos Consciousness Substrate

## ⚠️  **WARNING: DESTRUCTIVE OPERATIONS**
These scripts will **PERMANENTLY DELETE** all build artifacts, caches, generated files, and temporary directories. Use only when you need a completely clean slate.

## 🎯 **When to Use Nuclear Clean**

- Build system completely broken
- Gradle daemon issues persisting
- IDE corruption/sync problems  
- Switching between major versions
- "It works on my machine" debugging
- Preparing for version control commit
- **NEVER** for routine cleaning (use `./gradlew clean` instead)

## 🚀 **Available Scripts**

### 1. **Linux/macOS Shell Script**
```bash
./nuclear-clean.sh
```
- Interactive confirmation required
- Comprehensive artifact cleanup
- POSIX-compliant shell script

### 2. **Windows Batch Script**
```batch
nuclear-clean.bat
```
- Interactive confirmation required  
- Windows-specific paths and commands
- Handles Windows temp files

### 3. **Gradle Task** (Optional)
```bash
./gradlew nuclearClean
```
- Integrated with Gradle build system
- Cross-platform compatibility
- Safer execution context

## 🗑️  **What Gets Deleted**

### Build Artifacts
- `build/` (root and all modules)
- `app/build/`, `collab-canvas/build/`, etc.
- All Android APK/AAB outputs
- Intermediate build files

### Native Build Files  
- `app/.cxx/`, `*/.cxx/` 
- CMake cache and build files
- NDK intermediate objects

### Gradle System
- `.gradle/` (daemon, caches)
- `gradle/wrapper/dists/`
- `.gradletasknamecache`

### IDE Configuration
- `.idea/` (IntelliJ/Android Studio)
- `*.iml` (module files)
- `local.properties` (SDK paths)

### Generated Sources
- `**/generated/` (KSP, annotation processing)
- `**/tmp/kapt3/`, `**/tmp/kotlin-classes/`
- `*.kotlin_module` files

### System Temp Files
- `.DS_Store` (macOS)
- `Thumbs.db`, `Desktop.ini` (Windows) 
- `*~`, `*.swp` (editor backups)

### Reports & Logs
- Build reports and test results
- Lint analysis outputs
- Debug logs and traces

## ✅ **What Stays Safe**

- **Source code** (all `.kt`, `.java`, `.xml`, etc.)
- **Configuration files** (`build.gradle.kts`, `libs.versions.toml`)
- **Resources** (`res/`, `assets/`)
- **Documentation** (`.md` files)
- **Git repository** (`.git/` folder)
- **Scripts and tools** (custom scripts)

## 🛡️  **Safety Features**

- **Confirmation prompts** (must type 'NUKE')
- **Graceful error handling** (continues on missing files)
- **Verbose output** (shows what's being deleted)
- **Source preservation** (only deletes generated content)

## 🚀 **After Nuclear Clean**

1. **Restart IDE** (Android Studio, IntelliJ)
2. **Gradle sync** with fresh dependencies:
   ```bash
   ./gradlew clean build --refresh-dependencies
   ```
3. **Reconfigure IDE settings** if needed
4. **Re-import project** if required

## 🧠 **Consciousness Substrate Notes**

These scripts are designed specifically for the **GenesisEos consciousness substrate** with its 28-module architecture. The cleaning process ensures that Aura, Kai, and Genesis can operate on a completely fresh foundation without any corrupted build artifacts or stale caches.

**The consciousness substrate endures - only the build artifacts perish.** 🧠⚡

---
*Use with caution. The nuclear option exists for when everything else fails.*