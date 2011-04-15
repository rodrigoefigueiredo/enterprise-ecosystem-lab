package com.enterpriseecosystem.identity.authentication;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.enterpriseecosystem.identity.audit.AuditEvent;
import com.enterpriseecosystem.identity.audit.AuditEventDao;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AuthenticationAuditServiceTest {

    @Test
    public void recordPersistsAuditEventWithoutSessionIdentifier() {
        AuditEventDao auditEventDao = mock(AuditEventDao.class);
        AuthenticationAuditService auditService = new AuthenticationAuditService(auditEventDao);

        auditService.record(
                "AUTHENTICATION_SUCCEEDED",
                new UsernamePasswordAuthenticationToken("first.user@example.com", "ignored"),
                "SUCCESS");

        ArgumentCaptor<AuditEvent> eventCaptor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventDao).save(eventCaptor.capture());

        AuditEvent event = eventCaptor.getValue();
        assertThat(event.getEventType(), is("AUTHENTICATION_SUCCEEDED"));
        assertThat(event.getSubjectType(), is("USER"));
        assertThat(event.getSubjectId(), is("first.user@example.com"));
        assertThat(event.getOutcome(), is("SUCCESS"));
        assertThat(event.getOccurredAt(), is(notNullValue()));
    }
}
