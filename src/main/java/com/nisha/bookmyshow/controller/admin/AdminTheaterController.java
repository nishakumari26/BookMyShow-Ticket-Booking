package com.nisha.bookmyshow.controller.admin;

import com.nisha.bookmyshow.dto.ApiResponse;
import com.nisha.bookmyshow.dto.theater.TheaterRequest;
import com.nisha.bookmyshow.dto.theater.TheaterResponse;
import com.nisha.bookmyshow.service.TheaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/theaters")
@RequiredArgsConstructor
@Tag(name = "Admin — Theaters")
@SecurityRequirement(name = "bearerAuth")
public class AdminTheaterController {

    private final TheaterService theaterService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a theater")
    public ApiResponse<TheaterResponse> create(@Valid @RequestBody TheaterRequest request) {
        return ApiResponse.ok("Theater created", theaterService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a theater")
    public ApiResponse<TheaterResponse> update(@PathVariable Long id, @Valid @RequestBody TheaterRequest request) {
        return ApiResponse.ok("Theater updated", theaterService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a theater")
    public void delete(@PathVariable Long id) {
        theaterService.delete(id);
    }
}
