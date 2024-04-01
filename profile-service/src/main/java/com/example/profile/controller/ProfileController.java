package com.example.profile.controller;

import com.example.profile.dto.request.CreateProfileRequest;
import com.example.profile.dto.request.UpdateProfileRequest;
import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/profile")
    public ResponseEntity<ProfileResponse> createProfile(@Valid @RequestBody CreateProfileRequest request) {
        return ResponseEntity.status(CREATED).body(profileService.createProfile(request));
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable String id) {
        return ResponseEntity.ok(profileService.getProfileById(id));
    }

    @GetMapping("/profiles")
    public ResponseEntity<Page<ProfileResponse>> getProfilesByUsername(
            @RequestParam String username,
            Pageable pageable
    ) {
        Page<ProfileResponse> page = profileService.getProfilesByUsername(username, pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/profile/{id}/update")
    public ResponseEntity<ProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @PathVariable String id,
            @RequestHeader("Profile-Id") String profileId
    ) {
        return ResponseEntity.ok(profileService.updateProfile(id, request, profileId));
    }
}
