package com.trace360.dto.response;

public class OtpResponse {
    private boolean success;
    private String message;
    private int remainingAttempts;
    private int attemptsUsed;
    private int attemptsRemaining;
    private boolean limitReached;

    public OtpResponse() {}

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public int getRemainingAttempts() { return remainingAttempts; }
    public int getAttemptsUsed() { return attemptsUsed; }
    public int getAttemptsRemaining() { return attemptsRemaining; }
    public boolean isLimitReached() { return limitReached; }

    public void setSuccess(boolean v) { this.success = v; }
    public void setMessage(String v) { this.message = v; }
    public void setRemainingAttempts(int v) { this.remainingAttempts = v; }
    public void setAttemptsUsed(int v) { this.attemptsUsed = v; }
    public void setAttemptsRemaining(int v) { this.attemptsRemaining = v; }
    public void setLimitReached(boolean v) { this.limitReached = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final OtpResponse r = new OtpResponse();
        public Builder success(boolean v) { r.success = v; return this; }
        public Builder message(String v) { r.message = v; return this; }
        public Builder remainingAttempts(int v) { r.remainingAttempts = v; return this; }
        public Builder attemptsUsed(int v) { r.attemptsUsed = v; return this; }
        public Builder attemptsRemaining(int v) { r.attemptsRemaining = v; return this; }
        public Builder limitReached(boolean v) { r.limitReached = v; return this; }
        public OtpResponse build() { return r; }
    }
}
