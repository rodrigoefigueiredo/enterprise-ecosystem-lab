package com.enterpriseecosystem.identity.credential;

public class InvalidPasswordException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidPasswordException() {
        super("Password does not satisfy the password policy.");
    }
}
