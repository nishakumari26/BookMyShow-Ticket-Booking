package com.nisha.bookmyshow.repository;

import com.nisha.bookmyshow.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByScreenIdOrderByRowNumberAscSeatNumberAsc(Long screenId);
}
