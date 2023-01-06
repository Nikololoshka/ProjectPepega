plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = AppConfig.compileSdk
    namespace = "com.vereshchagin.nikolay.stankinschedule.core.ui"

    defaultConfig {
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    implementation(project(":domain:core"))

    // Kotlin
    implementation(libs.androidx.core)

    // Jetpack Compose & Material 3
    implementation(libs.bundles.compose)
    implementation(libs.compose.material3)
    implementation(libs.compose.paging)
    implementation(libs.compose.coil)
    implementation(libs.compose.activity)

    implementation(libs.accompanist.permission)
    implementation(libs.ui.material)
    implementation(libs.androidx.paging)

    // Components
    implementation(libs.integration.browser)
}