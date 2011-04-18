package com.enterpriseecosystem.identity.authentication;

import org.junit.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AuthenticationAuditListenerTest {

    @Test
    public void authenticationSuccessRecordsAuthenticationAndSessionEvents() {
        AuthenticationAuditRecorder auditRecorder = mock(AuthenticationAuditRecorder.class);
        AuthenticationAuditListener listener = new AuthenticationAuditListener(auditRecorder);
        Authentication authentication = new UsernamePasswordAuthenticationToken("user@example.com", "ignored");

        listener.onApplicationEvent(new AuthenticationSuccessEvent(authentication));

        verify(auditRecorder).record("AUTHENTICATION_SUCCEEDED", authentication, "SUCCESS");
        verify(auditRecorder).record("SESSION_CREATED", authentication, "SUCCESS");
    }

    @Test
    public void authenticationFailureRecordsCanonicalEventName() {
        AuthenticationAuditRecorder auditRecorder = mock(AuthenticationAuditRecorder.class);
        AuthenticationAuditListener listener = new AuthenticationAuditListener(auditRecorder);
        Authentication authentication = new UsernamePasswordAuthenticationToken("user@example.com", "ignored");

        listener.onApplicationEvent(new AuthenticationFailureBadCredentialsEvent(
                authentication,
                new BadCredentialsException("Bad credentials")));

        verify(auditRecorder).record("AUTHENTICATION_FAILED", authentication, "FAILURE");
    }
}
