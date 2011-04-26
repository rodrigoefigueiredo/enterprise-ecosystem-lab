package com.enterpriseecosystem.identity.platform.web;

import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class CredentialControllerTest {

    @Test
    public void changePasswordReturnsFormView() {
        CredentialController controller = new CredentialController();
        ExtendedModelMap model = new ExtendedModelMap();

        String viewName = controller.changePassword(model);

        assertThat(viewName, is("me/password"));
        assertThat(model.containsAttribute("changePasswordForm"), is(true));
        assertThat(model.get("changePasswordForm"), instanceOf(ChangePasswordForm.class));
    }
}
