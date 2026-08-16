package com.nisha.bookmyshow.dto.booking;

import com.nisha.bookmyshow.entity.Booking;
import com.nisha.bookmyshow.entity.BookingStatus;
import com.nisha.bookmyshow.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record BookingResponse(
        Long bookingId,
        String bookingReference,
        Long userId,
        String userName,
        Long showId,
        String movieTitle,
        String theaterName,
        String screenName,
        LocalDate showDate,
        LocalTime startTime,
        List<String> selectedSeats,
        BigDecimal totalAmount,
        Instant bookingDate,
        BookingStatus bookingStatus,
        PaymentStatus paymentStatus
) {
    public static BookingResponse from(Booking b) {
        List<String> seats = b.getSeats().stream().map(bs -> bs.getSeatNumber()).toList();
        return new BookingResponse(
                b.getId(),
                b.getBookingReference(),
                b.getUser().getId(),
                b.getUser().getName(),
                b.getShow().getId(),
                b.getShow().getMovie().getTitle(),
                b.getShow().getTheater().getName(),
                b.getShow().getScreen().getName(),
                b.getShow().getShowDate(),
                b.getShow().getStartTime(),
                seats,
                b.getTotalAmount(),
                b.getBookingDate(),
                b.getBookingStatus(),
                b.getPaymentStatus()
        );
    }
}
