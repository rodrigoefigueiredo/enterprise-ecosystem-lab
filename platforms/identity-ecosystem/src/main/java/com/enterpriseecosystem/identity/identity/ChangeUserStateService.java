package com.enterpriseecosystem.identity.identity;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enterpriseecosystem.identity.audit.AuditEvent;
import com.enterpriseecosystem.identity.audit.AuditEventDao;

@Service
public class ChangeUserStateService implements ChangeUserStateUseCase {

    private final UserDao userDao;
    private final AuditEventDao auditEventDao;

    @Autowired
    public ChangeUserStateService(UserDao userDao, AuditEventDao auditEventDao) {
        this.userDao = userDao;
        this.auditEventDao = auditEventDao;
    }

    @Transactional
    public User changeState(ChangeUserStateRequest request) {
        String publicId = value(request.getPublicId());
        String status = value(request.getStatus());
        if (publicId == null || !isAllowedStatus(status)) {
            throw new InvalidUserStateChangeException();
        }

        User user = userDao.findByPublicId(publicId);
        if (user == null) {
            throw new UserNotFoundException(publicId);
        }

        Date now = new Date();
        user.setStatus(status);
        user.setUpdatedAt(now);

        AuditEvent event = new AuditEvent();
        event.setEventType("USER_STATE_CHANGED");
        event.setSubjectType("USER");
        event.setSubjectId(user.getPublicId());
        event.setOccurredAt(now);
        event.setOutcome("SUCCESS");
        auditEventDao.save(event);

        return user;
    }

    private boolean isAllowedStatus(String status) {
        return "ACTIVE".equals(status)
                || "SUSPENDED".equals(status)
                || "LOCKED".equals(status)
                || "INACTIVE".equals(status);
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
