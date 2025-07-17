package com.cosbell.entity

import jakarta.persistence.*

@Entity
@Table(name = "permission")
data class Permission(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val resourcePath: String,

    @Column(nullable = false)
    val httpMethod: String
)

