package com.nisha.bookmyshow.repository;

import com.nisha.bookmyshow.entity.ShowSeat;
import com.nisha.bookmyshow.entity.ShowSeatStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {

    List<ShowSeat> findByShowId(Long showId);

    long countByShowIdAndStatus(Long showId, ShowSeatStatus status);

    boolean existsBySeatId(Long seatId);

    @Modifying
    void deleteByShowId(Long showId);

    @Query("SELECT ss FROM ShowSeat ss JOIN FETCH ss.seat WHERE ss.show.id = :showId ORDER BY ss.seat.rowNumber, ss.seat.seatNumber")
    List<ShowSeat> findByShowIdWithSeat(@Param("showId") Long showId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ss FROM ShowSeat ss JOIN FETCH ss.seat WHERE ss.id IN :ids AND ss.show.id = :showId")
    List<ShowSeat> lockSeatsForShow(@Param("ids") Collection<Long> ids, @Param("showId") Long showId);
}
