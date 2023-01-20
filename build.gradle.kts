// Top-level build file where you can add configuration options common to all sub-projects/modules.

ext {
    extra["appCompileSdkVersion"] = 33
    extra["appMinSdkVersion"] = 23
    extra["appTargetSdkVersion"] = 33
    extra["appVersionCode"] = 108
    extra["appVersionName"] = "2.0.3"
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

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
