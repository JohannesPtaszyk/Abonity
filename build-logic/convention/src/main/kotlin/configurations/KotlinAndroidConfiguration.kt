/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.nowinandroid

import com.android.build.api.dsl.CommonExtension
import configurations.configureKotlin
import configurations.projectJavaVersion
import extensions.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Configure base Kotlin with Android options
 */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = libs.findVersion("targetSdkVersion").get().toString().toInt()

        defaultConfig {
            minSdk = libs.findVersion("minSdkVersion").get().toString().toInt()
        }

        compileOptions {
            // Up to Java 11 APIs are available through desugaring
            // https://developer.android.com/studio/write/java11-minimal-support-table
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
