package com.example.profile.controller;

import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FollowController {

    private final FollowService followService;

    @GetMapping("/is-followed/{followeeId}")
    public ResponseEntity<Boolean> isFollowed(@PathVariable String followeeId, @RequestHeader String loggedInUser) {
        return ResponseEntity.ok(followService.isFollowed(followeeId, loggedInUser));
    }

    @PostMapping("/follow/{followeeId}")
    public ResponseEntity<Boolean> follow(@PathVariable String followeeId, @RequestHeader String loggedInUser) {
        return ResponseEntity.ok(followService.follow(followeeId, loggedInUser));
    }

    @DeleteMapping("/unfollow/{followeeId}")
    public ResponseEntity<Boolean> unfollow(@PathVariable String followeeId, @RequestHeader String loggedInUser) {
        return ResponseEntity.ok(followService.unfollow(followeeId, loggedInUser));
    }

    @GetMapping("/followers/{profileId}")
    public ResponseEntity<List<ProfileResponse>> getFollowers(@PathVariable String profileId) {
        return ResponseEntity.ok(followService.getFollowers(profileId));
    }

    @GetMapping("/followees/{profileId}")
    public ResponseEntity<List<ProfileResponse>> getFollowees(@PathVariable String profileId) {
        return ResponseEntity.ok(followService.getFollowees(profileId));
    }

    @GetMapping("/followees-celebrities/{profileId}")
    public ResponseEntity<List<ProfileResponse>> getFolloweesCelebrities(@PathVariable String profileId) {
        return ResponseEntity.ok(followService.getFolloweesCelebrities(profileId));
    }
}
