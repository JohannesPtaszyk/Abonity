package configurations

import kotlinx.kover.gradle.plugin.KoverGradlePlugin
import kotlinx.kover.gradle.plugin.dsl.KoverReportExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

fun Project.applyKover() {
    pluginManager.apply(KoverGradlePlugin::class)
}

fun Project.applyKoverAndroid() {
    pluginManager.apply(KoverGradlePlugin::class)
    configure<KoverReportExtension> {
        filters {
            excludes {
                classes(
                    "*Impl_Factory.*",
                    "*_*Factory",
                    "*_Factory*",
                )
                annotatedBy(
                    "*Generated*",
                    "*Generated",
                    "androidx.compose.runtime.Composable",
                )
            }
        }
    }
}


fun Project.applyKoverProject() {
    pluginManager.apply(KoverGradlePlugin::class)
    configure<KoverReportExtension> {
        dependencies {
            subprojects.forEach {
                add("kover", project(it.path))
            }
        }
    }
}
