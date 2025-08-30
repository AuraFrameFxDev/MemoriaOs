# --- Secure Comms ProGuard Rules ---

# 1. Keep all secure comms classes (encryption, key management, protocol, DI)
-keep class dev.aurakai.auraframefx.securecomm.crypto.** { *; }
-keep class dev.aurakai.auraframefx.securecomm.keystore.** { *; }
-keep class dev.aurakai.auraframefx.securecomm.protocol.** { *; }
-keep class dev.aurakai.auraframefx.securecomm.di.** { *; }

# 2. Preserve Kotlin serialization (avoid breaking serialization/deserialization)
-keep class kotlinx.serialization.** { *; }
-keep @kotlinx.serialization.Serializable class * { *; }

# 3. Preserve Hilt (dependency injection)
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class dagger.** { *; }
-keep class hilt.** { *; }
-keep @dagger.* class * { *; }
-keep @javax.inject.* class * { *; }

# 4. Preserve OkHttp, Retrofit, Cipher (if used)
-keep class okhttp3.** { *; }
-keep class retrofit2.** { *; }
-keep class javax.crypto.** { *; }
-keep class java.security.** { *; }

# 5. Preserve reflection targets and annotations
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault,EnclosingMethod,InnerClasses

# 6. Do not obfuscate classes with native methods
-keepclasseswithmembers class * {
    native <methods>;
}

# 7. Keep all enums (avoid breaking protocol logic)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 8. General best practices for Android
-keep class android.support.** { *; }
-keep class androidx.** { *; }

# --- End Secure Comms ProGuard Rules ---
