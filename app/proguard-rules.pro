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

## LockedIn ProGuard / R8 rules
##
## SECURITY:
## - Avoid leaking implementation details while preserving functionality
##   for libraries that use reflection or generated code.

# Hilt / Dagger / injection
-keepattributes *Annotation*
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class androidx.hilt.** { *; }
-keep class com.lockedin.app.di.** { *; }

# Room (entities, DAO interfaces)
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-keep class com.lockedin.app.data.local.entity.** { *; }
-keep class com.lockedin.app.data.local.dao.** { *; }

# WorkManager with Hilt workers
-keep class androidx.work.ListenableWorker { *; }
-keep class androidx.work.CoroutineWorker { *; }
-keep class com.lockedin.app.worker.** { *; }

# Glance widgets
-keep class androidx.glance.** { *; }
-dontwarn androidx.glance.**

# Retrofit / OkHttp / kotlinx.serialization
-dontwarn kotlinx.serialization.**
-keep class kotlinx.serialization.** { *; }
-keep @kotlinx.serialization.Serializable class ** { *; }

# Biometric / Security Crypto
-keep class androidx.biometric.** { *; }
-dontwarn androidx.biometric.**
-keep class androidx.security.crypto.** { *; }

# Domain models (for clearer crash logs and interop)
-keepclassmembers class com.lockedin.app.domain.model.** { *; }