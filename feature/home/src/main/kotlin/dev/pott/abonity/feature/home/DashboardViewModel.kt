package dev.pott.abonity.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.core.domain.notification.NotificationTeaserRepository
import dev.pott.abonity.core.domain.notification.usecase.ShouldShowNotificationTeaserUseCase
import dev.pott.abonity.core.domain.subscription.usecase.GetUpcomingPaymentsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getUpcomingSubscriptions: GetUpcomingPaymentsUseCase,
    shouldShowNotificationTeaser: ShouldShowNotificationTeaserUseCase,
    private val notificationTeaserRepository: NotificationTeaserRepository,
) : ViewModel() {

    val state = combine(
        getUpcomingSubscriptions(),
        shouldShowNotificationTeaser(),
    ) { subscriptions, shouldShowNotificationTeaser ->
        DashboardState.Loaded(
            upcomingPayments = subscriptions,
            shouldShowNotificationTeaser = shouldShowNotificationTeaser,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        DashboardState.Loading,
    )

    fun closeNotificationTeaser(shouldNotShowAgain: Boolean) {
        viewModelScope.launch {
            notificationTeaserRepository.closeTeaser(shouldNotShowAgain)
        }
    }
}
