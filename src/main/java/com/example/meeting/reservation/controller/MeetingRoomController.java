package com.example.meeting.reservation.controller;

import com.example.meeting.reservation.service.MeetingRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MeetingRoomController {
    private final MeetingRoomService meetingRoomService;

    @GetMapping("/rooms")
    public String getActiveRooms(Pageable pageable, Model model) {
        model.addAttribute("roomsPage", meetingRoomService.getActiveRooms(pageable));
        model.addAttribute("pageSize", pageable.getPageSize());
        return "meeting/rooms";
    }
}
