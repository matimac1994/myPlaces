package com.maciejak.myplaces.api;

/**
 * Created by Mati on 26.11.2017.
 */

public class ServerConfig {

    public static final String SERVER_URL = "http://192.168.0.9:9494";

    public static final String BASE_URL = "/api/myplaces";

    public static final String REGISTRATION = BASE_URL + "/register";

    public static final String LOGIN = BASE_URL + "/login";

    public static final String LOGOUT = BASE_URL + "/logout";

    public static final String ADD_PLACE = BASE_URL + "/addplace";

    public static final String GET_ACTIVE_PLACES = BASE_URL + "/active";

    public static final String GET_MAP_PLACES = BASE_URL + "/map";

    public static final String SHOW_PLACE_GET_PLACE_BY_ID = BASE_URL + "/showplace/{placeId}";

    public static final String DELETE_PLACE_BY_ID = BASE_URL + "/delete/{placeId}";

    public static final String RESTORE_PLACE_BY_ID = BASE_URL + "/restore/{placeId}";

    public static final String ARCHIVE_PLACE_BY_ID = BASE_URL + "/archive/{placeId}";

    public static final String ARCHIVE_PLACES_SPACE = BASE_URL + "/archived";

}
