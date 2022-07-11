package eu.openanalytics.containerproxy.test.unit;

import eu.openanalytics.containerproxy.ContainerProxyApplication;
import eu.openanalytics.containerproxy.test.proxy.PropertyOverrideContextInitializer;
import eu.openanalytics.containerproxy.test.proxy.TestIntegrationOnKube;
import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestParameterValidationService {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner();

    private void test(String resource, String expectedError) {
        this.contextRunner
                .withInitializer(new PropertyOverrideContextInitializer())
                .withInitializer(new TestPropertyLoader(resource))
                .withUserConfiguration(TestIntegrationOnKube.TestConfiguration.class, ContainerProxyApplication.class)
                .run(context -> {
                    assertThat(context)
                            .hasFailed();
                    assertThat(context.getStartupFailure().getMessage())
                            .contains(expectedError);
                });
    }

    @Test
    public void testParameterDefinitionErrors() {
        test("classpath:application-parameters-validation-1.yaml", "Configuration error: error in parameters of spec 'big-parameters', error: id of parameter may not be null");
        test("classpath:application-parameters-validation-2.yaml", "Configuration error: error in parameters of spec 'big-parameters', error: duplicate parameter id 'parameter1'");
        test("classpath:application-parameters-validation-3.yaml", "Configuration error: error in parameters of spec 'big-parameters', error: displayName may not be blank of parameter with id 'parameter1'");
        test("classpath:application-parameters-validation-4.yaml", "Configuration error: error in parameters of spec 'big-parameters', error: description may not be blank of parameter with id 'parameter1'");
    }

    @Test
    public void testValueSetErrors() {
        test("classpath:application-parameters-validation-5.yaml", "Configuration error: error in parameters of spec 'big-parameters', error: value set 1 is missing values for parameter with id 'parameter1'");
        test("classpath:application-parameters-validation-6.yaml", "Configuration error: error in parameters of spec 'big-parameters', error: value set 0 contains some duplicate values for parameter parameter1");
        test("classpath:application-parameters-validation-7.yaml", "Configuration error: error in parameters of spec 'big-parameters', error: value set 0 contains values for more parameters than there are defined");
    }

    public static class TestPropertyLoader implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        private final String location;

        public TestPropertyLoader(String resource) {
            this.location = resource;
        }

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            try {
                MutablePropertySources propertySources = configurableApplicationContext.getEnvironment().getPropertySources();
                Resource resource = configurableApplicationContext.getResource(location);
                YamlPropertySourceLoader sourceLoader = new YamlPropertySourceLoader();
                List<PropertySource<?>> yamlTestProperties = sourceLoader.load("yamlTestProperties", resource);
                propertySources.addFirst(yamlTestProperties.get(0));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
