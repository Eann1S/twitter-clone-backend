package test_util.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import static org.apache.commons.lang.CharEncoding.UTF_8;

@TestConfiguration
public class TestValidatorConfig {

    @Bean
    @Qualifier("test")
    public MessageSource testMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:validationMessages-test");
        messageSource.setDefaultEncoding(UTF_8);
        return messageSource;
    }
}
