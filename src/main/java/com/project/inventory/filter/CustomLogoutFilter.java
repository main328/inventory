package com.project.inventory.filter;

import com.project.inventory.jwt.UtilityJWT;
import com.project.inventory.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class CustomLogoutFilter extends GenericFilterBean {
    private final UtilityJWT utilityJWT;
    private final RefreshRepository refreshRepository;

    public CustomLogoutFilter(UtilityJWT utilityJWT, RefreshRepository refreshRepository) {
        this.utilityJWT = utilityJWT;
        this.refreshRepository = refreshRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, filterChain);
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String requestUri = request.getRequestURI();

        if (!requestUri.matches("^/api/auth/logout$")) {
            filterChain.doFilter(request, response);

            return;
        }

        String requestMethod = request.getMethod();

        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);

            return;
        }

        String refresh = null;
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh"))
                refresh = cookie.getValue();
        }

        if (refresh == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            return;
        }

        try {
            utilityJWT.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            return;
        }

        String category = utilityJWT.getCategory(refresh);

        if (!category.equals("refresh")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            return;
        }

        Boolean existsRefresh = refreshRepository.existsByToken(refresh);

        if (!existsRefresh) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            return;
        }

        refreshRepository.deleteByToken(refresh);

        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
