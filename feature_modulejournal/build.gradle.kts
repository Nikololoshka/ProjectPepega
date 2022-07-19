plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk

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

hilt {
    enableAggregatingTask = true
}

dependencies {
    implementation(project(":core"))

    implementation(AppDependencies.kotlin)

    implementation(AppDependencies.coreKtx)
    implementation(AppDependencies.compose)
    implementation(AppDependencies.composeActivity)
    implementation(AppDependencies.composeMaterial3)

    implementation(AppDependencies.appComponents)
    implementation(AppDependencies.appcompat)

    implementation(AppDependencies.retrofitRuntime)
    implementation(AppDependencies.retrofitGson)
    implementation(AppDependencies.gson)
    implementation("io.coil-kt:coil-compose:2.1.0")

    implementation(AppDependencies.paging)
    implementation(AppDependencies.pagingCompose)

    val accompanist = "0.24.13-rc"
    implementation("com.google.accompanist:accompanist-pager:$accompanist")
    implementation("com.google.accompanist:accompanist-pager-indicators:$accompanist")
    implementation("com.google.accompanist:accompanist-webview:$accompanist")
    implementation("com.google.accompanist:accompanist-swiperefresh:$accompanist")
    implementation("com.google.accompanist:accompanist-placeholder-material:$accompanist")

    // Dagger & Hilt
    implementation(AppDependencies.hiltAndroid)
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    kapt(AppDependencies.hiltCompiler)

    debugImplementation("androidx.compose.ui:ui-tooling:${Versions.compose}")
}