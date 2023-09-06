// Top-level build file where you can add configuration options common to all subprojects/modules.

ext {
    extra["appCompileSdkVersion"] = 33
    extra["appMinSdkVersion"] = 23
    extra["appTargetSdkVersion"] = 33
    extra["appVersionCode"] = 120
    extra["appVersionName"] = "2.1.0"
    extra["appBuildToolsVersion"] = "33.0.0"
}

buildscript {

    repositories {
        mavenCentral()
        mavenLocal()
        google()
        gradlePluginPortal()
    }

    dependencies {
        classpath(libs.androidGradlePlugin)
        classpath(libs.kotlinPlugin)
        classpath(libs.googleServicesPlugin)
        classpath(libs.firebase.plugin)
        classpath(libs.hilt.plugin)
    }
}

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.kotlin) apply false
}

allprojects {

    repositories {
        mavenCentral()
        google()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://jitpack.io")
    }
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()

    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
