package com.dev.api.repository

import com.dev.api.dto.MovieSearchDTO
import com.dev.api.model.Movie
import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import org.springframework.util.StringUtils

class MovieSpecifications {
    companion object {
        fun buildSpecification(searchDTO: MovieSearchDTO): Specification<Movie> {
            return Specification { root, query, criteriaBuilder ->
                val predicates = ArrayList<Predicate>()

                // Title search (case insensitive)
                if (StringUtils.hasText(searchDTO.title)) {
                    predicates.add(
                        criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("title")),
                            "%" + searchDTO.title!!.lowercase() + "%"
                        )
                    )
                }

                // Director search (case insensitive)
                if (StringUtils.hasText(searchDTO.director)) {
                    predicates.add(
                        criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("director")),
                            "%" + searchDTO.director!!.lowercase() + "%"
                        )
                    )
                }

                // Genre search
                if (StringUtils.hasText(searchDTO.genre)) {
                    // Using JOIN to search in the collection of genres
                    val genresJoin: Join<Movie, String> = root.join("genres")
                    predicates.add(
                        criteriaBuilder.like(
                            criteriaBuilder.lower(genresJoin as javax.persistence.criteria.Expression<String>),
                            "%" + searchDTO.genre!!.lowercase() + "%"
                        )
                    )

                    // Make sure we don't get duplicate movies due to the join
                    query?.distinct(true)
                }

                // Release date range
                searchDTO.releaseYearStart?.let {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("releaseDate"), it))
                }

                searchDTO.releaseYearEnd?.let {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("releaseDate"), it))
                }

                // Rating filter
                searchDTO.minRating?.let {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), it))
                }

                // Featured filter
                searchDTO.featured?.let {
                    predicates.add(criteriaBuilder.equal(root.get<Boolean>("featured"), it))
                }

                criteriaBuilder.and(*predicates.toTypedArray())
            }
        }
    }
}