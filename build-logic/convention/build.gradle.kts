plugins {
    `kotlin-dsl`
}

group = "dev.pott.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.detekt.plugin)
    compileOnly(libs.firebase.crashlytics.gradlePlugin)
    compileOnly(libs.firebase.performance.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("AndroidAppConventionPlugin") {
            id = "dev.pott.android.app"
            implementationClass = "plugins.AndroidAppConventionPlugin"
        }
        register("AndroidLibConventionPlugin") {
            id = "dev.pott.android.lib"
            implementationClass = "plugins.AndroidLibConventionPlugin"
        }
        register("AndroidLibComposeConventionPlugin") {
            id = "dev.pott.android.lib.compose"
            implementationClass = "plugins.AndroidLibComposeConventionPlugin"
        }
        register("AndroidLintConventionPlugin") {
            id = "dev.pott.android.lint"
            implementationClass = "plugins.AndroidLintConventionPlugin"
        }
        register("AndroidRoomConventionPlugin") {
            id = "dev.pott.android.room"
            implementationClass = "plugins.AndroidRoomConventionPlugin"
        }
        register("AndroidTestConventionPlugin") {
            id = "dev.pott.android.test"
            implementationClass = "plugins.AndroidTestConventionPlugin"
        }
        register("HiltConventionPlugin") {
            id = "dev.pott.hilt"
            implementationClass = "plugins.HiltConventionPlugin"
        }
        register("JvmLibConventionPlugin") {
            id = "dev.pott.jvm.lib"
            implementationClass = "plugins.JvmLibConventionPlugin"
        }
        register("ProjectConventionPlugin") {
            id = "dev.pott.project"
            implementationClass = "plugins.ProjectConventionPlugin"
        }
    }
}
