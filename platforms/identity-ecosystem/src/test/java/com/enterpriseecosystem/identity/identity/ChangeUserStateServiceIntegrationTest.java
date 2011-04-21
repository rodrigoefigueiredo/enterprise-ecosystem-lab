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
public class ChangeUserStateServiceIntegrationTest {

    @Autowired
    private CreateUserUseCase createUserUseCase;

    @Autowired
    private ChangeUserStateUseCase changeUserStateUseCase;

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
    public void persistsUserStateChangeAndAuditEvent() {
        User user = createUserUseCase.create(new CreateUserRequest(
                "alice@example.com",
                "Alice",
                "changeit123"));

        changeUserStateUseCase.changeState(new ChangeUserStateRequest(user.getPublicId(), "LOCKED"));
        entityManager.flush();
        entityManager.clear();

        String status = (String) entityManager
                .createNativeQuery("select status from users where public_id = :publicId")
                .setParameter("publicId", user.getPublicId())
                .getSingleResult();
        Number stateChangeEvents = (Number) entityManager
                .createNativeQuery("select count(*) from audit_events where event_type = 'USER_STATE_CHANGED'")
                .getSingleResult();

        assertThat(status, is("LOCKED"));
        assertThat(stateChangeEvents.longValue(), is(1L));
    }
}
