plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName

        testInstrumentationRunner = AppConfig.androidTestInstrumentation
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
        sourceCompatibility = Versions.java
        targetCompatibility = Versions.java
    }
    kotlinOptions {
        jvmTarget = Versions.kotlinLevel
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}

dependencies {

    implementation(AppDependencies.kotlin)

    implementation(AppDependencies.coreKtx)
    implementation(AppDependencies.compose)
    implementation(AppDependencies.composeActivity)
    implementation(AppDependencies.appComponents)

    implementation(AppDependencies.retrofitRuntime)
    implementation(AppDependencies.gson)
    implementation("io.coil-kt:coil-compose:2.1.0")

    implementation(AppDependencies.paging)
    implementation(AppDependencies.pagingCompose)

    // Room DB
    implementation(AppDependencies.roomRuntime)
    implementation(AppDependencies.roomKtx)
    kapt(AppDependencies.roomCompiler)

    debugImplementation("androidx.compose.ui:ui-tooling:${Versions.compose}")

    //debugImplementation("androidx.compose.ui:ui-tooling:${rootProject.extra["compose_version"]}")
    //debugImplementation("androidx.compose.ui:ui-test-manifest:${rootProject.extra["compose_version"]}")
}