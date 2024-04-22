package test_util;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.example.profile.config.gson.GsonConfig.GSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

@Getter
@Accessors(fluent = true)
public final class RequestConfig<T> {

    private final HttpMethod httpMethod;

    private final T requestBody;

    private final String url;

    @Setter
    private Consumer<MockHttpServletRequestBuilder> customizer;

    private final Map<String, String> params;

    public RequestConfig(HttpMethod httpMethod, T requestBody, String url) {
        this.httpMethod = httpMethod;
        this.requestBody = requestBody;
        this.url = url;
        params = new HashMap<>();
    }

    public void addParam(String name, String value) {
        params.put(name, value);
    }

    public MockHttpServletRequestBuilder createRequest() {
        String expandedUrl = expandUrlWithParams();
        MockHttpServletRequestBuilder requestBuilder = request(httpMethod, expandedUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(GSON.toJson(requestBody));
        if (customizer != null) {
            customizer.accept(requestBuilder);
        }
        return requestBuilder;
    }

    @NotNull
    private String expandUrlWithParams() {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
        return uriBuilder.buildAndExpand(params).toUriString();
    }
}
