package io.github.sekelenao.demo.configuration;

import io.github.sekelenao.demo.fluss.FlussClient;
import io.github.sekelenao.demo.fluss.LiveFlussClient;
import io.github.sekelenao.demo.fluss.NoOpFlussClient;
import io.github.sekelenao.demo.properties.AdtechProperties;
import org.apache.fluss.client.Connection;
import org.apache.fluss.client.ConnectionFactory;
import org.apache.fluss.client.lookup.Lookuper;
import org.apache.fluss.client.table.Table;
import org.apache.fluss.config.ConfigOptions;
import org.apache.fluss.metadata.TablePath;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Objects;

@Configuration
public class FlussConfiguration {

    @Configuration
    @Profile("demo")
    static class LiveConfiguration {

        @Bean
        Connection connection(AdtechProperties properties) {
            Objects.requireNonNull(properties);
            var configuration = new org.apache.fluss.config.Configuration();
            configuration.set(ConfigOptions.BOOTSTRAP_SERVERS, properties.fluss().bootstrapServers());
            return ConnectionFactory.createConnection(configuration);
        }

        @Bean
        Table table(Connection connection, AdtechProperties properties) {
            Objects.requireNonNull(properties);
            var path = TablePath.of(properties.fluss().database(), properties.fluss().table());
            return connection.getTable(path);
        }

        @Bean
        Lookuper lookuper(Table table) {
            return table.newLookup().createLookuper();
        }

        @Bean
        FlussClient liveFlussClient(Lookuper lookuper, AdtechProperties properties) {
            return new LiveFlussClient(lookuper, properties);
        }
    }

    @Configuration
    @Profile("!demo")
    static class NoOpConfiguration {

        @Bean
        FlussClient noOpFlussClient() {
            return new NoOpFlussClient();
        }
    }
}
