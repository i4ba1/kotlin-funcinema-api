package com.dev.api.service

import com.dev.api.exception.InvalidVerificationException
import com.dev.api.exception.ResourceNotFoundException
import com.dev.api.model.User
import com.dev.api.model.Verification
import com.dev.api.model.Verification.VerificationType
import com.dev.api.repository.UserRepository
import com.dev.api.repository.VerificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.LocalDateTime

@Service
class VerificationService(
    private val verificationRepository: VerificationRepository,
    private val userRepository: UserRepository,
    private val mailSender: JavaMailSender,

    @Value("\${otp.expiration}")
    private val otpExpirationMs: Long,

    @Value("\${otp.length}")
    private val otpLength: Int
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val random = SecureRandom()

    @Transactional
    suspend fun sendEmailVerification(user: User) = withContext(Dispatchers.IO) {
        // Delete any existing unverified codes
        verificationRepository.deleteByUserAndType(user, VerificationType.EMAIL)

        // Generate OTP code
        val otpCode = generateOTP()

        // Create verification record
        val verification = Verification(
            user = user,
            code = otpCode,
            type = VerificationType.EMAIL,
            expiresAt = LocalDateTime.now().plusSeconds(otpExpirationMs / 1000),
            used = false,
            createdAt = LocalDateTime.now()
        )

        verificationRepository.save(verification)

        // Send email
        val message = SimpleMailMessage()
        message.setTo(user.email)
        message.subject = "Email Verification Code"
        message.text = "Your verification code is: $otpCode\nThis code will expire in ${otpExpirationMs / 60000} minutes."

        try {
            mailSender.send(message)
            logger.info("Verification email sent to: {}", user.email)
        } catch (e: Exception) {
            logger.error("Failed to send verification email: {}", e.message)
        }
    }

    @Transactional
    suspend fun sendMobileVerification(user: User) = withContext(Dispatchers.IO) {
        // Delete any existing unverified codes
        verificationRepository.deleteByUserAndType(user, VerificationType.MOBILE)

        // Generate OTP code
        val otpCode = generateOTP()

        // Create verification record
        val verification = Verification(
            user = user,
            code = otpCode,
            type = VerificationType.MOBILE,
            expiresAt = LocalDateTime.now().plusSeconds(otpExpirationMs / 1000),
            used = false,
            createdAt = LocalDateTime.now()
        )

        verificationRepository.save(verification)

        // In a real implementation, this would integrate with an SMS service
        // For this example, we'll log the code
        logger.info("SMS Verification code for {}: {}", user.mobileNumber, otpCode)
        logger.info("In a real implementation, this would send an SMS to the user's mobile number")
    }

    @Transactional
    suspend fun verifyEmail(username: String, code: String): Boolean = withContext(Dispatchers.IO) {
        val user = userRepository.findByUsername(username)
            .orElseThrow { ResourceNotFoundException("User not found with username: $username") }

        val verification = verificationRepository
            .findByUserAndTypeAndCodeAndUsedFalse(user, VerificationType.EMAIL, code)
            .orElseThrow { InvalidVerificationException("Invalid or expired verification code") }

        if (verification.isExpired()) {
            verificationRepository.delete(verification)
            throw InvalidVerificationException("Verification code has expired")
        }

        // Mark as used
        verification.used = true
        verificationRepository.save(verification)

        // Update user
        user.emailVerified = true
        userRepository.save(user)

        true
    }

    @Transactional
    suspend fun verifyMobile(username: String, code: String): Boolean = withContext(Dispatchers.IO) {
        val user = userRepository.findByUsername(username)
            .orElseThrow { ResourceNotFoundException("User not found with username: $username") }

        val verification = verificationRepository
            .findByUserAndTypeAndCodeAndUsedFalse(user, VerificationType.MOBILE, code)
            .orElseThrow { InvalidVerificationException("Invalid or expired verification code") }

        if (verification.isExpired()) {
            verificationRepository.delete(verification)
            throw InvalidVerificationException("Verification code has expired")
        }

        // Mark as used
        verification.used = true
        verificationRepository.save(verification)

        // Update user
        user.mobileVerified = true
        userRepository.save(user)

        true
    }

    @Transactional
    suspend fun resendVerification(username: String, type: VerificationType) = withContext(Dispatchers.IO) {
        val user = userRepository.findByUsername(username)
            .orElseThrow { ResourceNotFoundException("User not found with username: $username") }

        if (type == VerificationType.EMAIL) {
            sendEmailVerification(user)
        } else if (type == VerificationType.MOBILE) {
            sendMobileVerification(user)
        }
    }

    // OTP generation
    private fun generateOTP(): String {
        val otp = StringBuilder()
        for (i in 0 until otpLength) {
            otp.append(random.nextInt(10))
        }
        return otp.toString()
    }
}