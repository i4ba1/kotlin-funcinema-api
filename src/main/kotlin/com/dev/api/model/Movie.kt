package com.dev.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Positive
import java.time.LocalDate

@Entity
@Table(name = "movies")
data class Movie(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @field:NotBlank(message = "Title is required")
    @Column(nullable = false)
    val title: String,

    @field:NotBlank(message = "Director is required")
    @Column(nullable = false)
    val director: String,

    @ElementCollection(fetch = FetchType.EAGER)
    val genres: MutableSet<String> = mutableSetOf(),

    @field:PastOrPresent(message = "Release date must be in the past or present")
    val releaseDate: LocalDate? = null,

    @field:Positive(message = "Duration must be positive")
    val durationMinutes: Int? = null,

    @field:NotNull(message = "Rating is required")
    @field:Positive(message = "Rating must be positive")
    val rating: Double,

    @Column(length = 1000)
    val plot: String? = null,

    val featured: Boolean = false
)