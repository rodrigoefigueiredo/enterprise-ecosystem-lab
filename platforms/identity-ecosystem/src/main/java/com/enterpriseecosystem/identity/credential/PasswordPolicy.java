package com.enterpriseecosystem.identity.credential;

import org.springframework.stereotype.Component;

@Component
public class PasswordPolicy {

    public static final int MINIMUM_LENGTH = 12;

    public boolean accepts(String password) {
        return password != null && password.length() >= MINIMUM_LENGTH;
    }
}
