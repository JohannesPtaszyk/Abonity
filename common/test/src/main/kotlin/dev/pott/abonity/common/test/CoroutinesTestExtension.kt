package dev.pott.abonity.common.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutinesTestExtension :
    BeforeEachCallback,
    AfterEachCallback {
    private val dispatcher = StandardTestDispatcher()

    override fun beforeEach(context: ExtensionContext?) {
        Dispatchers.setMain(dispatcher)
        injectTestDispatcher(context)
    }

    private fun injectTestDispatcher(context: ExtensionContext?) {
        val testInstance = context?.testInstance?.get() ?: return
        val fields = testInstance.javaClass.declaredFields

        for (field in fields) {
            if (field.isAnnotationPresent(InjectTestDispatcher::class.java)) {
                field.isAccessible = true
                field[testInstance] = dispatcher
            }
        }
    }

    override fun afterEach(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }
}
