package com.book.together.auth.dto.response;

import com.book.together.auth.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record ReIssueResponse(
        Long id,
        String newAt,
        String newRt,
        long newAtTtlMs,
        long newRtTtlMs
) {

    public static ReIssueResponse of(Member member, String newAt, String newRt, long newAtTtlMs,
            long newRtTtlMs) {
        return ReIssueResponse.builder()
                .id(member.getId())
                .newAt(newAt)
                .newRt(newRt)
                .newAtTtlMs(newAtTtlMs)
                .newRtTtlMs(newRtTtlMs)
                .build();
    }
}
