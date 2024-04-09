package com.example.profile.service;

import com.example.profile.dto.response.ProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowService {

    int CELEBRITY_FOLLOWERS_THRESHOLD = 10000;

    boolean follow(String followeeId, String profileId);

    boolean unfollow(String followeeId, String profileId);

    boolean isFollowed(String followeeId, String profileId);

    Page<ProfileResponse> getFollowers(String profileId, Pageable pageable);

    Page<ProfileResponse> getFollowees(String profileId, Pageable pageable);

    Page<ProfileResponse> getFolloweesCelebrities(String profileId);
}
