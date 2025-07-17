package com.cosbell.dto

data class AuthResponse(
    val message: String? = null,
    val email: String? = null,
    val name: String? = null,
    val token: String? = null,
    val role: String? = null,
    val userId: Long? = null
)