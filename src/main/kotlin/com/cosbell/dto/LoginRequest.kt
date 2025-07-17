package com.cosbell.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/*data class LoginRequest(
    @field:NotBlank(message = "El correo es obligatorio")
    @field:Email(message = "El correo no es válido")
    val email: String,

    @field:NotBlank(message = "La contraseña es obligatoria")
    val password: String
)*/

data class LoginRequest(
    @field:Email(message = "El correo no es válido")
    @field:NotBlank(message = "El correo es obligatorio")
    val email: String,

    @field:NotBlank(message = "La contraseña es obligatoria")
    val password: String
)

/*data class AuthResponse(
    val message: String? = null,
    val email: String? = null,
    val token: String? = null,
    val role: String? = null
)*/