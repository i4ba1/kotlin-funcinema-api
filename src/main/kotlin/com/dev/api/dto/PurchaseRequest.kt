package com.dev.api.dto

import com.dev.api.model.Purchase
import jakarta.validation.constraints.NotNull

data class PurchaseRequest(
    @field:NotNull(message = "Movie ID is required")
    val movieId: Long,

    @field:NotNull(message = "Payment method is required")
    val paymentMethod: Purchase.PaymentMethod,

    // Payment details - in a real system, these would be handled securely
    val cardNumber: String? = null,
    val cardExpiry: String? = null,
    val cardCvv: String? = null,
    val cardHolderName: String? = null
)
