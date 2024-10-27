package com._tcapital.centronotificaciones.application;

import com._tcapital.centronotificaciones.application.Dto.EmailDto;
import com._tcapital.centronotificaciones.application.Dto.EmailResponseDto;
import com._tcapital.centronotificaciones.application.Dto.FileUploadResponse;
import com._tcapital.centronotificaciones.application.Dto.RequestEmailDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmailService {
    EmailResponseDto sendEmail(RequestEmailDto email);
    List<EmailDto> getSentEmails(String status, String cc, String process, Integer pageNo, Integer pageSize);

    FileUploadResponse uploadFile(MultipartFile file);
}
