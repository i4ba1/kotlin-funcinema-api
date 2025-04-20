package com.dev.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(unique = true, nullable = false)
    val username: String,

    @field:NotBlank(message = "Password is required")
    @Column(nullable = false)
    val password: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email should be valid")
    @Column(unique = true, nullable = false)
    val email: String,

    @field:NotBlank(message = "Mobile number is required")
    @field:Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Mobile number should be valid")
    @Column(unique = true, nullable = false)
    val mobileNumber: String,

    @field:NotBlank(message = "Full name is required")
    val fullName: String,

    var emailVerified: Boolean = false,

    var mobileVerified: Boolean = false,

    var active: Boolean = true,

    @Column(updatable = false)
    val createdAt: LocalDateTime? = null,

    var updatedAt: LocalDateTime? = null,

    var lastLoginAt: LocalDateTime? = null,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: MutableSet<Role> = mutableSetOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val purchases: MutableSet<Purchase> = mutableSetOf()
)