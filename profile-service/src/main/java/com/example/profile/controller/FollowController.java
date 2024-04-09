package com.example.profile.controller;

import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.service.impl.FollowServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FollowController {

    private final FollowServiceImpl followServiceImpl;

    @GetMapping("/is-followed/{followeeId}")
    public ResponseEntity<Boolean> isFollowed(@PathVariable String followeeId, @RequestHeader String profileId) {
        return ResponseEntity.ok(followServiceImpl.isFollowed(followeeId, profileId));
    }

    @PostMapping("/follow/{followeeId}")
    public ResponseEntity<Boolean> follow(@PathVariable String followeeId, @RequestHeader String profileId) {
        return ResponseEntity.ok(followServiceImpl.follow(followeeId, profileId));
    }

    @DeleteMapping("/unfollow/{followeeId}")
    public ResponseEntity<Boolean> unfollow(@PathVariable String followeeId, @RequestHeader String profileId) {
        return ResponseEntity.ok(followServiceImpl.unfollow(followeeId, profileId));
    }

    @GetMapping("/followers/{profileId}")
    public ResponseEntity<Page<ProfileResponse>> getFollowers(@PathVariable String profileId, Pageable pageable) {
        return ResponseEntity.ok(followServiceImpl.getFollowers(profileId, pageable));
    }

    @GetMapping("/followees/{profileId}")
    public ResponseEntity<Page<ProfileResponse>> getFollowees(@PathVariable String profileId, Pageable pageable) {
        return ResponseEntity.ok(followServiceImpl.getFollowees(profileId, pageable));
    }

    @GetMapping("/followees-celebrities/{profileId}")
    public ResponseEntity<Page<ProfileResponse>> getFolloweesCelebrities(@PathVariable String profileId) {
        return ResponseEntity.ok(followServiceImpl.getFolloweesCelebrities(profileId));
    }
}
