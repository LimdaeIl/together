package com.book.together.common.exception;

import lombok.Getter;

@Getter
public class ErrorException extends RuntimeException {

    private final ErrorCode errorCode;

    private final transient Object[] args;

    public ErrorException(ErrorCode errorCode, Object... args) {
        super(errorCode.format(args));
        this.errorCode = errorCode;
        this.args = args;
    }


    public ErrorException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = null;
    }
}
