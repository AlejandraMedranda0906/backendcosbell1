package com.cosbell.controller

import com.cosbell.dto.ChatMessageDto
import com.cosbell.service.ChatService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChatWebSocketController(
    private val chatService: ChatService,
    private val objectMapper: ObjectMapper
) : TextWebSocketHandler() {

    // Manejo de WebSocket nativo
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            val payload = message.payload
            val data = objectMapper.readTree(payload)
            
            when (data.get("type").asText()) {
                "SEND_MESSAGE" -> {
                    val messageDto = objectMapper.treeToValue(data.get("message"), ChatMessageDto::class.java)
                    val savedMessage = chatService.sendMessage(messageDto)
                    
                    // Enviar respuesta al cliente
                    val response = mapOf(
                        "type" to "CHAT_MESSAGE",
                        "message" to savedMessage
                    )
                    session.sendMessage(TextMessage(objectMapper.writeValueAsString(response)))
                }
                "JOIN" -> {
                    val appointmentId = data.get("appointmentId").asLong()
                    // El usuario se une al chat
                    val response = mapOf(
                        "type" to "JOINED",
                        "appointmentId" to appointmentId
                    )
                    session.sendMessage(TextMessage(objectMapper.writeValueAsString(response)))
                }
            }
        } catch (e: Exception) {
            println("Error procesando mensaje WebSocket: ${e.message}")
        }
    }
} 