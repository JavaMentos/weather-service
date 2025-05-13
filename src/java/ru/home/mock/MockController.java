package ru.home.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/source")
public class MockController {

    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/{id}")
    public ResponseEntity<String> getWeatherData(@PathVariable Integer id) {
        // Simulate some delay
        try {
            Thread.sleep(random.nextInt(300));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Generate random temperature (15-30°C) and humidity (40-70%)
        double temperature = 15 + (random.nextDouble() * 15);
        double humidity = 40 + (random.nextDouble() * 30);

        // Return in different formats based on the source ID
        switch (id % 3) {
            case 0:
                // Format 1: { "temp": 20.1, "hum": 55 }
                ObjectNode format1 = objectMapper.createObjectNode();
                format1.put("temp", temperature);
                format1.put("hum", humidity);
                return ResponseEntity.ok(format1.toString());

            case 1:
                // Format 2: { "temperature": "21.7", "humidity": "58" }
                ObjectNode format2 = objectMapper.createObjectNode();
                format2.put("temperature", String.valueOf(temperature));
                format2.put("humidity", String.valueOf(humidity));
                return ResponseEntity.ok(format2.toString());

            case 2:
                // Format 3: { "weather": { "t": 22.5, "h": 53.3 } }
                ObjectNode weatherNode = objectMapper.createObjectNode();
                weatherNode.put("t", temperature);
                weatherNode.put("h", humidity);

                ObjectNode format3 = objectMapper.createObjectNode();
                format3.set("weather", weatherNode);
                return ResponseEntity.ok(format3.toString());

            default:
                return ResponseEntity.internalServerError().build();
        }
    }
}