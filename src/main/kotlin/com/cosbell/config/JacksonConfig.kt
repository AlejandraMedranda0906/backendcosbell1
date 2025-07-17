package com.cosbell.config

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
class JacksonConfig {

    @Bean
    fun jackson2ObjectMapperBuilderCustomizer(): Jackson2ObjectMapperBuilder.() -> Unit {
        return { // lambda expression
            featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            modules(JavaTimeModule()) // Register the JSR310 module
        }
    }
} 