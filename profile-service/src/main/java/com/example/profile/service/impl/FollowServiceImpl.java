package com.example.profile.service.impl;

import com.example.profile.dto.response.PageResponse;
import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.entity.Follow;
import com.example.profile.entity.Profile;
import com.example.profile.mapper.PageMapper;
import com.example.profile.repository.FollowRepository;
import com.example.profile.service.FollowService;
import com.example.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final ProfileService profileService;
    private final PageMapper pageMapper;

    @Override
    public void follow(String followeeId, String profileId) {
        if (!isFollowed(followeeId, profileId)) {
            Profile followee = profileService.findProfileById(followeeId);
            Profile profile = profileService.findProfileById(profileId);
            Follow follow = Follow.builder()
                    .followeeProfile(followee)
                    .followerProfile(profile)
                    .followDateTime(LocalDateTime.now())
                    .build();
            followRepository.save(follow);
        }
    }

    @Override
    public void unfollow(String followeeId, String profileId) {
        if (isFollowed(followeeId, profileId)) {
            followRepository.deleteByFollowerProfile_IdAndFolloweeProfile_Id(profileId, followeeId);
        }
    }

    @Override
    public boolean isFollowed(String followeeId, String profileId) {
        return followRepository.existsByFollowerProfile_IdAndFolloweeProfile_Id(profileId, followeeId);
    }

    @Override
    public PageResponse<ProfileResponse> getFollowers(String profileId, Pageable pageable) {
        Page<Follow> follows = followRepository.findAllByFolloweeProfile_Id(profileId, pageable);
        Page<Profile> followers = follows.map(Follow::getFollowerProfile);
        return pageMapper.mapProfilesToPageResponse(followers);
    }

    @Override
    public PageResponse<ProfileResponse> getFollowees(String profileId, Pageable pageable) {
        Page<Follow> follows = followRepository.findAllByFollowerProfile_Id(profileId, pageable);
        Page<Profile> followees = follows.map(Follow::getFolloweeProfile);
        return pageMapper.mapProfilesToPageResponse(followees);
    }

    @Override
    public PageResponse<ProfileResponse> getFolloweesCelebrities(String profileId) {
        PageResponse<ProfileResponse> followees = getFollowees(profileId, Pageable.unpaged());
        List<ProfileResponse> followeesCelebrities = filterFolloweesCelebrities(followees);
        followees.setContent(followeesCelebrities);
        return followees;
    }

    @NotNull
    private List<ProfileResponse> filterFolloweesCelebrities(PageResponse<ProfileResponse> followees) {
        return followees.getContent()
                .stream()
                .filter(profile -> profile.followers() > CELEBRITY_FOLLOWERS_THRESHOLD)
                .collect(toList());
    }
}
