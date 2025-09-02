# Genesis Protocol - ROM Tools Module
-keep class dev.aurakai.auraframefx.romtools.** { *; }

# Android system modification classes
# Avoid keeping platform classes; R8 treats them as library jars and these rules add noise.

# LSPosed and Xposed classes
-keep class de.robv.android.xposed.** { *; }
-keep class org.lsposed.** { *; }
-keep class com.highcapable.yukihookapi.** { *; }

# Kotlin serialization
-keepclassmembers class **$$serializer {
    *** serializer(...);
}
-keepclasseswithmembers class * {
    *** Companion;
}

# Hilt
-keepclasseswithmembers class * {
    @dagger.hilt.** <methods>;
}

# Remove debug logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
