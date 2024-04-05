package com.example.profile.mapper.qualifier.followers;

import com.example.profile.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FollowersCountQualifier {

    private final FollowRepository followRepository;

    @FollowersForProfile
    public int countFollowers(String profileId) {
        return followRepository.countAllByFolloweeProfile_Id(profileId);
    }
}
