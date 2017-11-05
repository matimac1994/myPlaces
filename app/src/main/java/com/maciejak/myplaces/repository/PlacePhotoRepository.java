package com.maciejak.myplaces.repository;

import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.model.PlacePhoto;
import com.maciejak.myplaces.model.PlacePhoto_Table;
import com.maciejak.myplaces.model.Place_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * Created by Mati on 05.11.2017.
 */

public class PlacePhotoRepository {

    public PlacePhoto createPlacePhoto(String image ,Place place){
        PlacePhoto placePhoto = new PlacePhoto();
        placePhoto.setImage(image);
        placePhoto.setPlace(place);
        placePhoto.save();
        return placePhoto;

    }

    public PlacePhoto getPlacePhotoById(long placePhotoId){
        return SQLite.select().from(PlacePhoto.class).where(PlacePhoto_Table.id.eq(placePhotoId)).querySingle();
    }

    public List<PlacePhoto> getPlacePhotosByPlace(Place place){
        return SQLite.select().from(PlacePhoto.class).where(PlacePhoto_Table.place_id.eq(place.getId())).queryList();
    }

    public List<PlacePhoto> getAllPlacePhotos(){
        return SQLite.select().from(PlacePhoto.class).queryList();
    }
}
