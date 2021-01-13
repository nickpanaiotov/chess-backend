package bg.chess.api.controller;

import bg.chess.api.model.User;
import bg.chess.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<User> create(@RequestBody User user) {
        return this.userService.create(user);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<User> getAll() {
        return this.userService.getUsers();
    }
}
