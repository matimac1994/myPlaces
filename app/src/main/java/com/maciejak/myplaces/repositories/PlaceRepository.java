package com.maciejak.myplaces.repositories;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.model.PlacePhoto;
import com.maciejak.myplaces.model.Place_Table;
import com.maciejak.myplaces.utils.Const;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mati on 30.10.2017.
 */

public class PlaceRepository {

    public Place savePlace(String title, LatLng position, String note, String description, String mapPhoto, List<Uri> photosUri){

        Place place = new Place();

        place.setTitle(title);
        place.setLatitude(position.latitude);
        place.setLongitude(position.longitude);
        place.setNote(note);
        place.setDescription(description);
        place.setMapPhoto(mapPhoto);
        place.setCreatedAt(System.currentTimeMillis());

        List<PlacePhoto> photos = new ArrayList<>();
        for (Uri photoUri : photosUri){
            PlacePhoto placePhoto = new PlacePhoto();
            placePhoto.setPlacePhotoPath(photoUri.toString());
            placePhoto.setPlacePhotoUrl(photoUri.toString());
            placePhoto.setPlace(place);
            placePhoto.save();
            photos.add(placePhoto);
        }

        place.setPhotos(photos);
        place.save();

        return place;
    }

    public Place getPlaceById(long placeId){
        return SQLite.select()
                .from(Place.class)
                .where(Place_Table.id.eq(placeId))
                .querySingle();
    }

    public List<Place> getAllPlaces(){
        return SQLite.select()
                .from(Place.class)
                .orderBy(Place_Table.createdAt.getNameAlias(), false)
                .queryList();
    }

    public List<Place> getAllVisiblePlaces(){
        return SQLite.select()
                .from(Place.class)
                .where(Place_Table.deletedAt.isNull())
                .orderBy(Place_Table.createdAt.getNameAlias(), false)
                .queryList();
    }

    public List<Place> getAllDeletedPlaces(){
        return SQLite.select()
                .from(Place.class)
                .where(Place_Table.deletedAt.isNotNull())
                .orderBy(Place_Table.deletedAt.getNameAlias(), false)
                .queryList();
    }

    public void editPlace(Long placeId, String title, String description, String note, List<PlacePhoto> photos) {
        Place place = getPlaceById(placeId);
        if (place != null){
            place.setTitle(title);
            place.setNote(note);
            place.setDescription(description);
            place.setPhotos(photos);
            place.setUpdatedAt(System.currentTimeMillis());
            place.save();
        }
    }

    public void deletePlaceSoft(Place place){
        place.setDeletedAt(System.currentTimeMillis());
        place.save();
    }

    public void restorePlace(Place place){
        place.setDeletedAt(null);
        place.save();
    }

    public void deletePlace(Place place){
        place.delete();
    }

    public void restorePlaceById(Long id) throws Exception {
        Place place = getPlaceById(id);
        if (place != null){
            place.setDeletedAt(null);
            place.save();
        } else {
            throw new Exception();
        }
    }

    public void deletePlaceById(Long id) throws Exception {
        Place place = getPlaceById(id);
        if (place != null){
            place.delete();
        } else {
            throw new Exception();
        }
    }
}
