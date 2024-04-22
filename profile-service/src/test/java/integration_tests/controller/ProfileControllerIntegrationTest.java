package integration_tests.controller;

import com.example.profile.ProfileServiceApplication;
import com.example.profile.dto.request.CreateProfileRequest;
import com.example.profile.dto.request.UpdateProfileRequest;
import com.example.profile.dto.response.PageResponse;
import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.entity.Profile;
import com.google.gson.reflect.TypeToken;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import test_util.RequestConfig;
import test_util.TestProfileUtil;
import test_util.starter.AllServicesStarter;

import java.time.LocalDate;
import java.util.List;

import static com.example.profile.config.gson.GsonConfig.GSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static test_util.TestControllerUtil.getContentWithExpectedStatus;
import static test_util.constant.GlobalConstants.TEST_EMAIL;
import static test_util.constant.GlobalConstants.TEST_USERNAME;
import static test_util.constant.UrlConstants.*;

@SpringBootTest(classes = {ProfileServiceApplication.class, TestProfileUtil.class})
@ActiveProfiles("test")
@Transactional(transactionManager = "mongoTransactionManager")
@ExtendWith(InstancioExtension.class)
@AutoConfigureMockMvc
public class ProfileControllerIntegrationTest implements AllServicesStarter {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestProfileUtil testProfileUtil;
    @Autowired
    @Qualifier("profiles")
    private Cache cache;

    @BeforeEach
    public void setUp() {
        cache.clear();
    }

    @Test
    void shouldCreateProfile() throws Exception {
        CreateProfileRequest createProfileRequest = new CreateProfileRequest(TEST_EMAIL, TEST_USERNAME, LocalDate.EPOCH);
        RequestConfig<CreateProfileRequest> config = new RequestConfig<>(HttpMethod.POST, createProfileRequest, CREATE_PROFILE);

        MockHttpServletRequestBuilder request = config.createRequest();
        ResultActions resultActions = mockMvc.perform(request);
        String jsonResponse = getContentWithExpectedStatus(resultActions, HttpStatus.OK);

        ProfileResponse createdProfile = GSON.fromJson(jsonResponse, ProfileResponse.class);
        assertThat(createdProfile)
                .extracting(ProfileResponse::email, ProfileResponse::username, ProfileResponse::joinDate)
                .containsExactly(TEST_EMAIL, TEST_USERNAME, LocalDate.EPOCH);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldUpdateProfile(Profile profile, String bio, String location, String website) throws Exception {
        ProfileResponse createdProfile = testProfileUtil.createProfile(profile);
        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest(TEST_USERNAME, bio, location, website, LocalDate.EPOCH);
        RequestConfig<UpdateProfileRequest> config = new RequestConfig<>(HttpMethod.PUT, updateProfileRequest, UPDATE_PROFILE);
        config.customizer(builder -> builder.header("Profile-Id", createdProfile.id()));

        MockHttpServletRequestBuilder request = config.createRequest();
        ResultActions resultActions = mockMvc.perform(request);
        String jsonResponse = getContentWithExpectedStatus(resultActions, HttpStatus.OK);

        ProfileResponse updatedProfile = GSON.fromJson(jsonResponse, ProfileResponse.class);
        assertThat(updatedProfile)
                .extracting(ProfileResponse::username, ProfileResponse::bio, ProfileResponse::location, ProfileResponse::website)
                .containsExactly(TEST_USERNAME, bio, location, website);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnProfile(Profile profile) throws Exception {
        ProfileResponse createdProfile = testProfileUtil.createProfile(profile);
        RequestConfig<Void> config = new RequestConfig<>(HttpMethod.GET, null, GET_PROFILE);
        config.addParam("id", createdProfile.id());

        MockHttpServletRequestBuilder request = config.createRequest();
        ResultActions resultActions = mockMvc.perform(request);
        String jsonResponse = getContentWithExpectedStatus(resultActions, HttpStatus.OK);

        ProfileResponse response = GSON.fromJson(jsonResponse, ProfileResponse.class);
        assertThat(response).isEqualTo(createdProfile);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnProfiles(String username) throws Exception {
        List<Profile> profiles = generateProfilesWithUsername(username);
        List<ProfileResponse> createdProfiles = testProfileUtil.createProfiles(profiles);
        RequestConfig<Void> config = new RequestConfig<>(HttpMethod.GET, null, GET_PROFILES);
        config.customizer(builder -> builder.queryParam("username", username));

        MockHttpServletRequestBuilder request = config.createRequest();
        ResultActions resultActions = mockMvc.perform(request);
        String jsonResponse = getContentWithExpectedStatus(resultActions, HttpStatus.OK);

        PageResponse<ProfileResponse> response = GSON.fromJson(jsonResponse, new TypeToken<>() {
        });
        assertThat(response.content()).containsExactlyInAnyOrderElementsOf(createdProfiles);
    }

    private List<Profile> generateProfilesWithUsername(String username) {
        return Instancio.ofList(Profile.class)
                .set(field(Profile::getUsername), username)
                .create();
    }
}
