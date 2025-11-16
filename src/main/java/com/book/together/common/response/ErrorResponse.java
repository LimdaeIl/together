package com.book.together.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import org.springframework.http.HttpStatus;

/**
 * <p>공통 에러 응답</p>
 *
 * <p>일관된 예외 응답 형식으로 직렬화해서 클라이언트에게 응답합니다.</p>
 *
 * @param httpStatus    HTTP 상태 코드(정수)
 * @param message       메시지(민감 정보 금지)
 * @param errorCode     도메인 에러 코드(선택) 예: {@code COMMON-S-1}
 * @param fieldErrors   필드 단위 오류 목록(선택)
 *
 */
@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int httpStatus,
        String message,
        String errorCode,
        List<FieldError> fieldErrors
) {

    public static ErrorResponse of(HttpStatus httpStatus, String message) {
        return ErrorResponse.builder()
                .httpStatus(httpStatus.value())
                .message(message)
                .errorCode(null)
                .fieldErrors(null)
                .build();
    }


    public static ErrorResponse of(HttpStatus httpStatus, String message, String errorCode) {
        return ErrorResponse.builder()
                .httpStatus(httpStatus.value())
                .message(message)
                .errorCode(errorCode)
                .fieldErrors(null)
                .build();
    }

    public static ErrorResponse of(HttpStatus httpStatus, String message, List<FieldError> data) {
        return ErrorResponse.builder()
                .httpStatus(httpStatus.value())
                .message(message)
                .errorCode(null)
                .fieldErrors((data == null || data.isEmpty()) ? null : data)
                .build();
    }

    public static ErrorResponse of(HttpStatus status, String message, String errorCode,
            List<FieldError> fieldErrors) {
        return ErrorResponse.builder()
                .httpStatus(status.value())
                .message(message)
                .errorCode(errorCode)
                .fieldErrors(fieldErrors)
                .build();
    }

    /**
     *
     * @param field
     * @param reason
     */
    public record FieldError(String field, String reason) {

        public static FieldError of(String field, String reason) {
            return new FieldError(field, reason);
        }
    }
}
