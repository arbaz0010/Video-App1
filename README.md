# KMP Template

> A clean Kotlin Multiplatform starter that runs on **Android, Desktop, and iOS** — Compose UI on the JVM side, native SwiftUI on iOS, all sharing one Kotlin core.

You get an app that builds and runs on all three platforms in one `git clone`, with the wiring decisions already made: MVI stores in `shared/`, Compose Material 3 theme, Koin DI, Ktor + serialization for networking, SKiE-bridged Swift APIs. No backend, no opinions on persistence — bring your own.

```
┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│   Android (JVM)  │  │   Desktop (JVM)  │  │     iOS (Swift)  │
│   Compose UI     │  │   Compose UI     │  │     SwiftUI      │
└────────┬─────────┘  └────────┬─────────┘  └────────┬─────────┘
         │                     │                     │
         └─────────────────────┼─────────────────────┘
                               ▼
                  ┌──────────────────────────┐
                  │  shared/  (Kotlin)       │
                  │  • MVI stores            │
                  │  • Koin DI               │
                  │  • Ktor + datetime       │
                  │  • Navigation routes     │
                  └──────────────────────────┘
```

---

## Quick start

**You'll need:** JDK 17, Android SDK (compileSdk 36 / minSdk 24), Xcode 15+ for iOS. The Gradle wrapper handles everything else.

```bash
# 1. Get the code
git clone <your-fork> myapp && cd myapp

# 2. Run on Desktop (fastest feedback)
./gradlew :desktopApp:run

# 3. Run on Android
#    Open in Android Studio → run :androidApp
#    or:
./gradlew :androidApp:installDebug

# 4. Run on iOS
open iosApp/iosApp.xcodeproj
#    then press ⌘R in Xcode
```

The first iOS build takes ~30s longer because it compiles the Kotlin framework (`Shared.framework`) and embeds it. Subsequent builds are incremental.

---

## What you get out of the box

| Piece | Where it lives | What it does |
|---|---|---|
| **MVI store** | `shared/.../core/mvi/MviStore.kt` | One base class. Subclass with your `State`, `Intent`, and `Effect` types. Flows + structured concurrency, no extra deps. |
| **DI graph** | `shared/.../core/di/AppModule.kt` | Koin. One `initKoin()` call from each platform, modules composed per feature. |
| **Theme** | `composeUI/.../theme/` | Material 3 light + dark, Inter font, design tokens (`AppTokens`). Mirrored on iOS as `AppColors` / `AppTokens` / `AppTypography`. |
| **Navigation** | `shared/.../navigation/AppRoutes.kt` | `@Serializable` route objects. Plug into compose-nav3 or roll your own — the chassis is here. |
| **iOS bridge** | `shared/src/iosMain/.../KoinHelper.kt` | SKiE generates Swift-friendly accessors. Get any store from Swift via `KoinHelper.shared.<name>Store`. |
| **Networking** | declared in `libs.versions.toml` | Ktor 3 (core, content negotiation, logging, JSON serialization, platform engines: OkHttp on JVM, Darwin on iOS). |

---

## Project layout

```
.
├── shared/                  Kotlin Multiplatform module — no UI, no Compose
│   └── src/
│       ├── commonMain/      Cross-platform business logic
│       │   └── kotlin/org/example/app/
│       │       ├── core/mvi/        MviStore base class
│       │       ├── core/di/         CoreModule, AppModule (initKoin)
│       │       ├── feature/<name>/  <Name>Store, <Name>Module
│       │       └── navigation/      AppRoute sealed interface
│       ├── androidMain/     Android-specific (e.g. OkHttp engine)
│       ├── iosMain/         iOS-specific (KoinHelper for Swift)
│       └── jvmMain/         Desktop JVM
│
├── composeUI/               Compose Multiplatform UI shared by Android + Desktop
│   └── src/
│       ├── commonMain/
│       │   ├── composeResources/    Inter font, strings.xml
│       │   └── kotlin/org/example/app/
│       │       ├── theme/           AppTheme, Tokens, Type, Color, Shape
│       │       ├── core/mvi/        CollectSideEffects helper
│       │       ├── feature/<name>/ui/  <Name>Screen.kt
│       │       └── App.kt           Root composable, route switch
│       └── androidMain, jvmMain     Platform-specific composables (rare)
│
├── androidApp/              Android entry point only
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── kotlin/.../AppActivity.kt   one Activity, calls App()
│       └── res/values/strings.xml
│
├── desktopApp/              Desktop entry point only
│   └── src/main/kotlin/main.kt         Window { App() }
│
└── iosApp/                  Native SwiftUI app
    ├── iosApp.xcodeproj/
    └── iosApp/
        ├── iOSApp.swift                @main App, calls KoinHelper
        ├── Features/<Name>/<Name>View.swift
        └── Theme/                      AppColors, AppTokens, AppTypography
```

---

## Adding a feature

This is the core loop. Pretend you're adding a `Counter` feature.

### 1. Define state, intents, effects, and a store

`shared/src/commonMain/kotlin/org/example/app/feature/counter/presentation/CounterStore.kt`

```kotlin
package org.example.app.feature.counter.presentation

import org.example.app.core.mvi.MviStore

data class CounterState(val count: Int = 0)

sealed interface CounterIntent {
    data object Increment : CounterIntent
    data object Reset : CounterIntent
}

sealed interface CounterEffect {
    data class ShowToast(val text: String) : CounterEffect
}

class CounterStore : MviStore<CounterState, CounterIntent, CounterEffect>(CounterState()) {
    override suspend fun handleIntent(state: CounterState, intent: CounterIntent) {
        when (intent) {
            CounterIntent.Increment -> updateState { copy(count = count + 1) }
            CounterIntent.Reset -> {
                updateState { copy(count = 0) }
                postEffect(CounterEffect.ShowToast("Reset"))
            }
        }
    }
}
```

### 2. Register it with Koin

`shared/.../feature/counter/di/CounterModule.kt`

```kotlin
package org.example.app.feature.counter.di

import org.example.app.feature.counter.presentation.CounterStore
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val counterModule = module {
    factoryOf(::CounterStore)
}
```

Wire it into `AppModule.kt`:

```kotlin
fun initKoin() {
    startKoin {
        modules(coreModule, homeModule, counterModule)   // ← added
    }
}
```

### 3. Add a route

`shared/.../navigation/AppRoutes.kt`:

```kotlin
sealed interface AppRoute {
    @Serializable data object Home : AppRoute
    @Serializable data object Counter : AppRoute   // ← added
}
```

### 4. Build the Compose screen

`composeUI/.../feature/counter/ui/CounterScreen.kt`

```kotlin
@Composable
fun CounterScreen() {
    val store = remember { GlobalContext.get().get<CounterStore>() }
    val state by store.state.collectAsState()

    CollectSideEffects(store.effects) { effect ->
        when (effect) {
            is CounterEffect.ShowToast -> { /* show snackbar */ }
        }
    }

    Column { /* render state.count, dispatch intents */ }
}
```

Branch on the new route in `App.kt`:

```kotlin
when (route) {
    AppRoute.Home -> HomeScreen()
    AppRoute.Counter -> CounterScreen()
}
```

### 5. Expose to iOS

In `shared/src/iosMain/.../KoinHelper.kt`:

```kotlin
val counterStore: CounterStore get() = get()
```

Then build a `CounterView.swift` under `iosApp/iosApp/Features/Counter/`, drag it into the Xcode project's `Features` group, and observe the store:

```swift
import SwiftUI
import Shared

struct CounterView: View {
    @State private var count: Int32 = 0
    private let store = KoinHelper.shared.counterStore

    var body: some View {
        VStack {
            Text("\(count)")
            Button("Increment") {
                store.dispatch(intent: CounterIntent.Increment.shared)
            }
        }
        .task {
            for await s in store.state {
                count = s.count
            }
        }
    }
}
```

That's the whole loop. Same store, three UIs.

---

## Rebranding the template

The template ships as `org.example.app` with display name `App`. To make it yours:

```bash
NEW_PKG=com.acme.foo        # your reverse-DNS package
NEW_NAME=Foo                # display name + bundle suffix

NEW_DIR=$(echo $NEW_PKG | tr '.' '/')

# Replace package strings in source files
find . -type f \( -name "*.kt" -o -name "*.kts" -o -name "*.swift" \
                 -o -name "*.xml" -o -name "*.toml" -o -name "*.plist" \
                 -o -name "*.pbxproj" \) \
  ! -path "./.gradle/*" ! -path "./build/*" ! -path "./.git/*" \
  -exec sed -i '' \
    -e "s/org\.example\.app/$NEW_PKG/g" \
    -e "s|org/example/app|$NEW_DIR|g" {} +

# Move the package directories
for d in $(find . -type d -path "*/org/example/app" ! -path "./build/*" ! -path "./.git/*"); do
  parent=$(dirname $d)
  mkdir -p "$parent/$NEW_DIR"
  mv "$d"/* "$parent/$NEW_DIR/"
  rm -rf "$parent/org/example"
done

# Display name
sed -i '' "s/>App</>$NEW_NAME</g" \
  androidApp/src/main/res/values/strings.xml \
  composeUI/src/commonMain/composeResources/values/strings.xml
sed -i '' "s/rootProject.name = \"App\"/rootProject.name = \"$NEW_NAME\"/" settings.gradle.kts
```

Then close + reopen Android Studio / Xcode so they re-index.

---

## Troubleshooting

**"No such module 'Shared'" in Xcode.**
The Kotlin framework hasn't been built yet. Run `./gradlew :shared:embedAndSignAppleFrameworkForXcode` once, or just rebuild in Xcode (the run-script phase does it for you).

**Gradle says it can't find a JDK.**
Set `JAVA_HOME` to a JDK 17. The wrapper requires it; toolchains downstream from there will be auto-fetched.

**iOS build complains about code signing.**
Open the Xcode target → Signing & Capabilities → set a development team. The template ships with `Sign to Run Locally` which works for the simulator but not a device.

**Compose build is slow.**
First builds compile the Compose compiler against your Kotlin version. Subsequent builds are incremental and fast. If it stays slow, check `gradle.properties` — `org.gradle.caching=true` and `configuration-cache=true` are on by default.

**Kotlin/Native link error about duplicate `Instant` symbols.**
This was a known issue with `kotlinx-datetime` ↔ supabase-kt mixing versions. The template pins `kotlinx-datetime = "0.7.1"` to avoid it. If you bump versions, you may need a `resolutionStrategy.eachDependency` block.

---

## What's intentionally NOT in the template

- **No backend.** Bring your own (Supabase, Firebase, your custom REST/GraphQL).
- **No persistence.** Add Room, SQLDelight, or DataStore when you know what you need.
- **No analytics, auth, or remote config.** Project-specific decisions.
- **No web/JS/wasm target.** Add `js { browser() }` and `wasmJs { browser() }` to module `kotlin {}` blocks if you need them, plus a `webApp` module.
- **No CI config.** The repo has the structure to support it; pick GitHub Actions / GitLab CI / etc. yourself.

These are subtractions, not omissions — every one of them is a project decision.

---

## Conventions worth keeping

- **One feature, one folder, three layers.** `feature/<name>/{di,presentation,ui}` — never split a feature across the tree.
- **Stores never reach for `MainScope` directly.** `MviStore` already gives you a scope. Cancel via `destroy()`.
- **No `Activity`/`UIView` references in `commonMain`.** If you need them, declare an `expect`/`actual`.
- **Theme tokens, not ad-hoc dp values.** `AppTokens.cardPadding` over `20.dp` everywhere except inside `AppTokens.kt`.
- **Routes are `@Serializable data object`s.** Compose-nav3 and stable equality both depend on this.

---

## License

MIT © 2026 Kashif Mehmood — see [LICENSE](LICENSE).
