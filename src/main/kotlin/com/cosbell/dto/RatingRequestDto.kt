package com.cosbell.dto

data class RatingRequestDto(
    val appointmentId: Long,
    val rating: Int,
    val comment: String? = null
) 