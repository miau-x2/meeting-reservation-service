package com.example.meeting.reservation.authentication;

import com.example.meeting.reservation.entity.EmployeeRole;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(of = "id")
public class CustomUserDetails implements UserDetails {
    @Getter
    private final Long id;
    private final String username;
    private final String password;
    private final EmployeeRole role;

    public CustomUserDetails(Long id, String username, String password, EmployeeRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
