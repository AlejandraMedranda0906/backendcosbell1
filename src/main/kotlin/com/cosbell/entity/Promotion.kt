package com.cosbell.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "promotion")
data class Promotion(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val description: String,

    @Column(name = "start_date", nullable = false)
    val startDate: LocalDate,

    @Column(name = "end_date", nullable = false)
    val endDate: LocalDate,

    @Column(nullable = true)
    val conditions: String? = null,

    @Column(name = "image_url", nullable = true)
    val imageUrl: String? = null
) 