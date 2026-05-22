# WishListApp 🎁

A modern, production-ready Android application for managing personal wishes, goals, and shopping lists. Built with the latest Android technologies and Clean Architecture principles.

## 🚀 Key Features

*   **Wish Management**: Add, edit, and track your wishes with titles, descriptions, prices, and categories.
*   **Smart Search**: Integrated with [DummyJSON](https://dummyjson.com/) API to provide product suggestions while adding new items.
*   **Pinterest-style UI**: A beautiful staggered grid layout for a visual and engaging experience.
*   **Budget Planning**: Set a total budget and track your spending progress in real-time.
*   **Statistics**: Detailed insights into your achievements, including total cost analysis and completion rates.
*   **Dark Mode**: Full support for system-wide dark and light themes.
*   **Localization Ready**: Cleanly structured for multi-language support (currently English).
*   **Swipe to Action**: Easily manage completed items with intuitive swipe gestures.

## 🛠 Tech Stack

*   **Language**: Kotlin
*   **UI Framework**: Jetpack Compose (100%)
*   **Dependency Injection**: Hilt
*   **Local Database**: Room (with production-safe migrations)
*   **Networking**: Retrofit + OkHttp (configured with timeouts and Result handling)
*   **Persistence**: Jetpack DataStore (for user preferences)
*   **Image Loading**: Coil
*   **Architecture**: Clean Architecture (UI, Domain, Data layers) + MVVM
*   **Asynchrony**: Kotlin Coroutines & Flow

## 🏗 Architecture Overview

The project follows strict **Clean Architecture** principles to ensure scalability and testability:

-   **Data Layer**: Handles Room database, DataStore, and Retrofit API integration. Includes Mappers to transform DTOs/Entities to Domain models.
-   **Domain Layer**: Contains business logic, repository interfaces, and pure Kotlin models.
-   -   **UI Layer**: Feature-based organization using ViewModels that expose immutable `UiState` via `StateFlow`.

## 📦 Setup & Installation

1.  Clone this repository.
2.  Open the project in **Android Studio Ladybug (or newer)**.
3.  Ensure you have **JDK 17** configured in your Gradle settings.
4.  Build the project and run it on an emulator or physical device (min SDK 24).

## 🛡 Production Readiness

This app is optimized for production use:
-   **R8/ProGuard**: Enabled for code shrinking and obfuscation.
-   **Resource Handling**: Safe `Resource` wrapper for all network and database operations.
-   **Build Performance**: Optimized Gradle configuration with parallel execution enabled.
-   **UI Stability**: Stable keys used in Lazy layouts and robust state management.

---
*Created with ❤️ using modern Android development best practices.*
