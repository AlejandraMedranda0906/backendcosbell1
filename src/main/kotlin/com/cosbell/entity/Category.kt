package com.cosbell.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

@Entity
@Table(name = "category")
@JsonIgnoreProperties(value = ["hibernateLazyInitializer", "handler"])
data class Category(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = true, name = "image_url")
    val imageUrl: String? = null,

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    val servicios: List<Servicio> = emptyList()
) 