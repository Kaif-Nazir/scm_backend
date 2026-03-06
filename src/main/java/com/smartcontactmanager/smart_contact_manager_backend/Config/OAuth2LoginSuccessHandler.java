package com.smartcontactmanager.smart_contact_manager_backend.Config;

import com.smartcontactmanager.smart_contact_manager_backend.Service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private  UserService userService;
    @Value("${app.oauth2.redirect-uri:http://localhost:5173/oauth2/callback}")
    private String redirectUri;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        String providerUserId = oAuth2User.getAttribute("sub");

        Map<String, Object> loginResponse = userService.loginWithGoogle(email, name, picture, providerUserId);
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) loginResponse.get("user");
        String token = String.valueOf(loginResponse.get("token"));
        String id = String.valueOf(user.get("id"));
        String userName = String.valueOf(user.get("name"));

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .queryParam("id", id)
                .queryParam("name", userName)
                .build()
                .toUriString();

        response.sendRedirect(targetUrl);
    }
}
