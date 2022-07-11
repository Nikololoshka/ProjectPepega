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
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
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

    implementation(project(":feature_news"))

    // App core
    implementation(AppDependencies.coreKtx)
    implementation(AppDependencies.ksp)

    // Kotlin
    implementation(AppDependencies.kotlin)

    // Components
    implementation(AppDependencies.appComponents)

    // Firebase
    implementation(platform(AppDependencies.firebaseBom))
    implementation(AppDependencies.firebaseModules)

    // UI
    implementation(AppDependencies.compose)
    implementation(AppDependencies.composeActivity)
    implementation(AppDependencies.composeNavigation)
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    implementation(AppDependencies.appcompat)
    implementation(AppDependencies.legacySupport)
    implementation(AppDependencies.constraintLayout)
    implementation(AppDependencies.gridLayout)
    implementation(AppDependencies.recyclerview)
    implementation(AppDependencies.pager)
    implementation(AppDependencies.paging)
    implementation(AppDependencies.material)
    implementation(AppDependencies.preference)

    // Lifecycle
    implementation(AppDependencies.lifecycle)

    // Dagger & Hilt
    implementation(AppDependencies.hiltAndroid)
    kapt(AppDependencies.hiltCompiler)

    implementation(AppDependencies.hiltWork)
    kapt(AppDependencies.hiltWorkCompiler)

    // Integration
    implementation(AppDependencies.chromeBrowser)
    implementation(AppDependencies.coreGooglePlay)
    implementation(AppDependencies.googleServices)

    // Glide
    implementation(AppDependencies.glideRuntime)
    kapt(AppDependencies.glideCompiler)

    // Utils
    implementation(AppDependencies.securityCrypto)
    implementation(AppDependencies.dataBinding)

    // Navigation
    implementation(AppDependencies.navigation)

    // Network
    implementation(AppDependencies.network)

    // Room DB
    implementation(AppDependencies.roomRuntime)
    implementation(AppDependencies.roomKtx)
    kapt(AppDependencies.roomCompiler)

    // Work
    implementation(AppDependencies.workRuntime)
    implementation(AppDependencies.workRuntimeKtx)

    // Other
    implementation(AppDependencies.gson)
    implementation(AppDependencies.holoColorPicker)
    implementation(AppDependencies.jodaTime)
    implementation(AppDependencies.json)
    implementation(AppDependencies.junit)
    implementation(AppDependencies.shimmer)
    implementation(AppDependencies.startup)
    implementation(AppDependencies.webkit)

    /*
    kapt deps.room.compiler
    kapt deps.glide.compiler
    kapt deps.databinding.compiler
    kapt deps.hilt.compiler
    kapt deps.hilt.work_compiler


    implementation platform(deps.firebase.bom)

    implementation deps.activity.activity_ktx
    implementation deps.appcompat
    implementation deps.chrome_browser
    implementation deps.commons
    implementation deps.constraint_layout
    implementation deps.core_google_play
    implementation deps.core_ktx
    implementation deps.firebase.analytics
    implementation deps.firebase.crashlytics
    implementation deps.firebase.storage
    implementation deps.fragment.runtime_ktx
    implementation deps.glide.runtime
    implementation deps.grid_layout
    implementation deps.gson
    implementation deps.hilt.hilt_android
    implementation deps.hilt.work
    implementation deps.holo_color_picker
    implementation deps.joda_time
    implementation deps.kotlin.reflect
    implementation deps.kotlin.stdlib
    implementation deps.legacy_support
    implementation deps.lifecycle.java8
    implementation deps.lifecycle.livedata_ktx
    implementation deps.lifecycle.runtime
    implementation deps.lifecycle.viewmodel_ktx
    implementation deps.material
    implementation deps.navigation.fragment_ktx
    implementation deps.navigation.runtime_ktx
    implementation deps.navigation.ui_ktx
    implementation deps.okhttp_logging_interceptor
    implementation deps.pager
    implementation deps.paging
    implementation deps.preference
    implementation deps.recyclerview
    implementation deps.retrofit.gson
    implementation deps.retrofit.runtime
    implementation deps.room.ktx
    implementation deps.room.runtime
    implementation deps.security_crypto
    implementation deps.shimmer
    implementation deps.webkit
    implementation deps.work.runtime
    implementation deps.work.runtime_ktx


    androidTestImplementation deps.room.testing
    androidTestImplementation 'androidx.test:runner:1.3.0'
    androidTestImplementation 'androidx.test.ext:junit:1.1.2'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.3.0'
    androidTestImplementation 'androidx.test:rules:1.4.0-alpha05'

//    debugImplementation 'com.squareup.leakcanary:leakcanary-android:2.6'

    testImplementation deps.junit
    testImplementation deps.json
    testImplementation deps.kotlin.test
    */
}