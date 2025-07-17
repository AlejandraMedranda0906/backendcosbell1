package com.cosbell.repository

import com.cosbell.entity.ChatMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatMessageRepository : JpaRepository<ChatMessage, Long> {
    fun findByAppointment_IdOrderByTimestampAsc(appointmentId: Long): List<ChatMessage>
    fun findByReceiver_IdAndReadFalse(receiverId: Long): List<ChatMessage>
} 