package com.dev.api.service

import com.dev.api.dto.MovieDTO
import com.dev.api.dto.MovieSearchDTO
import com.dev.api.model.Movie
import com.dev.api.repository.MovieRepository
import com.dev.api.repository.MovieSpecifications
import jakarta.persistence.EntityNotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MovieServiceImpl(private val movieRepository: MovieRepository) : MovieService {

    // Utility methods for conversion between entity and DTO
    private fun convertToDTO(movie: Movie): MovieDTO {
        return MovieDTO(
            title = movie.title,
            director = movie.director,
            genres = movie.genres,
            releaseDate = movie.releaseDate,
            durationMinutes = movie.durationMinutes,
            rating = movie.rating,
            plot = movie.plot,
            featured = movie.featured
        )
    }

    private fun convertToEntity(movieDTO: MovieDTO): Movie {
        return Movie(
            title = movieDTO.title,
            director = movieDTO.director,
            genres = movieDTO.genres.toMutableSet(),
            releaseDate = movieDTO.releaseDate,
            durationMinutes = movieDTO.durationMinutes,
            rating = movieDTO.rating,
            plot = movieDTO.plot,
            featured = movieDTO.featured
        )
    }

    @Transactional(readOnly = true)
    override suspend fun getMovieById(id: Long): MovieDTO = withContext(Dispatchers.IO) {
        val movie = movieRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Movie not found with ID: $id") }
        convertToDTO(movie)
    }

    @Transactional(readOnly = true)
    override suspend fun getAllMovies(): List<MovieDTO> = withContext(Dispatchers.IO) {
        movieRepository.findAll().map { convertToDTO(it) }
    }

    @Transactional
    override suspend fun deleteMovie(id: Long) = withContext(Dispatchers.IO) {
        // Check if movie exists
        if (!movieRepository.existsById(id)) {
            throw EntityNotFoundException("Movie not found with ID: $id")
        }

        movieRepository.deleteById(id)
    }

    @Transactional
    override suspend fun createMovie(movieDTO: MovieDTO): MovieDTO = withContext(Dispatchers.IO) {
        val movie = convertToEntity(movieDTO)
        val savedMovie = movieRepository.save(movie)
        convertToDTO(savedMovie)
    }

    @Transactional
    override suspend fun updateMovie(id: Long, movieDTO: MovieDTO): MovieDTO = withContext(Dispatchers.IO) {
        if (!movieRepository.existsById(id)) {
            throw EntityNotFoundException("Movie not found with ID: $id")
        }

        val movie = convertToEntity(movieDTO).copy(id = id) // Ensure ID is set correctly
        val updatedMovie = movieRepository.save(movie)
        convertToDTO(updatedMovie)
    }

    @Transactional(readOnly = true)
    override fun getAllMoviesFlow(): Flow<MovieDTO> = flow {
        val movies = movieRepository.findAll()
        for (movie in movies) {
            emit(convertToDTO(movie))
        }
    }.flowOn(Dispatchers.IO)

    @Transactional(readOnly = true)
    override suspend fun searchMovies(searchDTO: MovieSearchDTO): Page<MovieDTO> = withContext(Dispatchers.IO) {
        // Create pageable with sorting
        val pageable = PageRequest.of(
            searchDTO.page,
            searchDTO.size,
            if (searchDTO.ascending) Sort.Direction.ASC else Sort.Direction.DESC,
            searchDTO.sortBy
        )

        // Use specifications for dynamic querying
        val moviesPage = movieRepository.findAll(
            MovieSpecifications.buildSpecification(searchDTO),
            pageable
        )

        // Convert to DTOs
        moviesPage.map { convertToDTO(it) }
    }

    @Transactional(readOnly = true)
    override fun searchMoviesFlow(searchDTO: MovieSearchDTO): Flow<MovieDTO> = flow {
        // Create pageable with sorting
        val pageable = PageRequest.of(
            searchDTO.page,
            searchDTO.size,
            if (searchDTO.ascending) Sort.Direction.ASC else Sort.Direction.DESC,
            searchDTO.sortBy
        )

        // Use specifications for dynamic querying
        val movies = movieRepository.findAll(
            MovieSpecifications.buildSpecification(searchDTO),
            pageable
        )

        // Emit each movie as it's processed
        for (movie in movies) {
            emit(convertToDTO(movie))
        }
    }.flowOn(Dispatchers.IO)
}