package com.cosbell.dto

import com.cosbell.dto.HorarioRequest

data class UserDTO(
    val id: Long? = null,
    val name: String,
    val email: String,
    val roleId: Long
)

data class EmployeeWithServicesDTO(
    val id: Long? = null,
    val name: String,
    val email: String,
    val roleId: Long,
    val services: List<ServicioDTO>,
    val horarios: List<HorarioRequest>
)