package com.book.together.auth.exception;

import com.book.together.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum AuthErrorCode implements ErrorCode {

    EXISTS_BY_EMAIL(HttpStatus.CONFLICT, "회원가입: 이미 존재하는 이메일입니다."),
    NOT_FOUND_BY_EMAIL(HttpStatus.BAD_REQUEST, "로그인: 존재하지 않는 이메일입니다."),
    INCORRECT_PW(HttpStatus.BAD_REQUEST, "비밀번호: 비밀번호가 일치하지 않습니다."),
    RT_REGISTERED_BLACKLIST(HttpStatus.FORBIDDEN, "회원: 해당 RT 토큰은 블랙리스트로 등록되어 있습니다."),
    AT_BLACKLIST(HttpStatus.FORBIDDEN, "회원: 해당 AT 토큰은 블랙리스트로 등록되어 있습니다."),
    RT_BLACKLIST(HttpStatus.FORBIDDEN, "회원: 해당 RT 토큰은 블랙리스트로 등록되어 있습니다."),
    RT_NOT_FOUND(HttpStatus.NOT_FOUND, "회원: 해당 RT 토큰은 찾을 수 없습니다."),
    RT_JTI_INCORRECT(HttpStatus.BAD_REQUEST, "회원: 저장된 RT 토큰의 JTI와 틀립니다.");



    private final HttpStatus code;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return null;
    }

    @Override
    public String getMessage() {
        return "";
    }
}
