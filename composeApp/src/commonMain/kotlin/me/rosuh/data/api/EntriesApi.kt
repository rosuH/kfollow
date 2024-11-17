package me.rosuh.data.api

import io.ktor.http.HttpMethod
import kotlinx.serialization.encodeToString
import me.rosuh.data.NetworkManager
import me.rosuh.data.model.EntryResponse
import me.rosuh.data.model.PostEntriesRequest
import me.rosuh.data.model.PostEntriesResponse

class EntriesApi {

    suspend fun postFeedEntries(
        feedId: String,
        view: Int? = null,
        isArchived: Boolean = false,
        read: Boolean = false,
        publishedAfter: String? = null
    ): PostEntriesResponse {
        return postEntries(
            feedId = feedId,
            view = view,
            isArchived = isArchived,
            read = read,
            publishedAfter = publishedAfter
        )
    }

    suspend fun postListEntries(
        listId: String,
        view: Int? = null,
        isArchived: Boolean = false,
        read: Boolean = false,
        publishedAfter: String? = null
    ): PostEntriesResponse {
        return postEntries(
            listId = listId,
            view = view,
            isArchived = isArchived,
            read = read,
            publishedAfter = publishedAfter
        )
    }

    private suspend fun postEntries(
        feedId: String? = null,
        listId: String? = null,
        view: Int? = null,
        isArchived: Boolean = false,
        read: Boolean = false,
        publishedAfter: String? = null
    ): PostEntriesResponse {
        val body = PostEntriesRequest(
            feedId = feedId,
            listId = listId,
            view = view,
            isArchived = isArchived,
            read = read,
            publishedAfter = publishedAfter
        )
        return NetworkManager.request(
            resource = Entries(),
            body = NetworkManager.json.encodeToString(body),
            method = HttpMethod.Post
        )
    }

    suspend fun getEntry(id: String): EntryResponse {
        return NetworkManager.request(
            resource = Entries.Id(id = id),
            method = HttpMethod.Get
        )
    }
}