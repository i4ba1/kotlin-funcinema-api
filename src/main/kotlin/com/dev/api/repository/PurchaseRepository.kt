package com.dev.api.repository

import com.dev.api.model.Purchase
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PurchaseRepository : JpaRepository<Purchase, Long> {
    fun findByUserId(userId: Long): Set<Purchase>
    fun findByMovieId(movieId: Long): Set<Purchase>
    fun findByUserIdAndMovieId(userId: Long, movieId: Long): Set<Purchase>
}