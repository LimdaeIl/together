package com.book.together.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * <p>공통 성공/실패 응답</p>
 *
 * 성공: {@code { "code": "SUCCESS", "message": "OK", "data": ... }} <br/>
 * 실패: {@link ErrorResponse}
 *
 * @param code      공통 응답 코드 {@code SUCCESS, ERROR}: {@link ApiResponseCode}
 * @param message   메시지
 * @param data      데이터
 * @param <T>       페이로드 타입
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        ApiResponseCode code,
        String message,
        T data
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ApiResponseCode.SUCCESS, "OK", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(ApiResponseCode.SUCCESS, message, data);
    }


    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(ApiResponseCode.ERROR, message, null);
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(ApiResponseCode.ERROR, message, data);
    }


}
