package com.enterpriseecosystem.identity.platform.web;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LoginControllerTest {

    @Test
    public void loginReturnsLoginView() {
        LoginController controller = new LoginController();

        assertThat(controller.login(), is("login"));
    }
}
