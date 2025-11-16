package com.book.together.gatherings.exception;

import com.book.together.common.exception.ErrorCode;
import com.book.together.common.exception.ErrorException;

public class GatheringException extends ErrorException {

    public GatheringException(ErrorCode errorCode) {
        super(errorCode);
    }


    public GatheringException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}
