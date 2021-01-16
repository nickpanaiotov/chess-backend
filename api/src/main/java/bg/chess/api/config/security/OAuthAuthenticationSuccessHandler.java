package bg.chess.api.config.security;

import bg.chess.api.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.savedrequest.ServerRequestCache;
import org.springframework.security.web.server.savedrequest.WebSessionServerRequestCache;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

public class OAuthAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final URI location;
    private final ServerRedirectStrategy redirectStrategy;
    private final ServerRequestCache requestCache = new WebSessionServerRequestCache();

    private final UserMappingService userMappingService;


    public OAuthAuthenticationSuccessHandler(String location, UserMappingService userMappingService) {
        this.location = URI.create(location);
        var redirectStrategy = new DefaultServerRedirectStrategy();
        redirectStrategy.setContextRelative(false);
        this.redirectStrategy = redirectStrategy;

        this.userMappingService = userMappingService;

    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        User user = this.userMappingService.mapUser(((OAuth2AuthenticationToken) authentication));

        return this.userMappingService.createUserIfNotExist(user)
                .then(redirect(webFilterExchange));
    }

    private Mono<Void> redirect(WebFilterExchange webFilterExchange) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        return this.requestCache.getRedirectUri(exchange).defaultIfEmpty(this.location)
                .flatMap((location) -> this.redirectStrategy.sendRedirect(exchange, location));
    }
}
