package com.enterpriseecosystem.identity.audit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

@Repository
public class AuditEventDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(AuditEvent event) {
        entityManager.persist(event);
    }
}
