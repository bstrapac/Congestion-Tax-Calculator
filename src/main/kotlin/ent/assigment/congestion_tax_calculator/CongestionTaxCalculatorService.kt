package ent.assigment.congestion_tax_calculator

import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class CongestionTaxCalculatorService(
    private val dateTimeService: DateTimeService,
) {

    open var currency = "SEK"
    private val maxDailyAmount = 60

    fun calculate(input: String): CongestionTaxCalculation {
        val dateTime = parseDateTime(input)

        return CongestionTaxCalculation(
            dateTime = dateTime,
            amount = minOf(amountFor(dateTime), maxDailyAmount),
            currency = currency,
        )
    }

    fun parseDateTime(input: String): LocalDateTime {
        return dateTimeService.parseDateTime(input)
    }

    private fun amountFor(dateTime: LocalDateTime): Int {
        if (dateTimeService.isTaxFree(dateTime.toLocalDate())) {
            return 0
        }

        return amountFor(dateTime.toLocalTime())
    }

    private fun amountFor(time: LocalTime): Int =
        when {
            time.isInRange("06:00", "06:29") -> 8
            time.isInRange("06:30", "06:59") -> 13
            time.isInRange("07:00", "07:59") -> 18
            time.isInRange("08:00", "08:29") -> 13
            time.isInRange("08:30", "14:59") -> 8
            time.isInRange("15:00", "15:29") -> 13
            time.isInRange("15:30", "16:59") -> 18
            time.isInRange("17:00", "17:59") -> 13
            time.isInRange("18:00", "18:29") -> 8
            else -> 0
        }

    private fun LocalTime.isInRange(start: String, end: String): Boolean {
        val startTime = LocalTime.parse(start)
        val endTime = LocalTime.parse(end)

        return !isBefore(startTime) && !isAfter(endTime)
    }
}

data class CongestionTaxCalculation(
    val dateTime: LocalDateTime,
    val amount: Int,
    val currency: String,
)
