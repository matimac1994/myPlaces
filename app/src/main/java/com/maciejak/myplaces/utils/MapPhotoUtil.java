package com.maciejak.myplaces.utils;

/**
 * Created by Mati on 01.12.2017.
 */

public class MapPhotoUtil {
    private static final String URL_FOR_STATIC_MAP = "https://maps.googleapis.com/maps/api/staticmap?";
    private Double latitude;
    private Double longitude;
    private Integer width = 720;
    private Integer height = 500;
    private Integer zoom = 14;

    public MapPhotoUtil(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public MapPhotoUtil(Double latitude,
                        Double longitude,
                        Integer width,
                        Integer height,
                        Integer zoom) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.width = width;
        this.height = height;
        this.zoom = zoom;
    }

    public String createUrlForMapImage(){
        StringBuilder url = new StringBuilder();
        url.append(URL_FOR_STATIC_MAP);
        url.append("center=" + latitude + "," + longitude + "&");
        url.append("zoom=" + zoom + "&");
        url.append("size=" + width + "x" + height + "&");
        url.append("markers=" + latitude + "," + longitude);
        return url.toString();
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

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getZoom() {
        return zoom;
    }

    public void setZoom(Integer zoom) {
        this.zoom = zoom;
    }
}

