package guru.qa.niffler.config;

import guru.qa.niffler.service.cors.CorsCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@EnableWebSecurity
@Configuration
@Profile("local")
public class SecurityConfigLocal {

    private final CorsCustomizer corsCustomizer;
    private final Environment environment;

    @Autowired
    public SecurityConfigLocal(CorsCustomizer corsCustomizer, Environment environment) {
        this.corsCustomizer = corsCustomizer;
        this.environment = environment;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        corsCustomizer.corsCustomizer(http);

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(customizer ->
                        customizer.requestMatchers(
                                        antMatcher("/actuator/health"),
                                        antMatcher("/graphiql/**"),
                                        antMatcher("/graphql/**"),
                                        antMatcher("/favicon.ico"),
                                        antMatcher(HttpMethod.POST, "/graphql")
                                ).permitAll()
                                .anyRequest()
                                .authenticated()
                ).oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
