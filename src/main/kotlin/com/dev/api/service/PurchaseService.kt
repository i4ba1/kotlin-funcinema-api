package com.dev.api.service

import com.dev.api.dto.PaymentMethodDTO
import com.dev.api.dto.PurchaseRequest
import com.dev.api.dto.PurchaseResponse
import com.dev.api.exception.PaymentProcessingException
import com.dev.api.exception.ResourceNotFoundException
import com.dev.api.model.Purchase
import com.dev.api.model.Purchase.PurchaseStatus
import com.dev.api.repository.MovieRepository
import com.dev.api.repository.PurchaseRepository
import com.dev.api.repository.UserRepository
import com.dev.api.security.UserDetailsImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Service
class PurchaseService(
    private val purchaseRepository: PurchaseRepository,
    private val userRepository: UserRepository,
    private val movieRepository: MovieRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    suspend fun purchaseMovie(request: PurchaseRequest): PurchaseResponse = withContext(Dispatchers.IO) {
        // Get current user
        val userDetails = SecurityContextHolder.getContext()
            .authentication.principal as UserDetailsImpl

        val user = userRepository.findById(userDetails.id!!)
            .orElseThrow { ResourceNotFoundException("User not found") }

        // Verify user has verified email and mobile
        if (!user.emailVerified || !user.mobileVerified) {
            throw PaymentProcessingException("Both email and mobile number must be verified to make purchases")
        }

        // Get movie
        val movie = movieRepository.findById(request.movieId)
            .orElseThrow { ResourceNotFoundException("Movie not found with ID: ${request.movieId}") }

        // Check if already purchased
        val existingPurchases = purchaseRepository.findByUserIdAndMovieId(user.id!!, movie.id!!)
        if (existingPurchases.isNotEmpty()) {
            // Could return existing purchase instead of error
            for (existingPurchase in existingPurchases) {
                if (existingPurchase.status == PurchaseStatus.COMPLETED) {
                    throw PaymentProcessingException("You have already purchased this movie")
                }
            }
        }

        // Process payment (mock implementation)
        val transactionId = processPayment(request)

        // Create purchase record
        val purchase = Purchase(
            user = user,
            movie = movie,
            amount = BigDecimal("9.99"), // In a real implementation, this would come from the movie pricing
            paymentMethod = request.paymentMethod,
            transactionId = transactionId,
            status = PurchaseStatus.COMPLETED,
            purchaseDate = LocalDateTime.now(),
            completedDate = LocalDateTime.now()
        )

        val savedPurchase = purchaseRepository.save(purchase)

        convertToResponse(savedPurchase)
    }

    @Transactional(readOnly = true)
    suspend fun getUserPurchases(): List<PurchaseResponse> = withContext(Dispatchers.IO) {
        // Get current user
        val userDetails = SecurityContextHolder.getContext()
            .authentication.principal as UserDetailsImpl

        val purchases = purchaseRepository.findByUserId(userDetails.id!!)

        purchases.map { convertToResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getUserPurchasesFlow(): Flow<PurchaseResponse> = flow {
        // Get current user
        val userDetails = SecurityContextHolder.getContext()
            .authentication.principal as UserDetailsImpl

        val purchases = purchaseRepository.findByUserId(userDetails.id!!)

        for (purchase in purchases) {
            emit(convertToResponse(purchase))
        }
    }.flowOn(Dispatchers.IO)

    @Transactional(readOnly = true)
    suspend fun getAvailablePaymentMethods(): List<PaymentMethodDTO> = withContext(Dispatchers.IO) {
        // In a real implementation, this might come from a database or payment gateway API
        listOf(
            PaymentMethodDTO(
                code = "CREDIT_CARD",
                name = "Credit Card",
                description = "Pay with Visa, Mastercard, or American Express",
                enabled = true
            ),
            PaymentMethodDTO(
                code = "DEBIT_CARD",
                name = "Debit Card",
                description = "Pay with your bank debit card",
                enabled = true
            ),
            PaymentMethodDTO(
                code = "PAYPAL",
                name = "PayPal",
                description = "Pay with your PayPal account",
                enabled = true
            )
        )
    }

    // Mock payment processing
    private fun processPayment(request: PurchaseRequest): String {
        logger.info("Processing payment with method: {}", request.paymentMethod)

        // In a real implementation, this would integrate with a payment gateway
        // For this example, we'll simulate a payment processor

        // Generate a transaction ID
        val transactionId = UUID.randomUUID().toString()
        logger.info("Payment processed successfully. Transaction ID: {}", transactionId)

        return transactionId
    }

    private fun convertToResponse(purchase: Purchase): PurchaseResponse {
        return PurchaseResponse(
            id = purchase.id,
            userId = purchase.user.id,
            movieId = purchase.movie.id,
            movieTitle = purchase.movie.title,
            amount = purchase.amount,
            paymentMethod = purchase.paymentMethod,
            transactionId = purchase.transactionId,
            status = purchase.status,
            purchaseDate = purchase.purchaseDate,
            completedDate = purchase.completedDate
        )
    }
}