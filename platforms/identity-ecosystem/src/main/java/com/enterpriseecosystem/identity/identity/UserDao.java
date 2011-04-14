package com.enterpriseecosystem.identity.identity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.NoResultException;

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

    public User findByEmail(String email) {
        try {
            return (User) entityManager
                    .createQuery("select u from User u where u.email = :email")
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
