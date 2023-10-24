package dev.pott.abonity.feature.subscription.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.core.domain.SubscriptionRepository
import dev.pott.abonity.core.entity.SubscriptionId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SubscriptionRepository,
) : ViewModel() {

    private val args = DetailScreenDestination.getArgs(savedStateHandle)
    private val currentDetailId = MutableStateFlow(
        args.id?.let { SubscriptionId(it) }
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = currentDetailId.flatMapLatest { id ->
        if (id != null) {
            repository.getSubscription(id)
        } else {
            flowOf(null)
        }
    }.map {
        DetailState(subscription = it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = DetailState(),
        started = SharingStarted.WhileSubscribed()
    )
    fun setId(detailId: SubscriptionId?) {
        currentDetailId.value = detailId
    }
}
