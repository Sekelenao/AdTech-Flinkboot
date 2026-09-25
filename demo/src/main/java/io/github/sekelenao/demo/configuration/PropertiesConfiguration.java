package io.github.sekelenao.demo.configuration;

import io.github.sekelenao.demo.properties.AdtechProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Enables and registers configuration property records as Spring beans.
 */
@Configuration
@EnableConfigurationProperties(AdtechProperties.class)
public class PropertiesConfiguration {

}
