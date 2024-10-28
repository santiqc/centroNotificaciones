package com._tcapital.centronotificaciones.application.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageStatusResponse {
    private MessageStatus messageStatus;
    private ReceiptFile receiptFile;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageStatusRequest {
        private String trackingId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageStatus {
        private String status;
        private int statusCode;
        private String statusText;
        private List<Message> message;
        private List<ResultContent> resultContent;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String message;
        private String messageId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultContent {
        private String trackingId;
        private String customerTrackingId;
        private String senderName;
        private String senderAddress;
        private String date;
        private String status;
        private List<Recipient> recipients;
        private String redactedTextViewDetail;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Recipient {
        private String address;
        private String recipientType;
        private String deliveredDate;
        private String openedDate;
        private String deliveryStatus;
        private String deliveryDetail;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceiptFile {
        private String content;
        private String mediaType;
    }

}