package com.book.together.auth.dto.response;

import com.book.together.auth.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record SignInResponse(
        Long id,
        String at,
        String rt,
        long atTtlMs,
        long rtTtlMs
) {

    public static SignInResponse of(Member member, String at, String rt, long atTtlMs, long rtTtlMs) {
        return SignInResponse.builder()
                .id(member.getId())
                .at(at)
                .rt(rt)
                .atTtlMs(atTtlMs)
                .rtTtlMs(rtTtlMs)
                .build();
    }
}
