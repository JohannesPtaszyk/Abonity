package util

import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import javax.inject.Inject

private const val VERSION_CODE_OFFSET = 2

abstract class RcTagVersionCodeValueSource : ValueSource<Int, ValueSourceParameters.None> {

    @get:Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): Int {
        val rcTags = execOperations.getRcTags()
        val versionCode = rcTags.count() + VERSION_CODE_OFFSET
        println("Version Code: $versionCode")
        return versionCode
    }
}
