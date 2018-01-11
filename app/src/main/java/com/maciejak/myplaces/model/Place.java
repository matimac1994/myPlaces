package com.maciejak.myplaces.model;

import com.google.android.gms.maps.model.LatLng;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Mati on 22.10.2017.
 */
@Table(database = MyPlacesDatabase.class)
public class Place extends BaseModel {

    @PrimaryKey(autoincrement = true) long id;
    @Column String title;
    @Column Double latitude;
    @Column Double longitude;
    @Column(length = 10000) String note;
    @Column(length = 10000) String description;
    @Column String mapPhoto;
    @Column Long createdAt;
    @Column Long updatedAt;
    @Column Long deletedAt;
    List<PlacePhoto> photos;

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "photos")
    public List<PlacePhoto> getPhotos() {
        if (photos == null || photos.isEmpty()) {
            photos = new Select()
                    .from(PlacePhoto.class)
                    .where(PlacePhoto_Table.place_id.eq(id))
                    .queryList();
        }
        return photos;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhotos(List<PlacePhoto> photos) {
        this.photos = photos;
    }

    public String getMapPhoto() {
        return mapPhoto;
    }

    public void setMapPhoto(String mapPhoto) {
        this.mapPhoto = mapPhoto;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Long deletedAt) {
        this.deletedAt = deletedAt;
    }
}
