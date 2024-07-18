package com.roze.auth_service.dto;

import com.roze.auth_service.enums.RoleName;
import com.roze.auth_service.feign.UserServiceClient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class SecurityUserDetails implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private Long userProfileId;
    private List<RoleName> roleList;

    @Autowired
    private UserServiceClient userServiceClient;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return roleList == null ? Collections.emptyList() :
                roleList.stream()
                        .map(role -> (GrantedAuthority) role::name)
                        .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
