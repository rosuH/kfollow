package me.rosuh.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetWalletsResponse(
    val code: Int,
    val data: List<Wallet>
)

@Serializable
data class Wallet(
    @SerialName("createdAt") val createdAt: String,
    val userId: String,
    val addressIndex: Int,
    val address: String? = null,
    val powerToken: String,
    val dailyPowerToken: String,
    val cashablePowerToken: String,
    val level: Level? = null,
    val todayDailyPower: String
)

@Serializable
data class Level(
    val rank: Int? = null,
    val level: Int? = null,
    val prevActivityPoints: Int? = null,
    val activityPoints: Int? = null
)

@Serializable
data class PostWalletsResponse(
    val code: Int,
    val data: String
)