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

include(":schedule:schedule-core")
include(":schedule:schedule-creator")
include(":schedule:schedule-editor")
include(":schedule:schedule-list")
include(":schedule:schedule-repository")
include(":schedule:schedule-viewer")

include(":journal:journal-core")
include(":journal:journal-login")
include(":journal:journal-predict")
include(":journal:journal-viewer")

include(":news:news-core")
include(":news:news-review")
include(":news:news-viewer")

// include(":legacy")
