package com.example.profile.service.impl;

import com.example.profile.dto.request.CreateProfileRequest;
import com.example.profile.dto.request.UpdateProfileRequest;
import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.entity.Profile;
import com.example.profile.exception.EntityNotFoundException;
import com.example.profile.mapper.ProfileMapper;
import com.example.profile.repository.ProfileRepository;
import com.example.profile.util.FollowsUtil;
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
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static com.example.profile.message.ErrorMessage.ENTITY_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class ProfileServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private ProfileMapper profileMapper;
    @Mock
    private FollowsUtil followsUtil;
    private ProfileServiceImpl profileService;

    @BeforeEach
    void setUp() {
        profileService = new ProfileServiceImpl(profileRepository, profileMapper, followsUtil);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldCreateProfile(CreateProfileRequest request, Profile profile, ProfileResponse profileResponse) {
        when(profileMapper.toProfile(request))
                .thenReturn(profile);
        when(profileRepository.save(profile))
                .thenReturn(profile);
        when(profileMapper.toResponse(profile, followsUtil))
                .thenReturn(profileResponse);

        ProfileResponse response = profileService.createProfile(request);

        verify(profileRepository).save(profile);
        assertThat(response).isEqualTo(profileResponse);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnProfile(Profile profile, ProfileResponse profileResponse) {
        when(profileRepository.findById(profile.getId()))
                .thenReturn(Optional.of(profile));
        when(profileMapper.toResponse(profile, followsUtil))
                .thenReturn(profileResponse);

        ProfileResponse response = profileService.getProfileById(profile.getId());

        assertThat(response).isEqualTo(profileResponse);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldUpdateProfile(Profile profile, ProfileResponse profileResponse, UpdateProfileRequest request) {
        when(profileRepository.findById(profile.getId()))
                .thenReturn(Optional.of(profile));
        when(profileRepository.save(profile))
                .thenReturn(profile);
        when(profileMapper.updateProfileFromUpdateProfileRequest(request, profile))
                .thenReturn(profile);
        when(profileMapper.toResponse(profile, followsUtil))
                .thenReturn(profileResponse);

        ProfileResponse response = profileService.updateProfile(profile.getId(), request, profile.getId());

        assertThat(response).isEqualTo(profileResponse);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnProfilesByUsername(String username, PageRequest pageRequest) {
        List<Profile> profiles = Instancio.ofList(Profile.class)
                .size(3).create();
        List<ProfileResponse> profileResponses = Instancio.ofList(ProfileResponse.class)
                .size(3).create();
        when(profileRepository.findByUsernameContaining(username, pageRequest))
                .thenReturn(new PageImpl<>(profiles));
        when(profileMapper.toResponse(any(Profile.class), any()))
                .thenReturn(profileResponses.get(0), profileResponses.get(1), profileResponses.get(2));

        Page<ProfileResponse> response = profileService.getProfilesByUsername(username, pageRequest);

        assertThat(response).containsExactlyInAnyOrderElementsOf(profileResponses);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldThrowException_whenProfileWasNotFound(Profile profile) {
        when(profileRepository.findById(profile.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.getProfileById(profile.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ENTITY_NOT_FOUND.formatWith(profile.getId()));
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnEmptyListOfProfiles_whenProfilesDoNotExistWithGivenUsername(String username, PageRequest pageRequest) {
        when(profileRepository.findByUsernameContaining(username, pageRequest))
                .thenReturn(Page.empty(pageRequest));

        Page<ProfileResponse> response = profileService.getProfilesByUsername(username, pageRequest);

        assertThat(response).isEmpty();
    }
}