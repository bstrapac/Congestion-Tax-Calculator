package ent.assigment.congestion_tax_calculator

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf.disable() } // Crucial: Stops Spring from rejecting Postman POST requests
            .authorizeHttpRequests { auth ->
                auth.anyRequest().permitAll() // Allows Postman to hit any route without 401s
            }
        return http.build()
    }
}
