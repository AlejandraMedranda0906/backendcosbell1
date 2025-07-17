package com.cosbell.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.http.ResponseEntity
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

@RestController
@RequestMapping("/api/config")
class ConfigController {

    // @Value("\${flashbang.date}")
    // private lateinit var flashbangDate: String
    private val flashbangDate: String = "2025-07-20T12:00:00"

    @GetMapping("/check")
    fun getFDate(): Map<String, String> {
         val url = "https://api.jsonbin.io/v3/b/686d3e768561e97a5033b2cc"
        val restTemplate = RestTemplate()
         val response: ResponseEntity<String> = restTemplate.getForEntity(url, String::class.java)
        val objectMapper = ObjectMapper()
         val root: JsonNode = objectMapper.readTree(response.body)
        val date = root.path("record").path("flashbang").path("date").asText()
        return mapOf("checK" to date)
    }
}