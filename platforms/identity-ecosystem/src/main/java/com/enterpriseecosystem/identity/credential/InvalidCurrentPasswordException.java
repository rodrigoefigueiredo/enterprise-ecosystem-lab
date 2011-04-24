package com.enterpriseecosystem.identity.credential;

public class InvalidCurrentPasswordException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidCurrentPasswordException() {
        super("Current password is invalid.");
    }
}
