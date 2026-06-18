package ent.assigment.congestion_tax_calculator

import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import kotlin.test.assertEquals

class CongestionTaxCalculatorControllerTests {

    private val service = CongestionTaxCalculatorService(DateTimeService())
    private val mockMvc = MockMvcBuilders
        .standaloneSetup(CongestionTaxCalculatorController(service))
        .build()

    @Test
    fun `converts string input to date time`() {
        mockMvc.post("/congestion-tax-calculator") {
            contentType = MediaType.TEXT_PLAIN
            content = "2013-01-14 21:00:00"
        }.andExpect {
            status { isOk() }
            content { json("""{"dateTime":"2013-01-14T21:00","amount":"0 SEK"}""") }
        }
    }

    @Test
    fun `returns amount with configured currency`() {
        service.currency = "EUR"

        mockMvc.post("/congestion-tax-calculator") {
            contentType = MediaType.TEXT_PLAIN
            content = "2013-01-14 07:00:00"
        }.andExpect {
            status { isOk() }
            content { json("""{"dateTime":"2013-01-14T07:00","amount":"18 EUR"}""") }
        }
    }

    @Test
    fun `calculates amount from Gothenburg congestion tax price list`() {
        val testCases = mapOf(
            "2013-01-14 06:00:00" to 8,
            "2013-01-14 06:30:00" to 13,
            "2013-01-14 07:00:00" to 18,
            "2013-01-14 08:00:00" to 13,
            "2013-01-14 08:30:00" to 8,
            "2013-01-14 15:00:00" to 13,
            "2013-01-14 15:30:00" to 18,
            "2013-01-14 17:00:00" to 13,
            "2013-01-14 18:00:00" to 8,
            "2013-01-14 18:30:00" to 0,
            "2013-01-14 05:59:00" to 0,
        )

        testCases.forEach { (input, expectedAmount) ->
            val calculation = service.calculate(input)

            assertEquals(expectedAmount, calculation.amount)
        }
    }

}
