package com.cosbell.service

import com.cosbell.dto.ChatMessageDto
import com.cosbell.entity.ChatMessage
import com.cosbell.repository.ChatMessageRepository
import com.cosbell.repository.AppointmentRepository
import com.cosbell.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ChatService(
    private val chatMessageRepository: ChatMessageRepository,
    private val appointmentRepository: AppointmentRepository,
    private val userRepository: UserRepository
) {
    fun getMessagesByAppointment(appointmentId: Long): List<ChatMessageDto> {
        return chatMessageRepository.findByAppointment_IdOrderByTimestampAsc(appointmentId)
            .map { it.toDto() }
    }

    @Transactional
    fun sendMessage(dto: ChatMessageDto): ChatMessageDto {
        val appointment = appointmentRepository.findById(dto.appointmentId)
            .orElseThrow { IllegalArgumentException("Cita no encontrada") }
        val sender = userRepository.findById(dto.senderId)
            .orElseThrow { IllegalArgumentException("Remitente no encontrado") }
        
        // Determinar el destinatario automáticamente si no se proporciona uno válido
        val receiver = if (dto.receiverId != null && dto.receiverId > 0) {
            userRepository.findById(dto.receiverId)
                .orElseThrow { IllegalArgumentException("Destinatario no encontrado") }
        } else {
            // Si el sender es el cliente, el receiver es el empleado, y viceversa
            if (sender.id == appointment.userId) {
                // El sender es el cliente, el receiver es el empleado
                appointment.employee
            } else if (sender.id == appointment.employee.id) {
                // El sender es el empleado, el receiver es el cliente
                userRepository.findById(appointment.userId)
                    .orElseThrow { IllegalArgumentException("Cliente de la cita no encontrado") }
            } else {
                throw IllegalArgumentException("El remitente no está asociado a esta cita")
            }
        }
        
        val message = ChatMessage(
            appointment = appointment,
            sender = sender,
            receiver = receiver,
            content = dto.content,
            timestamp = LocalDateTime.now(),
            read = false
        )
        return chatMessageRepository.save(message).toDto()
    }

    @Transactional
    fun markMessagesAsRead(appointmentId: Long, userId: Long) {
        val messages = chatMessageRepository.findByAppointment_IdOrderByTimestampAsc(appointmentId)
            .filter { it.receiver.id == userId && !it.read }
        messages.forEach { msg ->
            chatMessageRepository.save(msg.copy(read = true))
        }
    }

    private fun ChatMessage.toDto(): ChatMessageDto = ChatMessageDto(
        id = this.id,
        appointmentId = this.appointment.id!!,
        senderId = this.sender.id!!,
        senderName = this.sender.name,
        receiverId = this.receiver.id!!,
        receiverName = this.receiver.name,
        content = this.content,
        timestamp = this.timestamp,
        read = this.read
    )
} 