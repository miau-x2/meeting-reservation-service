package com.example.meeting.reservation.job;

import com.example.meeting.reservation.config.ReservationProperties;
import com.example.meeting.reservation.dto.RoomTimeSlotInsertItem;
import com.example.meeting.reservation.dto.RoomTimeSlotItem;
import com.example.meeting.reservation.repository.MeetingRoomRepository;
import com.example.meeting.reservation.repository.RoomTimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.LongStream;

@Component
@RequiredArgsConstructor
public class RoomTimeSlotGenerationJob implements Job {
    private final MeetingRoomRepository meetingRoomRepository;
    private final RoomTimeSlotRepository roomTimeSlotRepository;
    private final ReservationProperties reservationProperties;
    private final Clock clock;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        var roomIds = meetingRoomRepository.findAllActiveRoomIds();
        var date = LocalDate.now(clock);
        var roomTimeSlotItems = generateRoomTimeSlot(date);
        var now = LocalDateTime.now(clock);
        var roomTimeSlotInsertItems = generateRoomTimeSlotInsertItem(roomIds, roomTimeSlotItems, now);
        roomTimeSlotRepository.bulkInsert(roomTimeSlotInsertItems);
    }

    private List<RoomTimeSlotInsertItem> generateRoomTimeSlotInsertItem(List<Long> ids, List<RoomTimeSlotItem> items, LocalDateTime now) {
        return ids.stream()
                .flatMap(id -> items.stream()
                        .map(slot -> RoomTimeSlotInsertItem.from(id, slot.startAt(), slot.endAt(), now))
                )
                .toList();
    }

    private List<RoomTimeSlotItem> generateRoomTimeSlot(LocalDate date) {
        var openTime = reservationProperties.openTime();
        var closeTime = reservationProperties.closeTime();
        var duration = Duration.between(openTime, closeTime).toHours();

        return LongStream.range(0, duration)
                .mapToObj(time -> {
                    var startAt = date.atTime(openTime).plusHours(time);
                    var endAt = startAt.plusHours(1);
                    return new RoomTimeSlotItem(startAt, endAt);
                })
                .toList();
    }
}
