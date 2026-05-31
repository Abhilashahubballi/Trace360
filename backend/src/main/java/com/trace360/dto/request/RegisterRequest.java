package com.trace360.dto.request;

import com.trace360.entity.User.Role;
import jakarta.validation.constraints.*;

public class RegisterRequest {
    @NotBlank private String fullName;
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 6) private String password;
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phone;
    private String otp;
    private Role role;

    public RegisterRequest() {}
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }
    public String getOtp() { return otp; }
    public Role getRole() { return role; }
    public void setFullName(String v) { this.fullName = v; }
    public void setEmail(String v) { this.email = v; }
    public void setPassword(String v) { this.password = v; }
    public void setPhone(String v) { this.phone = v; }
    public void setOtp(String v) { this.otp = v; }
    public void setRole(Role v) { this.role = v; }
}
