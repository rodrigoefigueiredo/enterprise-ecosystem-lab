package com.enterpriseecosystem.identity.credential;

public interface PasswordHasher {

    String algorithm();

    String hash(String password);

    boolean matches(String password, String storedHash);
}
