package com.nisha.bookmyshow.config;

import com.nisha.bookmyshow.entity.Movie;
import com.nisha.bookmyshow.entity.MovieStatus;
import com.nisha.bookmyshow.entity.Role;
import com.nisha.bookmyshow.entity.Screen;
import com.nisha.bookmyshow.entity.Seat;
import com.nisha.bookmyshow.entity.SeatType;
import com.nisha.bookmyshow.entity.Theater;
import com.nisha.bookmyshow.entity.User;
import com.nisha.bookmyshow.repository.MovieRepository;
import com.nisha.bookmyshow.repository.ScreenRepository;
import com.nisha.bookmyshow.repository.SeatRepository;
import com.nisha.bookmyshow.repository.TheaterRepository;
import com.nisha.bookmyshow.repository.UserRepository;
import com.nisha.bookmyshow.service.ShowService;
import com.nisha.bookmyshow.dto.show.ShowRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true")
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final ShowService showService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.existsByEmail("admin@bookmyshow.local")) {
            log.info("Sample data already present; skipping seed");
            return;
        }

        User admin = userRepository.save(User.builder()
                .name("Admin")
                .email("admin@bookmyshow.local")
                .password(passwordEncoder.encode("Admin@123"))
                .phone("9999999999")
                .role(Role.ADMIN)
                .build());

        userRepository.save(User.builder()
                .name("Demo User")
                .email("user@bookmyshow.local")
                .password(passwordEncoder.encode("User@123"))
                .phone("8888888888")
                .role(Role.USER)
                .build());

        Movie avengers = movieRepository.save(Movie.builder()
                .title("Avengers")
                .description("Earth's mightiest heroes assemble.")
                .language("English")
                .genre("Action")
                .duration(180)
                .releaseDate(LocalDate.of(2012, 4, 27))
                .posterUrl("https://example.com/avengers.jpg")
                .rating(8.4)
                .status(MovieStatus.NOW_SHOWING)
                .build());

        movieRepository.save(Movie.builder()
                .title("Inception")
                .description("A thief who steals corporate secrets through dream-sharing.")
                .language("English")
                .genre("Sci-Fi")
                .duration(148)
                .releaseDate(LocalDate.of(2010, 7, 16))
                .posterUrl("https://example.com/inception.jpg")
                .rating(8.8)
                .status(MovieStatus.NOW_SHOWING)
                .build());

        movieRepository.save(Movie.builder()
                .title("Jawan")
                .description("A man is driven by a personal vendetta to rectify the wrongs in society.")
                .language("Hindi")
                .genre("Action")
                .duration(169)
                .releaseDate(LocalDate.of(2023, 9, 7))
                .posterUrl("https://example.com/jawan.jpg")
                .rating(7.1)
                .status(MovieStatus.NOW_SHOWING)
                .build());

        Theater pvr = theaterRepository.save(Theater.builder()
                .name("PVR")
                .location("Phoenix Mall")
                .city("Bengaluru")
                .address("Whitefield, Bengaluru")
                .build());

        theaterRepository.save(Theater.builder()
                .name("INOX")
                .location("Forum Mall")
                .city("Bengaluru")
                .address("Koramangala, Bengaluru")
                .build());

        Screen screen2 = screenRepository.save(Screen.builder().name("Screen 2").theater(pvr).build());
        screenRepository.save(Screen.builder().name("Screen 1").theater(pvr).build());

        seatRepository.saveAll(buildSeats(screen2));

        LocalDate showDate = LocalDate.of(2026, 8, 20);
        if (showDate.isBefore(LocalDate.now())) {
            showDate = LocalDate.now().plusDays(7);
        }

        showService.create(new ShowRequest(
                avengers.getId(),
                pvr.getId(),
                screen2.getId(),
                showDate,
                LocalTime.of(19, 30),
                LocalTime.of(22, 30)
        ));

        log.info("Seeded development data. Admin login: {} / Admin@123 (development only). Sample user: user@bookmyshow.local / User@123",
                admin.getEmail());
    }

    private List<Seat> buildSeats(Screen screen) {
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            seats.add(seat(screen, "A" + i, "A", SeatType.REGULAR, "250.00"));
        }
        for (int i = 1; i <= 6; i++) {
            seats.add(seat(screen, "B" + i, "B", SeatType.PREMIUM, "400.00"));
        }
        for (int i = 1; i <= 4; i++) {
            seats.add(seat(screen, "C" + i, "C", SeatType.VIP, "600.00"));
        }
        return seats;
    }

    private Seat seat(Screen screen, String number, String row, SeatType type, String price) {
        return Seat.builder()
                .seatNumber(number)
                .rowNumber(row)
                .seatType(type)
                .price(new BigDecimal(price))
                .screen(screen)
                .build();
    }
}
