package com.example.meeting.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationAddCommand(
        Long reservedBy,
        Long roomId,
        LocalDate date,
        LocalTime startTime,
        int durationHours,
        int attendeeCount) {
}
