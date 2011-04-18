package com.enterpriseecosystem.identity.identity;

import org.springframework.stereotype.Component;

@Component
public class BootstrapAdminSettings {

    public String email() {
        return System.getenv("IDENTITY_BOOTSTRAP_ADMIN_EMAIL");
    }

    public String password() {
        return System.getenv("IDENTITY_BOOTSTRAP_ADMIN_PASSWORD");
    }

    public String displayName() {
        return System.getenv("IDENTITY_BOOTSTRAP_ADMIN_DISPLAY_NAME");
    }
}
