package com.dev.api.service

import com.dev.api.dto.AuthResponse
import com.dev.api.dto.LoginRequest
import com.dev.api.dto.RegisterRequest
import com.dev.api.exception.ResourceAlreadyExistsException
import com.dev.api.exception.UnverifiedAccountException
import com.dev.api.model.Role
import com.dev.api.model.User
import com.dev.api.repository.RoleRepository
import com.dev.api.repository.UserRepository
import com.dev.api.security.JwtUtils
import com.dev.api.security.UserDetailsImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtUtils: JwtUtils,
    private val verificationService: VerificationService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    suspend fun registerUser(registerRequest: RegisterRequest): User = withContext(Dispatchers.IO) {
        // Check if username, email, or mobile number already exists
        if (userRepository.existsByUsername(registerRequest.username)) {
            throw ResourceAlreadyExistsException("Username is already taken")
        }

        if (userRepository.existsByEmail(registerRequest.email)) {
            throw ResourceAlreadyExistsException("Email is already in use")
        }

        if (userRepository.existsByMobileNumber(registerRequest.mobileNumber)) {
            throw ResourceAlreadyExistsException("Mobile number is already in use")
        }

        // Create new user
        val user = User(
            username = registerRequest.username,
            password = passwordEncoder.encode(registerRequest.password),
            email = registerRequest.email,
            mobileNumber = registerRequest.mobileNumber,
            fullName = registerRequest.fullName,
            emailVerified = false,
            mobileVerified = false,
            active = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Assign default role (ROLE_USER)
        val roles = mutableSetOf<Role>()
        val userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
            .orElseThrow { RuntimeException("Error: Role USER is not found.") }
        roles.add(userRole)
        user.roles.addAll(roles)

        // Save user
        val savedUser = userRepository.save(user)

        // Send verification emails and SMS
        // These operations are now coroutine-compatible
        verificationService.sendEmailVerification(savedUser)
        verificationService.sendMobileVerification(savedUser)

        savedUser
    }

    @Transactional
    suspend fun authenticateUser(loginRequest: LoginRequest): AuthResponse = withContext(Dispatchers.IO) {
        // Authenticate user
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginRequest.username,
                loginRequest.password
            )
        )

        SecurityContextHolder.getContext().authentication = authentication

        val userDetails = authentication.principal as UserDetailsImpl

        // Check if email and mobile are verified
        if (!userDetails.emailVerified && !userDetails.mobileVerified) {
            throw UnverifiedAccountException("Both email and mobile number need to be verified")
        } else if (!userDetails.emailVerified) {
            throw UnverifiedAccountException("Email needs to be verified")
        } else if (!userDetails.mobileVerified) {
            throw UnverifiedAccountException("Mobile number needs to be verified")
        }

        // Generate JWT token
        val jwt = jwtUtils.generateJwtToken(authentication)
        val refreshToken = jwtUtils.generateRefreshToken(userDetails.username)

        // Update last login
        val user = userRepository.findByUsername(userDetails.username)
            .orElseThrow { RuntimeException("User not found with username: ${userDetails.username}") }
        user.lastLoginAt = LocalDateTime.now()
        userRepository.save(user)

        AuthResponse(
            token = jwt,
            refreshToken = refreshToken,
            tokenType = "Bearer",
            expiresIn = jwtUtils.getExpirationTime(),
            username = userDetails.username,
            emailVerified = userDetails.emailVerified,
            mobileVerified = userDetails.mobileVerified
        )
    }

    @Transactional
    suspend fun refreshToken(refreshToken: String): String = withContext(Dispatchers.IO) {
        if (!jwtUtils.validateJwtToken(refreshToken)) {
            throw RuntimeException("Invalid refresh token")
        }

        val username = jwtUtils.getUserNameFromJwtToken(refreshToken)
        jwtUtils.generateJwtToken(SecurityContextHolder.getContext().authentication)
    }

    @Transactional
    suspend fun logout(refreshToken: String) = withContext(Dispatchers.IO) {
        // In a stateless JWT-based authentication, we don't need to do anything on the server side
        // The client should discard the tokens
        logger.info("User logged out successfully")
    }
}