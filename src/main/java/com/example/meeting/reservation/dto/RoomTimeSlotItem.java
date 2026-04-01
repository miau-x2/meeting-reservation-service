package com.example.meeting.reservation.dto;

import java.time.LocalDateTime;

public record RoomTimeSlotItem(LocalDateTime startAt, LocalDateTime endAt) {
}
