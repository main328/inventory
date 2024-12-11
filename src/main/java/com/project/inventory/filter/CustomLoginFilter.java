package com.project.inventory.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.inventory.dto.AccountDTO;
import com.project.inventory.entity.AccountEntity;
import com.project.inventory.entity.RefreshEntity;
import com.project.inventory.jwt.UtilityJWT;
import com.project.inventory.repository.AccountRepository;
import com.project.inventory.repository.RefreshRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;

public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final UtilityJWT utilityJWT;
    private final AccountRepository accountRepository;
    private final RefreshRepository refreshRepository;

    public CustomLoginFilter(AuthenticationManager authenticationManager, UtilityJWT utilityJWT, AccountRepository accountRepository, RefreshRepository refreshRepository) {
        this.authenticationManager = authenticationManager;
        this.utilityJWT = utilityJWT;
        this.accountRepository = accountRepository;
        this.refreshRepository = refreshRepository;

        // 로그인 URL 설정.
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        AccountDTO authenticationDTO = new AccountDTO();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            authenticationDTO = objectMapper.readValue(messageBody, authenticationDTO.getClass());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String username = authenticationDTO.getUsername();
        String password = authenticationDTO.getPassword();

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password, null);

        return authenticationManager.authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String username = authentication.getName();
        String role = auth.getAuthority();
        // Access 토근 유효 기간 6시간(6*60*60*1000L)
        String access = utilityJWT.createJWT("access", username, role, 21600000L);
        // Refresh 토큰 유효 기간 24시간(24*60*60*1000L)
        String refresh = utilityJWT.createJWT("refresh", username, role, 86400000L);

        AccountEntity isUser = accountRepository.findByUsername(username);
        addRefreshToken(isUser, refresh);

        response.addHeader("access", access);
        response.addCookie(createCookie(refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(401);
    }

    private void addRefreshToken(AccountEntity isUser, String refresh) {
        RefreshEntity isRefresh = new RefreshEntity();
        isRefresh.setToken(refresh);
        isRefresh.setExpiration(LocalDateTime.now());
        isRefresh.setAccount(isUser);

        refreshRepository.save(isRefresh);
    }

    private Cookie createCookie(String value) {
        Cookie cookie = new Cookie("refresh", value);
        // Cookie 유효 기간 24시간(24*60*60)
        cookie.setMaxAge(86400);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        return cookie;
    }
}
