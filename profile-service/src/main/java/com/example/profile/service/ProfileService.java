package com.example.profile.service;

import com.example.profile.dto.request.CreateProfileRequest;
import com.example.profile.dto.request.UpdateProfileRequest;
import com.example.profile.dto.response.PageResponse;
import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.entity.Profile;
import org.springframework.data.domain.Pageable;

public interface ProfileService {

    ProfileResponse createProfile(CreateProfileRequest createProfileRequest);

    ProfileResponse getProfileResponseById(String id);

    ProfileResponse updateProfile(String id, UpdateProfileRequest updateProfileRequest);

    PageResponse<ProfileResponse> getProfileResponsesByUsername(String username, Pageable pageable);

    Profile findProfileById(String profileId);
}
