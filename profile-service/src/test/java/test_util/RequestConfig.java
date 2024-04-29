package test_util;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
@Accessors(fluent = true)
public final class RequestConfig<T> {

    private final HttpMethod httpMethod;

    private final T requestBody;

    private final String url;

    private Consumer<MockHttpServletRequestBuilder> customizer;

    private final Map<String, String> params;

    public RequestConfig(HttpMethod httpMethod, T requestBody, String url) {
        this.httpMethod = httpMethod;
        this.requestBody = requestBody;
        this.url = url;
        params = new HashMap<>();
        customizer = (b) -> {
        };
    }

    public void customize(Consumer<MockHttpServletRequestBuilder> customizer) {
        this.customizer = this.customizer.andThen(customizer);
    }

    public void addHeader(String name, String value) {
        customizer = customizer.andThen(b -> b.header(name, value));
    }

    public void addQueryParam(String name, String value) {
        customizer = customizer.andThen(b -> b.queryParam(name, value));
    }

    public void addParam(String name, String value) {
        params.put(name, value);
    }
}
