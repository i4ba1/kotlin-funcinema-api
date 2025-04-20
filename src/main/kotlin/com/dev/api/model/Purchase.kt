package com.dev.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "purchases")
data class Purchase(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    val movie: Movie,

    @field:NotNull
    @field:Positive
    val amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val paymentMethod: PaymentMethod,

    val transactionId: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: PurchaseStatus,

    @Column(updatable = false)
    val purchaseDate: LocalDateTime? = null,

    val completedDate: LocalDateTime? = null
) {
    enum class PaymentMethod {
        CREDIT_CARD,
        DEBIT_CARD,
        PAYPAL,
        APPLE_PAY,
        GOOGLE_PAY
    }

    enum class PurchaseStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED
    }
}