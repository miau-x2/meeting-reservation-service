package com.example.meeting.reservation.repository.impl;

import com.example.meeting.reservation.dto.PageResponse;
import com.example.meeting.reservation.dto.AdminMeetingRoomListItem;
import com.example.meeting.reservation.dto.MeetingRoomListItem;
import com.example.meeting.reservation.entity.QMeetingRoom;
import com.example.meeting.reservation.repository.MeetingRoomRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static com.example.meeting.reservation.entity.QMeetingRoom.meetingRoom;

@Repository
@RequiredArgsConstructor
public class MeetingRoomRepositoryImpl implements MeetingRoomRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public PageResponse<MeetingRoomListItem> getActiveRooms(Integer floor, Pageable pageable) {
        QMeetingRoom meetingRoom = QMeetingRoom.meetingRoom;
        var predicate = new BooleanBuilder();
        predicate.and(meetingRoom.active.isTrue());

        if (floor != null) {
            predicate.and(meetingRoom.floor.eq(floor));
        }

        var content = jpaQueryFactory
                .select(Projections.constructor(
                        MeetingRoomListItem.class,
                        meetingRoom.id,
                        meetingRoom.name,
                        meetingRoom.floor,
                        meetingRoom.capacity,
                        meetingRoom.description
                ))
                .from(meetingRoom)
                .where(predicate)
                .orderBy(meetingRoom.floor.asc(), meetingRoom.name.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        var count = jpaQueryFactory
                .select(meetingRoom.count())
                .from(meetingRoom)
                .where(predicate)
                .fetchOne();

        var totalElements = count == null ? 0L : count;
        return PageResponse.from(pageable.getPageNumber(), pageable.getPageSize(), totalElements, content);
    }

    @Override
    public PageResponse<AdminMeetingRoomListItem> getAdminRooms(Integer floor, Boolean active, Pageable pageable) {
        QMeetingRoom meetingRoom = QMeetingRoom.meetingRoom;
        var predicate = new BooleanBuilder();

        if (floor != null) {
            predicate.and(meetingRoom.floor.eq(floor));
        }
        if (active != null) {
            predicate.and(meetingRoom.active.eq(active));
        }

        var content = jpaQueryFactory
                .select(Projections.constructor(
                        AdminMeetingRoomListItem.class,
                        meetingRoom.id,
                        meetingRoom.name,
                        meetingRoom.floor,
                        meetingRoom.capacity,
                        meetingRoom.description,
                        meetingRoom.active
                ))
                .from(meetingRoom)
                .where(predicate)
                .orderBy(meetingRoom.active.desc(), meetingRoom.floor.asc(), meetingRoom.name.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        var count = jpaQueryFactory
                .select(meetingRoom.count())
                .from(meetingRoom)
                .where(predicate)
                .fetchOne();

        var totalElements = count == null ? 0L : count;
        return PageResponse.from(pageable.getPageNumber(), pageable.getPageSize(), totalElements, content);
    }

    @Override
    public void updateActive(Long id, boolean active) {
        jpaQueryFactory
                .update(meetingRoom)
                .set(meetingRoom.active, active)
                .where(meetingRoom.id.eq(id))
                .execute();
    }
}
