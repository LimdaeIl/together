package com.book.together.gatherings.dto.request;

import com.book.together.gatherings.entity.GatheringType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateGatheringRequest(

        @NotBlank(message = "모임: 모임 장소는 필수입니다.")
        String location,

        @NotNull(message = "모임: 모임 서비스 종류는 필수입니다.")
        GatheringType type,

        @NotBlank(message = "모임: 모임 이름은 필수입니다.")
        String name,

        @NotNull(message = "모임: 모임 날짜 및 시간은 필수입니다.")
        @Future(message = "모임: 이미 지난 시간에는 모임을 생성할 수 없습니다.")
        LocalDateTime dateTime,

        @NotNull(message = "모임: 모임 정원(최소 5인 이상)은 필수입니다.")
        @Min(value = 3, message = "모임: 모임 정원은 최소 5명 이상이어야 합니다.")
        Integer capacity,

        String image,

        LocalDateTime registrationEnd
) {

}
