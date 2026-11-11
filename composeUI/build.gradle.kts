plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    jvmToolchain(17)

    // تمام اینڈرائیڈ اور SDK کی سیٹنگز یہاں اندر آئیں گی
    androidTarget {
        namespace = "org.example.app.androidApp"
        compileSdk = 36
        minSdk = 23

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }
    
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared"))

            api(libs.compose.runtime)
            api(libs.compose.ui)
            api(libs.compose.foundation)
            api(libs.compose.resources)
            api(libs.compose.ui.tooling.preview)
            api(libs.compose.material3)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime)
            implementation(libs.coil)
            implementation(libs.coil.network.ktor)
            implementation(libs.icons.lucide)

            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.compose.ui.test)
            implementation(libs.kotlinx.coroutines.test)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

dependencies {
    // KMP پروجیکٹ میں debugImplementation کی جگہ androidDebugImplementation استعمال ہوتا ہے
    androidDebugImplementation(libs.compose.ui.tooling)
}
