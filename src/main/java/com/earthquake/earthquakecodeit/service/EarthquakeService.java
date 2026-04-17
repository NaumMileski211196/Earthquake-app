package com.earthquake.earthquakecodeit.service;


import com.earthquake.earthquakecodeit.model.Earthquake;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface EarthquakeService {

    void fetchAndStore();

    List<Earthquake> getAll();

    List<Earthquake> getByMagnitude(Double magnitude);

    List<Earthquake> getByTimeAfter(LocalDateTime time);

    List<Earthquake> getByMagnitudeAndTime(Double magnitude, LocalDateTime time);
}
