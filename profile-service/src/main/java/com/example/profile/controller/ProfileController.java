package com.example.profile.controller;

import com.example.profile.dto.request.CreateProfileRequest;
import com.example.profile.dto.request.UpdateProfileRequest;
import com.example.profile.dto.response.PageResponse;
import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/profile")
    public ResponseEntity<ProfileResponse> createProfile(@Valid @RequestBody CreateProfileRequest request) {
        return ResponseEntity.ok(profileService.createProfile(request));
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable String id) {
        return ResponseEntity.ok(profileService.getProfileResponseById(id));
    }

    @GetMapping("/profiles")
    public ResponseEntity<PageResponse<ProfileResponse>> getProfilesByUsername(
            @RequestParam String username,
            Pageable pageable
    ) {
        PageResponse<ProfileResponse> page = profileService.getProfileResponsesByUsername(username, pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/profile/update")
    public ResponseEntity<ProfileResponse> updateProfile(
            @RequestHeader("Profile-Id") String profileId,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(profileService.updateProfile(profileId, request));
    }
}
