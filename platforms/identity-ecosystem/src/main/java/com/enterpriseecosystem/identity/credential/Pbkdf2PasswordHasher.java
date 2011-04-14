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

            byte[] hash = pbkdf2(password, salt, ITERATIONS);

            return ITERATIONS + ":" + DatatypeConverter.printBase64Binary(salt)
                    + ":" + DatatypeConverter.printBase64Binary(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Could not hash password", e);
        }
    }

    public boolean matches(String password, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 3) {
                return false;
            }

            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = DatatypeConverter.parseBase64Binary(parts[1]);
            byte[] expectedHash = DatatypeConverter.parseBase64Binary(parts[2]);
            byte[] actualHash = pbkdf2(password, salt, iterations);

            return constantTimeEquals(expectedHash, actualHash);
        } catch (Exception e) {
            return false;
        }
    }

    private byte[] pbkdf2(String password, byte[] salt, int iterations) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH_BITS);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        return factory.generateSecret(spec).getEncoded();
    }

    private boolean constantTimeEquals(byte[] expected, byte[] actual) {
        if (expected == null || actual == null) {
            return false;
        }

        int diff = expected.length ^ actual.length;
        int max = Math.max(expected.length, actual.length);
        for (int i = 0; i < max; i++) {
            byte expectedByte = i < expected.length ? expected[i] : 0;
            byte actualByte = i < actual.length ? actual[i] : 0;
            diff |= expectedByte ^ actualByte;
        }
        return diff == 0;
    }
}
