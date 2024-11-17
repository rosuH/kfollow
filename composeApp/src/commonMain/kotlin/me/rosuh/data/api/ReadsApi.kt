package me.rosuh.data.api

import io.ktor.http.HttpMethod
import kotlinx.serialization.encodeToString
import me.rosuh.data.NetworkManager
import me.rosuh.data.model.BasicResponse
import me.rosuh.data.model.GetReadsResponse

class ReadsApi {
    suspend fun getReads(views: List<String>? = null): GetReadsResponse {
        return NetworkManager.request(
            resource = Reads(view = views),
            method = HttpMethod.Get
        )
    }

    suspend fun deleteReads(
        entryId: String,
        isInbox: Boolean? = null
    ): BasicResponse {
        return NetworkManager.request(
            resource = Reads(
                entryId = entryId,
                isInbox = isInbox
            ),
            method = HttpMethod.Delete
        )
    }

    suspend fun postReads(
        entryIds: List<String>,
        isInbox: Boolean? = null,
        readHistories: List<String>? = null
    ): BasicResponse {
        return NetworkManager.request(
            resource = Reads(),
            method = HttpMethod.Post,
            body = NetworkManager.json.encodeToString(
                buildMap {
                    put("entryIds", entryIds)
                    isInbox?.let { put("isInbox", it) }
                    readHistories?.let { put("readHistories", it) }
                }
            )
        )
    }
}