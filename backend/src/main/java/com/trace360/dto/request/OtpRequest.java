package com.trace360.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class OtpRequest {
    @NotBlank @Email
    private String email;

    public OtpRequest() {}
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
