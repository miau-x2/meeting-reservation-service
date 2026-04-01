package com.example.meeting.reservation.repository.impl;

import com.example.meeting.reservation.dto.RoomTimeSlotInsertItem;
import com.example.meeting.reservation.dto.RoomTimeSlotItem;
import com.example.meeting.reservation.entity.RoomTimeSlotStatus;
import com.example.meeting.reservation.repository.RoomTimeSlotRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.meeting.reservation.entity.QRoomTimeSlot.roomTimeSlot;

@Repository
@RequiredArgsConstructor
public class RoomTimeSlotRepositoryImpl implements RoomTimeSlotRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public long bulkReserve(Long roomId, List<LocalDateTime> slotStartAtList) {
        return jpaQueryFactory.update(roomTimeSlot)
                .set(roomTimeSlot.status, RoomTimeSlotStatus.RESERVED)
                .where(
                        roomTimeSlot.roomId.eq(roomId),
                        roomTimeSlot.status.eq(RoomTimeSlotStatus.AVAILABLE),
                        roomTimeSlot.slotStartAt.in(slotStartAtList)
                )
                .execute();
    }

    @Override
    public void bulkInsert(List<RoomTimeSlotInsertItem> items) {
        var sql = """
                  insert into room_time_slots(room_id, slot_start_at, slot_end_at, status, created_at)
                  values(?, ?, ?, ?, ?)
                  """;
        jdbcTemplate.batchUpdate(
                sql,
                items,
                items.size(),
                (ps, item) -> {
                    ps.setLong(1, item.roomId());
                    ps.setObject(2, item.slotStartAt());
                    ps.setObject(3, item.slotEndAt());
                    ps.setString(4,item.status().name());
                    ps.setObject(5, item.createdAt());
                });
    }

    @Override
    public List<RoomTimeSlotItem> findAvailableSlotsByRoomIdAndDate(Long roomId, LocalDate date) {
        var startOfDay = date.atStartOfDay();
        var nextOfDay = date.plusDays(1).atStartOfDay();

        return jpaQueryFactory.select(Projections.constructor(
                RoomTimeSlotItem.class,
                roomTimeSlot.slotStartAt,
                roomTimeSlot.slotEndAt))
                .from(roomTimeSlot)
                .where(
                        roomTimeSlot.roomId.eq(roomId),
                        roomTimeSlot.status.eq(RoomTimeSlotStatus.AVAILABLE),
                        roomTimeSlot.slotStartAt.goe(startOfDay),
                        roomTimeSlot.slotEndAt.lt(nextOfDay)
                        )
                .orderBy(roomTimeSlot.slotStartAt.asc())
                .fetch();
    }
}
