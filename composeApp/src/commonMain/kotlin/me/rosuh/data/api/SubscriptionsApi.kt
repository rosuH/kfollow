package me.rosuh.data.api

import io.ktor.http.HttpMethod
import me.rosuh.data.NetworkManager
import me.rosuh.data.model.SubscriptionsResponse

class SubscriptionsApi {
    suspend fun getSubscriptions(
        view: Int? = null
    ): SubscriptionsResponse {
        return NetworkManager.request(
            resource = Subscriptions(view),
            method = HttpMethod.Get
        )
    }
}