package com.enterpriseecosystem.identity.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component("auditingLogoutSuccessHandler")
public class AuditingLogoutSuccessHandler implements LogoutSuccessHandler {

    private final AuthenticationAuditRecorder auditRecorder;

    @Autowired
    public AuditingLogoutSuccessHandler(AuthenticationAuditRecorder auditRecorder) {
        this.auditRecorder = auditRecorder;
    }

    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        auditRecorder.record("SESSION_REVOKED", authentication, "SUCCESS");
        auditRecorder.record("LOGOUT_SUCCEEDED", authentication, "SUCCESS");
        response.sendRedirect(request.getContextPath() + "/login?logout=true");
    }
}
