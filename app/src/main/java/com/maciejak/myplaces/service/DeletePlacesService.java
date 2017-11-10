package com.maciejak.myplaces.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.repository.PlaceRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mati on 08.11.2017.
 */

public class DeletePlacesService extends IntentService {

    public static final String PLACES_TO_DELETE_IDS = "DeletePlacesService Places to delete ids";
    private ArrayList<String> placeListToDelete;
    public DeletePlacesService() {
        super("DeletePlacesService");
    }

    public DeletePlacesService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        placeListToDelete = intent.getStringArrayListExtra(PLACES_TO_DELETE_IDS);
        PlaceRepository placeRepository = new PlaceRepository();
        if (placeListToDelete != null){
            for (String placeId : placeListToDelete){
                Place place = placeRepository.getPlaceById(Long.decode(placeId));
                if (place != null){
                    place.delete();
                }
            }
        }
    }
}
