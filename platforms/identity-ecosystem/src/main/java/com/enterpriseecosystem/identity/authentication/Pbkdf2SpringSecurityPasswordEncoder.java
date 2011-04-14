package com.enterpriseecosystem.identity.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.enterpriseecosystem.identity.credential.PasswordHasher;

@Component("pbkdf2SpringSecurityPasswordEncoder")
public class Pbkdf2SpringSecurityPasswordEncoder implements PasswordEncoder {

    private final PasswordHasher passwordHasher;

    @Autowired
    public Pbkdf2SpringSecurityPasswordEncoder(PasswordHasher passwordHasher) {
        this.passwordHasher = passwordHasher;
    }

    public String encodePassword(String rawPass, Object salt) {
        return passwordHasher.hash(rawPass);
    }

    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        return passwordHasher.matches(rawPass, encPass);
    }
}
