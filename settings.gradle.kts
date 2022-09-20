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

    ":schedule:schedule-core",
    ":schedule:schedule-creator",
    ":schedule:schedule-editor",
    ":schedule:schedule-list",
    ":schedule:schedule-repository",
    ":schedule:schedule-viewer",

    ":journal:journal-core",
    ":journal:journal-login",
    ":journal:journal-predict",
    ":journal:journal-viewer",

    ":news:news-core",
    ":news:news-review",
    ":news:news-viewer",
)
