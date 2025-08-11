package com.cosbell.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class WhatsAppService(
    @Value("\${whatsapp.phone-number-id:}") private val phoneNumberId: String,
    @Value("\${whatsapp.access-token:}") private val accessToken: String,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val http = RestTemplate()

    fun sendText(toE164: String, body: String): Boolean {
        if (phoneNumberId.isBlank() || accessToken.isBlank()) {
            log.warn("[WhatsApp] No configurado. Falta phone-number-id o access-token")
            return false
        }
        val url = "https://graph.facebook.com/v20.0/$phoneNumberId/messages"

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(accessToken)
        }
        val payload = mapOf(
            "messaging_product" to "whatsapp",
            "to" to toE164.filter { it.isDigit() }, // E.164 solo dígitos (ej. 5939xxxxxxx)
            "type" to "text",
            "text" to mapOf("preview_url" to true, "body" to body)
        )

        return try {
            val res = http.postForEntity(url, HttpEntity(payload, headers), Map::class.java)
            if (res.statusCode.is2xxSuccessful) {
                log.info("[WhatsApp] OK -> {}", res.body)
                true
            } else {
                log.error("[WhatsApp] HTTP {} -> {}", res.statusCodeValue, res.body)
                false
            }
        } catch (e: Exception) {
            log.error("[WhatsApp] Excepción enviando: ${e.message}", e)
            false
        }
    }

}
