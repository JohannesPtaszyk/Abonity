package configurations

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

internal fun Project.configureKotlinJvm() {
    tasks.withType<KotlinCompilationTask<*>>().configureEach {
        compilerOptions.optIn.apply {
            add("kotlin.time.ExperimentalTime")
        }
    }
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = projectJavaVersion
        targetCompatibility = projectJavaVersion
    }
    configureKotlinTest()
}

internal fun Project.configureKotlinTest() {
    tasks.withType<Test> {
        useJUnitPlatform()
        failFast = true
        testLogging {
            events = setOf(
                TestLogEvent.SKIPPED,
                TestLogEvent.FAILED,
            )
            exceptionFormat = TestExceptionFormat.FULL
        }
    }
}
