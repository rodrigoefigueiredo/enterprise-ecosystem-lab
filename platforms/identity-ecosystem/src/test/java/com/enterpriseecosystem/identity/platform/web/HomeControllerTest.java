package com.enterpriseecosystem.identity.platform.web;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HomeControllerTest {

    @Test
    public void homeReturnsHomeView() {
        HomeController controller = new HomeController();

        assertThat(controller.home(), is("home"));
    }
}
