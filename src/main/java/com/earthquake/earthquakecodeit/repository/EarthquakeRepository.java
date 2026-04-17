package com.earthquake.earthquakecodeit.repository;

import com.earthquake.earthquakecodeit.model.Earthquake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EarthquakeRepository extends JpaRepository<Earthquake,Long> {
    List<Earthquake> findByMagnitudeGreaterThan(Double magnitude);

    List<Earthquake> findByTimeAfter(LocalDateTime time);

    List<Earthquake> findByMagnitudeGreaterThanAndTimeAfter(Double magnitude, LocalDateTime time);

}
