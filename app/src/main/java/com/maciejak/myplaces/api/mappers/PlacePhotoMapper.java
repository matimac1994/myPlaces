package com.maciejak.myplaces.api.mappers;

import com.maciejak.myplaces.api.dto.request.IdsRequest;
import com.maciejak.myplaces.api.dto.response.PlacePhotoResponse;
import com.maciejak.myplaces.model.PlacePhoto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created by Mati on 05.12.2017.
 */

@Mapper
public interface PlacePhotoMapper {
    PlacePhotoMapper INSTANCE = Mappers.getMapper(PlacePhotoMapper.class);

    PlacePhotoResponse placePhotoToPlacePhotoResponse(PlacePhoto placePhoto);
    PlacePhoto placePhotoResponseToPlacePhoto(PlacePhotoResponse placePhotoResponse);
}
