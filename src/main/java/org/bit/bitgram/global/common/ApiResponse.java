package org.bit.bitgram.global.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bit.bitgram.global.common.enums.ErrorCode;

@Getter
public class ApiResponse<T> {

    private final String result;
    private final String message;
    private final T data;
    private final String errorCode;

    private ApiResponse(String result, String message, T data, String errorCode) {
        this.result = result;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
    }

    // 성공 응답
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                "success",
                null,
                data,
                null
        );
    }

    // 실패 응답
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(
                "fail",
                errorCode.getMessage(),
                null, // 에러 상세 목록
                errorCode.getCode()
        );
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, T data) {
        return new ApiResponse<>(
                "fail",
                errorCode.getMessage(),
                data, // 에러 상세 목록
                errorCode.getCode()
        );
    }

}
