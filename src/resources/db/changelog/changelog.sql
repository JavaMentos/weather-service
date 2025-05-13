--liquibase formatted sql

--changeset developer:1
-- Создание таблицы для сырых данных о погоде
CREATE TABLE raw_weather_data
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_id INT       NOT NULL,
    payload   TEXT      NOT NULL,
    timestamp TIMESTAMP NOT NULL
);

--changeset developer:2
-- Создание таблицы для нормализованных данных о погоде
CREATE TABLE normalized_weather_data
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_id   INT       NOT NULL,
    temperature DOUBLE    NOT NULL,
    humidity    DOUBLE    NOT NULL,
    timestamp   TIMESTAMP NOT NULL
);

--changeset developer:3
-- Создание индексов для оптимизации поиска по source_id
CREATE INDEX idx_raw_weather_source_id ON raw_weather_data (source_id);
CREATE INDEX idx_normalized_weather_source_id ON normalized_weather_data (source_id);