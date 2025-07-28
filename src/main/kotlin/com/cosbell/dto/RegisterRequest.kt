package com.cosbell.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "El nombre es obligatorio")
    val name: String,

    @field:NotBlank(message = "El correo es obligatorio")
    @field:Email(message = "El correo no es válido")
    val email: String,

    @field:NotBlank(message = "La contraseña es obligatoria")
    val password: String,

    @field:NotBlank(message = "El rol es obligatorio")
    val role: String,  // "ADMIN", "CLIENT" o "EMPLOYEE"

    @field:NotBlank(message = "El número de teléfono es obligatorio")
    @field:Size(min = 10, max = 10, message = "El teléfono debe tener 10 dígitos")
    @field:Pattern(regexp = "^0\\d{9}\$", message = "Debe empezar con 0 y tener 10 dígitos")
    val phone: String   // <-- AGREGADO
)
