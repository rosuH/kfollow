package me.rosuh.data.api

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Resource("/entries")
@Serializable
class Entries {
    @Resource("{id}")
    @Serializable
    class Id(val parent: Entries = Entries(), val id: String)
}
