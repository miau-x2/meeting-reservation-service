package com.example.meeting.reservation.exception;

import com.example.meeting.reservation.common.ErrorMessageConst;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionHandler {
    private static final String ERROR_VIEW_NAME = "error";

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFoundException(
            HttpServletResponse response,
            Model model) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("message", "요청한 페이지를 찾을 수 없습니다.");
        return "error";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolationException(
            DataIntegrityViolationException e,
            HttpServletResponse response,
            Model model) {
        log.error("데이터 무결성 예외가 발생했습니다.", e);
        return renderInternalServerError(response, model);
    }

    @ExceptionHandler(Exception.class)
    public String handleException(
            Exception e,
            HttpServletResponse response,
            Model model) {
        log.error("처리되지 않은 예외가 발생했습니다.", e);
        return renderInternalServerError(response, model);
    }

    private String renderInternalServerError(HttpServletResponse response, Model model) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("message", ErrorMessageConst.INTERNAL_SERVER_ERROR);
        return ERROR_VIEW_NAME;
    }
}
