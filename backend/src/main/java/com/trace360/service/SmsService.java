package com.trace360.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.*;
import java.util.Base64;

@Service
public class SmsService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SmsService.class);

    @Value("${twilio.account.sid:}")
    private String accountSid;

    @Value("${twilio.auth.token:}")
    private String authToken;

    @Value("${twilio.phone.number:}")
    private String twilioPhone;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public void sendStatusSms(String toPhone, String receiverName,
                               String trackingId, String status) {
        if (accountSid == null || accountSid.isEmpty()) {
            log.info("Twilio not configured — SMS skipped for {}", toPhone);
            return;
        }
        String phone = formatPhone(toPhone);
        String message = buildSmsMessage(receiverName, trackingId, status);
        try {
            sendViaTwilio(phone, message);
            log.info("SMS sent to {} for package {}", phone, trackingId);
        } catch (Exception e) {
            log.error("SMS failed to {}: {}", phone, e.getMessage());
        }
    }

    @Async
    public void sendOtpSms(String toPhone, String otp) {
        if (accountSid == null || accountSid.isEmpty()) return;
        String message = "Your Trace360 OTP is: " + otp
                + "\nValid for 10 minutes. Do not share with anyone.";
        try {
            sendViaTwilio(formatPhone(toPhone), message);
        } catch (Exception e) {
            log.error("OTP SMS failed: {}", e.getMessage());
        }
    }

    @Async
    public void sendDeliveryOtpSms(String toPhone, String name, String otp) {
        if (accountSid == null || accountSid.isEmpty()) return;
        String message = "Hi " + name + "! Your Trace360 delivery OTP is: " + otp
                + "\nShare this with your delivery agent to confirm receipt. Valid 30 mins.";
        try {
            sendViaTwilio(formatPhone(toPhone), message);
        } catch (Exception e) {
            log.error("Delivery OTP SMS failed: {}", e.getMessage());
        }
    }

    private String buildSmsMessage(String name, String trackingId, String status) {
        return switch (status) {
            case "PICKED_UP" -> "Hi " + name + "! Package " + trackingId + " picked up.";
            case "IN_TRANSIT" -> "Hi " + name + "! Package " + trackingId + " is in transit.";
            case "OUT_FOR_DELIVERY" -> "Hi " + name + "! Package " + trackingId + " is out for delivery.";
            case "DELIVERED" -> "Hi " + name + "! Package " + trackingId + " delivered!";
            case "FAILED" -> "Hi " + name + "! Delivery attempt for " + trackingId + " failed.";
            default -> "Hi " + name + "! Package " + trackingId + " status updated.";
        };
    }

    private void sendViaTwilio(String toPhone, String message) {
        String url = "https://api.twilio.com/2010-04-01/Accounts/" + accountSid + "/Messages.json";
        String credentials = accountSid + ":" + authToken;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + encodedCredentials);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("From", twilioPhone);
        params.add("To", toPhone);
        params.add("Body", message);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        restTemplate.postForObject(url, request, String.class);
    }

    private String formatPhone(String phone) {
        if (phone == null) return "";
        phone = phone.replaceAll("[^0-9]", "");
        if (phone.length() == 10) return "+91" + phone;
        if (phone.startsWith("91") && phone.length() == 12) return "+" + phone;
        return "+" + phone;
    }
}
