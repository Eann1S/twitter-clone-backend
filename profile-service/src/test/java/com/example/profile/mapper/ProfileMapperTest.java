package com.example.profile.mapper;

import com.example.profile.dto.request.CreateProfileRequest;
import com.example.profile.dto.request.UpdateProfileRequest;
import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.entity.Profile;
import com.example.profile.mapper.qualifier.followees.FolloweesCountQualifier;
import com.example.profile.mapper.qualifier.followers.FollowersCountQualifier;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class ProfileMapperTest {

    @Mock
    private FolloweesCountQualifier followeesCountQualifier;
    @Mock
    private FollowersCountQualifier followersCountQualifier;
    private ProfileMapper profileMapper;

    @BeforeEach
    void setUp() {
        profileMapper = new ProfileMapperImpl(followeesCountQualifier, followersCountQualifier);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldMapToProfile(CreateProfileRequest request) {
        Profile profile = profileMapper.toProfile(request);

        assertThat(profile)
                .extracting(Profile::getEmail, Profile::getUsername, Profile::getJoinDate)
                .containsExactly(request.email(), request.username(), request.joinDate());
    }

    @ParameterizedTest
    @InstancioSource
    void shouldMapToResponse(Profile profile, int followeesCount, int followersCount) {
        when(followeesCountQualifier.countFollowees(profile.getId()))
                .thenReturn(followeesCount);
        when(followersCountQualifier.countFollowers(profile.getId()))
                .thenReturn(followersCount);

        ProfileResponse response = profileMapper.toResponse(profile);

        assertThat(response)
                .extracting(
                        ProfileResponse::profileId,
                        ProfileResponse::username,
                        ProfileResponse::email,
                        ProfileResponse::followees,
                        ProfileResponse::followers
                ).containsExactly(
                        profile.getId(),
                        profile.getUsername(),
                        profile.getEmail(),
                        followeesCount,
                        followersCount
                );
    }

    @ParameterizedTest
    @InstancioSource
    void updateProfileFromUpdateProfileRequest(UpdateProfileRequest request, Profile profileToUpdate) {
        Profile profile = profileMapper.updateProfileFromUpdateProfileRequest(request, profileToUpdate);

        assertThat(profile)
                .extracting(Profile::getUsername, Profile::getBio, Profile::getLocation, Profile::getBirthDate)
                .containsExactly(request.username(), request.bio(), request.location(), request.birthDate());
    }
}