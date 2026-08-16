package com.nisha.bookmyshow.service;

import com.nisha.bookmyshow.dto.booking.BookingResponse;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from:noreply@bookmyshow.local}")
    private String from;

    public void sendBookingConfirmation(String to, BookingResponse booking) {
        send(to, "Booking confirmed — " + booking.bookingReference(), confirmationBody(booking));
    }

    public void sendBookingCancellation(String to, BookingResponse booking) {
        send(to, "Booking cancelled — " + booking.bookingReference(), cancellationBody(booking));
    }

    private void send(String to, String subject, String body) {
        if (!mailEnabled) {
            log.info("SMTP disabled; skipping email '{}' to {}", subject, to);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);
            mailSender.send(message);
            log.info("Sent email '{}' to {}", subject, to);
        } catch (Exception ex) {
            log.warn("Failed to send email '{}' to {}: {}", subject, to, ex.getMessage());
        }
    }

    private String confirmationBody(BookingResponse b) {
        return """
                Hello %s,

                Your booking is confirmed.

                Booking ID: %s
                Booking reference: %s
                Movie: %s
                Theater: %s
                Screen: %s
                Show date: %s
                Show time: %s
                Seats: %s
                Total amount: %s
                Status: %s
                Payment: %s

                Thank you for booking with us.
                """.formatted(
                b.userName(),
                b.bookingId(),
                b.bookingReference(),
                b.movieTitle(),
                b.theaterName(),
                b.screenName(),
                b.showDate(),
                b.startTime(),
                String.join(", ", b.selectedSeats()),
                b.totalAmount(),
                b.bookingStatus(),
                b.paymentStatus()
        );
    }

    private String cancellationBody(BookingResponse b) {
        return """
                Hello %s,

                Your booking has been cancelled.

                Booking ID: %s
                Booking reference: %s
                Movie: %s
                Theater: %s
                Screen: %s
                Show date: %s
                Show time: %s
                Seats: %s
                Amount refunded (simulated): %s
                Status: %s

                We hope to see you at another show.
                """.formatted(
                b.userName(),
                b.bookingId(),
                b.bookingReference(),
                b.movieTitle(),
                b.theaterName(),
                b.screenName(),
                b.showDate(),
                b.startTime(),
                String.join(", ", b.selectedSeats()),
                b.totalAmount(),
                b.bookingStatus()
        );
    }
}
