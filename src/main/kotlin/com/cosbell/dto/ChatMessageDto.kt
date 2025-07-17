package com.cosbell.dto
import java.time.LocalDateTime

data class ChatMessageDto(
    val id: Long? = null,
    val appointmentId: Long,
    val senderId: Long,
    val senderName: String? = null,
    val receiverId: Long? = null,
    val receiverName: String? = null,
    val content: String,
    val timestamp: LocalDateTime? = null,
    val read: Boolean = false
) 