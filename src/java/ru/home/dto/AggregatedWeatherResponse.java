package ru.home.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AggregatedWeatherResponse {
    private Double averageTemperature;
    private Double averageHumidity;
}
