package com.book.together.gatherings.repository;

import com.book.together.gatherings.entity.GatheringParticipant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GatheringParticipantRepository
extends JpaRepository<GatheringParticipant, Long> {

    boolean existsByGathering_IdAndMemberIdAndCanceledAtIsNull(Long gatheringId, Long memberId);

    Optional<GatheringParticipant> findByGathering_IdAndMemberIdAndCanceledAtIsNull(
            Long gatheringId,
            Long memberId
    );

}
