package com.example.meeting.reservation.exception;

public class InvalidAttendeeCountException extends RuntimeException {
    public InvalidAttendeeCountException(String message) {
        super(message);
    }
}
