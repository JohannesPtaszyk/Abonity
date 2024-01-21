package dev.pott.abonity.feature.subscription.overview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.core.domain.subscription.SubscriptionRepository
import dev.pott.abonity.core.domain.subscription.usecase.GetSubscriptionsWithFilterUseCase
import dev.pott.abonity.core.entity.subscription.SubscriptionFilterItem
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getFilteredSubscriptions: GetSubscriptionsWithFilterUseCase,
    private val subscriptionRepository: SubscriptionRepository,
) : ViewModel() {

    private val args = OverviewScreenDestination.Args.parse(savedStateHandle)

    private val selectedDetailIdFlow = MutableStateFlow(args.detailId)

    private val selectedFilterItemsFlow = MutableStateFlow<ImmutableList<SubscriptionFilterItem>>(
        persistentListOf(),
    )

    val state = combine(
        selectedDetailIdFlow,
        getFilteredSubscriptions(selectedFilterItemsFlow),
    ) { detailId, subscriptionsWithFilter ->
        OverviewState.Loaded(
            subscriptions = subscriptionsWithFilter.filteredSubscriptions.toImmutableList(),
            detailId = detailId,
            filter = subscriptionsWithFilter.filter,
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = OverviewState.Loading,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
    )

    fun openDetails(id: SubscriptionId) {
        selectedDetailIdFlow.value = id
    }

    fun consumeDetails() {
        selectedDetailIdFlow.value = null
    }

    fun toggleFilter(item: SubscriptionFilterItem) {
        viewModelScope.launch {
            selectedFilterItemsFlow.update {
                if (it.contains(item)) {
                    it - item
                } else {
                    it + item
                }.toImmutableList()
            }
        }
    }

    fun delete(id: SubscriptionId) {
        viewModelScope.launch {
            subscriptionRepository.deleteSubscription(id)
            selectedDetailIdFlow.value = null
        }
    }
}
