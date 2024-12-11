package com.project.inventory.jwt;

import com.project.inventory.dto.CustomUserDetails;
import com.project.inventory.entity.AccountEntity;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class FilterJWT extends OncePerRequestFilter {
    private final UtilityJWT utilityJWT;

    public FilterJWT(UtilityJWT utilityJWT) {
        this.utilityJWT = utilityJWT;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader("access");

        if (accessToken == null) {
            filterChain.doFilter(request, response);

            return;
        }

        try {
            utilityJWT.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }
        String category = utilityJWT.getCategory(accessToken);

        if (!category.equals("access")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        String username = utilityJWT.getUsername(accessToken);
        String role = utilityJWT.getRole(accessToken);

        AccountEntity isUser = new AccountEntity();
        isUser.setUsername(username);
        isUser.setRole(role);

        CustomUserDetails customUserDetails = new CustomUserDetails(isUser);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}
