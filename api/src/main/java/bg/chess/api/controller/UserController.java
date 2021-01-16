package bg.chess.api.controller;

import bg.chess.api.model.User;
import bg.chess.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @PostMapping
    public Mono<User> create(@RequestBody User user) {
        return this.userService.create(user);
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN') or #user.username == #username")
    public Mono<User> get(@AuthenticationPrincipal UserDetails user,
                          @PathVariable String username) {
        return this.userService.getUser(username);
    }

    @GetMapping
    public Flux<User> getAll() {
        return this.userService.getUsers();
    }
}
