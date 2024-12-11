package com.project.inventory.config;

import com.project.inventory.filter.CustomLoginFilter;
import com.project.inventory.filter.CustomLogoutFilter;
import com.project.inventory.jwt.FilterJWT;
import com.project.inventory.jwt.UtilityJWT;
import com.project.inventory.repository.AccountRepository;
import com.project.inventory.repository.RefreshRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final UtilityJWT utilityJWT;
    private final AccountRepository accountRepository ;
    private final RefreshRepository refreshRepository;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, UtilityJWT utilityJWT, AccountRepository accountRepository, RefreshRepository refreshRepository) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.utilityJWT = utilityJWT;
        this.accountRepository = accountRepository;
        this.refreshRepository = refreshRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors((cors) -> cors.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(Collections.singletonList("http://localhost:8080"));
                config.setAllowedMethods(Collections.singletonList("*"));
                config.setAllowCredentials(true);
                config.setAllowedHeaders(Collections.singletonList("*"));
                config.setMaxAge(3600L);
                config.setExposedHeaders(Collections.singletonList("SetCookie"));
                config.setExposedHeaders(Collections.singletonList("access"));

                return config;
            }
        }));

        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests((request) -> request
                .requestMatchers("/**").permitAll()
                .requestMatchers("/admin").hasRole("ADMIN"));

        http.addFilterBefore(new FilterJWT(utilityJWT), CustomLoginFilter.class);
        http.addFilterBefore(new CustomLogoutFilter(utilityJWT, refreshRepository), LogoutFilter.class);
        http.addFilterAt(new CustomLoginFilter(authenticationManager(authenticationConfiguration), utilityJWT, accountRepository, refreshRepository), UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
