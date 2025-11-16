package com.book.together.common.jwt;


import com.book.together.common.exception.ErrorCode;
import com.book.together.common.exception.ErrorException;

public class TokenException extends ErrorException {

    public TokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
