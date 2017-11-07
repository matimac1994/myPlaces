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
            placePhoto.setPlace(place);
            placePhoto.save();
            photos.add(placePhoto);
        }

        place.setPhotos(photos);
        place.save();
    }

    public Place getPlaceById(long placeId){
        return SQLite.select().from(Place.class).where(Place_Table.id.eq(placeId)).querySingle();
    }

    public List<Place> getAllPlaces(){
        return SQLite.select().from(Place.class).queryList();
    }

    public void editPlace(Place place, String title, String note, String description, List<PlacePhoto> photos, List<PlacePhoto> photosToDelete) {
        for (PlacePhoto placePhoto : photosToDelete){
            placePhoto.delete();
        }
        place.setTitle(title);
        place.setNote(note);
        place.setDescription(description);
        place.setPhotos(photos);
        place.save();
    }
}