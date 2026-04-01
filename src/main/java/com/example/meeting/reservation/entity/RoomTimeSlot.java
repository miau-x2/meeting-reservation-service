package com.example.meeting.reservation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "room_time_slots")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomTimeSlot extends BaseEntity {
    @NotNull
    private Long roomId;
    @NotNull
    private LocalDateTime slotStartAt;
    @NotNull
    private LocalDateTime slotEndAt;
    @Enumerated(EnumType.STRING)
    private RoomTimeSlotStatus status;

    private RoomTimeSlot(Long roomId, LocalDateTime slotStartAt, LocalDateTime slotEndAt) {
        this.roomId = roomId;
        this.slotStartAt = slotStartAt;
        this.slotEndAt = slotEndAt;
        status = RoomTimeSlotStatus.AVAILABLE;
    }

    public static RoomTimeSlot from(Long roomId, LocalDateTime slotStartAt, LocalDateTime slotEndAt) {
        return new RoomTimeSlot(roomId, slotStartAt, slotEndAt);
    }
}
