package com.example.meeting.reservation.common.util;

import com.example.meeting.reservation.exception.*;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;

public final class ReservationValidator {
    private ReservationValidator() {}

    public static void validateRoomId(Long id) {
        if(id == null) {
            throw new InvalidMeetingRoomIdException("유효 하지 않은 회의실 ID입니다.");
        }
    }

    public static void validateEmployeeId(Long id) {
        if(id == null) {
            throw new InvalidEmployeeIdException("유효 하지 않은 직원 ID입니다.");
        }
    }

    public static void validateAttendeeCount(int threshold, int attendeeCount) {
        if(attendeeCount <= 0) {
            throw new InvalidAttendeeCountException("회의실 사용 인원은 1명 이상이어야 합니다.");
        }

        if(threshold < attendeeCount) {
            throw new ExceedMeetingRoomException("허용된 수용 인원을 초과했습니다.");
        }
    }

    public static void validateDurationHours(int hours) {
        if (hours != 1 && hours != 2) {
            throw new InvalidReservationTimeException("예약 시간은 1시간 또는 2시간만 가능합니다.");
        }
    }

    public static void validateReservationDate(LocalDate date, Clock clock) {
        if (date == null) {
            throw new InvalidReservationDateException("예약 날짜는 필수입니다.");
        }

        if(date.isBefore(LocalDate.now(clock))) {
            throw new InvalidReservationDateException("지난 날짜로는 예약할 수 없습니다.");
        }
    }

    public static void validateReservationTime(LocalTime startTime, LocalTime openTime, LocalTime closeTime, int durationHours) {
        if (startTime == null) {
            throw new InvalidReservationTimeException("예약 시작 시간은 필수입니다.");
        }

        if(startTime.isBefore(openTime)) {
            throw new InvalidReservationTimeException("예약 시작 시간은 운영 시작 시간 이후여야 합니다.");
        }

        var endTime = startTime.plusHours(durationHours);

        if (endTime.isAfter(closeTime)) {
            throw new InvalidReservationTimeException("예약 종료 시간은 운영 종료 시간 이전이어야 합니다.");
        }

        if (startTime.getMinute() != 0 || startTime.getSecond() != 0 || startTime.getNano() != 0) {
            throw new InvalidReservationTimeException("예약 시작 시간은 정각이어야 합니다.");
        }
    }
}
