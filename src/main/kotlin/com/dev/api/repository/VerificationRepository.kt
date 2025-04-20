package com.dev.api.repository

import com.dev.api.model.User
import com.dev.api.model.Verification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface VerificationRepository : JpaRepository<Verification, Long> {
    fun findByUserAndTypeAndUsedFalse(
        user: User,
        type: Verification.VerificationType
    ): Optional<Verification>

    fun findByUserAndTypeAndCodeAndUsedFalse(
        user: User,
        type: Verification.VerificationType,
        code: String
    ): Optional<Verification>

    fun deleteByUserAndType(user: User, type: Verification.VerificationType)
}