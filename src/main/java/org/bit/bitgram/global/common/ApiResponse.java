package org.bit.bitgram.global.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bit.bitgram.global.common.enums.ErrorCode;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private final String result;
    private final String message;
    private final T data;
    private final String errorCode;

    /**
     * 성공 응답
     * -> 데이터 필요 x (ex: 삭제, 로그아웃)
     */
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(
                "success",
                null,
                null,
                null
        );
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                "success",
                null,
                data,
                null
        );
    }

    /**
     * 실패 응답
     */
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
