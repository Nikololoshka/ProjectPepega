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
    namespace = "com.vereshchagin.nikolay.stankinschedule.schedule.widget.data"

    defaultConfig {
        minSdk = appMinSdkVersion
        targetSdk = appTargetSdkVersion

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
    implementation(project(":domain:schedule-core"))
    implementation(project(":domain:schedule-widget"))

    // Kotlin
    implementation(libs.androidx.core)

    // Components
    implementation(libs.androidx.datastore)

    // DI
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}