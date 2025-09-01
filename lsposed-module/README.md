# Genesis LSPosed Module

This module provides Xposed framework integration for the Genesis ecosystem using Yuki API. It allows for runtime method hooking and modification of target applications.

## Features

- **Yuki API Integration**: Modern and type-safe API for Xposed module development
- **LSParanoid Obfuscation**: Protects the module from reverse engineering
- **Hilt Dependency Injection**: Clean architecture with dependency injection
- **Multi-target Hooking**: Easily hook into multiple target applications
- **Runtime Configuration**: Toggle features and debug logging at runtime

## Requirements

- Android 13 (API 33) or higher
- LSPosed Framework installed
- Target SDK 36
- Java 24

## Installation

1. Build the module in Android Studio or run:
   ```bash
   ./gradlew :lsposed-module:assembleRelease
   ```
2. Install the generated APK on your device
3. Activate the module in LSPosed Manager
4. Select target applications to hook into
5. Reboot your device

## Configuration

The module can be configured through the LSPosed Manager interface or by using the following adb commands:

```bash
# Enable debug logging
adb shell pm grant com.aura.genesis.lsposed android.permission.WRITE_SECURE_SETTINGS
adb shell settings put global genesis_module_debug_log true

# Disable debug logging
adb shell settings put global genesis_module_debug_log false
```

## Development

### Adding New Hooks

1. Create a new Kotlin object for your hooks:
   ```kotlin
   object MyFeatureHooks {
       init {
           YukiHookAPI.encase {
               loadApp("com.target.app") {
                   // Your hooks here
               }
           }
       }
   }
   ```

2. Add it to the main hooks initialization in `GenesisXposedModule.kt`

### Building

```bash
# Build release APK
./gradlew :lsposed-module:assembleRelease

# Build debug APK
./gradlew :lsposed-module:assembleDebug
```

## License

```
Copyright 2025 AeGenesis Coinscience AI Ecosystem

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
