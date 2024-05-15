package com.example.profile.service;

import com.example.profile.dto.response.PageResponse;
import com.example.profile.dto.response.ProfileResponse;
import org.springframework.data.domain.Pageable;

public interface FollowService {

    void follow(String followeeId, String profileId);

    void unfollow(String followeeId, String profileId);

    boolean isFollowed(String followeeId, String profileId);

    PageResponse<ProfileResponse> getFollowers(String profileId, Pageable pageable);

    PageResponse<ProfileResponse> getFollowees(String profileId, Pageable pageable);

    PageResponse<ProfileResponse> getFolloweesCelebrities(String profileId);
}
