# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# ========================
# GENERAL OPTIMIZATION
# ========================

# Preserve line numbers for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep annotations
-keepattributes *Annotation*

# Keep generic signatures for reflection
-keepattributes Signature

# Keep exception attributes for proper stack traces
-keepattributes Exceptions

# ========================
# KOTLIN
# ========================

# Kotlin metadata
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings { <fields>; }
-keep class kotlin.reflect.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keep class kotlinx.coroutines.** { *; }

# ========================
# JETPACK COMPOSE
# ========================

# Compose runtime
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.material.** { *; }
-keep class androidx.compose.material3.** { *; }

# ========================
# RETROFIT & OKHTTP
# ========================

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# OkHttp Logging Interceptor
-keep class okhttp3.logging.** { *; }

# ========================
# GSON
# ========================

# Gson uses generic type information stored in a class file when working with fields.
-keepattributes Signature

# Gson specific classes
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep all data model classes (adjust package name as needed)
-keep class com.Tom.uceva_dengue.Data.Model.** { *; }
-keep class com.Tom.uceva_dengue.Model.** { *; }

# ========================
# GOOGLE MAPS & LOCATION
# ========================

# Google Maps
-keep class com.google.android.gms.maps.** { *; }
-keep class com.google.maps.android.** { *; }
-dontwarn com.google.android.gms.**

# Maps Compose
-keep class com.google.maps.android.compose.** { *; }

# Location Services
-keep class com.google.android.gms.location.** { *; }

# Maps Utils (Heatmap)
-keep class com.google.maps.android.heatmaps.** { *; }
-keep class com.google.maps.android.clustering.** { *; }

# ========================
# FIREBASE
# ========================

# Firebase Messaging
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.common.** { *; }
-dontwarn com.google.firebase.**

# ========================
# COIL (Image Loading)
# ========================

-keep class coil.** { *; }
-keep interface coil.** { *; }
-dontwarn coil.**

# ========================
# DATASTORE
# ========================

-keep class androidx.datastore.*.** { *; }

# ========================
# SIGNALR
# ========================

-keep class com.microsoft.signalr.** { *; }
-dontwarn com.microsoft.signalr.**

# ========================
# APACHE POI (Excel)
# ========================

-keep class org.apache.poi.** { *; }
-dontwarn org.apache.poi.**
-dontwarn org.apache.xmlbeans.**
-dontwarn org.apache.commons.codec.**
-dontwarn org.apache.commons.collections4.**

# ========================
# SECURITY CRYPTO
# ========================

-keep class androidx.security.crypto.** { *; }
-keep class com.google.crypto.tink.** { *; }

# ========================
# VIEWMODELS & LIVEDATA
# ========================

-keep class * extends androidx.lifecycle.ViewModel {
    <init>();
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}

# ========================
# NAVIGATION
# ========================

-keep class androidx.navigation.** { *; }
-keepnames class androidx.navigation.fragment.NavHostFragment

# ========================
# MATERIAL ICONS
# ========================

-keep class androidx.compose.material.icons.** { *; }

# ========================
# APP-SPECIFIC RULES
# ========================

# Keep all ViewModels
-keep class com.Tom.uceva_dengue.ui.viewModel.** { *; }

# Keep all Service interfaces (Retrofit)
-keep interface com.Tom.uceva_dengue.Data.Service.** { *; }

# Keep all Composables
-keep @androidx.compose.runtime.Composable class * { *; }

# ========================
# GENERAL ANDROID
# ========================

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom views
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable implementations
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}