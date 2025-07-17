package com.cosbell.entity

import jakarta.persistence.*
import com.cosbell.entity.User

@Entity
@Table(name = "schedule") // <-- tabla en inglés
data class Horario(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "day", nullable = false)        // <-- campo en inglés
    val dia: String,

    @Column(name = "start_time", nullable = false) // <-- campo en inglés
    val horaInicio: String,

    @Column(name = "end_time", nullable = false)   // <-- campo en inglés
    val horaFin: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true) // Cambiar a nullable = true
    val user: User? = null // Hacer opcional
)
