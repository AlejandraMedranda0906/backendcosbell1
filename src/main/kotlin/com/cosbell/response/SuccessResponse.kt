package com.cosbell.response

data class SuccessResponse(
    val message: String,
    val token: String? = null,
    val role: String? = null
)