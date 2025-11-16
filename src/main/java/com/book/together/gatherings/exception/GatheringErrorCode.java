package com.book.together.gatherings.exception;

import com.book.together.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum GatheringErrorCode implements ErrorCode {
    INVALID_LOCATION(HttpStatus.BAD_REQUEST, "모임: 지원하지 않는 모임 장소입니다. value={0}"),
    INVALID_TYPE(HttpStatus.BAD_REQUEST, "모임: 모임 서비스 종류는 필수입니다."),
    INVALID_DATETIME(HttpStatus.BAD_REQUEST, "모임: 이미 지난 시간에는 모임을 생성할 수 없습니다."),
    INVALID_CAPACITY(HttpStatus.BAD_REQUEST, "모임: 모임 정원은 최소 5명 이상이어야 합니다."),
    INVALID_REGISTRATION_END(HttpStatus.BAD_REQUEST, "모임: 모집 마감일은 모임 시작 시간 이전이어야 합니다."),
    DUPLICATE_GATHERING(HttpStatus.CONFLICT, "모임: 해당 장소와 시간에 이미 모임이 존재합니다."),
    NOT_FOUND_ID(HttpStatus.NOT_FOUND, "모임: ID에 해당하는 모임을 찾을 수 없습니다."),
    ALREADY_JOINED(HttpStatus.CONFLICT, "모임: 이미 참여한 모임입니다."),
    GATHERING_CANCELED(HttpStatus.BAD_REQUEST, "모임: 이미 취소된 모임입니다."),
    GATHERING_FULL(HttpStatus.BAD_REQUEST, "모임: 이미 정원이 모두 차서 참여할 수 없습니다."),
    GATHERING_CLOSED(HttpStatus.BAD_REQUEST, "모임: 모집 마감 이후에는 참여할 수 없습니다.");


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
