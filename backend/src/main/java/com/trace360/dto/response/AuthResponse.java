package com.trace360.dto.response;

import com.trace360.entity.User.Role;

public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String email;
    private String fullName;
    private Role role;
    private boolean emailVerified;
    private String message;

    public AuthResponse() {}

    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public Role getRole() { return role; }
    public boolean isEmailVerified() { return emailVerified; }
    public String getMessage() { return message; }

    public void setToken(String v) { this.token = v; }
    public void setTokenType(String v) { this.tokenType = v; }
    public void setUserId(Long v) { this.userId = v; }
    public void setEmail(String v) { this.email = v; }
    public void setFullName(String v) { this.fullName = v; }
    public void setRole(Role v) { this.role = v; }
    public void setEmailVerified(boolean v) { this.emailVerified = v; }
    public void setMessage(String v) { this.message = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final AuthResponse r = new AuthResponse();
        public Builder token(String v) { r.token = v; return this; }
        public Builder tokenType(String v) { r.tokenType = v; return this; }
        public Builder userId(Long v) { r.userId = v; return this; }
        public Builder email(String v) { r.email = v; return this; }
        public Builder fullName(String v) { r.fullName = v; return this; }
        public Builder role(Role v) { r.role = v; return this; }
        public Builder emailVerified(boolean v) { r.emailVerified = v; return this; }
        public Builder message(String v) { r.message = v; return this; }
        public AuthResponse build() { return r; }
    }
}
