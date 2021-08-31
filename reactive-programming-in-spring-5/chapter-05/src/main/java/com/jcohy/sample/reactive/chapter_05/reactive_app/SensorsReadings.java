package com.jcohy.sample.reactive.chapter_05.reactive_app;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:11:02
 * @since 1.0.0
 */
@Document(collection = SensorsReadings.COLLECTION_NAME)
public class SensorsReadings {

    public static final String COLLECTION_NAME = "iot-readings";

    @JsonIgnore
    @Id
    private ObjectId id;

    private LocalDateTime readingTime;

    private Double temperature;

    private Double humidity;

    private Double luminosity;

    public SensorsReadings() {
    }

    public SensorsReadings(ObjectId id, LocalDateTime readingTime, Double temperature, Double humidity, Double luminosity) {
        this.id = id;
        this.readingTime = readingTime;
        this.temperature = temperature;
        this.humidity = humidity;
        this.luminosity = luminosity;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public LocalDateTime getReadingTime() {
        return readingTime;
    }

    public void setReadingTime(LocalDateTime readingTime) {
        this.readingTime = readingTime;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getLuminosity() {
        return luminosity;
    }

    public void setLuminosity(Double luminosity) {
        this.luminosity = luminosity;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SensorsReadings{");
        sb.append("id=").append(id);
        sb.append(", readingTime=").append(readingTime);
        sb.append(", temperature=").append(temperature);
        sb.append(", humidity=").append(humidity);
        sb.append(", luminosity=").append(luminosity);
        sb.append('}');
        return sb.toString();
    }
}
