package ent.assigment.congestion_tax_calculator

import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import kotlin.test.assertEquals
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.spyk
import io.mockk.verify
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class CongestionTaxCalculatorControllerTests {

    private val service = spyk(CongestionTaxCalculatorService(DateTimeService()))
    private val congestionTaxCalculatorService : CongestionTaxCalculatorService = mockk(relaxed = true)
    private val objectMapper = ObjectMapper().apply {
        registerModule(JavaTimeModule())
        registerKotlinModule()
    }
    private val mockMvc = MockMvcBuilders
        .standaloneSetup(CongestionTaxCalculatorController(congestionTaxCalculatorService))
        .build()

    @Test
    fun `converts string input to date time`() {
        val domainResult = CongestionTaxCalculation(
            LocalDateTime.parse("2013-01-14T21:00:00"),
            0,
            "SEK"
        )
        every { congestionTaxCalculatorService.calculate(any()) } returns domainResult
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
        every { congestionTaxCalculatorService.currency = "EUR" } just runs
        val domainResult = CongestionTaxCalculation(
            LocalDateTime.parse("2013-01-14T07:00:00"),
            18,
            "EUR"
        )
        every { congestionTaxCalculatorService.calculate(any()) } returns domainResult
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

    @Test
    fun `should calculate tax for array endpoint`() {
        val input = listOf("2013-01-14 06:00:00", "2013-01-14 08:00:00")

        val mockResult = listOf(
            CongestionTaxCalculation(
        LocalDateTime.parse("2013-01-14 06:00:00",
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),8 ,"SEK"
            ),
            CongestionTaxCalculation(
        LocalDateTime.parse("2013-01-14 08:00:00",
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),13 ,"SEK"
            ),
        )
       var expectedResponse = listOf(
            CongestionTaxCalculatorResponse( "2013-01-14T06:00","8 SEK"),
            CongestionTaxCalculatorResponse( "2013-01-14T08:00","13 SEK"),
            )

        input.forEachIndexed { index, dateStr ->
            every { congestionTaxCalculatorService.calculate(dateStr) } returns mockResult[index]
        }

        val result = mockMvc.post("/congestion-tax-calculator/array") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(input)
        }.andReturn()

        assertEquals(200, result.response.status)

        val actualResponse = objectMapper.readValue(
            result.response.contentAsString,
            Array<CongestionTaxCalculatorResponse>::class.java
        ).toList()

        assertEquals(2, actualResponse.size)
        assertEquals(expectedResponse, actualResponse)

        verify(exactly = 1) { congestionTaxCalculatorService.calculate("2013-01-14 06:00:00") }
    }
}
