package com.book.together.common.jwt;

import com.book.together.auth.entity.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenProvider {

    private final JwtProvider jwtProvider;

    @Override
    public String issueAt(Long userId, MemberRole role) {
        return jwtProvider.generateAccessToken(userId, role.name());
    }

    @Override
    public String issueRt(Long userId) {
        return jwtProvider.generateRefreshToken(userId);
    }

    @Override
    public String getRtJti(String token) {
        return jwtProvider.getRtJti(token);
    }

    @Override
    public String getAtJti(String token) {
        return jwtProvider.getAtJti(token);
    }

    @Override
    public long getRtTtlMs(String rt) {
        return jwtProvider.getRtTtlMs(rt);
    }

    @Override
    public long getAtTtlMs(String at) {
        return jwtProvider.getAtTtlMs(at);
    }

    @Override
    public Long getRtMemberId(String rt) {
        return jwtProvider.getRtUserId(rt);
    }

    @Override
    public Long getAtMemberId(String at) {
        return jwtProvider.getAtUserId(at);
    }


}
