# Harmin app — recompilation validation tracker

Tracks the class-by-class validation of the recovered `com.bike.computer` app. Each class was Java
decompiled from the original Kotlin bytecode by jadx and hand-fixed to compile ("recompiled"). We
validate each by **porting it back to idiomatic Kotlin** — cross-checked against the jadx baseline
(`../bikecomputer_recovery/decompiled/`) and the original `apk/base.apk`. A class is `validated`
once its `.kt` replaces the `.java`, carries no decompiler artifacts, and the build is green.

Vendored `btools/` + `org/openstreetmap/` are authentic upstream BRouter/OSM source — **not in scope**.

**Baseline:** `./gradlew :app:assembleDebug` BUILD SUCCESSFUL on the Java tree (2026-07-21).

Status legend: `recompiled` (untouched decompiled Java) → `in-progress` → `validated`.

## Learnings that shape the migration

- **`@Metadata` must be stripped from every remaining `.java`.** The Kotlin compiler reads sibling
  Java *sources* for symbol resolution but silently drops any imported `.java` class still carrying
  `@kotlin.Metadata` (it mistakes decompiled Java for a Kotlin class with a bogus header). Done
  tree-wide in Batch A (incl. `btools/router/NavHint.java`, `HintAccess.java`).
- **Kotlin cannot use named args when calling a Java constructor/method** — call still-Java types
  positionally from ported Kotlin.
- **Data classes with field-accessing consumers are ported last.** Decompiled consumers reach into
  Kotlin-compiled internals (package-private fields, synthetic `copy$default`) that a clean Kotlin
  data class doesn't expose to Java. So `RideSummary` and `DashTile` stay Java until their consumers
  (RideHistory/RideService/MainActivity/…) are Kotlin → moved to Batch D.
- **`Ble` / `Sneaky`** are hand-written Java helpers using true statics (`Ble.canScan(...)`); porting
  to Kotlin `object` would change callers to `.INSTANCE.`. Ported/removed last (Batch D).

| Class | Batch | Kotlin file | Status | Notes |
|-------|-------|-------------|--------|-------|
| Units | A | Units.kt | validated | object |
| HrZone | A | HrZone.kt | validated | enum |
| DashBlock | A | DashBlock.kt | validated | enum |
| Metric | A | Metric.kt | validated | enum |
| RouteResult | A | RouteResult.kt | validated | data class (consumers use getters/componentN) |
| DriveFile | A | DriveFile.kt | validated | data class |
| Pages | A | Pages.kt | validated | object; `fixedTitle` original branches lost in decompile → identity preserved |
| AppState | A | AppState.kt | validated | object |
| ActionBus | A | ActionBus.kt | validated | object |
| Bell | A | Bell.kt | validated | object; dropped spurious jadx aliases (HgtReader.HGT_VOID→-32768) |
| Voice | A | Voice.kt | validated | object; dropped spurious recyclerview alias (→200) |
| GpxSummary | A | GpxSummary.kt | validated | object; faithful re-port of the "decompiled incorrectly" summarize() parser |
| ImmersiveKt | A | Immersive.kt | validated | file-facade → `Activity.enterImmersive()` extension |
| DashboardViewKt | A | — | validated | dead file-facade (only a private unused const) → removed |
| RideSummary | D | RideSummary.kt | recompiled | data class; deferred — consumers use package-private fields |
| DashTile | D | DashTile.kt | recompiled | data class; deferred — consumers use synthetic copy$default |
| Sneaky | — | — | validated | removed — dead code once all callers became Kotlin (use `use{}`) |
| Ble | D | Ble.kt | recompiled | clean Java helper; port once all callers are Kotlin |
| LocalTiles | B | LocalTiles.kt | validated | |
| GpxRoute | B | GpxRoute.kt | validated | |
| GmapsRoute | B | GmapsRoute.kt | validated | |
| Geocoder | B | Geocoder.kt | validated | |
| StreetNames | B | StreetNames.kt | validated | ⚠ past decompiler bug (infinite loop) |
| BikeRouter | B | BikeRouter.kt | validated | ⚠ past decompiler bug (dup waypoint) |
| RideHistory | B | RideHistory.kt | validated | 🐛 FIXED: `rename` copy$default mask was corrupt (name arg was dead code) → restored to set the name |
| RideRecorder | B | RideRecorder.kt | validated | |
| HrGraphView | B | HrGraphView.kt | validated | View. 🐛 FIXED: int-division in x-mapping (collapsed graph to a vertical line) + inverted bounds check in 5-tap smoothing |
| ElevationView | B | ElevationView.kt | validated | View |
| RouteThumb | B | RouteThumb.kt | validated | View |
| LedController | B | LedController.kt | validated | |
| Prefs | C | Prefs.kt | validated | 91 Intrinsics, Result blocks |
| GoogleDriveClient | C | GoogleDriveClient.kt | validated | 6 WARN, raw Drive REST |
| CyclingSensor | C | CyclingSensor.kt | validated | BLE, type-inference WARNs |
| HrSensor | C | HrSensor.kt | validated | BLE |
| DashboardView | C | DashboardView.kt | validated | ⚠ past grid-pack infinite loop |
| WelcomeActivity | D | WelcomeActivity.kt | validated | absorbs WelcomeActivity$conn$1 |
| DriveAuthActivity | D | DriveAuthActivity.kt | validated | |
| DestinationSearchActivity | D | DestinationSearchActivity.kt | validated | |
| RidesActivity | D | RidesActivity.kt | validated | |
| RideSummaryActivity | D | RideSummaryActivity.kt | validated | |
| RideService | D | RideService.kt | validated | foreground service; reads RideSummary fields |
| SettingsActivity | D | SettingsActivity.kt | validated | 1662 lines |
| MainActivity | D | MainActivity.kt | recompiled | 3099 lines, 16 WARN; absorbs MainActivity$conn$1 |
