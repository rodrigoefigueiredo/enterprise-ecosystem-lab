package com.enterpriseecosystem.identity.identity;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListUsersServiceTest {

    @Test
    public void listsUsersFromDao() {
        UserDao userDao = mock(UserDao.class);
        User user = new User();
        when(userDao.findAllOrderByCreatedAtDesc()).thenReturn(Arrays.asList(user));
        ListUsersService service = new ListUsersService(userDao);

        List<User> users = service.listUsers();

        assertThat(users.size(), is(1));
        assertThat(users.get(0), is(user));
    }
}
