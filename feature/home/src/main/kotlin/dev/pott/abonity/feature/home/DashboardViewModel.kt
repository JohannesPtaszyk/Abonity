package dev.pott.abonity.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.core.domain.notification.NotificationTeaserRepository
import dev.pott.abonity.core.domain.notification.usecase.ShouldShowNotificationTeaserUseCase
import dev.pott.abonity.core.domain.subscription.usecase.GetUpcomingSubscriptionsUseCase
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getUpcomingSubscriptions: GetUpcomingSubscriptionsUseCase,
    shouldShowNotificationTeaser: ShouldShowNotificationTeaserUseCase,
    private val notificationTeaserRepository: NotificationTeaserRepository,
) : ViewModel() {

    private val selectedId: MutableStateFlow<SubscriptionId?> = MutableStateFlow(null)

    val state = combine(
        getUpcomingSubscriptions(PaymentPeriod.MONTHS),
        shouldShowNotificationTeaser(),
        selectedId,
    ) { subscriptions, shouldShowNotificationTeaser, selectedId ->
        DashboardState(
            upcomingSubscriptions = subscriptions.toImmutableList(),
            selectedId = selectedId,
            shouldShowNotificationTeaser = shouldShowNotificationTeaser,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        DashboardState(),
    )

    fun select(subscriptionId: SubscriptionId) {
        selectedId.value = subscriptionId
    }

    fun consumeSelectedId() {
        selectedId.value = null
    }

    fun closeNotificationTeaser(shouldNotShowAgain: Boolean) {
        viewModelScope.launch {
            notificationTeaserRepository.closeTeaser(shouldNotShowAgain)
        }
    }
}
