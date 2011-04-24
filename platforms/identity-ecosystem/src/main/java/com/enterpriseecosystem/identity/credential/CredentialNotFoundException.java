package com.enterpriseecosystem.identity.credential;

public class CredentialNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CredentialNotFoundException(String userPublicId) {
        super(userPublicId);
    }
}
