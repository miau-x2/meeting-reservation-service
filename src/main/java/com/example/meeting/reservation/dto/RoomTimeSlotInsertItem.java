package com.example.meeting.reservation.dto;

import com.example.meeting.reservation.entity.RoomTimeSlotStatus;

import java.time.LocalDateTime;

public record RoomTimeSlotInsertItem(
        Long roomId,
        LocalDateTime slotStartAt,
        LocalDateTime slotEndAt,
        RoomTimeSlotStatus status,
        LocalDateTime createdAt) {

    public static RoomTimeSlotInsertItem from(
            Long roomId,
            LocalDateTime slotStartAt,
            LocalDateTime slotEndAt,
            LocalDateTime createdAt) {

        return new RoomTimeSlotInsertItem(roomId,slotStartAt, slotEndAt, RoomTimeSlotStatus.AVAILABLE, createdAt);
    }
}
