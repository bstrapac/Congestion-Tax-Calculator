package ent.assigment.congestion_tax_calculator

import java.time.LocalDateTime

data class CongestionTaxCalculatorResponse(
    val dateTime: String,
    val amount: String,
)

fun CongestionTaxCalculation.toResponse(): CongestionTaxCalculatorResponse =
    CongestionTaxCalculatorResponse(
        dateTime = dateTime.toString(),
        amount = "$amount $currency",
    )

data class CongestionTaxCalculation(
    val dateTime: LocalDateTime,
    val amount: Int,
    val currency: String,
)
