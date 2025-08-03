package me.rosuh.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.rosuh.FLog
import me.rosuh.formatPublishedAt

@Serializable
data class EntryResponse(
    val code: Int,
    val data: EntryData
)

@Serializable
data class PostEntriesResponse(
    val code: Int,
    val remaining: Int? = null,
    val data: List<EntryData>? = null,
    val total: Int? = null
)

@Serializable
class PostEntriesRequest(
    val feedId: String? = null,
    val listId: String? = null,
    val view: Int? = null,
    val isArchived: Boolean? = null,
    val read: Boolean? = null,
    val publishedAfter: String? = null
)

@Serializable
data class EntryData(
    val entries: Entry,
    val feeds: Feed,
    val read: Boolean? = null,
    val view: Int? = null,
    val collections: Collections? = null,
    val settings: Settings? = null,
    val from: List<String> = emptyList<String>()
)

@Serializable
data class Entry(
    val description: String? = null,
    val title: String? = null,
    val id: String,
    val author: String? = null,
    val url: String? = null,
    val guid: String,
    val categories: List<String>? = null,
    @SerialName("authorUrl") val authorUrl: String? = null,
    @SerialName("authorAvatar") val authorAvatar: String? = null,
    @SerialName("insertedAt") val insertedAt: String,
    @SerialName("publishedAt") val publishedAt: String,
    val media: List<Media>? = null,
    val attachments: List<Attachment>? = null,
    val extra: Extra? = null
) {
    val realTitle by lazy {
        title?.trim() ?: ""
    }
    val publishedDate by lazy {
        publishedAt.formatPublishedAt()
    }

    var icon: String = ""
}

val Feed.cover: String
    get() {
        return image ?: ""
    }

@Serializable
data class Feed(
    val type: String,
    val id: String,
    val url: String,
    val description: String? = null,
    val title: String? = null,
    val image: String? = null,
    @SerialName("siteUrl") val siteUrl: String? = null,
    @SerialName("errorMessage") val errorMessage: String? = null,
    @SerialName("errorAt") val errorAt: String? = null,
    @SerialName("ownerUserId") val ownerUserId: String? = null,
    val owner: User? = null,
    @SerialName("tipUsers") val tipUsers: List<User>? = null
)

@Serializable
data class User(
    val name: String? = null,
    val id: String,
    @SerialName("emailVerified") val emailVerified: String? = null,
    val image: String? = null,
    val handle: String? = null,
    @SerialName("createdAt") val createdAt: String
)

@Serializable
data class Media(
    val type: String,
    val url: String,
    val width: Double? = null,
    val height: Double? = null,
    @SerialName("preview_image_url") val previewImageUrl: String? = null,
    val blurhash: String? = null
)

@Serializable
data class Attachment(
    val url: String,
    val title: String? = null,
    @SerialName("duration_in_seconds") val durationInSeconds: String? = null,
    @SerialName("mime_type") val mimeType: String? = null,
    @SerialName("size_in_bytes") val sizeInBytes: String? = null
)

@Serializable
data class Extra(
    val links: List<Link>? = null
)

@Serializable
data class Link(
    val type: String,
    val url: String,
    @SerialName("content_html") val contentHtml: String? = null
)

@Serializable
data class Collections(
    @SerialName("createdAt") val createdAt: String
)

@Serializable
data class Settings(
    val summary: Boolean? = null,
    val translation: String? = null,
    val readability: Boolean? = null,
    val silence: Boolean? = null,
    @SerialName("newEntryNotification") val newEntryNotification: Boolean? = null,
    @SerialName("rewriteRules") val rewriteRules: List<RewriteRule>? = null,
    val webhooks: List<String>? = null
)

@Serializable
data class RewriteRule(
    val from: String,
    val to: String
)