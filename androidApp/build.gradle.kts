import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler) // Required if MainActivity calls setContent { }
}

android {
    namespace = "org.example.app.androidApp"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        targetSdk = 36

        applicationId = "org.example.app.androidApp"
        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures {
        compose = true // Enables Jetpack Compose in androidApp
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":composeUI"))
    implementation(project(":shared"))
    implementation(libs.androidx.activityCompose)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)

    // Tooling belongs in androidApp's debugImplementation, not in KMP modules
    debugImplementation(libs.compose.ui.tooling)
}
