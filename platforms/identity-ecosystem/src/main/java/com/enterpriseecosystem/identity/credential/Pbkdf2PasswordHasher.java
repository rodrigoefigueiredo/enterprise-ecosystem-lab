package com.enterpriseecosystem.identity.credential;

import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.stereotype.Component;

@Component
public class Pbkdf2PasswordHasher implements PasswordHasher {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int SALT_BYTES = 16;
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH_BITS = 256;

    private final SecureRandom secureRandom = new SecureRandom();

    public String algorithm() {
        return ALGORITHM;
    }

    public String hash(String password) {
        try {
            byte[] salt = new byte[SALT_BYTES];
            secureRandom.nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();

            return ITERATIONS + ":" + DatatypeConverter.printBase64Binary(salt)
                    + ":" + DatatypeConverter.printBase64Binary(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Could not hash password", e);
        }
    }
}
