# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Genesis Protocol - Secure Communication Module
-keep class dev.aurakai.auraframefx.securecomm.crypto.CryptoManager { *; }
-keep class dev.aurakai.auraframefx.securecomm.protocol.SecureChannel { *; }

# Crypto and security classes (scoped)
-keep class org.bouncycastle.jce.provider.BouncyCastleProvider { *; }
-keep class javax.crypto.Cipher { *; }
-keep class java.security.Key { *; }

# Kotlin serialization (scoped to models)
-keepclassmembers class dev.aurakai.auraframefx.securecomm.model.**$serializer {
    *** serializer(...);
}

# Hilt
-keepclasseswithmembers class * {
    @dagger.hilt.** <methods>;
}

# Remove debug and logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Removed overly broad keep rules
# -keep class dev.aurakai.auraframefx.securecomm.** { *; }
# -keep class javax.crypto.** { *; }
# -keep class java.security.** { *; }
# -keep class org.bouncycastle.** { *; }
