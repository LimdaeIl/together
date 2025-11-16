package com.book.together.gatherings.repository;

import com.book.together.gatherings.entity.Gathering;
import com.book.together.gatherings.entity.GatheringLocation;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GatheringRepository extends JpaRepository<Gathering,Long> {

    boolean existsByGatheringLocationAndDateTimeAndCanceledAtIsNull(
            GatheringLocation gatheringLocation,
            LocalDateTime dateTime
    );
}
