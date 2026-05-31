package com.trace360.exception;
public class OtpLimitExceededException extends RuntimeException {
    public OtpLimitExceededException(String message) { super(message); }
}
