package configurations

import com.google.android.libraries.mapsplatform.secrets_gradle_plugin.SecretsPluginExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

fun Project.applySecrets() {
    subprojects {
        pluginManager.withPlugin(
            "com.google.android.libraries.mapsplatform.secrets-gradle-plugin",
        ) {
            configure<SecretsPluginExtension> {
                defaultPropertiesFileName = "secrets.defaults.properties"
                propertiesFileName = "secrets.properties"
            }
        }
    }
}
