# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable

# Rename source file attribute to hide original source file name
-renamesourcefileattribute SourceFile

# ===== Kotlin Serialization =====
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Keep Serializers
-keep,includedescriptorclasses class com.wolt.restofinder.**$$serializer { *; }
-keepclassmembers class com.wolt.restofinder.** {
    *** Companion;
}
-keepclasseswithmembers class com.wolt.restofinder.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep serialization-related annotations
-keep class kotlinx.serialization.** { *; }
-keep @kotlinx.serialization.Serializable class * { *; }

# ===== Retrofit =====
# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items)
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not kept
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Keep Retrofit service interfaces
-keep interface com.wolt.restofinder.data.remote.api.** { *; }

# ===== OkHttp =====
# Platform used when running on JVM
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# ===== DataStore =====
-keep class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite { *; }
-keep class androidx.datastore.**.** { *; }

# ===== Coroutines =====
# ServiceLoader support for kotlinx.coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ===== Hilt =====
-dontwarn com.google.errorprone.annotations.**
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# ===== Compose =====
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }

# Keep @Composable functions
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# ===== Coil =====
-keep class coil.** { *; }
-dontwarn coil.**

# ===== Timber =====
-dontwarn org.jetbrains.annotations.**
-keep class timber.log.** { *; }

# ===== General Android =====
# Keep custom view classes
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ===== App-specific rules =====
# Keep data classes used in API responses
-keep class com.wolt.restofinder.data.remote.dto.** { *; }

# Keep domain models
-keep class com.wolt.restofinder.domain.model.** { *; }

# Keep custom exceptions
-keep class com.wolt.restofinder.data.remote.interceptor.** { *; }
