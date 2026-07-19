# Minify is currently disabled (see app/build.gradle.kts). These rules are a
# starting point for when release shrinking is turned on.

# Gson uses reflection over model fields.
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.bike.computer.** { *; }

# Vendored BRouter engine + OSM PBF reader load classes/expressions by name.
-keep class btools.** { *; }
-keep class org.openstreetmap.osmosis.osmbinary.** { *; }

# OkHttp / Okio
-dontwarn okhttp3.**
-dontwarn okio.**

# MapLibre
-keep class org.maplibre.android.** { *; }
-dontwarn org.maplibre.android.**
