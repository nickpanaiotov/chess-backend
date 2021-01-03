package bg.chess.api.repository;

import bg.chess.api.model.Game;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface GameRepository extends ReactiveCrudRepository<Game, String> {

    @Query("{'$or':[ {'whitePlayerId':?0}, {'blackPlayerId':?0} ] }")
    Flux<Game> findAllByPlayerId(String playerId);
}
