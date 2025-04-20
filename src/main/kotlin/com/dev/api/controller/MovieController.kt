package com.dev.api.controller

import com.dev.api.dto.MovieDTO
import com.dev.api.dto.MovieSearchDTO
import com.dev.api.service.MovieService
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/movies")
class MovieController(private val movieService: MovieService) {

    @PostMapping
    suspend fun createMovie(
        @Valid @RequestBody movieDTO: MovieDTO
    ): ResponseEntity<MovieDTO> {
        val createdMovie = movieService.createMovie(movieDTO)
        return ResponseEntity(createdMovie, HttpStatus.CREATED)
    }

    @GetMapping("/getMovieById/{id}")
    suspend fun getMovieById(
        @PathVariable id: Long
    ): ResponseEntity<MovieDTO> {
        val movie = movieService.getMovieById(id)
        return ResponseEntity.ok(movie)
    }

    @GetMapping
    fun getAllMovies(): Flow<MovieDTO> {
        return movieService.getAllMoviesFlow()
    }

    @PutMapping("/{id}")
    suspend fun updateMovie(
        @PathVariable id: Long,
        @Valid @RequestBody movieDTO: MovieDTO
    ): ResponseEntity<MovieDTO> {
        val updatedMovie = movieService.updateMovie(id, movieDTO)
        return ResponseEntity.ok(updatedMovie)
    }

    @DeleteMapping("/{id}")
    suspend fun deleteMovie(
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        movieService.deleteMovie(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/search")
    suspend fun searchMovies(
        @RequestBody searchDTO: MovieSearchDTO
    ): ResponseEntity<Page<MovieDTO>> {
        val results = movieService.searchMovies(searchDTO)
        return ResponseEntity.ok(results)
    }

    // Alternative endpoint returning a Flow for streaming results
    @PostMapping("/search/flow")
    fun searchMoviesFlow(
        @RequestBody searchDTO: MovieSearchDTO
    ): Flow<MovieDTO> {
        return movieService.searchMoviesFlow(searchDTO)
    }
}