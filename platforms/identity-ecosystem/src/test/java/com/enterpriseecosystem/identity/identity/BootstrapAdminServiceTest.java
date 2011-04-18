package com.enterpriseecosystem.identity.identity;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

import com.enterpriseecosystem.identity.audit.AuditEvent;
import com.enterpriseecosystem.identity.audit.AuditEventDao;
import com.enterpriseecosystem.identity.credential.PasswordCredentialDao;
import com.enterpriseecosystem.identity.credential.PasswordHasher;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BootstrapAdminServiceTest {

    @Test
    public void doesNothingWhenBootstrapSettingsAreAbsent() {
        UserAuthorityDao authorityDao = mock(UserAuthorityDao.class);
        BootstrapAdminService service = newService(settings(null, null, null), authorityDao);

        service.bootstrapIfConfigured();

        verify(authorityDao, never()).authorityExists("ROLE_ADMIN");
    }

    @Test
    public void doesNothingWhenAdminAlreadyExists() {
        UserAuthorityDao authorityDao = mock(UserAuthorityDao.class);
        when(authorityDao.authorityExists("ROLE_ADMIN")).thenReturn(true);
        BootstrapAdminService service = newService(
                settings("admin@example.com", "very-secret-password", "Admin"),
                authorityDao);

        service.bootstrapIfConfigured();

        verify(authorityDao).authorityExists("ROLE_ADMIN");
    }

    @Test
    public void createsInitialAdminWhenConfiguredAndNoAdminExists() {
        UserDao userDao = mock(UserDao.class);
        PasswordCredentialDao credentialDao = mock(PasswordCredentialDao.class);
        UserAuthorityDao authorityDao = mock(UserAuthorityDao.class);
        AuditEventDao auditEventDao = mock(AuditEventDao.class);
        PasswordHasher passwordHasher = mock(PasswordHasher.class);
        when(authorityDao.authorityExists("ROLE_ADMIN")).thenReturn(false);
        when(userDao.emailExists("admin@example.com")).thenReturn(false);
        when(passwordHasher.hash("very-secret-password")).thenReturn("hashed-password");
        when(passwordHasher.algorithm()).thenReturn("PBKDF2WithHmacSHA1");

        BootstrapAdminService service = new BootstrapAdminService(
                settings(" Admin@Example.com ", "very-secret-password", " Admin "),
                userDao,
                credentialDao,
                authorityDao,
                auditEventDao,
                passwordHasher,
                new TestTransactionManager());

        service.bootstrapIfConfigured();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao).save(userCaptor.capture());
        User user = userCaptor.getValue();
        assertThat(user.getEmail(), is("admin@example.com"));
        assertThat(user.getDisplayName(), is("Admin"));
        assertThat(user.getStatus(), is("ACTIVE"));
        verify(authorityDao).grant(user, "ROLE_USER");
        verify(authorityDao).grant(user, "ROLE_ADMIN");

        ArgumentCaptor<AuditEvent> eventCaptor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventDao).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getEventType(), is("ADMIN_BOOTSTRAPPED"));
    }

    @Test(expected = IllegalStateException.class)
    public void rejectsShortBootstrapPassword() {
        BootstrapAdminService service = newService(
                settings("admin@example.com", "short", "Admin"),
                mock(UserAuthorityDao.class));

        service.bootstrapIfConfigured();
    }

    private BootstrapAdminService newService(BootstrapAdminSettings settings, UserAuthorityDao authorityDao) {
        return new BootstrapAdminService(
                settings,
                mock(UserDao.class),
                mock(PasswordCredentialDao.class),
                authorityDao,
                mock(AuditEventDao.class),
                mock(PasswordHasher.class),
                new TestTransactionManager());
    }

    private BootstrapAdminSettings settings(final String email, final String password, final String displayName) {
        return new BootstrapAdminSettings() {
            public String email() {
                return email;
            }

            public String password() {
                return password;
            }

            public String displayName() {
                return displayName;
            }
        };
    }

    private static class TestTransactionManager extends AbstractPlatformTransactionManager {

        protected Object doGetTransaction() {
            return new Object();
        }

        protected void doBegin(Object transaction, TransactionDefinition definition) {
        }

        protected void doCommit(DefaultTransactionStatus status) {
        }

        protected void doRollback(DefaultTransactionStatus status) {
        }
    }
}
