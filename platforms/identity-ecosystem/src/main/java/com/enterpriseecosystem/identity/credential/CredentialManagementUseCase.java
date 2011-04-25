package com.enterpriseecosystem.identity.credential;

public interface CredentialManagementUseCase {

    void changePassword(ChangePasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
