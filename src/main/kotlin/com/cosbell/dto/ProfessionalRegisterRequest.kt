package com.cosbell.dto

import com.cosbell.entity.Role

data class ProfessionalRegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String, // 👈 AGREGA AQUÍ
    val roleName: String, // e.g., "EMPLOYEE" or "ADMIN"
    val serviceIds: List<Long> = emptyList(),
    val schedules: List<HorarioRequest> = emptyList()
) 