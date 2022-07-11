rootProject.name = "Stankin Schedule"

include(":app")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }
}
include(":core")
include(":feature_news")
// include(":feature_modulejournal")
// include(":feature_schedule")
