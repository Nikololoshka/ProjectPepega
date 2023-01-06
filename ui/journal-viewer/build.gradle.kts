plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")

    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
}

android {
    compileSdk = AppConfig.compileSdk
    namespace = "com.vereshchagin.nikolay.stankinschedule.journal.viewer.ui"

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

kapt { correctErrorTypes = true }
hilt { enableAggregatingTask = true }

dependencies {
    implementation(project(":ui:core"))
    implementation(project(":domain:core"))
    implementation(project(":domain:journal-core"))

    // Kotlin
    implementation(libs.androidx.core)

    // Jetpack Compose & Material 3
    implementation(libs.bundles.compose)
    implementation(libs.compose.material3)
    implementation(libs.compose.paging)

    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.swiperefresh)

    // UI
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.androidx.paging)
    implementation(libs.ui.material)

    // Worker
    implementation(libs.work.runtime)
    implementation(libs.work.hilt)
    kapt(libs.work.hiltCompiler)

    // DI
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}