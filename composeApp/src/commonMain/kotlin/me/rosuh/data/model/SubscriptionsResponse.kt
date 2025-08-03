package me.rosuh.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val SubscriptionsResponse.Subscription.cover: String
    get() {
        return when (this.view) {
            0 -> this.lists?.image ?: ""
            else -> this.feeds?.image ?: feeds?.siteUrl ?: ""
        }
    }

val SubscriptionsResponse.Subscription.realTitle: String
    get() {
        return when (this.view) {
            0 -> (this.lists?.title ?: "").trim()
            else -> (this.feeds?.title ?: "").trim()
        }
    }

@Serializable
class SubscriptionsRequest(
    val userId: List<String>? = null,
    val view: List<Int>? = null
)

@Serializable
data class SubscriptionsResponse(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val data: List<Subscription>,
    @SerialName("message")
    val message: String? = null,
) {
    @Serializable
    data class Subscription(
        val feeds: Feeds? = null,
        @SerialName("isPrivate")
        val isPrivate: Boolean? = null,
        @SerialName("lastViewedAt")
        val lastViewedAt: String? = null,
        @SerialName("listId")
        val listId: String? = null,
        @SerialName("lists")
        val lists: Lists? = null,
        @SerialName("title")
        val title: String? = null,
        @SerialName("userId")
        val userId: String? = null,
        @SerialName("feedId")
        val feedId: String,
        @SerialName("view")
        val view: Int
    ) {

        @Serializable
        data class Feeds(
            @SerialName("description")
            val description: String,
            @SerialName("errorAt")
            val errorAt: String?,
            @SerialName("errorMessage")
            val errorMessage: String?,
            @SerialName("id")
            val id: String,
            @SerialName("image")
            val image: String?,
            @SerialName("owner")
            val owner: String?,
            @SerialName("ownerUserId")
            val ownerUserId: String?,
            @SerialName("siteUrl")
            val siteUrl: String,
            @SerialName("title")
            val title: String,
            @SerialName("type")
            val type: String,
            @SerialName("url")
            val url: String
        )

        @Serializable
        data class Lists(
            @SerialName("description")
            val description: String,
            @SerialName("fee")
            val fee: Int,
            @SerialName("feedIds")
            val feedIds: List<String>,
            @SerialName("id")
            val id: String,
            @SerialName("image")
            val image: String,
            @SerialName("owner")
            val owner: Owner,
            @SerialName("ownerUserId")
            val ownerUserId: String,
            @SerialName("title")
            val title: String,
            @SerialName("type")
            val type: String,
            @SerialName("view")
            val view: Int
        ) {
            @Serializable
            data class Owner(
                @SerialName("createdAt")
                val createdAt: String,
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
    }
}