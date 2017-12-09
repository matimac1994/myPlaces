package com.maciejak.myplaces.api.mappers;

import com.maciejak.myplaces.api.dto.request.AddPlaceRequest;
import com.maciejak.myplaces.api.dto.request.EditPlaceRequest;
import com.maciejak.myplaces.api.dto.response.PlaceListResponse;
import com.maciejak.myplaces.api.dto.response.PlaceMapResponse;
import com.maciejak.myplaces.api.dto.response.PlaceResponse;
import com.maciejak.myplaces.model.Place;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created by Mati on 02.12.2017.
 */

@Mapper
public interface PlaceMapper {

    PlaceMapper INSTANCE = Mappers.getMapper(PlaceMapper.class);

    PlaceResponse placeToPlaceResponse(Place place);

    Place placeResponseToPlace(PlaceResponse placeResponse);

    PlaceMapResponse placeToMapPlaceResponse(Place place);

    PlaceListResponse placeToPlaceListResponse(Place place);

    Place addPlaceRequestToPlace(AddPlaceRequest addPlaceRequest);

    AddPlaceRequest placeToAddPlaceRequest(Place place);

    EditPlaceRequest placeToEditPlaceRequest(Place place);
}
