package com.book.together.common.aspect;

import static com.book.together.common.util.AuthKeys.Attr.USER_ROLE;

import com.book.together.auth.entity.MemberRole;
import com.book.together.common.annotation.RequireRole;
import com.book.together.common.exception.CommonErrorCode;
import com.book.together.common.exception.ErrorException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.EnumSet;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j(topic = "AuthorizationAspect")
@Aspect
@Component
public class AuthorizationAspect {


    @Before("@within(com.book.together.common.annotation.RequireRole) || @annotation(com.book.together.common.annotation.RequireRole)")
    public void requireRole(JoinPoint jp) {
        RequireRole requireRole = resolveRequireRole(jp);
        if (requireRole == null) {
            return; // 애너테이션이 없으면 권한 체크 생략
        }

        if (requireRole.allowAnonymous()) {
            return; // 익명 허용인 경우 바로 통과
        }

        MemberRole currentUserRole = currentUserRole(); // 권한 없으면 401 발생

        MemberRole[] required = requireRole.value();
        if (required.length == 0) {
            return;
        }
        EnumSet<MemberRole> allowedUserRole = EnumSet.noneOf(MemberRole.class);
        allowedUserRole.addAll(Arrays.asList(required));

        if (!allowedUserRole.contains(currentUserRole)) {
            if (log.isDebugEnabled()) {
                log.debug("포함되지 않은 권한 거부: 현재 권한: {}, 허용 권한: {}", currentUserRole,
                        Arrays.toString(required));
            }
            throw new ErrorException(CommonErrorCode.FORBIDDEN);
        }


    }

    private RequireRole resolveRequireRole(JoinPoint jp) {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        RequireRole annotation = signature.getMethod().getAnnotation(RequireRole.class);

        if (annotation != null) {
            return annotation;
        }

        return jp.getTarget().getClass().getAnnotation(RequireRole.class);
    }

    private MemberRole currentUserRole() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs == null) {
            throw new ErrorException(CommonErrorCode.REQUEST_CONTEXT_NOT_FOUND);
        }

        HttpServletRequest request = attrs.getRequest();
        Object roleAttr = request.getAttribute(USER_ROLE);

        if (roleAttr == null) {
            throw new ErrorException(CommonErrorCode.MISSING_HEADER_USER_ROLE);
        }

        if (roleAttr instanceof MemberRole userRole) {
            return userRole;
        }

        // 혹시 문자열이 들어온 경우만 방어적으로 파싱 → 실패 시 400
        if (roleAttr instanceof String s) {
            return MemberRole.parseForHeader(s);
        }

        //  기타 타입은 형식 오류
        if (log.isDebugEnabled()) {
            log.debug("Unsupported USER_ROLE attr type: {}", roleAttr.getClass().getName());
        }

        throw new ErrorException(CommonErrorCode.INVALID_HEADER_USER_ROLE);
    }
}
