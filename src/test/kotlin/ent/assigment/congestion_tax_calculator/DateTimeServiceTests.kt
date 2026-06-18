package ent.assigment.congestion_tax_calculator

import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DateTimeServiceTests {

    private val dateTimeService = DateTimeService()

    @Test
    fun `recognizes yearless tax free dates in other years`() {
        val taxFreeDates = listOf(
            LocalDate.of(2014, 1, 1),
            LocalDate.of(2015, 5, 1),
            LocalDate.of(2016, 12, 25),
        )

        taxFreeDates.forEach { date ->
            assertTrue(dateTimeService.isTaxFree(date))
        }
    }

    @Test
    fun `accepts supported date time input formats`() {
        val testCases = mapOf(
            "14/01/2013 07:00:00" to LocalDateTime.of(2013, 1, 14, 7, 0),
            "14/01/2013 07:00" to LocalDateTime.of(2013, 1, 14, 7, 0),
            "2013-01-14 07:00" to LocalDateTime.of(2013, 1, 14, 7, 0),
            "2013-01-14T07:00:00" to LocalDateTime.of(2013, 1, 14, 7, 0),
            "2013-01-14T06:00:00Z" to LocalDateTime.of(2013, 1, 14, 7, 0),
            "2013-01-14T08:00:00+02:00" to LocalDateTime.of(2013, 1, 14, 7, 0),
        )

        testCases.forEach { (input, expectedDateTime) ->
            assertEquals(expectedDateTime, dateTimeService.parseDateTime(input))
        }
    }

    @Test
    fun `recognizes weekends public holidays days before public holidays and July as tax free`() {
        val taxFreeDates = listOf(
            LocalDate.of(2013, 1, 12),
            LocalDate.of(2013, 5, 1),
            LocalDate.of(2013, 4, 30),
            LocalDate.of(2013, 7, 1),
        )

        taxFreeDates.forEach { date ->
            assertTrue(dateTimeService.isTaxFree(date))
        }
    }

    @Test
    fun `throws exception when date time is not in 2013`() {
        assertFailsWith<IllegalArgumentException> {
            dateTimeService.parseDateTime("2014-01-14 07:00:00")
        }
    }

    @Test
    fun `throws exception when string input cannot be converted to date time`() {
        assertFailsWith<IllegalArgumentException> {
            dateTimeService.parseDateTime("not-a-date-time")
        }
    }
}
