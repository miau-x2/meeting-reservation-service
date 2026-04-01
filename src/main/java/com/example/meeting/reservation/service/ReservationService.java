package com.example.meeting.reservation.service;

import com.example.meeting.reservation.config.ReservationProperties;
import com.example.meeting.reservation.dto.ReservationAddCommand;
import com.example.meeting.reservation.dto.ReserveRoomTimeSlotCommand;
import com.example.meeting.reservation.entity.Reservation;
import com.example.meeting.reservation.exception.AlreadyReservationException;
import com.example.meeting.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static com.example.meeting.reservation.common.util.ReservationValidator.*;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final MeetingRoomService meetingRoomService;
    private final RoomTimeSlotService roomTimeSlotService;
    private final ReservationProperties reservationProperties;
    private final Clock clock;

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Transactional
    public void addReservation(ReservationAddCommand command) {
        var roomId = command.roomId();
        validateRoomId(roomId);

        var room = meetingRoomService.getActiveRoom(roomId);
        var attendeeCount = command.attendeeCount();
        validateAttendeeCount(room.getCapacity(), attendeeCount);

        var reservedBy = command.reservedBy();
        validateEmployeeId(reservedBy);

        var durationHours = command.durationHours();
        validateDurationHours(durationHours);

        var date = command.date();
        validateReservationDate(date, clock);

        var startTime = command.startTime();
        validateReservationTime(startTime, reservationProperties.openTime(), reservationProperties.closeTime(), durationHours);

        var startAt = date.atTime(startTime);
        var slotStartAtList = getSlotStartAtList(startAt, durationHours);

        if(durationHours != roomTimeSlotService.reservedSlot(
                new ReserveRoomTimeSlotCommand(roomId, slotStartAtList))) {
            throw new AlreadyReservationException("이미 예약된 시간입니다.");
        }

        var endAt = startAt.plusHours(durationHours);
        var reservation = Reservation.from(reservedBy, roomId, startAt, endAt, attendeeCount);
        reservationRepository.save(reservation);
    }

    private List<LocalDateTime> getSlotStartAtList(LocalDateTime startAt, int durationHours) {
        return IntStream.range(0, durationHours)
                .mapToObj(startAt::plusHours)
                .toList();
    }
}
