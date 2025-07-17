package com.cosbell.controller

import com.cosbell.dto.ChatMessageDto
import com.cosbell.service.ChatService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat")
class ChatController(private val chatService: ChatService) {

    @GetMapping("/appointment/{appointmentId}")
    fun getMessages(@PathVariable appointmentId: Long): ResponseEntity<List<ChatMessageDto>> =
        ResponseEntity.ok(chatService.getMessagesByAppointment(appointmentId))

    @PostMapping
    fun sendMessage(@RequestBody message: ChatMessageDto): ResponseEntity<ChatMessageDto> =
        ResponseEntity.ok(chatService.sendMessage(message))

    @PostMapping("/appointment/{appointmentId}/read/{userId}")
    fun markAsRead(@PathVariable appointmentId: Long, @PathVariable userId: Long): ResponseEntity<Void> {
        chatService.markMessagesAsRead(appointmentId, userId)
        return ResponseEntity.ok().build()
    }
} 