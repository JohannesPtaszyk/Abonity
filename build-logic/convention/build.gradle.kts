plugins {
    `kotlin-dsl`
}

group = "dev.pott.buildlogic"

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
    compileOnly(libs.kotlin.composeCompilerPlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.kover.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.secrets.gradlePlugin)
    compileOnly(libs.spotless.gradlePlugin)
    compileOnly(libs.sonar.gradlePlugin)

    implementation(libs.versioncompare)
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
