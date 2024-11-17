package me.rosuh.data


import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionResponse(
    @SerialName("expires")
    val expires: String,
    @SerialName("invitation")
    val invitation: Invitation,
    @SerialName("role")
    val role: String,
    @SerialName("user")
    val user: User,
    @SerialName("userId")
    val userId: String
) {
    fun isExpired(): Boolean {
        return Clock.System.now().toEpochMilliseconds() > Instant.parse(expires).toEpochMilliseconds()
    }

    @Serializable
    data class Invitation(
        @SerialName("code")
        val code: String,
        @SerialName("createdAt")
        val createdAt: String,
        @SerialName("fromUserId")
        val fromUserId: String,
        @SerialName("toUserId")
        val toUserId: String
    )

    @Serializable
    data class User(
        @SerialName("createdAt")
        val createdAt: String,
        @SerialName("email")
        val email: String,
        @SerialName("emailVerified")
        val emailVerified: String?,
        @SerialName("handle")
        val handle: String,
        @SerialName("id")
        val id: String,
        @SerialName("image")
        val image: String,
        @SerialName("name")
        val name: String
    )
}