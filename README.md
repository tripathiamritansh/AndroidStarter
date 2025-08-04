# Android Starter Project

This is a modern Android project template configured with all the essential dependencies for Android development.

## ⚡ Features

### Architecture & Dependencies
- **Jetpack Compose** - Modern declarative UI toolkit
- **Hilt** - Dependency injection framework
- **Navigation Component** - Navigation for Compose and Fragments  
- **Retrofit** - Type-safe HTTP client
- **OkHttp** - HTTP client with logging interceptor
- **Moshi** - JSON serialization/deserialization
- **Coil** - Image loading library for Compose
- **Room** - Local database
- **DataStore** - Modern data storage solution
- **Coroutines** - Asynchronous programming
- **ViewModel & LiveData** - MVVM architecture components

### Development & Testing
- **MockK** - Mocking library for Kotlin
- **Turbine** - Testing library for flows
- **Compose Testing** - UI testing for Compose
- **Espresso** - UI testing framework

## 🏗️ Project Structure

```
app/src/main/java/com/sample/starter/
├── StarterApplication.kt           # Application class with Hilt
├── MainActivity.kt                 # Main activity with Hilt integration
├── data/
│   └── model/
│       └── User.kt                 # Data models with Moshi annotations
├── di/
│   └── NetworkModule.kt            # Hilt dependency injection modules
├── network/
│   └── ApiService.kt               # Retrofit API interfaces
├── repository/
│   └── UserRepository.kt           # Repository pattern implementation
└── ui/
    ├── compose/
    │   ├── ExampleComposeActivity.kt   # Example Compose Activity
    │   ├── UserListViewModel.kt        # ViewModel with Hilt
    └── theme/
        ├── Theme.kt                    # Material3 theme
        └── Type.kt                     # Typography definitions
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or newer
- JDK 11 or higher
- Android SDK with API level 24+ (minSdk) and 35 (targetSdk)

### Setup
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run the app

### Key Files Updated
- `gradle/libs.versions.toml` - Version catalog with all dependency versions
- `build.gradle.kts` (Project level) - Plugin configuration
- `app/build.gradle.kts` - App-level dependencies and configuration
- `AndroidManifest.xml` - Application class and permissions

## 📱 Example Usage

### Hilt Dependency Injection
```kotlin
@HiltAndroidApp
class StarterApplication : Application()

@AndroidEntryPoint  
class MainActivity : AppCompatActivity()

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel()
```

### Retrofit with Moshi
```kotlin
@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String
)

interface ApiService {
    @GET("users")
    suspend fun getUsers(): Response<List<User>>
}
```

### Compose UI with Coil
```kotlin
@Composable
fun UserItem(user: User) {
    Card {
        Row {
            AsyncImage(
                model = user.avatar,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Text(text = user.name)
        }
    }
}
```

## 📦 Dependencies Overview

### Core Android
- androidx.core:core-ktx
- androidx.appcompat:appcompat
- com.google.android.material:material

### Jetpack Compose
- androidx.compose:compose-bom (Bill of Materials)
- androidx.compose.ui:ui
- androidx.compose.material3:material3
- androidx.activity:activity-compose

### Architecture Components
- androidx.lifecycle:lifecycle-viewmodel-ktx
- androidx.lifecycle:lifecycle-viewmodel-compose
- androidx.navigation:navigation-compose

### Dependency Injection
- com.google.dagger:hilt-android
- androidx.hilt:hilt-navigation-compose

### Networking
- com.squareup.retrofit2:retrofit
- com.squareup.retrofit2:converter-moshi
- com.squareup.okhttp3:okhttp
- com.squareup.okhttp3:logging-interceptor

### JSON & Serialization
- com.squareup.moshi:moshi
- com.squareup.moshi:moshi-kotlin

### Image Loading
- io.coil-kt:coil-compose

### Database
- androidx.room:room-runtime
- androidx.room:room-ktx

### Asynchronous Programming
- org.jetbrains.kotlinx:kotlinx-coroutines-android

## 🧪 Testing

The project includes comprehensive testing dependencies:
- JUnit for unit testing
- MockK for mocking
- Turbine for testing Flows
- Compose testing utilities
- Espresso for UI testing

## 🔧 Build Configuration

- **compileSdk**: 35
- **minSdk**: 24
- **targetSdk**: 35
- **JVM Target**: 11
- **Kotlin**: 2.0.21
- **AGP**: 8.10.0

## 📄 License

This project is a template/starter project for Android development.