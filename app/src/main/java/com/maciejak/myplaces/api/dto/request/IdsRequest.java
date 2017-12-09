package com.maciejak.myplaces.api.dto.request;

import java.util.List;
/**
 * Created by Mati on 01.12.2017.
 */

public class IdsRequest {

    List<Long> ids;

    public IdsRequest() {
    }

    public IdsRequest(List<Long> ids) {
        this.ids = ids;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
