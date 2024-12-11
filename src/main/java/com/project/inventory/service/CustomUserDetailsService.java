package com.project.inventory.service;

import com.project.inventory.dto.CustomUserDetails;
import com.project.inventory.entity.AccountEntity;
import com.project.inventory.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AccountRepository accountRepository;

    public CustomUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountEntity isUser = accountRepository.findByUsername(username);

        if (isUser != null) {
            log.info(isUser.toString());
            return new CustomUserDetails(isUser);
        }

        return null;
    }
}
