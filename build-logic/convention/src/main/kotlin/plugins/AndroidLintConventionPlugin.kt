package plugins

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import configurations.configureLint
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

@Suppress("unused")
class AndroidLintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            when {
                pluginManager.hasPlugin("com.android.application") ->
                    configure<ApplicationExtension> { configureLint(lint) }

                pluginManager.hasPlugin("com.android.library") ->
                    configure<LibraryExtension> { configureLint(lint) }

                else -> {
                    pluginManager.apply("com.android.lint")
                    configure<Lint> { configureLint(this) }
                }
            }
        }
    }
}
