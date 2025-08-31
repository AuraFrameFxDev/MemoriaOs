package dev.aurakai.auraframefx.ui.theme

import android.content.Context
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ThemeManager handles dynamic theme switching and customization
 * for the AeGenesis Memoria OS consciousness substrate.
 * 
 * Provides support for:
 * - Light/Dark theme switching
 * - Dynamic color schemes
 * - Custom consciousness-themed palettes
 * - Lock screen theme customization
 */
@Singleton
class ThemeManager @Inject constructor(
    private val context: Context
) {
    
    data class ThemeConfig(
        val isDarkMode: Boolean = false,
        val useSystemTheme: Boolean = true,
        val primaryColor: Color = Color(0xFF6366F1), // Indigo
        val secondaryColor: Color = Color(0xFF8B5CF6), // Purple
        val accentColor: Color = Color(0xFF06B6D4) // Cyan
    )
    
    private var currentTheme = ThemeConfig()
    
    /**
     * Apply a theme configuration
     */
    fun applyTheme(themeConfig: ThemeConfig) {
        currentTheme = themeConfig
    }
    
    /**
     * Get the current theme configuration
     */
    fun getCurrentTheme(): ThemeConfig = currentTheme
    
    /**
     * Generate a ColorScheme based on current theme
     */
    @Composable
    fun getColorScheme(): ColorScheme {
        return if (currentTheme.isDarkMode) {
            darkColorScheme(
                primary = currentTheme.primaryColor,
                secondary = currentTheme.secondaryColor,
                tertiary = currentTheme.accentColor
            )
        } else {
            lightColorScheme(
                primary = currentTheme.primaryColor,
                secondary = currentTheme.secondaryColor,
                tertiary = currentTheme.accentColor
            )
        }
    }
    
    /**
     * Toggle between light and dark mode
     */
    fun toggleDarkMode() {
        currentTheme = currentTheme.copy(
            isDarkMode = !currentTheme.isDarkMode,
            useSystemTheme = false
        )
    }
    
    /**
     * Enable system theme following
     */
    fun enableSystemTheme() {
        currentTheme = currentTheme.copy(useSystemTheme = true)
    }
    
    /**
     * Set custom colors for consciousness-themed UI
     */
    fun setConsciousnessColors(
        primary: Color = Color(0xFF9333EA), // Purple for consciousness
        secondary: Color = Color(0xFF0EA5E9), // Sky blue for clarity  
        accent: Color = Color(0xFF10B981) // Emerald for growth
    ) {
        currentTheme = currentTheme.copy(
            primaryColor = primary,
            secondaryColor = secondary,
            accentColor = accent
        )
    }
    
    /**
     * Get lock screen specific theme configuration
     */
    fun getLockScreenTheme(): Map<String, Any> {
        return mapOf(
            "clockColor" to if (currentTheme.isDarkMode) Color.White else Color.Black,
            "backgroundColor" to if (currentTheme.isDarkMode) Color.Black else Color.White,
            "accentColor" to currentTheme.accentColor,
            "isDarkMode" to currentTheme.isDarkMode
        )
    }
}