package integration_tests.controller;

import com.example.profile.ProfileServiceApplication;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import test_util.starter.AllServicesStarter;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static test_util.constant.GlobalConstants.PROFILE_EMAIL;
import static test_util.constant.UrlConstants.*;

@SpringBootTest(classes = ProfileServiceApplication.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor
public class FollowControllerIntegrationTest implements AllServicesStarter {

    private final MockMvc mockMvc;

    @Test
    public void isFollowedTest() throws Exception {
        String profileId = getProfileIdByEmail("EXISTENT_PROFILE_EMAIL.getConstant()");
        isFollowedExpect(profileId, PROFILE_EMAIL.getConstant(), "false");
        followExpect(profileId, PROFILE_EMAIL.getConstant(), "true");
        followExpect(profileId, PROFILE_EMAIL.getConstant(), "false");
        isFollowedExpect(profileId, PROFILE_EMAIL.getConstant(), "true");
        unfollowExpect(profileId, PROFILE_EMAIL.getConstant(), "true");
        unfollowExpect(profileId, PROFILE_EMAIL.getConstant(), "false");
        isFollowedExpect(profileId, PROFILE_EMAIL.getConstant(), "false");
    }

    @Test
    public void getFollowersTest() throws Exception {
        String followeeId = getProfileIdByEmail("UPDATE_PROFILE_EMAIL.getConstant()");
        followExpect(followeeId, PROFILE_EMAIL.getConstant(), "true");
        followExpect(followeeId, "EXISTENT_PROFILE_EMAIL.getConstant()", "true");
        getFollowersExpectAmount(followeeId, 2);
        unfollowExpect(followeeId, PROFILE_EMAIL.getConstant(), "true");
        getFollowersExpectAmount(followeeId, 1);
        unfollowExpect(followeeId, "EXISTENT_PROFILE_EMAIL.getConstant()", "true");
        getFollowersExpectAmount(followeeId, 0);
    }

    @Test
    public void getFolloweesTest() throws Exception {
        String firstFolloweeId = getProfileIdByEmail("EXISTENT_PROFILE_EMAIL.getConstant()");
        String secondFolloweeId = getProfileIdByEmail(PROFILE_EMAIL.getConstant());
        followExpect(firstFolloweeId, "UPDATE_PROFILE_EMAIL.getConstant()", "true");
        followExpect(secondFolloweeId, "UPDATE_PROFILE_EMAIL.getConstant()", "true");
        String followerId = getProfileIdByEmail("UPDATE_PROFILE_EMAIL.getConstant()");
        getFolloweesExpectAmount(followerId, 2);
        unfollowExpect(firstFolloweeId, "UPDATE_PROFILE_EMAIL.getConstant()", "true");
        getFolloweesExpectAmount(followerId, 1);
        unfollowExpect(secondFolloweeId, "UPDATE_PROFILE_EMAIL.getConstant()", "true");
        getFolloweesExpectAmount(followerId, 0);
    }

    private void isFollowedExpect(String followeeId, String profileId, String expected) throws Exception {
        mockMvc.perform(get(FOLLOW_BY_ID_URL.getConstant().formatted(followeeId))
                        .header("profileId", profileId))
                .andExpectAll(
                        status().is2xxSuccessful(),
                        content().string(expected)
                );
    }

    private void followExpect(String followeeId, String profileId, String expected) throws Exception {
        mockMvc.perform(post(FOLLOW_BY_ID_URL.getConstant().formatted(followeeId))
                        .header("profileId", profileId))
                .andExpectAll(
                        status().is2xxSuccessful(),
                        content().string(expected)
                );
    }

    private void unfollowExpect(String followeeId, String profileId, String expected) throws Exception {
        mockMvc.perform(delete(FOLLOW_BY_ID_URL.getConstant().formatted(followeeId))
                        .header("profileId", profileId))
                .andExpectAll(
                        status().is2xxSuccessful(),
                        content().string(expected)
                );
    }

    private void getFollowersExpectAmount(String profileId, Integer amount) throws Exception {
        mockMvc.perform(get(FOLLOWERS_BY_ID_URL.getConstant().formatted(profileId)))
                .andExpectAll(
                        status().is2xxSuccessful(),
                        jsonPath("$", hasSize(amount))
                );
    }

    private void getFolloweesExpectAmount(String profileId, Integer amount) throws Exception {
        mockMvc.perform(get(FOLLOWEES_BY_ID_URL.getConstant().formatted(profileId)))
                .andExpectAll(
                        status().is2xxSuccessful(),
                        jsonPath("$", hasSize(amount))
                );
    }

    private String getProfileIdByEmail(String email) throws Exception {
        return mockMvc.perform(get(PROFILE_ID_BY_EMAIL_URL.getConstant().formatted(email)))
                .andExpectAll(
                        status().is2xxSuccessful(),
                        content().string(not(emptyString())),
                        content().string(hasLength(24))
                )
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}
