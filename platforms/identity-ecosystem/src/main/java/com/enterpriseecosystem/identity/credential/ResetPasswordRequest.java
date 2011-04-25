package com.enterpriseecosystem.identity.credential;

public class ResetPasswordRequest {

    private final String userPublicId;
    private final String newPassword;

    public ResetPasswordRequest(String userPublicId, String newPassword) {
        this.userPublicId = userPublicId;
        this.newPassword = newPassword;
    }

    public String getUserPublicId() {
        return userPublicId;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
