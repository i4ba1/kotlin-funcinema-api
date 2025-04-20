package com.dev.api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Positive
import java.time.LocalDate

data class MovieDTO(
    @field:NotBlank(message = "Title is required")
    val title: String,

    @field:NotBlank(message = "Director is required")
    val director: String,

    val genres: Set<String> = emptySet(),

    @field:PastOrPresent(message = "Release date must be in the past or present")
    val releaseDate: LocalDate? = null,

    @field:Positive(message = "Duration must be positive")
    val durationMinutes: Int? = null,

    @field:NotNull(message = "Rating is required")
    @field:Positive(message = "Rating must be positive")
    val rating: Double,

    val plot: String? = null,

    val featured: Boolean = false
)