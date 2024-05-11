package test_util.constant;

public class UrlConstants {

    public static final String CREATE_PROFILE = "/api/v1/profile";
    public static final String GET_PROFILE = "/api/v1/profile/{id}";
    public static final String GET_PROFILES = "/api/v1/profiles";
    public static final String UPDATE_PROFILE = "/api/v1/profile/update";

    public static final String IS_FOLLOWED = "/api/v1/is-followed/{followeeId}";
    public static final String FOLLOW = "/api/v1/follow/{followeeId}";
    public static final String UNFOLLOW = "/api/v1/unfollow/{followeeId}";
    public static final String FOLLOWERS = "/api/v1/followers/{profileId}";
    public static final String FOLLOWEES = "/api/v1/followees/{profileId}";
    public static final String FOLLOWEES_CELEBRITIES = "/api/v1/followees-celebrities/{profileId}";
}
