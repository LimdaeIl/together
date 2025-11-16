package com.book.together.gatherings.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "v1_gathering")
@Entity
public class Gathering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private GatheringType gatheringType;

    private String name;

    private LocalDateTime dateTime; // 모집 시작일자

    private LocalDateTime registrationEnd; // 모집 마감일자

    @Enumerated(EnumType.STRING)
    private GatheringLocation gatheringLocation;

    private int participantCount;

    private int capacity;

    private String image;

    private LocalDateTime createdAt;

    private Long createdBy;

    private LocalDateTime canceledAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "gathering", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GatheringParticipant> participants = new ArrayList<>();


    @Builder(access = AccessLevel.PRIVATE)
    private Gathering(
            GatheringType gatheringType,
            String name,
            LocalDateTime dateTime,
            LocalDateTime registrationEnd,
            GatheringLocation gatheringLocation,
            int capacity,
            String image,
            Long createdBy
    ) {
        this.gatheringType = gatheringType;
        this.name = name;
        this.dateTime = dateTime;
        this.registrationEnd = registrationEnd;
        this.gatheringLocation = gatheringLocation;
        this.participantCount = 0;
        this.capacity = capacity;
        this.image = image;
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
        this.canceledAt = null;
        this.updatedAt = null;
    }

    public static Gathering of(
            GatheringType gatheringType,
            String name,
            LocalDateTime dateTime,
            LocalDateTime registrationEnd,
            GatheringLocation gatheringLocation,
            int capacity,
            String imageUrl,
            Long createdBy
    ) {
        return Gathering.builder()
                .gatheringType(gatheringType)
                .name(name)
                .dateTime(dateTime)
                .registrationEnd(registrationEnd)
                .gatheringLocation(gatheringLocation)
                .capacity(capacity)
                .image(imageUrl)
                .createdBy(createdBy)
                .build();
    }

    public void addParticipant(GatheringParticipant participant) {
        participants.add(participant);
    }

    public void increaseParticipantCount() {
        this.participantCount += 1;
    }

    public void decreaseParticipantCount() {
        if (this.participantCount > 0) {
            this.participantCount -= 1;
        }
    }
}
