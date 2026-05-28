# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Memories is a single-module Android app (Kotlin + Jetpack Compose) for capturing photo/video/journal entries. Compose is the only UI layer — there are no XML view layouts to migrate. Min SDK 24, target/compile SDK 36, Java/JVM 17.

Notable Kotlin compiler flags in `app/build.gradle.kts`: `-Xexplicit-backing-fields` and `-XXLanguage:+ExplicitBackingFields` are enabled — code uses the explicit backing field syntax (`field: T`) for state properties, so don't "fix" these to look like plain Kotlin properties.

## Build & run

Use the Gradle wrapper from the project root:

- `./gradlew assembleDebug` — build debug APK
- `./gradlew installDebug` — install on connected device/emulator
- `./gradlew :app:lint` — Android lint
- `./gradlew test` — JVM unit tests (`app/src/test`)
- `./gradlew connectedAndroidTest` — instrumented tests (`app/src/androidTest`)
- Single test: `./gradlew :app:testDebugUnitTest --tests "com.example.memories.SomeTest.someMethod"`

`release` build type has `isMinifyEnabled = false`, so R8 keep-rule changes only matter if minification is later enabled.

There is no CI config, no formatter wired into Gradle, and no pre-commit hooks — don't invent commands that aren't here.

## Architecture

MVVM + Clean Architecture (data → domain → presentation) inside a **single `:app` module**. The package layout is the boundary, not Gradle modules:

```
com.example.memories
├── MainActivity, MemoriesApplication, AppNav      # entry points
├── di/AppModule.kt                                # ALL Hilt providers live here
├── navigation/                                    # AppNavHost, Screen.kt (type-safe routes), TopLevelNavigation
├── core/
│   ├── data/data_source/{room,alarm,notification,worker,media,graphics}
│   ├── data/repository                            # *RepositoryImpl
│   ├── domain/{model,repository,usecase}          # interfaces + use cases used across features
│   └── presentation/                              # ThemeViewModel, shared Composables
└── feature/feature_<name>/                        # camera, feed, media_edit, memory, notifications, onboarding, other, backup
    ├── data/{data_source,repository,remote}
    ├── domain/{model,repository,usecase}
    └── presentation/{<screen>/, components/, navigation/}
```

### Things that look wrong but are intentional / load-bearing

- `feature/feature_media_edit/presentatiion/` — typo in the package name, used in imports throughout. Don't rename without updating every import.
- `core/data/data_source/room/Entity/` is capitalized (unlike sibling `dao/`, `mapper/`, etc.).
- Every Hilt binding goes through `di/AppModule.kt` — a single ~570-line `@Module object`. There is no per-feature module split. When adding a use case, wire it into the appropriate `*UseCaseWrapper`/`*UseCases` aggregate in `AppModule` rather than injecting individual use cases into ViewModels.
- Use cases are aggregated into wrapper classes (`FeedUseCaseWrapper`, `MediaUseCases`, `CameraUseCases`, `MemoryUseCase`, `SearchUseCase`, `HiddenUseCase`, `NotificationUseCase`, `TagUseCaseWrapper`, `HistoryUseCaseWrapper`, `TagWithMemoryUseCaseWrapper`). ViewModels inject the wrapper, not the individual use cases.

### Navigation

Type-safe Compose Navigation using `kotlinx.serialization`. Routes are `@Serializable` objects/data classes in `navigation/Screen.kt` (`TopLevelScreen`, `AppScreen`). `AppNavHost.kt` composes per-feature graphs via `createXGraph()` extensions on `NavGraphBuilder` defined under each feature's `presentation/navigation/`. Deep link scheme is `memories://app` (see `Screen.kt` `BASE_URL` and `AndroidManifest.xml`).

Bottom-bar visibility is controlled by `onBottomBarVisibilityChange: (Boolean) -> Unit` plumbed from `AppNav` through every graph — screens that should hide the bar call this in a `LaunchedEffect`.

### Room database

- `MemoryDatabase` (version 7) holds `MemoryEntity`, `MediaEntity`, `TagEntity`, `MemoryTagCrossRef`, `SearchEntity`.
- Migrations 1→2 through 6→7 live in `core/data/data_source/room/migrations/` and are registered in `AppModule.providesMemoryDatabase`. **When bumping `version` on `@Database`, add a new `MEMORY_MIGRATION_N_M` and add it to the `addMigrations(...)` call** — schema dumps in `app/schemas/` are KSP-generated (`room.schemaLocation` arg set in `app/build.gradle.kts`).
- DB name is `"memory-db"`. A query callback logs every SQL statement to logcat tag `MemoryDatabase`.

### Background work

Two parallel mechanisms, owned by `MemoryNotificationSchedulerImpl` (`core/data/data_source/notification`):
- **WorkManager** — `OnThisDayNotificationWorker` for the "on this day" memory reminders. App is a `Configuration.Provider` with `HiltWorkerFactory`.
- **AlarmManager** — `AlarmManagerService` + `AlarmReceiver` for precise daily journaling reminders.

Notification channels (`ON_THIS_DAY_CHANNEL`, `DAILY_REMINDER_CHANNEL`) are created in `MemoriesApplication.onCreate()`.

### Camera & media editing

- CameraX is wrapped by `feature_camera/data/data_source/CameraManager` → `CameraRepositoryImpl` → `CameraUseCases`. UI state flows from the use cases, not directly from CameraX.
- The media editor uses AGSL shaders. `core/data/data_source/graphics/ShaderPipeLine.kt` + `ShaderStep.kt` compose shader steps; filter shader sources live in `core/data/data_source/media/FilterShaders.kt`. State is per-page (`AdjustState` + `ShaderStep`) — see recent commit `00ac118` for the model.
- Image loading: Coil 3 with `VideoFrameDecoder` registered in `MemoriesApplication.newImageLoader` so video thumbnails render via the same `AsyncImage` calls as photos.

### Theming

`MainActivity` provides `LocalTheme` (a `compositionLocalOf<Boolean>`) and wraps the nav graph in `MemoriesTheme`. Theme preference is read from `OtherSettingsDatastore` via `ThemeViewModel` / `ThemeUseCase` (Preferences DataStore, not Room). `dynamicColor = false` is hard-coded in `MainActivity`.
