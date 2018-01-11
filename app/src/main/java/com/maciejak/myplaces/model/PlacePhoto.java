package com.maciejak.myplaces.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.math.BigInteger;

/**
 * Created by Mati on 22.10.2017.
 */
@Table(database = MyPlacesDatabase.class)
public class PlacePhoto extends BaseModel {

    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    private String placePhotoUrl;

    @Column
    private String placePhotoPath;

    @Column
    @ForeignKey(stubbedRelationship = true)
    Place place;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPlacePhotoUrl() {
        return placePhotoUrl;
    }

    public void setPlacePhotoUrl(String placePhotoUrl) {
        this.placePhotoUrl = placePhotoUrl;
    }

    public String getPlacePhotoPath() {
        return placePhotoPath;
    }

    public void setPlacePhotoPath(String placePhotoPath) {
        this.placePhotoPath = placePhotoPath;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
}
