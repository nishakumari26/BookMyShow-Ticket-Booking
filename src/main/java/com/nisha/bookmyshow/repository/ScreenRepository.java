package com.nisha.bookmyshow.repository;

import com.nisha.bookmyshow.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScreenRepository extends JpaRepository<Screen, Long> {

    @Query("SELECT s FROM Screen s JOIN FETCH s.theater WHERE s.theater.id = :theaterId")
    List<Screen> findByTheaterId(@Param("theaterId") Long theaterId);

    boolean existsByTheaterId(Long theaterId);
}
