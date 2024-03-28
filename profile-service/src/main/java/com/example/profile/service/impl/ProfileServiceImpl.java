package com.example.profile.service.impl;

import com.example.profile.dto.request.CreateProfileRequest;
import com.example.profile.dto.request.UpdateProfileRequest;
import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.entity.Profile;
import com.example.profile.exception.ActionNotAllowedException;
import com.example.profile.exception.EntityNotFoundException;
import com.example.profile.mapper.ProfileMapper;
import com.example.profile.repository.ProfileRepository;
import com.example.profile.service.ProfileService;
import com.example.profile.util.FollowsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.example.profile.constant.CacheName.PROFILES_CACHE;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final FollowsUtil followsUtil;

    @Override
    public ProfileResponse createProfile(CreateProfileRequest createProfileRequest) {
        Profile profile = profileMapper.toProfile(createProfileRequest);
        profile = profileRepository.save(profile);
        return profileMapper.toResponse(profile, followsUtil);
    }

    @Override
    @Cacheable(cacheNames = PROFILES_CACHE, key = "#p0")
    public ProfileResponse getProfileById(String id) {
        Profile profile = findProfileByIdInDatabase(id);
        return profileMapper.toResponse(profile, followsUtil);
    }

    @Override
    @CachePut(cacheNames = PROFILES_CACHE, key = "#p0")
    public ProfileResponse updateProfile(String id, UpdateProfileRequest updateProfileRequest, String profileId) {
        Profile profileToUpdate = findProfileByIdInDatabase(id);
        validateThatProfileIsLoggedIn(profileId, profileToUpdate);
        Profile updatedProfile = profileMapper.updateProfileFromUpdateProfileRequest(updateProfileRequest, profileToUpdate);
        updatedProfile = profileRepository.save(updatedProfile);
        return profileMapper.toResponse(updatedProfile, followsUtil);
    }

    @Override
    public Page<ProfileResponse> getProfilesByUsername(String username, Pageable pageable) {
        Page<Profile> profiles = findProfilesByUsernameInDatabase(username, pageable);
        return profiles.map(profile -> profileMapper.toResponse(profile, followsUtil));
    }

    private Page<Profile> findProfilesByUsernameInDatabase(String username, Pageable pageable) {
        return profileRepository.findByUsernameContaining(username, pageable);
    }

    private Profile findProfileByIdInDatabase(String id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
    }

    private void validateThatProfileIsLoggedIn(String profileId, Profile loggedInProfile) {
        if (!loggedInProfile.getId().equals(profileId)) {
            throw new ActionNotAllowedException();
        }
    }
}
