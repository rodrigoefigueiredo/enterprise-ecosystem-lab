package com.enterpriseecosystem.identity.identity;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String publicId) {
        super(publicId);
    }
}
