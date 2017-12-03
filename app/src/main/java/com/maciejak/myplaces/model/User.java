package com.maciejak.myplaces.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mati on 28.11.2017.
 */
@Table(database = MyPlacesDatabase.class)
public class User extends BaseModel{

    @PrimaryKey
    Long id;

    @Column
    String username;

    @Column
    String token;

    List<Place> places = new ArrayList<>();

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "places")
    public List<Place> getPlaces(){
        if (places == null || places.isEmpty()) {
            places = new Select()
                    .from(Place.class)
                    .where(Place_Table.user_id.eq(id))
                    .queryList();
        }
        return places;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }
}
