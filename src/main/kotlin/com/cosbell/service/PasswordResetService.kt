package com.cosbell.service

import com.cosbell.entity.PasswordResetToken
import com.cosbell.entity.User
import com.cosbell.repository.PasswordResetTokenRepository
import com.cosbell.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import org.springframework.transaction.annotation.Transactional

@Service
class PasswordResetService(
    private val userRepository: UserRepository,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val notificationService: NotificationService
) {
    @Transactional
    fun createPasswordResetToken(email: String): String {
        println("[PasswordResetService] Entrando a createPasswordResetToken con: $email")
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("No existe un usuario con ese correo.")
        passwordResetTokenRepository.deleteByUser_Id(user.id)
        val token = UUID.randomUUID().toString()
        val expiration = LocalDateTime.now().plusHours(1)
        val resetToken = PasswordResetToken(
            token = token,
            user = user,
            expirationDate = expiration
        )
        passwordResetTokenRepository.save(resetToken)
        val resetLink = "http://localhost:4200/reset-password?token=$token"
        println("[PasswordResetService] Enviando correo de recuperación a ${user.email}...")
        notificationService.sendPasswordResetEmail(user.email, user.name, resetLink)
        return token
    }

    fun validateToken(token: String): User {
        val resetToken = passwordResetTokenRepository.findByToken(token)
            .orElseThrow { IllegalArgumentException("Token inválido o expirado.") }
        if (resetToken.expirationDate.isBefore(LocalDateTime.now())) {
            throw IllegalArgumentException("El token ha expirado.")
        }
        return resetToken.user
    }

    @Transactional
    fun resetPassword(token: String, newPassword: String) {
        val user = validateToken(token)
        user.password = passwordEncoder.encode(newPassword)
        userRepository.save(user)
        passwordResetTokenRepository.deleteByUser_Id(user.id)
    }
} 