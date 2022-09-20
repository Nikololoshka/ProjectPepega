plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
}

android {
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
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

    namespace = "com.vereshchagin.nikolay.stankinschedule.core"
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}

hilt {
    enableAggregatingTask = true
}

dependencies {
    // Core
    implementation(libs.androidx.core)

    // Compose
    implementation(libs.bundles.compose)
    implementation(libs.compose.navigation)
    implementation(libs.compose.material3)
    implementation(libs.compose.paging)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.indicators)

    // UI
    implementation(libs.ui.material)

    // Components
    implementation(libs.androidx.paging)
    implementation(libs.androidx.preference)
    implementation(libs.integration.browser)
    api(libs.other.joda)

    // Network
    implementation(libs.bundles.network)

    // DI
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}