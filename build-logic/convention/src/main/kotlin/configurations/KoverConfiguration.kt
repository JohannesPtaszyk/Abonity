package configurations

import kotlinx.kover.gradle.plugin.KoverGradlePlugin
import kotlinx.kover.gradle.plugin.dsl.KoverReportExtension
import kotlinx.kover.gradle.plugin.dsl.KoverReportFilters
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

fun KoverReportFilters.coverageExclusions() {
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
    configure<KoverReportExtension> {
        filters {
            coverageExclusions()
        }
    }
}

fun Project.applyKoverAndroid() {
    pluginManager.apply(KoverGradlePlugin::class)
    configure<KoverReportExtension> {
        defaults {
            mergeWith("debug")
            mergeWith("release")
        }
        filters {
            coverageExclusions()
        }
    }
}

fun Project.applyKoverProject() {
    pluginManager.apply(KoverGradlePlugin::class)
    configure<KoverReportExtension> {
        filters {
            coverageExclusions()
        }
    }
}
