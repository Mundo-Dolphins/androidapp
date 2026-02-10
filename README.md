# 🐬 Mundo Dolphins Podcast App

[![Build Status](https://github.com/Mundo-Dolphins/androidapp/actions/workflows/android.yml/badge.svg)](https://github.com/Mundo-Dolphins/androidapp/actions/workflows/android.yml)
[![Version](https://img.shields.io/badge/version-2.0.1-blue.svg)](https://github.com/Mundo-Dolphins/androidapp/releases)
[![Android API](https://img.shields.io/badge/Android-26%2B-brightgreen.svg)](https://www.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.10-purple.svg)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

[Google Play Store](https://play.google.com/store/apps/details?id=es.mundodolphins.app) |
[Website](https://mundodolphins.es) |
[Issues](https://github.com/Mundo-Dolphins/androidapp/issues) |
[Releases](https://github.com/Mundo-Dolphins/androidapp/releases)

## 📱 About

**Mundo Dolphins** is the official app for the [Mundo Dolphins](https://mundodolphins.es) podcast, your ultimate source for Miami Dolphins content in Spanish.

With this application you can:

- 🎙️ **Listen to Episodes**: Access all podcast episodes from all seasons
- 📺 **Watch Videos**: Enjoy exclusive video content about the Dolphins
- 📰 **Read News**: Read articles and analysis of the latest team events
- ⏸️ **Smart Playback**: Save your listening progress and resume where you left off
- 🔍 **Explore Seasons**: Browse all seasons and their organized chapters

The app offers a seamless and personalized experience to follow all Miami Dolphins coverage in one place.

## ✨ Features

- ✅ **Native Audio Playback**: Episode streaming with Media3/ExoPlayer
- ✅ **Progress Management**: Automatically save your listening position
- ✅ **Modern Interface**: Responsive design with Jetpack Compose and Material Design 3
- ✅ **Offline Support**: Data stored locally with Room Database
- ✅ **Real-time Synchronization**: Get the latest news and episodes automatically
- ✅ **Integrated Analytics**: Firebase Analytics to better understand your experience
- ✅ **Robust Connectivity Handling**: Automatic network change detection
- ✅ **Optimized Performance**: Compiled with ProGuard for optimized releases

## 🛠️ Tech Stack

### Architecture and Development

- **Language**: Kotlin 2.3.10
- **Framework**: Android (API 26-36)
- **Architecture**: MVVM with Repository Pattern
- **UI**: Jetpack Compose + Material Design 3
- **Dependency Injection**: Manual (ViewModelFactory)

### Data and Networking

- **Local Database**: Room ORM 2.8.4
- **Networking**: Retrofit 3.0.0 + Gson

### Multimedia

- **Audio Playback**: AndroidX Media3 ExoPlayer 1.9.2
- **Background Service**: Foreground Service
- **Images**: Coil 3.3.0

### Backend

- **Analytics**: Firebase Analytics
- **Remote Config**: Firebase Remote Config
- **Crash Reporting**: Firebase Crashlytics
- **Google Services Plugin**: v4.4.4

### Tools and Testing

- **Build Tool**: Gradle 9.0.0 with Kotlin DSL
- **Testing**: JUnit4, Robolectric 4.16.1
- **Mocking**: MockK 1.14.9
- **API Testing**: MockWebServer 5.3.2
- **Assertions**: AssertJ 3.27.7, Truth 1.4.5
- **Static Analysis**: ktlint 14.0.1, detekt 1.23.8
- **Coverage**: JaCoCo

## 📋 Requirements

- Android 8.0+ (API level 26)
- JDK 17+
- Gradle 9.0.0+
- Android Studio Koala or higher (recommended)

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/Mundo-Dolphins/androidapp.git
cd androidapp
```

### 2. Install Dependencies

The project uses Gradle with Version Catalog. Dependencies will be downloaded automatically:

```bash
./gradlew build
```

### 3. Configure Google Services

Place your `google-services.json` file in the `app/` folder:

```bash
cp /path/to/your/google-services.json app/
```

### 4. Build the App

```bash
./gradlew assembleDebug
```

### 5. Run on Emulator or Device

```bash
./gradlew installDebug
```

Or open the project in Android Studio and press Run.

## 📂 Project Structure

```text
app/src/main/java/es/mundodolphins/app/
├── client/              # Retrofit services and HTTP client
├── data/                # Room Database, DAOs, and entities
├── models/              # API response DTOs
├── repository/          # Repository layer and orchestration
├── viewmodel/           # MVVM ViewModels
├── services/            # Audio playback service
├── observer/            # Connectivity observers
└── ui/                  # Compose screens, navigation and theme
```

### Main Data Flows

**Episodes Feed**:

```text
UI → ViewModel → Repository → Retrofit → Room
```

**Audio Playback**:

```text
EpisodeScreen → PlayerViewModel → AudioPlayerService (Media3/ExoPlayer)
```

**Progress Synchronization**:

```text
PlayerViewModel → Repository → AudioPlayerService → Room
```

## 🧪 Testing

The project includes a comprehensive test suite with multiple levels:

### Run Unit Tests

```bash
./gradlew test
```

### Run Instrumented Tests

```bash
./gradlew connectedAndroidTest
```

### Code Coverage

```bash
./gradlew testDebugUnitTestCoverage
```

Reports will be generated in `app/build/reports/`.

### Testing Stack

- **Framework**: JUnit4 + Robolectric
- **Mocking**: MockK
- **Assertions**: Truth, AssertJ
- **Coroutines**: kotlinx-coroutines-test
- **HTTP Mocking**: MockWebServer
- **Database**: Room Testing Library

## 🔍 Static Analysis

The project maintains high quality standards with automatic analysis:

### Run ktlint

```bash
./gradlew ktlintCheck
./gradlew ktlintFormat  # Auto-fix
```

### Run detekt

```bash
./gradlew detekt
```

Both tools are part of CI and will fail if there are violations.

## 📤 Build and Releases

### Debug Build

```bash
./gradlew assembleDebug
```

The APK is generated in `app/build/outputs/apk/debug/`.

### Release Build

```bash
./gradlew assembleRelease
```

The signed APK is generated in `app/release/`.

**Note**: You'll need a valid keystore configured in `local.properties`.

### Google Play

The application is distributed through:

- **Google Play Store**: [Mundo Dolphins](https://play.google.com/store/apps/details?id=es.mundodolphins.app)

## 🤝 Contributing Guide

Thank you for your interest in contributing to Mundo Dolphins! Here's how you can help.

### Before You Start

1. **Fork the repository** on GitHub
2. **Clone your fork** locally
3. **Create a branch** for your change: `git checkout -b feature/your-feature` or `git checkout -b fix/your-bug`
4. **Stay updated** with the `main` branch: `git fetch upstream && git rebase upstream/main`

### Development Workflow

1. **Read the existing code** to understand the MVVM architecture, Repository pattern, and project conventions
2. **Follow the guidelines** in [copilot-instructions.md](.github/copilot-instructions.md) to maintain consistency
3. **Write tests** for all new functionality or bug fixes
4. **Make sure everything compiles**:

   ```bash
   ./gradlew clean build
   ```

5. **Run static analysis**:

   ```bash
   ./gradlew ktlintCheck detekt
   ```

6. **Run tests**:

   ```bash
   ./gradlew test
   ```

### Code Standards

- **Language**: Kotlin, following [Kotlin style guide](https://kotlinlang.org/docs/coding-conventions.html)
- **Max line length**: 140 characters (see `.editorconfig`)
- **Naming conventions**:
  - Compose functions: `PascalCase`
  - Regular functions: `camelCase`
  - Constants: `UPPER_CASE`
- **Nullability**: Be explicit with nullable types (`Type?`) and use safe calls (`?.`)
- **Avoid**:
  - Force unwrap (`!!`)
  - Logic in Activities/Composables
  - Blocking calls on main thread
  - New external dependencies without justification

### Architecture to Respect

- **MVVM Pattern**: `UI → ViewModel → Repository → Data/Network`
- **No DI Framework**: Use `ViewModelFactory` as it already exists
- **Coroutines**: For all async/IO work, inject `Dispatcher`s
- **Room Database**: As source of truth for episodes
- **Playback**: In `AudioPlayerService`, not in Activities

### Creating a Pull Request

1. **Push your branch**: `git push origin your-branch`
2. **Open a PR** on GitHub against `main`
3. **Write a clear description**:
   - What change you're making
   - Why you're making it
   - How you tested it
4. **Wait for review**: CI checks must pass
5. **Respond to comments**: Iterate based on feedback

### Pre-PR Checklist

- [ ] My code follows the project's style guide
- [ ] I've run `ktlintCheck` and `detekt` without errors
- [ ] I've added/updated tests
- [ ] I've run `test` and all pass
- [ ] My branch is up to date with `main`
- [ ] My PR description is clear and complete

### Welcome Contribution Types

- 🐛 **Bugs**: Open an issue, then create a PR with a fix
- ✨ **Features**: Discuss first in an issue, then implement
- 📖 **Docs**: Improve README, inline comments, guides
- 🧪 **Tests**: Increase coverage and reliability
- 🔧 **Refactor**: Clean and improve existing code
- 🚀 **Performance**: Optimize where possible

### Reporting Bugs

1. **Verify** the bug hasn't already been reported
2. **Open an issue** with:
   - Clear problem description
   - Steps to reproduce
   - Expected vs. actual behavior
   - Device/OS information

### Requesting Features

1. **Open an issue** with the `enhancement` label
2. **Describe** the use case and desired functionality
3. **Wait for feedback** from maintainers
4. **Implement** if approved

## 📋 Architecture Conventions and Decisions

For more details on architectural decisions, patterns, and important project constraints, see:

- **[agents.md](agents.md)**: Detailed architecture, data flows, key decisions
- **[.github/copilot-instructions.md](.github/copilot-instructions.md)**: Contributors guide

## 📱 API Endpoints

The app consumes data from `https://mundodolphins.es/api/`:

- `GET /seasons.json`: List of seasons
- `GET /season_{id}.json`: Episodes of a season
- `GET /articles.json`: Articles and news

See [client/](app/src/main/java/es/mundodolphins/app/client/) for Retrofit details.

## 🔐 Security

- **Proguard**: Enabled in release builds for obfuscation
- **Firebase Crashlytics**: Monitors crashes in production
- **Network Security**: Endpoints secured by HTTPS

## 📞 Contact and Community

- **Website**: [mundodolphins.es](https://mundodolphins.es)
- **Google Play**: [Mundo Dolphins App](https://play.google.com/store/apps/details?id=es.mundodolphins.app)
- **GitHub Issues**: [Report bugs or request features](https://github.com/Mundo-Dolphins/androidapp/issues)
- **GitHub Discussions**: [Community discussions](https://github.com/Mundo-Dolphins/androidapp/discussions)

## 📄 License

This project is under MIT license.

---

**Made with ❤️ for Miami Dolphins fans** 🐬

[⬆ Back to top](#-mundo-dolphins-podcast-app)
