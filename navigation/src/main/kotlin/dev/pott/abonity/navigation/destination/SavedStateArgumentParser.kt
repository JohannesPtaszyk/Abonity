package dev.pott.abonity.navigation.destination

import androidx.lifecycle.SavedStateHandle

fun interface SavedStateArgumentParser<T> {
    fun parse(savedStateHandle: SavedStateHandle): T
}
