package com.example.profile.service;

import com.example.profile.dto.request.CreateProfileRequest;
import com.example.profile.dto.request.UpdateProfileRequest;
import com.example.profile.dto.response.ProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProfileService {

    ProfileResponse createProfile(CreateProfileRequest createProfileRequest);
    ProfileResponse getProfileById(String id);
    ProfileResponse updateProfile(String id, UpdateProfileRequest updateProfileRequest, String profileId);
    Page<ProfileResponse> getProfilesByUsername(String username, Pageable pageable);
}
