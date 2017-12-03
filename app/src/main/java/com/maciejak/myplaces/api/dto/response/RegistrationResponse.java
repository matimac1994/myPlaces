package com.maciejak.myplaces.api.dto.response;

/**
 * Created by Mati on 26.11.2017.
 */

public class RegistrationResponse extends BaseResponse {

    private Long userId;

    public RegistrationResponse() {
    }

    public RegistrationResponse(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
