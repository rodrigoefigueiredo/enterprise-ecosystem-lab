package com.enterpriseecosystem.identity.identity;

import org.junit.Test;

import com.enterpriseecosystem.identity.audit.AuditEventDao;
import com.enterpriseecosystem.identity.credential.PasswordCredentialDao;
import com.enterpriseecosystem.identity.credential.PasswordHasher;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateUserServiceTest {

    @Test
    public void createsActiveUserWithNormalizedEmail() {
        UserDao userDao = mock(UserDao.class);
        PasswordCredentialDao credentialDao = mock(PasswordCredentialDao.class);
        AuditEventDao auditEventDao = mock(AuditEventDao.class);
        PasswordHasher passwordHasher = mock(PasswordHasher.class);
        when(userDao.emailExists("alice@example.com")).thenReturn(false);
        when(passwordHasher.hash("changeit123")).thenReturn("hashed-password");
        when(passwordHasher.algorithm()).thenReturn("PBKDF2WithHmacSHA1");

        CreateUserService service = new CreateUserService(userDao, credentialDao, auditEventDao, passwordHasher);

        User user = service.create(new CreateUserRequest(" Alice@Example.com ", "Alice", "changeit123"));

        assertThat(user.getEmail(), is("alice@example.com"));
        assertThat(user.getDisplayName(), is("Alice"));
        assertThat(user.getStatus(), is("ACTIVE"));
        verify(userDao).save(user);
    }

    @Test(expected = DuplicateEmailException.class)
    public void rejectsDuplicateEmail() {
        UserDao userDao = mock(UserDao.class);
        when(userDao.emailExists("alice@example.com")).thenReturn(true);

        CreateUserService service = new CreateUserService(
                userDao,
                mock(PasswordCredentialDao.class),
                mock(AuditEventDao.class),
                mock(PasswordHasher.class));

        service.create(new CreateUserRequest("alice@example.com", "Alice", "changeit123"));
    }
}
