package com.dev.api.service

import com.dev.api.dto.MovieDTO
import com.dev.api.dto.MovieSearchDTO
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Page

interface MovieService {

    // Suspend functions for single-item operations
    suspend fun getMovieById(id: Long): MovieDTO
    suspend fun createMovie(movieDTO: MovieDTO): MovieDTO
    suspend fun updateMovie(id: Long, movieDTO: MovieDTO): MovieDTO
    suspend fun deleteMovie(id: Long)

    // List-returning operations
    suspend fun getAllMovies(): List<MovieDTO>

    // Flow-based APIs for streaming results
    fun getAllMoviesFlow(): Flow<MovieDTO>
    fun searchMoviesFlow(searchDTO: MovieSearchDTO): Flow<MovieDTO>

    // Search operation returning Page (for pagination)
    suspend fun searchMovies(searchDTO: MovieSearchDTO): Page<MovieDTO>
}