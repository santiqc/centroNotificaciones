package com._tcapital.centronotificaciones.application.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AttributesDto {
    private String status;
    private long statusCode;
    private String statusText;
    private MessageDto[] messageDtos;
    private String trackingID;

    @JsonProperty("status")
    public String getStatus() { return status; }
    @JsonProperty("status")
    public void setStatus(String value) { this.status = value; }

    @JsonProperty("statusCode")
    public long getStatusCode() { return statusCode; }
    @JsonProperty("statusCode")
    public void setStatusCode(long value) { this.statusCode = value; }

    @JsonProperty("statusText")
    public String getStatusText() { return statusText; }
    @JsonProperty("statusText")
    public void setStatusText(String value) { this.statusText = value; }

    @JsonProperty("messages")
    public MessageDto[] getMessages() { return messageDtos; }
    @JsonProperty("messages")
    public void setMessages(MessageDto[] value) { this.messageDtos = value; }

    @JsonProperty("trackingId")
    public String getTrackingID() { return trackingID; }
    @JsonProperty("trackingId")
    public void setTrackingID(String value) { this.trackingID = value; }
}
