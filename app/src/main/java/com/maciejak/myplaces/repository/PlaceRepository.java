package com.maciejak.myplaces.repository;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.model.PlacePhoto;
import com.maciejak.myplaces.model.Place_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mati on 30.10.2017.
 */

public class PlaceRepository {

    public void savePlace(String title, LatLng position, String note, String description, Uri mapPhoto, List<Uri> photosUri){

        Place place = new Place();

        place.setTitle(title);
        place.setLatitude(position.latitude);
        place.setLongitude(position.longitude);
        place.setNote(note);
        place.setDescription(description);
        place.setMapPhoto(mapPhoto.toString());

        List<PlacePhoto> photos = new ArrayList<>();
        for (Uri photoUri : photosUri){
            PlacePhoto placePhoto = new PlacePhoto();
            placePhoto.setImage(photoUri.toString());
            placePhoto.save();
            photos.add(placePhoto);
        }

        place.setPhotos(photos);
        place.save();

    }

    public Place getPlaceById(long placeId){
        return SQLite.select().from(Place.class).where(Place_Table.id.eq(placeId)).querySingle();
    }

    public List<Place> getPlaces(){
        return SQLite.select().from(Place.class).queryList();
    }
}
