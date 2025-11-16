package com.book.together.auth.entity;

import com.book.together.common.exception.CommonErrorCode;
import com.book.together.common.exception.ErrorException;
import java.util.Locale;

public enum MemberRole {
    USER,
    ADMIN;

    // JWT 안의 role 문자열을 파싱 → 실패 시 401 INVALID_TOKEN
    public static MemberRole parseForToken(String raw) {
        try {
            return parse(raw);
        } catch (IllegalArgumentException e) {
            throw new ErrorException(CommonErrorCode.INVALID_TOKEN);
        }
    }

    // 헤더/요청 속성의 role 문자열을 파싱 → 실패 시 400 INVALID_HEADER_USER_ROLE
    public static MemberRole parseForHeader(String raw) {
        try {
            return parse(raw);
        } catch (IllegalArgumentException e) {
            throw new ErrorException(CommonErrorCode.INVALID_HEADER_USER_ROLE);
        }
    }

    private static MemberRole parse(String raw) {
        if (raw == null) {
            throw new ErrorException(CommonErrorCode.NULL_USER_ROLE);
        }

        String t = raw.trim();
        if (t.isEmpty()) {
            throw new ErrorException(CommonErrorCode.EMPTY_USER_ROLE);
        }

        t = t.toUpperCase(Locale.ROOT);
        if (t.startsWith("ROLE_")) {
            t = t.substring(5);
        }

        return MemberRole.valueOf(t);
    }
}
