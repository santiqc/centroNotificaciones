package com._tcapital.centronotificaciones.Infrastructure.Adapter;

import com._tcapital.centronotificaciones.application.Dto.CamerFirma.AttributesLoginResponse;
import com._tcapital.centronotificaciones.application.Dto.CamerFirma.DataLoginResponse;
import com._tcapital.centronotificaciones.application.Dto.LoginCamerResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class LoginAdapter {

    @Value("${camer.api}")
    private String apiDomain;

    @Value("${camer.user}")
    private String username;

    @Value("${camer.pass}")
    private String password;

    @Value("${camer.admin}")
    private String adminUsername;

    @Value("${camer.pass.admin}")
    private String adminPassword;

    private final RestTemplate restTemplate;

    public LoginAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LoginCamerResponse login() {
        try {

            AttributesLoginResponse userToken = performLogin(username, password);

            AttributesLoginResponse adminToken = performLogin(adminUsername, adminPassword);


            LoginCamerResponse response = new LoginCamerResponse();
            DataLoginResponse data = new DataLoginResponse();
            AttributesLoginResponse attributes = new AttributesLoginResponse();


            attributes.setAccessToken(userToken.getAccessToken());
            attributes.setTokenType(userToken.getTokenType());
            attributes.setExpiresIn(userToken.getExpiresIn());
            attributes.setUserName(userToken.getUserName());
            attributes.setIssued(userToken.getIssued());
            attributes.setExpires(userToken.getExpires());


            data.setType("token");
            data.setAttributes(attributes);


            response.setData(data);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }

    private AttributesLoginResponse performLogin(String username, String password) {
        String loginUrl = apiDomain + "token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("username", username);
        map.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<AttributesLoginResponse> response = restTemplate.postForEntity(
                loginUrl,
                request,
                AttributesLoginResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Login failed with status: " + response.getStatusCode());
        }
    }
}