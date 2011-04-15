package com.enterpriseecosystem.identity.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationAuditListener implements ApplicationListener<AbstractAuthenticationEvent> {

    private final AuthenticationAuditService auditService;

    @Autowired
    public AuthenticationAuditListener(AuthenticationAuditService auditService) {
        this.auditService = auditService;
    }

    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationSuccessEvent) {
            auditService.record("AUTHENTICATION_SUCCEEDED", event.getAuthentication(), "SUCCESS");
        } else if (event instanceof AbstractAuthenticationFailureEvent) {
            auditService.record("AUTHENTICATION_FAILED", event.getAuthentication(), "FAILURE");
        }
    }
}
