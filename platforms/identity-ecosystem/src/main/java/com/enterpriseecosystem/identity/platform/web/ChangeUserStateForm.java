package com.enterpriseecosystem.identity.platform.web;

public class ChangeUserStateForm {

    private String publicId;
    private String status;

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
