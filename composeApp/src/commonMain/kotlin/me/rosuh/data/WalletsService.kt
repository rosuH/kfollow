package me.rosuh.data

import io.ktor.resources.Resource
import me.rosuh.data.model.GetWalletsResponse


@Resource("/wallets")
class WalletsResource

class WalletsService {
    suspend fun getWallets(): GetWalletsResponse {
        return NetworkManager.request(WalletsResource())
    }
}