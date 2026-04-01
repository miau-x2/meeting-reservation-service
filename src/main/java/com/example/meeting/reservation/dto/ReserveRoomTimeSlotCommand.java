package com.example.meeting.reservation.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ReserveRoomTimeSlotCommand(
        Long roomId,
        List<LocalDateTime> slotStartAtList) {
}
