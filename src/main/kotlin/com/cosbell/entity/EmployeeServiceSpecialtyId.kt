package com.cosbell.entity

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class EmployeeServiceSpecialtyId(
    val employeeId: Long,
    val serviceId: Long
) : Serializable 