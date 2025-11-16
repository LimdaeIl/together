package com.book.together.common.util;

import com.book.together.auth.entity.MemberRole;

public record CurrentUserInfo(
        Long userId,
        MemberRole memberRole
) {

    public static CurrentUserInfo of(Long userId, MemberRole memberRole) {
        return new CurrentUserInfo(userId, memberRole);
    }

}
