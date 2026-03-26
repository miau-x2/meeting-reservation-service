package com.example.meeting.reservation.authentication;

import com.example.meeting.reservation.common.ErrorMessageConst;
import com.example.meeting.reservation.common.FlashMapKey;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        var flashMap = new FlashMap();
        flashMap.put(FlashMapKey.USERNAME, request.getParameter("username"));

        if (exception instanceof BadCredentialsException) {
            flashMap.put(FlashMapKey.LOGIN_FIELD_ERROR_MESSAGE, "아이디 또는 비밀번호가 일치하지 않습니다.");
        } else {
            flashMap.put(FlashMapKey.LOGIN_GLOBAL_ERROR_MESSAGE, ErrorMessageConst.INTERNAL_SERVER_ERROR);
        }

        var flashMapManager = new SessionFlashMapManager();
        flashMapManager.saveOutputFlashMap(flashMap, request, response);

        getRedirectStrategy().sendRedirect(request, response, "/login");
    }
}
