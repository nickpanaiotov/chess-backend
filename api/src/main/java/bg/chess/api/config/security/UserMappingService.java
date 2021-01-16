package bg.chess.api.config.security;

import bg.chess.api.model.User;
import bg.chess.api.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UserMappingService {

    private final UserService userService;

    public Mono<User> createUserIfNotExist(User user) {
        return this.userService.getUser(user.getUsername())
                .onErrorResume(throwable -> userService.create(user));
    }

    public User mapUser(OAuth2AuthenticationToken authentication) {
        User user = new User();
        user.setUsername(authentication.getPrincipal().getAttribute("login"));
        user.addAttribute("id", authentication.getPrincipal().getName());
        user.addAttribute("avatar_url", authentication.getPrincipal().getAttribute("avatar_url"));
        user.setProvider(authentication.getAuthorizedClientRegistrationId());
//        user.addAuthority("ROLE_USER");

        return user;
    }
}
