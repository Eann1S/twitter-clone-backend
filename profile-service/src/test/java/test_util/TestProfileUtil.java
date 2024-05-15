package test_util;

import com.example.profile.dto.request.CreateProfileRequest;
import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.service.ProfileService;
import org.instancio.Instancio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import java.util.List;
import java.util.stream.Collectors;

import static org.instancio.Select.field;

@TestComponent
public class TestProfileUtil {

    @Autowired
    private ProfileService profileService;

    public ProfileResponse getProfileResponse(String id) {
        return profileService.getProfileResponseById(id);
    }

    public ProfileResponse createRandomProfile() {
        CreateProfileRequest request = Instancio.create(CreateProfileRequest.class);
        return createProfile(request);
    }

    public List<ProfileResponse> createProfilesWithUsername(String username) {
        return Instancio.ofList(CreateProfileRequest.class)
                .set(field(CreateProfileRequest::username), username)
                .create()
                .stream()
                .map(this::createProfile)
                .collect(Collectors.toList());
    }

    public ProfileResponse createProfile(CreateProfileRequest request) {
        return profileService.createProfile(request);
    }
}
