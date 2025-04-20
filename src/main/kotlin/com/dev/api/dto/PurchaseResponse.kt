package com.dev.api.dto

import com.dev.api.model.Purchase
import java.math.BigDecimal
import java.time.LocalDateTime

data class PurchaseResponse(
    val id: Long? = null,
    val userId: Long? = null,
    val movieId: Long? = null,
    val movieTitle: String? = null,
    val amount: BigDecimal? = null,
    val paymentMethod: Purchase.PaymentMethod? = null,
    val transactionId: String? = null,
    val status: Purchase.PurchaseStatus? = null,
    val purchaseDate: LocalDateTime? = null,
    val completedDate: LocalDateTime? = null
)