package plugins

import com.android.build.gradle.LibraryExtension
import configurations.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

@Suppress("unused")
class AndroidLibComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.gradle.jacoco")
                apply("dev.pott.android.lib")
            }
            extensions.getByType<LibraryExtension>().apply {
                configureAndroidCompose(this)
            }
        }
    }
}
