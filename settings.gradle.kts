rootProject.name = "Abonity"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

includeBuild("build-logic")

include(":app")

include(
    ":core:local",
    ":core:domain",
    ":core:entity",
    ":core:ui",
    ":core:test",
)

include(
    ":common:test",
)

include(":navigation")

include(
    ":feature:subscription",
    ":feature:settings",
)
