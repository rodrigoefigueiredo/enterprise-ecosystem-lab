package com.enterpriseecosystem.identity.credential;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CredentialManagementRequestTest {

    @Test
    public void holdsSelfServicePasswordChangeData() {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "user-public-id",
                "current-password",
                "new-password");

        assertThat(request.getUserPublicId(), is("user-public-id"));
        assertThat(request.getCurrentPassword(), is("current-password"));
        assertThat(request.getNewPassword(), is("new-password"));
    }

    @Test
    public void holdsAdministrativeResetData() {
        ResetPasswordRequest request = new ResetPasswordRequest(
                "user-public-id",
                "new-password");

        assertThat(request.getUserPublicId(), is("user-public-id"));
        assertThat(request.getNewPassword(), is("new-password"));
    }
}
