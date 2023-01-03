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

    namespace = "com.vereshchagin.nikolay.stankinschedule.schedule.table.ui"
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}

hilt {
    enableAggregatingTask = true
}

dependencies {
    implementation(project(":ui:core"))
    implementation(project(":domain:core"))
    implementation(project(":domain:schedule-core"))
    implementation(project(":domain:schedule-table"))

    // Core
    implementation(libs.androidx.core)

    // Jetpack Compose & Material 3
    implementation(libs.bundles.compose)
    implementation(libs.compose.activity)
    implementation(libs.compose.material3)

    // Components
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.ui.material)

    // DI
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}