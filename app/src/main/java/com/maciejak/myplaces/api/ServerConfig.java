package com.maciejak.myplaces.api;

/**
 * Created by Mati on 26.11.2017.
 */

public class ServerConfig {

    public static final String SERVER_URL = "http://192.168.1.13:9494";

    public static final String BASE_URL = "/api/myplaces";

    public static final String TOKEN_NAME = "X-Auth-Token";

    public static final String REGISTRATION = BASE_URL + "/register";

    public static final String LOGIN = BASE_URL + "/login";

    public static final String LOGOUT = BASE_URL + "/logout";

    public static final String PLACE_PHOTOS = BASE_URL + "/photos";


}
