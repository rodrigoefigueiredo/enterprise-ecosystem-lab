package com.enterpriseecosystem.identity.credential;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class Pbkdf2PasswordHasherTest {

    @Test
    public void hashesPasswordWithConfiguredAlgorithmAndSalt() {
        Pbkdf2PasswordHasher hasher = new Pbkdf2PasswordHasher();

        String firstHash = hasher.hash("changeit123");
        String secondHash = hasher.hash("changeit123");

        assertThat(hasher.algorithm(), is("PBKDF2WithHmacSHA1"));
        assertThat(firstHash, not("changeit123"));
        assertThat(firstHash, not(secondHash));
        assertThat(hasher.matches("changeit123", firstHash), is(true));
        assertThat(hasher.matches("wrong-password", firstHash), is(false));
    }
}
