package com.dev.api.dto

import jakarta.validation.constraints.NotBlank

data class OtpRequest(
    @field:NotBlank(message = "Username is required")
    val username: String,

    @field:NotBlank(message = "OTP type is required")
    val type: String // "email" or "mobile"
)