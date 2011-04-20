package com.enterpriseecosystem.identity.identity;

public class ChangeUserStateRequest {

    private final String publicId;
    private final String status;

    public ChangeUserStateRequest(String publicId, String status) {
        this.publicId = publicId;
        this.status = status;
    }

    public String getPublicId() {
        return publicId;
    }

    public String getStatus() {
        return status;
    }
}
