package ru.home.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherData {
    private Double temperature;
    private Double humidity;
}
