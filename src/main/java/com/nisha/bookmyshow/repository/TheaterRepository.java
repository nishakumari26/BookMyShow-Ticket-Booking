package com.nisha.bookmyshow.repository;

import com.nisha.bookmyshow.entity.Theater;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterRepository extends JpaRepository<Theater, Long> {
    Page<Theater> findByCityIgnoreCase(String city, Pageable pageable);
}
