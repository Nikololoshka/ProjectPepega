# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:

# NewsViewer interface
-keepclassmembers class com.vereshchagin.nikolay.stankinschedule.ui.news.viewer.NewsViewerFragment.NewsViewInterface {
   public *;
}

# Uncomment this to preserve the line number information for
# debugging stack traces.
# -keepattributes SourceFile, LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
# -renamesourcefileattribute SourceFile

-dontobfuscate

#===================================================================================================
#===================================================================================================
#===================================================================================================

# Gson
-keepclassmembers, allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

#===================================================================================================
# Retrofit2
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclassmembernames interface * {
    @retrofit2.http.* <methods>;
}

#===================================================================================================
# Joda Time
# All the resources are retrieved via reflection, so we need to make sure we keep them
-keep class net.danlew.android.joda.R$raw { *; }

# These aren't necessary if including joda-convert, but
# most people aren't, so it's helpful to include it.
-dontwarn org.joda.convert.FromString
-dontwarn org.joda.convert.ToString

# Joda classes use the writeObject special method for Serializable, so
# if it's stripped, we'll run into NotSerializableExceptions.
# https://www.guardsquare.com/en/products/proguard/manual/examples#serializable
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#===================================================================================================
