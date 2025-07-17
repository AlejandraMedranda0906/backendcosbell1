package com.cosbell.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class RegisterRequest(
    @field:NotBlank(message = "El nombre es obligatorio")
    val name: String,

    @field:NotBlank(message = "El correo es obligatorio")
    @field:Email(message = "El cor reo no es válido")
    val email: String,

    @field:NotBlank(message = "La contraseña es obligatoria")
    val password: String,

    @field:NotBlank(message = "El rol es obligatorio")
    val role: String  //"ADMIN", "CLIENT" o "EMPLOYEE"
)