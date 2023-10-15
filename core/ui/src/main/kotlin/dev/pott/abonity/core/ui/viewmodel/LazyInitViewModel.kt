package dev.pott.abonity.core.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class LazyInitViewModel<STATE>(initialState: STATE) : ViewModel() {

    private val mutableState = MutableStateFlow(initialState)

    val state: StateFlow<STATE> by lazy {
        initialize()
        mutableState.asStateFlow()
    }

    protected abstract fun initialize()

    protected fun updateState(block: STATE.() -> STATE) {
        mutableState.value = mutableState.value.block()
    }
}
