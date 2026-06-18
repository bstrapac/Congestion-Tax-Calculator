package ent.assigment.congestion_tax_calculator

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/congestion-tax-calculator")
class CongestionTaxCalculatorController(
    private val congestionTaxCalculatorService: CongestionTaxCalculatorService,
) {

    @PostMapping
    fun calculate(@RequestBody input: String): ResponseEntity<CongestionTaxCalculatorResponse> {
        val calculation = congestionTaxCalculatorService.calculate(input)

        return ResponseEntity.ok(
            CongestionTaxCalculatorResponse(
                dateTime = calculation.dateTime.toString(),
                amount = "${calculation.amount} ${calculation.currency}",
            ),
        )
    }
}

data class CongestionTaxCalculatorResponse(
    val dateTime: String,
    val amount: String,
)
