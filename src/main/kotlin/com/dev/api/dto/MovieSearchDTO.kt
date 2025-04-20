package com.dev.api.dto

import java.time.LocalDate

data class MovieSearchDTO(
    val title: String? = null,
    val director: String? = null,
    val genre: String? = null,
    val releaseYearStart: LocalDate? = null,
    val releaseYearEnd: LocalDate? = null,
    val minRating: Double? = null,
    val featured: Boolean? = null,
    val page: Int = 0,
    val size: Int = 10,
    val sortBy: String = "title",
    val ascending: Boolean = true
)