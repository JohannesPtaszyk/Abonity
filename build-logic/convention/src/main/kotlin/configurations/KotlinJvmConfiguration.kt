package configurations

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = projectJavaVersion
        targetCompatibility = projectJavaVersion
    }
    configureKotlin()
}

internal fun Project.configureKotlin() {
    // Workaround https://youtrack.jetbrains.com/issue/KT-55947
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = projectJavaVersion.toString()
            allWarningsAsErrors = true
            freeCompilerArgs +=
                listOf(
                    "-opt-in=kotlin.RequiresOptIn",
                )
        }

        configurations.configureEach {
            incoming.afterResolve {
                dependencies.forEach {
                    if (it.name.contains("kotlinx.coroutines")) {
                        kotlinOptions {
                            freeCompilerArgs +=
                                listOf(
                                    "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                                    "-opt-in=kotlinx.coroutines.FlowPreview",
                                )
                        }
                    }
                }
            }
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        failFast = true
        testLogging {
            events = setOf(
                TestLogEvent.SKIPPED,
                TestLogEvent.FAILED,
                TestLogEvent.STANDARD_ERROR,
                TestLogEvent.STANDARD_OUT,
            )
            exceptionFormat = TestExceptionFormat.FULL
        }
    }
}
