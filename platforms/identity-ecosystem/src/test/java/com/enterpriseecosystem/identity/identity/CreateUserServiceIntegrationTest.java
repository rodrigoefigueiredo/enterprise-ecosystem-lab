package com.enterpriseecosystem.identity.identity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/applicationContext.xml")
@Transactional
public class CreateUserServiceIntegrationTest {

    @Autowired
    private CreateUserUseCase createUserUseCase;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void cleanDatabase() {
        entityManager.createNativeQuery("delete from audit_events").executeUpdate();
        entityManager.createNativeQuery("delete from user_authorities").executeUpdate();
        entityManager.createNativeQuery("delete from password_credentials").executeUpdate();
        entityManager.createNativeQuery("delete from users").executeUpdate();
        entityManager.flush();
    }

    @Test
    public void createsUserCredentialAuthorityAndAuditEvent() {
        User user = createUserUseCase.create(new CreateUserRequest(
                " Alice@Example.com ",
                "Alice",
                "changeit123"));
        entityManager.flush();

        assertThat(user.getId() != null, is(true));
        assertThat(user.getEmail(), is("alice@example.com"));

        Number users = count("users");
        Number credentials = count("password_credentials");
        Number authorities = count("user_authorities");
        Number auditEvents = count("audit_events");

        assertThat(users.longValue(), is(1L));
        assertThat(credentials.longValue(), is(1L));
        assertThat(authorities.longValue(), is(1L));
        assertThat(auditEvents.longValue(), is(1L));
        assertThat(authority(user.getId()), is("ROLE_USER"));
        assertThat(auditEvent(), is("USER_CREATED"));
    }

    private Number count(String tableName) {
        return (Number) entityManager
                .createNativeQuery("select count(*) from " + tableName)
                .getSingleResult();
    }

    private String authority(Long userId) {
        return (String) entityManager
                .createNativeQuery("select authority from user_authorities where user_id = :userId")
                .setParameter("userId", userId)
                .getSingleResult();
    }

    private String auditEvent() {
        return (String) entityManager
                .createNativeQuery("select event_type from audit_events")
                .getSingleResult();
    }
}
