package com.example.profile.service.impl;

import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.entity.Follow;
import com.example.profile.entity.Profile;
import com.example.profile.mapper.ProfileMapper;
import com.example.profile.repository.FollowRepository;
import com.example.profile.repository.ProfileRepository;
import com.example.profile.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.profile.config.cache.CacheConfig.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final CacheManager cacheManager;

    public boolean follow(String followeeId, String profileId) {
        return profileRepository.findByEmail(profileId)
                .map(Profile::getId)
                .filter(followerId -> !isFollowed(followeeId, profileId))
                .map(followerId -> Follow.builder()
                        .followerProfile(profileRepository.findById(followerId).orElseThrow())
                        .followeeProfile(profileRepository.findById(followeeId).orElseThrow())
                        .followDateTime(LocalDateTime.now())
                        .build())
                .map(followRepository::save)
                .isPresent();
    }

    public boolean unfollow(String followeeId, String profileId) {
        return profileRepository.findByEmail(profileId)
                .map(Profile::getId)
                .filter(followerId -> isFollowed(followeeId, profileId))
                .map(followerId -> {
                    Objects.requireNonNull(cacheManager.getCache(FOLLOWERS_CACHE)).evictIfPresent(followeeId);
                    Objects.requireNonNull(cacheManager.getCache(FOLLOWEES_CACHE)).evictIfPresent(followerId);
                    Objects.requireNonNull(cacheManager.getCache(FOLLOWEES_CELEBRITIES_CACHE)).evictIfPresent(followerId);
                    return followRepository.deleteByFollowerProfile_IdAndFolloweeProfile_Id(followerId, followeeId);
                })
                .isPresent();
    }

    @Cacheable(cacheNames = FOLLOWERS_CACHE, key = "#p0")
    public List<ProfileResponse> getFollowers(String profileId) {
        return followRepository.findAllByFolloweeProfile_Id(profileId)
                .stream()
                .map(Follow::getFollowerProfile)
                .map(profileMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(cacheNames = FOLLOWEES_CACHE, key = "#p0")
    public List<ProfileResponse> getFollowees(String profileId) {
        return followRepository.findAllByFollowerProfile_Id(profileId)
                .stream()
                .map(Follow::getFolloweeProfile)
                .map(profileMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(cacheNames = FOLLOWEES_CELEBRITIES_CACHE, key = "#p0")
    public List<ProfileResponse> getFolloweesCelebrities(String profileId) {
        return getFollowees(profileId)
                .stream()
                .filter(followee -> followee.getFollowers() > 10000)
                .collect(Collectors.toList());
    }

    public boolean isFollowed(String followeeId, String profileId) {
        return profileRepository.findByEmail(profileId)
                .map(Profile::getId)
                .map(followerId -> followRepository.existsByFollowerProfile_IdAndFolloweeProfile_Id(followerId, followeeId))
                .orElse(false);
    }
}
