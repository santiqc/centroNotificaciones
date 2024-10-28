package com._tcapital.centronotificaciones.application.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestAddresseeDto {

    private String name;
    private String process;
    private List<MultipartFile> file;
}
