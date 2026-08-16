package com.nisha.bookmyshow.repository;

import com.nisha.bookmyshow.entity.Booking;
import com.nisha.bookmyshow.entity.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByUserId(Long userId, Pageable pageable);

    Optional<Booking> findByIdAndUserId(Long id, Long userId);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.user.id = :userId
              AND b.bookingStatus = :status
              AND b.show.showDate >= :today
            """)
    Page<Booking> findUpcoming(@Param("userId") Long userId,
                               @Param("status") BookingStatus status,
                               @Param("today") LocalDate today,
                               Pageable pageable);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.user.id = :userId
              AND (b.bookingStatus = com.nisha.bookmyshow.entity.BookingStatus.CANCELLED
                   OR b.show.showDate < :today)
            """)
    Page<Booking> findPast(@Param("userId") Long userId,
                           @Param("today") LocalDate today,
                           Pageable pageable);

    long countByBookingStatus(BookingStatus status);

    boolean existsByShowId(Long showId);

    @Query("""
            SELECT DISTINCT b FROM Booking b
            JOIN FETCH b.user
            JOIN FETCH b.show s
            JOIN FETCH s.movie
            JOIN FETCH s.theater
            JOIN FETCH s.screen
            LEFT JOIN FETCH b.seats
            WHERE b.id = :id
            """)
    Optional<Booking> findDetailedById(@Param("id") Long id);

    @Query("""
            SELECT DISTINCT b FROM Booking b
            JOIN FETCH b.user
            JOIN FETCH b.show s
            JOIN FETCH s.movie
            JOIN FETCH s.theater
            JOIN FETCH s.screen
            LEFT JOIN FETCH b.seats
            WHERE b.id IN :ids
            """)
    List<Booking> findDetailedByIdIn(@Param("ids") Collection<Long> ids);
}
