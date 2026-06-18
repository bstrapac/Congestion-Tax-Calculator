package ent.assigment.congestion_tax_calculator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
	excludeName = [
		"org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration",
		"org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
		"org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
		"org.springframework.boot.session.jdbc.autoconfigure.JdbcSessionAutoConfiguration",
	],
)
class CongestionTaxCalculatorApplication

fun main(args: Array<String>) {
	runApplication<CongestionTaxCalculatorApplication>(*args)
}
