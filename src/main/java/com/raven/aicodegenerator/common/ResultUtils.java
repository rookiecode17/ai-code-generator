package com.raven.aicodegenerator.common;


import com.raven.aicodegenerator.exception.ErrorCode;

/**
 * utility class for quickly building standardized response results
 */
public class ResultUtils {

    /**
     * success
     *
     * @param data data
     * @param <T>  data type
     * @return response
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * failure
     *
     * @param errorCode error code
     * @return response
     */
    public static BaseResponse<?> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * failure
     *
     * @param code    error code
     * @param message error message
     * @return response
     */
    public static BaseResponse<?> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }

    /**
     * failure
     *
     * @param errorCode error code
     * @return response
     */
    public static BaseResponse<?> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }
}