package com._tcapital.centronotificaciones.application.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public class EmailResponseDto {
    private DataBodyDto data;

    @JsonProperty("data")
    public DataBodyDto getData() { return data; }
    @JsonProperty("data")
    public void setData(DataBodyDto value) { this.data = value; }
}

