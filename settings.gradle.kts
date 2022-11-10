pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

rootProject.name = "stankin-schedule"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    ":app",
    ":core",

    ":settings",

    ":schedule:schedule-core",
    ":schedule:schedule-core-ui",
    ":schedule:schedule-settings",
    ":schedule:schedule-creator",
    ":schedule:schedule-editor",
    ":schedule:schedule-home",
    ":schedule:schedule-list",
    ":schedule:schedule-repository",
    ":schedule:schedule-viewer",
    ":schedule:schedule-widget",

    ":journal:journal-core",
    ":journal:journal-login",
    ":journal:journal-predict",
    ":journal:journal-viewer",

    ":news:news-core",
    ":news:news-home",
    ":news:news-review",
    ":news:news-viewer",
)
