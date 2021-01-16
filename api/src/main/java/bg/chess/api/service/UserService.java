package bg.chess.api.service;

import bg.chess.api.model.User;
import bg.chess.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public Mono<User> create(User user) {
        if (user.getPassword() != null) {
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        }
        return this.userRepository.save(user)
                .map(created -> {
                    created.setPassword(null);
                    return created;
                });
    }

    public Flux<User> getUsers() {
        return this.userRepository.findAll()
                .map(user -> {
                    user.setPassword(null);
                    return user;
                });
    }

    public Mono<User> getUser(String username) {
        return this.userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND, "Unable to find user with username")));
    }
}
