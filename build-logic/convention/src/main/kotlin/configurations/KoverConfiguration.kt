package configurations

import kotlinx.kover.gradle.plugin.KoverGradlePlugin
import kotlinx.kover.gradle.plugin.dsl.KoverReportExtension
import kotlinx.kover.gradle.plugin.dsl.KoverReportFilters
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

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
        )
        annotatedBy(
            "*Generated*",
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
            mergeWith("release")
            mergeWith("debug")
        }
        androidReports("release") {
            filters {
                coverageExclusions()
            }
        }
        androidReports("debug") {
            filters {
               coverageExclusions()
            }
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
        dependencies {
            subprojects.forEach {
                add("kover", project(it.path))
            }
        }
    }
}
