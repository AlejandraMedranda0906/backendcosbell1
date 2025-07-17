package com.cosbell.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime

@Entity
@Table(name = "appointment")
data class Appointment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(optional = false)
    @JoinColumn(name = "service_id")
    val servicio: Servicio,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val fecha: LocalDate,

    @Column(nullable = false)
    val hora: LocalTime,

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false)
    val phone: String,

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    val employee: User,

    @Column(nullable = false)
    val status: String = "PENDING",

    @Column(nullable = false)
    val reminderSent: Boolean = false
)
