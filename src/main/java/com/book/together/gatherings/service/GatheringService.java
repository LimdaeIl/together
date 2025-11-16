package com.book.together.gatherings.service;

import com.book.together.common.util.CurrentUserInfo;
import com.book.together.gatherings.dto.request.CreateGatheringRequest;
import com.book.together.gatherings.dto.response.CreateGatheringResponse;
import com.book.together.gatherings.entity.Gathering;
import com.book.together.gatherings.entity.GatheringLocation;
import com.book.together.gatherings.entity.GatheringParticipant;
import com.book.together.gatherings.entity.GatheringType;
import com.book.together.gatherings.exception.GatheringErrorCode;
import com.book.together.gatherings.exception.GatheringException;
import com.book.together.gatherings.repository.GatheringParticipantRepository;
import com.book.together.gatherings.repository.GatheringRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class GatheringService {

    private final GatheringRepository gatheringRepository;

    private final GatheringParticipantRepository gatheringParticipantRepository;

    private static final Integer MAX_CAPACITY = 3;

    public CreateGatheringResponse create(CreateGatheringRequest request,
            CurrentUserInfo info) {

        String locationValue = request.location();
        GatheringType type = request.type();
        String name = request.name();
        LocalDateTime localDateTime = request.dateTime();
        Integer capacity = request.capacity();
        String image = request.image() == null ? null : request.image().trim();
        LocalDateTime registrationEnd = request.registrationEnd();

        // 1) 비즈니스 검증
        if (capacity == null || capacity < MAX_CAPACITY) {
            throw new GatheringException(GatheringErrorCode.INVALID_CAPACITY);
        }

        if (type == null) {
            throw new GatheringException(GatheringErrorCode.INVALID_TYPE);
        }

        if (localDateTime == null || localDateTime.isBefore(LocalDateTime.now())) {
            throw new GatheringException(GatheringErrorCode.INVALID_DATETIME);
        }

        if (registrationEnd != null && registrationEnd.isAfter(localDateTime)) {
            throw new GatheringException(GatheringErrorCode.INVALID_REGISTRATION_END);
        }

        // 2) 모임 위치 문자열 -> enum 변환 ("건대입구" -> GatheringLocation.KONKUK_UNIVERSITY_STATION)
        GatheringLocation gatheringLocation = GatheringLocation.from(locationValue);

        // 3) 중복 체크
        boolean duplicated = gatheringRepository
                .existsByGatheringLocationAndDateTimeAndCanceledAtIsNull(
                        gatheringLocation,
                        localDateTime
                );

        if (duplicated) {
            throw new GatheringException(
                    GatheringErrorCode.DUPLICATE_GATHERING,
                    locationValue,
                    localDateTime
            );
        }

        // 4) 엔티티 생성
        Long currentUserId = info.userId();

        Gathering gathering = Gathering.of(
                type,
                name,
                localDateTime,
                registrationEnd,
                gatheringLocation,
                capacity,
                image,
                currentUserId
        );

        // 4-1) 주최자를 참가자로 등록
        GatheringParticipant hostParticipant = GatheringParticipant.join(gathering, currentUserId);
        gathering.addParticipant(hostParticipant);
        gathering.increaseParticipantCount();

        // 5) 저장 (CascadeType.ALL 덕분에 participant도 같이 저장됨)
        Gathering saved = gatheringRepository.save(gathering);

        // 6) 응답 변환
        return CreateGatheringResponse.from(saved);
    }


    public void join(Long id, CurrentUserInfo info) {
        // 1) 모임 조회
        Gathering gathering = gatheringRepository.findById(id)
                .orElseThrow(() -> new GatheringException(GatheringErrorCode.NOT_FOUND_ID));

        Long memberId = info.userId();

        // 2) 모임 상태 검증
        if (gathering.getCanceledAt() != null) {
            throw new GatheringException(GatheringErrorCode.GATHERING_CANCELED);
        }

        LocalDateTime now = LocalDateTime.now();

        // 모집 마감 이후면 참가 불가
        if (gathering.getRegistrationEnd() != null &&
                gathering.getRegistrationEnd().isBefore(now)) {
            throw new GatheringException(GatheringErrorCode.GATHERING_CLOSED);
        }

        // 모임 시간이 이미 지났으면 참가 불가 (선택이지만 보통 이렇게 막음)
        if (gathering.getDateTime().isBefore(now)) {
            throw new GatheringException(GatheringErrorCode.INVALID_DATETIME);
        }

        // 정원 초과 여부
        if (gathering.getParticipantCount() >= gathering.getCapacity()) {
            throw new GatheringException(GatheringErrorCode.GATHERING_FULL);
        }

        // 3) 이미 참여 중인지 확인 (취소 안 한 상태)
        boolean alreadyJoined = gatheringParticipantRepository
                .existsByGathering_IdAndMemberIdAndCanceledAtIsNull(gathering.getId(), memberId);

        if (alreadyJoined) {
            throw new GatheringException(GatheringErrorCode.ALREADY_JOINED);
        }

        // 4) 참가 엔티티 생성
        GatheringParticipant participant = GatheringParticipant.join(gathering, memberId);

        // 5) 모임 참가자 수 증가
        gathering.increaseParticipantCount();

        // 6) 저장
        gatheringParticipantRepository.save(participant);
    }
}
