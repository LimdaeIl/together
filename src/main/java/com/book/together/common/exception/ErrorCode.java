package com.book.together.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus getHttpStatus();

    String getMessage();

    default String format(Object... args) {
        return String.format(getMessage(), args);
    }
}
