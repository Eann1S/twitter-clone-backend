package integration_tests.controller;

import com.example.profile.ProfileServiceApplication;
import com.example.profile.config.service.FollowServiceConfig;
import com.example.profile.dto.response.PageResponse;
import com.example.profile.dto.response.ProfileResponse;
import com.example.utils.test.RequestConfig;
import com.example.utils.test.TestControllerUtil;
import com.google.gson.reflect.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import test_util.TestFollowUtil;
import test_util.TestProfileUtil;
import test_util.starter.AllServicesStarter;

import static com.example.profile.message.ErrorMessage.ALREADY_FOLLOWING;
import static com.example.profile.message.ErrorMessage.NOT_FOLLOWING;
import static com.example.utils.config.gson.GsonConfig.GSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static test_util.constant.UrlConstants.*;

@SpringBootTest(classes = {
        ProfileServiceApplication.class,
        TestProfileUtil.class,
        TestControllerUtil.class,
        TestFollowUtil.class
})
@ActiveProfiles("test")
@Transactional(transactionManager = "mongoTransactionManager")
@ExtendWith(InstancioExtension.class)
public class FollowControllerIntegrationTest implements AllServicesStarter {

    @Autowired
    private TestProfileUtil testProfileUtil;
    @Autowired
    private TestControllerUtil testControllerUtil;
    @Autowired
    private TestFollowUtil testFollowUtil;
    @Autowired
    private FollowServiceConfig config;

    private ProfileResponse followee;
    private ProfileResponse profile;

    @BeforeEach
    void setUp() {
        followee = testProfileUtil.createRandomProfile();
        profile = testProfileUtil.createRandomProfile();
    }

    @Nested
    class SuccessCases {
        @Test
        void shouldFollow() throws Exception {
            var requestConfig = createConfigForFollowRequest();

            testControllerUtil.expectStatusFromPerformedRequest(requestConfig, OK);

            boolean followed = testFollowUtil.isFollowed(followee.id(), profile.id());
            assertThat(followed).isTrue();
        }

        @Test
        void shouldUnfollow() throws Exception {
            testFollowUtil.followOneProfileToAnother(followee.id(), profile.id());
            var requestConfig = createConfigForUnfollowRequest();

            testControllerUtil.expectStatusFromPerformedRequest(requestConfig, OK);

            boolean followed = testFollowUtil.isFollowed(followee.id(), profile.id());
            assertThat(followed).isFalse();
        }

        @Test
        void shouldReturnTrue_whenFollowing() throws Exception {
            testFollowUtil.followOneProfileToAnother(followee.id(), profile.id());
            var requestConfig = createConfigForIsFollowedRequest();

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, OK);

            Boolean followed = GSON.fromJson(json, Boolean.class);
            assertThat(followed).isTrue();
        }

        @Test
        void shouldReturnFollowers() throws Exception {
            testFollowUtil.followOneProfileToAnother(followee.id(), profile.id());
            profile = updateProfileById(profile.id());
            var requestConfig = createConfigForGetFollowersRequest();

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, OK);

            PageResponse<ProfileResponse> followers = GSON.fromJson(json, new TypeToken<>() {
            });
            assertThat(followers.getContent()).containsExactly(profile);
        }

        @Test
        void shouldReturnFollowees() throws Exception {
            testFollowUtil.followOneProfileToAnother(followee.id(), profile.id());
            followee = updateProfileById(followee.id());
            var requestConfig = createConfigForGetFolloweesRequest();

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, OK);

            PageResponse<ProfileResponse> followees = GSON.fromJson(json, new TypeToken<>() {
            });
            assertThat(followees.getContent()).containsExactly(followee);
        }

        @Test
        void shouldReturnFolloweesCelebrities() throws Exception {
            followee = createCelebrity();
            testFollowUtil.followOneProfileToAnother(followee.id(), profile.id());
            followee = updateProfileById(followee.id());
            var requestConfig = createConfigForGetFolloweesCelebritiesRequest();

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, OK);

            PageResponse<ProfileResponse> followeesCelebrities = GSON.fromJson(json, new TypeToken<>() {
            });
            assertThat(followeesCelebrities.getContent()).containsExactly(followee);
        }
    }

    @Nested
    class FailureCases {
        @Test
        void shouldNotFollow_whenProfileIsAlreadyFollowingAnotherOne() throws Exception {
            testFollowUtil.followOneProfileToAnother(followee.id(), profile.id());
            var requestConfig = createConfigForFollowRequest();

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, BAD_REQUEST);

            assertThat(json).contains(ALREADY_FOLLOWING.formatWith(followee.id(), profile.id()));
        }

        @Test
        void shouldNotUnfollow_whenProfileIsNotFollowingAnotherOne() throws Exception {
            var requestConfig = createConfigForUnfollowRequest();

            String json = testControllerUtil.getJsonResponseFromPerformedRequestWithExpectedStatus(requestConfig, BAD_REQUEST);

            assertThat(json).contains(NOT_FOLLOWING.formatWith(followee.id(), profile.id()));
        }
    }

    private RequestConfig<Void> createConfigForFollowRequest() {
        return createRequestConfigWithFolloweeIdParamAndProfileIdHeader(HttpMethod.POST, FOLLOW);
    }

    private RequestConfig<Void> createConfigForUnfollowRequest() {
        return createRequestConfigWithFolloweeIdParamAndProfileIdHeader(HttpMethod.DELETE, UNFOLLOW);
    }

    private RequestConfig<Void> createConfigForIsFollowedRequest() {
        return createRequestConfigWithFolloweeIdParamAndProfileIdHeader(HttpMethod.GET, IS_FOLLOWED);
    }

    private RequestConfig<Void> createConfigForGetFollowersRequest() {
        return createGetRequestConfigWithIdParam(FOLLOWERS, followee.id());
    }

    private RequestConfig<Void> createConfigForGetFolloweesRequest() {
        return createGetRequestConfigWithIdParam(FOLLOWEES, profile.id());
    }

    private RequestConfig<Void> createConfigForGetFolloweesCelebritiesRequest() {
        return createGetRequestConfigWithIdParam(FOLLOWEES_CELEBRITIES, profile.id());
    }

    @NotNull
    private RequestConfig<Void> createGetRequestConfigWithIdParam(String url, String profileId) {
        RequestConfig<Void> config = new RequestConfig<>(HttpMethod.GET, null, url);
        config.addParam("profileId", profileId);
        return config;
    }

    @NotNull
    private RequestConfig<Void> createRequestConfigWithFolloweeIdParamAndProfileIdHeader(HttpMethod httpMethod, String url) {
        RequestConfig<Void> config = new RequestConfig<>(httpMethod, null, url);
        config.addParam("followeeId", followee.id());
        config.addHeader("Profile-Id", profile.id());
        return config;
    }

    public ProfileResponse createCelebrity() {
        ProfileResponse celebrity = testProfileUtil.createRandomProfile();
        for (int i = 0; i < config.getCelebrityFollowersThreshold() + 1; i++) {
            ProfileResponse randomProfile = testProfileUtil.createRandomProfile();
            testFollowUtil.followOneProfileToAnother(celebrity.id(), randomProfile.id());
        }
        return celebrity;
    }

    private ProfileResponse updateProfileById(String id) {
        return testProfileUtil.getProfileResponse(id);
    }
}
