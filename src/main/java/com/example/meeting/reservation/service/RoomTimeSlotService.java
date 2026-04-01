package com.example.meeting.reservation.service;

import com.example.meeting.reservation.dto.AvailableRoomItemSlotResponse;
import com.example.meeting.reservation.dto.ReserveRoomTimeSlotCommand;
import com.example.meeting.reservation.dto.RoomTimeSlotItem;
import com.example.meeting.reservation.repository.RoomTimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.meeting.reservation.common.util.ReservationValidator.*;

@Service
@RequiredArgsConstructor
public class RoomTimeSlotService {
    private final RoomTimeSlotRepository roomTimeSlotRepository;
    private final Clock clock;

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public long reservedSlot(ReserveRoomTimeSlotCommand command) {
        return roomTimeSlotRepository.bulkReserve(command.roomId(), command.slotStartAtList());
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public List<AvailableRoomItemSlotResponse> getAvailableSlots(int durationHours, Long roomId, LocalDate date) {
        validateDurationHours(durationHours);
        validateRoomId(roomId);
        validateReservationDate(date, clock);

        var slots =  roomTimeSlotRepository.findAvailableSlotsByRoomIdAndDate(roomId, date);
        if(durationHours == 1) {
            return getAvailableOneHourSlots(slots);
        }

        return getAvailableTwoHourSlots(slots);
    }

    private List<AvailableRoomItemSlotResponse> getAvailableTwoHourSlots(List<RoomTimeSlotItem> items) {
        var result = new ArrayList<AvailableRoomItemSlotResponse>();
        var idx = 0;
        while (idx < items.size() - 1) {
            var cur = items.get(idx);
            var next = items.get(idx + 1);

            if(cur.endAt().equals(next.startAt())) {
                var startTime = cur.startAt().toLocalTime();
                var endTime = next.endAt().toLocalTime();
                var slot = new AvailableRoomItemSlotResponse(startTime, endTime);
                result.add(slot);
                idx += 2;
            }
            else {
                idx++;
            }
        }

        return result;
    }

    private List<AvailableRoomItemSlotResponse> getAvailableOneHourSlots(List<RoomTimeSlotItem> items) {
        return items.stream()
                .map(slot -> {
                    var startTime = slot.startAt().toLocalTime();
                    var endTime = slot.endAt().toLocalTime();
                    return new AvailableRoomItemSlotResponse(startTime, endTime);
                })
                .toList();
    }
}
