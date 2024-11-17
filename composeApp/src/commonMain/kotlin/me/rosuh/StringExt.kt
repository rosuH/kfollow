package me.rosuh

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormatBuilder
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime


@OptIn(FormatStringsInDatetimeFormats::class)
fun String.formatPublishedAt(): String {
    val publishedAt = this
    val publishedInstant = Instant.parse(publishedAt)
    val now = Clock.System.now()
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