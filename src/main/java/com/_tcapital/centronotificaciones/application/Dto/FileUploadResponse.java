package com._tcapital.centronotificaciones.application.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResponse {
    private FileData data;

    @Data
    @Builder
    public static class FileData {
        private String type;
        private String fileName;
        private Attributes attributes;
    }

    @Data
    @Builder
    public static class Attributes {
        private String response;
    }
}