package bg.chess.api.service;

import bg.chess.api.model.*;
import bg.chess.api.repository.GameRepository;
import bg.chess.api.repository.UserRepository;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.game.GameContext;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final EngineService engineService;

    public GameService(GameRepository gameRepository, UserRepository userRepository, EngineService engineService) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.engineService = engineService;
    }

    public Mono<Game> createGame(String username, Game game, Side join) {
        return this.userRepository.findByUsername(username).flatMap(user -> {
            game.setDateStarted(System.currentTimeMillis());

            if (join != null) {
                Player player = new Player();
                player.setType(PlayerType.HUMAN);
                player.setId(user.getId());
                player.setDescription(user.getDescription());
                player.setElo(user.getElo());
                player.setName(user.getName());
                if (join.equals(Side.WHITE)) {
                    game.setWhitePlayer(player);
                } else {
                    game.setBlackPlayer(player);
                }
            }

            game.setHalfMoves(new LinkedList<>());
            game.setResult(GameResult.ONGOING);
            game.setBoardFen(Game.START_FEN);

            return this.gameRepository.save(game);
        });
    }

    public Mono<Game> getGame(String gameId) {
        return this.gameRepository
                .findById(gameId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND, "Unable to find resource")));
    }

    public Mono<Game> move(String gameId, Move move) {
        return this.gameRepository.findById(gameId)
                .flatMap(game -> {
                    if (move == null) {
                        if ((game.getWhitePlayer().getType().equals(PlayerType.ENGINE) && game.getTurn().equals(Side.WHITE))
                                || game.getBlackPlayer().getType().equals(PlayerType.ENGINE) && game.getTurn().equals(Side.BLACK)) {
                            return this.engineService.bestMove(game)
                                    .flatMap(generatedMove -> this.move(game, Move.of(generatedMove)));
                        } else {
                            return Mono.error(new ResponseStatusException(CONFLICT, "Not engine turn"));
                        }
                    } else {
                        if (!((game.getWhitePlayer().getType().equals(PlayerType.HUMAN) && game.getTurn().equals(Side.WHITE))
                                || (game.getBlackPlayer().getType().equals(PlayerType.HUMAN) && game.getTurn().equals(Side.BLACK)))) {
                            return Mono.error(new ResponseStatusException(CONFLICT, "Not human turn"));
                        }

                        return this.move(game, move);
                    }
                })
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND, "Unable to find resource")));
    }

    private Mono<Game> move(Game game, Move move) {
        Mono<Boolean> subscribeOnValidateMove = this.engineService.legalMoves(game.getBoardFen())
                .map(legalMoves -> legalMoves.contains(move.toString()));

        Mono<String> subscribeOnNewState = this.engineService.move(game.getBoardFen(), move);

        return Mono.zip(subscribeOnValidateMove, subscribeOnNewState)
                .flatMap(data -> {
                    Boolean isValid = data.getT1();
                    if (!isValid) {
                        return Mono.error(new ResponseStatusException(CONFLICT, "Not allowed move"));
                    }

                    String newState = data.getT2();

                    Mono<Boolean> subscribeOnHasValidMoves = this.engineService.legalMoves(newState)
                            .map(StringUtils::hasText);

                    Mono<Boolean> subscribeOnInCheck = this.engineService.checkers(newState)
                            .map(StringUtils::hasText);

                    return Mono.zip(subscribeOnHasValidMoves, subscribeOnInCheck)
                            .flatMap(checkData -> {
                                Boolean hasMoves = checkData.getT1();
                                if (!hasMoves) {
                                    Boolean checked = checkData.getT2();
                                    if (checked) {
                                        if (game.getTurn().equals(Side.WHITE)) {
                                            game.setResult(GameResult.WHITE_WON);
                                        } else {
                                            game.setResult(GameResult.BLACK_WON);
                                        }
                                    } else {
                                        game.setResult(GameResult.DRAW);
                                    }
                                }

                                game.setBoardFen(newState);
                                game.getHalfMoves().add(move);
                                return this.gameRepository.save(game);
                            });
                });
    }

    public Flux<Game> getGames(String username) {
        return this.userRepository.findByUsername(username)
                .map(user -> user)
                .flatMapMany(user -> this.gameRepository.findAllByPlayerId(user.getId()));
    }

    public Mono<String> test() {
        return this.engineService.test();
    }
}
