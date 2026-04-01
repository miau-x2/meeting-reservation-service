package com.example.meeting.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public record AvailableRoomItemSlotResponse(
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,
        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime) {
}
