package com.earthquake.earthquakecodeit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "earthquakes",
        indexes = {
                @Index(name = "idx_time", columnList = "time"),
                @Index(name = "idx_magnitude", columnList = "magnitude")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Earthquake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(nullable = false)
    private Double magnitude;

    @Column(name = "mag_type")
    private String magType;

    @Column(nullable = false)
    private String place;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime time;

    private Double latitude;
    private Double longitude;
    private Double depth;
}
