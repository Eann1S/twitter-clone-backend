package com.example.profile.service.impl;

import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.entity.Follow;
import com.example.profile.entity.Profile;
import com.example.profile.mapper.ProfileMapper;
import com.example.profile.repository.FollowRepository;
import com.example.profile.service.FollowService;
import com.example.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ProfileMapper profileMapper;

    @Override
    public boolean follow(String followeeId, String profileId) {
        if (!isFollowed(followeeId, profileId)) {
            Profile followee = profileService.findProfileById(followeeId);
            Profile profile = profileService.findProfileById(profileId);
            Follow follow = Follow.builder()
                    .followeeProfile(followee)
                    .followerProfile(profile)
                    .followDateTime(LocalDateTime.now())
                    .build();
            followRepository.save(follow);
            return true;
        }
        return false;
    }

    @Override
    public boolean unfollow(String followeeId, String profileId) {
        if (isFollowed(followeeId, profileId)) {
            followRepository.deleteByFollowerProfile_IdAndFolloweeProfile_Id(profileId, followeeId);
            return true;
        }
        return false;
    }

    @Override
    public boolean isFollowed(String followeeId, String profileId) {
        return followRepository.existsByFollowerProfile_IdAndFolloweeProfile_Id(profileId, followeeId);
    }

    @Override
    public List<ProfileResponse> getFollowers(String profileId) {
        return followRepository.findAllByFolloweeProfile_Id(profileId)
                .stream()
                .map(Follow::getFollowerProfile)
                .map(profileMapper::toResponse)
                .collect(toList());
    }

    @Override
    public List<ProfileResponse> getFollowees(String profileId) {
        return followRepository.findAllByFollowerProfile_Id(profileId)
                .stream()
                .map(Follow::getFolloweeProfile)
                .map(profileMapper::toResponse)
                .collect(toList());
    }

    @Override
    public List<ProfileResponse> getFolloweesCelebrities(String profileId) {
        return getFollowees(profileId)
                .stream()
                .filter(profile -> profile.followers() > CELEBRITY_FOLLOWERS_THRESHOLD)
                .collect(toList());
    }
}
