package com._tcapital.centronotificaciones.application.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArchivoDto {
    private String name;
    private String path;
    private boolean isWitness;
}

