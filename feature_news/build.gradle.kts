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
    implementation(AppDependencies.appComponents)
    implementation(AppDependencies.appcompat)

    implementation(AppDependencies.retrofitRuntime)
    implementation(AppDependencies.retrofitGson)
    implementation(AppDependencies.gson)
    implementation("io.coil-kt:coil-compose:2.1.0")

    implementation(AppDependencies.paging)
    implementation(AppDependencies.pagingCompose)

    val accompanist = "0.23.1"
    implementation("com.google.accompanist:accompanist-pager:$accompanist")
    implementation("com.google.accompanist:accompanist-pager-indicators:$accompanist")
    implementation("com.google.accompanist:accompanist-webview:0.24.13-rc")
    implementation(AppDependencies.webkit)

    // Dagger & Hilt
    implementation(AppDependencies.hiltAndroid)
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    kapt(AppDependencies.hiltCompiler)

    // Room DB
    implementation(AppDependencies.roomRuntime)
    implementation(AppDependencies.roomKtx)
    kapt(AppDependencies.roomCompiler)

    implementation(AppDependencies.shimmer)

    debugImplementation("androidx.compose.ui:ui-tooling:${Versions.compose}")

    //debugImplementation("androidx.compose.ui:ui-tooling:${rootProject.extra["compose_version"]}")
    //debugImplementation("androidx.compose.ui:ui-test-manifest:${rootProject.extra["compose_version"]}")
}