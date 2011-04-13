package com.enterpriseecosystem.identity.identity;

public class CreateUserRequest {

    private final String email;
    private final String displayName;
    private final String password;

    public CreateUserRequest(String email, String displayName, String password) {
        this.email = email;
        this.displayName = displayName;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPassword() {
        return password;
    }
}
