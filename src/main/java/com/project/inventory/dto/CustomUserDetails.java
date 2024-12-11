package com.project.inventory.dto;

import com.project.inventory.entity.AccountEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final AccountEntity authenticationEntity;

    public CustomUserDetails(AccountEntity authenticationEntity) {
        this.authenticationEntity = authenticationEntity;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return authenticationEntity.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {
        return authenticationEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return authenticationEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
