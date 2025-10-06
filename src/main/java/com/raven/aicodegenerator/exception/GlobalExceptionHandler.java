package com.raven.aicodegenerator.exception;

import cn.hutool.json.JSONUtil;

import com.raven.aicodegenerator.common.BaseResponse;
import com.raven.aicodegenerator.common.ResultUtils;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Map;

@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        // attempt to process the SSE request
        if (handleSseError(e.getCode(), e.getMessage())) {
            return null;
        }
        // for normal requests, return a standard JSON response
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        // attempt to handle the SSE request
        if (handleSseError(ErrorCode.SYSTEM_ERROR.getCode(), "System Error")) {
            return null;
        }
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "System Error");
    }

    /**
     * handle error response for SSE request
     *
     * @param errorCode error code
     * @param errorMessage error message
     * @return true: request is an SSE request and has been handled; false: request is not an SSE request
     */
    private boolean handleSseError(int errorCode, String errorMessage) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return false;
        }
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();
        // Check if the request is SSE (by Accept header or request path)
        String accept = request.getHeader("Accept");
        String uri = request.getRequestURI();
        if ((accept != null && accept.contains("text/event-stream")) ||
                uri.contains("/chat/gen/code")) {
            try {
                // set SSE response headers
                response.setContentType("text/event-stream");
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Connection", "keep-alive");
                // construct SSE formatted error message
                Map<String, Object> errorData = Map.of(
                        "error", true,
                        "code", errorCode,
                        "message", errorMessage
                );
                String errorJson = JSONUtil.toJsonStr(errorData);
                // send a business error event (to avoid clashing with the standard error event)
                String sseData = "event: business-error\ndata: " + errorJson + "\n\n";
                response.getWriter().write(sseData);
                response.getWriter().flush();
                // send end event
                response.getWriter().write("event: done\ndata: {}\n\n");
                response.getWriter().flush();
                // indicate that the SSE request has been handled
                return true;
            } catch (IOException ioException) {
                log.error("Failed to write SSE error response", ioException);
                // even if writing fails, still mark it as an SSE request
                return true;
            }
        }
        return false;
    }
}