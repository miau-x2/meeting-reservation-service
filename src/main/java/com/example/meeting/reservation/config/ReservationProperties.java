package com.example.meeting.reservation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.time.LocalTime;

@ConfigurationProperties(prefix = "app.reservation")
public record ReservationProperties(LocalTime openTime, LocalTime closeTime, Duration maxDuration) {
}
