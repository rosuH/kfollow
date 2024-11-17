package me.rosuh.data.api

import io.ktor.http.HttpMethod
import me.rosuh.data.NetworkManager
import me.rosuh.data.model.GetWalletsResponse

class WalletsApi {
    suspend fun getWallets(): GetWalletsResponse {
        return NetworkManager.request(
            resource = Wallets(),
            method = HttpMethod.Get
        )
    }
}