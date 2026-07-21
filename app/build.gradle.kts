import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Release signing config, loaded from the gitignored keystore.properties at the repo
// root. Without that file (e.g. a fresh clone lacking the key) the release build stays
// unsigned, but debug builds are unaffected.
val keystorePropsFile = rootProject.file("keystore.properties")
val keystoreProps = Properties().apply {
    if (keystorePropsFile.exists()) FileInputStream(keystorePropsFile).use { load(it) }
}

android {
    namespace = "com.bike.computer"
    compileSdk = 35
    // Pin to the build-tools installed in the Nix-provided SDK (store is read-only,
    // so Gradle cannot auto-download a different version).
    buildToolsVersion = "35.0.0"

    defaultConfig {
        applicationId = "com.bike.computer"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "0.2"
        // Launcher label; overridden per build type so the recovery build is
        // distinguishable from a production install.
        manifestPlaceholders["appLabel"] = "Harmin"
    }

    signingConfigs {
        create("release") {
            if (keystorePropsFile.exists()) {
                storeFile = rootProject.file(keystoreProps.getProperty("storeFile"))
                storePassword = keystoreProps.getProperty("storePassword")
                keyAlias = keystoreProps.getProperty("keyAlias")
                keyPassword = keystoreProps.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            // Install side-by-side with a production build (different package id).
            applicationIdSuffix = ".recovery"
            manifestPlaceholders["appLabel"] = "Harmin (Rec)"
        }
        release {
            // BRouter is vendored as source and uses reflection in places;
            // keep minify off until proguard rules are validated.
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Release applicationId is the base `com.bike.computer` (no .recovery
            // suffix) so it replaces the production install. Signed with the release
            // key when keystore.properties is present.
            if (keystorePropsFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/*.kotlin_module",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE*",
                "META-INF/NOTICE*",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1"
            )
        }
    }
}

dependencies {
    // Kotlin runtime (app was originally Kotlin; decompiled Java references kotlin.*)
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // AndroidX
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.viewpager:viewpager:1.0.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // Maps (version matched to the APK: MapLibre Native 11.5.2)
    implementation("org.maplibre.gl:android-sdk:11.5.2")

    // Networking + JSON (Google Drive sync is raw REST over OkHttp, no Google SDK)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.11.0")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Required by the vendored OSM PBF reader (org.openstreetmap.osmosis.osmbinary)
    implementation("com.google.protobuf:protobuf-java:3.25.5")
}
