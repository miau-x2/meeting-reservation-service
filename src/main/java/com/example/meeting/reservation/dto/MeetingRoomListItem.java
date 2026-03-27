package com.example.meeting.reservation.dto;

public record MeetingRoomListItem(
        Long id,
        String name,
        int floor,
        int capacity,
        String description) {
}
