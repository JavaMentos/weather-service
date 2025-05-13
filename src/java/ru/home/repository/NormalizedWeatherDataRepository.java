package ru.home.repository;

import ru.home.entity.NormalizedWeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NormalizedWeatherDataRepository extends JpaRepository<NormalizedWeatherData, Long> {
}