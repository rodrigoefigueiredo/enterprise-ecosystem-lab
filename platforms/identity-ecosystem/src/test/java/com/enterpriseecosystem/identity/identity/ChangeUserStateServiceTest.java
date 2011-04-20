package com.enterpriseecosystem.identity.identity;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.enterpriseecosystem.identity.audit.AuditEvent;
import com.enterpriseecosystem.identity.audit.AuditEventDao;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChangeUserStateServiceTest {

    @Test
    public void changesUserStateAndAuditsTheChange() {
        UserDao userDao = mock(UserDao.class);
        AuditEventDao auditEventDao = mock(AuditEventDao.class);
        User user = new User();
        user.setPublicId("public-id-1");
        user.setStatus("ACTIVE");
        when(userDao.findByPublicId("public-id-1")).thenReturn(user);

        ChangeUserStateService service = new ChangeUserStateService(userDao, auditEventDao);

        User changedUser = service.changeState(new ChangeUserStateRequest("public-id-1", "SUSPENDED"));

        assertThat(changedUser.getStatus(), is("SUSPENDED"));
        assertThat(changedUser.getUpdatedAt(), is(notNullValue()));

        ArgumentCaptor<AuditEvent> eventCaptor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventDao).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getEventType(), is("USER_STATE_CHANGED"));
        assertThat(eventCaptor.getValue().getSubjectType(), is("USER"));
        assertThat(eventCaptor.getValue().getSubjectId(), is("public-id-1"));
        assertThat(eventCaptor.getValue().getOutcome(), is("SUCCESS"));
    }

    @Test(expected = InvalidUserStateChangeException.class)
    public void rejectsInvalidState() {
        ChangeUserStateService service = new ChangeUserStateService(
                mock(UserDao.class),
                mock(AuditEventDao.class));

        service.changeState(new ChangeUserStateRequest("public-id-1", "DISABLED"));
    }

    @Test(expected = UserNotFoundException.class)
    public void rejectsUnknownUser() {
        UserDao userDao = mock(UserDao.class);
        when(userDao.findByPublicId("missing-id")).thenReturn(null);
        ChangeUserStateService service = new ChangeUserStateService(
                userDao,
                mock(AuditEventDao.class));

        service.changeState(new ChangeUserStateRequest("missing-id", "LOCKED"));
    }
}
