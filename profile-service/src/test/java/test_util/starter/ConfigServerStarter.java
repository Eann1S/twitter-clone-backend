package test_util.starter;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(parallel = true)
public interface ConfigServerStarter {

    @Container
    @SuppressWarnings({"resource", "deprecation"})
    GenericContainer<?> configServer = new GenericContainer<>(DockerImageName.parse("twitterclone0/twitter-spring-cloud-config-server:latest"))
            .withExposedPorts(8888)
            .withFileSystemBind("C://Users/123/Desktop/code_java/microservices-config-server", "/config-repo")
            .withEnv("SPRING_PROFILES_ACTIVE", "native")
            .withEnv("CONFIG_LOCATION", "file:///config-repo/{application}")
            .waitingFor(Wait.forHttp("/profile-service/test").forStatusCode(200));

    @BeforeAll
    static void setEnvironmentVariables() {
        System.setProperty("CONFIG_SERVER_URL", configServer.getHost() + ":" + configServer.getFirstMappedPort());
    }
}