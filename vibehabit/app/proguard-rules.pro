# =================================================================
# Advanced Code Obfuscation, Anti-Cloning & Security Rules (R8/ProGuard)
# =================================================================

# Repackage all internal classes into root to destroy reverse engineering hierarchy
-repackageclasses ''
-allowaccessmodification

# Hide source file names and strip debugging line numbers
-renamesourcefileattribute ''
-keepattributes !SourceFile,!LineNumberTable,!MethodParameters

# Aggressive name mangling
-overloadaggressively
-useuniqueclassmembernames

# Strip diagnostic logs in compiled builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Preserve necessary model serialization metadata
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-keep class com.example.data.model.** { *; }
-dontwarn com.squareup.moshi.**
