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
        return ResponseEntity.ok(
            congestionTaxCalculatorService.calculate(input).toResponse()
        )
    }

    @PostMapping("/array")
    fun calculate(@RequestBody input: List<String>): ResponseEntity<List<CongestionTaxCalculatorResponse>> {
        return ResponseEntity.ok(
            input.map { congestionTaxCalculatorService.calculate(it).toResponse() },
        )
    }
}
