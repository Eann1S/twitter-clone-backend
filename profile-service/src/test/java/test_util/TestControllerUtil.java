package test_util;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.function.Consumer;

import static com.example.profile.config.gson.GsonConfig.GSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class TestControllerUtil {

    @Autowired
    private MockMvc mockMvc;

    public <T> String getJsonResponseFromPerformedRequestWithExpectedStatus(RequestConfig<T> requestConfig, HttpStatus status) throws Exception {
        ResultActions resultActions = mockMvc.perform(createRequest(requestConfig));
        return getJsonResponseWithExpectedStatus(resultActions, status);
    }

    private <T> MockHttpServletRequestBuilder createRequest(RequestConfig<T> config) {
        String expandedUrl = expandUrlWithParams(config);
        MockHttpServletRequestBuilder requestBuilder = request(config.httpMethod(), expandedUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(GSON.toJson(config.requestBody()));
        acceptCustomizer(config.customizer(), requestBuilder);
        return requestBuilder;
    }

    @NotNull
    private <T> String expandUrlWithParams(RequestConfig<T> config) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(config.url());
        return uriBuilder.buildAndExpand(config.params()).toUriString();
    }

    private void acceptCustomizer(Consumer<MockHttpServletRequestBuilder> customizer, MockHttpServletRequestBuilder requestBuilder) {
        if (customizer != null) {
            customizer.accept(requestBuilder);
        }
    }

    private String getJsonResponseWithExpectedStatus(ResultActions resultActions, HttpStatus status) throws Exception {
        expectStatus(resultActions, status);
        return getContentAsStringFrom(resultActions);
    }

    private void expectStatus(ResultActions resultActions, HttpStatus status) throws Exception {
        resultActions.andExpect(status().is(status.value()));
    }

    private String getContentAsStringFrom(ResultActions resultActions) throws Exception {
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        return response.getContentAsString();
    }
}
