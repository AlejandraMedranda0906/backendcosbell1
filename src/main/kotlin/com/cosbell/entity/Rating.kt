package com.cosbell.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "rating")
data class Rating(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val appointmentId: Long,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val employeeId: Long,

    @Column(nullable = false)
    val rating: Int, // Calificación con estrellas (ej. 1-5)

    @Column(nullable = true, length = 500)
    val comment: String? = null, // Comentario adicional opcional

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val approved: Boolean = false, // Para moderación, si se muestra públicamente

    @Column(nullable = false)
    val active: Boolean = true // Para marcar si la valoración está activa o fue eliminada lógicamente
) 