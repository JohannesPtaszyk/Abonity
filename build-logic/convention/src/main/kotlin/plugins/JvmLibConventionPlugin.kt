package plugins

import configurations.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmLibConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("dev.pott.android.lint")
                apply("org.jetbrains.kotlin.jvm")
            }
            configureKotlinJvm()
        }
    }
}
