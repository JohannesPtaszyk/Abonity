package dev.pott.abonity.feature.subscription.add

data class AddState(
    val showNameAsTitle: Boolean = false,
    val input: AddFormState = AddFormState(),
    val savingState: SavingState = SavingState.IDLE,
    val loading: Boolean,
) {
    enum class SavingState {
        IDLE,
        SAVING,
        SAVED,
        ERROR,
    }
}
