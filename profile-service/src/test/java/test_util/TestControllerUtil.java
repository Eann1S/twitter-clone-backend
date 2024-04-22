package test_util;

import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestControllerUtil {

    public static String getContentWithExpectedStatus(ResultActions resultActions, HttpStatus status) throws Exception {
        expectStatus(resultActions, status);
        return getContentAsStringFrom(resultActions);
    }

    private static void expectStatus(ResultActions resultActions, HttpStatus status) throws Exception {
        resultActions.andExpect(status().is(status.value()));
    }

    private static String getContentAsStringFrom(ResultActions resultActions) throws Exception {
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        return response.getContentAsString();
    }
}
