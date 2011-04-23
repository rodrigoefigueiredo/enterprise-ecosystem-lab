package com.enterpriseecosystem.identity.credential;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PasswordPolicyTest {

    private final PasswordPolicy policy = new PasswordPolicy();

    @Test
    public void rejectsNullPassword() {
        assertThat(policy.accepts(null), is(false));
    }

    @Test
    public void rejectsPasswordShorterThanMinimum() {
        assertThat(policy.accepts("12345678901"), is(false));
    }

    @Test
    public void acceptsPasswordWithExactlyMinimumLength() {
        assertThat(policy.accepts("123456789012"), is(true));
    }

    @Test
    public void acceptsPasswordLongerThanMinimum() {
        assertThat(policy.accepts("1234567890123"), is(true));
    }
}
