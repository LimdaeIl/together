package com.book.together.auth.exception;

import com.book.together.common.exception.ErrorCode;
import com.book.together.common.exception.ErrorException;

public class AuthException extends ErrorException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}
