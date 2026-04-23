package configurations

import com.android.build.api.dsl.CommonExtension
import extensions.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradleSubplugin

internal fun Project.configureAndroidCompose(commonExtension: CommonExtension) {
    pluginManager.apply(ComposeCompilerGradleSubplugin::class)
    pluginManager.withPlugin("org.jetbrains.kotlin.plugin.compose") {
        extensions.configure<ComposeCompilerGradlePluginExtension> {
            includeSourceInformation = true
        }
    }
    commonExtension.apply {
        buildFeatures.apply {
            compose = true
        }

        @Suppress("UnstableApiUsage")
        testOptions.unitTests.apply {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }

        dependencies {
            val bom = libs.findLibrary("androidx-compose-bom").get()
            add("implementation", platform(bom))
            add("implementation", libs.findLibrary("androidx.compose.runtime").get())
            add("implementation", libs.findLibrary("kotlinx.collections.immutable").get())
            add("implementation", libs.findLibrary("androidx.compose.ui.testManifest").get())
            add("implementation", libs.findLibrary("androidx.compose.ui.tooling.preview").get())

            add("debugImplementation", libs.findLibrary("androidx.compose.ui.testManifest").get())
            add("debugImplementation", libs.findLibrary("androidx.compose.ui.tooling").get())

            add("androidTestImplementation", platform(bom))

            configurations.findByName("testFixturesImplementation")?.let {
                add("testFixturesImplementation", platform(bom))
            }
        }
    }
}
