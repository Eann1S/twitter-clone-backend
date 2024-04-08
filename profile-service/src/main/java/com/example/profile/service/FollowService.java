package com.example.profile.service;

import com.example.profile.dto.response.ProfileResponse;

import java.util.List;

public interface FollowService {

    int CELEBRITY_FOLLOWERS_THRESHOLD = 10000;

    boolean follow(String followeeId, String profileId);

    boolean unfollow(String followeeId, String profileId);

    boolean isFollowed(String followeeId, String profileId);

    List<ProfileResponse> getFollowers(String profileId);

    List<ProfileResponse> getFollowees(String profileId);

    List<ProfileResponse> getFolloweesCelebrities(String profileId);
}
