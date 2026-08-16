package com.nisha.bookmyshow.repository;

import com.nisha.bookmyshow.entity.Show;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShowRepository extends JpaRepository<Show, Long> {

    boolean existsByMovieId(Long movieId);

    boolean existsByTheaterId(Long theaterId);

    boolean existsByScreenId(Long screenId);

    List<Show> findByScreenIdAndShowDate(Long screenId, LocalDate date);

    @Query("SELECT s FROM Show s JOIN FETCH s.movie JOIN FETCH s.theater JOIN FETCH s.screen WHERE s.id = :id")
    Optional<Show> findDetailedById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"movie", "theater", "screen"})
    @Query("""
            SELECT s FROM Show s
            WHERE (:movieId IS NULL OR s.movie.id = :movieId)
              AND (:theaterId IS NULL OR s.theater.id = :theaterId)
              AND (:date IS NULL OR s.showDate = :date)
            """)
    Page<Show> search(@Param("movieId") Long movieId,
                      @Param("theaterId") Long theaterId,
                      @Param("date") LocalDate date,
                      Pageable pageable);
}
