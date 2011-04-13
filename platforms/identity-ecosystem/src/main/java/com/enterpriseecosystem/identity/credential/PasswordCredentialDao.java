package com.enterpriseecosystem.identity.credential;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

@Repository
public class PasswordCredentialDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(PasswordCredential credential) {
        entityManager.persist(credential);
    }
}
