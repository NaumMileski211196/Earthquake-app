package com.earthquake.earthquakecodeit.service.impl;

import com.earthquake.earthquakecodeit.model.Earthquake;
import com.earthquake.earthquakecodeit.repository.EarthquakeRepository;
import com.earthquake.earthquakecodeit.service.EarthquakeService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class EarthquakeServiceImpl implements EarthquakeService {

    private final EarthquakeRepository earthquakeRepository;
    private final RestTemplate restTemplate = new RestTemplate(); //ni sluzi za prakjanje na HTTP baranja do API
    private final ObjectMapper mapper = new ObjectMapper(); // 

    private final String url =
            "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";

    public EarthquakeServiceImpl(EarthquakeRepository earthquakeRepository) {
        this.earthquakeRepository = earthquakeRepository;
    }


    @Override
    public void fetchAndStore() {
        try {
            String json = restTemplate.getForObject(url, String.class);

            if (json == null) {
                throw new RuntimeException("API returned null response");
            }

            JsonNode root = mapper.readTree(json);
            JsonNode features = root.path("features");

            if (!features.isArray() || features.isEmpty()) {
                throw new RuntimeException("No earthquake data found in API response");
            }

            List<Earthquake> earthquakes = new ArrayList<>();

            for (JsonNode feature : features) {

                JsonNode props = feature.path("properties");
                JsonNode geometry = feature.path("geometry");

                if (props.isMissingNode() || geometry.isMissingNode()) continue;

                JsonNode magNode = props.get("mag");
                JsonNode placeNode = props.get("place");
                JsonNode timeNode = props.get("time");

                if (magNode == null || magNode.isNull() ||
                        placeNode == null || placeNode.isNull() ||
                        timeNode == null || timeNode.isNull()) {
                    continue;
                }

                Double mag = magNode.asDouble();

                if (mag <= 2.0) continue;

                String place = placeNode.asText();

                long timeMillis = timeNode.asLong();

                LocalDateTime time = Instant.ofEpochMilli(timeMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                JsonNode coords = geometry.path("coordinates");

                if (!coords.isArray() || coords.size() < 3) continue;

                double lon = coords.get(0).asDouble();
                double lat = coords.get(1).asDouble();
                double depth = coords.get(2).asDouble();

                Earthquake eq = new Earthquake(
                        null,
                        mag,
                        "ml",
                        place,
                        "Earthquake - " + place,
                        time,
                        lat,
                        lon,
                        depth
                );

                earthquakes.add(eq);
            }

            earthquakeRepository.deleteAll();

            if (!earthquakes.isEmpty()) {
                earthquakeRepository.saveAll(earthquakes);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error fetching or parsing earthquake data", e);
        }
    }

    @Override
    public List<Earthquake> getAll() {
        return earthquakeRepository.findAll();
    }

    @Override
    public List<Earthquake> getByMagnitude(Double magnitude) {
        return earthquakeRepository.findByMagnitudeGreaterThan(magnitude);
    }

    @Override
    public List<Earthquake> getByTimeAfter(LocalDateTime time) {
        return earthquakeRepository.findByTimeAfter(time);
    }

    @Override
    public List<Earthquake> getByMagnitudeAndTime(Double magnitude, LocalDateTime time) {
        return earthquakeRepository.findByMagnitudeGreaterThanAndTimeAfter(magnitude, time);
    }
}
