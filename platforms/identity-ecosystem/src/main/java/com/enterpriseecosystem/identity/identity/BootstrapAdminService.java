package com.enterpriseecosystem.identity.identity;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.enterpriseecosystem.identity.audit.AuditEvent;
import com.enterpriseecosystem.identity.audit.AuditEventDao;
import com.enterpriseecosystem.identity.credential.PasswordCredential;
import com.enterpriseecosystem.identity.credential.PasswordCredentialDao;
import com.enterpriseecosystem.identity.credential.PasswordHasher;

@Service
public class BootstrapAdminService implements InitializingBean {

    private static final int MINIMUM_BOOTSTRAP_PASSWORD_LENGTH = 12;

    private final BootstrapAdminSettings settings;
    private final UserDao userDao;
    private final PasswordCredentialDao passwordCredentialDao;
    private final UserAuthorityDao userAuthorityDao;
    private final AuditEventDao auditEventDao;
    private final PasswordHasher passwordHasher;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public BootstrapAdminService(BootstrapAdminSettings settings,
                                 UserDao userDao,
                                 PasswordCredentialDao passwordCredentialDao,
                                 UserAuthorityDao userAuthorityDao,
                                 AuditEventDao auditEventDao,
                                 PasswordHasher passwordHasher,
                                 PlatformTransactionManager transactionManager) {
        this.settings = settings;
        this.userDao = userDao;
        this.passwordCredentialDao = passwordCredentialDao;
        this.userAuthorityDao = userAuthorityDao;
        this.auditEventDao = auditEventDao;
        this.passwordHasher = passwordHasher;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public void afterPropertiesSet() {
        bootstrapIfConfigured();
    }

    public void bootstrapIfConfigured() {
        final String email = value(settings.email());
        final String password = value(settings.password());
        final String displayName = value(settings.displayName());

        if (email == null && password == null && displayName == null) {
            return;
        }
        if (email == null || password == null || displayName == null) {
            throw new IllegalStateException("Bootstrap admin requires e-mail, password, and display name.");
        }
        if (password.length() < MINIMUM_BOOTSTRAP_PASSWORD_LENGTH) {
            throw new IllegalStateException("Bootstrap admin password must contain at least 12 characters.");
        }

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                bootstrap(email, password, displayName);
            }
        });
    }

    private void bootstrap(String email, String password, String displayName) {
        if (userAuthorityDao.authorityExists("ROLE_ADMIN")) {
            return;
        }

        String normalizedEmail = email.toLowerCase(Locale.US);
        if (userDao.emailExists(normalizedEmail)) {
            throw new IllegalStateException("Bootstrap admin e-mail already exists without an admin authority.");
        }

        Date now = new Date();
        User user = new User();
        user.setPublicId(UUID.randomUUID().toString());
        user.setEmail(normalizedEmail);
        user.setDisplayName(displayName);
        user.setStatus("ACTIVE");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userDao.save(user);

        PasswordCredential credential = new PasswordCredential();
        credential.setUser(user);
        credential.setPasswordHash(passwordHasher.hash(password));
        credential.setHashAlgorithm(passwordHasher.algorithm());
        credential.setCreatedAt(now);
        credential.setActive(true);
        passwordCredentialDao.save(credential);

        userAuthorityDao.grant(user, "ROLE_USER");
        userAuthorityDao.grant(user, "ROLE_ADMIN");

        AuditEvent event = new AuditEvent();
        event.setEventType("ADMIN_BOOTSTRAPPED");
        event.setSubjectType("USER");
        event.setSubjectId(user.getPublicId());
        event.setOccurredAt(now);
        event.setOutcome("SUCCESS");
        auditEventDao.save(event);
    }

    private String value(String candidate) {
        if (candidate == null) {
            return null;
        }
        String trimmed = candidate.trim();
        if (trimmed.length() == 0) {
            return null;
        }
        return trimmed;
    }
}
