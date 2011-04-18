package com.enterpriseecosystem.identity.authentication;

import org.springframework.security.core.Authentication;

public interface AuthenticationAuditRecorder {

    void record(String eventType, Authentication authentication, String outcome);
}
