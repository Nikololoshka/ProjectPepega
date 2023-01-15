plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")

    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
}

android {
    val appCompileSdkVersion: Int by rootProject.extra
    val appMinSdkVersion: Int by rootProject.extra
    val appTargetSdkVersion: Int by rootProject.extra

    compileSdk = appCompileSdkVersion
    namespace = "com.vereshchagin.nikolay.stankinschedule.news.viewer.ui"

    defaultConfig {
        minSdk = appMinSdkVersion
        targetSdk = appTargetSdkVersion

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
        dataBinding = true
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
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
    implementation(project(":domain:news-core"))

    // Core
    implementation(libs.androidx.core)

    // Compose
    implementation(libs.compose.coil)

    // Components
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.webkit)
    implementation(libs.ui.swiperefreshlayout)
    implementation(libs.ui.material)

    // Network
    implementation(libs.bundles.network)

    // DI
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}