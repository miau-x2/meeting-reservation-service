package com.example.meeting.reservation.controller;

import com.example.meeting.reservation.common.ApiResponse;
import com.example.meeting.reservation.common.MeetingRoomSuccessCode;
import com.example.meeting.reservation.dto.MeetingRoomFloorNameCheckResponse;
import com.example.meeting.reservation.service.MeetingRoomService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMeetingRoomRestController {
    private final MeetingRoomService meetingRoomService;

    @GetMapping("/rooms/check-floor-name")
    public ResponseEntity<ApiResponse<MeetingRoomFloorNameCheckResponse>> checkFloorName(
            @RequestParam
            @Min(value = 1, message = "층수는 1층 이상이어야 합니다.")
            @Max(value = 10, message = "층수는 10층 이하여야 합니다.")
            int floor,
            @NotBlank(message = "회의실 이름을 입력해주세요.")
            @Size(max = 50, message = "회의실 이름은 50자 이하여야 합니다.")
            String name) {

        var available = meetingRoomService.isFloorNameAvailable(floor, name);
        var response = new MeetingRoomFloorNameCheckResponse(available);
        var code = MeetingRoomSuccessCode.CHECK_FLOOR_NAME;

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.success(code, response));
    }
}
