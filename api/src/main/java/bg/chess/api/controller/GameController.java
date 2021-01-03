package bg.chess.api.controller;

import bg.chess.api.model.Game;
import bg.chess.api.model.Move;
import bg.chess.api.model.Side;
import bg.chess.api.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping("/{gameId}")
    public Mono<Game> getByUUID(@PathVariable String gameId) {
        return this.gameService.getGame(gameId);
    }

    @GetMapping()
    public Flux<Game> getAll(@AuthenticationPrincipal User user) {
        return this.gameService.getGames(user.getUsername());
    }

    @PostMapping
    public Mono<Game> create(@AuthenticationPrincipal User user,
                             @RequestBody Game game,
                             @RequestParam(required = false) Side join) {
        return this.gameService.createGame(user.getUsername(), game, join);
    }

    @PostMapping("/{gameId}/join")
    public Mono<Game> join(@PathVariable String gameId) {
        return null;
    }

    @PutMapping("/{gameId}")
    public Mono<Game> move(@PathVariable String gameId,
                           @RequestParam(required = false) String move) {
        return this.gameService.move(gameId, StringUtils.hasText(move) ? Move.of(move) : null);
    }

    @PostMapping("/test")
    public Mono<String> test() {
        return this.gameService.test();
    }
}
