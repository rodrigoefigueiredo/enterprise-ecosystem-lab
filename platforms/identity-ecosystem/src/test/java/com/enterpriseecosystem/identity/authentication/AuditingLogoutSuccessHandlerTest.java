package com.enterpriseecosystem.identity.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuditingLogoutSuccessHandlerTest {

    @Test
    public void logoutRecordsSessionAndLogoutEventsAndRedirectsToLogin() throws Exception {
        AuthenticationAuditRecorder auditRecorder = mock(AuthenticationAuditRecorder.class);
        AuditingLogoutSuccessHandler handler = new AuditingLogoutSuccessHandler(auditRecorder);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Authentication authentication = new UsernamePasswordAuthenticationToken("user@example.com", "ignored");
        when(request.getContextPath()).thenReturn("/identity-ecosystem");

        handler.onLogoutSuccess(request, response, authentication);

        verify(auditRecorder).record("SESSION_REVOKED", authentication, "SUCCESS");
        verify(auditRecorder).record("LOGOUT_SUCCEEDED", authentication, "SUCCESS");
        verify(response).sendRedirect("/identity-ecosystem/login?logout=true");
    }
}
