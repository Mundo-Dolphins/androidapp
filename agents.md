# Mundo Dolphins Android App: Agent & Engineering Guide

## Purpose

`MundoDolphins` is an Android app for consuming Mundo Dolphins podcast content and related news.

Primary product goals inferred from code:

- Show recent podcast episodes and season history.
- Let users open detailed episode content.
- Stream episode audio with progress persistence.
- Show article/news content from backend JSON endpoints.
- Work with local cached episode data through Room.

## Target Audience and Main Use Case

Audience:

- Spanish-speaking Miami Dolphins fans using Android devices.
- Users who consume podcast episodes and articles on mobile.

Main use case:

1. Open app.
2. App refreshes seasons/episodes from backend.
3. User browses feed, seasons, links, and articles via bottom navigation.
4. User opens episode details and plays audio.
5. Playback progress and listening status are persisted locally.

## Module and Repository Layout

Current project layout is a **single Android application module**:

- Root module: `:app`
- No feature modules, no dynamic feature modules.

Key files:

- `settings.gradle.kts`: includes only `:app`
- `app/build.gradle.kts`: Android, Kotlin, Compose, Room, Firebase, test/lint setup
- `gradle/libs.versions.toml`: centralized dependency/plugin versions

## Technical Stack (Source of Truth from Build Files)

### Language and Platform

- Kotlin (`org.jetbrains.kotlin.android`, Kotlin 2.3.x)
- Android SDK
  - `compileSdk = 36`
  - `targetSdk = 36`
  - `minSdk = 26`
- Java/Kotlin toolchain: Java 17

### Build System

- Gradle with Kotlin DSL (`*.gradle.kts`)
- Version catalog (`gradle/libs.versions.toml`)
- KSP enabled (used for Room compiler)

### UI

- Jetpack Compose (Material3)
- Navigation Compose for in-app routing
- Coil 3 for remote image loading

### Data and Persistence

- Room (`runtime`, `ktx`, `paging` dependency present)
- `InstantConverter` (`java.time.Instant`) for DB type conversion
- Local DB currently configured with `Episode` entity in production `AppDatabase`

### Networking

- Retrofit + Gson converter
- API base URL hardcoded in client: `https://mundodolphins.es/api/`
- Endpoints:
  - `seasons.json`
  - `season_{id}.json`
  - `articles.json`

### Media

- AndroidX Media3 ExoPlayer
- Foreground `Service` for audio playback (`AudioPlayerService`)

### Firebase

- Analytics
- Remote Config
- Crashlytics
- `google-services` plugin and CI handling for `google-services.json`

### Static Analysis and Formatting

- `ktlint` Gradle plugin
- `detekt` with custom `detekt.yml`
- `.editorconfig` rules:
  - `max_line_length = 140`
  - trailing commas allowed
  - function/property naming lint relaxations for Compose/Android patterns

### Coverage and Quality Gates

- JaCoCo enabled for debug unit tests (`testDebugUnitTestCoverage` task)
- CI (`.github/workflows/android.yml`) runs:
  - `:app:ktlintCheck`
  - `:app:detekt`
  - `assembleDebug`
  - `:app:testDebugUnitTest`

## Architecture (What Exists Today)

This project follows a pragmatic layered structure with strong MVVM influence, not strict Clean Architecture.

Observed layers:

- **UI layer** (`ui/`, `MainActivity`, Compose screens)
- **Presentation layer** (`viewmodel/`)
- **Domain-ish orchestration** (`repository/`)
- **Data layer** (`client/`, `data/`, Room DAO/entities)
- **Platform/service layer** (`services/`, `observer/`)

Important architectural characteristics:

- Dependency injection is **manual** (ViewModel factories + direct construction in Composables/Activity).
- No Hilt/Dagger/Koin in use.
- Network models (`models/*Response`) are mapped to DB/domain entities in ViewModel logic.
- Room + Retrofit are the primary state sources; no separate domain module/use-case classes.

## Package Organization

Under `app/src/main/java/es/mundodolphins/app`:

- `client/`: Retrofit service interfaces and API client singleton
- `data/`: Room database, converters, entities, DAO
- `models/`: network response DTOs
- `repository/`: DB interaction and playback progress update logic
- `viewmodel/`: app state + orchestration for UI
- `services/`: foreground audio service using ExoPlayer
- `observer/`: connectivity observer (ConnectivityManager callback)
- `ui/`: routes, bars, theme, and Compose screens

## Typical Data Flows

### Feed Refresh (Episodes)

1. `MundoDolphinsScreen` triggers `EpisodesViewModel.refreshDatabase()`.
2. `EpisodesViewModel` calls `FeedService.getAllSeasons()`.
3. For each season filename, calls `getSeasonEpisodes(season)`.
4. Maps `EpisodeResponse` -> `Episode`.
5. Inserts only non-existing IDs into Room via `EpisodeRepository`.
6. UI reads `statusRefresh` and `feed` Flow to render loading/success/error/empty states.

### Episode Detail + Playback

1. Navigation route passes episode ID.
2. `EpisodesViewModel.getEpisode(id)` exposes selected episode via Flow.
3. `EpisodeScreen` creates `PlayerViewModel` (manual factory).
4. `AudioPlayerView` initializes foreground audio service.
5. On dispose/save, playback position is persisted through `EpisodeRepository.updateEpisodePosition`.
6. Episode listening status transitions:
   - `NOT_LISTENED`
   - `LISTENING`
   - `LISTENED`

### Articles

1. Articles route calls `ArticlesViewModel.fetchArticles()`.
2. `ArticlesService.getArticles()` returns list directly (no `Response<T>` wrapper in current API contract).
3. UI renders article list from `StateFlow`.
4. Detail screen resolves article by `publishedTimestamp` and renders markdown text.

## State Management Patterns

- Compose + `collectAsState` for Flows.
- `mutableStateOf` in ViewModel for enum/UI status.
- `MutableStateFlow` for article lists.
- Some `LiveData` still used for player service state.
- No MVI reducer/store pattern currently.

## Conventions You Should Preserve

### Naming and Structure

- Keep package namespace rooted at `es.mundodolphins.app`.
- `*ViewModel`, `*Repository`, `*Service`, `*Dao`, `*Response` suffixes are consistently used.
- Compose screen functions use PascalCase (`EpisodeScreen`, `EpisodesScreen`).

### Nullability

- Prefer explicit nullable types at boundaries (`Episode?`, `ArticlesResponse?`).
- Use safe access (`?.`) and sensible fallbacks in UI.
- Do not force unwrap (`!!`) unless absolutely unavoidable.

### Coroutines and Threading

- Use coroutines for async/network/DB work.
- Use injected dispatchers in ViewModels when testability matters.
- Keep blocking work off main thread.

### Error Handling

- Catch and log recoverable failures in repository/viewmodel.
- Keep UI state consistent on errors (`LoadStatus.ERROR`).
- Crashlytics logging is used in production paths; tests may run without Firebase runtime setup.

### UI State

- `EpisodesViewModel.LoadStatus` drives feed screen states.
- Maintain deterministic state transitions for loading/error/empty/success.

### Persistence

- `Episode.id` is derived from publication timestamp (`epochMilli`).
- Preserve this ID contract unless a full migration is planned.
- Preserve listening progress/status semantics used by repository + player.

## Testing Strategy in This Repo

Current tests are broad and mixed:

- **Unit tests** for ViewModels, repository, utilities.
- **Robolectric-based JVM tests** for Compose UI and Room DAOs.
- **MockWebServer tests** for Retrofit service behavior/paths.
- **Instrumented tests** under `androidTest` for app context/activity launch smoke checks.

Key frameworks/libraries:

- JUnit4 (main test framework)
- Robolectric
- MockK
- Truth and AssertJ assertions
- kotlinx-coroutines-test
- MockWebServer
- Compose UI test JUnit4 APIs

Test support utilities:

- `MainDispatcherRule` for deterministic `Dispatchers.Main` substitution
- fake/test doubles (`FakeEpisodesViewModel`, `TestEpisodeDao`, fake feed services)

Recommended existing execution command:

- `./gradlew :app:testDebugUnitTest`

## Implicit Architectural Decisions (Important)

These are not always explicitly documented in code comments, but are real project decisions:

- Single-module app; no modularized clean-domain split.
- Manual dependency wiring instead of DI framework.
- Room is canonical local source for episodes.
- Retrofit client is singleton (`MundoDolphinsClient`) with fixed base URL.
- UI is Compose-first with Navigation Compose.
- Audio playback is foreground service + Media3, not in-activity player logic.
- Static analysis (`ktlint`, `detekt`) is part of CI and should remain green.

## Key Constraints That Must Not Be Broken

- Do not break API route contracts (`seasons.json`, `season_{id}.json`, `articles.json`).
- Do not change `Episode.id` derivation semantics without migration strategy.
- Do not move playback logic into `Activity`; keep it service-based.
- Do not bypass repository/DAO when persisting listening progress.
- Do not introduce incompatible threading (network/DB work on main thread).
- Do not add libraries/frameworks (especially DI frameworks) without explicit project decision.

## Known Ambiguities and Current State Gaps

These are intentional notes for agents to avoid wrong assumptions:

- `Article`/`ArticleDao` exist in data layer and tests, but production `AppDatabase` currently includes only `Episode` entity.
- No explicit domain/use-case layer exists.
- Some side effects are triggered directly from Composables (for example fetch/refresh calls in navigation destinations).
- Firebase and Remote Config initialization happens from UI entry path, which complicates some tests.

When proposing refactors, treat these as migration topics, not silent assumptions.

## Anti-Patterns to Avoid in Future Changes

- Adding business logic directly into `Activity`/screen Composables.
- Adding blocking I/O on main thread.
- Creating hidden global state outside ViewModel/repository responsibilities.
- Introducing new architecture frameworks (Hilt/MVI/etc.) incrementally without an agreed migration plan.
- Duplicating API mapping logic across UI files instead of centralizing in ViewModel/repository.
- Bypassing static analysis and tests to make changes pass quickly.

## Guidance for AI Agents Contributing Code

When generating code in this repo:

1. Respect existing package boundaries and naming.
2. Prefer extending current MVVM + repository patterns.
3. Reuse existing dependencies; avoid introducing new ones.
4. Add/update tests together with behavior changes.
5. Keep coroutine usage testable (inject dispatchers where appropriate).
6. Keep Compose UI declarative and state-driven.
7. Run or target CI-equivalent checks (`ktlint`, `detekt`, unit tests).

## Assumptions Used for This Document

- The app is an Android Kotlin project (validated by Gradle/build files).
- Stack/architecture statements are based on repository code present at generation time.
- No undocumented external module or private architectural RFC was available in repo root.
