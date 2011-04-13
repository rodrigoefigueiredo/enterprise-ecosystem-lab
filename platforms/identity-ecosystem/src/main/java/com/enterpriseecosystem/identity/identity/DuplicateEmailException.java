package com.enterpriseecosystem.identity.identity;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("E-mail already exists: " + email);
    }
}
