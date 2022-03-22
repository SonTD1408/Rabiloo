package com.namtg.egovernment.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namtg.egovernment.service.RedirectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RestApiAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Autowired
    private RedirectService redirectService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        String urlRedirect;
        if(savedRequest != null) {
            urlRedirect = savedRequest.getRedirectUrl();
        } else {
            CustomUserDetail currentUserLogin = (CustomUserDetail) authentication.getPrincipal();
            urlRedirect = redirectService.getUrlDefault(currentUserLogin);
        }

        clearAuthenticationAttributes(request);
        responseRedirectUrl(response, urlRedirect);

    }

    public void responseRedirectUrl(HttpServletResponse response, String urlRedirect) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            response.getWriter().write(objectMapper.writeValueAsString(urlRedirect));
        } catch (IOException e) {
            return;
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
