plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
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
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


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

    packagingOptions {
        resources.excludes.addAll(
            listOf(
                "META-INF/LICENSE.md",
                "META-INF/NOTICE.md",
            )
        )
    }
    namespace = "com.vereshchagin.nikolay.stankinschedule"
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(":core"))

    implementation(project(":schedule:schedule-core"))
    implementation(project(":schedule:schedule-core-ui"))
    implementation(project(":schedule:schedule-home"))
    implementation(project(":schedule:schedule-creator"))
    implementation(project(":schedule:schedule-editor"))
    implementation(project(":schedule:schedule-list"))
    implementation(project(":schedule:schedule-repository"))
    implementation(project(":schedule:schedule-viewer"))

    implementation(project(":journal:journal-core"))
    implementation(project(":journal:journal-login"))
    implementation(project(":journal:journal-predict"))
    implementation(project(":journal:journal-viewer"))

    implementation(project(":news:news-core"))
    implementation(project(":news:news-home"))
    implementation(project(":news:news-review"))
    implementation(project(":news:news-viewer"))

    // Core
    implementation(libs.androidx.core)

    // Jetpack Compose & Material 3
    implementation(libs.bundles.compose)
    implementation(libs.compose.material3)
    implementation(libs.compose.activity)
    implementation(libs.compose.coil)

    implementation(libs.accompanist.navigation)

    // Components
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.paging)
    implementation(libs.androidx.startup)
    implementation(libs.ui.material)

    implementation(libs.integration.coreGooglePlay)
    implementation(libs.integration.googleServices)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.storage)

    // Room DB
    implementation(libs.bundles.room)
    kapt(libs.room.compiler)

    // Worker
    implementation(libs.work.runtime)
    implementation(libs.work.hilt)
    kapt(libs.work.hiltCompiler)

    // DI
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation)
    kapt(libs.hilt.compiler)
}