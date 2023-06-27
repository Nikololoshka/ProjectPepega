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

    ":data:core",
    ":data:journal-core",
    ":data:news-core",
    ":data:schedule-core",
    ":data:schedule-table",
    ":data:schedule-parser",
    ":data:schedule-viewer",
    ":data:schedule-settings",
    ":data:schedule-repository",
    ":data:schedule-widget",
    ":data:settings",

    ":domain:core",
    ":domain:journal-core",
    ":domain:news-core",
    ":domain:schedule-core",
    ":domain:schedule-table",
    ":domain:schedule-parser",
    ":domain:schedule-viewer",
    ":domain:schedule-settings",
    ":domain:schedule-repository",
    ":domain:schedule-widget",
    ":domain:settings",

    ":ui:core",
    ":ui:home",
    ":ui:journal-login",
    ":ui:journal-viewer",
    ":ui:journal-predict",
    ":ui:news-review",
    ":ui:news-viewer",
    ":ui:schedule-core",
    ":ui:schedule-list",
    ":ui:schedule-table",
    ":ui:schedule-parser",
    ":ui:schedule-viewer",
    ":ui:schedule-creator",
    ":ui:schedule-editor",
    ":ui:schedule-repository",
    ":ui:schedule-widget",
    ":ui:settings",
)
