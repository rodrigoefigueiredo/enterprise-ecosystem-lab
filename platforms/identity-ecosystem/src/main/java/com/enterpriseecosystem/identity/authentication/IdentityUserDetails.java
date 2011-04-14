package com.enterpriseecosystem.identity.authentication;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class IdentityUserDetails implements UserDetails {

    private final String publicId;
    private final String email;
    private final String passwordHash;
    private final boolean active;
    private final Collection<GrantedAuthority> authorities;

    public IdentityUserDetails(String publicId,
                               String email,
                               String passwordHash,
                               boolean active,
                               Collection<GrantedAuthority> authorities) {
        this.publicId = publicId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.active = active;
        this.authorities = authorities;
    }

    public String getPublicId() {
        return publicId;
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getPassword() {
        return passwordHash;
    }

    public String getUsername() {
        return email;
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return active;
    }
}
