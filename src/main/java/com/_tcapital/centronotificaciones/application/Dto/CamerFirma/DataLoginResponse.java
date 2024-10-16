package com._tcapital.centronotificaciones.application.Dto.CamerFirma;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataLoginResponse {
    private String type;
    private AttributesLoginResponse attributes;

    @JsonProperty("type")
    public String getType() { return type; }
    @JsonProperty("type")
    public void setType(String value) { this.type = value; }

    @JsonProperty("attributes")
    public AttributesLoginResponse getAttributes() { return attributes; }
    @JsonProperty("attributes")
    public void setAttributes(AttributesLoginResponse value) { this.attributes = value; }
}
