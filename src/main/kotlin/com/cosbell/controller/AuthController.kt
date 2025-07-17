package com.cosbell.controller

import com.cosbell.dto.AuthResponse
import com.cosbell.dto.LoginRequest
import com.cosbell.dto.RegisterRequest
import com.cosbell.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.cosbell.service.PasswordResetService

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val passwordResetService: PasswordResetService
) {
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> =
        ResponseEntity.ok(authService.register(request))

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> =
        ResponseEntity.ok(authService.login(request))

    @PostMapping("/forgot-password")
    fun forgotPassword(@RequestParam email: String): ResponseEntity<String> {
        println("[AuthController] Solicitud de recuperación recibida para: $email")
        return try {
            passwordResetService.createPasswordResetToken(email)
            ResponseEntity.ok("Si el correo existe, se ha enviado un enlace de recuperación.")
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.ok("Si el correo existe, se ha enviado un enlace de recuperación.")
        }
    }

    @PostMapping("/reset-password")
    fun resetPassword(@RequestParam token: String, @RequestParam newPassword: String): ResponseEntity<String> {
        return try {
            passwordResetService.resetPassword(token, newPassword)
            ResponseEntity.ok("Contraseña restablecida correctamente.")
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }
}