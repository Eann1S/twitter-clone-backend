package com.example.profile.service.impl;

import com.example.profile.config.service.FollowServiceConfig;
import com.example.profile.dto.response.PageResponse;
import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.entity.Follow;
import com.example.profile.entity.Profile;
import com.example.profile.mapper.PageMapper;
import com.example.profile.repository.FollowRepository;
import com.example.profile.service.FollowService;
import com.example.profile.service.ProfileService;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class FollowServiceImplTest {

    @Mock
    private FollowRepository followRepository;
    @Mock
    private ProfileService profileService;
    @Mock
    private PageMapper pageMapper;
    @Mock
    private FollowServiceConfig followServiceConfig;
    private FollowService followService;

    @BeforeEach
    void setUp() {
        followService = new FollowServiceImpl(followRepository, profileService, pageMapper, followServiceConfig);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldFollow(Profile followee, Profile follower) {
        when(profileService.findProfileById(followee.getId()))
                .thenReturn(followee);
        when(profileService.findProfileById(follower.getId()))
                .thenReturn(follower);
        when(followRepository.existsByFollowerProfile_IdAndFolloweeProfile_Id(follower.getId(), followee.getId()))
                .thenReturn(false);

        followService.follow(followee.getId(), follower.getId());

        verify(followRepository).save(
                assertArg(follow -> assertThat(follow)
                        .extracting(Follow::getFolloweeProfile, Follow::getFollowerProfile)
                        .containsExactly(followee, follower))
        );
    }

    @ParameterizedTest
    @InstancioSource
    void shouldUnfollow(Profile followee, Profile follower) {
        when(followRepository.existsByFollowerProfile_IdAndFolloweeProfile_Id(follower.getId(), followee.getId()))
                .thenReturn(true);

        followService.unfollow(followee.getId(), follower.getId());

        verify(followRepository).deleteByFollowerProfile_IdAndFolloweeProfile_Id(follower.getId(), followee.getId());
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnTrueIfProfileIsFollowed(Profile followee, Profile follower) {
        when(followRepository.existsByFollowerProfile_IdAndFolloweeProfile_Id(follower.getId(), followee.getId()))
                .thenReturn(true);

        boolean result = followService.isFollowed(followee.getId(), follower.getId());

        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnFollowers(Profile followee, PageResponse<ProfileResponse> pageResponse) {
        Page<Follow> follows = generateFollowsPage();
        when(followRepository.findAllByFolloweeProfile_Id(followee.getId(), Pageable.unpaged()))
                .thenReturn(follows);
        Page<Profile> followers = follows.map(Follow::getFollowerProfile);
        when(pageMapper.mapProfilesToPageResponse(followers))
                .thenReturn(pageResponse);

        PageResponse<ProfileResponse> followersResponse = followService.getFollowers(followee.getId(), Pageable.unpaged());

        assertThat(followersResponse).isEqualTo(pageResponse);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnFollowees(Profile follower, PageResponse<ProfileResponse> pageResponse) {
        Page<Follow> follows = generateFollowsPage();
        when(followRepository.findAllByFollowerProfile_Id(follower.getId(), Pageable.unpaged()))
                .thenReturn(follows);
        Page<Profile> followees = follows.map(Follow::getFolloweeProfile);
        when(pageMapper.mapProfilesToPageResponse(followees))
                .thenReturn(pageResponse);

        PageResponse<ProfileResponse> followeesResponse = followService.getFollowees(follower.getId(), Pageable.unpaged());

        assertThat(followeesResponse).isEqualTo(pageResponse);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnFolloweesCelebrities(int threshold, Profile follower, PageResponse<ProfileResponse> pageResponse) {
        when(followServiceConfig.getCelebrityFollowersThreshold())
                .thenReturn(threshold);
        Page<Follow> follows = generateFollowsPage();
        when(followRepository.findAllByFollowerProfile_Id(follower.getId(), Pageable.unpaged()))
                .thenReturn(follows);
        Page<Profile> followees = follows.map(Follow::getFolloweeProfile);
        when(pageMapper.mapProfilesToPageResponse(followees))
                .thenReturn(pageResponse);

        PageResponse<ProfileResponse> followeesCelebrities = followService.getFolloweesCelebrities(follower.getId());

        assertThat(followeesCelebrities).isEqualTo(pageResponse);
    }

    private Page<Follow> generateFollowsPage() {
        List<Follow> follows = Instancio.createList(Follow.class);
        return new PageImpl<>(follows);
    }
}