package ru.home.service;

import ru.home.dto.AggregatedWeatherResponse;
import ru.home.dto.WeatherData;
import ru.home.entity.NormalizedWeatherData;
import ru.home.entity.RawWeatherData;
import ru.home.repository.NormalizedWeatherDataRepository;
import ru.home.repository.RawWeatherDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    private final RawWeatherDataRepository rawWeatherDataRepository;
    private final NormalizedWeatherDataRepository normalizedWeatherDataRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${weather.sources.count}")
    private Integer sourcesCount;

    @Value("${weather.sources.base-url}")
    private String baseUrl;

    public Mono<AggregatedWeatherResponse> getAggregatedWeatherData() {
        List<WeatherData> normalizedDataList = new CopyOnWriteArrayList<>();

        return Flux.range(1, sourcesCount)
                .flatMap(this::fetchAndProcessWeatherData)
                .doOnNext(normalizedDataList::add)
                .then(aggregateAndSaveData(normalizedDataList));
    }

    private Mono<WeatherData> fetchAndProcessWeatherData(Integer sourceId) {
        String url = baseUrl + "/source/" + sourceId;
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(rawData -> saveRawData(sourceId, rawData))
                .map(rawData -> {
                    WeatherData normalizedData = adaptToCommonFormat(rawData);
                    saveNormalizedData(sourceId, normalizedData);
                    return normalizedData;
                })
                .onErrorResume(e -> {
                    log.error("Error fetching data from source {}: {}", sourceId, e.getMessage());
                    return Mono.empty();
                });
    }

    private WeatherData adaptToCommonFormat(String rawData) {
        try {
            JsonNode rootNode = objectMapper.readTree(rawData);

            // Format 1: { "temp": 20.1, "hum": 55 }
            if (rootNode.has("temp") && rootNode.has("hum")) {
                return new WeatherData(
                        rootNode.get("temp").asDouble(),
                        rootNode.get("hum").asDouble()
                );
            }

            // Format 2: { "temperature": "21.7", "humidity": "58" }
            if (rootNode.has("temperature") && rootNode.has("humidity")) {
                return new WeatherData(
                        rootNode.get("temperature").asDouble(),
                        rootNode.get("humidity").asDouble()
                );
            }

            // Format 3: { "weather": { "t": 22.5, "h": 53.3 } }
            if (rootNode.has("weather")) {
                JsonNode weatherNode = rootNode.get("weather");
                return new WeatherData(
                        weatherNode.get("t").asDouble(),
                        weatherNode.get("h").asDouble()
                );
            }

            throw new IllegalArgumentException("Unknown data format: " + rawData);
        } catch (JsonProcessingException e) {
            log.error("Error parsing weather data: {}", e.getMessage());
            throw new RuntimeException("Error parsing weather data", e);
        }
    }

    private void saveRawData(Integer sourceId, String rawData) {
        RawWeatherData entity = RawWeatherData.builder()
                .sourceId(sourceId)
                .payload(rawData)
                .timestamp(LocalDateTime.now())
                .build();

        rawWeatherDataRepository.save(entity);
    }

    private void saveNormalizedData(Integer sourceId, WeatherData weatherData) {
        NormalizedWeatherData entity = NormalizedWeatherData.builder()
                .sourceId(sourceId)
                .temperature(weatherData.getTemperature())
                .humidity(weatherData.getHumidity())
                .timestamp(LocalDateTime.now())
                .build();

        normalizedWeatherDataRepository.save(entity);
    }

    private Mono<AggregatedWeatherResponse> aggregateAndSaveData(List<WeatherData> weatherDataList) {
        return Mono.fromCallable(() -> {
            if (weatherDataList.isEmpty()) {
                return AggregatedWeatherResponse.builder()
                        .averageTemperature(0.0)
                        .averageHumidity(0.0)
                        .build();
            }

            // Calculate averages
            double avgTemp = weatherDataList.stream()
                    .mapToDouble(WeatherData::getTemperature)
                    .average()
                    .orElse(0.0);

            double avgHum = weatherDataList.stream()
                    .mapToDouble(WeatherData::getHumidity)
                    .average()
                    .orElse(0.0);

            return AggregatedWeatherResponse.builder()
                    .averageTemperature(avgTemp)
                    .averageHumidity(avgHum)
                    .build();
        });
    }
}
