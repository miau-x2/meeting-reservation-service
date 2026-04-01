package com.example.meeting.reservation.exception;

public class InvalidReservationTimeException extends RuntimeException {
    public InvalidReservationTimeException(String message) {
        super(message);
    }
}
