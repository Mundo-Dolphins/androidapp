# Copilot Instructions for MundoDolphins

## Scope
- Use Kotlin for all production and test code.
- Target Android app module `:app`.
- Follow existing package root: `es.mundodolphins.app`.

## Architecture (Always Follow)
- Keep MVVM-style layering: `ui -> viewmodel -> repository -> data/client`.
- Keep playback in `AudioPlayerService`; do not move playback logic into `Activity`.
- Keep manual dependency wiring with existing `ViewModelFactory` patterns.
- Do not introduce Hilt/Dagger/Koin unless explicitly requested.

## Code Style
- Follow Kotlin official style and existing project formatting.
- Keep Compose functions in PascalCase.
- Keep lines under 140 chars when practical.
- Prefer immutable values (`val`) and explicit nullability.
- Avoid `!!`; use safe calls and explicit fallbacks.

## Concurrency and Data
- Use coroutines for async work.
- Keep network and Room operations off the main thread.
- Inject dispatchers for testable ViewModel code.
- Expose UI state via `Flow`/`StateFlow`/Compose state as already used.

## Networking and Persistence
- Reuse Retrofit services in `client/` and existing DTOs in `models/`.
- Preserve endpoint contracts: `seasons.json`, `season_{id}.json`, `articles.json`.
- Keep `Episode.id` derived from publication timestamp unless a migration is requested.
- Persist listening progress via `EpisodeRepository` and `EpisodeDao`.

## UI Rules
- Keep screens declarative and state-driven.
- Keep navigation in `MainNavigation`/`Routes` patterns.
- Do not put business rules in Activities.
- Do not call blocking APIs from Composables.

## Testing Rules
- Add or update tests for every behavior change.
- Use existing test stack: JUnit4, Robolectric, MockK, Truth/AssertJ, MockWebServer, coroutines-test.
- For DAO tests, use in-memory Room + Robolectric style used in repo.
- For ViewModel tests, use fakes/mocks and deterministic dispatchers.
- Prefer focused tests; avoid full `MainActivity` initialization in unit tests when unnecessary.

## Static Analysis and CI
- Keep `ktlint` and `detekt` clean.
- Do not disable rules to make changes pass.
- Keep code compatible with CI tasks:
  - `:app:ktlintCheck`
  - `:app:detekt`
  - `:app:testDebugUnitTest`

## Explicitly Avoid
- Logic-heavy Activities.
- Direct `Context` usage in repository/data/domain layers.
- New dependencies without strong justification.
- Blocking calls (`Thread.sleep`, synchronous network/db on main thread).
- Architectural rewrites mixed with feature changes.
