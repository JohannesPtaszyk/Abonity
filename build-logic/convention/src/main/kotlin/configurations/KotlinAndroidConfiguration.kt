package configurations

import com.android.build.api.dsl.CommonExtension
import extensions.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Configure base Kotlin with Android options
 */
internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        compileSdk = libs.findVersion("targetSdkVersion").get().toString().toInt()

        defaultConfig {
            minSdk = libs.findVersion("minSdkVersion").get().toString().toInt()
        }

        packaging {
            resources {
                excludes.add("META-INF/LICENSE.md")
                excludes.add("META-INF/LICENSE-notice.md")
                excludes.add("/META-INF/{AL2.0,LGPL2.1}")
            }
        }

        compileOptions {
            sourceCompatibility = projectJavaVersion
            targetCompatibility = projectJavaVersion
            isCoreLibraryDesugaringEnabled = true
        }

        sourceSets {
            getByName("main").java.srcDirs("src/main/kotlin")
            getByName("test").java.srcDirs("src/test/kotlin")
            getByName("androidTest").java.srcDirs("src/androidTest/kotlin")
        }
    }

    configureKotlin()

    dependencies {
        add("coreLibraryDesugaring", libs.findLibrary("android.desugarJdkLibs").get())
    }
}
