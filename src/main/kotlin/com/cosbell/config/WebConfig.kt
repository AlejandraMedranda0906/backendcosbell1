package com.cosbell.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
//import org.springframework.web.servlet.config.annotation.EnableWebMvc // Eliminar esta línea
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
//@EnableWebMvc // Eliminar esta línea
class WebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        println("WebConfig: Configurando mapeos CORS globales a través de WebMvcConfigurer.") // Log para depuración
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:53992", "http://localhost:4200")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("Content-Type", "Authorization")
            .allowCredentials(true)
            .exposedHeaders("Authorization")
    }
} 