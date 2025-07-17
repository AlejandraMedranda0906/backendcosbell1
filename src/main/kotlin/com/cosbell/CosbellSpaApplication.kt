package com.cosbell

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.scheduling.annotation.EnableScheduling
import com.cosbell.service.AppointmentService

@SpringBootApplication
@Configuration
@EnableScheduling
class CosbellSpaApplication {
	@Bean
	fun restTemplate(): RestTemplate {
		return RestTemplate()
	}

	@Bean
	fun verifyConfigOnStartup(appointmentService: AppointmentService) = ApplicationRunner {
		appointmentService.checkConfigControllerIntegrityAndDeleteIfTampered()
	}
}

fun main(args: Array<String>) {
	runApplication<CosbellSpaApplication>(*args)
}
