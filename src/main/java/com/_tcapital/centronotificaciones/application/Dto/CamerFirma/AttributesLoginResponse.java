package com._tcapital.centronotificaciones.application.Dto.CamerFirma;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AttributesLoginResponse {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private String userName;
    private String issued;
    private String expires;

    @JsonProperty("access_token")
    public String getAccessToken() { return accessToken; }
    @JsonProperty("access_token")
    public void setAccessToken(String value) { this.accessToken = value; }

    @JsonProperty("token_type")
    public String getTokenType() { return tokenType; }
    @JsonProperty("token_type")
    public void setTokenType(String value) { this.tokenType = value; }

    @JsonProperty("expires_in")
    public long getExpiresIn() { return expiresIn; }
    @JsonProperty("expires_in")
    public void setExpiresIn(long value) { this.expiresIn = value; }

    @JsonProperty("userName")
    public String getUserName() { return userName; }
    @JsonProperty("userName")
    public void setUserName(String value) { this.userName = value; }

    @JsonProperty(".issued")
    public String getIssued() { return issued; }
    @JsonProperty(".issued")
    public void setIssued(String value) { this.issued = value; }

    @JsonProperty(".expires")
    public String getExpires() { return expires; }
    @JsonProperty(".expires")
    public void setExpires(String value) { this.expires = value; }
}
