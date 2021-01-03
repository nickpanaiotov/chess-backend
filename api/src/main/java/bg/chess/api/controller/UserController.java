package bg.chess.api.controller;

import bg.chess.api.model.User;
import bg.chess.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public Mono<User> create(@RequestBody User user) {
        return this.userService.create(user);
    }

    @GetMapping
    public Flux<User> getAll() {
        return this.userService.getUsers();
    }
}
