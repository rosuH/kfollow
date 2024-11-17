
package me.rosuh.data.api

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Resource("/reads")
@Serializable
class Reads(
    val view: List<String>? = null,
    val entryId: String? = null,
    val isInbox: Boolean? = null
)