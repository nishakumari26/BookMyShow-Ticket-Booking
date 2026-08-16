package com.nisha.bookmyshow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "movies", indexes = {
        @Index(name = "idx_movies_title", columnList = "title"),
        @Index(name = "idx_movies_language", columnList = "language"),
        @Index(name = "idx_movies_genre", columnList = "genre")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(length = 40)
    private String language;

    @Column(length = 60)
    private String genre;

    private Integer duration;

    private LocalDate releaseDate;

    private String posterUrl;

    private Double rating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MovieStatus status;
}
