package com.cosbell.dto

import java.time.LocalDate
import java.time.LocalTime
import com.fasterxml.jackson.annotation.JsonProperty

data class AppointmentDto(
    @JsonProperty("id")
    val id: Long? = null,
    @JsonProperty("servicioId")
    val serviceId: Long,
    val userId: Long,
    val fecha: LocalDate,
    val hora: LocalTime,
    val email: String,
    val phone: String,
    val employeeId: Long,
    val employeeName: String? = null,
    val userName: String? = null,
    val serviceName: String? = null,
    val status: String? = "PENDING",
    val hasBeenRated: Boolean? = null,
    @JsonProperty("serviceDuration")
    val serviceDuration: Int? = null
)
