package com.example.meeting.reservation.exception;

public class MeetingRoomNotFoundException extends RuntimeException {
    public MeetingRoomNotFoundException(Long id) {
        super("회의실을 찾을 수 없습니다. id=%d".formatted(id));
    }
}
