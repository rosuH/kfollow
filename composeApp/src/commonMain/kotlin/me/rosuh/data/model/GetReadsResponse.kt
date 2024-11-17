package me.rosuh.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GetReadsResponse(
    val code: Int,
    val data: Map<String, Int>
)

@Serializable
data class BasicResponse(
    val code: Int
)