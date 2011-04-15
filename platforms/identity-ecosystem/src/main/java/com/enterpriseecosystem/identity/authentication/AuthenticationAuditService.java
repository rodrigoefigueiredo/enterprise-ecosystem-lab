package com.enterpriseecosystem.identity.authentication;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enterpriseecosystem.identity.audit.AuditEvent;
import com.enterpriseecosystem.identity.audit.AuditEventDao;

@Service
public class AuthenticationAuditService {

    private final AuditEventDao auditEventDao;

    @Autowired
    public AuthenticationAuditService(AuditEventDao auditEventDao) {
        this.auditEventDao = auditEventDao;
    }

    @Transactional
    public void record(String eventType, Authentication authentication, String outcome) {
        AuditEvent event = new AuditEvent();
        event.setEventType(eventType);
        event.setSubjectType("USER");
        event.setSubjectId(subjectId(authentication));
        event.setOccurredAt(new Date());
        event.setOutcome(outcome);
        auditEventDao.save(event);
    }

    private String subjectId(Authentication authentication) {
        if (authentication == null) {
            return "UNKNOWN";
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof IdentityUserDetails) {
            return ((IdentityUserDetails) principal).getPublicId();
        }
        return String.valueOf(authentication.getName());
    }
}
