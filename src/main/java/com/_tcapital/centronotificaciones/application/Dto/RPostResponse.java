package com._tcapital.centronotificaciones.application.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RPostResponse {

    @JsonProperty("Status")
    private String status;

    @JsonProperty("StatusCode")
    private String statusCode;

    @JsonProperty("StatusText")
    private String statusText;

    @JsonProperty("Message")
    private MessageDto[] message;

    @JsonProperty("ResultContent")
    private ResultContent resultContent;

    @Data
    public static class ResultContent {

        @JsonProperty("TrackingId")
        private String trackingId;
    }
}
