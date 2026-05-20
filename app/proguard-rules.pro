# Room
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(...);
}

# Hilt
-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep class com.google.dagger.hilt.** { *; }

# Retrofit & Gson
-keepattributes Signature, InnerClasses, EnclosingMethod
-keep class com.example.wishlistapp.data.remote.dto.** { *; }
-keep class com.google.gson.** { *; }

# Coil
-keep class coil.** { *; }
