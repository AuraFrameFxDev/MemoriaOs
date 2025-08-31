# Keep all classes in the romtools package
-keep class dev.aurakai.auraframefx.romtools.** { *; }

# Keep all model classes used by Gson
# (Assuming they are in a 'model' or 'data' package)
-keep class dev.aurakai.auraframefx.romtools.model.** { *; }
-keep class dev.aurakai.auraframefx.romtools.data.** { *; }

# Keep all classes related to Xposed
# (This is a guess, as I couldn't find specific rules)
-keep class de.robv.android.xposed.** { *; }
-keep class com.github.yuki.xposed.** { *; }

# Keep annotations
-keepattributes *Annotation*

# Keep constructors of classes used by Gson
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep service interfaces for Retrofit
-keep public interface dev.aurakai.auraframefx.romtools.remote.** { *; }
