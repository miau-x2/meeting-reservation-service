package com.example.meeting.reservation.exception;

import com.example.meeting.reservation.common.ApiResponse;
import com.example.meeting.reservation.common.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class EmployeeRestExceptionHandler {
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound() {
        var code = CommonErrorCode.NOT_FOUND;
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.error(code));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("처리되지 않은 예외가 발생했습니다.", e);
        var code = CommonErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(code));
    }
}
