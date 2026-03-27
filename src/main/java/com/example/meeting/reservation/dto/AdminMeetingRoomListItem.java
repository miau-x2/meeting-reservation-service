package com.example.meeting.reservation.dto;

public record AdminMeetingRoomListItem(
        Long id,
        String name,
        int floor,
        int capacity,
        String description,
        boolean active) {
}
