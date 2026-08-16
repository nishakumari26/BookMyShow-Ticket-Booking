package com.nisha.bookmyshow.repository;

import com.nisha.bookmyshow.entity.Movie;
import com.nisha.bookmyshow.entity.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("""
            SELECT m FROM Movie m
            WHERE (:q IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :q, '%')))
              AND (:language IS NULL OR LOWER(m.language) = LOWER(:language))
              AND (:genre IS NULL OR LOWER(m.genre) = LOWER(:genre))
              AND (:status IS NULL OR m.status = :status)
            """)
    Page<Movie> search(@Param("q") String q,
                       @Param("language") String language,
                       @Param("genre") String genre,
                       @Param("status") MovieStatus status,
                       Pageable pageable);
}
