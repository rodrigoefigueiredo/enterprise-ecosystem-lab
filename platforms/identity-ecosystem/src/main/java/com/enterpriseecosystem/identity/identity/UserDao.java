package com.enterpriseecosystem.identity.identity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(User user) {
        entityManager.persist(user);
    }

    public boolean emailExists(String email) {
        Long count = (Long) entityManager
                .createQuery("select count(u.id) from User u where u.email = :email")
                .setParameter("email", email)
                .getSingleResult();
        return count.longValue() > 0L;
    }
}
