package com.dev.api.dto

data class PaymentMethodDTO(
    val code: String? = null,
    val name: String? = null,
    val description: String? = null,
    val enabled: Boolean = false
)