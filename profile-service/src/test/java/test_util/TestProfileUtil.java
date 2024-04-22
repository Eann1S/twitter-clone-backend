package test_util;

import com.example.profile.dto.request.CreateProfileRequest;
import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.entity.Profile;
import com.example.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

import java.util.ArrayList;
import java.util.List;

@TestComponent
@RequiredArgsConstructor
public class TestProfileUtil {

    private final ProfileService profileService;

    public ProfileResponse createProfile(Profile profile) {
        CreateProfileRequest request = new CreateProfileRequest(profile.getEmail(), profile.getUsername(), profile.getJoinDate());
        return profileService.createProfile(request);
    }

    public List<ProfileResponse> createProfiles(List<Profile> profiles) {
        List<ProfileResponse> res = new ArrayList<>();
        for (Profile profile : profiles) {
            ProfileResponse response = createProfile(profile);
            res.add(response);
        }
        return res;
    }
}
