package com.cosbell.dto

data class ServicioDTO(
    val id: Long? = null,
    val name: String,
    val duration: Int,
    val price: Double,
    val description: String? = null,
    val descripcionExtend: String? = null,
    val categoryId: Long? = null,
    val imageUrl: String? = null
)