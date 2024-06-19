package configurations

import kotlinx.kover.gradle.plugin.KoverGradlePlugin
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import kotlinx.kover.gradle.plugin.dsl.KoverReportFiltersConfig
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

fun KoverReportFiltersConfig.coverageExclusions() {
    excludes {
        classes(
            "*Impl_Factory.*",
            "*_*Factory",
            "*_Factory*",
            "**/R.class",
            "**/R\$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "dagger.**",
            "_**",
            "**Activity**",
            "**Module**",
            "*.gradle.kts",
        )
        annotatedBy(
            "*Generated*",
            "**Generated**",
            "*Generated",
            "androidx.compose.runtime.Composable",
        )
    }
}

fun Project.applyKover() {
    pluginManager.apply(KoverGradlePlugin::class)
    extensions.configure<KoverProjectExtension>("kover") {
        reports {
            filters {
                coverageExclusions()
            }
        }
    }
}

fun Project.applyKoverAndroid() {
    pluginManager.apply(KoverGradlePlugin::class)
    extensions.configure<KoverProjectExtension>("kover") {
        currentProject {
            createVariant("kover") {
                addWithDependencies("debug", "release")
            }
        }
        reports {
            filters {
                coverageExclusions()
            }
        }
    }
}

fun Project.applyKoverProject() {
    pluginManager.apply(KoverGradlePlugin::class)
    extensions.configure<KoverProjectExtension>("kover") {
        reports {
            filters {
                coverageExclusions()
            }
        }
    }
}
