package configurations

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ManagedVirtualDevice
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.invoke

/**
 * Configure project for Gradle managed devices
 */
@Suppress("UnstableApiUsage")
internal fun configureGradleManagedDevices(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    val pixel6 = GradleManagedDeviceConfig("Pixel 6", 34, "google")

    val allDevices = listOf(pixel6)
    val ciDevices = listOf(pixel6)

    commonExtension.testOptions {
        managedDevices {
            devices {
                allDevices.forEach { deviceConfig ->
                    maybeCreate(deviceConfig.taskName, ManagedVirtualDevice::class.java).apply {
                        device = deviceConfig.device
                        apiLevel = deviceConfig.apiLevel
                        systemImageSource = deviceConfig.systemImageSource
                    }
                }
            }
            groups {
                maybeCreate("ci").apply {
                    ciDevices.forEach { deviceConfig ->
                        targetDevices.add(devices[deviceConfig.taskName])
                    }
                }
            }
        }
    }
}

private data class GradleManagedDeviceConfig(
    val device: String,
    val apiLevel: Int,
    val systemImageSource: String,
) {
    val taskName =
        buildString {
            append(device.lowercase().replace(" ", ""))
            append("api")
            append(apiLevel)
        }
}
