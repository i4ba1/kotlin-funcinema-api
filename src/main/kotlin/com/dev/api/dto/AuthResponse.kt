package com.dev.api.dto

data class AuthResponse(
    val token: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val username: String,
    val emailVerified: Boolean,
    val mobileVerified: Boolean
)