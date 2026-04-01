package com.example.meeting.reservation.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationAddRequest(
        @NotNull
        LocalDate date,
        @NotNull
        LocalTime startTime,
        int durationHours,
        int attendeeCount) {
}