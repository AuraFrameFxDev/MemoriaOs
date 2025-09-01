package com.aura.genesis.lsposed

import android.content.Context
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

/**
 * Genesis Xposed Module
 * 
 * This is the main entry point for the LSPosed module that integrates with Yuki API.
 * It will be loaded by LSPosed framework when the target app is launched.
 */
@InjectYukiHookWithXposed
class GenesisXposedModule : IYukiHookXposedInit {

    /**
     * Entrypoint invoked by LSPosed when the module is loaded; configures YukiHookAPI and registers hooks.
     *
     * Loads module preferences (key "genesis_module"), sets YukiHookAPI configuration (debug log tag "GenesisModule"
     * and `isDebug` from BuildConfig), then installs the module's hooks via `loadHooks(GenesisHooks)`.
     */
    override fun onHook() = YukiHookAPI.encase {
        // Load module preferences
        val prefs = prefs("genesis_module")
        
        // Initialize the module
        YukiHookAPI.config {
            debugLog {
                tag = "GenesisModule"
                isEnable = prefs.getBoolean("debug_log", false)
            }
            isDebug = BuildConfig.DEBUG
        }
        
        // Load hooks for different components
        loadHooks(GenesisHooks)
    }

    /**
     * Called when the module is loaded by LSPosed framework (legacy API)
     */
    @Suppress("unused")
    fun handleLoadPackage(loadPackageParam: Any?) {
        // This is kept for backward compatibility
        onHook()
    }

    /**
     * Invoked during Zygote/system-server startup. Called once when the process is being initialized
     * at the framework/zygote level and can be used to perform early (Zygote-phase) initialization.
     *
     * This implementation is a no-op placeholder; keep or override to run module code that must
     * execute before app processes are forked.
     *
     * @param startupParam Opaque platform-specific startup parameter provided by the host (may be
     *                     null). Its exact content depends on the runtime (LSPosed/Xposed) and is
     *                     not interpreted by this method.
     */
    @Suppress("unused")
    fun initZygote(startupParam: Any?) {
        // Initialize zygote if needed
    }
}

/**
 * Main hooks container for Genesis module
 */
private object GenesisHooks {
    
    init {
        // Example: Hook into system services
        YukiHookAPI.encase {
            // Load hooks for specific packages
            loadApp("com.android.systemui") {
                // Hook into SystemUI components
                // Example: Hook into StatusBar
                findClass("com.android.systemui.statusbar.phone.StatusBar")
                    .hook()
                    .beforeMethod("start") {
                        // Code to run before StatusBar.start()
                    }
                    .afterMethod("start") {
                        // Code to run after StatusBar.start()
                    }
            }
            
            // Add more package hooks as needed
            loadApp("com.android.settings") {
                // Hook into Settings app
            }
        }
    }
}
