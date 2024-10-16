package com._tcapital.centronotificaciones.Infrastructure.Adapter;

import com._tcapital.centronotificaciones.application.Dto.EmailDto;
import com._tcapital.centronotificaciones.application.Dto.EmailResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CamerFirmaAdapter {

    @Value("${camer.api}")
    private String ApiDomain;

    @Value("${camer.user}")
    private String UserKey;

    @Value("${camer.pass}")
    private String Password;

    @Autowired
    private RestTemplate restTemplate;


    public EmailResponseDto sendEmail(EmailDto emailRequest) {
        String externalApiUrl = ApiDomain+"/api/v1/sendmail";
        EmailResponseDto response = restTemplate.postForObject(externalApiUrl, emailRequest, EmailResponseDto.class);

        if (response != null) {
            System.out.println("Tracking ID: " + response.getData().getID());
            return response;
        } else {
            throw new RuntimeException("Failed to send email, no response from external API");
        }
    }

}
