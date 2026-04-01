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
@Table(name = "reservations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {
    @NotNull
    private Long reservedBy;
    @NotNull
    private Long roomId;
    @NotNull
    private LocalDateTime startAt;
    @NotNull
    private LocalDateTime endAt;
    private int attendeeCount;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private Reservation(Long reservedBy, Long roomId, LocalDateTime startAt, LocalDateTime endAt, int attendeeCount) {
        this.reservedBy = reservedBy;
        this.roomId = roomId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.attendeeCount = attendeeCount;
        status = ReservationStatus.RESERVED;
    }

    public static Reservation from(Long reservedBy, Long roomId, LocalDateTime startAt, LocalDateTime endAt, int attendeeCount) {
        return new Reservation(reservedBy, roomId, startAt, endAt, attendeeCount);
    }
}
