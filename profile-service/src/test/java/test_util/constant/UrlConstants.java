package test_util.constant;

public class UrlConstants {

    public static final String CREATE_PROFILE = "/api/v1/profile";
    public static final String GET_PROFILE = "/api/v1/profile/{id}";
    public static final String GET_PROFILES = "/api/v1/profiles";
    public static final String UPDATE_PROFILE = "/api/v1/profile/update";

    public static final String FOLLOW_BY_ID_URL = "/api/v1/follows/%s";
    public static final String FOLLOWERS_BY_ID_URL = "/api/v1/follows/%s/followers";
    public static final String FOLLOWEES_BY_ID_URL = "/api/v1/follows/%s/followees";
}
