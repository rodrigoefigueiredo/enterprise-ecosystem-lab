package com.enterpriseecosystem.identity.credential;

public interface PasswordHasher {

    String algorithm();

    String hash(String password);
}
