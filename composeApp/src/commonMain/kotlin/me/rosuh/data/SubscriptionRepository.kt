package me.rosuh.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import me.rosuh.FLog
import me.rosuh.LoadState
import me.rosuh.data.api.SubscriptionsApi
import me.rosuh.data.model.SubscriptionsResponse
import me.rosuh.data.model.cover
import me.rosuh.utils.Either
import me.rosuh.utils.either


class SubscriptionRepository {

    companion object {
        private const val TAG = "SubscriptionRepository"
    }

    private val subscriptionApi = SubscriptionsApi()

    suspend fun loadSubscription(
        view: Int? = null
    ): Either<Throwable, SubscriptionsResponse> = withContext(Dispatchers.IO) {
        FLog.d(TAG, "load subscription")
        return@withContext either<Throwable, SubscriptionsResponse> {
            subscriptionApi.getSubscriptions(view)
        }
    }
}