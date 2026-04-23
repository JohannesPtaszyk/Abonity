package configurations

import com.android.build.api.dsl.CommonExtension
import extensions.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

/**
 * Configure base Kotlin with Android options
 */
internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension) {
    commonExtension.apply {
        compileSdk = libs.findVersion("compileSdkVersion").get().toString().toInt()

        defaultConfig.apply {
            minSdk = libs.findVersion("minSdkVersion").get().toString().toInt()
        }

        packaging.resources.apply {
            excludes.add("META-INF/LICENSE.md")
            excludes.add("META-INF/LICENSE-notice.md")
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }

        compileOptions.apply {
            sourceCompatibility = projectJavaVersion
            targetCompatibility = projectJavaVersion
            isCoreLibraryDesugaringEnabled = true
        }
    }

    with(extensions.getByType<KotlinAndroidProjectExtension>()) {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
        }
    }

    configureKotlinTest()

    tasks.withType<KotlinCompilationTask<*>>().configureEach {
        compilerOptions.optIn.apply {
            add("kotlin.time.ExperimentalTime")
        }
    }

    dependencies {
        add("coreLibraryDesugaring", libs.findLibrary("android.desugarJdkLibs").get())
    }
}
