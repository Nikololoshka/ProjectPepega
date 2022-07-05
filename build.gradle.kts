// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    repositories {
        mavenCentral()
        mavenLocal()
        google()
        gradlePluginPortal()
    }

    dependencies {
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files

        classpath(AppDependencies.androidGradlePlugin)
        classpath(AppDependencies.kotlinPlugin)
        classpath(AppDependencies.googleServices)
        classpath(AppDependencies.firebaseGradle)
        classpath(AppDependencies.hiltPlugin)
        classpath(AppDependencies.navigationSafeArgsPlugin)
    }
}

plugins {
    id("com.android.application") version Versions.agp apply false
    id("com.android.library") version Versions.agp apply false
    // id("com.google.devtools.ksp") version Versions.ksp apply false
    kotlin("android") version Versions.kotlin apply false
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
