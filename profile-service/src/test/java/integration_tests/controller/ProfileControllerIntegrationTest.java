package integration_tests.controller;

import com.example.profile.ProfileServiceApplication;
import com.example.profile.dto.request.CreateProfileRequest;
import com.example.profile.dto.request.UpdateProfileRequest;
import com.example.profile.dto.response.PageResponse;
import com.example.profile.dto.response.ProfileResponse;
import com.example.profile.entity.Profile;
import com.google.gson.reflect.TypeToken;
import org.instancio.Instancio;
import org.instancio.generators.Generators;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import test_util.RequestConfig;
import test_util.TestControllerUtil;
import test_util.TestProfileUtil;
import test_util.config.TestValidatorConfig;
import test_util.starter.AllServicesStarter;

import java.util.List;
import java.util.Locale;

import static com.example.profile.config.gson.GsonConfig.GSON;
import static com.example.profile.message.ErrorMessage.ENTITY_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.springframework.http.HttpStatus.*;
import static test_util.constant.UrlConstants.*;
import static test_util.model.TestModels.VALID_CREATE_REQUEST_MODEL;
import static test_util.model.TestModels.VALID_UPDATE_REQUEST_MODEL;

@SpringBootTest(classes = {
        ProfileServiceApplication.class,
        TestValidatorConfig.class,
        TestProfileUtil.class,
        TestControllerUtil.class
})
@ActiveProfiles("test")
@Transactional(transactionManager = "mongoTransactionManager")
@ExtendWith(InstancioExtension.class)
public class ProfileControllerIntegrationTest implements AllServicesStarter {

    @Autowired
    private TestControllerUtil testControllerUtil;
    @Autowired
    private TestProfileUtil testProfileUtil;
    @Autowired
    @Qualifier("profiles")
    private Cache cache;
    @Autowired
    @Qualifier("test")
    private MessageSource messageSource;

    @BeforeEach
    public void setUp() {
        cache.clear();
    }

    @Nested
    class SuccessCases {
        @Test
        void shouldCreateProfile() throws Exception {
            var request = Instancio.of(VALID_CREATE_REQUEST_MODEL).create();
            var requestConfig = createConfigForCreateProfileRequest(request);

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, OK);

            ProfileResponse profileResponse = GSON.fromJson(json, ProfileResponse.class);
            assertThat(profileResponse)
                    .extracting(ProfileResponse::email, ProfileResponse::username, ProfileResponse::joinDate)
                    .containsExactly(request.email(), request.username(), request.joinDate());
        }

        @Test
        void shouldUpdateProfile() throws Exception {
            ProfileResponse createdProfile = testProfileUtil.createRandomProfile();
            var request = Instancio.of(VALID_UPDATE_REQUEST_MODEL).create();
            var requestConfig = createConfigForUpdateProfileRequest(createdProfile.id(), request);

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, OK);

            ProfileResponse profileResponse = GSON.fromJson(json, ProfileResponse.class);
            assertThat(profileResponse)
                    .extracting(
                            ProfileResponse::username,
                            ProfileResponse::bio,
                            ProfileResponse::location,
                            ProfileResponse::website,
                            ProfileResponse::birthDate
                    )
                    .containsExactly(
                            request.username(),
                            request.bio(),
                            request.location(),
                            request.website(),
                            request.birthDate()
                    );
        }

        @Test
        void shouldReturnProfile() throws Exception {
            ProfileResponse createdProfile = testProfileUtil.createRandomProfile();
            var requestConfig = createConfigForGetProfileRequest(createdProfile.id());

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, OK);

            ProfileResponse profileResponse = GSON.fromJson(json, ProfileResponse.class);
            assertThat(profileResponse).isEqualTo(createdProfile);
        }

        @ParameterizedTest
        @InstancioSource
        void shouldReturnProfiles(String username) throws Exception {
            List<ProfileResponse> createdProfiles = testProfileUtil.createProfilesWithUsername(username);
            var requestConfig = createConfigForGetProfilesRequest(username);

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, OK);

            PageResponse<ProfileResponse> profileResponses = GSON.fromJson(json, new TypeToken<>() {
            });
            assertThat(profileResponses.getContent()).containsExactlyInAnyOrderElementsOf(createdProfiles);
        }
    }

    @Nested
    class FailureCases {
        @Test
        void shouldNotCreateProfile_whenEmailIsEmpty() throws Exception {
            var createProfileRequest = Instancio.of(VALID_CREATE_REQUEST_MODEL)
                    .set(field(CreateProfileRequest::email), "")
                    .create();
            var requestConfig = createConfigForCreateProfileRequest(createProfileRequest);

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, BAD_REQUEST);

            String errorMessage = getErrorMessageByCode("email.not-blank");
            assertThat(json).contains(errorMessage);
        }

        @Test
        void shouldNotCreateProfile_whenEmailIsInvalid() throws Exception {
            var createProfileRequest = Instancio.of(VALID_CREATE_REQUEST_MODEL)
                    .generate(field(CreateProfileRequest::email), Generators::string)
                    .create();
            var requestConfig = createConfigForCreateProfileRequest(createProfileRequest);

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, BAD_REQUEST);

            String errorMessage = getErrorMessageByCode("email.invalid");
            assertThat(json).contains(errorMessage);
        }

        @Test
        void shouldNotCreateProfile_whenUsernameIsEmpty() throws Exception {
            var createProfileRequest = Instancio.of(VALID_CREATE_REQUEST_MODEL)
                    .set(field(CreateProfileRequest::username), "")
                    .create();
            var requestConfig = createConfigForCreateProfileRequest(createProfileRequest);

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, BAD_REQUEST);

            String errorMessage = getErrorMessageByCode("username.not-blank");
            assertThat(json).contains(errorMessage);
        }

        @Test
        void shouldNotCreateProfile_whenUsernameHasInvalidLength() throws Exception {
            var createProfileRequest = Instancio.of(VALID_CREATE_REQUEST_MODEL)
                    .generate(field(CreateProfileRequest::username), gen -> gen.string().length(100))
                    .create();
            var requestConfig = createConfigForCreateProfileRequest(createProfileRequest);

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, BAD_REQUEST);

            String errorMessage = getErrorMessageByCode("username.size", 5, 25);
            assertThat(json).contains(errorMessage);
        }

        @Test
        void shouldNotCreateProfile_whenJoinDateIsNull() throws Exception {
            var createProfileRequest = Instancio.of(VALID_CREATE_REQUEST_MODEL)
                    .set(field(CreateProfileRequest::joinDate), null)
                    .create();
            var requestConfig = createConfigForCreateProfileRequest(createProfileRequest);

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, BAD_REQUEST);

            String errorMessage = getErrorMessageByCode("joinDate.not-null");
            assertThat(json).contains(errorMessage);
        }

        @Test
        void shouldNotUpdateProfile_whenUsernameIsEmpty() throws Exception {
            ProfileResponse createdProfile = testProfileUtil.createRandomProfile();
            var updateProfileRequest = Instancio.of(VALID_UPDATE_REQUEST_MODEL)
                    .set(field(UpdateProfileRequest::username), "")
                    .create();
            var requestConfig = createConfigForUpdateProfileRequest(createdProfile.id(), updateProfileRequest);

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, BAD_REQUEST);

            String errorMessage = getErrorMessageByCode("username.not-blank");
            assertThat(json).contains(errorMessage);
        }

        @Test
        void shouldNotUpdateProfile_whenUsernameHasInvalidLength() throws Exception {
            ProfileResponse createdProfile = testProfileUtil.createRandomProfile();
            var updateProfileRequest = Instancio.of(VALID_UPDATE_REQUEST_MODEL)
                    .generate(field(UpdateProfileRequest::username), gen -> gen.string().length(100))
                    .create();
            var requestConfig = createConfigForUpdateProfileRequest(createdProfile.id(), updateProfileRequest);

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, BAD_REQUEST);

            String errorMessage = getErrorMessageByCode("username.size", 5, 25);
            assertThat(json).contains(errorMessage);
        }

        @Test
        void shouldNotUpdateProfile_whenBioHasInvalidLength() throws Exception {
            ProfileResponse createdProfile = testProfileUtil.createRandomProfile();
            var updateProfileRequest = Instancio.of(VALID_UPDATE_REQUEST_MODEL)
                    .generate(field(UpdateProfileRequest::bio), gen -> gen.string().length(300))
                    .create();
            var requestConfig = createConfigForUpdateProfileRequest(createdProfile.id(), updateProfileRequest);

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, BAD_REQUEST);

            String errorMessage = getErrorMessageByCode("bio.size", 160);
            assertThat(json).contains(errorMessage);
        }

        @Test
        void shouldNotUpdateProfile_whenLocationHasInvalidLength() throws Exception {
            ProfileResponse createdProfile = testProfileUtil.createRandomProfile();
            var updateProfileRequest = Instancio.of(VALID_UPDATE_REQUEST_MODEL)
                    .generate(field(UpdateProfileRequest::location), gen -> gen.string().length(100))
                    .create();
            var requestConfig = createConfigForUpdateProfileRequest(createdProfile.id(), updateProfileRequest);

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, BAD_REQUEST);

            String errorMessage = getErrorMessageByCode("location.size", 30);
            assertThat(json).contains(errorMessage);
        }

        @Test
        void shouldNotUpdateProfile_whenWebsiteHasInvalidLength() throws Exception {
            ProfileResponse createdProfile = testProfileUtil.createRandomProfile();
            var updateProfileRequest = Instancio.of(VALID_UPDATE_REQUEST_MODEL)
                    .generate(field(UpdateProfileRequest::website), gen -> gen.string().length(150))
                    .create();
            var requestConfig = createConfigForUpdateProfileRequest(createdProfile.id(), updateProfileRequest);

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, BAD_REQUEST);

            String errorMessage = getErrorMessageByCode("website.size", 100);
            assertThat(json).contains(errorMessage);
        }

        @Test
        void shouldNotUpdateProfile_whenBirthdateIsNotInThePast() throws Exception {
            ProfileResponse createdProfile = testProfileUtil.createRandomProfile();
            var updateProfileRequest = Instancio.of(VALID_UPDATE_REQUEST_MODEL)
                    .generate(field(UpdateProfileRequest::birthDate), gen -> gen.temporal().localDate().future())
                    .create();
            var requestConfig = createConfigForUpdateProfileRequest(createdProfile.id(), updateProfileRequest);

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, BAD_REQUEST);

            String errorMessage = getErrorMessageByCode("birthDate.past");
            assertThat(json).contains(errorMessage);
        }

        @ParameterizedTest
        @InstancioSource
        void shouldNotReturnProfile_whenProfileWasNotFound(Profile profile) throws Exception {
            var requestConfig = createConfigForGetProfileRequest(profile.getId());

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, NOT_FOUND);

            String errorMessage = ENTITY_NOT_FOUND.formatWith(profile.getId());
            assertThat(json).contains(errorMessage);
        }
    }

    @NotNull
    private RequestConfig<CreateProfileRequest> createConfigForCreateProfileRequest(CreateProfileRequest createProfileRequest) {
        return new RequestConfig<>(HttpMethod.POST, createProfileRequest, CREATE_PROFILE);
    }

    @NotNull
    private RequestConfig<UpdateProfileRequest> createConfigForUpdateProfileRequest(String id, UpdateProfileRequest updateProfileRequest) {
        var config = new RequestConfig<>(HttpMethod.PUT, updateProfileRequest, UPDATE_PROFILE);
        config.addHeader("Profile-Id", id);
        return config;
    }

    private RequestConfig<Void> createConfigForGetProfileRequest(String id) {
        RequestConfig<Void> config = new RequestConfig<>(HttpMethod.GET, null, GET_PROFILE);
        config.addParam("id", id);
        return config;
    }

    private RequestConfig<Void> createConfigForGetProfilesRequest(String username) {
        RequestConfig<Void> config = new RequestConfig<>(HttpMethod.GET, null, GET_PROFILES);
        config.addQueryParam("username", username);
        return config;
    }


    @NotNull
    private String getErrorMessageByCode(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.getDefault());
    }
}
