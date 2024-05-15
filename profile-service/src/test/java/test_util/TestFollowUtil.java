package test_util;

import com.example.profile.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class TestFollowUtil {

    @Autowired
    private FollowService followService;

    public void followOneProfileToAnother(String followeeId, String profileId) {
        followService.follow(followeeId, profileId);
    }

    public boolean isFollowed(String followeeId, String profileId) {
        return followService.isFollowed(followeeId, profileId);
    }
}
