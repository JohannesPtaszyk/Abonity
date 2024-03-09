package plugins

import configurations.applyDetekt
import configurations.applyKoverProject
import configurations.applySonar
import configurations.applySpotless
import org.gradle.api.Plugin
import org.gradle.api.Project
import tasks.CreateRcTagTask

@Suppress("unused")
class ProjectConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applySpotless()
            applyKoverProject()
            applyDetekt()
            applySonar()

            tasks.register(CreateRcTagTask.NAME, CreateRcTagTask::class.java)
        }
    }
}
