package ru.home.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "normalized_weather_data")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NormalizedWeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_id", nullable = false)
    private Integer sourceId;

    @Column(name = "temperature", nullable = false)
    private Double temperature;

    @Column(name = "humidity", nullable = false)
    private Double humidity;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
