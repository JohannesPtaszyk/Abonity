package dev.pott.abonity.feature.subscription.add

data class AddState(
    val input: AddFormInput = AddFormInput(),
    val savingState: SavingState = SavingState.IDLE,
    val loading: Boolean,
) {
    enum class SavingState {
        IDLE,
        SAVING,
        SAVED,
    }
}
