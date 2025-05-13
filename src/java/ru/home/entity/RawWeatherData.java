package ru.home.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "raw_weather_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawWeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_id", nullable = false)
    private Integer sourceId;

    @Column(name = "payload", nullable = false)
    private String payload;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}