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
was **reconstructed by decompiling the installed APK with jadx**, then:

- The `com.bike.computer` app classes were **Java decompiled from Kotlin bytecode**, hand-fixed
  to compile, and have now been **fully re-authored back into idiomatic Kotlin, class by class,
  each validated against the jadx baseline + APK** (see `VALIDATION.md` and git history). No
  decompiled Java, `@Metadata`, `Intrinsics`, or `$$ExternalSyntheticLambda` artifacts remain.
- Porting surfaced and fixed several genuine decompiler bugs (jadx had flagged them "decompiled
  incorrectly"): an HR-graph integer-division/​bounds bug, three corrupt `copy$default` masks
  (rename / critical-shutdown / normal ride-stop all mis-saving fields), and lost `when` branches
  (page titles, turn maneuvers) — all restored to the original intent.
- The vendored `btools/` routing engine is **authentic upstream BRouter 1.7.9** source (the
  version pinned in `OsmTrack.version`) — the decompiled routing core was corrupt. (Two small
  app-authored helpers, `btools/router/NavHint` + `HintAccess`, remain Java in that package.)
- **Status:** the whole app is validated Kotlin and builds to a debug APK (`./gradlew
  :app:assembleDebug`). A fresh on-device smoke test on the XZ1 is the last step before merge.
- Work is on branch **`recovery-buildable`**.

## Project layout

```
app/src/main/
  java/com/bike/computer/   43 app classes, validated idiomatic Kotlin (.kt)
  java/btools/              BRouter routing engine, upstream v1.7.9 (104 files, not on Maven;
                            offline-only server/ + most of mapcreator/ removed, HgtReader kept)
  java/org/openstreetmap/   vendored OSM PBF reader used by BRouter (16 files)
  res/                      full resource tree recovered from the APK
  AndroidManifest.xml
```

## Building

AGP 8.7.3, Gradle 8.9 (wrapper committed), compileSdk / build-tools 35, JDK 17.

```
./gradlew :app:assembleDebug      # -> app/build/outputs/apk/debug/app-debug.apk
```

Point `local.properties` (`sdk.dir=...`) at an Android SDK with platform 35 and
`build-tools;35.0.0`. On non-standard Linux (e.g. NixOS) where the Maven-bundled `aapt2`
won't run, set `android.aapt2FromMavenOverride=<sdk>/build-tools/35.0.0/aapt2` in
`~/.gradle/gradle.properties`.

### Side-by-side install (test build)

The **debug** build sets `applicationIdSuffix = ".recovery"` and label "Harmin (Rec)", so it
installs as `com.bike.computer.recovery` **alongside** a production `com.bike.computer`
without replacing it. `adb install -r app-debug.apk`.

### Running it (fresh install starts empty)

The app needs, on first run: the location + Bluetooth (`BLUETOOTH_SCAN`/`CONNECT`) + notification
runtime permissions; **All-files access** (it reads its MapLibre style from
`/sdcard/BikeComputer/styles/style.json`); and the offline map DB `california.mbtiles` in its
`files/` dir. Without the map DB / style it will otherwise crash on the map screen.

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
