package com.example.meeting.reservation.exception;

public class InvalidMeetingRoomIdException extends RuntimeException {
    public InvalidMeetingRoomIdException(String message) {
        super(message);
    }
}
