package com.cosbell.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

@Entity
@Table(name = "service")
@JsonIgnoreProperties(value = ["hibernateLazyInitializer", "handler"])
data class Servicio(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val duration: Int,   //en minutos

    @Column(nullable = false)
    val price: Double,

    @Column(nullable = true)
    val description: String? = null,

    @Column(nullable = true, name = "descripcion_extend")
    val descripcionExtend: String? = null,

    @Column(nullable = true, name = "image_url")
    val imageUrl: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    val category: Category
)