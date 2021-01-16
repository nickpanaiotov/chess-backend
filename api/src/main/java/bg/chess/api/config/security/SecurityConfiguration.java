package bg.chess.api.config.security;

import bg.chess.api.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@AllArgsConstructor
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    private final UserMappingService userMappingService;

    @Bean
    public SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http,
                                                       JwtTokenProvider tokenProvider,
                                                       UnauthorizedAuthenticationEntryPoint entryPoint) {
        http.httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .logout().disable();


        return http
                .exceptionHandling()
                .authenticationEntryPoint(entryPoint)
                .and()
                .authorizeExchange(exchanges ->
                        exchanges
                                .pathMatchers(HttpMethod.POST, "/authentication/token").permitAll()
                                .pathMatchers(HttpMethod.POST, "/users").permitAll()
                                .pathMatchers(HttpMethod.GET, "/").permitAll()
                                .pathMatchers("/oauth2/**").permitAll()
                                .pathMatchers("/v2/api-docs", "/swagger-ui/**", "/swagger-resources/**", "/actuator/**").permitAll()
                                .anyExchange().authenticated()
                )
                .oauth2Login((loginSpec) -> loginSpec.authenticationSuccessHandler(new OAuthAuthenticationSuccessHandler("/#/oauth", this.userMappingService)))
                .oauth2Client(withDefaults())
                .addFilterAt(new JwtTokenAuthenticationFilter(tokenProvider), SecurityWebFiltersOrder.HTTP_BASIC)
                .build();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository userRepository) {
        return (username) -> userRepository.findByUsername(username)
                .map(u -> User.withUsername(u.getUsername())
                        .password(u.getPassword())
                        .authorities(u.getAuthorities())
                        .accountExpired(!u.isEnabled())
                        .credentialsExpired(!u.isCredentialsNonExpired())
                        .disabled(!u.isEnabled())
                        .accountLocked(!u.isAccountNonLocked())
                        .build()
                )
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND, "Unable to find user with username")));
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.applyPermitDefaultValues();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
