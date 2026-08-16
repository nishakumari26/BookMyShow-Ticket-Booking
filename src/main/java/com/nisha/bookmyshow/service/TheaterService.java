package com.nisha.bookmyshow.service;

import com.nisha.bookmyshow.dto.theater.TheaterRequest;
import com.nisha.bookmyshow.dto.theater.TheaterResponse;
import com.nisha.bookmyshow.entity.Theater;
import com.nisha.bookmyshow.exception.ApiException;
import com.nisha.bookmyshow.exception.ResourceNotFoundException;
import com.nisha.bookmyshow.repository.ScreenRepository;
import com.nisha.bookmyshow.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TheaterService {

    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;

    public Page<TheaterResponse> list(String city, Pageable pageable) {
        if (city != null && !city.isBlank()) {
            return theaterRepository.findByCityIgnoreCase(city, pageable).map(TheaterResponse::from);
        }
        return theaterRepository.findAll(pageable).map(TheaterResponse::from);
    }

    public TheaterResponse get(Long id) {
        return TheaterResponse.from(find(id));
    }

    public TheaterResponse create(TheaterRequest request) {
        Theater theater = Theater.builder()
                .name(request.name())
                .location(request.location())
                .city(request.city())
                .address(request.address())
                .build();
        return TheaterResponse.from(theaterRepository.save(theater));
    }

    public TheaterResponse update(Long id, TheaterRequest request) {
        Theater theater = find(id);
        theater.setName(request.name());
        theater.setLocation(request.location());
        theater.setCity(request.city());
        theater.setAddress(request.address());
        return TheaterResponse.from(theaterRepository.save(theater));
    }

    public void delete(Long id) {
        if (screenRepository.existsByTheaterId(id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Cannot delete a theater that still has screens");
        }
        theaterRepository.delete(find(id));
    }

    public Theater find(Long id) {
        return theaterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found: " + id));
    }
}
