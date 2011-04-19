package com.enterpriseecosystem.identity.identity;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

@Repository
public class UserAuthorityDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void grant(User user, String authority) {
        entityManager.flush();
        entityManager
                .createNativeQuery("insert into user_authorities (user_id, authority) values (:userId, :authority)")
                .setParameter("userId", user.getId())
                .setParameter("authority", authority)
                .executeUpdate();
    }

    public List<String> findByUserId(Long userId) {
        return entityManager
                .createNativeQuery("select authority from user_authorities where user_id = :userId order by authority")
                .setParameter("userId", userId)
                .getResultList();
    }

    public boolean authorityExists(String authority) {
        Number count = (Number) entityManager
                .createNativeQuery("select count(*) from user_authorities where authority = :authority")
                .setParameter("authority", authority)
                .getSingleResult();
        return count.longValue() > 0L;
    }
}
