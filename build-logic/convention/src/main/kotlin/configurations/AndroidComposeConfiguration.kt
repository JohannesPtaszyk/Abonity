package configurations

import com.android.build.api.dsl.CommonExtension
import extensions.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(commonExtension: CommonExtension<*, *, *, *, *>) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion =
                libs.findVersion("androidxComposeCompiler").get().toString()
        }

        @Suppress("UnstableApiUsage")
        testOptions {
            unitTests {
                isIncludeAndroidResources = true
                isReturnDefaultValues = true
            }
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

            add("testImplementation", libs.findLibrary("robolectric").get())

            add("androidTestImplementation", platform(bom))
        }
    }
}
