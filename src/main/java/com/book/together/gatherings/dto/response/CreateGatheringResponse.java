package com.book.together.gatherings.dto.response;

import com.book.together.gatherings.entity.Gathering;
import com.book.together.gatherings.entity.GatheringType;
import java.time.LocalDateTime;

public record CreateGatheringResponse(

        Long id,
        GatheringType type,
        String name,
        LocalDateTime dateTime,
        LocalDateTime registrationEnd,
        String location,
        int participantCount,
        int capacity,
        String image,
        Long createdBy
) {

    public static CreateGatheringResponse from(Gathering gathering) {
        return new CreateGatheringResponse(
                gathering.getId(),
                gathering.getGatheringType(),
                gathering.getName(),
                gathering.getDateTime(),
                gathering.getRegistrationEnd(),
                gathering.getGatheringLocation().getName(),
                gathering.getParticipantCount(),
                gathering.getCapacity(),
                gathering.getImage(),
                gathering.getCreatedBy()
        );
    }
}
