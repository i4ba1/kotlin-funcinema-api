package com.dev.api.model


import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "verifications")
data class Verification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val code: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: VerificationType,

    @Column(nullable = false)
    val expiresAt: LocalDateTime,

    var used: Boolean = false,

    @Column(updatable = false)
    val createdAt: LocalDateTime? = null
) {
    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(this.expiresAt)
    }

    enum class VerificationType {
        EMAIL,
        MOBILE
    }
}