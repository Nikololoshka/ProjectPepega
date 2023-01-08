plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
}

android {

    val appCompileSdkVersion: Int by rootProject.extra
    val appMinSdkVersion: Int by rootProject.extra
    val appTargetSdkVersion: Int by rootProject.extra
    val appVersionCode: Int by rootProject.extra
    val appVersionName: String by rootProject.extra
    val appBuildToolsVersion: String by rootProject.extra

    compileSdk = appCompileSdkVersion
    buildToolsVersion = appBuildToolsVersion

    defaultConfig {

        applicationId = "com.vereshchagin.nikolay.stankinschedule"

        minSdk = appMinSdkVersion
        targetSdk = appTargetSdkVersion
        versionCode = appVersionCode
        versionName = appVersionName

        setProperty("archivesBaseName", "stankin-schedule_v$versionName($versionCode)")

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
            versionNameSuffix = "-debug"

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

    implementation(project(":domain:core"))

    implementation(project(":data:core"))
    implementation(project(":data:journal-core"))
    implementation(project(":data:schedule-core"))
    implementation(project(":data:schedule-table"))
    implementation(project(":data:schedule-repository"))
    implementation(project(":data:schedule-settings"))
    implementation(project(":data:schedule-viewer"))
    implementation(project(":data:schedule-widget"))
    implementation(project(":data:news-core"))
    implementation(project(":data:settings"))

    implementation(project(":ui:core"))
    implementation(project(":ui:home"))
    implementation(project(":ui:journal-login"))
    implementation(project(":ui:journal-predict"))
    implementation(project(":ui:journal-viewer"))
    implementation(project(":ui:schedule-creator"))
    implementation(project(":ui:schedule-editor"))
    implementation(project(":ui:schedule-table"))
    implementation(project(":ui:schedule-list"))
    implementation(project(":ui:schedule-repository"))
    implementation(project(":ui:schedule-viewer"))
    implementation(project(":ui:schedule-widget"))
    implementation(project(":ui:news-review"))
    implementation(project(":ui:news-viewer"))
    implementation(project(":ui:settings"))


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
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
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