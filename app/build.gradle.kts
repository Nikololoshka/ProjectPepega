/*
apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-kapt'
apply plugin: 'kotlin-parcelize'
apply plugin: 'com.google.gms.google-services'
apply plugin: 'com.google.firebase.crashlytics'
apply plugin: 'dagger.hilt.android.plugin'
*/

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-parcelize")
    //  id("com.google.devtools.ksp")
    kotlin("android")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
}

android {

    compileSdk = AppConfig.compileSdk
    buildToolsVersion = AppConfig.buildToolsVersion

    defaultConfig {

        applicationId = "com.vereshchagin.nikolay.stankinschedule"

        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName

        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = AppConfig.androidTestInstrumentation


        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
        }

    }


    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isDebuggable = false
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = Versions.java
        targetCompatibility = Versions.java
    }

    kotlinOptions {
        jvmTarget = Versions.java.toString()
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
        resources.excludes.addAll(
            listOf(
                "META-INF/LICENSE.md",
                "META-INF/NOTICE.md",
            )
        )
    }
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(":core"))

    implementation(project(":schedule:schedule-core"))
    implementation(project(":schedule:schedule-editor"))
    implementation(project(":schedule:schedule-list"))
    implementation(project(":schedule:schedule-repository"))
    implementation(project(":schedule:schedule-viewer"))

    implementation(project(":journal:journal-core"))
    implementation(project(":journal:journal-login"))
    implementation(project(":journal:journal-predict"))
    implementation(project(":journal:journal-viewer"))

    implementation(project(":news:news-core"))
    implementation(project(":news:news-review"))
    implementation(project(":news:news-viewer"))

    // App core
    implementation(AppDependencies.coreKtx)
    // implementation(AppDependencies.ksp)

    // Kotlin
    implementation(AppDependencies.kotlin)

    // Firebase
    implementation(platform(AppDependencies.firebaseBom))
    implementation(AppDependencies.firebaseModules)

    // UI
    implementation(AppDependencies.compose)
    implementation(AppDependencies.composeActivity)
    implementation(AppDependencies.composeNavigation)
    implementation(AppDependencies.composeMaterial3)

    implementation(AppDependencies.accompanistNavigation)


    implementation(AppDependencies.appcompat)
    // implementation(AppDependencies.legacySupport)
    // implementation(AppDependencies.constraintLayout)
    // implementation(AppDependencies.gridLayout)
    // implementation(AppDependencies.recyclerview)
    // implementation(AppDependencies.pager)
    implementation(AppDependencies.material3)
    // implementation(AppDependencies.preference)

    // Lifecycle
    // implementation(AppDependencies.lifecycle)

    // Dagger & Hilt
    implementation(AppDependencies.hiltAndroid)
    implementation(AppDependencies.hiltNavigation)
    kapt(AppDependencies.hiltCompiler)

    implementation(AppDependencies.hiltWork)
    kapt(AppDependencies.hiltWorkCompiler)

    // Integration
    // implementation(AppDependencies.chromeBrowser)
    implementation(AppDependencies.coreGooglePlay)
    implementation(AppDependencies.googleServices)

    // Glide
    // implementation(AppDependencies.glideRuntime)
    // kapt(AppDependencies.glideCompiler)

    // Utils
    // implementation(AppDependencies.securityCrypto)
    // implementation(AppDependencies.dataBinding)

    // Navigation
    implementation(AppDependencies.navigation)

    // Network
    // implementation(AppDependencies.network)

    // Room DB
    implementation(AppDependencies.roomRuntime)
    implementation(AppDependencies.roomKtx)
    kapt(AppDependencies.roomCompiler)

    implementation(AppDependencies.paging)

    // Work
    implementation(AppDependencies.workRuntime)
    implementation(AppDependencies.workRuntimeKtx)

    // Other
    // implementation(AppDependencies.gson)
    // implementation(AppDependencies.holoColorPicker)
    // implementation(AppDependencies.jodaTime)
    // implementation(AppDependencies.json)
    // implementation(AppDependencies.junit)
    // implementation(AppDependencies.shimmer)
    implementation(AppDependencies.startup)
    // implementation(AppDependencies.webkit)
}