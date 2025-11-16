package com.book.together.common.resolver;

import static com.book.together.common.util.AuthKeys.Attr.USER_ID;
import static com.book.together.common.util.AuthKeys.Attr.USER_ROLE;
import static com.book.together.common.util.AuthKeys.Header.HDR_USER_ID;
import static com.book.together.common.util.AuthKeys.Header.HDR_USER_ROLE;

import com.book.together.auth.entity.MemberRole;
import com.book.together.common.annotation.CurrentUser;
import com.book.together.common.exception.CommonErrorCode;
import com.book.together.common.exception.ErrorException;
import com.book.together.common.util.CurrentUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j(topic = "CurrentUserArgumentResolver")
@RequiredArgsConstructor
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Value("${security.current-user.allow-header-fallback:false}")
    private boolean allowHeaderFallback;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (!parameter.hasParameterAnnotation(CurrentUser.class)) {
            return false;
        }

        Class<?> type = parameter.getParameterType();
        if (CurrentUserInfo.class.isAssignableFrom(type)) {
            return true;
        }

        if (Optional.class.isAssignableFrom(type)) {
            Type g = parameter.getGenericParameterType();
            if (g instanceof ParameterizedType pt) {
                Type arg = pt.getActualTypeArguments()[0];
                if (arg instanceof Class<?> c && CurrentUserInfo.class.isAssignableFrom(c)) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest req = webRequest.getNativeRequest(HttpServletRequest.class);
        if (req == null) {
            throw new ErrorException(CommonErrorCode.REQUEST_CONTEXT_NOT_FOUND);
        }

        boolean wantsOptional = Optional.class.isAssignableFrom(parameter.getParameterType());

        CurrentUserInfo info = extractCurrentUser(req);

        if (wantsOptional) {
            return Optional.ofNullable(info);
        }

        if (info == null) {
            // 인증 정보가 전혀 없는 경우(익명) → 401
            throw new ErrorException(CommonErrorCode.UNAUTHORIZED);
        }
        return info;
    }

    /**
     * attribute 우선, 필요 시(설정에 따라) header fallback
     */
    private CurrentUserInfo extractCurrentUser(HttpServletRequest req) {
        Object idAttr = req.getAttribute(USER_ID);
        Object roleAttr = req.getAttribute(USER_ROLE);

        Long userId = coerceUserId(idAttr);
        MemberRole role = coerceUserRole(roleAttr);

        if (userId == null || role == null) {
            if (allowHeaderFallback) {
                if (userId == null) {
                    userId = parseUserIdHeader(req.getHeader(HDR_USER_ID));
                }
                if (role == null) {
                    role = parseUserRoleHeader(req.getHeader(HDR_USER_ROLE));
                }
            }
        }

        if (userId == null && role == null) {
            return null; // 완전 익명
        }
        if (userId == null) {
            throw new ErrorException(CommonErrorCode.MISSING_HEADER_USER_ID);
        }
        if (role == null) {
            throw new ErrorException(CommonErrorCode.MISSING_HEADER_USER_ROLE);
        }

        try {
            return CurrentUserInfo.of(userId, role);
        } catch (IllegalArgumentException e) {
            throw new ErrorException(CommonErrorCode.INVALID_USER_INFO);
        }
    }

    private Long coerceUserId(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Long l) {
            return l;
        }
        if (v instanceof Integer i) {
            return i.longValue();
        }
        if (v instanceof String s) {
            try {
                return Long.parseLong(s.trim());
            } catch (NumberFormatException ignore) {
                return null;
            }
        }
        return null;
    }

    private MemberRole coerceUserRole(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof MemberRole r) {
            return r;
        }
        if (v instanceof String s) {
            return toUserRoleOrNull(s);
        }
        return null;
    }

    private Long parseUserIdHeader(String headerVal) {
        if (headerVal == null || headerVal.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(headerVal.trim());
        } catch (NumberFormatException e) {
            throw new ErrorException(CommonErrorCode.INVALID_HEADER_USER_ID_NOT_INTEGER);
        }
    }

    private MemberRole parseUserRoleHeader(String headerVal) {
        if (headerVal == null || headerVal.isBlank()) {
            return null;
        }
        MemberRole userRoleOrNull = toUserRoleOrNull(headerVal);
        if (userRoleOrNull == null) {
            throw new ErrorException(CommonErrorCode.INVALID_HEADER_USER_ROLE);
        }
        return userRoleOrNull;
    }

    private MemberRole toUserRoleOrNull(String s) {
        try {
            return MemberRole.valueOf(s.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}