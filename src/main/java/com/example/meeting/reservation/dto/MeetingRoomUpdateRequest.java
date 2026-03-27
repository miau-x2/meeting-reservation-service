package com.example.meeting.reservation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MeetingRoomUpdateRequest(
        @NotBlank(message = "회의실 이름을 입력해주세요.")
        @Size(max = 50, message = "회의실 이름은 50자 이하여야 합니다.")
        String name,
        @Min(value = 1, message = "층수는 1층 이상이어야 합니다.")
        @Max(value = 10, message = "층수는 10층 이하여야 합니다.")
        int floor,
        @NotBlank(message = "회의실 설명을 입력해주세요.")
        @Size(max = 100, message = "회의실 설명은 100자 이하여야 합니다.")
        String description,
        @Min(value = 1, message = "수용 인원은 1명 이상이어야 합니다.")
        @Max(value = 200, message = "수용 인원은 200명 이하여야 합니다.")
        int capacity) {

    public static MeetingRoomUpdateRequest empty() {
        return new MeetingRoomUpdateRequest("", 1, "", 1);
    }
}
