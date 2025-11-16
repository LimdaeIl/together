package com.book.together.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum CommonErrorCode implements ErrorCode {

    // System & Infra
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "공통: 서버 내부 오류가 발생했습니다."),
    REQUEST_CONTEXT_NOT_FOUND(HttpStatus.NOT_FOUND, "공통: 요청 컨텍스트를 찾을 수 없습니다."),

    // Request & Format & Protocol
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "공통: 잘못된 입력입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "공통: 허용되지 않은 HTTP 메서드입니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "공통: 지원하지 않는 콘텐츠 타입입니다."),

    // Resource & Status
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "공통: 요청한 리소스를 찾을 수 없습니다."),
    RESPONSE_BODY_WRITE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "공통: 응답 본문을 생성/쓰기 중 오류가 발생했습니다."),
    MEDIA_TYPE_NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "공통: 요청한 응답 형식을 제공할 수 없습니다."),

    // authorization, authentication, filter
    TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, "공통: 인증 토큰이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "공통: 인증 토큰이 유효하지 않습니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "공통: 인증에 실패했습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "공통: 인증이 필요합니다."),
    INVALID_HEADER_USER_ROLE(HttpStatus.BAD_REQUEST, "공통: 값이 올바른 회원 권한 형식이 아닙니다."),
    INVALID_HEADER_USER_ID_NOT_INTEGER(HttpStatus.BAD_REQUEST, "공통: 회원 ID 헤더 값이 숫자가 아닙니다."),
    MISSING_HEADER_USER_ID(HttpStatus.BAD_REQUEST, "공통: 회원 ID 헤더가 누락되었습니다."),
    MISSING_HEADER_USER_ROLE(HttpStatus.BAD_REQUEST, "공통: 회원 권한 헤더가 누락되었습니다."),
    INVALID_USER_INFO(HttpStatus.BAD_REQUEST, "공통: 현재 사용자 정보가 올바르지 않습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "공통: 접근 권한이 없습니다."),


    // role parse
    NULL_USER_ROLE(HttpStatus.BAD_REQUEST, "공통: 권한이 NULL 입니다."),
    EMPTY_USER_ROLE(HttpStatus.BAD_REQUEST, "공통: 권한이 빈 값입니다.");


    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}