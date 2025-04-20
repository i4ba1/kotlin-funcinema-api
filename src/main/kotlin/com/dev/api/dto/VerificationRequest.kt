package com.dev.api.dto


import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class VerificationRequest(
    @field:NotBlank(message = "Username is required")
    val username: String,

    @field:NotBlank(message = "Code is required")
    val code: String
)