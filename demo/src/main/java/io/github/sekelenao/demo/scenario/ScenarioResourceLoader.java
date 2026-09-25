package io.github.sekelenao.demo.scenario;

import io.github.sekelenao.demo.model.Scenario;
import io.github.sekelenao.demo.properties.AdtechProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ScenarioResourceLoader {

    private final ResourcePatternResolver resourcePatternResolver;

    private final ObjectMapper objectMapper;

    private final String pattern;

    public ScenarioResourceLoader(ResourcePatternResolver resolver, ObjectMapper mapper, AdtechProperties properties) {
        this.resourcePatternResolver = Objects.requireNonNull(resolver);
        this.objectMapper = Objects.requireNonNull(mapper);
        this.pattern = Objects.requireNonNull(properties).scenarios().pattern();
    }

    @Cacheable("scenarios")
    public List<Scenario> retrieveAll() throws IOException {
        var scenarios = new ArrayList<Scenario>();
        var resources = resourcePatternResolver.getResources(pattern);
        for (var resource : resources) {
            try (var inputStream = resource.getInputStream()) {
                var scenario = objectMapper.readValue(inputStream, Scenario.class);
                scenarios.add(scenario);
            }
        }
        return List.copyOf(scenarios);
    }
}
