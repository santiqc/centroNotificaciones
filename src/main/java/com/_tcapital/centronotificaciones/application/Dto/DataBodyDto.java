package com._tcapital.centronotificaciones.application.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DataBodyDto {
    private String type;
    private String id;
    private AttributesDto attributes;

    @JsonProperty("type")
    public String getType() { return type; }
    @JsonProperty("type")
    public void setType(String value) { this.type = value; }

    @JsonProperty("id")
    public String getID() { return id; }
    @JsonProperty("id")
    public void setID(String value) { this.id = value; }

    @JsonProperty("attributes")
    public AttributesDto getAttributes() { return attributes; }
    @JsonProperty("attributes")
    public void setAttributes(AttributesDto value) { this.attributes = value; }
}
