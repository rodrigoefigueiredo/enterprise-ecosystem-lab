package com.enterpriseecosystem.identity.credential;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.enterpriseecosystem.identity.identity.User;
import com.enterpriseecosystem.identity.identity.UserDao;
import com.enterpriseecosystem.identity.identity.UserNotFoundException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CredentialManagementServiceTest {

    @Test
    public void changesPasswordAndPreservesPreviousCredentialAsInactive() {
        User user = user();
        PasswordCredential currentCredential = credential(user, "old-hash");
        UserDao userDao = userDao(user);
        PasswordCredentialDao credentialDao = credentialDao(currentCredential);
        PasswordHasher passwordHasher = mock(PasswordHasher.class);
        when(passwordHasher.matches("old-password", "old-hash")).thenReturn(true);
        when(passwordHasher.hash("new-password")).thenReturn("new-hash");
        when(passwordHasher.algorithm()).thenReturn("PBKDF2WithHmacSHA1");

        CredentialManagementService service = newService(
                userDao, credentialDao, passwordHasher);

        service.changePassword(new ChangePasswordRequest(
                "user-public-id", "old-password", "new-password"));

        assertThat(currentCredential.isActive(), is(false));
        ArgumentCaptor<PasswordCredential> captor = ArgumentCaptor.forClass(PasswordCredential.class);
        verify(credentialDao).save(captor.capture());
        assertThat(captor.getValue().getUser(), is(user));
        assertThat(captor.getValue().getPasswordHash(), is("new-hash"));
        assertThat(captor.getValue().getHashAlgorithm(), is("PBKDF2WithHmacSHA1"));
        assertThat(captor.getValue().isActive(), is(true));
        assertThat(captor.getValue().getCreatedAt(), is(notNullValue()));
    }

    @Test(expected = InvalidCurrentPasswordException.class)
    public void rejectsChangeWhenCurrentPasswordDoesNotMatch() {
        User user = user();
        PasswordCredential currentCredential = credential(user, "old-hash");
        PasswordHasher passwordHasher = mock(PasswordHasher.class);
        when(passwordHasher.matches("wrong-password", "old-hash")).thenReturn(false);
        PasswordCredentialDao credentialDao = credentialDao(currentCredential);

        CredentialManagementService service = newService(
                userDao(user), credentialDao, passwordHasher);

        service.changePassword(new ChangePasswordRequest(
                "user-public-id", "wrong-password", "new-password"));

        verify(credentialDao, never()).save(org.mockito.Matchers.any(PasswordCredential.class));
    }

    @Test(expected = InvalidPasswordException.class)
    public void rejectsChangeWhenNewPasswordViolatesPolicy() {
        User user = user();
        PasswordCredential currentCredential = credential(user, "old-hash");
        PasswordHasher passwordHasher = mock(PasswordHasher.class);
        when(passwordHasher.matches("old-password", "old-hash")).thenReturn(true);

        CredentialManagementService service = newService(
                userDao(user), credentialDao(currentCredential), passwordHasher);

        service.changePassword(new ChangePasswordRequest(
                "user-public-id", "old-password", "short"));
    }

    @Test
    public void resetsPasswordWithoutCheckingCurrentPassword() {
        User user = user();
        PasswordCredential currentCredential = credential(user, "old-hash");
        PasswordHasher passwordHasher = mock(PasswordHasher.class);
        when(passwordHasher.hash("new-password")).thenReturn("new-hash");
        when(passwordHasher.algorithm()).thenReturn("PBKDF2WithHmacSHA1");
        PasswordCredentialDao credentialDao = credentialDao(currentCredential);

        CredentialManagementService service = newService(
                userDao(user), credentialDao, passwordHasher);

        service.resetPassword(new ResetPasswordRequest("user-public-id", "new-password"));

        assertThat(currentCredential.isActive(), is(false));
        verify(passwordHasher, never()).matches(
                org.mockito.Matchers.anyString(), org.mockito.Matchers.anyString());
        verify(credentialDao).save(org.mockito.Matchers.any(PasswordCredential.class));
    }

    @Test(expected = UserNotFoundException.class)
    public void rejectsUnknownUser() {
        CredentialManagementService service = newService(
                mock(UserDao.class),
                mock(PasswordCredentialDao.class),
                mock(PasswordHasher.class));

        service.resetPassword(new ResetPasswordRequest("missing-user", "new-password"));
    }

    private CredentialManagementService newService(UserDao userDao,
                                                   PasswordCredentialDao credentialDao,
                                                   PasswordHasher passwordHasher) {
        return new CredentialManagementService(
                userDao,
                credentialDao,
                passwordHasher,
                new PasswordPolicy());
    }

    private User user() {
        User user = new User();
        user.setPublicId("user-public-id");
        return user;
    }

    private UserDao userDao(User user) {
        UserDao userDao = mock(UserDao.class);
        when(userDao.findByPublicId("user-public-id")).thenReturn(user);
        return userDao;
    }

    private PasswordCredential credential(User user, String passwordHash) {
        PasswordCredential credential = new PasswordCredential();
        credential.setUser(user);
        credential.setPasswordHash(passwordHash);
        credential.setActive(true);
        return credential;
    }

    private PasswordCredentialDao credentialDao(PasswordCredential credential) {
        PasswordCredentialDao credentialDao = mock(PasswordCredentialDao.class);
        when(credentialDao.findActiveByUserId(null)).thenReturn(credential);
        return credentialDao;
    }
}
