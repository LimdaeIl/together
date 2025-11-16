package com.book.together.auth.dto.response;

import com.book.together.auth.entity.Member;
import com.book.together.auth.entity.MemberRole;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record SignupResponse(
        Long id,
        String email,
        MemberRole role,
        String name,
        String companyName
) {

    public static SignupResponse from(Member member) {
        return SignupResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .role(member.getMemberRole())
                .name(member.getName())
                .companyName(member.getCompanyName())
                .build();
    }
}
