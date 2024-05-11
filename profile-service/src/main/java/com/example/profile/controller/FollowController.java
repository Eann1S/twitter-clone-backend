package com.example.profile.controller;

import com.example.profile.dto.response.PageResponse;
import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.service.impl.FollowServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FollowController {

    private final FollowServiceImpl followServiceImpl;

    @PostMapping("/follow/{followeeId}")
    public ResponseEntity<Void> follow(@PathVariable String followeeId, @RequestHeader("Profile-Id") String profileId) {
        followServiceImpl.follow(followeeId, profileId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unfollow/{followeeId}")
    public ResponseEntity<Void> unfollow(@PathVariable String followeeId, @RequestHeader("Profile-Id") String profileId) {
        followServiceImpl.unfollow(followeeId, profileId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/is-followed/{followeeId}")
    public ResponseEntity<Boolean> isFollowed(@PathVariable String followeeId, @RequestHeader("Profile-Id") String profileId) {
        return ResponseEntity.ok(followServiceImpl.isFollowed(followeeId, profileId));
    }

    @GetMapping("/followers/{profileId}")
    public ResponseEntity<PageResponse<ProfileResponse>> getFollowers(@PathVariable String profileId, Pageable pageable) {
        return ResponseEntity.ok(followServiceImpl.getFollowers(profileId, pageable));
    }

    @GetMapping("/followees/{profileId}")
    public ResponseEntity<PageResponse<ProfileResponse>> getFollowees(@PathVariable String profileId, Pageable pageable) {
        return ResponseEntity.ok(followServiceImpl.getFollowees(profileId, pageable));
    }

    @GetMapping("/followees-celebrities/{profileId}")
    public ResponseEntity<PageResponse<ProfileResponse>> getFolloweesCelebrities(@PathVariable String profileId) {
        return ResponseEntity.ok(followServiceImpl.getFolloweesCelebrities(profileId));
    }
}
