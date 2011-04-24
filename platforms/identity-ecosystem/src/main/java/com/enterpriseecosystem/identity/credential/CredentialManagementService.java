package com.enterpriseecosystem.identity.credential;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enterpriseecosystem.identity.identity.User;
import com.enterpriseecosystem.identity.identity.UserDao;
import com.enterpriseecosystem.identity.identity.UserNotFoundException;

@Service
public class CredentialManagementService implements CredentialManagementUseCase {

    private final UserDao userDao;
    private final PasswordCredentialDao passwordCredentialDao;
    private final PasswordHasher passwordHasher;
    private final PasswordPolicy passwordPolicy;

    @Autowired
    public CredentialManagementService(UserDao userDao,
                                      PasswordCredentialDao passwordCredentialDao,
                                      PasswordHasher passwordHasher,
                                      PasswordPolicy passwordPolicy) {
        this.userDao = userDao;
        this.passwordCredentialDao = passwordCredentialDao;
        this.passwordHasher = passwordHasher;
        this.passwordPolicy = passwordPolicy;
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = findUser(request.getUserPublicId());
        PasswordCredential currentCredential = findActiveCredential(user);

        if (!passwordHasher.matches(request.getCurrentPassword(), currentCredential.getPasswordHash())) {
            throw new InvalidCurrentPasswordException();
        }

        validateNewPassword(request.getNewPassword());
        replaceCredential(user, currentCredential, request.getNewPassword());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = findUser(request.getUserPublicId());
        PasswordCredential currentCredential = findActiveCredential(user);

        validateNewPassword(request.getNewPassword());
        replaceCredential(user, currentCredential, request.getNewPassword());
    }

    private User findUser(String userPublicId) {
        User user = userDao.findByPublicId(userPublicId);
        if (user == null) {
            throw new UserNotFoundException(userPublicId);
        }
        return user;
    }

    private PasswordCredential findActiveCredential(User user) {
        PasswordCredential credential = passwordCredentialDao.findActiveByUserId(user.getId());
        if (credential == null) {
            throw new CredentialNotFoundException(user.getPublicId());
        }
        return credential;
    }

    private void validateNewPassword(String password) {
        if (!passwordPolicy.accepts(password)) {
            throw new InvalidPasswordException();
        }
    }

    private void replaceCredential(User user,
                                   PasswordCredential currentCredential,
                                   String newPassword) {
        Date now = new Date();
        PasswordCredential newCredential = new PasswordCredential();
        newCredential.setUser(user);
        newCredential.setPasswordHash(passwordHasher.hash(newPassword));
        newCredential.setHashAlgorithm(passwordHasher.algorithm());
        newCredential.setCreatedAt(now);
        newCredential.setActive(true);

        currentCredential.setActive(false);
        passwordCredentialDao.save(newCredential);
    }
}
