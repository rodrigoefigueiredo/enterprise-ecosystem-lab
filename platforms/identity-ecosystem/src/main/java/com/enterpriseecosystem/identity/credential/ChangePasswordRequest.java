package com.enterpriseecosystem.identity.credential;

public class ChangePasswordRequest {

    private final String userPublicId;
    private final String currentPassword;
    private final String newPassword;

    public ChangePasswordRequest(String userPublicId, String currentPassword, String newPassword) {
        this.userPublicId = userPublicId;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getUserPublicId() {
        return userPublicId;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
