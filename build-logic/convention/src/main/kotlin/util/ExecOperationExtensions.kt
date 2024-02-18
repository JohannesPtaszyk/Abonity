package util

import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

fun ExecOperations.execToString(vararg args: String): String {
    val output = ByteArrayOutputStream()
    exec {
        commandLine(*args)
        standardOutput = output
    }
    return String(output.toByteArray(), Charset.defaultCharset())
}
