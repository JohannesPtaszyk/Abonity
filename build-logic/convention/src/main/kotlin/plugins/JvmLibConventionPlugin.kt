package plugins

import configurations.applyKover
import configurations.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class JvmLibConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
                apply("dev.pott.android.lint")
            }
            configureKotlinJvm()
            applyKover()
        }
    }
}
