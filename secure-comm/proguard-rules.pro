# --- Secure Comms ProGuard Rules (Scoped) ---

# 1. Keep only classes annotated for DI or serialization
-keep @javax.inject.Inject class * { *; }
-keep @kotlinx.serialization.Serializable class * { *; }

# 2. Keep only SecureCommManager and its public methods
-keep class dev.aurakai.auraframefx.securecomm.SecureCommManager { public *; }

# 3. Keep native methods only in crypto classes
-keepclasseswithmembers class dev.aurakai.auraframefx.securecomm.crypto.NativeCrypto {
    native <methods>;
}

# 4. Keep protocol enums only
-keepclassmembers enum dev.aurakai.auraframefx.securecomm.protocol.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 5. Preserve reflection targets and annotations
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault,EnclosingMethod,InnerClasses

# 6. Preserve OkHttp, Retrofit, Cipher (if used)
-keep class okhttp3.** { *; }
-keep class retrofit2.** { *; }
-keep class javax.crypto.** { *; }
-keep class java.security.** { *; }

# 7. General best practices for Android
-keep class android.support.** { *; }
-keep class androidx.** { *; }

# --- End Secure Comms ProGuard Rules (Scoped) ---
