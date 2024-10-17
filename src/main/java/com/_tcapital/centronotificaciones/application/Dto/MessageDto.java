package com._tcapital.centronotificaciones.application.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageDto {
    private String message;
    private String messageID;

    @JsonProperty("Message")
    public String getMessage() { return message; }
    @JsonProperty("Message")
    public void setMessage(String value) { this.message = value; }

    @JsonProperty("MessageId")
    public String getMessageID() { return messageID; }
    @JsonProperty("MessageId")
    public void setMessageID(String value) { this.messageID = value; }
}
