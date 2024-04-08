package com.example.profile.service.impl;

import com.example.profile.dto.request.CreateProfileRequest;
import com.example.profile.dto.request.UpdateProfileRequest;
import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.entity.Profile;
import com.example.profile.exception.EntityNotFoundException;
import com.example.profile.mapper.ProfileMapper;
import com.example.profile.repository.ProfileRepository;
import com.example.profile.service.CacheService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class ProfileServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private ProfileMapper profileMapper;
    @Mock
    private CacheService cacheService;
    private ProfileServiceImpl profileService;

    @BeforeEach
    void setUp() {
        profileService = new ProfileServiceImpl(profileRepository, profileMapper, cacheService);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldCreateProfile(CreateProfileRequest request, Profile profile, ProfileResponse profileResponse) {
        when(profileMapper.toProfile(request))
                .thenReturn(profile);
        when(profileRepository.save(profile))
                .thenReturn(profile);
        when(profileMapper.toResponse(profile))
                .thenReturn(profileResponse);

        ProfileResponse actualResponse = profileService.createProfile(request);

        verify(profileRepository).save(profile);
        assertThat(actualResponse).isEqualTo(profileResponse);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnProfileResponseById(Profile profile, ProfileResponse profileResponse) {
        when(cacheService.getFromCache(profile.getId()))
                .thenReturn(Optional.empty());
        when(profileRepository.findById(profile.getId()))
                .thenReturn(Optional.of(profile));
        when(profileMapper.toResponse(profile))
                .thenReturn(profileResponse);

        ProfileResponse actualResponse = profileService.getProfileResponseById(profile.getId());

        assertThat(actualResponse).isEqualTo(profileResponse);
        verify(cacheService).putInCache(profile.getId(), profileResponse);
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
        when(profileMapper.toResponse(profile))
                .thenReturn(profileResponse);

        ProfileResponse actualResponse = profileService.updateProfile(profile.getId(), request, profile.getId());

        assertThat(actualResponse).isEqualTo(profileResponse);
        verify(cacheService).putInCache(profile.getId(), profileResponse);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnProfilesByUsername(Profile profile, ProfileResponse profileResponse, PageRequest pageRequest) {
        when(profileRepository.findByUsernameContaining(profile.getUsername(), pageRequest))
                .thenReturn(new PageImpl<>(List.of(profile)));
        when(profileMapper.toResponse(profile))
                .thenReturn(profileResponse);

        Page<ProfileResponse> actualResponse = profileService.getProfileResponsesByUsername(profile.getUsername(), pageRequest);

        assertThat(actualResponse).containsExactly(profileResponse);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldThrowException_whenProfileWasNotFound(Profile profile) {
        when(profileRepository.findById(profile.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.getProfileResponseById(profile.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ENTITY_NOT_FOUND.formatWith(profile.getId()));
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnEmptyListOfProfiles_whenProfilesDoNotExistWithGivenUsername(String username, PageRequest pageRequest) {
        when(profileRepository.findByUsernameContaining(username, pageRequest))
                .thenReturn(Page.empty(pageRequest));

        Page<ProfileResponse> actualResponse = profileService.getProfileResponsesByUsername(username, pageRequest);

        assertThat(actualResponse).isEmpty();
    }
}