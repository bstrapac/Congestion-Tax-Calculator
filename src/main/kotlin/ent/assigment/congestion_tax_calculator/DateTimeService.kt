package ent.assigment.congestion_tax_calculator

import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.MonthDay
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class DateTimeService {

    private val supportedYear = 2013
    private val taxZone = ZoneId.of("Europe/Stockholm")
    private val localInputFormatters = listOf(
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
    )
    private val taxFreeDates = setOf(
        "--01-01",
        "--01-05",
        "--01-06",
        "--03-28",
        "--03-29",
        "--03-30",
        "--03-31",
        "--04-01",
        "--04-30",
        "--05-01",
        "--05-08",
        "--05-09",
        "--05-18",
        "--05-19",
        "--06-05",
        "--06-06",
        "--06-21",
        "--06-22",
        "--11-01",
        "--11-02",
        "--12-24",
        "--12-25",
        "--12-26",
        "--12-31",
    ).map(MonthDay::parse).toSet()

    fun parseDateTime(input: String): LocalDateTime {
        val normalizedInput = input.trim()

        if (normalizedInput.isEmpty()) {
            throw IllegalArgumentException("Input must contain a date and time")
        }

        return parseSupportedDateTime(normalizedInput).also { dateTime ->
            if (dateTime.year != supportedYear) {
                throw IllegalArgumentException("Input must be in year $supportedYear")
            }
        }
    }

    fun isTaxFree(date: LocalDate): Boolean =
        date.dayOfWeek == DayOfWeek.SATURDAY ||
            date.dayOfWeek == DayOfWeek.SUNDAY ||
            date.monthValue == 7 ||
            MonthDay.from(date) in taxFreeDates

    private fun parseSupportedDateTime(input: String): LocalDateTime {
        localInputFormatters.forEach { formatter ->
            runCatching { return LocalDateTime.parse(input, formatter) }
        }

        runCatching {
            return OffsetDateTime.parse(input, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                .atZoneSameInstant(taxZone)
                .toLocalDateTime()
        }

        runCatching {
            return ZonedDateTime.parse(input, DateTimeFormatter.ISO_ZONED_DATE_TIME)
                .withZoneSameInstant(taxZone)
                .toLocalDateTime()
        }

        throw IllegalArgumentException(
            "Input must be a valid date/time. Supported examples: 2013-01-14 21:00:00, " +
                "14/01/2013 21:00:00, 2013-01-14T21:00:00, 2013-01-14T20:00:00Z",
        )
    }
}
