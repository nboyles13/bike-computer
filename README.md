# Bike Computer ("Harmin")

A Garmin Edge-class cycling head unit that runs on a rooted **Sony Xperia XZ1 Compact**
(G8441, LineageOS). The app registers as the device **home/launcher**, so the phone boots
straight into the bike computer.

Features (from the recovered v0.2 build):
- MapLibre vector maps with offline tiles
- Configurable dashboard tiles / metrics; swipeable pages (Summary, Data, Map, HR, Elevation)
- Ride recording as a foreground service, with ride history & summaries
- **Offline routing via an embedded BRouter engine** (`btools`)
- BLE cycling sensors (speed/cadence, heart rate) + HR zones
- Destination search / geocoding, elevation profile, turn-by-turn street names
- Google Drive ride sync (raw Drive REST over OkHttp — no Google SDK)
- Voice cues, bell, and LED controller

## ⚠️ Provenance — this is recovered source

The original Kotlin sources were lost when the dev machine was wiped (2026-07-19). This tree
was **reconstructed by decompiling the installed APK with jadx**, so:

- Source files are **Java decompiled from Kotlin bytecode**, not the original `.kt` files.
  They read correctly and preserve the logic, but are not guaranteed to compile without
  manual fixes (jadx output is optimized for reading).
- The plan is: **get the Java baseline building in Android Studio first**, then migrate to
  idiomatic Kotlin incrementally, one class at a time.

## Project layout

```
app/src/main/
  java/com/bike/computer/   43 app classes (the recovered application)
  java/btools/              vendored BRouter routing engine (150 files, not on Maven)
  java/org/openstreetmap/   vendored OSM PBF reader used by BRouter (16 files)
  res/                      full resource tree recovered from the APK
  AndroidManifest.xml
```

## Building

Requires Android Studio (AGP 8.7, compileSdk 35, JDK 17).

```
# The Gradle wrapper JAR is not committed; generate it once:
gradle wrapper --gradle-version 8.9
./gradlew :app:assembleDebug
```

Expect compile errors on the first pass — these are decompiler artifacts to fix by hand.
Track them as you go; each fixed class is a candidate for Kotlin migration.

## Dependencies of note

| Purpose | Dependency |
|---|---|
| Maps | `org.maplibre.gl:android-sdk:11.5.2` (matched to the APK) |
| Routing | BRouter — **vendored as source** under `btools/` |
| OSM PBF | `com.google.protobuf:protobuf-java` (for vendored `osmbinary`) |
| Networking | OkHttp + Gson (Drive sync is raw REST) |

## Security note

The running app stores **live Google OAuth tokens + client secret in plaintext** in
`shared_prefs/bike_prefs.xml` on the device. Those credentials should be rotated, and no
app-data dump belongs in this repo (see `.gitignore`).
