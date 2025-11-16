package com.book.together.common.jwt;

import com.book.together.auth.entity.MemberRole;

public interface TokenProvider {

    String issueAt(Long memberId, MemberRole memberRole);

    String issueRt(Long memberId);

    String getRtJti(String token);

    String getAtJti(String token);

    long getRtTtlMs(String rt);

    long getAtTtlMs(String at);

    Long getRtMemberId(String rt);

    Long getAtMemberId(String at);
}