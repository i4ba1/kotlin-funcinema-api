package com.dev.api.repository

import com.dev.api.model.Movie
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface MovieRepository : JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {

    // Find by exact title
    fun findByTitle(title: String): List<Movie>

    // Find by title containing the search string (case insensitive)
    fun findByTitleContainingIgnoreCase(title: String, pageable: Pageable): Page<Movie>

    // Find by director
    fun findByDirectorContainingIgnoreCase(director: String, pageable: Pageable): Page<Movie>

    // Find by genre (using JPQL to search in a collection)
    @Query("SELECT m FROM Movie m JOIN m.genres g WHERE LOWER(g) LIKE LOWER(CONCAT('%', :genre, '%'))")
    fun findByGenreContainingIgnoreCase(@Param("genre") genre: String, pageable: Pageable): Page<Movie>

    // Find movies with rating greater than or equal to the provided value
    fun findByRatingGreaterThanEqual(minRating: Double, pageable: Pageable): Page<Movie>

    // Find movies released between two dates
    fun findByReleaseDateBetween(startDate: LocalDate, endDate: LocalDate, pageable: Pageable): Page<Movie>

    // Find featured movies
    fun findByFeaturedTrue(pageable: Pageable): Page<Movie>

    // Complex query combining multiple search criteria
    @Query("""
        SELECT m FROM Movie m WHERE 
        (:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND 
        (:director IS NULL OR LOWER(m.director) LIKE LOWER(CONCAT('%', :director, '%'))) AND 
        (:minRating IS NULL OR m.rating >= :minRating) AND 
        (:featured IS NULL OR m.featured = :featured)
    """)
    fun findByTitleDirectorRatingAndFeatured(
        @Param("title") title: String?,
        @Param("director") director: String?,
        @Param("minRating") minRating: Double?,
        @Param("featured") featured: Boolean?,
        pageable: Pageable
    ): Page<Movie>
}