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

    implementation(AppDependencies.kotlin)

    implementation(AppDependencies.coreKtx)
    implementation(AppDependencies.compose)
    implementation(AppDependencies.composeNavigation)

    implementation(AppDependencies.composeMaterial3)
    implementation(AppDependencies.material3)

    implementation(AppDependencies.accompanistPager)
    implementation(AppDependencies.accompanistPagerIndicators)

    implementation(AppDependencies.network)

    // Dagger & Hilt
    implementation(AppDependencies.hiltAndroid)
    kapt(AppDependencies.hiltCompiler)

    implementation(AppDependencies.paging)
    implementation(AppDependencies.pagingCompose)

    implementation(AppDependencies.chromeBrowser)

    implementation(AppDependencies.preference)
    api(AppDependencies.jodaTime)
}