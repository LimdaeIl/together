package com.book.together.gatherings.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "v1_gathering_participants")
@Entity
public class GatheringParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id", nullable = false)
    private Gathering gathering;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    private GatheringParticipant(Gathering gathering, Long memberId) {
        this.gathering = gathering;
        this.memberId = memberId;
        this.joinedAt = LocalDateTime.now();
        this.canceledAt = null;
    }

    public static GatheringParticipant of(Gathering gathering, Long memberId) {
        return new GatheringParticipant(gathering, memberId);
    }

    public void cancel() {
        this.canceledAt = LocalDateTime.now();
    }

    public boolean isCanceled() {
        return canceledAt != null;
    }

    public static GatheringParticipant join(Gathering gathering, Long memberId) {
        return new GatheringParticipant(gathering, memberId);
    }

}
