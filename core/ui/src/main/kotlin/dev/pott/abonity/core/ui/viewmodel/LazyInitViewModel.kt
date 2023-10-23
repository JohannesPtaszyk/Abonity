package dev.pott.abonity.core.ui.viewmodel

import androidx.lifecycle.ViewModel
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlin.math.log

abstract class LazyInitViewModel<STATE>(initialState: STATE) : ViewModel() {

    private val logger = Logger.withTag(this::class.java.simpleName)
    private val mutableState = MutableStateFlow(initialState)

    val state: StateFlow<STATE> by lazy {
        logger.v { "Initializing" }
        initialize()
        logger.v { "Initialization finished" }
        mutableState.asStateFlow()
    }

    protected abstract fun initialize()

    protected fun updateState(block: STATE.() -> STATE) {
        val newState = mutableState.updateAndGet(block)
        logger.v(newState.toString())
    }
}
