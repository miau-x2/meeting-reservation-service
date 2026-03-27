package com.example.meeting.reservation.exception;

public class DuplicateMeetingRoomException extends RuntimeException {
    public DuplicateMeetingRoomException(String message) {
        super(message);
    }
}
