package ru.home.repository;

import ru.home.entity.RawWeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawWeatherDataRepository extends JpaRepository<RawWeatherData, Long> {
}
