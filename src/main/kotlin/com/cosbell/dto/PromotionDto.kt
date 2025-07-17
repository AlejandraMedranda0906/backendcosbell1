package com.cosbell.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class PromotionDto(
    val id: Long? = null,
    val name: String,
    val description: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val conditions: String? = null,
    @JsonProperty("image_url")
    val imageUrl: String? = null
) 