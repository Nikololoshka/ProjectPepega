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
        dataBinding = true
        viewBinding = true
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
    implementation(AppDependencies.material3)

    implementation(AppDependencies.securityCrypto)

    implementation(AppDependencies.appComponents)
    implementation(AppDependencies.appcompat)

    implementation(AppDependencies.retrofitRuntime)
    implementation(AppDependencies.retrofitGson)
    implementation(AppDependencies.gson)
    implementation(AppDependencies.coil)

    implementation(AppDependencies.paging)
    implementation(AppDependencies.pagingCompose)

    // Firebase
    implementation(platform(AppDependencies.firebaseBom))
    implementation(AppDependencies.firebaseModules)

    implementation(AppDependencies.accompanistPager)
    implementation(AppDependencies.accompanistPagerIndicators)
    implementation(AppDependencies.accompanistSwipeRefresh)
    implementation(AppDependencies.accompanistPlaceholder)
    implementation(AppDependencies.accompanistNavigation)
    implementation(AppDependencies.accompanistFlowLayout)

    // Dagger & Hilt
    implementation(AppDependencies.hiltAndroid)
    implementation(AppDependencies.hiltNavigation)
    kapt(AppDependencies.hiltCompiler)

    // Room DB
    implementation(AppDependencies.roomRuntime)
    implementation(AppDependencies.roomKtx)
    kapt(AppDependencies.roomCompiler)

    debugImplementation("androidx.compose.ui:ui-tooling:${Versions.compose}")
}