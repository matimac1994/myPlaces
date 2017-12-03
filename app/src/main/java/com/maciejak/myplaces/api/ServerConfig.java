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

}
