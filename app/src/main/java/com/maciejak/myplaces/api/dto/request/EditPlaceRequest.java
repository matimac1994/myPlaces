package com.maciejak.myplaces.api.dto.request;

/**
 * Created by Mati on 03.12.2017.
 */

public class EditPlaceRequest {

    private Long id;
    private String title;
    private String description;
    private String note;

    public EditPlaceRequest() {
    }

    public EditPlaceRequest(Long id, String title, String description, String note) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.note = note;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}