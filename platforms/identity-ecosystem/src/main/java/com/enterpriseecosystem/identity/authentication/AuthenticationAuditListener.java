package com.enterpriseecosystem.identity.authentication;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.enterpriseecosystem.identity.audit.AuditEvent;
import com.enterpriseecosystem.identity.audit.AuditEventDao;

@Component
public class AuthenticationAuditListener implements ApplicationListener<AbstractAuthenticationEvent> {

    private final AuditEventDao auditEventDao;

    @Autowired
    public AuthenticationAuditListener(AuditEventDao auditEventDao) {
        this.auditEventDao = auditEventDao;
    }

    @Transactional
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationSuccessEvent) {
            record("LOGIN_SUCCEEDED", event.getAuthentication(), "SUCCESS");
        } else if (event instanceof AbstractAuthenticationFailureEvent) {
            record("LOGIN_FAILED", event.getAuthentication(), "FAILURE");
        }
    }

    private void record(String eventType, Authentication authentication, String outcome) {
        AuditEvent event = new AuditEvent();
        event.setEventType(eventType);
        event.setSubjectType("USER");
        event.setSubjectId(subjectId(authentication));
        event.setOccurredAt(new Date());
        event.setOutcome(outcome);
        auditEventDao.save(event);
    }

    private String subjectId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof IdentityUserDetails) {
            return ((IdentityUserDetails) principal).getPublicId();
        }
        return String.valueOf(authentication.getName());
    }
}
