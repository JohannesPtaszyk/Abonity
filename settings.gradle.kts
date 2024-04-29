rootProject.name = "Abonity"
// https://github.com/SonarSource/sonar-scanning-examples/issues/169#issuecomment-1769854089
System.setProperty("sonar.gradle.skipCompile", "true")

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
    ":common:extensions",
    ":common:compose",
    ":common:injection",
)

include(":navigation")

include(
    ":feature:subscription",
    ":feature:settings",
    ":feature:home",
)
