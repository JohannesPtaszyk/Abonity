package dev.pott.abonity.feature.subscription.add

import dev.pott.abonity.core.entity.subscription.Category
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class AddState(
    val showNameAsTitle: Boolean = false,
    val formState: AddFormState = AddFormState(),
    val categories: ImmutableList<Category> = persistentListOf(),
) {
    enum class SavingState {
        IDLE,
        SAVING,
        SAVED,
        ERROR,
    }
}
