package com.enterpriseecosystem.identity.identity;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enterpriseecosystem.identity.audit.AuditEvent;
import com.enterpriseecosystem.identity.audit.AuditEventDao;
import com.enterpriseecosystem.identity.credential.PasswordCredential;
import com.enterpriseecosystem.identity.credential.PasswordCredentialDao;
import com.enterpriseecosystem.identity.credential.PasswordHasher;
import com.enterpriseecosystem.identity.credential.PasswordPolicy;
import com.enterpriseecosystem.identity.credential.InvalidPasswordException;

@Service
public class CreateUserService implements CreateUserUseCase {

    private final UserDao userDao;
    private final PasswordCredentialDao passwordCredentialDao;
    private final UserAuthorityDao userAuthorityDao;
    private final AuditEventDao auditEventDao;
    private final PasswordHasher passwordHasher;
    private final PasswordPolicy passwordPolicy;

    @Autowired
    public CreateUserService(UserDao userDao,
                             PasswordCredentialDao passwordCredentialDao,
                             UserAuthorityDao userAuthorityDao,
                             AuditEventDao auditEventDao,
                             PasswordHasher passwordHasher,
                             PasswordPolicy passwordPolicy) {
        this.userDao = userDao;
        this.passwordCredentialDao = passwordCredentialDao;
        this.userAuthorityDao = userAuthorityDao;
        this.auditEventDao = auditEventDao;
        this.passwordHasher = passwordHasher;
        this.passwordPolicy = passwordPolicy;
    }

    @Transactional
    public User create(CreateUserRequest request) {
        if (!passwordPolicy.accepts(request.getPassword())) {
            throw new InvalidPasswordException();
        }

        String email = normalizeEmail(request.getEmail());
        if (userDao.emailExists(email)) {
            throw new DuplicateEmailException(email);
        }

        Date now = new Date();
        User user = new User();
        user.setPublicId(UUID.randomUUID().toString());
        user.setEmail(email);
        user.setDisplayName(request.getDisplayName().trim());
        user.setStatus("ACTIVE");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userDao.save(user);

        PasswordCredential credential = new PasswordCredential();
        credential.setUser(user);
        credential.setPasswordHash(passwordHasher.hash(request.getPassword()));
        credential.setHashAlgorithm(passwordHasher.algorithm());
        credential.setCreatedAt(now);
        credential.setActive(true);
        passwordCredentialDao.save(credential);

        userAuthorityDao.grant(user, "ROLE_USER");

        AuditEvent event = new AuditEvent();
        event.setEventType("USER_CREATED");
        event.setSubjectType("USER");
        event.setSubjectId(user.getPublicId());
        event.setOccurredAt(now);
        event.setOutcome("SUCCESS");
        auditEventDao.save(event);

        return user;
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.US);
    }
}
