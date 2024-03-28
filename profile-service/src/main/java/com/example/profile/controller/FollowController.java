package com.example.profile.controller;

import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.service.impl.FollowServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<ProfileResponse>> getFollowers(@PathVariable String profileId) {
        return ResponseEntity.ok(followServiceImpl.getFollowers(profileId));
    }

    @GetMapping("/followees/{profileId}")
    public ResponseEntity<List<ProfileResponse>> getFollowees(@PathVariable String profileId) {
        return ResponseEntity.ok(followServiceImpl.getFollowees(profileId));
    }

    @GetMapping("/followees-celebrities/{profileId}")
    public ResponseEntity<List<ProfileResponse>> getFolloweesCelebrities(@PathVariable String profileId) {
        return ResponseEntity.ok(followServiceImpl.getFolloweesCelebrities(profileId));
    }
}
