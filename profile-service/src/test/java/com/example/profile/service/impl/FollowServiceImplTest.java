package com.example.profile.service.impl;

import com.example.profile.dto.response.PageResponse;
import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.entity.Follow;
import com.example.profile.entity.Profile;
import com.example.profile.mapper.PageMapper;
import com.example.profile.repository.FollowRepository;
import com.example.profile.service.FollowService;
import com.example.profile.service.ProfileService;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    private PageMapper pageMapper;
    private FollowService followService;

    @BeforeEach
    void setUp() {
        followService = new FollowServiceImpl(followRepository, profileService, pageMapper);
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

        verify(followRepository).save(any(Follow.class));
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
    void shouldReturnFollowers(Profile followee, Profile follower, PageResponse<ProfileResponse> pageResponse) {
        Follow follow = generateFollowWithFolloweeAndFollower(followee, follower);
        when(followRepository.findAllByFolloweeProfile_Id(followee.getId(), Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(follow)));
        when(pageMapper.mapProfilesToPageResponse(any()))
                .thenReturn(pageResponse);

        PageResponse<ProfileResponse> followers = followService.getFollowers(followee.getId(), Pageable.unpaged());

        assertThat(followers.getContent())
                .containsExactlyInAnyOrderElementsOf(pageResponse.getContent());
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnFollowees(Profile followee, Profile follower, ProfileResponse followeeResponse) {
        Follow follow = generateFollowWithFolloweeAndFollower(followee, follower);
        var pageResponse = generatePageResponseWithContent(followeeResponse);
        when(followRepository.findAllByFollowerProfile_Id(follower.getId(), Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(follow)));
        when(pageMapper.mapProfilesToPageResponse(any()))
                .thenReturn(pageResponse);

        PageResponse<ProfileResponse> followees = followService.getFollowees(follower.getId(), Pageable.unpaged());

        assertThat(followees.getContent()).containsExactly(followeeResponse);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnFolloweesCelebrities(Profile followee, Profile follower) {
        ProfileResponse followeeCelebrity = generateFolloweeCelebrity();
        Follow follow = generateFollowWithFolloweeAndFollower(followee, follower);
        var pageResponse = generatePageResponseWithContent(followeeCelebrity);
        when(followRepository.findAllByFollowerProfile_Id(follower.getId(), Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(follow)));
        when(pageMapper.mapProfilesToPageResponse(any()))
                .thenReturn(pageResponse);

        PageResponse<ProfileResponse> followeesCelebrities = followService.getFolloweesCelebrities(follower.getId());

        assertThat(followeesCelebrities.getContent())
                .containsExactlyInAnyOrderElementsOf(pageResponse.getContent());
    }

    private Follow generateFollowWithFolloweeAndFollower(Profile followee, Profile follower) {
        return Instancio.of(Follow.class)
                .set(field(Follow::getFolloweeProfile), followee)
                .set(field(Follow::getFollowerProfile), follower)
                .create();
    }

    private PageResponse<ProfileResponse> generatePageResponseWithContent(ProfileResponse... profileResponses) {
        PageResponse<ProfileResponse> pageResponse = Instancio.create(new TypeToken<>() {
        });
        pageResponse.setContent(List.of(profileResponses));
        return pageResponse;
    }

    private ProfileResponse generateFolloweeCelebrity() {
        return Instancio.of(ProfileResponse.class)
                .set(field(ProfileResponse::followers), CELEBRITY_FOLLOWERS_THRESHOLD + 1)
                .create();
    }
}