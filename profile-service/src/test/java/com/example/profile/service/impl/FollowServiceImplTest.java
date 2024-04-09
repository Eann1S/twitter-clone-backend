package com.example.profile.service.impl;

import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.entity.Follow;
import com.example.profile.entity.Profile;
import com.example.profile.mapper.ProfileMapper;
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

import static com.example.profile.service.FollowService.CELEBRITY_FOLLOWERS_THRESHOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class FollowServiceImplTest {

    @Mock
    private FollowRepository followRepository;
    @Mock
    private ProfileService profileService;
    @Mock
    private ProfileMapper profileMapper;
    private FollowService followService;

    @BeforeEach
    void setUp() {
        followService = new FollowServiceImpl(followRepository, profileService, profileMapper);
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
        boolean result = followService.follow(followee.getId(), follower.getId());

        assertThat(result).isTrue();
        verify(followRepository).save(any(Follow.class));
    }

    @ParameterizedTest
    @InstancioSource
    void shouldUnfollow(Profile followee, Profile follower) {
        when(followRepository.existsByFollowerProfile_IdAndFolloweeProfile_Id(follower.getId(), followee.getId()))
                .thenReturn(true);

        boolean result = followService.unfollow(followee.getId(), follower.getId());

        assertThat(result).isTrue();
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
    void shouldReturnFollowers(Profile followee, Profile follower, ProfileResponse followerResponse) {
        Follow follow = generateFollowWithFolloweeAndFollower(followee, follower);
        when(followRepository.findAllByFolloweeProfile_Id(followee.getId()))
                .thenReturn(new PageImpl<>(List.of(follow)));
        when(profileMapper.toResponse(follower))
                .thenReturn(followerResponse);


        Page<ProfileResponse> followers = followService.getFollowers(followee.getId(), Pageable.unpaged());

        assertThat(followers).containsExactly(followerResponse);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnFollowees(Profile followee, Profile follower, ProfileResponse followeeResponse) {
        Follow follow = generateFollowWithFolloweeAndFollower(followee, follower);
        when(followRepository.findAllByFollowerProfile_Id(follower.getId()))
                .thenReturn(new PageImpl<>(List.of(follow)));
        when(profileMapper.toResponse(followee))
                .thenReturn(followeeResponse);

        Page<ProfileResponse> followees = followService.getFollowees(follower.getId(), Pageable.unpaged());

        assertThat(followees).containsExactly(followeeResponse);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnFolloweesCelebrities(Profile followee, Profile follower) {
        ProfileResponse followeeResponse = Instancio.of(ProfileResponse.class)
                .set(field(ProfileResponse::followers), CELEBRITY_FOLLOWERS_THRESHOLD + 1)
                .create();
        Follow follow = generateFollowWithFolloweeAndFollower(followee, follower);
        when(followRepository.findAllByFollowerProfile_Id(follower.getId()))
                .thenReturn(new PageImpl<>(List.of(follow)));
        when(profileMapper.toResponse(followee))
                .thenReturn(followeeResponse);

        Page<ProfileResponse> followeesCelebrities = followService.getFolloweesCelebrities(follower.getId());

        assertThat(followeesCelebrities).containsExactly(followeeResponse);
    }

    private Follow generateFollowWithFolloweeAndFollower(Profile followee, Profile follower) {
        return Instancio.of(Follow.class)
                .set(field(Follow::getFolloweeProfile), followee)
                .set(field(Follow::getFollowerProfile), follower)
                .create();
    }
}