package com.enterpriseecosystem.identity.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationAuditListener implements ApplicationListener<AbstractAuthenticationEvent> {

    private final AuthenticationAuditRecorder auditRecorder;

    @Autowired
    public AuthenticationAuditListener(AuthenticationAuditRecorder auditRecorder) {
        this.auditRecorder = auditRecorder;
    }

    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationSuccessEvent) {
            auditRecorder.record("AUTHENTICATION_SUCCEEDED", event.getAuthentication(), "SUCCESS");
            auditRecorder.record("SESSION_CREATED", event.getAuthentication(), "SUCCESS");
        } else if (event instanceof AbstractAuthenticationFailureEvent) {
            auditRecorder.record("AUTHENTICATION_FAILED", event.getAuthentication(), "FAILURE");
        }
    }
}
