package com.nisha.bookmyshow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "theaters", indexes = {
        @Index(name = "idx_theaters_city", columnList = "city")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 80)
    private String location;

    @Column(nullable = false, length = 60)
    private String city;

    @Column(length = 255)
    private String address;
}
