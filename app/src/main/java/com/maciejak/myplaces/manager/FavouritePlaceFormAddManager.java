package com.maciejak.myplaces.manager;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.model.PlacePhoto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mati on 22.10.2017.
 */

public class FavouritePlaceFormAddManager {

    public void savePlace(String title, LatLng position, String note, String description,Uri mapPhoto, List<Uri> photosUri){

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


}
