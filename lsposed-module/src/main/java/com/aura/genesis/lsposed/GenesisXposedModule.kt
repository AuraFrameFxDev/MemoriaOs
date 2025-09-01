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
     * Initializes the module's hook environment for LSPosed.
     *
     * Loads the module preferences ("genesis_module"), configures YukiHookAPI (sets the debugLog tag
     * to "GenesisModule" and enables it based on the "debug_log" preference; sets global isDebug from
     * BuildConfig.DEBUG), and registers hooks by loading the GenesisHooks container.
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
     * Legacy LSPosed entry point invoked when a package is loaded.
     *
     * Delegates to [onHook] for modern initialization. The `loadPackageParam` is accepted
     * for compatibility with older LSPosed/Module API signatures and is ignored.
     *
     * @param loadPackageParam Legacy load-package parameter (may be null and is unused).
     */
    @Suppress("unused")
    fun handleLoadPackage(loadPackageParam: Any?) {
        // This is kept for backward compatibility
        onHook()
    }

    /**
     * Entry point called during the zygote/system-server startup phase.
     *
     * This is a legacy hook stub for performing zygote-phase initialization when the module
     * is loaded early (e.g., by LSPosed). The default implementation is a no-op.
     *
     * @param startupParam Platform/loader-provided startup parameters (may be null and are ignored by default).
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
