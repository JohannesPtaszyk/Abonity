package dev.pott.abonity.navigation.destination

import androidx.lifecycle.SavedStateHandle

interface SavedStateArgumentParser<T> {
    fun parse(savedStateHandle: SavedStateHandle): T
}
