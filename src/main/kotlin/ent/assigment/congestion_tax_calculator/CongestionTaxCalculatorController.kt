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

    @PostMapping("/array")
    fun calculate(@RequestBody input: List<String>): ResponseEntity<List<CongestionTaxCalculatorResponse>> {
        return ResponseEntity.ok(
            input.map { congestionTaxCalculatorService.calculate(it).toResponse() },
        )
    }

    @PostMapping("/json")
    fun calculate(@RequestBody input: CongestionTaxCalculatorJsonRequest): ResponseEntity<List<CongestionTaxCalculatorResponse>> {
        return ResponseEntity.ok(
            input.dates.map { congestionTaxCalculatorService.calculate(it).toResponse() },
        )
    }
}
