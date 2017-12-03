package com.maciejak.myplaces.api.dto.response;

/**
 * Created by Mati on 28.11.2017.
 */

public class LoginResponse extends BaseResponse {

    String token;

    public LoginResponse() {
    }

    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
