package com.dev.api.model

import jakarta.persistence.*

@Entity
@Table(name = "roles")
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true)
    val name: ERole
) {
    enum class ERole {
        ROLE_USER,
        ROLE_MODERATOR,
        ROLE_ADMIN
    }
}