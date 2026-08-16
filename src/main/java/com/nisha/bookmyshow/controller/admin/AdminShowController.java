package com.nisha.bookmyshow.controller.admin;

import com.nisha.bookmyshow.dto.ApiResponse;
import com.nisha.bookmyshow.dto.show.ShowRequest;
import com.nisha.bookmyshow.dto.show.ShowResponse;
import com.nisha.bookmyshow.service.ShowService;
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
@RequestMapping("/api/admin/shows")
@RequiredArgsConstructor
@Tag(name = "Admin — Shows")
@SecurityRequirement(name = "bearerAuth")
public class AdminShowController {

    private final ShowService showService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a show and generate show seats from the screen layout")
    public ApiResponse<ShowResponse> create(@Valid @RequestBody ShowRequest request) {
        return ApiResponse.ok("Show created", showService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update show movie/date/time")
    public ApiResponse<ShowResponse> update(@PathVariable Long id, @Valid @RequestBody ShowRequest request) {
        return ApiResponse.ok("Show updated", showService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a show that has no bookings")
    public void delete(@PathVariable Long id) {
        showService.delete(id);
    }
}
