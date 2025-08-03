package me.rosuh

import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime


@OptIn(FormatStringsInDatetimeFormats::class, ExperimentalTime::class)
fun String.formatPublishedAt(): String {
    val publishedAt = this
    val publishedInstant = kotlin.time.Instant.parse(publishedAt)
    val now = kotlin.time.Clock.System.now()
    val diffDuration = now - publishedInstant
    val publishedDateTime = publishedInstant.toLocalDateTime(TimeZone.currentSystemDefault())

    val time = publishedInstant.format(DateTimeComponents.Format {
        byUnicodePattern("HH:mm")
    })

    val date = publishedInstant.format(DateTimeComponents.Format {
        byUnicodePattern("yyyy-MM-dd")
    })

    return if (diffDuration.inWholeDays < 1) {
        time
    } else {
        date
    }
}