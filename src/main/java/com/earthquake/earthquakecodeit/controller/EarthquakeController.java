package com.earthquake.earthquakecodeit.controller;


import com.earthquake.earthquakecodeit.model.Earthquake;
import com.earthquake.earthquakecodeit.service.EarthquakeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/earthquakes")
@CrossOrigin(origins = "*")
public class EarthquakeController {
    private final EarthquakeService earthquakeService;

    public EarthquakeController(EarthquakeService earthquakeService) {
        this.earthquakeService = earthquakeService;
    }

    @GetMapping("/fetch")
    public String fetchAndStore() {
        earthquakeService.fetchAndStore();
        return "Earthquakes fetched and stored successfully!";
    }

    @GetMapping
    public List<Earthquake> getAll() {
        return earthquakeService.getAll();
    }


    @GetMapping("/magnitude")
    public List<Earthquake> getByMagnitude(@RequestParam Double mag) {
        return earthquakeService.getByMagnitude(mag);
    }


    @GetMapping("/after-time")
    public List<Earthquake> getAfterTime(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime time
    ) {
        return earthquakeService.getByTimeAfter(time);
    }
    @GetMapping("/filter")
    public List<Earthquake> filter(
            @RequestParam Double mag,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime time
    ) {
        return earthquakeService.getByMagnitudeAndTime(mag, time);
    }

}
