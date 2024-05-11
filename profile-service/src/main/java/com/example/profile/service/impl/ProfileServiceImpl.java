package com.example.profile.service.impl;

import com.example.profile.dto.request.CreateProfileRequest;
import com.example.profile.dto.request.UpdateProfileRequest;
import com.example.profile.dto.response.PageResponse;
import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.entity.Profile;
import com.example.profile.exception.EntityNotFoundException;
import com.example.profile.mapper.PageMapper;
import com.example.profile.mapper.ProfileMapper;
import com.example.profile.repository.ProfileRepository;
import com.example.profile.service.CacheService;
import com.example.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final PageMapper pageMapper;
    private final CacheService cacheService;

    @Override
    public ProfileResponse createProfile(CreateProfileRequest createProfileRequest) {
        Profile profile = profileMapper.toProfile(createProfileRequest);
        profile = profileRepository.save(profile);
        return profileMapper.toResponse(profile);
    }

    @Override
    public ProfileResponse getProfileResponseById(String id) {
        return cacheService.<ProfileResponse>getFromCache(id)
                .orElseGet(() -> {
                    Profile profile = findProfileById(id);
                    ProfileResponse response = profileMapper.toResponse(profile);
                    cacheService.putInCache(id, response);
                    return response;
                });
    }

    @Override
    public ProfileResponse updateProfile(String id, UpdateProfileRequest updateProfileRequest) {
        Profile profile = findProfileById(id);
        Profile updatedProfile = profileMapper.updateProfileFromUpdateProfileRequest(updateProfileRequest, profile);
        updatedProfile = profileRepository.save(updatedProfile);
        ProfileResponse response = profileMapper.toResponse(updatedProfile);
        cacheService.putInCache(id, response);
        return response;
    }

    @Override
    public PageResponse<ProfileResponse> getProfileResponsesByUsername(String username, Pageable pageable) {
        Page<Profile> profiles = findProfilesByUsername(username, pageable);
        return pageMapper.mapProfilesToPageResponse(profiles);
    }

    @Override
    public Profile findProfileById(String profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException(profileId));
    }

    private Page<Profile> findProfilesByUsername(String username, Pageable pageable) {
        return profileRepository.findByUsernameContaining(username, pageable);
    }
}
