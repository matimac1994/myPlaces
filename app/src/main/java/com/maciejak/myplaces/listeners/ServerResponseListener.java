package com.maciejak.myplaces.listeners;

import com.maciejak.myplaces.api.dto.response.BaseResponse;
import com.maciejak.myplaces.api.dto.response.error.ErrorResponse;

/**
 * Created by Mati on 29.11.2017.
 */

public interface ServerResponseListener {
    void onSuccessResponse(BaseResponse response);

    void onErrorResponse(ErrorResponse response);

    void onFailure(String message);
}
