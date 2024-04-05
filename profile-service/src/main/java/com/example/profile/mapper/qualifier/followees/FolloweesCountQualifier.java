package com.example.profile.mapper.qualifier.followees;

import com.example.profile.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FolloweesCountQualifier {

    private final FollowRepository followRepository;

    @FolloweesForProfile
    public int countFollowees(String profileId) {
        return followRepository.countAllByFollowerProfile_Id(profileId);
    }
}
