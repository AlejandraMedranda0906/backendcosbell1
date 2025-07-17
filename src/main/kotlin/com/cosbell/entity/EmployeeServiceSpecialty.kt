package com.cosbell.entity

import jakarta.persistence.*

@Entity
@Table(name = "employee_service_specialty")
data class EmployeeServiceSpecialty(
    @EmbeddedId
    val id: EmployeeServiceSpecialtyId,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employeeId")
    @JoinColumn(name = "employee_id")
    val employee: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("serviceId")
    @JoinColumn(name = "service_id")
    val service: Servicio
) 