package com.example.meeting.reservation.repository;

import com.example.meeting.reservation.dto.RoomTimeSlotInsertItem;
import com.example.meeting.reservation.dto.RoomTimeSlotItem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RoomTimeSlotRepositoryCustom {
    long bulkReserve(Long roomId, List<LocalDateTime> slotStartAtList);
    void bulkInsert(List<RoomTimeSlotInsertItem> items);
    List<RoomTimeSlotItem> findAvailableSlotsByRoomIdAndDate(Long roomId, LocalDate date);
}
