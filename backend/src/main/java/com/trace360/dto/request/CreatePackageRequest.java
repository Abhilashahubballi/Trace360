package com.trace360.dto.request;

import jakarta.validation.constraints.*;

public class CreatePackageRequest {
    @NotBlank private String senderName;
    @NotBlank private String senderCity;
    @NotBlank private String receiverName;
    @NotBlank @Email private String receiverEmail;
    @NotBlank private String destination;
    @NotNull @Positive private Double weightKg;
    private String packageType = "Parcel";
    private String priority = "Standard";
    private Long agentId;
    private boolean sendEmailToReceiver = true;

    public CreatePackageRequest() {}
    public String getSenderName() { return senderName; }
    public String getSenderCity() { return senderCity; }
    public String getReceiverName() { return receiverName; }
    public String getReceiverEmail() { return receiverEmail; }
    public String getDestination() { return destination; }
    public Double getWeightKg() { return weightKg; }
    public String getPackageType() { return packageType; }
    public String getPriority() { return priority; }
    public Long getAgentId() { return agentId; }
    public boolean isSendEmailToReceiver() { return sendEmailToReceiver; }
    public void setSenderName(String v) { this.senderName = v; }
    public void setSenderCity(String v) { this.senderCity = v; }
    public void setReceiverName(String v) { this.receiverName = v; }
    public void setReceiverEmail(String v) { this.receiverEmail = v; }
    public void setDestination(String v) { this.destination = v; }
    public void setWeightKg(Double v) { this.weightKg = v; }
    public void setPackageType(String v) { this.packageType = v; }
    public void setPriority(String v) { this.priority = v; }
    public void setAgentId(Long v) { this.agentId = v; }
    public void setSendEmailToReceiver(boolean v) { this.sendEmailToReceiver = v; }
}
