plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")

    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
}

android {
    compileSdk = AppConfig.compileSdk
    namespace = "com.vereshchagin.nikolay.stankinschedule.core.data"

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
}

kapt { correctErrorTypes = true }
hilt { enableAggregatingTask = true }

dependencies {
    implementation(project(":domain:core"))

    // Kotlin
    implementation(libs.androidx.core)

    implementation(libs.androidx.paging)
    implementation(libs.androidx.preference)

    implementation(libs.bundles.network)

    // DI
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}