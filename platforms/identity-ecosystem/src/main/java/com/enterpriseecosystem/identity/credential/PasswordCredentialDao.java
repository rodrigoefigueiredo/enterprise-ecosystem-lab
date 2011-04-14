package com.enterpriseecosystem.identity.credential;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

@Repository
public class PasswordCredentialDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(PasswordCredential credential) {
        entityManager.persist(credential);
    }

    public PasswordCredential findActiveByUserId(Long userId) {
        try {
            return (PasswordCredential) entityManager
                    .createQuery("select c from PasswordCredential c where c.user.id = :userId and c.active = true")
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
